package com.justra.app.ui.navigation

/**
 * Sealed Route Registry for JusTra application navigation.
 * Provides type-safe route strings and parameterized route builder helpers.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Landing : Screen("landing")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object VoiceIntake : Screen("voice_intake")
    data object ChatAndVoice : Screen("chat")
    data object MyCasesHub : Screen("my_cases_hub")
    data object StatutoryToolsHub : Screen("statutory_tools_hub")
    data object AccountSettingsHub : Screen("account_settings_hub")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object NewGrievance : Screen("new_grievance")
    data object SmartComplaint : Screen("smart_complaint/{caseId}") {
        fun createRoute(caseId: String): String = "smart_complaint/$caseId"
    }
    data object MactCalculator : Screen("motor_accident_mact")
    data object LimitationReminders : Screen("limitation_reminders")
    data object ActionNavigator : Screen("action_navigator/{caseId}") {
        fun createRoute(caseId: String): String = "action_navigator/$caseId"
    }
    data object EvidenceVault : Screen("evidence_vault/{caseId}") {
        fun createRoute(caseId: String): String = "evidence_vault/$caseId"
    }
    data object Timeline : Screen("timeline_readiness/{caseId}") {
        fun createRoute(caseId: String): String = "timeline_readiness/$caseId"
    }
    data object ScamChecker : Screen("scam_checker")
    data object LegalProblemIntake : Screen("legal_problem_intake")
    data object CounselHandoff : Screen("counsel_handoff/{caseId}") {
        fun createRoute(caseId: String): String = "counsel_handoff/$caseId"
    }
    data object SubTopicDetail : Screen("sub_topic_detail/{categoryKey}") {
        fun createRoute(categoryKey: String): String = "sub_topic_detail/$categoryKey"
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
    const val Settings = "settings"
    const val Profile = "profile"
    const val AccountSettings = "account_settings"
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
