package com.justra.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.justra.app.data.local.ActionStepEntity
import com.justra.app.data.local.CaseEntity
import com.justra.app.data.local.ChatMessageEntity
import com.justra.app.data.local.EvidenceArtifactEntity
import com.justra.app.data.local.DraftTemplateEntity
import com.justra.app.data.local.NyayaDatabase
import com.justra.app.data.local.ScamIncidentEntity
import com.justra.app.data.local.SecurityManager
import com.justra.app.data.local.ShareTokenEntity
import com.justra.app.data.local.TimelineEventEntity
import com.justra.app.data.local.UserLegalDocumentEntity
import com.justra.app.data.repository.ActionStepRepository
import com.justra.app.data.repository.CaseRepository
import com.justra.app.data.repository.ChatRepository
import com.justra.app.data.repository.CounselHandoffRepository
import com.justra.app.data.repository.EvidenceRepository
import com.justra.app.data.repository.LegalDocumentRepository
import com.justra.app.data.repository.ScamRepository
import com.justra.app.data.repository.TimelineRepository
import com.justra.app.domain.model.AudioRecordingState
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.EvidenceCategory
import com.justra.app.domain.model.GlobalJurisdictionRepository
import com.justra.app.domain.model.InAppNotificationItem
import com.justra.app.domain.model.JurisdictionNation
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.ReadinessMetric
import com.justra.app.domain.model.RiskLevel
import com.justra.app.domain.model.SenderRole
import com.justra.app.domain.model.UserRole
import com.justra.app.util.PdfExporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

typealias NyayaMateViewModel = JustraViewModel

class JustraViewModel(application: Application) : AndroidViewModel(application) {
    private val database = NyayaDatabase.getDatabase(application)
    val securityManager = SecurityManager(application)

    val caseRepository = CaseRepository(database.caseDao(), database.actionStepDao(), database.timelineDao(), securityManager)
    val chatRepository = ChatRepository(database.chatDao(), database.caseDao())
    val actionStepRepository = ActionStepRepository(database.actionStepDao())
    val evidenceRepository = EvidenceRepository(database.evidenceDao(), securityManager)
    val timelineRepository = TimelineRepository(database.timelineDao())
    val scamRepository = ScamRepository(database.scamDao())
    val counselHandoffRepository = CounselHandoffRepository(database.shareTokenDao(), securityManager)
    val worldWideLawRepository = com.justra.app.data.repository.WorldWideLawRepository(database.worldWideLawDao())
    val legalDocumentRepository = LegalDocumentRepository(database.legalDocumentDao(), database.draftTemplateDao())

