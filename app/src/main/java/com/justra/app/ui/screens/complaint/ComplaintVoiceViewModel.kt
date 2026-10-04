package com.justra.app.ui.screens.complaint

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.justra.app.BuildConfig
import com.justra.app.data.api.GeminiApiClient
import com.justra.app.data.api.GeminiContent
import com.justra.app.data.api.GeminiGenerationConfig
import com.justra.app.data.api.GeminiPart
import com.justra.app.data.api.GeminiRequest
import com.justra.app.data.local.NyayaDatabase
import com.justra.app.data.local.SecurityManager
import com.justra.app.data.repository.CaseRepository
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.LegalComplaintResult
import com.justra.app.domain.model.UserRole
import com.justra.app.data.api.GeminiLegalEngine
import com.justra.app.ui.voice.AudioRecorderHelper
import com.justra.app.ui.voice.VoiceInputManager
import com.justra.app.ui.voice.VoiceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DynamicComplaint(
    val title: String,
    val subject: String,
    val recipientAuthority: String,
    val opposingParty: String,
    val statementOfFacts: String,
    val demandedRelief: String,
    val statutoryGrounds: List<String>,
    val date: String,
    val fullDraftText: String,
    val suggestedCategory: DisputeCategory = DisputeCategory.CONSUMER_GRIEVANCE
)

sealed class ComplaintVoiceUiState {
    object Idle : ComplaintVoiceUiState()
    object Recording : ComplaintVoiceUiState()
    data class Reviewing(val transcript: String) : ComplaintVoiceUiState()
    object Generating : ComplaintVoiceUiState()
    data class Success(val complaint: DynamicComplaint) : ComplaintVoiceUiState()
    data class Error(val error: String) : ComplaintVoiceUiState()
}

class ComplaintVoiceViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ComplaintVoiceVM"

        fun suggestCategoryFromTranscript(rawTranscript: String): DisputeCategory {
            val lower = rawTranscript.lowercase()
            return when {
                lower.contains("rent") || lower.contains("landlord") || lower.contains("tenant") || lower.contains("deposit") || lower.contains("flat") || lower.contains("lease") -> DisputeCategory.TENANCY_RENT
                lower.contains("upi") || lower.contains("otp") || lower.contains("phishing") || lower.contains("cyber") || lower.contains("hacked") -> DisputeCategory.CYBER_FINANCIAL_FRAUD
                lower.contains("salary") || lower.contains("employer") || lower.contains("gratuity") || lower.contains("pf") || lower.contains("wages") -> DisputeCategory.EMPLOYMENT_SALARY
                lower.contains("land") || lower.contains("patta") || lower.contains("property") || lower.contains("encroach") || lower.contains("deed") -> DisputeCategory.LAND_PROPERTY
                lower.contains("women") || lower.contains("harassment") || lower.contains("domestic") || lower.contains("posh") || lower.contains("dowry") -> DisputeCategory.WOMEN_RIGHTS
                lower.contains("rti") || lower.contains("certificate") || lower.contains("officer") || lower.contains("ration") -> DisputeCategory.GOVT_RTI
                lower.contains("scam") || lower.contains("lottery") || lower.contains("suspicious") || lower.contains("fake") -> DisputeCategory.SCAM_ANALYSIS
                else -> DisputeCategory.CONSUMER_GRIEVANCE
            }
        }
    }

    private val database = NyayaDatabase.getDatabase(application)
    private val securityManager = SecurityManager(application)
    val caseRepository = CaseRepository(
        database.caseDao(),
        database.actionStepDao(),
        database.timelineDao(),
        securityManager
    )

    val voiceManager = VoiceInputManager(application)
    val audioRecorderHelper = AudioRecorderHelper(application)

    private val _uiState = MutableStateFlow<ComplaintVoiceUiState>(ComplaintVoiceUiState.Idle)
    val uiState: StateFlow<ComplaintVoiceUiState> = _uiState.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(LanguagePreference.TAMIL)
    val selectedLanguage: StateFlow<LanguagePreference> = _selectedLanguage.asStateFlow()

    private val _currentRole = MutableStateFlow(securityManager.getUserRole())
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _editableTranscript = MutableStateFlow("")
    val editableTranscript: StateFlow<String> = _editableTranscript.asStateFlow()

    val liveWaveforms: StateFlow<List<Float>> = audioRecorderHelper.amplitudeFlow

    init {
        viewModelScope.launch {
            voiceManager.voiceState.collect { state ->
                when (state) {
                    is VoiceState.Listening -> {
                        _uiState.value = ComplaintVoiceUiState.Recording
                    }
                    is VoiceState.PartialResult -> {
                        _editableTranscript.value = state.text
                    }
                    is VoiceState.FinalResult -> {
                        _editableTranscript.value = state.text
                        if (_uiState.value is ComplaintVoiceUiState.Recording) {
                            _uiState.value = ComplaintVoiceUiState.Reviewing(state.text)
                        }
                    }
                    is VoiceState.Error -> {
                        Log.w(TAG, "Voice Error: ${state.message}")
                        if (_editableTranscript.value.isNotBlank()) {
                            _uiState.value = ComplaintVoiceUiState.Reviewing(_editableTranscript.value)
                        } else {
                            _uiState.value = ComplaintVoiceUiState.Error(state.message)
                        }
                    }
                    is VoiceState.Idle -> {
                        if (_editableTranscript.value.isNotBlank() && _uiState.value == ComplaintVoiceUiState.Recording) {
                            _uiState.value = ComplaintVoiceUiState.Reviewing(_editableTranscript.value)
                        }
                    }
                }
            }
        }
    }

    fun setLanguage(language: LanguagePreference) {
        _selectedLanguage.value = language
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
        securityManager.setUserRole(role)
        if (_editableTranscript.value.isNotBlank() && (_uiState.value is ComplaintVoiceUiState.Success || _uiState.value is ComplaintVoiceUiState.Reviewing)) {
            generateComplaintFromTranscript()
        }
    }

    fun toggleLanguage() {
        _selectedLanguage.value = if (_selectedLanguage.value == LanguagePreference.TAMIL) {
            LanguagePreference.ENGLISH
        } else {
            LanguagePreference.TAMIL
        }
    }

    fun updateTranscript(text: String) {
        _editableTranscript.value = text
        voiceManager.updateManualTranscript(text)
        if (_uiState.value !is ComplaintVoiceUiState.Generating && _uiState.value !is ComplaintVoiceUiState.Success) {
            _uiState.value = ComplaintVoiceUiState.Reviewing(text)
        }
    }

    fun startVoiceIntake() {
        val localeCode = if (_selectedLanguage.value == LanguagePreference.TAMIL) "ta-IN" else "en-IN"
        _uiState.value = ComplaintVoiceUiState.Recording
        audioRecorderHelper.startRecording()
        voiceManager.startListening(localeCode)
    }

    fun stopVoiceIntake() {
        voiceManager.stopListening()
        audioRecorderHelper.stopRecording()
        val text = _editableTranscript.value
        _uiState.value = if (text.isNotBlank()) {
            ComplaintVoiceUiState.Reviewing(text)
        } else {
            ComplaintVoiceUiState.Idle
        }
    }

    fun resetIntake() {
        _editableTranscript.value = ""
        _uiState.value = ComplaintVoiceUiState.Idle
    }

    fun generateComplaintFromTranscript() {
        val transcript = _editableTranscript.value.trim()
        if (transcript.isBlank()) {
            _uiState.value = ComplaintVoiceUiState.Error("Transcript is empty. Please speak or type your grievance.")
            return
        }

        _uiState.value = ComplaintVoiceUiState.Generating

        viewModelScope.launch {
            try {
                val role = _currentRole.value
                val complaint = transformGrievanceWithGemini(transcript, _selectedLanguage.value, role)
                _uiState.value = ComplaintVoiceUiState.Success(complaint)
            } catch (e: Exception) {
                Log.e(TAG, "Generation failed: ${e.message}", e)
                _uiState.value = ComplaintVoiceUiState.Error("Failed to draft complaint: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun transformGrievanceWithGemini(
        rawTranscript: String,
        language: LanguagePreference,
        userRole: UserRole
    ): DynamicComplaint = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isTamil = language == LanguagePreference.TAMIL
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

        val roleSpecificInstruction = when (userRole) {
            UserRole.LEGAL_COUNSEL -> """
                Role: Legal Counsel / Advocate. Draft a formal Advocate Statutory Demand Notice & Legal Brief on behalf of your client.
                Format: Include an Advocate representation clause ("Under instructions from and on behalf of my client..."), reference to Advocate Act, specific 15-day cure notice, legal drafting fees demand, and notice of immediate civil/criminal court filing if unheeded.
            """.trimIndent()
            UserRole.MSME_BUSINESS -> """
                Role: MSME Business / Enterprise Owner. Draft a formal Commercial Recovery Notice under the Micro, Small and Medium Enterprises Development (MSMED) Act, 2006 (Sections 15 & 16) and Commercial Courts Act, 2015.
                Format: Reference commercial invoice payment defaults beyond 45 days, demand mandatory compound interest at 3x RBI Bank Rate, and mention escalation to the MSME Samadhaan Facilitation Council.
            """.trimIndent()
            UserRole.CYBER_FRAUD_VICTIM -> """
                Role: Cyber Crime & Financial Fraud Victim. Draft an urgent cyber incident complaint to the National Cyber Crime Reporting Portal (1930) and Bank Nodal Grievance Cell.
                Format: Invoke Information Technology Act, 2000 (Section 66D) and RBI Master Direction on Customer Protection (Zero Liability for Unauthorized Electronic Banking Transactions). Demand immediate account freezing of destination UPI/bank accounts.
            """.trimIndent()
            UserRole.CITIZEN -> """
                Role: Citizen / Consumer. Draft a formal consumer grievance petition under the Consumer Protection Act, 2019 / relevant statutes.
                Format: Citizen pro se petition demanding full refund, compensation for mental harassment, and procedural rectification.
            """.trimIndent()
        }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val promptText = """
                    Transform this raw grievance statement into a formal Indian legal complaint tailored for the persona: ${userRole.titleEn}.
                    
                    STRICT INSTRUCTION:
                    DO NOT default to a consumer dispute or a fixed template.
                    You must choose the applicable law strictly based on the user's grievance:
                    1. Landlord refusing deposit / Eviction -> Model Tenancy Act / State Rent Control Acts.
                    2. Salary delay / Unpaid wages / Workplace -> Payment of Wages Act / Industrial Disputes Act.
                    3. UPI / Online banking / Phishing / Fake call -> Information Technology Act, 2000 & Section 318 BNS.
                    4. Defective product / Non-delivery / Service refusal -> Consumer Protection Act, 2019.
                    5. Physical threat / Extortion / Assault -> Bharatiya Nyaya Sanhita (BNS).

                    Persona Guidelines:
                    $roleSpecificInstruction
                    
                    Statement: "$rawTranscript"
                    
                    Return a valid JSON object ONLY with the following keys (no markdown code blocks, pure JSON):
                    {
                      "title": "Short title of the dispute",
                      "subject": "Formal legal subject line tailored to ${userRole.titleEn}",
                      "recipientAuthority": "Appropriate forum, authority, police station, or respondent",
                      "opposingParty": "Identified respondent/merchant/landlord/accused",
                      "statementOfFacts": "Chronological, legal statement of facts drafted in formal prose",
                      "demandedRelief": "Exact legal remedies requested tailored to this persona",
                      "statutoryGrounds": ["Section X of Act Y", "Section Z of relevant statute"],
                      "suggestedCategory": "CONSUMER_GRIEVANCE or CYBER_FINANCIAL_FRAUD or TENANCY_RENT or EMPLOYMENT_SALARY or WOMEN_RIGHTS or LAND_PROPERTY or GOVT_RTI",
                      "fullDraftText": "Complete multi-paragraph formal legal notice / complaint ready to be submitted to court or authority"
                    }
                    Language: ${if (isTamil) "Tamil (தமிழ்)" else "English"}.
                    Do NOT use default static templates. Tailor specifically to the facts in the citizen statement.
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = promptText)))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.2f,
                        maxOutputTokens = 2048
                    )
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!responseText.isNullOrBlank()) {
                    val cleanJson = responseText
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()

                    val json = JSONObject(cleanJson)
                    val grounds = mutableListOf<String>()
                    val groundsArr = json.optJSONArray("statutoryGrounds")
                    if (groundsArr != null) {
                        for (i in 0 until groundsArr.length()) {
                            grounds.add(groundsArr.getString(i))
                        }
                    }

                    val catStr = json.optString("suggestedCategory", "CONSUMER_GRIEVANCE")
                    val cat = try {
                        DisputeCategory.valueOf(catStr)
                    } catch (_: Exception) {
                        DisputeCategory.CONSUMER_GRIEVANCE
                    }

                    return@withContext DynamicComplaint(
                        title = json.optString("title", "Legal Grievance Notice"),
                        subject = json.optString("subject", "Formal Notice under Applicable Law"),
                        recipientAuthority = json.optString("recipientAuthority", "Appropriate Adjudicating Authority"),
                        opposingParty = json.optString("opposingParty", "Opposing Party / Respondent"),
                        statementOfFacts = json.optString("statementOfFacts", rawTranscript),
                        demandedRelief = json.optString("demandedRelief", "Immediate redressal and compensation"),
                        statutoryGrounds = if (grounds.isEmpty()) listOf("Relevant provisions of Indian Law") else grounds,
                        date = currentDate,
                        fullDraftText = json.optString("fullDraftText", rawTranscript),
                        suggestedCategory = cat
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini call failed or JSON parsing error: ${e.message}. Using dynamic local legal engine.")
            }
        }

        // Dynamic synthesis without static mock
        val extractedOpponent = extractOpposingParty(rawTranscript)
        val extractedAmount = extractAmount(rawTranscript)
        val grounds = determineStatutoryGrounds(rawTranscript)

        val subject = when (userRole) {
            UserRole.LEGAL_COUNSEL -> if (isTamil) "வழக்கறிஞர் சட்ட அறிவிப்பு: $extractedOpponent-க்கு எதிரான சட்டப்பூர்வ கோரிக்கை அறிவிப்பு" else "Legal Counsel Statutory Demand Notice against $extractedOpponent"
            UserRole.MSME_BUSINESS -> if (isTamil) "MSMED சட்டம் பிரிவு 15/16: $extractedOpponent-க்கு எதிரான வணிக நிலுவைத் தொகை மீட்பு அறிவிப்பு" else "MSMED Act Commercial Notice: Overdue Invoices Demand against $extractedOpponent"
            UserRole.CYBER_FRAUD_VICTIM -> if (isTamil) "அவசர இணைய நிதி மோசடி புகார்: $extractedOpponent கணக்கு முடக்கம் கோரிக்கை (1930 / IT Act)" else "Emergency Cyber Crime Incident Report & Freezing Request against $extractedOpponent"
            UserRole.CITIZEN -> if (isTamil) "சட்டப்பூர்வ அறிவிப்பு: $extractedOpponent-க்கு எதிரான முறையீடு மற்றும் இழப்பீட்டுக் கோரிக்கை" else "Statutory Legal Notice & Formal Grievance against $extractedOpponent"
        }

        val authority = when (userRole) {
            UserRole.CYBER_FRAUD_VICTIM -> if (isTamil) "சைபர் கிரைம் பிரிவு & தேசிய இணைய குற்றப் பதிவு போர்டல் (1930)" else "Cyber Crime Cell & National Cyber Crime Reporting Portal (1930)"
            UserRole.MSME_BUSINESS -> if (isTamil) "MSME சமாதான் குறைதீர்ப்பு கவுன்சில் / வணிக நீதிமன்றம்" else "MSME Facilitation Council & Commercial Court"
            UserRole.LEGAL_COUNSEL -> if (isTamil) "தகுதிவாய்ந்த சிவில் / நுகர்வோர் நீதிமன்றம்" else "Competent Civil Court / Statutory Adjudicating Forum"
            UserRole.CITIZEN -> if (isTamil) "மாவட்ட நுகர்வோர் குறைதீர்ப்பு ஆணையம் / தகுதிவாய்ந்த தீர்ப்பாயம்" else "District Consumer Disputes Redressal Commission"
        }

        val relief = when (userRole) {
            UserRole.LEGAL_COUNSEL -> if (isTamil) {
                "1. எனது கட்சிக்காரருக்கு ஏற்பட்ட இழப்புத் தொகையை ${if (extractedAmount.isNotBlank()) "₹$extractedAmount உடன் " else ""}உடனடியாகத் திருப்பிச் செலுத்துதல்.\n2. அறிவிப்பு தயாரிப்பு கட்டணம் ₹15,000 வழங்குதல்.\n3. 15 நாட்களுக்குள் நிவர்த்தி செய்யாவிடில் சிவில்/குற்றவியல் நடவடிக்கை தொடரப்படும்."
            } else {
                "1. Immediate restitution of disputed sum ${if (extractedAmount.isNotBlank()) "(₹$extractedAmount) " else ""}with statutory interest to my client.\n2. Payment of ₹15,000 towards legal notice drafting costs.\n3. Mandatory compliance within 15 days failing which legal prosecution shall commence."
            }
            UserRole.MSME_BUSINESS -> if (isTamil) {
                "1. வணிக நிலுவைத் தொகையான ${if (extractedAmount.isNotBlank()) "₹$extractedAmount-ஐ " else ""}உடனடியாக விடுவித்தல்.\n2. MSMED சட்டம் பிரிவு 16-ன் கீழ் RBI வங்கி விகிதத்தில் 3 மடங்கு கூட்டு வட்டி வழங்குதல்.\n3. தொழில் இழப்பீடு வழங்குதல்."
            } else {
                "1. Immediate liquidation of overdue commercial invoices ${if (extractedAmount.isNotBlank()) "(₹$extractedAmount)" else ""}.\n2. Mandatory compound monthly interest at 3x RBI Bank Rate under Section 16 MSMED Act, 2006.\n3. Restitution for impaired working capital."
            }
            UserRole.CYBER_FRAUD_VICTIM -> if (isTamil) {
                "1. மோசடி செய்யப்பட்ட ${if (extractedAmount.isNotBlank()) "₹$extractedAmount " else ""}பரிவர்த்தனையை RBI பூஜ்ஜிய பொறுப்பு விதிகளின் கீழ் திரும்பப் பெறுதல்.\n2. $extractedOpponent-ன் வங்கி/UPI கணக்குகளை உடனடியாக முடக்குதல்.\n3. IT சட்டம் 66D-ன் கீழ் முதல் தகவல் அறிக்கை (FIR) பதிவு செய்தல்."
            } else {
                "1. Immediate emergency freeze of destination bank account/UPI associated with $extractedOpponent.\n2. Full credit reversal of ${if (extractedAmount.isNotBlank()) "₹$extractedAmount " else ""}under RBI Zero-Liability Mandate.\n3. Registration of FIR under Section 66D IT Act."
            }
            UserRole.CITIZEN -> if (isTamil) {
                "1. பாதிக்கப்பட்டவருக்கு ஏற்பட்ட இழப்புத் தொகையை ${if (extractedAmount.isNotBlank()) "₹$extractedAmount உடன் " else ""}உடனடியாகத் திரும்ப வழங்குதல்.\n2. மன உளைச்சல் மற்றும் சேவைக் குறைபாட்டிற்கான சட்டப்பூர்வ இழப்பீடு வழங்குதல்.\n3. இந்த அறிவிப்பு கிடைத்த 15 நாட்களுக்குள் நடவடிக்கை எடுத்தல்."
            } else {
                "1. Immediate restitution and refund of claimed sum ${if (extractedAmount.isNotBlank()) "(₹$extractedAmount) " else ""}with statutory interest.\n2. Reasonable compensation towards mental agony and litigation costs.\n3. Compliance within statutory period of 15 days."
            }
        }

        val fullDraft = if (isTamil) {
            val senderDesignation = when (userRole) {
                UserRole.LEGAL_COUNSEL -> "வழக்கறிஞர் (மனுதாரர் கட்சிக்காரரின் சார்பாக)"
                UserRole.MSME_BUSINESS -> "சிறு, குறு & நடுத்தர தொழில் நிறுவனம் (MSME அங்கீகரிக்கப்பட்ட பிரதிநிதி)"
                UserRole.CYBER_FRAUD_VICTIM -> "பாதிக்கப்பட்ட மனுதாரர் (இணைய நிதி மோசடி பாதிக்கப்பட்டவர்)"
                UserRole.CITIZEN -> "பாதிக்கப்பட்ட குடிமகன் / நுகர்வோர்"
            }
            """
            தேதி: $currentDate
            
            அனுப்புநர்:
            $senderDesignation
            
            பெறுநர்:
            $extractedOpponent
            நகல்: $authority
            
            பொருள்: $subject
            
            மதிப்பிற்குரியீர்,
            
            1. உண்மை விவரங்கள் (Statement of Facts):
            $rawTranscript
            
            2. சட்டப்பிரிவுகள் (Statutory Grounds):
            ${grounds.joinToString("\n") { "• $it" }}
            
            3. கோரப்படும் நிவாரணம் (Demanded Relief):
            $relief
            
            இப்படிக்கு,
            $senderDesignation
            """.trimIndent()
        } else {
            val senderDesignation = when (userRole) {
                UserRole.LEGAL_COUNSEL -> "Advocate & Legal Counsel (On behalf of Aggrieved Client)"
                UserRole.MSME_BUSINESS -> "Authorized Signatory, MSME Registered Enterprise"
                UserRole.CYBER_FRAUD_VICTIM -> "Aggrieved Victim of Cyber Financial Fraud"
                UserRole.CITIZEN -> "Aggrieved Citizen / Consumer"
            }
            """
            DATE: $currentDate
            
            FROM:
            $senderDesignation
            
            TO:
            $extractedOpponent
            COPY TO: $authority
            
            SUBJECT: $subject
            
            LEGAL STATEMENT OF FACTS:
            1. The complainant submits that the incident occurred as stated below:
            "$rawTranscript"
            
            2. STATUTORY GROUNDS & VIOLATIONS:
            ${grounds.joinToString("\n") { "• $it" }}
            
            3. RELIEF & DEMANDS:
            $relief
            
            Take notice that failure to comply within statutory period shall compel legal proceedings before the competent court at your sole risk and costs.
            
            Yours faithfully,
            $senderDesignation
            """.trimIndent()
        }

        DynamicComplaint(
            title = when (userRole) {
                UserRole.LEGAL_COUNSEL -> if (isTamil) "வழக்கறிஞர் சட்ட அறிவிப்பு மனு" else "Advocate Statutory Demand Notice"
                UserRole.MSME_BUSINESS -> if (isTamil) "MSME வணிக நிலுவை மீட்பு மனு" else "MSME Commercial Recovery Demand"
                UserRole.CYBER_FRAUD_VICTIM -> if (isTamil) "இணைய குற்ற அவசர புகார் மனு" else "Emergency Cyber Crime Complaint"
                UserRole.CITIZEN -> if (isTamil) "சட்டப்பூர்வ நுகர்வோர் புகார் மனு" else "Statutory Citizen Grievance"
            },
            subject = subject,
            recipientAuthority = authority,
            opposingParty = extractedOpponent,
            statementOfFacts = rawTranscript,
            demandedRelief = relief,
            statutoryGrounds = grounds,
            date = currentDate,
            fullDraftText = fullDraft,
            suggestedCategory = when {
                userRole == UserRole.CYBER_FRAUD_VICTIM || rawTranscript.contains("upi", ignoreCase = true) || rawTranscript.contains("bank", ignoreCase = true) -> DisputeCategory.CYBER_FINANCIAL_FRAUD
                userRole == UserRole.MSME_BUSINESS -> DisputeCategory.CONSUMER_GRIEVANCE
                rawTranscript.contains("rent", ignoreCase = true) || rawTranscript.contains("deposit", ignoreCase = true) -> DisputeCategory.TENANCY_RENT
                rawTranscript.contains("salary", ignoreCase = true) || rawTranscript.contains("work", ignoreCase = true) -> DisputeCategory.EMPLOYMENT_SALARY
                else -> DisputeCategory.CONSUMER_GRIEVANCE
            }
        )
    }

    private fun extractOpposingParty(text: String): String {
        val words = text.split(" ")
        for (i in 0 until words.size - 1) {
            if (words[i].equals("against", ignoreCase = true) || words[i].equals("by", ignoreCase = true) || words[i].equals("from", ignoreCase = true)) {
                return words.subList(i + 1, (i + 4).coerceAtMost(words.size)).joinToString(" ")
            }
        }
        return "Opposing Party / Merchant"
    }

    private fun extractAmount(text: String): String {
        val regex = Regex("""(?i)(?:rs\.?|inr|₹)\s*(\d+(?:,\d+)*(?:\.\d+)?)|\b(\d+)\s*(?:rupees|rs)""")
        val match = regex.find(text)
        return match?.groupValues?.get(1)?.ifBlank { match.groupValues.getOrNull(2) } ?: ""
    }

    private fun determineStatutoryGrounds(text: String): List<String> {
        val grounds = mutableListOf<String>()
        val lower = text.lowercase()
        if (lower.contains("money") || lower.contains("pay") || lower.contains("fraud") || lower.contains("otp") || lower.contains("upi")) {
            grounds.add("Information Technology Act, 2000 (Section 66D - Cheating by Personation)")
            grounds.add("Bharatiya Nyaya Sanhita, 2023 (Section 318 - Cheating)")
        }
        if (lower.contains("defect") || lower.contains("product") || lower.contains("service") || lower.contains("refund")) {
            grounds.add("Consumer Protection Act, 2019 (Section 2(47) & Section 35 - Unfair Trade Practice)")
        }
        if (lower.contains("rent") || lower.contains("deposit") || lower.contains("house") || lower.contains("landlord")) {
            grounds.add("Transfer of Property Act, 1882 & Tenancy Protection Rules")
        }
        if (grounds.isEmpty()) {
            grounds.add("Consumer Protection Act, 2019 (Section 35)")
            grounds.add("Bharatiya Nyaya Sanhita, 2023")
        }
        return grounds
    }

    fun saveAsActiveCase(complaint: DynamicComplaint, onSaved: (caseId: String) -> Unit) {
        viewModelScope.launch {
            val role = _currentRole.value
            val caseId = caseRepository.createCaseWithDefaults(
                title = complaint.title,
                category = complaint.suggestedCategory,
                incidentDate = complaint.date,
                opposingParty = complaint.opposingParty,
                estimatedClaimAmount = extractAmount(complaint.statementOfFacts),
                factualSummary = complaint.statementOfFacts,
                demandedRelief = complaint.demandedRelief,
                userRole = role
            )
            // Update the generated draft text
            val createdCase = caseRepository.getCaseOnce(caseId)
            if (createdCase != null) {
                caseRepository.updateCase(
                    createdCase.copy(
                        subjectLine = complaint.subject,
                        recipientAuthority = complaint.recipientAuthority,
                        generatedComplaintDraft = complaint.fullDraftText
                    )
                )
            }
            resetIntake()
            onSaved(caseId)
        }
    }

    fun analyzeVoiceGrievanceDynamic(
        transcript: String,
        onResult: (LegalComplaintResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = GeminiLegalEngine.analyzeDynamicVoiceGrievance(transcript, _selectedLanguage.value)
            onResult(result)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
        audioRecorderHelper.release()
    }
}
