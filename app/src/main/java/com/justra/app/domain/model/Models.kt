package com.justra.app.domain.model

import java.util.UUID

enum class LanguagePreference(val code: String, val displayName: String, val tamilName: String) {
    ENGLISH("en_IN", "English", "English"),
    TAMIL("ta_IN", "தமிழ்", "Tamil"),
    HINDI("hi_IN", "हिन्दी", "Hindi"),
    TELUGU("te_IN", "తెలుగు", "Telugu"),
    MALAYALAM("ml_IN", "മലയാളം", "Malayalam"),
    KANNADA("kn_IN", "ಕನ್ನಡ", "Kannada")
}

enum class DisputeCategory(
    val id: String,
    val titleEn: String,
    val titleTa: String,
    val iconName: String,
    val descriptionEn: String,
    val descriptionTa: String,
    val relevantAct: String
) {
    CONSUMER_GRIEVANCE(
        "consumer",
        "Consumer Grievance",
        "நுகர்வோர் குறைதீர்ப்பு",
        "ShoppingBag",
        "Defective goods, refund denials, unfair trade practices",
        "குறைபாடுள்ள பொருட்கள், பணத்தைத் திரும்பப் பெற மறுப்பது",
        "Consumer Protection Act, 2019 (Sec 35)"
    ),
    CYBER_FINANCIAL_FRAUD(
        "cyber",
        "Cyber & Financial Fraud",
        "சைபர் மற்றும் நிதி மோசடி",
        "Security",
        "UPI fraud, phishing, unauthorized deductions, identity theft",
        "UPI மோசடி, ஃபிஷிங், அங்கீகரிக்கப்படாத பணப் பரிவர்த்தனை",
        "Information Technology Act, 2000 (Sec 66D) & BNS"
    ),
    TENANCY_RENT(
        "tenancy",
        "Tenancy & Rent Disputes",
        "வாடகை மற்றும் குத்தகை சிக்கல்",
        "Home",
        "Security deposit withholding, unlawful eviction, maintenance",
        "முன்பணம் திரும்ப தராமை, சட்டவிரோத வெளியேற்றம்",
        "Tenancy Act & Transfer of Property Act"
    ),
    EMPLOYMENT_SALARY(
        "employment",
        "Employment & Salary",
        "வேலை மற்றும் சம்பளப் பிரச்சினை",
        "Work",
        "Unpaid wages, wrongful termination, gratuity, PF delay",
        "சம்பள பாக்கி, முறையற்ற பணிநீக்கம், PF தாமதம்",
        "Payment of Wages Act & Industrial Disputes Act"
    ),
    WOMEN_RIGHTS(
        "women_rights",
        "Women's Statutory Rights",
        "பெண்கள் சட்ட உரிமைகள்",
        "Shield",
        "Workplace harassment (PoSH), domestic protection, maintenance",
        "பணியிடப் பாதுகாப்பு (PoSH), குடும்ப வன்முறை பாதுகாப்பு",
        "Protection of Women from Domestic Violence Act, 2005"
    ),
    LAND_PROPERTY(
        "land_property",
        "Land & Property",
        "நிலம் மற்றும் சொத்து சர்ச்சை",
        "Landscape",
        "Encroachment, title dispute, registration delays, patta",
        "ஆக்கிரமிப்பு, பட்டா மாறுதல் தாமதம், சொத்து சர்ச்சை",
        "Tamil Nadu Patta Passbook Act & Specific Relief Act"
    ),
    GOVT_RTI(
        "govt_rti",
        "Government Services & RTI",
        "அரசு சேவைகள் மற்றும் RTI",
        "AccountBalance",
        "Delayed certificates, public grievances, RTI application filing",
        "தாமதமான சான்றிதழ்கள், தகவல் அறியும் உரிமை சட்டம் (RTI)",
        "Right to Information Act, 2005"
    ),
    SCAM_ANALYSIS(
        "scam_analysis",
        "Scam & Suspicious Analysis",
        "சந்தேகத்திற்குரிய செய்தி சோதனை",
        "Warning",
        "Immediate scan for APK links, threat sms, blackmail, OTP scams",
        "போலி குறுஞ்செய்திகள், மோசடி இணைப்புகள் பகுப்பாய்வு",
        "National Cyber Crime Reporting Portal (1930 Relay)"
    )
}

enum class RiskLevel(val labelEn: String, val labelTa: String) {
    NONE("No Immediate Risk", "ஆபத்து இல்லை"),
    LOW("Low Risk", "குறைந்த ஆபத்து"),
    MEDIUM("Suspicious / Medium Risk", "சந்தேகத்திற்குரியது / நடுத்தர ஆபத்து"),
    HIGH("High Risk Alert", "அதிக ஆபத்து எச்சரிக்கை"),
    CRITICAL("Critical Threat / Financial Scam", "முக்கிய அச்சுறுத்தல் / நிதி மோசடி")
}

