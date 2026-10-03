package com.example.ui.navigation

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
import com.example.domain.model.LanguagePreference
import com.example.ui.components.BiometricReauthModal
import com.example.ui.screens.AccountSettingsHubScreen
import com.example.ui.screens.ActionNavigatorScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BiometricAuthScreen
import com.example.ui.screens.BnsIpcTransitionScreen
import com.example.ui.screens.ChatAssistantScreen
import com.example.ui.screens.ComplaintGeneratorScreen
import com.example.ui.screens.ChatAndVoiceScreen
import com.example.ui.screens.ConsumerMediationScreen
import com.example.ui.screens.CounselHandoffScreen
import com.example.ui.screens.CourtFeeCalculatorScreen
import com.example.ui.screens.CyberCrimeDossierScreen
import com.example.ui.screens.EvidenceVaultScreen
import com.example.ui.screens.GlobalJurisdictionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.LanguageConsentScreen
import com.example.ui.screens.LegalNoticeDisputeComposerScreen
import com.example.ui.screens.LimitationRemindersScreen
import com.example.ui.screens.MotorAccidentMactScreen
import com.example.ui.screens.MyCasesHubScreen
import com.example.ui.screens.NalsaFreeLegalAidScreen
import com.example.ui.screens.NewGrievanceScreen
import com.example.ui.screens.NotificationsRemindersCenterScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PinAuthScreen
import com.example.ui.screens.RtiDraftingWizardScreen
import com.example.ui.screens.ScamCheckerScreen
import com.example.ui.screens.Section65BCertificateGeneratorScreen
import com.example.ui.screens.SmartComplaintScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatutoryToolsHubScreen
import com.example.ui.screens.TimelineReadinessScreen
import com.example.ui.screens.WomenDomesticRightsScreen
import com.example.ui.screens.WorldWideLawScreen
import com.example.ui.screens.citizenship.CitizenshipLawsScreen
import com.example.ui.screens.citizenship.LegalTopicDetailScreen
import com.example.ui.screens.complaint.VoiceComplaintRegistrationScreen
import com.example.ui.screens.complaint.VoiceComplaintScreen
import com.example.ui.screens.subtopics.SubTopicDetailScreen
import com.example.ui.viewmodel.NyayaMateViewModel

object JustraDestinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
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
    const val VOICE_COMPLAINT = "voice_complaint"
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
    const val VOICE_COMPLAINT_REGISTRATION = "voice_complaint_registration"
    const val CITIZENSHIP_LAWS = "citizenship_laws"
    const val LEGAL_TOPIC_DETAIL = "legal_topic_detail/{topicId}"
    const val VOICE_INTAKE = "voice_intake"
}

val NyayaMateDestinations = JustraDestinations

@Composable
fun JustraNavGraph(
    navController: NavHostController,
    viewModel: NyayaMateViewModel,
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
                        navController.navigate(JustraDestinations.LANDING) {
                            popUpTo(JustraDestinations.ONBOARDING) { inclusive = true }
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
                    onDialEmergency1930 = { com.example.util.ActionUtils.dialEmergencyHelpline(context, "1930") },
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
                    onLockApp = { viewModel.lockApp() },
                    onSwitchRole = { newRole -> viewModel.setUserRole(newRole) },
                    onNavigateToRoute = { route -> navController.navigate(route) },
                    onNavigateToChat = { navController.navigate("chat") },
                    onNavigateToCaseDetail = { caseId -> navController.navigate("action_navigator/") },
                    onNavigateToActionNavigator = { caseId -> navController.navigate("action_navigator/") },
                    onNavigateToComplaint = { caseId -> navController.navigate("complaint_generator/") },
                    onNavigateToEvidenceVault = { caseId -> navController.navigate("evidence_vault/") },
                    onNavigateToTimeline = { caseId -> navController.navigate("timeline_readiness/") },
                    onNavigateToScamChecker = { navController.navigate(JustraDestinations.SCAM_CHECKER) },
                    onNavigateToCounselHandoff = { caseId -> navController.navigate("counsel_handoff/") },
                    onCreateNewDispute = { _, _, _, _, _, _, _, _ -> navController.navigate(JustraDestinations.NEW_GRIEVANCE) },
                    onDialHelpline = { number -> com.example.util.ActionUtils.dialEmergencyHelpline(context, number) }
                )
            }

            composable(JustraDestinations.NEW_GRIEVANCE) {
                NewGrievanceScreen(
                    viewModel = viewModel,
                    currentLanguage = language,
                    onBackClick = { navController.popBackStack() },
                    onCreated = { caseId ->
                        navController.navigate("smart_complaint/")
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

                ChatAssistantScreen(
                    currentLanguage = language,
                    messages = chatMessages,
                    recordingState = recordingState,
                    audioWaveforms = audioWaveforms,
                    initialPrompt = prompt,
                    onSendMessage = { text -> viewModel.sendChatMessage(text) },
                    onStartRecording = { viewModel.startVoiceRecording() },
                    onStopRecording = { viewModel.stopVoiceRecordingAndSubmit() },
                    onAddProofToVault = { key, value -> viewModel.addProofFromExtractedChip(key, value) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
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
                    onDialHelpline = { number -> com.example.util.ActionUtils.dialEmergencyHelpline(context, number) },
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
                val stepsFlow: kotlinx.coroutines.flow.Flow<List<com.example.data.local.ActionStepEntity>> = remember(caseId) { viewModel.getStepsForCase(caseId) }
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
                val artifactsFlow: kotlinx.coroutines.flow.Flow<List<com.example.data.local.EvidenceArtifactEntity>> = remember(caseId) { viewModel.getArtifactsForCase(caseId) }
                EvidenceVaultScreen(
                    currentLanguage = language,
                    caseEntity = caseEntity,
                    artifactsFlow = artifactsFlow,
                    onAddArtifact = { name, category, notes -> viewModel.addEvidenceArtifact(caseId, name, category, notes) },
                    onDeleteArtifact = { artifactId -> viewModel.deleteArtifactById(artifactId) },
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(JustraDestinations.TIMELINE_READINESS) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                val caseEntity by viewModel.getCaseStream(caseId).collectAsState(initial = null)
                val eventsFlow: kotlinx.coroutines.flow.Flow<List<com.example.data.local.TimelineEventEntity>> = remember(caseId) { viewModel.getEventsForCase(caseId) }
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
                val tokensFlow: kotlinx.coroutines.flow.Flow<List<com.example.data.local.ShareTokenEntity>> = remember(caseId) { viewModel.getTokensForCase(caseId) }
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

            composable(JustraDestinations.VOICE_COMPLAINT_REGISTRATION) {
                VoiceComplaintRegistrationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCaseCreated = { caseId ->
                        navController.navigate("action_navigator/") {
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
                        navController.navigate("timeline_readiness/")
                    },
                    onNavigateToEvidenceVault = {
                        val fallbackCaseId = allCases.firstOrNull()?.caseId ?: "new"
                        navController.navigate("evidence_vault/")
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
                        navController.navigate("timeline_readiness/")
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
                        navController.navigate(Screen.VoiceComplaint.route)
                    }
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

@Composable
fun NyayaMateNavGraph(
    navController: NavHostController,
    viewModel: NyayaMateViewModel,
    modifier: Modifier = Modifier
) {
    JustraNavGraph(
        navController = navController,
        viewModel = viewModel,
        modifier = modifier
    )
}
