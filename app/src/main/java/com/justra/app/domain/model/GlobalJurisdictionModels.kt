package com.justra.app.domain.model

enum class LegalSystemType(
    val titleEn: String,
    val titleTa: String,
    val descriptionEn: String,
    val descriptionTa: String
) {
    COMMON_LAW(
        titleEn = "Common Law System",
        titleTa = "பொது சட்டக் கட்டமைப்பு (Common Law)",
        descriptionEn = "Adversarial system based on judicial precedents, case laws, discovery, and binding statutory interpretations.",
        descriptionTa = "நீதிமன்ற முன்மாதிரிகள், சட்ட முன்முடிவுகள் (Precedents) மற்றும் குறுக்கு விசாரணை அடிப்படையிலான பொது சட்ட முறைமை."
    ),
    CIVIL_LAW(
        titleEn = "Civil Law / Inquisitorial System",
        titleTa = "சிவில் சட்டக் கட்டமைப்பு (Civil Law)",
        descriptionEn = "Inquisitorial system based on comprehensive codified statutes (Napoleonic / BGB / Six Codes) with judge-led investigation.",
        descriptionTa = "விரிவான சட்டக் குறியீடுகள் (Codes) மற்றும் நீதிபதியே நேரடியாக விசாரிக்கும் விசாரணை முறைமை."
    ),
    MIXED_SHARIA_COMMERCIAL(
        titleEn = "Mixed Sharia & Statutory Commercial Law",
        titleTa = "ஷரியத் & வணிக சட்டக் கட்டமைப்பு (Mixed / Sharia)",
        descriptionEn = "Dual framework combining Islamic jurisprudence (family/civil) with modern statutory commercial codes and English Common Law free zones (DIFC/ADGM).",
        descriptionTa = "குடும்ப/தனிநபர் சட்டங்களுக்கு இஸ்லாமிய ஷரியத் மற்றும் வணிக/முதலீட்டு சட்டங்களுக்கு சர்வதேச பொதுச் சட்டம் இணைந்த கட்டமைப்பு."
    ),
    SOCIALIST_CIVIL(
        titleEn = "Socialist Civil Law System",
        titleTa = "சோசலிச சிவில் சட்டக் கட்டமைப்பு (Socialist Civil)",
        descriptionEn = "Codified civil law system structured under state socialist legal principles and people's tribunals.",
        descriptionTa = "அரசு வழிகாட்டுதல் மற்றும் மக்கள் தீர்ப்பாயங்கள் சார்ந்த குறியீட்டு சட்டக் கட்டமைப்பு."
    )
}

enum class JurisdictionPhase(
    val phaseNumber: Int,
    val titleEn: String,
    val titleTa: String,
    val subtitleEn: String,
    val subtitleTa: String,
    val badgeColor: Long
) {
    PHASE_1(
        phaseNumber = 1,
        titleEn = "Phase 1: Common Law & Tamil Diaspora Hub",
        titleTa = "Phase 1: முதற்கட்டம் - பொது சட்டம் & தமிழ் டயஸ்போரா",
        subtitleEn = "India, USA, UK, Australia, Canada, Singapore with English & Tamil support.",
        subtitleTa = "இந்தியா, அமெரிக்கா, இங்கிலாந்து, ஆஸ்திரேலியா, கனடா, சிங்கப்பூர் (ஆங்கிலம் + தமிழ்).",
        badgeColor = 0xFF1B5E20
    ),
    PHASE_2(
        phaseNumber = 2,
        titleEn = "Phase 2: European Expansion",
        titleTa = "Phase 2: ஐரோப்பிய விரிவாக்கம்",
        subtitleEn = "France, Germany with French & German civil law integration.",
        subtitleTa = "பிரான்ஸ், ஜெர்மனி (பிரெஞ்சு, ஜெர்மன் சட்டக் கட்டமைப்பு).",
        badgeColor = 0xFF0D47A1
    ),
    PHASE_3(
        phaseNumber = 3,
        titleEn = "Phase 3: Middle East & Gulf Hub",
        titleTa = "Phase 3: மத்திய கிழக்கு & வளைகுடா மையம்",
        subtitleEn = "UAE, Saudi Arabia with Arabic & labor/diaspora portal integration.",
        subtitleTa = "ஐக்கிய அரபு அமீரகம், சவுதி அரேபியா (அரபு மொழி & தொழிலாளர் போர்ட்டல்கள்).",
        badgeColor = 0xFFE65100
    ),
    UN_P5_STRATEGIC(
        phaseNumber = 4,
        titleEn = "UN Security Council P5 & Global Powers",
        titleTa = "ஐ.நா. பாதுகாப்பு கவுன்சில் P5 & உலகளாவிய வல்லரசுகள்",
        subtitleEn = "USA, UK, France, China, Russia + Japan, Germany, India strategic powers.",
        subtitleTa = "P5 நிரந்தர உறுப்பு நாடுகள் மற்றும் முன்னணி பொருளாதார வல்லரசுகள்.",
        badgeColor = 0xFF4A148C
    )
}

data class UNOfficialLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val tamilName: String,
    val isUNOfficial6: Boolean,
    val usageScopeEn: String,
    val usageScopeTa: String,
    val globalSpeakers: String
)

data class CrossBorderReliefItem(
    val titleEn: String,
    val titleTa: String,
    val detailsEn: String,
    val detailsTa: String,
    val authorityOrPortal: String
)

data class JurisdictionNation(
    val countryCode: String, // e.g. "IN", "US", "GB", "FR", "CN", "RU", "DE", "JP", "CA", "AU", "AE", "SA", "SG"
    val nameEn: String,
    val nameTa: String,
    val nativeName: String,
    val flagEmoji: String,
    val isP5PermanentMember: Boolean,
    val unStatusBadgeEn: String,
    val unStatusBadgeTa: String,
    val primaryLegalLanguagesEn: List<String>,
    val primaryLegalLanguagesTa: List<String>,
    val phase: JurisdictionPhase,
    val legalSystem: LegalSystemType,
    val apexCourtEn: String,
    val apexCourtTa: String,
    val emergencyPoliceNumber: String,
    val cyberHelplineNumber: String,
    val consumerOrLaborHelpline: String,
    val majorActsEn: List<String>,
    val majorActsTa: List<String>,
    val eFilingPortalName: String,
    val eFilingPortalUrl: String,
    val tamilDiasporaSummaryEn: String,
    val tamilDiasporaSummaryTa: String,
    val crossBorderRelief: List<CrossBorderReliefItem>,
    val electronicEvidenceStandardEn: String,
    val electronicEvidenceStandardTa: String
)

object GlobalJurisdictionRepository {