enum class EvidenceCategory(val labelEn: String, val labelTa: String) {
    PAYMENT_PROOF("Payment Proof (UPI / Bank / Receipt)", "பணப் பரிவர்த்தனை சான்று"),
    WRITTEN_COMMUNICATION("Written Communication (Email / Chat / SMS)", "எழுத்துப்பூர்வ உரையாடல்"),
    CONTRACTUAL_AGREEMENT("Contract / Agreement / Invoice", "ஒப்பந்தம் / ரசீது"),
    DAMAGE_PHOTO("Photo / Video Evidence", "புகைப்பட / வீடியோ சான்று"),
    IDENTITY_DOCUMENT("Identity / Official Proof", "அடையாள / அதிகாரப்பூர்வ ஆவணம்"),
    OTHER("Other Evidentiary Artifact", "பிற ஆவணங்கள்")
}

enum class ReadinessMetric(val titleEn: String, val titleTa: String, val weight: Int) {
    FACTUAL_CHRONOLOGY("Factual Incident Chronology Documented", "சம்பவங்களின் வரிசை ஆவணப்படுத்தப்பட்டுள்ளது", 20),
    PAYMENT_OR_LOSS_PROOF("Transaction/Loss Proof Verified", "பரிவர்த்தனை/இழப்பு சான்று சரிபார்க்கப்பட்டது", 25),
    OPPOSING_PARTY_IDENTIFIED("Opposing Party & Contact Identified", "எதிர் தரப்பினர் விவரம் குறிப்பிடப்பட்டுள்ளது", 20),
    FORMAL_NOTICE_SENT("Prior Formal Grievance/Notice Issued", "முந்தைய முறையீடு/நோட்டீஸ் அனுப்பப்பட்டது", 15),
    STATUTORY_JURISDICTION_MAPPED("Statutory Jurisdiction & Forum Mapped", "சட்டப்பிரிவு & விசாரணை அமைப்பு கண்டறியப்பட்டது", 20)
}

enum class SenderRole {
    USER, ASSISTANT, SYSTEM
}

enum class AudioRecordingState {
    IDLE, RECORDING, TRANSCRIBING, PROCESSING
}

enum class UserRole(
    val id: String,
    val titleEn: String,
    val titleTa: String,
    val subtitleEn: String,
    val subtitleTa: String,
    val badgeEn: String,
    val badgeTa: String
) {
    CITIZEN(
        id = "citizen",
        titleEn = "Citizen Complainant",
        titleTa = "பொதுக் குடிமக்கள் / புகார்தாரர்",
        subtitleEn = "Consumer disputes, tenancy, salary arrears, domestic rights",
        subtitleTa = "நுகர்வோர் குறை, வாடகை, சம்பள பாக்கி, குடும்ப நலன்",
        badgeEn = "Citizen Grievance",
        badgeTa = "குடிமக்கள் மனு"
    ),
    LEGAL_COUNSEL(
        id = "counsel",
        titleEn = "Advocate & Legal Counsel",
        titleTa = "வழக்கறிஞர் & சட்ட ஆலோசகர்",
        subtitleEn = "Brief verification, Sec 65B hash audit, 72h handoff tokens",
        subtitleTa = "சான்றுகள் தணிக்கை, பிரிவு 65B சான்றிதழ், கிளையன்ட் குறிப்புகள்",
        badgeEn = "Legal Counsel",
        badgeTa = "வழக்கறிஞர்"
    ),
    MSME_BUSINESS(
        id = "msme",
        titleEn = "MSME & Business Owner",
        titleTa = "சிறு குறு தொழில் & வர்த்தகர்",
        subtitleEn = "Delayed payments, Sec 138 NI Act, commercial contract breach",
        subtitleTa = "தாமதமான பணப்பட்டுவாடா, காசோலை சர்ச்சை, வணிக ஒப்பந்தம்",
        badgeEn = "MSME Recovery",
        badgeTa = "வணிக மீட்பு"
    ),
    CYBER_FRAUD_VICTIM(
        id = "cyber_victim",
        titleEn = "Cyber & Financial Fraud Defense",
        titleTa = "சைபர் & நிதி மோசடி தற்காப்பு",
        subtitleEn = "Golden hour 1930 freeze, phishing forensics, cyber cell relay",
        subtitleTa = "1930 உடனடி முடக்கம், ஃபிஷிங் ஆய்வு, சைபர் கிரைம் புகார்",
        badgeEn = "Emergency Defense",
        badgeTa = "அவசர தற்காப்பு"
    )
}

enum class NotificationType(val labelEn: String, val labelTa: String) {
    ALL("All Notifications", "அனைத்து அறிவிப்புகள்"),
    DEADLINE("Limitation & Deadlines", "காலக்கெடு & வரம்புகள்"),
    CASE_STATUS("Case Progress Updates", "வழக்கு முன்னேற்ற நிலை"),
    SYSTEM_ALERT("System & Fraud Alerts", "கணினி & மோசடி எச்சரிக்கை"),
    UNREAD("Unread", "படிக்காதவை")
}

data class InAppNotificationItem(
    val id: String,
    val titleEn: String,
    val titleTa: String,
    val messageEn: String,
    val messageTa: String,
    val category: String,
    val targetRoute: String,
    val isUrgent: Boolean = false,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType = NotificationType.SYSTEM_ALERT,
    val deadlineDaysRemaining: Int? = null,
    val statutoryAct: String? = null,
    val actionLabelEn: String? = "Take Action",
    val actionLabelTa: String? = "நடவடிக்கை எடுக்க"
)

