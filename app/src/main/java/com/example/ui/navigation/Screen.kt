package com.example.ui.navigation

/**
 * Sealed Route Registry for Pocket Lawyer (Justra) application navigation.
 * Provides type-safe route strings and parameterized route builder helpers.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Landing : Screen("landing")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object VoiceComplaint : Screen("voice_complaint_screen")
    data object VoiceIntake : Screen("voice_intake_screen")
    data object ChatAndVoice : Screen("chat_and_voice_screen")
    data object MyCasesHub : Screen("my_cases_hub")
    data object StatutoryToolsHub : Screen("statutory_tools_hub")
    data object AccountSettingsHub : Screen("account_settings_hub")
    data object NewGrievance : Screen("new_grievance")
    data object SmartComplaint : Screen("smart_complaint/{caseId}") {
        fun createRoute(caseId: String): String = "smart_complaint/"
    }
    data object MactCalculator : Screen("motor_accident_mact")
    data object LimitationReminders : Screen("limitation_reminders")
    data object ActionNavigator : Screen("action_navigator_screen/{caseId}") {
        fun createRoute(caseId: String): String = "action_navigator_screen/"
    }
    data object EvidenceVault : Screen("evidence_vault_screen/{caseId}") {
        fun createRoute(caseId: String): String = "evidence_vault_screen/"
    }
    data object Timeline : Screen("timeline_screen/{caseId}") {
        fun createRoute(caseId: String): String = "timeline_screen/"
    }
    data object ScamChecker : Screen("scam_checker_screen")
    data object LegalProblemIntake : Screen("legal_problem_intake_screen")
    data object CounselHandoff : Screen("counsel_handoff_screen/{caseId}") {
        fun createRoute(caseId: String): String = "counsel_handoff_screen/"
    }
    data object SubTopicDetail : Screen("sub_topic_detail/{categoryKey}") {
        fun createRoute(categoryKey: String): String = "sub_topic_detail/"
    }
}

object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Landing = "landing"
    const val Auth = "auth"
    const val MainDashboard = "home"
    const val AiIntake = "chat"
    const val ScamChecker = "scam_checker"
    const val EvidenceVault = "evidence_vault"
    const val ActionSteps = "action_navigator"
    const val SmartComplaint = "smart_complaint"
    const val TimelineReadiness = "timeline_readiness"
    const val CounselHandoff = "counsel_handoff"
    const val SecuritySettings = "security_settings"
    const val LanguageSelect = "language_consent"
    const val CourtFeeCalculator = "court_fee_calculator"
    const val EvidenceHashAudit = "section_65b_certificate"
    const val Settings = "account_settings"
    const val LimitationReminders = "notifications_center"
    const val RtiWizard = "rti_drafting_wizard"
    const val DemandNoticeGenerator = "legal_notice_composer"
    const val BnsIpcTransition = "bns_ipc_transition"
    const val NalsaAidInfo = "nalsa_free_legal_aid"
    const val MactCalculator = "motor_accident_mact"
    const val ConsumerPortalInfo = "consumer_mediation"
    const val PoshRights = "women_domestic_rights"
    const val CyberDossier = "cyber_crime_dossier"
    const val NewGrievance = "new_grievance"
}