    val UN_6_OFFICIAL_LANGUAGES: List<UNOfficialLanguage> = listOf(
        UNOfficialLanguage(
            code = "en",
            englishName = "English",
            nativeName = "English",
            tamilName = "ஆங்கிலம்",
            isUNOfficial6 = true,
            usageScopeEn = "Global common law, international commerce, diplomacy, and maritime jurisdiction.",
            usageScopeTa = "உலகளாவிய பொது சட்டம், சர்வதேச வணிகம், தூதரகம் மற்றும் கடல்சார் எல்லைகள்.",
            globalSpeakers = "1.5 Billion+"
        ),
        UNOfficialLanguage(
            code = "fr",
            englishName = "French",
            nativeName = "Français",
            tamilName = "பிரெஞ்சு",
            isUNOfficial6 = true,
            usageScopeEn = "International treaties, diplomatic protocols, Civil Law jurisprudence, and ICJ official proceedings.",
            usageScopeTa = "சர்வதேச உடன்படிக்கைகள், தூதரக விதிமுறைகள், சிவில் சட்டம் மற்றும் சர்வதேச நீதிமன்ற (ICJ) நடைமுறைகள்.",
            globalSpeakers = "320 Million+"
        ),
        UNOfficialLanguage(
            code = "es",
            englishName = "Spanish",
            nativeName = "Español",
            tamilName = "ஸ்பானிஷ்",
            isUNOfficial6 = true,
            usageScopeEn = "Latin America, Spain, UN general assemblies, and inter-American human rights treaties.",
            usageScopeTa = "லத்தீன் அமெரிக்கா, ஸ்பெயின், ஐ.நா. பொதுச்சபை மற்றும் மனித உரிமைகள் உடன்படிக்கைகள்.",
            globalSpeakers = "550 Million+"
        ),
        UNOfficialLanguage(
            code = "zh",
            englishName = "Mandarin Chinese",
            nativeName = "中文 (普通话)",
            tamilName = "மாண்டரின் சீனம்",
            isUNOfficial6 = true,
            usageScopeEn = "East Asia, UN Security Council official records, and international trade disputes.",
            usageScopeTa = "கிழக்காசியா, ஐ.நா. பாதுகாப்பு கவுன்சில் உத்தியோகபூர்வ பதிவுகள் மற்றும் வர்த்தக சர்ச்சைகள்.",
            globalSpeakers = "1.1 Billion+"
        ),
        UNOfficialLanguage(
            code = "ru",
            englishName = "Russian",
            nativeName = "Русский язык",
            tamilName = "ரஷ்யன்",
            isUNOfficial6 = true,
            usageScopeEn = "Eurasian economic treaties, UN Security Council records, and Eastern European legal systems.",
            usageScopeTa = "யூரேசிய வர்த்தக ஒப்பந்தங்கள், ஐ.நா. பாதுகாப்பு கவுன்சில் ஆவணங்கள் மற்றும் கிழக்கு ஐரோப்பிய சட்டம்.",
            globalSpeakers = "250 Million+"
        ),
        UNOfficialLanguage(
            code = "ar",
            englishName = "Arabic",
            nativeName = "العربية",
            tamilName = "அரபு",
            isUNOfficial6 = true,
            usageScopeEn = "Middle East and North Africa (22 sovereign states), Arab League, and GCC trade dispute frameworks.",
            usageScopeTa = "மத்திய கிழக்கு மற்றும் வட ஆப்பிரிக்கா (22 நாடுகள்), அரபு லீக் மற்றும் GCC வர்த்தக தீர்ப்பாயங்கள்.",
            globalSpeakers = "420 Million+"
        ),
        UNOfficialLanguage(
            code = "ta",
            englishName = "Tamil",
            nativeName = "தமிழ்",
            tamilName = "தமிழ்",
            isUNOfficial6 = false,
            usageScopeEn = "Official language in India, Singapore, Sri Lanka; recognized diaspora language in Malaysia, UK, Canada, Australia, UAE.",
            usageScopeTa = "இந்தியா, சிங்கப்பூர், இலங்கையின் அதிகாரப்பூர்வ மொழி; மலேசியா, இங்கிலாந்து, கனடா, ஆஸ்திரேலியா, வளைகுடா நாடுகளில் அங்கீகரிக்கப்பட்ட மொழி.",
            globalSpeakers = "88 Million+"
        ),
        UNOfficialLanguage(
            code = "de",
            englishName = "German",
            nativeName = "Deutsch",
            tamilName = "ஜெர்மன்",
            isUNOfficial6 = false,
            usageScopeEn = "European Union leading economic language, Civil Code (BGB) jurisprudence, and European Patent Office.",
            usageScopeTa = "ஐரோப்பிய ஒன்றியத்தின் முதன்மை பொருளாதார மொழி, BGB சிவில் சட்டம் மற்றும் ஐரோப்பிய காப்புரிமை அமைப்பு.",
            globalSpeakers = "130 Million+"
        ),
        UNOfficialLanguage(
            code = "ja",
            englishName = "Japanese",
            nativeName = "日本語",
            tamilName = "ஜப்பானியம்",
            isUNOfficial6 = false,
            usageScopeEn = "Six Codes civil framework, advanced technology intellectual property, and East Asian maritime commerce.",
            usageScopeTa = "ஆறு குறியீடுகள் (Six Codes) சட்டம், தொழில்நுட்ப அறிவுசார் சொத்துரிமை மற்றும் வர்த்தகம்.",
            globalSpeakers = "125 Million+"
        ),
        UNOfficialLanguage(
            code = "hi",
            englishName = "Hindi",
            nativeName = "हिन्दी",
            tamilName = "இந்தி",
            isUNOfficial6 = false,
            usageScopeEn = "Official language of the Union of India, widely used in South Asian diaspora, consular proceedings.",
            usageScopeTa = "இந்திய ஒன்றியத்தின் அதிகாரப்பூர்வ மொழி, தெற்காசிய டயஸ்போரா மற்றும் தூதரக நடைமுறைகள்.",
            globalSpeakers = "600 Million+"
        )
    )

