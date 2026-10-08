package com.justra.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.components.BiometricReauthModal
import com.justra.app.ui.screens.AccountSettingsHubScreen
import com.justra.app.ui.screens.ActionNavigatorScreen
import com.justra.app.ui.screens.AuthScreen
import com.justra.app.ui.screens.BiometricAuthScreen
import com.justra.app.ui.screens.BnsIpcTransitionScreen
import com.justra.app.ui.screens.ChatAssistantScreen
import com.justra.app.ui.screens.ComplaintGeneratorScreen
import com.justra.app.ui.screens.ChatAndVoiceScreen
import com.justra.app.ui.screens.ConsumerMediationScreen
import com.justra.app.ui.screens.CounselHandoffScreen
import com.justra.app.ui.screens.CourtFeeCalculatorScreen
import com.justra.app.ui.screens.CyberCrimeDossierScreen
import com.justra.app.ui.screens.EvidenceVaultScreen
import com.justra.app.ui.screens.GlobalJurisdictionScreen
import com.justra.app.ui.screens.HomeScreen
import com.justra.app.ui.screens.LandingScreen
import com.justra.app.ui.screens.LanguageConsentScreen
import com.justra.app.ui.screens.LegalNoticeDisputeComposerScreen
import com.justra.app.ui.screens.LimitationRemindersScreen
import com.justra.app.ui.screens.MotorAccidentMactScreen
import com.justra.app.ui.screens.MyCasesHubScreen
import com.justra.app.ui.screens.NalsaFreeLegalAidScreen
import com.justra.app.ui.screens.NewGrievanceScreen
import com.justra.app.ui.screens.NotificationsRemindersCenterScreen
import com.justra.app.ui.screens.OnboardingScreen
import com.justra.app.ui.screens.PermissionsSetupScreen
import com.justra.app.ui.screens.PinAuthScreen
import com.justra.app.ui.screens.RtiDraftingWizardScreen
import com.justra.app.ui.screens.ScamCheckerScreen
import com.justra.app.ui.screens.Section65BCertificateGeneratorScreen
import com.justra.app.ui.screens.SmartComplaintScreen
import com.justra.app.ui.screens.SplashScreen
import com.justra.app.ui.screens.StatutoryToolsHubScreen
import com.justra.app.ui.screens.TimelineReadinessScreen
import com.justra.app.ui.screens.WomenDomesticRightsScreen
import com.justra.app.ui.screens.WorldWideLawScreen
import com.justra.app.ui.screens.citizenship.CitizenshipLawsScreen
import com.justra.app.ui.screens.citizenship.LegalTopicDetailScreen
import com.justra.app.ui.screens.complaint.VoiceComplaintRegistrationScreen
import com.justra.app.ui.screens.complaint.VoiceComplaintScreen
import com.justra.app.ui.screens.subtopics.SubTopicDetailScreen
import com.justra.app.ui.screens.AdoptionLegalProcessScreen
import com.justra.app.ui.screens.ArbitrationClauseDraftingScreen
import com.justra.app.ui.screens.ArrestRightsEmergencyGuideScreen
import com.justra.app.ui.screens.BailEligibilityPredictorScreen
import com.justra.app.ui.screens.ChequeBounceNoticeWizardScreen
import com.justra.app.ui.screens.ContractorVendorRecoveryScreen
import com.justra.app.ui.screens.CyberBankFreezeUnfreezeScreen
import com.justra.app.ui.screens.CyberBullyingPoshScreen
import com.justra.app.ui.screens.DefamationNoticeWizardScreen
import com.justra.app.ui.screens.DivorceMutualConsentScreen
import com.justra.app.ui.screens.EnvironmentalPollutionNGTScreen
import com.justra.app.ui.screens.GratuityPFCalculatorScreen
import com.justra.app.ui.screens.IdentityTheftSIMFraudScreen
import com.justra.app.ui.screens.InsuranceClaimRejectionScreen
import com.justra.app.ui.screens.IntellectualPropertyCopyrightScreen
import com.justra.app.ui.screens.LegalCostsTaxationEstimatorScreen
import com.justra.app.ui.screens.LokAdalatApplicationScreen
import com.justra.app.ui.screens.MedicalNegligenceClaimScreen
import com.justra.app.ui.screens.PassportImpoundmentAppealScreen
import com.justra.app.ui.screens.RERAHomebuyerDisputeScreen
import com.justra.app.ui.screens.SeniorCitizenMaintenanceScreen
import com.justra.app.ui.screens.TenantEvictionDefenseScreen
import com.justra.app.ui.screens.TradeLicenceShopActScreen
import com.justra.app.ui.screens.WillProbateSuccessionScreen
import com.justra.app.ui.viewmodel.JustraViewModel

object JustraDestinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val PERMISSIONS_SETUP = "permissions_setup"
    const val LANDING = "landing"
    const val AUTH = "auth"
    const val LANGUAGE_CONSENT = "language_consent"
    const val BIOMETRIC_AUTH = "biometric_auth"
    const val HOME = "home"
    const val CHAT = "chat?prompt={prompt}"
    const val ACTION_NAVIGATOR = "action_navigator/{caseId}"
    const val COMPLAINT_GENERATOR = "complaint_generator/{caseId}"
    const val SMART_COMPLAINT = "smart_complaint/{caseId}"
    const val NEW_GRIEVANCE = "new_grievance"
    const val EVIDENCE_VAULT = "evidence_vault/{caseId}"
    const val VOICE_INTAKE = "voice_intake"
    const val TIMELINE_READINESS = "timeline_readiness/{caseId}"
    const val SCAM_CHECKER = "scam_checker"
    const val COUNSEL_HANDOFF = "counsel_handoff/{caseId}"
    const val COURT_FEE_CALCULATOR = "court_fee_calculator"
    const val SECTION_65B_CERTIFICATE = "section_65b_certificate"
    const val RTI_DRAFTING_WIZARD = "rti_drafting_wizard"
    const val LEGAL_NOTICE_COMPOSER = "legal_notice_composer"
    const val BNS_IPC_TRANSITION = "bns_ipc_transition"
    const val NALSA_FREE_LEGAL_AID = "nalsa_free_legal_aid"
    const val MOTOR_ACCIDENT_MACT = "motor_accident_mact"
    const val CONSUMER_MEDIATION = "consumer_mediation"
    const val WOMEN_DOMESTIC_RIGHTS = "women_domestic_rights"
    const val CYBER_CRIME_DOSSIER = "cyber_crime_dossier"
    const val ACCOUNT_SETTINGS = "account_settings"
    const val NOTIFICATIONS_CENTER = "notifications_center"
    const val LIMITATION_REMINDERS = "limitation_reminders"
    const val GLOBAL_JURISDICTION = "global_jurisdiction"
    const val WORLD_WIDE_LAW = "world_wide_law"
    const val CITIZENSHIP_LAWS = "citizenship_laws"
    const val BAIL_PREDICTOR = "bail_predictor"
    const val CHEQUE_BOUNCE_WIZARD = "cheque_bounce_wizard"
    const val CYBER_POSH_CELL = "cyber_posh_cell"
    const val SENIOR_CITIZEN_TRIBUNAL = "senior_citizen_tribunal"
    const val DIVORCE_MUTUAL_CONSENT = "divorce_mutual_consent"
    const val CHILD_CUSTODY_VISITATION = "child_custody_visitation"
    const val ARREST_RIGHTS_GUIDE = "arrest_rights_guide"
    const val GRATUITY_PF_CALCULATOR = "gratuity_pf_calculator"
    const val CYBER_BANK_UNFREEZE = "cyber_bank_unfreeze"
    const val TRADE_LICENSE_SHOP_ACT = "trade_license_shop_act"
    const val RERA_HOMEBUYER_DISPUTE = "rera_homebuyer_dispute"
    const val IPR_TRADEMARK_COPYRIGHT = "ipr_trademark_copyright"
    const val ARBITRATION_CLAUSE_DRAFT = "arbitration_clause_draft"
    const val LOK_ADALAT_PETITION = "lok_adalat_petition"
    const val WILL_PROBATE_SUCCESSION = "will_probate_succession"
    const val MEDICAL_NEGLIGENC_CLAIM = "medical_negligence_claim"
    const val INSURANCE_OMBUDSMAN_APPEAL = "insurance_ombudsman_appeal"
    const val TENANT_EVICTION_DEFENSE = "tenant_eviction_defense"
    const val DEFAMATION_NOTICE_WIZARD = "defamation_notice_wizard"
    const val PASSPORT_IMPOUND_APPEAL = "passport_impound_appeal"
    const val VENDOR_RECOVERY_SUIT = "vendor_recovery_suit"
    const val ENVIRONMENTAL_NGT_PORTAL = "environmental_ngt_portal"
    const val SIM_FRAUD_TAFCOP = "sim_fraud_tafcop"
    const val ADOPTION_CARA_PROCESS = "adoption_cara_process"
    const val LITIGATION_COSTS_ESTIMATOR = "litigation_costs_estimator"
    const val LEGAL_TOPIC_DETAIL = "legal_topic_detail/{topicId}"
    const val VOICE_COMPLAINT_REGISTRATION = "voice_complaint_registration"
}