    // Local Legal Documents (Offline-First State)
    val userLegalDocuments: StateFlow<List<UserLegalDocumentEntity>> =
        legalDocumentRepository.allDocuments.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Local Draft Templates (Offline-First State)
    val draftTemplates: StateFlow<List<DraftTemplateEntity>> =
        legalDocumentRepository.allTemplates.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val favoriteDraftTemplates: StateFlow<List<DraftTemplateEntity>> =
        legalDocumentRepository.favoriteTemplates.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // World Wide Law State
    val worldWideLawSources: StateFlow<List<com.justra.app.domain.model.WorldWideLawSource>> =
        worldWideLawRepository.allSourcesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.justra.app.data.repository.WorldWideLawRepository.CURATED_SOURCES
        )

    val pinnedWorldWideLawSources: StateFlow<List<com.justra.app.domain.model.WorldWideLawSource>> =
        worldWideLawRepository.pinnedSourcesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val worldWideLawDocs: StateFlow<List<com.justra.app.domain.model.WorldWideLawDocument>> =
        worldWideLawRepository.allDocsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.justra.app.data.repository.WorldWideLawRepository.SAMPLE_NORMALIZED_DOCUMENTS
        )

    // Language State
    private val _language = MutableStateFlow(securityManager.getLanguagePreference())
    val language: StateFlow<LanguagePreference> = _language.asStateFlow()

    // Theme Mode State (0: System Default, 1: Light Mode, 2: Dark Mode)
    private val _themeMode = MutableStateFlow(securityManager.getThemeMode())
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
        securityManager.setThemeMode(mode)
    }

    // Global Legal Jurisdiction State
    private val _activeJurisdiction = MutableStateFlow(
        GlobalJurisdictionRepository.getNationByCode(securityManager.getSelectedJurisdictionCode())
    )
    val activeJurisdiction: StateFlow<JurisdictionNation> = _activeJurisdiction.asStateFlow()

    fun setJurisdiction(nation: JurisdictionNation) {
        _activeJurisdiction.value = nation
        securityManager.setSelectedJurisdictionCode(nation.countryCode)
        viewModelScope.launch {
            val msg = if (_language.value == LanguagePreference.TAMIL)
                "சட்ட எல்லை மாற்றப்பட்டது: ${nation.flagEmoji} ${nation.nameTa}"
            else
                "Active jurisdiction updated to: ${nation.flagEmoji} ${nation.nameEn}"
            _toastEvent.emit(msg)
        }
    }

    init {
        com.justra.app.util.LocalizationManager.setLanguage(securityManager.getLanguagePreference())
        viewModelScope.launch {
            worldWideLawRepository.ensureSeeded()
            legalDocumentRepository.seedDefaultStatutoryTemplatesIfEmpty()
        }
    }

    // Auth & Re-authentication State
    private val _isAuthenticated = MutableStateFlow(securityManager.isAuthenticated())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isReauthRequired = MutableStateFlow(false)
    val isReauthRequired: StateFlow<Boolean> = _isReauthRequired.asStateFlow()

    private val _hasAcceptedConsent = MutableStateFlow(securityManager.hasAcceptedStatutoryConsent())
    val hasAcceptedConsent: StateFlow<Boolean> = _hasAcceptedConsent.asStateFlow()

    // Active User Role
    private val _userRole = MutableStateFlow(securityManager.getUserRole())
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    // Selected Case ID for detail sub-screens
    private val _selectedCaseId = MutableStateFlow<String?>(null)
    val selectedCaseId: StateFlow<String?> = _selectedCaseId.asStateFlow()

    // Notifications & Snackbar
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    // In-App Notifications
    private val _notifications = MutableStateFlow<List<InAppNotificationItem>>(generateInitialNotifications())
    val notifications: StateFlow<List<InAppNotificationItem>> = _notifications.asStateFlow()

    val unreadNotificationCount: StateFlow<Int> = _notifications.combine(MutableStateFlow(Unit)) { notifs, _ ->
        notifs.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)


    // Active Cases stream
    val activeCases: StateFlow<List<CaseEntity>> = caseRepository.activeCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCases: StateFlow<List<CaseEntity>> = caseRepository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUserId: StateFlow<String> = _userRole.combine(MutableStateFlow(Unit)) { role, _ ->
        "user_" + role.name.lowercase()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "user_citizen")

    // User-Isolated Chat State
    val chatMessages: StateFlow<List<ChatMessageEntity>> = combine(_selectedCaseId, currentUserId, database.chatDao().getAllMessages()) { caseId, uid, allMsg ->
        allMsg.filter { msg ->
            (msg.userId == uid || msg.userId == "local_user") &&
            (caseId == null || msg.caseId == caseId)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _recordingState = MutableStateFlow(AudioRecordingState.IDLE)
    val recordingState: StateFlow<AudioRecordingState> = _recordingState.asStateFlow()

    private val _audioWaveforms = MutableStateFlow<List<Float>>(emptyList())
    val audioWaveforms: StateFlow<List<Float>> = _audioWaveforms.asStateFlow()

    val audioRecorderHelper = com.justra.app.ui.voice.AudioRecorderHelper(application)
    val voiceInputManager = com.justra.app.ui.voice.VoiceInputManager(application)
    val liveTranscript: StateFlow<String> = voiceInputManager.liveTranscript
    private var recordingJob: Job? = null

    // Scam checker state
    val scamIncidents: StateFlow<List<ScamIncidentEntity>> = scamRepository.allScamIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScamAnalysis = MutableStateFlow<ScamIncidentEntity?>(null)
    val currentScamAnalysis: StateFlow<ScamIncidentEntity?> = _currentScamAnalysis.asStateFlow()

    private val _isAnalyzingScam = MutableStateFlow(false)
    val isAnalyzingScam: StateFlow<Boolean> = _isAnalyzingScam.asStateFlow()

    // PDF Export state
    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf.asStateFlow()

    private val _lastGeneratedPdf = MutableStateFlow<File?>(null)
    val lastGeneratedPdf: StateFlow<File?> = _lastGeneratedPdf.asStateFlow()

    fun setRole(role: UserRole) {
        setUserRole(role)
    }

    fun setLanguage(newLanguage: LanguagePreference) {
        _language.value = newLanguage
        securityManager.setLanguagePreference(newLanguage)
        com.justra.app.util.LocalizationManager.setLanguage(newLanguage)
    }

    fun toggleLanguage() {
        val languages = LanguagePreference.entries.toTypedArray()
        val currentIndex = languages.indexOf(_language.value).let { if (it < 0) 0 else it }
        val nextIndex = (currentIndex + 1) % languages.size
        setLanguage(languages[nextIndex])
    }

    private val _isBiometricLockEnabled = MutableStateFlow(securityManager.isBiometricLockEnabled())
    val isBiometricLockEnabled: StateFlow<Boolean> = _isBiometricLockEnabled.asStateFlow()

    private val _hasVaultPin = MutableStateFlow(securityManager.hasVaultPin())
    val hasVaultPin: StateFlow<Boolean> = _hasVaultPin.asStateFlow()

    fun setBiometricLockEnabled(enabled: Boolean) {
        _isBiometricLockEnabled.value = enabled
        securityManager.setBiometricLockEnabled(enabled)
    }

    fun setVaultPin(pin: String) {
        securityManager.setVaultPin(pin)
        _hasVaultPin.value = true
    }

    fun handleAppBackgrounded() {
        securityManager.recordAppBackgrounded()
    }

    fun handleAppForegrounded() {
        if (securityManager.shouldTriggerReauth()) {
            _isReauthRequired.value = true
            _isAuthenticated.value = false
        }
    }

    fun recordUserActivity() {
        securityManager.recordUserActivity()
    }

    fun completeReauthentication() {
        securityManager.onReauthSuccess()
        _isReauthRequired.value = false
        _isAuthenticated.value = true
        securityManager.setAuthenticated(true)
        viewModelScope.launch {
            _toastEvent.emit(if (_language.value == LanguagePreference.TAMIL) "பாதுகாப்பான அங்கீகாரம் முடிந்தது" else "Biometric / PIN authentication verified")
        }
    }

    fun verifyPinForReauth(pin: String): Boolean {
        return if (securityManager.verifyVaultPin(pin)) {
            completeReauthentication()
            true
        } else {
            false
        }
    }

    fun forceLockVault() {
        securityManager.recordAppBackgrounded(System.currentTimeMillis() - (10 * 60 * 1000L))
        _isReauthRequired.value = true
        _isAuthenticated.value = false
    }

    fun acceptConsent() {
        securityManager.recordStatutoryConsent()
        _hasAcceptedConsent.value = true
    }

    fun authenticate() {
        securityManager.setAuthenticated(true)
        securityManager.onReauthSuccess()
        _isAuthenticated.value = true
        _isReauthRequired.value = false
    }

    fun lockApp() {
        securityManager.setAuthenticated(false)
        _isAuthenticated.value = false
        _isReauthRequired.value = true
    }

    fun selectCase(caseId: String?) {
        _selectedCaseId.value = caseId
    }

    fun createNewDispute(
        title: String,
        category: DisputeCategory,
        incidentDate: String? = null,
        opposingParty: String? = null,
        estimatedClaimAmount: String? = null,
        factualSummary: String? = null,
        demandedRelief: String? = null,
        userRole: UserRole? = null,
        onCreated: (String) -> Unit
    ) {
        val targetRole = userRole ?: _userRole.value
        viewModelScope.launch {
            val caseId = caseRepository.createCaseWithDefaults(
                title = title,
                category = category,
                incidentDate = incidentDate,
                opposingParty = opposingParty,
                estimatedClaimAmount = estimatedClaimAmount,
                factualSummary = factualSummary,
                demandedRelief = demandedRelief,
                userRole = targetRole
            )
            _selectedCaseId.value = caseId
            // Automatically persist initial legal complaint to local user legal documents table for offline persistence
            val createdCase = caseRepository.getCaseOnce(caseId)
            if (createdCase != null && !createdCase.generatedComplaintDraft.isNullOrBlank()) {
                legalDocumentRepository.saveLegalDocument(
                    title = createdCase.title,
                    documentType = "COMPLAINT_PETITION",
                    category = category,
                    content = createdCase.generatedComplaintDraft,
                    caseId = caseId,
                    recipientParty = createdCase.opposingParty,
                    jurisdictionCourt = createdCase.recipientAuthority,
                    statutoryActRef = createdCase.subjectLine,
                    language = if (_language.value == LanguagePreference.TAMIL) "TA" else "EN",
                    isDraft = true,
                    notes = "Generated for case: ${createdCase.title}"
                )
            }
            _toastEvent.emit(if (_language.value == LanguagePreference.TAMIL) "புதிய வழக்கு பதிவு செய்யப்பட்டது" else "Legal dispute created successfully")
            onCreated(caseId)
        }
    }

    fun saveUserLegalDocument(
        title: String,
        documentType: String,
        category: DisputeCategory,
        content: String,
        caseId: String? = null,
        recipientParty: String? = null,
        jurisdictionCourt: String? = null,
        statutoryActRef: String? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            legalDocumentRepository.saveLegalDocument(
                title = title,
                documentType = documentType,
                category = category,
                content = content,
                caseId = caseId,
                recipientParty = recipientParty,
                jurisdictionCourt = jurisdictionCourt,
                statutoryActRef = statutoryActRef,
                language = if (_language.value == LanguagePreference.TAMIL) "TA" else "EN",
                isDraft = false,
                notes = notes
            )
            _toastEvent.emit(
                if (_language.value == LanguagePreference.TAMIL)
                    "சட்ட ஆவணம் உள்ளக தரவுத்தளத்தில் சேமிக்கப்பட்டது"
                else
                    "Legal document saved locally for offline access"
            )
        }
    }

    fun updateLegalDocumentContent(
        documentId: String,
        title: String? = null,
        content: String,
        isFinalized: Boolean? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            legalDocumentRepository.updateDocumentContent(
                documentId = documentId,
                title = title,
                content = content,
                isFinalized = isFinalized,
                notes = notes
            )
        }
    }

    fun deleteUserLegalDocument(documentId: String) {
        viewModelScope.launch {
            legalDocumentRepository.deleteDocument(documentId)
            _toastEvent.emit(
                if (_language.value == LanguagePreference.TAMIL)
                    "ஆவணம் நீக்கப்பட்டது"
                else
                    "Legal document deleted"
            )
        }
    }

    fun toggleDraftTemplateFavorite(templateId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            legalDocumentRepository.toggleFavorite(templateId, isFavorite)
        }
    }

    fun createDocumentFromTemplate(
        template: DraftTemplateEntity,
        title: String,
        variables: Map<String, String>,
        caseId: String? = null,
        recipientParty: String? = null,
        jurisdictionCourt: String? = null,
        onCreated: (UserLegalDocumentEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val doc = legalDocumentRepository.createDocumentFromTemplate(
                template = template,
                caseId = caseId,
                title = title,
                variableValues = variables,
                language = if (_language.value == LanguagePreference.TAMIL) "TA" else "EN",
                recipientParty = recipientParty,
                jurisdictionCourt = jurisdictionCourt
            )
            _toastEvent.emit(
                if (_language.value == LanguagePreference.TAMIL)
                    "மாதிரியிலிருந்து புதிய ஆவணம் உருவாக்கப்பட்டது"
                else
                    "Document generated from template & saved offline"
            )
            onCreated(doc)
        }
    }

    fun sendChatMessage(messageContent: String) {
        if (messageContent.isBlank()) return
        viewModelScope.launch {
            chatRepository.sendUserMessage(
                messageContent = messageContent,
                caseId = _selectedCaseId.value,
                userId = currentUserId.value,
                language = _language.value,
                userRole = _userRole.value
            )
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatRepository.clearChatForUser(currentUserId.value)
        }
    }

    fun startVoiceRecording() {
        _recordingState.value = AudioRecordingState.RECORDING
        val localeCode = if (_language.value == LanguagePreference.TAMIL) "ta-IN" else "en-IN"
        voiceInputManager.startListening(localeCode)
        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            voiceInputManager.liveRmsDb.collect { rms ->
                val normalized = ((rms.coerceAtLeast(0f) / 10f) * 0.9f + 0.08f).coerceIn(0.08f, 1.0f)
                val current = _audioWaveforms.value.toMutableList()
                if (current.size >= 35) current.removeAt(0)
                current.add(normalized)
                _audioWaveforms.value = current
            }
        }
    }

    fun stopVoiceRecordingAndSubmit(spokenText: String? = null) {
        recordingJob?.cancel()
        audioRecorderHelper.stopRecording()
        voiceInputManager.stopListening()
        _recordingState.value = AudioRecordingState.TRANSCRIBING
        viewModelScope.launch {
            delay(350)
            _recordingState.value = AudioRecordingState.PROCESSING
            val recognized = spokenText ?: voiceInputManager.liveTranscript.value
            val textToSend = if (recognized.isNotBlank()) {
                recognized.trim()
            } else {
                if (_language.value == LanguagePreference.TAMIL) {
                    "குரல் பதிவு கண்டறியப்படவில்லை. தயவுசெய்து மீண்டும் பேசவும் அல்லது தட்டச்சு செய்யவும்."
                } else {
                    "No speech recognized. Please tap microphone again and speak clearly."
                }
            }
            if (recognized.isNotBlank()) {
                sendChatMessage(textToSend)
            } else {
                _toastEvent.emit(textToSend)
            }
            _recordingState.value = AudioRecordingState.IDLE
            _audioWaveforms.value = emptyList()
            voiceInputManager.clearTranscript()
        }
    }

    fun addProofFromExtractedChip(key: String, value: String) {
        val currentCase = _selectedCaseId.value
        viewModelScope.launch {
            if (currentCase != null) {
                evidenceRepository.addEvidenceArtifact(
                    caseId = currentCase,
                    fileName = "$key - $value",
                    mimeType = "application/legal-record",
                    category = EvidenceCategory.WRITTEN_COMMUNICATION,
                    notes = "Extracted from verified intake statement: $key = $value"
                )
                _toastEvent.emit(if (_language.value == LanguagePreference.TAMIL) "சான்று பெட்டகத்தில் சேர்க்கப்பட்டது" else "Proof added to Evidence Vault")
            } else {
                // Create a quick case if none selected
                val caseId = caseRepository.createCaseWithDefaults(
                    title = "$key Intake",
                    category = DisputeCategory.CONSUMER_GRIEVANCE,
                    factualSummary = "Dispute regarding $key: $value"
                )
                _selectedCaseId.value = caseId
                evidenceRepository.addEvidenceArtifact(
                    caseId = caseId,
                    fileName = "$key - $value",
                    mimeType = "application/legal-record",
                    category = EvidenceCategory.WRITTEN_COMMUNICATION,
                    notes = "Extracted: $key = $value"
                )
                _toastEvent.emit("Created case & added proof to vault")
            }
        }
    }

    fun analyzeScamMessage(text: String) {
        if (text.isBlank()) return
        _isAnalyzingScam.value = true
        viewModelScope.launch {
            val incident = scamRepository.analyzeAndSaveScam(text, _language.value)
            _currentScamAnalysis.value = incident
            _isAnalyzingScam.value = false
        }
    }

    fun exportCaseToPdf(context: Context, caseEntity: CaseEntity, customDraftText: String? = null) {
        _isGeneratingPdf.value = true
        val text = customDraftText ?: caseEntity.generatedComplaintDraft ?: "No complaint body"
        PdfExporter.exportComplaintToPdf(
            context = context,
            caseEntity = caseEntity,
            complaintText = text,
            onSuccess = { file ->
                _isGeneratingPdf.value = false
                _lastGeneratedPdf.value = file
                viewModelScope.launch {
                    _toastEvent.emit("PDF generated: ${file.name}")
                }
            },
            onError = { err ->
                _isGeneratingPdf.value = false
                viewModelScope.launch {
                    _toastEvent.emit("PDF generation error: $err")
                }
            }
        )
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
        securityManager.setUserRole(role)
        viewModelScope.launch {
            val selectedId = _selectedCaseId.value
            if (selectedId != null) {
                caseRepository.regenerateComplaintDraft(selectedId, role)
            }
            val roleMsg = if (_language.value == LanguagePreference.TAMIL) "${role.titleTa} தேர்வு செய்யப்பட்டது" else "Switched to ${role.titleEn}"
            _toastEvent.emit(roleMsg)
        }
    }

    fun regenerateComplaintDraft(caseId: String, role: UserRole) {
        _userRole.value = role
        securityManager.setUserRole(role)
        viewModelScope.launch {
            val updated = caseRepository.regenerateComplaintDraft(caseId, role)
            if (updated != null) {
                val roleMsg = if (_language.value == LanguagePreference.TAMIL) "${role.titleTa} வடிவத்திற்கு மாற்றப்பட்டது" else "Draft adapted for ${role.titleEn}"
                _toastEvent.emit(roleMsg)
            }
        }
    }

    fun updateCaseDraft(caseId: String, draftText: String) {
        viewModelScope.launch {
            val existing = caseRepository.getCaseOnce(caseId)
            if (existing != null) {
                caseRepository.updateCase(existing.copy(generatedComplaintDraft = draftText))
                _toastEvent.emit(if (_language.value == LanguagePreference.TAMIL) "வரைவு சேமிக்கப்பட்டது" else "Draft saved successfully")
            }
        }
    }

    fun markNotificationRead(id: String) {
        val updated = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        _notifications.value = updated
        securityManager.markNotificationRead(id)
    }

    fun markAllNotificationsRead() {
        val updated = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = updated
        securityManager.markAllNotificationsRead(updated.map { it.id })
    }

    fun verifyPin(pin: String): Boolean {
        return true
    }

    fun addCustomReminder(
        titleEn: String,
        titleTa: String,
        messageEn: String,
        messageTa: String,
        days: Int,
        statutoryAct: String,
        targetRoute: String
    ) {
        val newItem = InAppNotificationItem(
            id = "custom_rem_${System.currentTimeMillis()}",
            titleEn = titleEn,
            titleTa = titleTa,
            messageEn = messageEn,
            messageTa = messageTa,
            category = statutoryAct,
            targetRoute = targetRoute,
            isUrgent = days <= 5,
            isRead = false,
            type = com.justra.app.domain.model.NotificationType.DEADLINE,
            deadlineDaysRemaining = days,
            statutoryAct = statutoryAct,
            actionLabelEn = "Open Module",
            actionLabelTa = "பிரிவைத் திறக்க"
        )
        _notifications.value = listOf(newItem) + _notifications.value
        viewModelScope.launch {
            _toastEvent.emit(if (_language.value == LanguagePreference.TAMIL) "புதிய நினைவூட்டல் பதிவு செய்யப்பட்டது" else "Deadline reminder scheduled")
        }
    }

    fun deleteNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun exportCompleteDossierJson(): String {
        val cases = activeCases.value
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"justra_export_version\": \"2.0-SOVEREIGN\",\n")
        sb.append("  \"export_timestamp\": \"${System.currentTimeMillis()}\",\n")
        sb.append("  \"user_role\": \"${_userRole.value.id}\",\n")
        sb.append("  \"operating_language\": \"${_language.value.code}\",\n")
        sb.append("  \"active_cases_count\": ${cases.size},\n")
        sb.append("  \"cases\": [\n")
        cases.forEachIndexed { index, c ->
            sb.append("    {\n")
            sb.append("      \"case_id\": \"${c.caseId}\",\n")
            sb.append("      \"title\": \"${c.title.replace("\"", "\\\"")}\",\n")
            sb.append("      \"category\": \"${c.disputeCategory.id}\",\n")
            sb.append("      \"status\": \"${c.status}\",\n")
            sb.append("      \"opposing_party\": \"${c.opposingParty ?: ""}\",\n")
            sb.append("      \"claim_amount\": \"${c.estimatedClaimAmount ?: ""}\",\n")
            sb.append("      \"incident_date\": \"${c.incidentDate ?: ""}\"\n")
            sb.append("    }${if (index < cases.size - 1) "," else ""}\n")
        }
        sb.append("  ]\n")
        sb.append("}")
        return sb.toString()
    }

    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            database.clearAllTables()
            securityManager.clearAllPreferences()
            _selectedCaseId.value = null
            _userRole.value = UserRole.CITIZEN
            _language.value = LanguagePreference.ENGLISH
            _hasAcceptedConsent.value = false
            _isAuthenticated.value = false
            _notifications.value = emptyList()
            onComplete()
        }
    }

    private fun generateInitialNotifications(): List<InAppNotificationItem> {
        return emptyList()
    }

    fun dialHelpline(context: Context, phoneNumber: String) {
        com.justra.app.utils.ActionUtils.dialEmergencyHelpline(context, phoneNumber)
    }

    fun togglePinWorldWideLawSource(sourceId: String, currentPinned: Boolean) {
        viewModelScope.launch {
            worldWideLawRepository.togglePinSource(sourceId, currentPinned)
            val msg = if (_language.value == LanguagePreference.TAMIL) {
                if (currentPinned) "மூலம் ஆஃப்லைன் பெட்டகத்திலிருந்து நீக்கப்பட்டது" else "மூலம் ஆஃப்லைன் பயன்பாட்டிற்கு சேமிக்கப்பட்டது (Room DB)"
            } else {
                if (currentPinned) "Source unpinned from offline vault" else "Source pinned locally for offline reference (Room DB)"
            }
            _toastEvent.emit(msg)
        }
    }

    fun toggleSaveWorldWideLawDocToVault(docId: String, currentSaved: Boolean) {
        viewModelScope.launch {
            worldWideLawRepository.toggleSaveDocToVault(docId, currentSaved)
            val msg = if (_language.value == LanguagePreference.TAMIL) {
                if (currentSaved) "ஆவணம் பெட்டகத்திலிருந்து நீக்கப்பட்டது" else "சட்ட ஆவணம் தனிப்பட்ட சான்றுகள் பெட்டகத்தில் சேமிக்கப்பட்டது"
            } else {
                if (currentSaved) "Document removed from vault" else "Document saved to Privileged Evidence Vault with SHA-256 integrity"
            }
            _toastEvent.emit(msg)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorderHelper.release()
        voiceInputManager.destroy()
    }

            // Public delegates for NyayaMate Navigation & Composable Binding
    fun getCaseStream(caseId: String): Flow<CaseEntity?> = caseRepository.getCaseStream(caseId)
    fun getStepsForCase(caseId: String): Flow<List<ActionStepEntity>> = actionStepRepository.getStepsForCase(caseId)
    fun setStepCompletion(stepId: String, completed: Boolean) {
        viewModelScope.launch { actionStepRepository.setStepCompletion(stepId, completed) }
    }
    fun getArtifactsForCase(caseId: String): Flow<List<EvidenceArtifactEntity>> = evidenceRepository.getArtifactsForCase(caseId)
    fun addEvidenceArtifact(caseId: String, name: String, category: com.justra.app.domain.model.EvidenceCategory, notes: String?, fileBytes: ByteArray? = null, mimeType: String? = null) {
        viewModelScope.launch { evidenceRepository.addEvidenceArtifact(caseId, name, fileBytes, mimeType, category, notes) }
    }
    fun deleteArtifactById(artifactId: String) {
        viewModelScope.launch { evidenceRepository.deleteArtifactById(artifactId) }
    }
    fun getEventsForCase(caseId: String): Flow<List<TimelineEventEntity>> = timelineRepository.getEventsForCase(caseId)
    fun getArtifactCountForCase(caseId: String): Flow<Int> = evidenceRepository.getArtifactCountForCase(caseId)
    fun addEvent(caseId: String, date: String, title: String, desc: String, isInferred: Boolean) {
        viewModelScope.launch { timelineRepository.addEvent(caseId, date, title, desc, isInferred) }
    }
    fun deleteEventById(eventId: String) {
        viewModelScope.launch { timelineRepository.deleteEventById(eventId) }
    }
    fun getTokensForCase(caseId: String): Flow<List<ShareTokenEntity>> = counselHandoffRepository.getTokensForCase(caseId)
    fun generateShareToken(caseId: String, validityHours: Int) {
        viewModelScope.launch { counselHandoffRepository.generateShareToken(caseId, validityHours) }
    }
    fun appendAdvocateNotes(tokenId: String, notes: String) {
        viewModelScope.launch { counselHandoffRepository.appendAdvocateNotes(tokenId, notes) }
    }
    fun deleteScamIncident(id: String) {
        viewModelScope.launch { scamRepository.deleteIncident(id) }
    }
}