    val ALL_NATIONS: List<JurisdictionNation> = listOf(
        // 1. INDIA (Phase 1 / Major Global Power)
        JurisdictionNation(
            countryCode = "IN",
            nameEn = "India",
            nameTa = "இந்தியா",
            nativeName = "भारत / இந்தியா",
            flagEmoji = "🇮🇳",
            isP5PermanentMember = false,
            unStatusBadgeEn = "Major Global Power / G20 Asian Hub",
            unStatusBadgeTa = "5-வது பெரிய பொருளாதாரம் / G20 மையம்",
            primaryLegalLanguagesEn = listOf("English (Statutory/Courts)", "Hindi (Official)", "Tamil (State)"),
            primaryLegalLanguagesTa = listOf("ஆங்கிலம் (நீதிமன்ற நடைமுறை)", "இந்தி", "தமிழ்"),
            phase = JurisdictionPhase.PHASE_1,
            legalSystem = LegalSystemType.COMMON_LAW,
            apexCourtEn = "Supreme Court of India (New Delhi)",
            apexCourtTa = "இந்திய உச்ச நீதிமன்றம் (புது தில்லி)",
            emergencyPoliceNumber = "112",
            cyberHelplineNumber = "1930",
            consumerOrLaborHelpline = "1915",
            majorActsEn = listOf(
                "Bharatiya Nyaya Sanhita (BNS) 2023",
                "Bharatiya Nagarik Suraksha Sanhita (BNSS) 2023",
                "Bharatiya Sakshya Adhiniyam (BSA) 2023",
                "Consumer Protection Act 2019",
                "Information Technology Act 2000"
            ),
            majorActsTa = listOf(
                "பாரதிய நியாய சன்ஹிதா (BNS) 2023",
                "பாரதிய குடிமக்கள் பாதுகாப்பு சட்டம் (BNSS) 2023",
                "பாரதிய சாட்சிய சட்டம் (BSA) 2023",
                "நுகர்வோர் பாதுகாப்புச் சட்டம் 2019",
                "தகவல் தொழில்நுட்பச் சட்டம் 2000"
            ),
            eFilingPortalName = "e-Courts Services & e-Daakhil",
            eFilingPortalUrl = "https://services.ecourts.gov.in",
            tamilDiasporaSummaryEn = "Home jurisdiction for Tamil Nadu & Puducherry; extensive NALSA legal aid and state High Court benches in Chennai and Madurai.",
            tamilDiasporaSummaryTa = "தமிழ்நாடு மற்றும் புதுச்சேரிக்கான தாய் சட்ட எல்லை; சென்னை மற்றும் மதுரை உயர்நீதிமன்ற கிளைகள், NALSA இலவச சட்ட உதவி.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "MADAD Portal for NRI Grievances",
                    titleTa = "வெளிநாடு வாழ் இந்தியர் குறைதீர்க்கும் 'மதத்' போர்ட்டல்",
                    detailsEn = "Consular grievance tracking with Ministry of External Affairs for NRIs across Gulf, Europe, and Americas.",
                    detailsTa = "வெளிநாட்டில் உள்ள இந்தியர்களின் அவசர சட்ட மற்றும் தூதரக உதவிகளுக்கான வெளியுறவு அமைச்சக போர்ட்டல்.",
                    authorityOrPortal = "madad.gov.in"
                ),
                CrossBorderReliefItem(
                    titleEn = "New York Convention 1958 Enforcement",
                    titleTa = "நியூயார்க் உடன்படிக்கை 1958 - நடுவர் தீர்ப்பு அமலாக்கம்",
                    detailsEn = "Enforcement of foreign commercial arbitral awards under Section 44 of Arbitration and Conciliation Act 1996.",
                    detailsTa = "சர்வதேச நடுவர் தீர்ப்புகளை இந்தியாவில் அமல்படுத்தும் சட்ட நடைமுறை.",
                    authorityOrPortal = "Arbitration Act 1996"
                )
            ),
            electronicEvidenceStandardEn = "Section 63 of Bharatiya Sakshya Adhiniyam (BSA 2023) / Section 65B Indian Evidence Act with SHA-256 hash certificate.",
            electronicEvidenceStandardTa = "பாரதிய சாட்சிய சட்டம் பிரிவு 63 (முந்தைய 65B IEA) மற்றும் SHA-256 குறியாக்க சான்றிதழ்."
        ),

        // 2. USA (Phase 1 / P5 Permanent Member)
        JurisdictionNation(
            countryCode = "US",
            nameEn = "United States of America",
            nameTa = "அமெரிக்கா (USA)",
            nativeName = "United States of America",
            flagEmoji = "🇺🇸",
            isP5PermanentMember = true,
            unStatusBadgeEn = "UN Security Council Permanent Member (P5)",
            unStatusBadgeTa = "ஐ.நா. பாதுகாப்பு கவுன்சில் நிரந்தர நாடு (P5)",
            primaryLegalLanguagesEn = listOf("English (Primary Federal)", "Spanish (Widely Recognized)"),
            primaryLegalLanguagesTa = listOf("ஆங்கிலம் (கூட்டாட்சி மொழி)", "ஸ்பானிஷ்"),
            phase = JurisdictionPhase.PHASE_1,
            legalSystem = LegalSystemType.COMMON_LAW,
            apexCourtEn = "Supreme Court of the United States (SCOTUS, Washington D.C.)",
            apexCourtTa = "அமெரிக்க உச்ச நீதிமன்றம் (SCOTUS, வாஷிங்டன்)",
            emergencyPoliceNumber = "911",
            cyberHelplineNumber = "1-800-CALL-FBI / IC3.gov",
            consumerOrLaborHelpline = "1-877-FTC-HELP (382-4357)",
            majorActsEn = listOf(
                "United States Code (U.S.C. Titles 18 & 28)",
                "Federal Rules of Civil Procedure (FRCP)",
                "Federal Trade Commission Act (15 U.S.C.)",
                "Computer Fraud and Abuse Act (CFAA)",
                "Civil Rights Act of 1964"
            ),
            majorActsTa = listOf(
                "அமெரிக்க கூட்டாட்சி சட்டக் குறியீடு (U.S. Code)",
                "கூட்டாட்சி சிவில் நடைமுறை விதிகள் (FRCP)",
                "கூட்டாட்சி வர்த்தக ஆணைய சட்டம் (FTC Act)",
                "கணினி மோசடி மற்றும் முறைகேடு தடுப்பு சட்டம் (CFAA)",
                "குடிமக்கள் உரிமைகள் சட்டம் 1964"
            ),
            eFilingPortalName = "PACER (Public Access to Court Electronic Records)",
            eFilingPortalUrl = "https://pacer.uscourts.gov",
            tamilDiasporaSummaryEn = "Over 450,000 Tamil Americans (Silicon Valley, New Jersey, Texas). Active Tamil Sangams and bilateral consular assistance via Indian Consulates (NY, SF, Chicago, Houston, Atlanta).",
            tamilDiasporaSummaryTa = "4.5 லட்சத்திற்கும் மேற்பட்ட தமிழ் அமெரிக்கர்கள்; சிலிகான் வேலி, நியூஜெர்சி, டெக்சாஸ் தமிழ்ச் சங்கங்கள் மற்றும் இந்திய துணைத் தூதரக சேவைகள்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "IC3 FBI Internet Crime Complaint Center",
                    titleTa = "FBI இன்டர்நெட் குற்றப் புகார் மையம் (IC3)",
                    detailsEn = "Federal filing for wire fraud, BEC scams, cryptocurrency theft, and international extortion.",
                    detailsTa = "வங்கி பண மோசடி, இணைய வழி மிரட்டல் மற்றும் சர்வதேச கிரிப்டோ மோசடிகளுக்கான புகார் மையம்.",
                    authorityOrPortal = "ic3.gov"
                ),
                CrossBorderReliefItem(
                    titleEn = "Hague Convention Apostille & Service",
                    titleTa = "ஹேக் சர்வதேச ஆவண உறுதிப்படுத்தல் (Apostille)",
                    detailsEn = "Authentication of legal affidavits, power of attorney, and birth certificates for cross-border Indian use.",
                    detailsTa = "அமெரிக்காவில் வழங்கப்படும் பவர் ஆஃப் அட்டர்னி மற்றும் பிரமாணப் பத்திரங்களை இந்தியாவில் செல்லுபடியாக்கும் முறை.",
                    authorityOrPortal = "Hague Convention 1961"
                )
            ),
            electronicEvidenceStandardEn = "Federal Rules of Evidence (FRE) Rule 902(13) & (14) for self-authenticating electronic records with cryptographic hash values.",
            electronicEvidenceStandardTa = "கூட்டாட்சி சாட்சிய விதி 902(13) & (14) - டிஜிட்டல் பதிவுகளுக்கான சுய-உறுதிப்படுத்தல் மற்றும் ஹாஷ் சான்றிதழ்."
        ),

        // 3. UNITED KINGDOM (Phase 1 / P5 Permanent Member)
        JurisdictionNation(
            countryCode = "GB",
            nameEn = "United Kingdom",
            nameTa = "இங்கிலாந்து (UK)",
            nativeName = "United Kingdom",
            flagEmoji = "🇬🇧",
            isP5PermanentMember = true,
            unStatusBadgeEn = "UN Security Council Permanent Member (P5)",
            unStatusBadgeTa = "ஐ.நா. பாதுகாப்பு கவுன்சில் நிரந்தர நாடு (P5)",
            primaryLegalLanguagesEn = listOf("English (Sole Official Legal)", "Welsh (Wales)"),
            primaryLegalLanguagesTa = listOf("ஆங்கிலம் (முதன்மை சட்டம்)", "வெல்ஷ்"),
            phase = JurisdictionPhase.PHASE_1,
            legalSystem = LegalSystemType.COMMON_LAW,
            apexCourtEn = "Supreme Court of the United Kingdom (London)",
            apexCourtTa = "இங்கிலாந்து உச்ச நீதிமன்றம் (லண்டன்)",
            emergencyPoliceNumber = "999 / 101 (Non-Emergency)",
            cyberHelplineNumber = "0300 123 2040 (Action Fraud UK)",
            consumerOrLaborHelpline = "0808 223 1133 (Citizens Advice)",
            majorActsEn = listOf(
                "Consumer Rights Act 2015",
                "Employment Rights Act 1996",
                "Data Protection Act 2018 (UK GDPR)",
                "Computer Misuse Act 1990",
                "Police and Criminal Evidence Act 1984 (PACE)"
            ),
            majorActsTa = listOf(
                "நுகர்வோர் உரிமைகள் சட்டம் 2015",
                "வேலைவாய்ப்பு உரிமைகள் சட்டம் 1996",
                "தரவு பாதுகாப்பு சட்டம் 2018 (UK GDPR)",
                "கணினி தவறான பயன்பாட்டு சட்டம் 1990",
                "காவல்துறை மற்றும் குற்றவியல் சாட்சிய சட்டம் 1984 (PACE)"
            ),
            eFilingPortalName = "HM Courts & Tribunals Service (HMCTS) e-Filing",
            eFilingPortalUrl = "https://www.gov.uk/government/organisations/hm-courts-and-tribunals-service",
            tamilDiasporaSummaryEn = "Home to over 200,000 British Tamils (London, Wembley, Tooting, East Ham). Extensive legal advocacy networks, community legal centers, and High Commission of India (London).",
            tamilDiasporaSummaryTa = "2 லட்சத்திற்கும் மேற்பட்ட பிரிட்டிஷ் தமிழர்கள்; லண்டன், வெம்ப்ளி, டூட்டிங் பகுதிகள், இந்திய உயர் ஆணையரக சட்ட ஆதரவு.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "Action Fraud & National Fraud Intelligence Bureau",
                    titleTa = "ஆக்சன் ஃபிராட் (Action Fraud) & நிதி புலனாய்வு",
                    detailsEn = "National reporting center for fraud and cybercrime in the UK with instant Crime Reference Number (CRN).",
                    detailsTa = "இங்கிலாந்தில் நடக்கும் நிதி மோசடிகளுக்கான அதிகாரப்பூர்வ தேசிய புகார் போர்ட்டல்.",
                    authorityOrPortal = "actionfraud.police.uk"
                ),
                CrossBorderReliefItem(
                    titleEn = "ACAS Early Conciliation",
                    titleTa = "ACAS ஆரம்பகால வேலைவாய்ப்பு சமரசம்",
                    detailsEn = "Mandatory dispute resolution step before filing an Employment Tribunal claim for unfair dismissal or unpaid wages.",
                    detailsTa = "தொழிலாளர் தீர்ப்பாயத்திற்கு செல்லும் முன் கட்டாயமாக மேற்கொள்ளப்படும் இலவச சமரச தீர்வு.",
                    authorityOrPortal = "acas.org.uk"
                )
            ),
            electronicEvidenceStandardEn = "Civil Evidence Act 1995 Section 8 and Police and Criminal Evidence Act 1984 Section 69 compliance with metadata integrity.",
            electronicEvidenceStandardTa = "சிவில் சாட்சிய சட்டம் 1995 பிரிவு 8 மற்றும் PACE சட்டம் 1984 பிரிவு 69 - டிஜிட்டல் மெட்டாடேட்டா ஒருமைப்பாடு."
        ),

        // 4. AUSTRALIA (Phase 1 / Commonwealth Power)
        JurisdictionNation(
            countryCode = "AU",
            nameEn = "Australia",
            nameTa = "ஆஸ்திரேலியா",
            nativeName = "Commonwealth of Australia",
            flagEmoji = "🇦🇺",
            isP5PermanentMember = false,
            unStatusBadgeEn = "Commonwealth Leading Power / G20",
            unStatusBadgeTa = "காமன்வெல்த் முன்னணி நாடு / G20",
            primaryLegalLanguagesEn = listOf("English (Official)"),
            primaryLegalLanguagesTa = listOf("ஆங்கிலம்"),
            phase = JurisdictionPhase.PHASE_1,
            legalSystem = LegalSystemType.COMMON_LAW,
            apexCourtEn = "High Court of Australia (Canberra)",
            apexCourtTa = "ஆஸ்திரேலிய உயர் நீதிமன்றம் (கான்பெரா)",
            emergencyPoliceNumber = "000",
            cyberHelplineNumber = "1300 CYBER1 (1300 292 371) / ReportCyber",
            consumerOrLaborHelpline = "1300 302 502 (ACCC / Scamwatch)",
            majorActsEn = listOf(
                "Competition and Consumer Act 2010 (Australian Consumer Law)",
                "Fair Work Act 2009",
                "Privacy Act 1988 (Cth)",
                "Criminal Code Act 1995 (Cth)",
                "Evidence Act 1995 (Cth)"
            ),
            majorActsTa = listOf(
                "போட்டி மற்றும் நுகர்வோர் சட்டம் 2010 (ACL)",
                "நியாயமான வேலை சட்டம் 2009 (Fair Work)",
                "தனியுரிமை சட்டம் 1988",
                "குற்றவியல் சட்டக் குறியீடு 1995",
                "சாட்சிய சட்டம் 1995"
            ),
            eFilingPortalName = "Commonwealth Courts Portal (CCP) & State Tribunals (NCAT/VCAT)",
            eFilingPortalUrl = "https://www.comcourts.gov.au",
            tamilDiasporaSummaryEn = "Over 100,000 Tamil Australians (Sydney, Melbourne, Brisbane). Strong community presence with pro-bono legal services and High Commission of India (Canberra / Sydney).",
            tamilDiasporaSummaryTa = "1 லட்சத்திற்கும் மேற்பட்ட தமிழ் ஆஸ்திரேலியர்கள்; சிட்னி, மெல்போர்ன் தமிழ்ச் சங்கங்கள், NCAT/VCAT தீர்ப்பாயங்கள்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "ACCC Scamwatch & National Anti-Scam Centre",
                    titleTa = "ஸ்கேம்வாட்ச் (Scamwatch) மோசடி தடுப்பு மையம்",
                    detailsEn = "Direct scam reporting and intelligence sharing to prevent cross-border investment and identity theft scams.",
                    detailsTa = "முதலீட்டு மோசடிகள், போலி தொலைபேசி அழைப்புகளை முடக்கும் தேசிய அமைப்பு.",
                    authorityOrPortal = "scamwatch.gov.au"
                ),
                CrossBorderReliefItem(
                    titleEn = "Fair Work Ombudsman Mediation",
                    titleTa = "நியாயமான வேலைவாய்ப்பு நடுவர் சமரசம்",
                    detailsEn = "Free government mediation for wage underpayment, workplace exploitation, and award entitlements.",
                    detailsTa = "சம்பளக் குறைப்பு மற்றும் பணிப் பாதுகாப்பு தொடர்பான இலவச அரசு சமரச உதவி.",
                    authorityOrPortal = "fairwork.gov.au"
                )
            ),
            electronicEvidenceStandardEn = "Evidence Act 1995 (Cth) Section 146 & 147 presumption of machine reliability and cryptographic hash verification.",
            electronicEvidenceStandardTa = "சாட்சிய சட்டம் 1995 பிரிவு 146 & 147 - கணினி நம்பகத்தன்மை மற்றும் ஹாஷ் சரிபார்ப்பு."
        ),

        // 5. CANADA (Phase 1 / G7 Bilingual Power)
        JurisdictionNation(
            countryCode = "CA",
            nameEn = "Canada",
            nameTa = "கனடா",
            nativeName = "Canada",
            flagEmoji = "🇨🇦",
            isP5PermanentMember = false,
            unStatusBadgeEn = "G7 Leading Power / High Diaspora",
            unStatusBadgeTa = "G7 முன்னணி நாடு / அதிக புலம்பெயர்ந்தோர்",
            primaryLegalLanguagesEn = listOf("English (Federal/Provincial)", "French (Bilingual Federal/Quebec)"),
            primaryLegalLanguagesTa = listOf("ஆங்கிலம்", "பிரெஞ்சு (இருமொழிக் கட்டமைப்பு)"),
            phase = JurisdictionPhase.PHASE_1,
            legalSystem = LegalSystemType.COMMON_LAW, // Civil law in Quebec
            apexCourtEn = "Supreme Court of Canada (Cour suprême du Canada, Ottawa)",
            apexCourtTa = "கனடா உச்ச நீதிமன்றம் (ஒட்டாவா)",
            emergencyPoliceNumber = "911",
            cyberHelplineNumber = "1-888-495-8501 (Canadian Anti-Fraud Centre)",
            consumerOrLaborHelpline = "1-800-348-5358 (Competition Bureau Canada)",
            majorActsEn = listOf(
                "Criminal Code (R.S.C., 1985, c. C-46)",
                "Canada Labour Code (R.S.C., 1985, c. L-2)",
                "Personal Information Protection and Electronic Documents Act (PIPEDA)",
                "Canada Evidence Act (R.S.C., 1985, c. C-5)",
                "Consumer Packaging and Labelling Act"
            ),
            majorActsTa = listOf(
                "கனடா குற்றவியல் குறியீடு (Criminal Code)",
                "கனடா தொழிலாளர் சட்டம் (Canada Labour Code)",
                "தனிநபர் தகவல் பாதுகாப்பு மற்றும் மின்னணு ஆவண சட்டம் (PIPEDA)",
                "கனடா சாட்சிய சட்டம் (Canada Evidence Act)",
                "நுகர்வோர் பாதுகாப்பு சட்டம்"
            ),
            eFilingPortalName = "CanLII & Provincial Online Tribunals (e.g. Ontario CRT / LTB)",
            eFilingPortalUrl = "https://www.canlii.org",
            tamilDiasporaSummaryEn = "Over 300,000 Tamil Canadians (Greater Toronto Area, Scarborough, Markham, Montreal). Recognized Tamil Heritage Month (January) and active legal clinics.",
            tamilDiasporaSummaryTa = "3 லட்சத்திற்கும் மேற்பட்ட தமிழ் கனடியர்கள் (டொராண்டோ, ஸ்கார்பாரோ, மார்க்கம்); ஜனவரி தமிழ் மரபுத் திங்கள் அங்கீகாரம்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "Canadian Anti-Fraud Centre (CAFC)",
                    titleTa = "கனடிய மோசடி தடுப்பு மையம் (CAFC)",
                    detailsEn = "Joint RCMP/OPP/Competition Bureau repository for fraud and cybercrime reporting.",
                    detailsTa = "RCMP காவல் துறை மற்றும் வணிக ஆணையத்தின் கூட்டு இணைய மோசடி தடுப்பு மையம்.",
                    authorityOrPortal = "antifraudcentre-centreantifraude.ca"
                ),
                CrossBorderReliefItem(
                    titleEn = "Civil Resolution Tribunal (CRT)",
                    titleTa = "சிவில் தீர்வு தீர்ப்பாயம் (CRT)",
                    detailsEn = "Canada's first online tribunal resolving small claims and tenancy disputes without lawyers.",
                    detailsTa = "வழக்கறிஞர் இன்றியே ஆன்லைனில் சிறு நிதி மற்றும் வாடகை சர்ச்சைகளைத் தீர்க்கும் முறை.",
                    authorityOrPortal = "civilresolutionbc.ca"
                )
            ),
            electronicEvidenceStandardEn = "Canada Evidence Act Section 31.1-31.8 statutory requirements for electronic documents and system integrity standards.",
            electronicEvidenceStandardTa = "கனடா சாட்சிய சட்டம் பிரிவு 31.1-31.8 - மின்னணு ஆவணங்கள் மற்றும் கணினி ஒருமைப்பாடு."
        ),

        // 6. SINGAPORE (Phase 1 / International Arbitration Hub)
        JurisdictionNation(
            countryCode = "SG",
            nameEn = "Singapore",
            nameTa = "சிங்கப்பூர்",
            nativeName = "Republic of Singapore / சிங்கப்பூர் குடியரசு",
            flagEmoji = "🇸🇬",
            isP5PermanentMember = false,
            unStatusBadgeEn = "Global International Arbitration & Financial Hub",
            unStatusBadgeTa = "சர்வதேச நடுவர் தீர்ப்பு மையம் (Arbitration Hub)",
            primaryLegalLanguagesEn = listOf("English (Official Legal/Courts)", "Tamil (Official State)", "Mandarin", "Malay (National)"),
            primaryLegalLanguagesTa = listOf("ஆங்கிலம் (முதன்மை சட்ட மொழி)", "தமிழ் (அதிகாரப்பூர்வ அரசு மொழி)", "மாண்டரின்", "மலாய்"),
            phase = JurisdictionPhase.PHASE_1,
            legalSystem = LegalSystemType.COMMON_LAW,
            apexCourtEn = "Supreme Court of Singapore (High Court & Court of Appeal)",
            apexCourtTa = "சிங்கப்பூர் உச்ச நீதிமன்றம் (Supreme Court of Singapore)",
            emergencyPoliceNumber = "999",
            cyberHelplineNumber = "1799 (ScamShield Helpline / Anti-Scam Centre)",
            consumerOrLaborHelpline = "6100 0315 (CASE - Consumers Association of Singapore)",
            majorActsEn = listOf(
                "Consumer Protection (Fair Trading) Act 2003 (CPFTA)",
                "Employment Act 1968 (Cap 91)",
                "Protection from Harassment Act (POHA)",
                "Personal Data Protection Act 2012 (PDPA)",
                "Evidence Act 1893 (2020 Rev Ed)"
            ),
            majorActsTa = listOf(
                "நுகர்வோர் பாதுகாப்பு (நேர்மையான வர்த்தகம்) சட்டம் 2003 (CPFTA)",
                "வேலைவாய்ப்பு சட்டம் 1968 (Employment Act)",
                "துன்புறுத்தலில் இருந்து பாதுகாப்பு சட்டம் (POHA)",
                "தனிநபர் தரவு பாதுகாப்பு சட்டம் 2012 (PDPA)",
                "சாட்சிய சட்டம் 1893 (திருத்தப்பட்டது 2020)"
            ),
            eFilingPortalName = "Singapore Courts eLitigation & CJTS (Community Justice)",
            eFilingPortalUrl = "https://www.judiciary.gov.sg",
            tamilDiasporaSummaryEn = "Tamil is one of Singapore's 4 official languages with full parliamentary and legal standing. SIAC (Singapore International Arbitration Centre) is the world's premier commercial hub.",
            tamilDiasporaSummaryTa = "தமிழ் சிங்கப்பூரின் 4 அதிகாரப்பூர்வ மொழிகளில் ஒன்று; பாராளுமன்றம் மற்றும் அரசுப் பதிவுகளில் தமிழ் பயன்பாடு, SIAC சர்வதேச நடுவர் மையம்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "SIAC International Commercial Arbitration",
                    titleTa = "SIAC சர்வதேச வணிக நடுவர் தீர்ப்பு மையம்",
                    detailsEn = "World-leading arbitration venue enforcing cross-border commercial disputes under the Singapore Convention.",
                    detailsTa = "சர்வதேச வணிக ஒப்பந்தங்களை விரைவாகவும் ரகசியமாகவும் தீர்த்து வைக்கும் முன்னணி மையம்.",
                    authorityOrPortal = "siac.org.sg"
                ),
                CrossBorderReliefItem(
                    titleEn = "TADM & Employment Claims Tribunal (ECT)",
                    titleTa = "TADM தொழிலாளர் குறைதீர்க்கும் தீர்ப்பாயம்",
                    detailsEn = "Tripartite Alliance for Dispute Management offering mandatory fast-track wage claim mediation.",
                    detailsTa = "சம்பள பாக்கிகள் மற்றும் தொழிலாளர் குறைபாடுகளுக்கான விரைவு தீர்ப்பாயம்.",
                    authorityOrPortal = "tal.sg/tadm"
                )
            ),
            electronicEvidenceStandardEn = "Evidence Act 1893 Section 35-36A electronic records certification and presumption of secure electronic records under Electronic Transactions Act.",
            electronicEvidenceStandardTa = "சாட்சிய சட்டம் பிரிவு 35-36A மற்றும் மின்னணு பரிவர்த்தனை சட்டம் - பாதுகாப்பான டிஜிட்டல் சான்றிதழ்."
        ),

        // 7. FRANCE (Phase 2 / P5 Permanent Member / Civil Law Leader)
        JurisdictionNation(
            countryCode = "FR",
            nameEn = "France",
            nameTa = "பிரான்ஸ்",
            nativeName = "République française",
            flagEmoji = "🇫🇷",
            isP5PermanentMember = true,
            unStatusBadgeEn = "UN Security Council Permanent Member (P5) / EU Pillar",
            unStatusBadgeTa = "ஐ.நா. பாதுகாப்பு கவுன்சில் நிரந்தர நாடு (P5) / ஐரோப்பிய மையம்",
            primaryLegalLanguagesEn = listOf("French (Official Legal & Diplomatic)"),
            primaryLegalLanguagesTa = listOf("பிரெஞ்சு (முதன்மை சட்ட & தூதரக மொழி)"),
            phase = JurisdictionPhase.PHASE_2,
            legalSystem = LegalSystemType.CIVIL_LAW,
            apexCourtEn = "Cour de Cassation (Judicial) & Conseil d'État (Administrative)",
            apexCourtTa = "பிரெஞ்சு மேல்முறையீட்டு நீதிமன்றம் (Cour de Cassation) & மாநில கவுன்சில்",
            emergencyPoliceNumber = "112 / 17 (Police Secours)",
            cyberHelplineNumber = "0 805 805 817 (Cybermalveillance.gouv.fr)",
            consumerOrLaborHelpline = "0809 540 550 (DGCCRF / SignalConso)",
            majorActsEn = listOf(
                "Code Civil des Français (Napoleonic Civil Code)",
                "Code de Procédure Civile (CPC)",
                "Code du Travail (Labor Code)",
                "Code de la Consommation (Consumer Code)",
                "General Data Protection Regulation (EU GDPR / RGPD)"
            ),
            majorActsTa = listOf(
                "நெப்போலியன் சிவில் சட்டக் குறியீடு (Code Civil)",
                "சிவில் நடைமுறை குறியீடு (CPC)",
                "தொழிலாளர் சட்டக் குறியீடு (Code du Travail)",
                "நுகர்வோர் பாதுகாப்பு குறியீடு",
                "ஐரோப்பிய ஒன்றிய GDPR தரவு பாதுகாப்பு சட்டம்"
            ),
            eFilingPortalName = "Portail Justice.fr & FranceConnect",
            eFilingPortalUrl = "https://www.justice.fr",
            tamilDiasporaSummaryEn = "Vibrant Tamil community in Paris (La Chapelle, Sarcelles, Île-de-France) and overseas territories (Réunion, Guadeloupe, Martinique). Strong consular ties via Embassy of India (Paris).",
            tamilDiasporaSummaryTa = "பாரிஸ், லா சேப்பல், சர்கெல்ஸ் மற்றும் ரியூனியன் தீவு தமிழர்கள்; இந்திய தூதரகத்தின் வழிகாட்டுதல் சேவைகள்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "SignalConso Consumer Portal",
                    titleTa = "சிக்னல்கான்சோ (SignalConso) நுகர்வோர் புகார் தளம்",
                    detailsEn = "Direct DGCCRF government mediation for fraudulent online merchants and deceptive billing.",
                    detailsTa = "பிரான்ஸ் நுகர்வோர் பாதுகாப்பு அமைப்பிடம் போலி வணிகர்கள் மீது புகார் அளிக்கும் தளம்.",
                    authorityOrPortal = "signal.conso.gouv.fr"
                ),
                CrossBorderReliefItem(
                    titleEn = "Conseil de Prud'hommes (Labor Court)",
                    titleTa = "தொழிலாளர் சமரச தீர்ப்பாயம் (Prud'hommes)",
                    detailsEn = "Specialized conciliation court for employment contract disputes and severance claims.",
                    detailsTa = "வேலைவாய்ப்பு ஒப்பந்த மீறல்கள் மற்றும் பணிநீக்க இழப்பீடுகளை விசாரிக்கும் தீர்ப்பாயம்.",
                    authorityOrPortal = "service-public.fr"
                )
            ),
            electronicEvidenceStandardEn = "Article 1366 & 1367 of the French Civil Code regarding electronic signature admissibility and timestamped hashing.",
            electronicEvidenceStandardTa = "பிரெஞ்சு சிவில் குறியீடு பிரிவு 1366 & 1367 - மின்னணு கையொப்பம் மற்றும் நேர முத்திரை சான்றுகள்."
        ),

        // 8. GERMANY (Phase 2 / EU Economic Powerhouse)
        JurisdictionNation(
            countryCode = "DE",
            nameEn = "Germany",
            nameTa = "ஜெர்மனி",
            nativeName = "Bundesrepublik Deutschland",
            flagEmoji = "🇩🇪",
            isP5PermanentMember = false,
            unStatusBadgeEn = "Largest Economy in Europe / G7 Powerhouse",
            unStatusBadgeTa = "ஐரோப்பாவின் மிகப்பெரிய பொருளாதாரம் / G7",
            primaryLegalLanguagesEn = listOf("German (Official Sole Legal Language)"),
            primaryLegalLanguagesTa = listOf("ஜெர்மன் (முதன்மை சட்ட மொழி)"),
            phase = JurisdictionPhase.PHASE_2,
            legalSystem = LegalSystemType.CIVIL_LAW,
            apexCourtEn = "Bundesverfassungsgericht (Federal Constitutional Court, Karlsruhe) & BGH",
            apexCourtTa = "ஜெர்மன் கூட்டாட்சி அரசியலமைப்பு நீதிமன்றம் & உச்ச நீதிமன்றம் (BGH)",
            emergencyPoliceNumber = "110 (Police) / 112 (Ambulance)",
            cyberHelplineNumber = "0800 274 1000 (BSI - Federal Cyber Security)",
            consumerOrLaborHelpline = "030 258 000 (Verbraucherzentrale Bundesverband)",
            majorActsEn = listOf(
                "Bürgerliches Gesetzbuch (BGB - German Civil Code)",
                "Zivilprozessordnung (ZPO - Code of Civil Procedure)",
                "Strafgesetzbuch (StGB - Criminal Code)",
                "Handelsgesetzbuch (HGB - Commercial Code)",
                "Bundesdatenschutzgesetz (BDSG & EU GDPR)"
            ),
            majorActsTa = listOf(
                "ஜெர்மன் சிவில் சட்டக் குறியீடு (BGB)",
                "சிவில் நடைமுறை சட்டம் (ZPO)",
                "குற்றவியல் சட்டக் குறியீடு (StGB)",
                "வணிகச் சட்டம் (HGB)",
                "கூட்டாட்சி தரவு பாதுகாப்பு சட்டம் (BDSG / GDPR)"
            ),
            eFilingPortalName = "Justizportal des Bundes und der Länder & beA (Elektronisches Anwaltspostfach)",
            eFilingPortalUrl = "https://justiz.de",
            tamilDiasporaSummaryEn = "Over 60,000 Tamils in Germany (North Rhine-Westphalia, Frankfurt, Stuttgart, Berlin). Strong community networks with Tamil schools, cultural centers, and Indian Embassy (Berlin).",
            tamilDiasporaSummaryTa = "60,000-க்கும் மேற்பட்ட தமிழர்கள் (ஃபிராங்க்ஃபர்ட், ஸ்டட்கார்ட், பெர்லின்); தமிழ்ப் பள்ளிகள் மற்றும் இந்திய தூதரக ஆதரவு.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "Verbraucherzentrale Consumer Dispute Resolution",
                    titleTa = "நுகர்வோர் உரிமைகள் ஆலோசனை மையம்",
                    detailsEn = "Independent state-backed consumer defense associations providing legal advice and dispute mediation.",
                    detailsTa = "நுகர்வோர் ஒப்பந்தங்கள் மற்றும் வங்கி கட்டண மோசடிகளுக்கு சட்ட ஆலோசனை வழங்கும் அமைப்பு.",
                    authorityOrPortal = "verbraucherzentrale.de"
                ),
                CrossBorderReliefItem(
                    titleEn = "Online Mahnverfahren (Summary Debt Order)",
                    titleTa = "ஆன்லைன் கடன் மீட்பு கட்டளை (Mahnverfahren)",
                    detailsEn = "Fast automated court enforcement of unpaid invoices and debts without initial court hearing.",
                    detailsTa = "நீதிமன்ற விசாரணை இன்றியே தாமதமான பணப்பட்டுவாடாவை மீட்கும் ஆன்லைன் வழிமுறை.",
                    authorityOrPortal = "online-mahnantrag.de"
                )
            ),
            electronicEvidenceStandardEn = "Section 371a & 416a ZPO regarding qualified electronic signatures (QES) and eIDAS compliance.",
            electronicEvidenceStandardTa = "ZPO பிரிவு 371a & 416a - தகுதிவாய்ந்த டிஜிட்டல் கையொப்பம் மற்றும் eIDAS ஐரோப்பிய விதிமுறைகள்."
        ),

        // 9. UNITED ARAB EMIRATES (Phase 3 / Middle East Trade Hub)
        JurisdictionNation(
            countryCode = "AE",
            nameEn = "United Arab Emirates",
            nameTa = "ஐக்கிய அரபு அமீரகம் (UAE)",
            nativeName = "الإمارات العربية المتحدة",
            flagEmoji = "🇦🇪",
            isP5PermanentMember = false,
            unStatusBadgeEn = "Middle East Global Trade & Aviation Hub / GCC",
            unStatusBadgeTa = "மத்திய கிழக்கு வர்த்தக மையம் / GCC முன்னணி",
            primaryLegalLanguagesEn = listOf("Arabic (Official Legal Language)", "English (Commercial Courts / DIFC / ADGM)"),
            primaryLegalLanguagesTa = listOf("அரபு (அதிகாரப்பூர்வ அரசு மொழி)", "ஆங்கிலம் (வணிக நீதிமன்றங்கள் / DIFC)"),
            phase = JurisdictionPhase.PHASE_3,
            legalSystem = LegalSystemType.MIXED_SHARIA_COMMERCIAL,
            apexCourtEn = "Federal Supreme Court (Abu Dhabi) & Dubai Court of Cassation",
            apexCourtTa = "கூட்டாட்சி உச்ச நீதிமன்றம் (அபுதாபி) & துபாய் மேல்முறையீட்டு நீதிமன்றம்",
            emergencyPoliceNumber = "999",
            cyberHelplineNumber = "800 2626 (Aman Service Abu Dhabi) / Dubai Police e-Crime",
            consumerOrLaborHelpline = "600 590000 (MOHRE Labor Helpline) / 800 12 (Consumer)",
            majorActsEn = listOf(
                "Federal Decree-Law No. 33 of 2021 (UAE Labor Law)",
                "Federal Decree-Law No. 31 of 2021 (Penal Code)",
                "Federal Decree-Law No. 34 of 2021 (Cybercrimes Law)",
                "DIFC Law No. 10 of 2004 (Court Law - English Common Law)",
                "Commercial Transactions Law"
            ),
            majorActsTa = listOf(
                "கூட்டாட்சி தொழிலாளர் சட்டம் எண் 33 (2021)",
                "கூட்டாட்சி குற்றவியல் சட்டம் (Penal Code 2021)",
                "சைபர் குற்றங்கள் தடுப்பு சட்டம் எண் 34 (2021)",
                "DIFC நீதிமன்ற சட்டம் (ஆங்கில பொது சட்டம்)",
                "வணிகப் பரிவர்த்தனை சட்டம்"
            ),
            eFilingPortalName = "MOHRE Labor Portal, Dubai Courts & Abu Dhabi Judicial Department (ADJD)",
            eFilingPortalUrl = "https://www.mohre.gov.ae",
            tamilDiasporaSummaryEn = "Home to over 400,000 Tamil expatriates (Dubai, Abu Dhabi, Sharjah, Ajman). Extensive labor support channels via Indian Consulate (Dubai) and Pravasi Bharatiya Sahayata Kendra (PBSK).",
            tamilDiasporaSummaryTa = "4 லட்சத்திற்கும் மேற்பட்ட தமிழ் புலம்பெயர்ந்த தொழிலாளர்கள் மற்றும் வர்த்தகர்கள்; பிரவாசி பாரதிய உதவி மையம் (PBSK), இந்திய தூதரகம்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "MOHRE Labor Dispute Fast-Track Resolution",
                    titleTa = "MOHRE தொழிலாளர் குறைதீர்க்கும் அவசர தளம்",
                    detailsEn = "Statutory mediation for delayed wages (WPS), passport retention, end-of-service gratuity, and labor complaints.",
                    detailsTa = "சம்பள பாக்கி (WPS), பாஸ்போர்ட் பறிமுதல் மற்றும் பணிக்கொடை சிக்கல்களுக்கான உடனடி அரசு தலையீடு.",
                    authorityOrPortal = "mohre.gov.ae"
                ),
                CrossBorderReliefItem(
                    titleEn = "Dubai e-Crime Financial Scam Reporting",
                    titleTa = "துபாய் போலீஸ் e-Crime இணைய மோசடி புகார்",
                    detailsEn = "Direct online portal for phishing, unauthorized credit card charges, SIM swap, and electronic blackmail.",
                    detailsTa = "வங்கி அட்டை மோசடிகள் மற்றும் இணைய மிரட்டல்களுக்கான துபாய் போலீஸ் தளம்.",
                    authorityOrPortal = "ecrime.ae"
                ),
                CrossBorderReliefItem(
                    titleEn = "Pravasi Bharatiya Sahayata Kendra (PBSK)",
                    titleTa = "பிரவாசி பாரதிய உதவி மையம் (PBSK 24/7)",
                    detailsEn = "24x7 toll-free legal and psychological counseling helpline for Indian nationals in the UAE (800 46342).",
                    detailsTa = "இந்திய தொழிலாளர்களுக்கான 24 மணி நேர இலவச சட்ட மற்றும் உளவியல் ஆலோசனை உதவி எண்.",
                    authorityOrPortal = "800 46342"
                )
            ),
            electronicEvidenceStandardEn = "Federal Decree-Law No. 46 of 2021 on Electronic Transactions and Trust Services ensuring full legal equivalence of digital evidence.",
            electronicEvidenceStandardTa = "மின்னணு பரிவர்த்தனைகள் சட்டம் 2021 - டிஜிட்டல் சான்றுகளுக்கு முழு சட்டப்பூர்வ அங்கீகாரம்."
        ),

        // 10. SAUDI ARABIA (Phase 3 / Middle East Hub)
        JurisdictionNation(
            countryCode = "SA",
            nameEn = "Saudi Arabia",
            nameTa = "சவுதி அரேபியா",
            nativeName = "المملكة العربية السعودية",
            flagEmoji = "🇸🇦",
            isP5PermanentMember = false,
            unStatusBadgeEn = "G20 Middle East Energy & Economic Power",
            unStatusBadgeTa = "G20 மத்திய கிழக்கு பொருளாதார முன்னணி",
            primaryLegalLanguagesEn = listOf("Arabic (Official Sole Legal Language)", "English (Commercial Arbitration)"),
            primaryLegalLanguagesTa = listOf("அரபு (முதன்மை சட்ட மொழி)", "ஆங்கிலம் (வணிக நடுவர் தீர்ப்பு)"),
            phase = JurisdictionPhase.PHASE_3,
            legalSystem = LegalSystemType.MIXED_SHARIA_COMMERCIAL,
            apexCourtEn = "Supreme Court of Saudi Arabia (Riyadh) & High Judicial Council",
            apexCourtTa = "சவுதி அரேபிய உச்ச நீதிமன்றம் (ரியாத்) & உயர் நீதித்துறை கவுன்சில்",
            emergencyPoliceNumber = "911 / 999",
            cyberHelplineNumber = "Kulluna Amn App / 989",
            consumerOrLaborHelpline = "19911 (Ministry of Human Resources and Social Development - MHRSD)",
            majorActsEn = listOf(
                "Saudi Labor Law (Royal Decree No. M/51)",
                "Anti-Cyber Crime Law (Royal Decree No. M/17)",
                "Commercial Courts Law (Royal Decree No. M/93)",
                "Evidence Law (Royal Decree No. M/43 of 2022)",
                "Civil Transactions Law (2023)"
            ),
            majorActsTa = listOf(
                "சவுதி தொழிலாளர் சட்டம் (Royal Decree M/51)",
                "சைபர் குற்றங்கள் தடுப்பு சட்டம் (M/17)",
                "வணிக நீதிமன்றங்கள் சட்டம் (M/93)",
                "புதிய சாட்சிய சட்டம் 2022 (M/43)",
                "சிவில் பரிவர்த்தனைகள் சட்டம் 2023"
            ),
            eFilingPortalName = "Najiz Judicial Portal (Ministry of Justice) & Qiwa Platform",
            eFilingPortalUrl = "https://najiz.sa",
            tamilDiasporaSummaryEn = "Over 350,000 Tamil workers and professionals across Riyadh, Jeddah, Dammam, and Jubail. Active Indian Embassy (Riyadh) and Consulate General (Jeddah) legal aid cells.",
            tamilDiasporaSummaryTa = "3.5 லட்சத்திற்கும் மேற்பட்ட தமிழ் தொழிலாளர்கள் (ரியாத், ஜித்தா, தம்மாம்); இந்திய தூதரக இலவச சட்ட உதவி மையங்கள்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "Qiwa & Wedi Labor Dispute Mediation",
                    titleTa = "கிவா (Qiwa) & வேதி (Wedi) தொழிலாளர் சமரசம்",
                    detailsEn = "Online dispute filing for unpaid wages, final exit visas, sponsorship transfer, and contract termination.",
                    detailsTa = "சம்பள பாக்கி, வெளியேறும் விசா மற்றும் ஒப்பந்த சிக்கல்களுக்கான மனிதவள அமைச்சக தளம்.",
                    authorityOrPortal = "qiwa.sa / hrsd.gov.sa"
                ),
                CrossBorderReliefItem(
                    titleEn = "Kulluna Amn Security App",
                    titleTa = "குல்லுனா அம்ன் (Kulluna Amn) பாதுகாப்பு செயலி",
                    detailsEn = "Direct ministry app for citizens and expats to report financial fraud, blackmail, and traffic incidents.",
                    detailsTa = "இணைய மோசடி மற்றும் அவசர பாதுகாப்பு புகார்களைப் பதிவு செய்யும் அரசு செயலி.",
                    authorityOrPortal = "moi.gov.sa"
                )
            ),
            electronicEvidenceStandardEn = "Evidence Law 2022 (Royal Decree M/43) recognizing electronic records, SMS, and WhatsApp communications as primary conclusive evidence.",
            electronicEvidenceStandardTa = "சவுதி சாட்சிய சட்டம் 2022 - மின்னணு ஆவணங்கள் மற்றும் வாட்ஸ்அப் பதிவுகளுக்கு முழு சாட்சிய அந்தஸ்து."
        ),

        // 11. CHINA (P5 Permanent Member / Strategic Power)
        JurisdictionNation(
            countryCode = "CN",
            nameEn = "China",
            nameTa = "சீனா",
            nativeName = "中华人民共和国",
            flagEmoji = "🇨🇳",
            isP5PermanentMember = true,
            unStatusBadgeEn = "UN Security Council Permanent Member (P5)",
            unStatusBadgeTa = "ஐ.நா. பாதுகாப்பு கவுன்சில் நிரந்தர நாடு (P5)",
            primaryLegalLanguagesEn = listOf("Standard Mandarin Chinese (Sole Official)"),
            primaryLegalLanguagesTa = listOf("மாண்டரின் சீனம் (முதன்மை மொழி)"),
            phase = JurisdictionPhase.UN_P5_STRATEGIC,
            legalSystem = LegalSystemType.SOCIALIST_CIVIL,
            apexCourtEn = "Supreme People's Court of the People's Republic of China (Beijing)",
            apexCourtTa = "சீன உச்ச மக்கள் நீதிமன்றம் (பெய்ஜிங்)",
            emergencyPoliceNumber = "110",
            cyberHelplineNumber = "12321 (Cyber Security & Anti-Fraud Center)",
            consumerOrLaborHelpline = "12315 (Consumer Rights Protection) / 12333 (Labor)",
            majorActsEn = listOf(
                "Civil Code of the People's Republic of China (2021)",
                "Criminal Law of the PRC",
                "Personal Information Protection Law (PIPL 2021)",
                "Data Security Law (DSL)",
                "Cybersecurity Law of the PRC"
            ),
            majorActsTa = listOf(
                "சீன மக்கள் குடியரசின் சிவில் குறியீடு (2021)",
                "சீன குற்றவியல் சட்டம்",
                "தனிநபர் தகவல் பாதுகாப்பு சட்டம் (PIPL 2021)",
                "தரவு பாதுகாப்பு சட்டம் (DSL)",
                "சைபர் பாதுகாப்பு சட்டம்"
            ),
            eFilingPortalName = "China Judgments Online & Supreme People's Court e-Service",
            eFilingPortalUrl = "https://wenshu.court.gov.cn",
            tamilDiasporaSummaryEn = "Expatriate Tamil traders in Guangzhou, Yiwu, Shanghai, and Hong Kong SAR. CIETAC international arbitration and Indian Embassy (Beijing) support.",
            tamilDiasporaSummaryTa = "குவாங்சோ, யிவு, ஷாங்காய் மற்றும் ஹாங்காங் தமிழ் வர்த்தகர்கள்; CIETAC நடுவர் தீர்ப்பு மையம்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "CIETAC International Commercial Arbitration",
                    titleTa = "CIETAC சர்வதேச வர்த்தக நடுவர் மையம்",
                    detailsEn = "China International Economic and Trade Arbitration Commission for cross-border export/import disputes.",
                    detailsTa = "சர்வதேச ஏற்றுமதி-இறக்குமதி ஒப்பந்த சர்ச்சைகளைத் தீர்க்கும் நடுவர் மன்றம்.",
                    authorityOrPortal = "cietac.org"
                )
            ),
            electronicEvidenceStandardEn = "Civil Procedure Law of the PRC Article 63 and SPC Provisions on Electronic Data in Civil Litigation using blockchain timestamping.",
            electronicEvidenceStandardTa = "சீன சிவில் நடைமுறை சட்டம் பிரிவு 63 மற்றும் பிளாக்செயின் நேர முத்திரை சான்றுகள்."
        ),

        // 12. JAPAN (Strategic Global Power / Tech Leader)
        JurisdictionNation(
            countryCode = "JP",
            nameEn = "Japan",
            nameTa = "ஜப்பான்",
            nativeName = "日本国 (Nihon-koku)",
            flagEmoji = "🇯🇵",
            isP5PermanentMember = false,
            unStatusBadgeEn = "Leading Global Tech Power / G7 Member",
            unStatusBadgeTa = "உலகின் முன்னணி தொழில்நுட்ப நாடு / G7",
            primaryLegalLanguagesEn = listOf("Japanese (Official Sole Legal Language)"),
            primaryLegalLanguagesTa = listOf("ஜப்பானியம் (முதன்மை சட்ட மொழி)"),
            phase = JurisdictionPhase.UN_P5_STRATEGIC,
            legalSystem = LegalSystemType.CIVIL_LAW,
            apexCourtEn = "Supreme Court of Japan (Saikō-saibansho, Tokyo)",
            apexCourtTa = "ஜப்பான் உச்ச நீதிமன்றம் (டோக்கியோ)",
            emergencyPoliceNumber = "110",
            cyberHelplineNumber = "#9110 (Police Consultation) / NPA Cybercrime",
            consumerOrLaborHelpline = "188 (National Consumer Affairs Center Hotline)",
            majorActsEn = listOf(
                "Civil Code of Japan (Minpō - Six Codes)",
                "Code of Civil Procedure (Minji Soshō Hō)",
                "Penal Code (Keihō)",
                "Act on the Protection of Personal Information (APPI)",
                "Consumer Contract Act"
            ),
            majorActsTa = listOf(
                "ஜப்பான் சிவில் குறியீடு (Minpō - ஆறு குறியீடுகள்)",
                "சிவில் நடைமுறை சட்டம்",
                "குற்றவியல் சட்டம் (Keihō)",
                "தனிநபர் தகவல் பாதுகாப்பு சட்டம் (APPI)",
                "நுகர்வோர் ஒப்பந்த சட்டம்"
            ),
            eFilingPortalName = "Courts of Japan (Saibansho) & Houterasu Legal Aid Portal",
            eFilingPortalUrl = "https://www.courts.go.jp",
            tamilDiasporaSummaryEn = "Growing Tamil engineering and research community in Tokyo, Yokohama, and Osaka. Active Japan Tamil Sangam and Embassy of India (Tokyo).",
            tamilDiasporaSummaryTa = "டோக்கியோ, யோகோகாமா, ஒசாகா தமிழ் பொறியாளர்கள்; ஜப்பான் தமிழ்ச் சங்கம் மற்றும் இந்திய தூதரகம்.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "Houterasu Japan Legal Support Center",
                    titleTa = "ஹௌதெராசு (Houterasu) இலவச சட்ட உதவி மையம்",
                    detailsEn = "Government legal aid organization providing multilingual consultation for residents and foreigners.",
                    detailsTa = "வெளிநாட்டினருக்கு பன்மொழி சட்ட ஆலோசனை வழங்கும் ஜப்பான் அரசு மையம்.",
                    authorityOrPortal = "houterasu.or.jp"
                )
            ),
            electronicEvidenceStandardEn = "Act on Electronic Signatures and Certification Business (Act No. 102 of 2000) establishing presumption of authenticity for verified digital records.",
            electronicEvidenceStandardTa = "மின்னணு கையொப்ப சட்டம் 2000 - சரிபார்க்கப்பட்ட டிஜிட்டல் பதிவுகளின் உண்மைத்தன்மை."
        ),

        // 13. RUSSIA (P5 Permanent Member / Strategic Power)
        JurisdictionNation(
            countryCode = "RU",
            nameEn = "Russia",
            nameTa = "ரஷ்யா",
            nativeName = "Российская Федерация",
            flagEmoji = "🇷🇺",
            isP5PermanentMember = true,
            unStatusBadgeEn = "UN Security Council Permanent Member (P5)",
            unStatusBadgeTa = "ஐ.நா. பாதுகாப்பு கவுன்சில் நிரந்தர நாடு (P5)",
            primaryLegalLanguagesEn = listOf("Russian (Official State Language)"),
            primaryLegalLanguagesTa = listOf("ரஷ்யன் (முதன்மை சட்ட மொழி)"),
            phase = JurisdictionPhase.UN_P5_STRATEGIC,
            legalSystem = LegalSystemType.CIVIL_LAW,
            apexCourtEn = "Supreme Court of the Russian Federation (Moscow) & Constitutional Court",
            apexCourtTa = "ரஷ்ய கூட்டமைப்பு உச்ச நீதிமன்றம் (மாஸ்கோ) & அரசியலமைப்பு நீதிமன்றம்",
            emergencyPoliceNumber = "112 / 102 (Police)",
            cyberHelplineNumber = "MVD Cyber Crime Unit 'K' / Roskomnadzor",
            consumerOrLaborHelpline = "8 800 555 49 43 (Rospotrebnadzor Consumer Protection)",
            majorActsEn = listOf(
                "Civil Code of the Russian Federation (Parts I-IV)",
                "Criminal Code of the Russian Federation",
                "Code of Civil Procedure of the RF",
                "Federal Law on Personal Data (No. 152-FZ)",
                "Law on Protection of Consumers' Rights"
            ),
            majorActsTa = listOf(
                "ரஷ்ய கூட்டமைப்பு சிவில் குறியீடு",
                "ரஷ்ய குற்றவியல் குறியீடு",
                "சிவில் நடைமுறை குறியீடு",
                "தனிநபர் தரவு சட்டம் (152-FZ)",
                "நுகர்வோர் உரிமைகள் பாதுகாப்பு சட்டம்"
            ),
            eFilingPortalName = "GAS Pravosudie & Moy Arbitr Electronic Filing",
            eFilingPortalUrl = "https://ej.sudrf.ru",
            tamilDiasporaSummaryEn = "Tamil medical and engineering students and professionals in Moscow, Saint Petersburg, and Kursk. Consular support via Embassy of India (Moscow).",
            tamilDiasporaSummaryTa = "மாஸ்கோ, செயின்ட் பீட்டர்ஸ்பர்க் தமிழ் மருத்துவ மாணவர்கள்; இந்திய தூதரக ஆதரவு.",
            crossBorderRelief = listOf(
                CrossBorderReliefItem(
                    titleEn = "Rospotrebnadzor Consumer Defense",
                    titleTa = "ரோஸ்போட்ரெப்நாட்சோர் நுகர்வோர் பாதுகாப்பு",
                    detailsEn = "Federal Service for Surveillance on Consumer Rights Protection and Human Wellbeing.",
                    detailsTa = "நுகர்வோர் உரிமைகள் மற்றும் உணவுப் பாதுகாப்புக்கான மத்திய கண்காணிப்பு அமைப்பு.",
                    authorityOrPortal = "rospotrebnadzor.ru"
                )
            ),
            electronicEvidenceStandardEn = "Arbitration Procedure Code Article 75 and Civil Procedure Code Article 71 for notarized electronic evidence and digital logs.",
            electronicEvidenceStandardTa = "சிவில் நடைமுறை குறியீடு பிரிவு 71 - நோட்டரி சான்றளிக்கப்பட்ட டிஜிட்டல் பதிவுகள்."
        )
    )

    fun getNationByCode(code: String): JurisdictionNation {
        return ALL_NATIONS.firstOrNull { it.countryCode.equals(code, ignoreCase = true) }
            ?: ALL_NATIONS.first() // Default to India
    }

    fun getNationsByPhase(phase: JurisdictionPhase): List<JurisdictionNation> {
        return ALL_NATIONS.filter { it.phase == phase }
    }

    fun getP5Nations(): List<JurisdictionNation> {
        return ALL_NATIONS.filter { it.isP5PermanentMember }
    }
}