val NyayaMateDestinations = JustraDestinations

@Composable
fun JustraNavGraph(
    navController: NavHostController,
    viewModel: JustraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val hasAcceptedConsent by viewModel.hasAcceptedConsent.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val isReauthRequired by viewModel.isReauthRequired.collectAsState()
    val activeCases by viewModel.activeCases.collectAsState()
    val allCases by viewModel.allCases.collectAsState()
    val activeNation by viewModel.activeJurisdiction.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    val startDestination = JustraDestinations.SPLASH

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(350))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(350))
            },
            modifier = modifier
        ) {
            // Startup Sequence: Splash -> Onboarding -> Landing -> Auth -> Home
            composable(JustraDestinations.SPLASH) {
                SplashScreen(
                    currentLanguage = language,
                    onNavigateNext = {
                        navController.navigate(JustraDestinations.ONBOARDING) {
                            popUpTo(JustraDestinations.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(JustraDestinations.ONBOARDING) {
                OnboardingScreen(
                    onOnboardingFinished = {
                        navController.navigate(JustraDestinations.PERMISSIONS_SETUP) {
                            popUpTo(JustraDestinations.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(JustraDestinations.PERMISSIONS_SETUP) {
                PermissionsSetupScreen(
                    currentLanguage = language,
                    onPermissionsFinished = {
                        navController.navigate(JustraDestinations.LANDING) {
                            popUpTo(JustraDestinations.PERMISSIONS_SETUP) { inclusive = true }
                        }
                    }
                )
            }

            composable(JustraDestinations.LANDING) {
                LandingScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onGetStarted = {
                        navController.navigate(JustraDestinations.AUTH) {
                            popUpTo(JustraDestinations.LANDING) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(JustraDestinations.AUTH) {
                            popUpTo(JustraDestinations.LANDING) { inclusive = true }
                        }
                    },
                    onDialEmergency1930 = { com.justra.app.util.ActionUtils.dialEmergencyHelpline(context, "1930") },
                    onCategoryClick = { _ ->
                        navController.navigate(JustraDestinations.AUTH)
                    }
                )
            }

            composable(JustraDestinations.AUTH) {
                AuthScreen(
                    currentLanguage = language,
                    onAuthenticated = {
                        navController.navigate(JustraDestinations.HOME) {
                            popUpTo(JustraDestinations.AUTH) { inclusive = true }
                        }
                    }
                )
            }

            // Core & 22 Navigation Drawer Routes
            composable(JustraDestinations.LANGUAGE_CONSENT) {
                LanguageConsentScreen(
                    currentLanguage = language,
                    onLanguageSelected = { viewModel.setLanguage(it) },
                    onConsentAccepted = {
                        viewModel.acceptConsent()
                        navController.navigate(JustraDestinations.AUTH) {
                            popUpTo(JustraDestinations.LANGUAGE_CONSENT) { inclusive = true }
                        }
                    }
                )
            }

            composable(JustraDestinations.HOME) {
                HomeScreen(
                    currentLanguage = language,
                    activeCases = activeCases,
                    userRole = userRole,
                    notifications = emptyList(),
                    unreadNotificationsCount = 0,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onSwitchRole = { newRole -> viewModel.setUserRole(newRole) },
                    onNavigateToRoute = { route -> navController.navigate(route) },
                    onNavigateToChat = { navController.navigate("chat") },
                    onNavigateToCaseDetail = { caseId -> navController.navigate("action_navigator/$caseId") },
                    onNavigateToActionNavigator = { caseId -> navController.navigate("action_navigator/$caseId") },
                    onNavigateToComplaint = { caseId -> navController.navigate("complaint_generator/$caseId") },
                    onNavigateToEvidenceVault = { caseId -> navController.navigate("evidence_vault/$caseId") },
                    onNavigateToTimeline = { caseId -> navController.navigate("timeline_readiness/$caseId") },
                    onNavigateToScamChecker = { navController.navigate(JustraDestinations.SCAM_CHECKER) },
                    onNavigateToCounselHandoff = { caseId -> navController.navigate("counsel_handoff/$caseId") },
                    onTopicClick = { categoryKey -> navController.navigate("sub_topic_detail/$categoryKey") },
                    onNavigateToVoice = { navController.navigate(JustraDestinations.VOICE_INTAKE) },
                    onCreateNewDispute = { _, _, _, _, _, _, _, _ -> navController.navigate(JustraDestinations.NEW_GRIEVANCE) },
                    onDialHelpline = { number -> com.justra.app.util.ActionUtils.dialEmergencyHelpline(context, number) }
                )
            }

            composable(JustraDestinations.NEW_GRIEVANCE) {
                NewGrievanceScreen(
                    viewModel = viewModel,
                    currentLanguage = language,
                    onBackClick = { navController.popBackStack() },
                    onCreated = { caseId ->
                        navController.navigate("smart_complaint/$caseId")
                    }
                )
            }

            composable(JustraDestinations.SMART_COMPLAINT) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: "new"
                SmartComplaintScreen(
                    caseId = caseId,
                    currentLanguage = language,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.CHAT) { backStackEntry ->
                val prompt = backStackEntry.arguments?.getString("prompt")
                val chatMessages by viewModel.chatMessages.collectAsState(initial = emptyList())
                val recordingState by viewModel.recordingState.collectAsState()
                val audioWaveforms by viewModel.audioWaveforms.collectAsState()
                val liveTranscript by viewModel.liveTranscript.collectAsState()

                ChatAssistantScreen(
                    currentLanguage = language,
                    messages = chatMessages,
                    recordingState = recordingState,
                    audioWaveforms = audioWaveforms,
                    initialPrompt = prompt,
                    liveTranscript = liveTranscript,
                    onSendMessage = { text -> viewModel.sendChatMessage(text) },
                    onStartRecording = { viewModel.startVoiceRecording() },
                    onStopRecording = { viewModel.stopVoiceRecordingAndSubmit() },
                    onAddProofToVault = { key, value -> viewModel.addProofFromExtractedChip(key, value) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() },
                    onClearChatHistory = { viewModel.clearChatHistory() }
                )
            }

            composable(JustraDestinations.SCAM_CHECKER) {
                val currentAnalysis by viewModel.currentScamAnalysis.collectAsState()
                val isAnalyzing by viewModel.isAnalyzingScam.collectAsState()
                val historyList by viewModel.scamIncidents.collectAsState()
                ScamCheckerScreen(
                    currentLanguage = language,
                    currentAnalysis = currentAnalysis,
                    isAnalyzing = isAnalyzing,
                    historyList = historyList,
                    onAnalyzeText = { text -> viewModel.analyzeScamMessage(text) },
                    onDeleteIncident = { incidentId -> viewModel.deleteScamIncident(incidentId) },
                    onDialHelpline = { number -> com.justra.app.util.ActionUtils.dialEmergencyHelpline(context, number) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("security_settings") {
                PinAuthScreen(
                    currentLanguage = language,
                    onAuthenticated = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.LIMITATION_REMINDERS) {
                LimitationRemindersScreen(
                    viewModel = viewModel,
                    currentLanguage = language,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.NOTIFICATIONS_CENTER) {
                NotificationsRemindersCenterScreen(
                    viewModel = viewModel,
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() },
                    onNavigateToRoute = { route -> navController.navigate(route) }
                )
            }

            composable(JustraDestinations.ACTION_NAVIGATOR) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                val caseEntity by viewModel.getCaseStream(caseId).collectAsState(initial = null)
                val stepsFlow: kotlinx.coroutines.flow.Flow<List<com.justra.app.data.local.ActionStepEntity>> = remember(caseId) { viewModel.getStepsForCase(caseId) }
                ActionNavigatorScreen(
                    currentLanguage = language,
                    caseEntity = caseEntity,
                    stepsFlow = stepsFlow,
                    onStepChecked = { stepId, completed -> viewModel.setStepCompletion(stepId, completed) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.COMPLAINT_GENERATOR) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                val caseEntity by viewModel.getCaseStream(caseId).collectAsState(initial = null)
                val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
                val lastGeneratedPdf by viewModel.lastGeneratedPdf.collectAsState()
                ComplaintGeneratorScreen(
                    currentLanguage = language,
                    caseEntity = caseEntity,
                    userRole = userRole,
                    isGeneratingPdf = isGeneratingPdf,
                    lastGeneratedPdf = lastGeneratedPdf,
                    onSaveComplaintDraft = { updatedText -> viewModel.updateCaseDraft(caseId, updatedText) },
                    onSwitchRoleAndRegenerate = { newRole -> viewModel.regenerateComplaintDraft(caseId, newRole) },
                    onExportPdf = { ctx, draft -> val targetCase = caseEntity; if (targetCase != null) viewModel.exportCaseToPdf(ctx, targetCase, draft) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.EVIDENCE_VAULT) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                val caseEntity by viewModel.getCaseStream(caseId).collectAsState(initial = null)
                val artifactsFlow: kotlinx.coroutines.flow.Flow<List<com.justra.app.data.local.EvidenceArtifactEntity>> = remember(caseId) { viewModel.getArtifactsForCase(caseId) }
                EvidenceVaultScreen(
                    currentLanguage = language,
                    caseEntity = caseEntity,
                    artifactsFlow = artifactsFlow,
                    onAddArtifact = { name, category, notes, fileBytes, mimeType -> viewModel.addEvidenceArtifact(caseId, name, category, notes, fileBytes, mimeType) },
                    onDeleteArtifact = { artifactId -> viewModel.deleteArtifactById(artifactId) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.TIMELINE_READINESS) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                val caseEntity by viewModel.getCaseStream(caseId).collectAsState(initial = null)
                val eventsFlow: kotlinx.coroutines.flow.Flow<List<com.justra.app.data.local.TimelineEventEntity>> = remember(caseId) { viewModel.getEventsForCase(caseId) }
                val evidenceCountFlow: kotlinx.coroutines.flow.Flow<Int> = remember(caseId) { viewModel.getArtifactCountForCase(caseId) }
                TimelineReadinessScreen(
                    currentLanguage = language,
                    caseEntity = caseEntity,
                    eventsFlow = eventsFlow,
                    evidenceCountFlow = evidenceCountFlow,
                    onAddEvent = { date, title, desc, isInferred -> viewModel.addEvent(caseId, date, title, desc, isInferred) },
                    onDeleteEvent = { eventId -> viewModel.deleteEventById(eventId) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.COUNSEL_HANDOFF) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                val caseEntity by viewModel.getCaseStream(caseId).collectAsState(initial = null)
                val tokensFlow: kotlinx.coroutines.flow.Flow<List<com.justra.app.data.local.ShareTokenEntity>> = remember(caseId) { viewModel.getTokensForCase(caseId) }
                CounselHandoffScreen(
                    currentLanguage = language,
                    caseEntity = caseEntity,
                    tokensFlow = tokensFlow,
                    onGenerateShareToken = { validityHours -> viewModel.generateShareToken(caseId, validityHours) },
                    onAppendAdvocateNotes = { tokenId, notes -> viewModel.appendAdvocateNotes(tokenId, notes) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.COURT_FEE_CALCULATOR) {
                CourtFeeCalculatorScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.SECTION_65B_CERTIFICATE) {
                Section65BCertificateGeneratorScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.RTI_DRAFTING_WIZARD) {
                RtiDraftingWizardScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.LEGAL_NOTICE_COMPOSER) {
                LegalNoticeDisputeComposerScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.BNS_IPC_TRANSITION) {
                BnsIpcTransitionScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.NALSA_FREE_LEGAL_AID) {
                NalsaFreeLegalAidScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.MOTOR_ACCIDENT_MACT) {
                MotorAccidentMactScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.CONSUMER_MEDIATION) {
                ConsumerMediationScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.WOMEN_DOMESTIC_RIGHTS) {
                WomenDomesticRightsScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.CYBER_CRIME_DOSSIER) {
                CyberCrimeDossierScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("my_cases_hub") {
                MyCasesHubScreen(
                    currentLanguage = language,
                    activeCases = activeCases,
                    userRole = userRole,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onNavigateToRoute = { route -> navController.navigate(route) },
                    onNavigateToActionNavigator = { caseId -> navController.navigate("action_navigator/$caseId") },
                    onNavigateToComplaint = { caseId -> navController.navigate("complaint_generator/$caseId") },
                    onNavigateToEvidenceVault = { caseId -> navController.navigate("evidence_vault/$caseId") },
                    onNavigateToTimeline = { caseId -> navController.navigate("timeline_readiness/$caseId") },
                    onNavigateToCounselHandoff = { caseId -> navController.navigate("counsel_handoff/$caseId") },
                    onCreateNewDispute = { title, category, incidentDate, opposingParty, estimatedAmount, summary, relief, role ->
                        viewModel.createNewDispute(
                            title = title,
                            category = category,
                            incidentDate = incidentDate,
                            opposingParty = opposingParty,
                            estimatedClaimAmount = estimatedAmount,
                            factualSummary = summary ?: "",
                            demandedRelief = relief,
                            onCreated = { newId -> navController.navigate("smart_complaint/$newId") }
                        )
                    }
                )
            }

            composable("statutory_tools_hub") {
                StatutoryToolsHubScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onNavigateToRoute = { route -> navController.navigate(route) },
                    onSelectTopic = { categoryKey -> navController.navigate("sub_topic_detail/$categoryKey") }
                )
            }

            composable("account_settings_hub") {
                AccountSettingsHubScreen(
                    viewModel = viewModel,
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(JustraDestinations.AUTH) {
                            popUpTo(JustraDestinations.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(JustraDestinations.ACCOUNT_SETTINGS) {
                AccountSettingsHubScreen(
                    viewModel = viewModel,
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(JustraDestinations.AUTH) {
                            popUpTo(JustraDestinations.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(JustraDestinations.GLOBAL_JURISDICTION) {
                GlobalJurisdictionScreen(
                    currentLanguage = language,
                    activeNation = activeNation,
                    onSelectNation = { nation -> viewModel.setJurisdiction(nation) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() },
                    onNavigateToWorldWideLaw = { navController.navigate(JustraDestinations.WORLD_WIDE_LAW) }
                )
            }

            composable(JustraDestinations.WORLD_WIDE_LAW) {
                WorldWideLawScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.VOICE_INTAKE) {
                VoiceComplaintRegistrationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCaseCreated = { caseId ->
                        navController.navigate("action_navigator/$caseId") {
                            popUpTo(JustraDestinations.HOME)
                        }
                    }
                )
            }

            composable(JustraDestinations.CITIZENSHIP_LAWS) {
                CitizenshipLawsScreen(
                    currentLanguage = language,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTimeline = {
                        val fallbackCaseId = allCases.firstOrNull()?.caseId ?: "new"
                        navController.navigate("timeline_readiness/$fallbackCaseId")
                    },
                    onNavigateToEvidenceVault = {
                        val fallbackCaseId = allCases.firstOrNull()?.caseId ?: "new"
                        navController.navigate("evidence_vault/$fallbackCaseId")
                    },
                    onToggleLanguage = { viewModel.toggleLanguage() }
                )
            }

            composable(
                route = JustraDestinations.LEGAL_TOPIC_DETAIL,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                LegalTopicDetailScreen(
                    topicId = topicId,
                    currentLanguage = language,
                    onNavigateBack = { navController.popBackStack() },
                    onInitiateGrievance = { category ->
                        viewModel.createNewDispute(
                            title = " Notice",
                            category = category,
                            incidentDate = null,
                            opposingParty = null,
                            estimatedClaimAmount = null,
                            factualSummary = "Statutory grievance under ",
                            demandedRelief = null,
                            onCreated = {
                                navController.navigate(JustraDestinations.VOICE_COMPLAINT_REGISTRATION)
                            }
                        )
                    },
                    onNavigateToTimeline = {
                        val fallbackCaseId = allCases.firstOrNull()?.caseId ?: "new"
                        navController.navigate("timeline_readiness/$fallbackCaseId")
                    },
                    onToggleLanguage = { viewModel.toggleLanguage() }
                )
            }

            composable(
                route = Screen.SubTopicDetail.route,
                arguments = listOf(navArgument("categoryKey") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryKey = backStackEntry.arguments?.getString("categoryKey") ?: ""
                SubTopicDetailScreen(
                    categoryKey = categoryKey,
                    currentLanguage = language,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToVoice = { _ ->
                        navController.navigate(JustraDestinations.VOICE_INTAKE)
                    }
                )
            }

            composable(JustraDestinations.BAIL_PREDICTOR) {
                BailEligibilityPredictorScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.CHEQUE_BOUNCE_WIZARD) {
                ChequeBounceNoticeWizardScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.CYBER_POSH_CELL) {
                CyberBullyingPoshScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.SENIOR_CITIZEN_TRIBUNAL) {
                SeniorCitizenMaintenanceScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.DIVORCE_MUTUAL_CONSENT) {
                DivorceMutualConsentScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.ARREST_RIGHTS_GUIDE) {
                ArrestRightsEmergencyGuideScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.GRATUITY_PF_CALCULATOR) {
                GratuityPFCalculatorScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.CYBER_BANK_UNFREEZE) {
                CyberBankFreezeUnfreezeScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.TRADE_LICENSE_SHOP_ACT) {
                TradeLicenceShopActScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.RERA_HOMEBUYER_DISPUTE) {
                RERAHomebuyerDisputeScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.IPR_TRADEMARK_COPYRIGHT) {
                IntellectualPropertyCopyrightScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.ARBITRATION_CLAUSE_DRAFT) {
                ArbitrationClauseDraftingScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.LOK_ADALAT_PETITION) {
                LokAdalatApplicationScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.WILL_PROBATE_SUCCESSION) {
                WillProbateSuccessionScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.MEDICAL_NEGLIGENC_CLAIM) {
                MedicalNegligenceClaimScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.INSURANCE_OMBUDSMAN_APPEAL) {
                InsuranceClaimRejectionScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.TENANT_EVICTION_DEFENSE) {
                TenantEvictionDefenseScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.DEFAMATION_NOTICE_WIZARD) {
                DefamationNoticeWizardScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.PASSPORT_IMPOUND_APPEAL) {
                PassportImpoundmentAppealScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.VENDOR_RECOVERY_SUIT) {
                ContractorVendorRecoveryScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.ENVIRONMENTAL_NGT_PORTAL) {
                EnvironmentalPollutionNGTScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.SIM_FRAUD_TAFCOP) {
                IdentityTheftSIMFraudScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.ADOPTION_CARA_PROCESS) {
                AdoptionLegalProcessScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(JustraDestinations.LITIGATION_COSTS_ESTIMATOR) {
                LegalCostsTaxationEstimatorScreen(
                    currentLanguage = language,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        if (isReauthRequired && hasAcceptedConsent && isAuthenticated) {
            BiometricReauthModal(
                currentLanguage = language,
                onUnlockWithBiometric = { viewModel.completeReauthentication() },
                onVerifyPin = { pin -> viewModel.verifyPinForReauth(pin) }
            )
        }
    }
}

@Deprecated("Use JustraNavGraph instead", ReplaceWith("JustraNavGraph(navController, viewModel, modifier)"))
@Composable
fun NyayaMateNavGraph(
    navController: NavHostController,
    viewModel: JustraViewModel,
    modifier: Modifier = Modifier
) {
    JustraNavGraph(
        navController = navController,
        viewModel = viewModel,
        modifier = modifier
    )
}
