package com.example.util

import com.example.domain.model.LanguagePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing a formal Indian statutory legal term with bilingual accuracy.
 */
data class LegalGlossaryTerm(
    val id: String,
    val actShortName: String,
    val actSection: String,
    val englishTerm: String,
    val tamilTerm: String,
    val englishDefinition: String,
    val tamilDefinition: String,
    val legalConsequenceEn: String,
    val legalConsequenceTa: String
)

/**
 * Dynamic Resource-Based Localization Manager for NyayaMate.
 * Provides synchronized app-wide string lookup, statutory terminology definitions,
 * and seamless dynamic switching between 'en_IN' (Indian English) and 'ta_IN' (Tamil).
 */
object LocalizationManager {

    const val LOCALE_EN_IN = "en_IN"
    const val LOCALE_TA_IN = "ta_IN"

    private val _currentLanguage = MutableStateFlow(LanguagePreference.ENGLISH)
    val currentLanguage: StateFlow<LanguagePreference> = _currentLanguage.asStateFlow()

    fun setLanguage(preference: LanguagePreference) {
        _currentLanguage.value = preference
    }

    fun setLocaleCode(localeCode: String) {
        _currentLanguage.value = when (localeCode) {
            LOCALE_TA_IN, "ta" -> LanguagePreference.TAMIL
            else -> LanguagePreference.ENGLISH
        }
    }

    fun getLocaleCode(preference: LanguagePreference = _currentLanguage.value): String {
        return if (preference == LanguagePreference.TAMIL) LOCALE_TA_IN else LOCALE_EN_IN
    }

    /**
     * Resolves app-wide string key for the current or specified language preference.
     */
    fun getString(key: String, lang: LanguagePreference = _currentLanguage.value): String {
        val isTa = lang == LanguagePreference.TAMIL
        return stringRegistry[key]?.let { if (isTa) it.tamil else it.english } ?: key
    }

    /**
     * Comprehensive Legal Terminology Glossary for Indian and Tamil Nadu Jurisdictions.
     */
    val legalGlossary: List<LegalGlossaryTerm> = listOf(
        // 1. Criminal Law / BNS & BNSS
        LegalGlossaryTerm(
            id = "fir",
            actShortName = "BNSS 2023 / CrPC",
            actSection = "Section 173 BNSS (Sec 154 CrPC)",
            englishTerm = "First Information Report (FIR)",
            tamilTerm = "முதல் தகவல் அறிக்கை (FIR)",
            englishDefinition = "Information recorded by police relating to the commission of a cognizable offence.",
            tamilDefinition = "காவல் துறையினரால் பிடியாணையின்றி கைது செய்யத்தக்க குற்றம் குறித்து பதிவு செய்யப்படும் ஆரம்ப அதிகாரப்பூர்வ அறிக்கை.",
            legalConsequenceEn = "Triggers statutory police investigation and mandatory arrest powers.",
            legalConsequenceTa = "காவல்துறை விசாரணை மற்றும் கைது நடவடிக்கைகளை உடனடியாகத் தொடங்குகிறது."
        ),
        LegalGlossaryTerm(
            id = "zero_fir",
            actShortName = "BNSS 2023",
            actSection = "Section 173(1) BNSS Proviso",
            englishTerm = "Zero FIR (Jurisdiction-Free FIR)",
            tamilTerm = "பூஜ்ஜிய எஃப்.ஐ.ஆர் (எல்லை வரம்பற்ற புகார்)",
            englishDefinition = "FIR registered at any police station irrespective of territorial jurisdiction, later transferred to the competent station.",
            tamilDefinition = "சம்பவம் நடந்த எல்லை வரம்பு பாராமல் எந்தவொரு காவல் நிலையத்திலும் உடனடியாக பதிவு செய்யப்படும் அவசர எஃப்.ஐ.ஆர்.",
            legalConsequenceEn = "Mandatory acceptance; police cannot refuse registration citing jurisdictional boundaries.",
            legalConsequenceTa = "காவல்துறை எல்லை வரம்பைக் கூறி புகாரை நிராகரிக்க முடியாது; பதிவு செய்து தகுந்த நிலையத்திற்கு மாற்ற வேண்டும்."
        ),
        LegalGlossaryTerm(
            id = "cognizable_offence",
            actShortName = "BNSS 2023",
            actSection = "Section 2(1)(g) BNSS",
            englishTerm = "Cognizable Offence",
            tamilTerm = "பிடியாணையின்றி கைது செய்யத்தக்க குற்றம்",
            englishDefinition = "An offence for which a police officer may arrest without a court warrant.",
            tamilDefinition = "நீதிமன்றத்தின் பிடியாணை (Warrant) இன்றியே காவல்துறை குற்றவாளியைக் கைது செய்யக்கூடிய தீவிர குற்றம்.",
            legalConsequenceEn = "Immediate arrest and automatic registration of FIR.",
            legalConsequenceTa = "உடனடி கைது மற்றும் முதல் தகவல் அறிக்கை பதிவு கட்டாயமாகிறது."
        ),
        LegalGlossaryTerm(
            id = "anticipatory_bail",
            actShortName = "BNSS 2023",
            actSection = "Section 482 BNSS (Sec 438 CrPC)",
            englishTerm = "Anticipatory Bail / Pre-Arrest Bail",
            tamilTerm = "முன்ஜாமீன் மனு (கைதுக்கு முந்தைய பிணை)",
            englishDefinition = "Direction issued by Sessions Court or High Court granting bail prior to an imminent arrest in a non-bailable case.",
            tamilDefinition = "பிணையில் வெளிவர முடியாத வழக்கில் கைது செய்யப்படுவோம் என்ற நியாயமான அச்சத்தின் போது நீதிமன்றத்தில் பெறப்படும் கைது பாதுகாப்பு ஆணை.",
            legalConsequenceEn = "Protects from police custody upon arrest subject to cooperation.",
            legalConsequenceTa = "கைது செய்யப்படும் போது பிணையில் விடுவிக்கப்பட்டு காவல் துறை காவலில் அடைக்கப்படுவது தடுக்கப்படுகிறது."
        ),
        LegalGlossaryTerm(
            id = "charge_sheet",
            actShortName = "BNSS 2023",
            actSection = "Section 193 BNSS (Sec 173(2) CrPC)",
            englishTerm = "Final Police Report / Charge Sheet",
            tamilTerm = "குற்றப்பத்திரிகை (இறுதி அறிக்கை)",
            englishDefinition = "Comprehensive final report submitted by the investigating officer to the Magistrate establishing prima facie guilt.",
            tamilDefinition = "காவல்துறை விசாரணை முடிவடைந்த பின் குற்றச்சாட்டுகளுக்கான சான்றுகளுடன் மாஜிஸ்திரேட்டிடம் தாக்கல் செய்யப்படும் இறுதி அறிக்கை.",
            legalConsequenceEn = "Initiates formal court trial and framing of charges against accused.",
            legalConsequenceTa = "நீதிமன்ற விசாரணையைத் தொடங்கி எதிரி மீதான குற்றச்சாட்டுகளைப் பதிவு செய்ய வழிவகுக்கிறது."
        ),

        // 2. Evidence & Cryptographic Section 65B / Section 63 BSA
        LegalGlossaryTerm(
            id = "sec65b_cert",
            actShortName = "BSA 2023 / IEA 1872",
            actSection = "Section 63 BSA (Sec 65B IEA)",
            englishTerm = "Section 65B / 63 BSA Electronic Certificate",
            tamilTerm = "பிரிவு 65B / 63 BSA மின்னணு சான்றிதழ்",
            englishDefinition = "Statutory certificate proving authenticity, integrity and lawful custody of digital device prints, WhatsApp chats and call logs.",
            tamilDefinition = "டிஜிட்டல் ஆதாரங்கள் (வாட்ஸ்அப், ஆடியோ, மின்னஞ்சல்) நீதிமன்றத்தில் செல்லுபடியாக சமர்ப்பிக்கப்படும் சட்டப்பூர்வ உண்மைத்தன்மை சான்றிதழ்.",
            legalConsequenceEn = "Mandatory prerequisite; electronic records inadmissible in Indian courts without Section 65B/63 certificate (Anvar P.V. vs P.K. Basheer).",
            legalConsequenceTa = "இந்த சான்றிதழ் இன்றி சமர்ப்பிக்கப்படும் டிஜிட்டல் சான்றுகளை நீதிமன்றம் ஆதாரமாக ஏற்றுக்கொள்ளாது."
        ),
        LegalGlossaryTerm(
            id = "sha256_hash",
            actShortName = "IT Act 2000 & BSA",
            actSection = "Section 3 IT Act & Sec 63 BSA",
            englishTerm = "SHA-256 Cryptographic Hash Seal",
            tamilTerm = "SHA-256 மாறா குறியாக்க ஹாஷ் முத்திரை",
            englishDefinition = "A 256-bit cryptographic fingerprint guaranteeing the exact immutability and zero-tampering of an electronic document.",
            tamilDefinition = "டிஜிட்டல் ஆவணத்தில் எந்தவித மாற்றமும் செய்யப்படவில்லை என்பதை நிரூபிக்கும் 256-பிட் கணிதவியல் குறியாக்க கைரேகை.",
            legalConsequenceEn = "Establishes non-repudiation and chain of custody beyond reasonable doubt.",
            legalConsequenceTa = "ஆவணம் மாற்றப்படவில்லை என்பதை நீதிமன்றத்தில் மறுக்க முடியாதபடி நிரூபிக்கிறது."
        ),
        LegalGlossaryTerm(
            id = "affidavit",
            actShortName = "CPC 1908 & BSA",
            actSection = "Order XIX CPC & Sec 63 BSA",
            englishTerm = "Sworn Affidavit",
            tamilTerm = "பிரமாணப் பத்திரம் (உறுதிமொழி ஆவணம்)",
            englishDefinition = "Written statement of facts sworn under oath before a Notary Public or Oath Commissioner.",
            tamilDefinition = "நோட்டரி பப்ளிக் அல்லது பதவிப் பிரமாண அதிகாரி முன்னிலையில் உண்மையை மட்டுமே கூறுவதாக கையொப்பமிடப்படும் சட்டப்பூர்வ உறுதிமொழி ஆவணம்.",
            legalConsequenceEn = "Submitting false statements constitutes perjury under Section 227 BNS (Sec 193 IPC).",
            legalConsequenceTa = "பொய் உறுதிமொழி அளிப்பது நீதிமன்ற அவமதிப்பு மற்றும் தண்டனைக்குரிய குற்றமாகும்."
        ),

        // 3. Civil, Consumer & Tenancy Law
        LegalGlossaryTerm(
            id = "plaintiff",
            actShortName = "CPC 1908",
            actSection = "Order VII CPC",
            englishTerm = "Plaintiff / Complainant",
            tamilTerm = "வாதி / புகார் மனுதாரர்",
            englishDefinition = "The person or entity who institutes a civil suit or consumer complaint seeking legal redressal.",
            tamilDefinition = "நீதிமன்றத்தில் தனக்கு ஏற்பட்ட பாதிப்பிற்கு நிவாரணம் கோரி வழக்கு அல்லது புகார் மனுவை தாக்கல் செய்யும் நபர்.",
            legalConsequenceEn = "Carries the initial legal burden of proof under Section 104 BSA (Sec 101 IEA).",
            legalConsequenceTa = "வழக்கை நிரூபிக்கும் ஆரம்ப சட்டக் கடமை வாதிக்கே உரியது."
        ),
        LegalGlossaryTerm(
            id = "defendant",
            actShortName = "CPC 1908",
            actSection = "Order VIII CPC",
            englishTerm = "Defendant / Opposite Party",
            tamilTerm = "பிரதிவாதி / எதிர் மனுதாரர்",
            englishDefinition = "The person against whom a legal relief, monetary claim, or grievance is filed in court.",
            tamilDefinition = "யாருக்கு எதிராக நீதிமன்றத்தில் வழக்கு அல்லது இழப்பீடு கோரப்பட்டுள்ளதோ அந்த எதிர்தரப்பு நபர்.",
            legalConsequenceEn = "Must file Written Statement within 30 days (extendable to 120 days) of summons.",
            legalConsequenceTa = "அழைப்பாணை பெற்ற 30 நாட்களுக்குள் பதில் மனு (Written Statement) தாக்கல் செய்ய வேண்டும்."
        ),
        LegalGlossaryTerm(
            id = "cause_of_action",
            actShortName = "CPC 1908",
            actSection = "Order II Rule 2 CPC",
            englishTerm = "Cause of Action",
            tamilTerm = "வழக்கு மூலம் (வழக்குக்கான அடிப்படை நிகழ்வு)",
            englishDefinition = "The bundle of essential facts giving the plaintiff the statutory right to seek judicial remedy.",
            tamilDefinition = "பாதிக்கப்பட்ட நபர் நீதிமன்றத்தை அணுகுவதற்கான சட்டப்பூர்வ உரிமையை உருவாக்கும் முக்கிய நிகழ்வுகளின் தொகுப்பு.",
            legalConsequenceEn = "Determines territorial jurisdiction and limitation period countdown.",
            legalConsequenceTa = "நீதிமன்ற எல்லை வரம்பையும் காலவரையறை தொடங்கும் தேதியையும் தீர்மானிக்கிறது."
        ),
        LegalGlossaryTerm(
            id = "limitation_period",
            actShortName = "Limitation Act 1963",
            actSection = "Section 3 & Schedule Limitation Act",
            englishTerm = "Statutory Limitation Period",
            tamilTerm = "சட்டப்பூர்வ காலவரையறை வரம்பு",
            englishDefinition = "The strictly enforced maximum time window prescribed by statute within which a legal action must be instituted.",
            tamilDefinition = "ஒரு வழக்கையோ முறையீட்டையோ நீதிமன்றத்தில் தாக்கல் செய்வதற்கு சட்டத்தால் நிர்ணயிக்கப்பட்ட அதிகபட்ச கால அவகாசம்.",
            legalConsequenceEn = "Suits filed after expiry of limitation must be summarily dismissed under Section 3.",
            legalConsequenceTa = "காலக்கெடு முடிந்த பின் தாக்கல் செய்யப்படும் மனுக்கள் தள்ளுபடி செய்யப்படும்."
        ),
        LegalGlossaryTerm(
            id = "injunction",
            actShortName = "Specific Relief Act 1963",
            actSection = "Section 37-39 Specific Relief Act & Order 39 CPC",
            englishTerm = "Interim Injunction / Stay Order",
            tamilTerm = "இடைக்கால தடையாணை (தடை உத்தரவு)",
            englishDefinition = "Judicial order restraining a party from doing an act (e.g., unlawful eviction, demolition, or sale of disputed property).",
            tamilDefinition = "வழக்கு முடியும் வரை சொத்தை விற்கவோ, இடிக்கவோ அல்லது அத்துமீறவோ கூடாது என நீதிமன்றம் பிறப்பிக்கும் தடை ஆணை.",
            legalConsequenceEn = "Violation leads to attachment of property and civil prison detention.",
            legalConsequenceTa = "உத்தரவை மீறினால் சொத்து முடக்கம் மற்றும் சிறைத்தண்டனை விதிக்கப்படும்."
        ),
        LegalGlossaryTerm(
            id = "vakalatnama",
            actShortName = "Advocates Act 1961",
            actSection = "Section 30 Advocates Act & Order III CPC",
            englishTerm = "Vakalatnama (Counsel Retainer Authorization)",
            tamilTerm = "வக்காலத்து நாமா (வழக்கறிஞர் அதிகார ஆவணம்)",
            englishDefinition = "Formal written document executed by a litigant appointing and authorizing an advocate to represent them before the court.",
            tamilDefinition = "நீதிமன்றத்தில் தன் சார்பாக வாதாட வழக்கறிஞருக்கு முழு அதிகாரம் வழங்கி மனுதாரர் கையொப்பமிடும் ஆவணம்.",
            legalConsequenceEn = "Confers lawful representation, power to receive court notices, and inspect records.",
            legalConsequenceTa = "மனுதாரர் சார்பாக நீதிமன்றத்தில் ஆஜராகவும் ஆவணங்களை ஆய்வு செய்யவும் வழக்கறிஞருக்கு அதிகாரம் அளிக்கிறது."
        ),

        // 4. Banking & Cheque Bounce (Sec 138 NI Act)
        LegalGlossaryTerm(
            id = "sec138_ni_act",
            actShortName = "NI Act 1881",
            actSection = "Section 138 Negotiable Instruments Act",
            englishTerm = "Dishonour of Cheque (Sec 138 NI Act)",
            tamilTerm = "காசோலை பவுன்ஸ் / அவமதிப்பு வழக்கு (பிரிவு 138)",
            englishDefinition = "Criminal liability for return of cheque unpaid due to insufficiency of funds in bank account.",
            tamilDefinition = "வங்கி கணக்கில் போதிய பணம் இல்லாததால் காசோலை திரும்பப் பெறப்படும் போது ஏற்படும் குற்றவியல் பொறுப்பு.",
            legalConsequenceEn = "Mandatory 15-day demand notice required; punishable with up to 2 years imprisonment or twice the cheque amount.",
            legalConsequenceTa = "15 நாள் சட்ட அறிவிப்பு கட்டாயம்; 2 ஆண்டுகள் வரை சிறை அல்லது காசோலை தொகையை விட 2 மடங்கு அபராதம் விதிக்கப்படலாம்."
        ),

        // 5. Motor Accident Claims (MACT)
        LegalGlossaryTerm(
            id = "mact_claim",
            actShortName = "Motor Vehicles Act 1988",
            actSection = "Section 166 MV Act 1988 (Amended 2019)",
            englishTerm = "MACT Claim Petition",
            tamilTerm = "மோட்டார் வாகன விபத்து இழப்பீட்டு மனு",
            englishDefinition = "Application for compensation filed before MACT for death, grievous injury, or property damage caused by motor accidents.",
            tamilDefinition = "வாகன விபத்தினால் ஏற்படும் உயிரிழப்பு அல்லது பலத்த காயத்திற்கு உரிய இழப்பீடு கோரி தீர்ப்பாயத்தில் தாக்கல் செய்யப்படும் மனு.",
            legalConsequenceEn = "Strict 6-month statutory limitation period from incident date (2019 Amendment).",
            legalConsequenceTa = "விபத்து நடந்த தேதியிலிருந்து 6 மாதங்களுக்குள் கட்டாயமாக மனு தாக்கல் செய்யப்பட வேண்டும்."
        ),

        // 6. Right to Information (RTI Act 2005)
        LegalGlossaryTerm(
            id = "rti_application",
            actShortName = "RTI Act 2005",
            actSection = "Section 6(1) & Section 7(1) RTI Act",
            englishTerm = "RTI Request & 30-Day Clock",
            tamilTerm = "தகவல் அறியும் உரிமை மனு & 30 நாள் காலக்கெடு",
            englishDefinition = "Citizen request to Public Information Officer (PIO) for public records; mandatory response within 30 days (48 hours for life/liberty).",
            tamilDefinition = "அரசு அலுவலக ஆவணங்களை பெற பொது தகவல் அலுவலருக்கு அனுப்பப்படும் மனு; 30 நாட்களுக்குள் (உயிர்/சுதந்திரம் எனில் 48 மணி நேரத்தில்) பதில் தருவது கட்டாயம்.",
            legalConsequenceEn = "Deemed refusal if unanswered; empowers Section 19(1) First Appeal and ₹250/day penalty on PIO.",
            legalConsequenceTa = "காலக்கெடுவுக்குள் தகவல் தராவிடில் முதல் மேல்முறையீடு செய்யலாம் மற்றும் அலுவலருக்கு நாள் ஒன்றுக்கு ₹250 அபராதம் விதிக்கப்படும்."
        ),

        // 7. Free Legal Aid & NALSA
        LegalGlossaryTerm(
            id = "nalsa_aid",
            actShortName = "Legal Services Authorities Act 1987",
            actSection = "Section 12 Legal Services Authorities Act",
            englishTerm = "Free Legal Aid Entitlement",
            tamilTerm = "இலவச சட்ட உதவி & லோக் அதாலத்",
            englishDefinition = "Statutory right to free advocate representation, exemption from court fees, and dispute settlement via Lok Adalat.",
            tamilDefinition = "பெண்கள், எஸ்சி/எஸ்டி, தொழிலாளர்கள் மற்றும் ஏழை எளிய மக்களுக்கு அரசு செலவில் வழக்கறிஞர் மற்றும் நீதிமன்ற கட்டண விலக்கு பெறும் சட்டப்பூர்வ உரிமை.",
            legalConsequenceEn = "Lok Adalat awards possess the status of a final civil decree with zero appeal.",
            legalConsequenceTa = "லோக் அதாலத் தீர்ப்புக்கு மேல்முறையீடு கிடையாது; இறுதி சிவில் நீதிமன்ற தீர்ப்பாக கருதப்படும்."
        )
    )

    fun getLegalTerm(termId: String): LegalGlossaryTerm? {
        return legalGlossary.firstOrNull { it.id.equals(termId, ignoreCase = true) }
    }

    /**
     * Internal String Key Registry mapping English and Tamil translations.
     */
    private data class StringPair(val english: String, val tamil: String)

    private val stringRegistry: Map<String, StringPair> = mapOf(
        "landing_hero_title" to StringPair("Democratizing Statutory Justice", "நீதி அணுகலை எளிமையாக்குதல்"),
        "landing_hero_sub" to StringPair("AI-assisted legal intake, cryptographic evidence vault & smart statutory notice generator for every citizen.", "குடிமக்களுக்கான AI சட்ட வழிகாட்ட, சான்றுகள் பெட்டகம் & சட்ட அறிவிப்பு தயாரிப்பு."),
        "citizen_rights_title" to StringPair("Citizen Statutory Rights & Legal Protections", "குடிமக்களின் சட்டப்பூர்வ உரிமைகள் & பாதுகாப்புக்கள்"),
        "ux_principles_title" to StringPair("Citizen Statutory Rights & Legal Protections", "குடிமக்களின் சட்டப்பூர்வ உரிமைகள் & பாதுகாப்புக்கள்"),
        // Core Brand & Identity
        "app_title" to StringPair("Justra", "ஜஸ்ட்ரா"),
        "app_sub" to StringPair("Digital Legal Intake & Vault", "டிஜிட்டல் சட்ட உதவி & ஆவணப் பெட்டகம்"),
        "welcome_title" to StringPair("Welcome to Justra — The Citizen's Legal Compass", "வணக்கம் | Welcome to Justra"),
        "welcome_desc" to StringPair("Human Dignity, Procedural Clarity & AI-Assisted Legal Protection", "சட்டப் பாதுகாப்பும் எளிமையான வழிகாட்டலும் உங்கள் கைகளில்"),
        "select_language" to StringPair("Select Operating Language", "இயக்க மொழியைத் தேர்வுசெய்யவும்"),
        "terms_headline" to StringPair("Statutory Boundary & Consent", "சட்டப்பூர்வ நிபந்தனைகள் & ஒப்புதல்"),
        "accept_and_proceed" to StringPair("Accept & Proceed", "ஏற்றுக்கொண்டு தொடர்க"),
        "unlock_title" to StringPair("Unlock Justra", "ஜஸ்ட்ராவைத் திறக்கவும்"),
        "unlock_desc" to StringPair("Your evidence vault, dispute records and drafts are hardware-encrypted", "உங்கள் ஆவணப் பெட்டகம் மற்றும் சான்றுகள் பாதுகாப்பாக பூட்டப்பட்டுள்ளன"),
        "biometric_unlock" to StringPair("Biometric Unlock", "கைரேகை / முக அங்கீகாரம்"),
        "use_pin" to StringPair("Use Device PIN / Passcode", "சாதன PIN / கடவுச்சொல் பயன்படுத்தவும்"),
        "unlock_success" to StringPair("Access Granted", "வெற்றிகரமாக திறக்கப்பட்டது"),
        "describe_legal_problem" to StringPair("Describe your legal problem", "உங்கள் பிரச்சனையை கூறுங்கள்"),
        "describe_hint" to StringPair("Tap microphone to speak or type here...", "வாய்வழியாகப் பேசவும் அல்லது தட்டச்சு செய்யவும்..."),
        "active_cases" to StringPair("Active Cases & Disputes", "நடப்பு வழக்குகள் & முறையீடுகள்"),
        "no_active_cases" to StringPair("No active cases. Describe a problem or select a category below.", "நடப்பு வழக்குகள் எதுவும் இல்லை. புதிய பிரச்சனையைத் தொடங்கவும்."),
        "categories" to StringPair("Dispute Categories & Statutory Portals", "சட்டப்பிரிவுகள் & உதவிப்பிரிவுகள்"),
        "emergency_helpline_bar" to StringPair("Emergency Helplines (Direct OS Dialer)", "அவசர உதவி எண்கள்"),
        "cyber_1930" to StringPair("1930 Cybercrime (Golden Hour)", "1930 சைபர் கிரைம் (உடனடி நிதி முடக்கம்)"),
        "emergency_112" to StringPair("112 National Emergency", "112 பொது அவசர உதவி"),
        "consumer_1915" to StringPair("1915 Consumer Helpline", "1915 நுகர்வோர் உதவி"),
        "action_navigator" to StringPair("Legal Action Navigator", "சட்ட நடவடிக்கை வழிகாட்டி"),
        "smart_complaint" to StringPair("Smart Complaint Generator", "புகார் மனு ஜெனரேட்டர்"),
        "evidence_vault" to StringPair("Evidence Vault", "சான்றுகள் பெட்டகம் (Vault)"),
        "timeline_readiness" to StringPair("Timeline & Readiness", "காலவரிசை & தயார்நிலை"),
        "scam_checker" to StringPair("Scam & Fraud Checker", "மோசடி செய்தி சோதனை"),
        "counsel_handoff" to StringPair("Legal Counsel Handoff", "வழக்கறிஞர் இணைப்பு (Handoff)"),
        "export_pdf" to StringPair("Export Signed PDF", "மனுவை PDF-ஆக பதிவிறக்குக"),
        "add_artifact" to StringPair("Add Artifact to Vault", "புதிய சான்றை சேர்க்க"),
        "paste_clipboard" to StringPair("Paste from Clipboard", "நகலெடுத்ததை ஒட்டுக (Paste)"),
        "analyze_threat" to StringPair("Analyze Threat Indicators", "அச்சுறுத்தலை ஆய்வு செய்க"),
        "generate_token" to StringPair("Generate Secure Share Link", "பாதுகாப்பான பகிர்வு குறியீட்டை உருவாக்கு"),
        "new_case_title" to StringPair("Initiate New Legal Dispute", "புதிய வழக்கை உருவாக்கு"),
        "case_created" to StringPair("Dispute record created successfully", "வழக்கு வெற்றிகரமாக உருவாக்கப்பட்டது"),

        // Navigation
        "nav_home" to StringPair("Dashboard", "முகப்பு"),
        "nav_chat" to StringPair("AI Legal Intake", "சட்ட உதவி AI"),
        "nav_scam" to StringPair("Scam Checker", "மோசடி சோதனை"),
        "nav_vault" to StringPair("Evidence Vault", "சான்றுகள்"),
        "nav_menu" to StringPair("Menu Bar", "மெனு பார்"),
        "nav_navigator" to StringPair("Action Steps", "நடவடிக்கை வழிகாட்டி"),
        "nav_complaint" to StringPair("Draft Complaint", "மனு எழுதுதல்"),
        "nav_timeline" to StringPair("Timeline & Readiness", "காலவரிசை & தயார்நிலை"),
        "nav_counsel" to StringPair("Counsel Handoff", "வழக்கறிஞர் இணைப்பு"),
        "nav_auth" to StringPair("Vault Security", "பெட்டக பூட்டு"),
        "menubar_title" to StringPair("Justra Navigation Menu Bar", "ஜஸ்ட்ரா மெனு பார் (அனைத்து பிரிவுகள்)"),
        "quick_jump" to StringPair("Quick Jump & Modules", "விரைவு வழிசெலுத்தல்"),

        // 10 Specialized Modules
        "nav_court_fee" to StringPair("Court Fee & Jurisdiction", "நீதிமன்ற கட்டண கணக்கீடு"),
        "nav_sec65b" to StringPair("Section 65B / 63 BSA", "பிரிவு 65B சான்றிதழ்"),
        "nav_rti" to StringPair("RTI Application & Appeals", "RTI மனு & மேல்முறையீடு"),
        "nav_legal_notice" to StringPair("Legal Demand Notice", "சட்ட அறிவிப்பு தயாரிப்பான்"),
        "nav_bns_ipc" to StringPair("BNS vs IPC Navigator", "BNS 2023 vs IPC ஒப்பீடு"),
        "nav_nalsa" to StringPair("NALSA Free Legal Aid", "இலவச சட்ட உதவி & லோக் அதாலத்"),
        "nav_mact" to StringPair("MACT Accident Claim", "வாகன விபத்து இழப்பீடு (MACT)"),
        "nav_consumer_mediation" to StringPair("Consumer Mediation & e-Daakhil", "நுகர்வோர் சமரச தீர்வு"),
        "nav_women_rights" to StringPair("Women's Rights & PoSH", "பெண்கள் பாதுகாப்பு & PoSH"),
        "nav_cyber_cell" to StringPair("Cyber Crime Dossier", "சைபர் கிரைம் புகார் கோப்பு"),
        "nav_account_settings" to StringPair("Account & App Settings", "கணக்கு & செயலி அமைப்புகள்"),
        "nav_notifications_center" to StringPair("Notifications & Reminders", "அறிவிப்புகள் & நினைவூட்டல்"),

        // Voice & Visualizer Strings
        "voice_listening" to StringPair("Listening to Tamil Speech Input...", "தமிழ் குரல் உள்ளீடு செயலாக்கப்படுகிறது..."),
        "voice_tap_speak" to StringPair("Tap to speak in Tamil or English", "தமிழிலோ அல்லது ஆங்கிலத்திலோ பேச தட்டவும்"),
        "voice_processing" to StringPair("Real-time Audio Processing & Transcribing", "நேரலை குரல் செயலாக்கம் & உரை மாற்றம்"),
        "voice_tap_stop" to StringPair("Tap Stop to Finalize Intake", "உரையாடலை முடிக்க நிறுத்தவும்"),
        "voice_waveform_hint" to StringPair("Interactive Canvas Waveform Active", "நேரலை கேன்வாஸ் அலைவரிசை செயல்படுகிறது"),
        "voice_mic_active" to StringPair("Microphone Live", "மைக்ரோஃபோன் இயங்குகிறது"),

        // Re-authentication & Security
        "reauth_title" to StringPair("Biometric Re-authentication Required", "மீண்டும் பயோமெட்ரிக் அங்கீகாரம் தேவை"),
        "reauth_desc" to StringPair("Justra auto-locked after 5 minutes of inactivity to protect your privileged evidentiary vault.", "உங்கள் தனிப்பட்ட ஆவணப் பெட்டகத்தைப் பாதுகாக்க 5 நிமிட செயலற்ற நிலைக்குப் பிறகு ஜஸ்ட்ரா பூட்டப்பட்டது."),
        "reauth_cta" to StringPair("Unlock with Biometrics", "பயோமெட்ரிக் மூலம் திறக்கவும்"),
        "reauth_pin_cta" to StringPair("Unlock with Master PIN", "மாஸ்டர் PIN மூலம் திறக்கவும்"),
        "reauth_inactivity_badge" to StringPair("5-Minute Inactivity Protection Active", "5 நிமிட செயலற்ற பாதுகாப்பு பூட்டு செயலில் உள்ளது"),

        // Global Jurisdiction & UN Strings
        "nav_global_jurisdiction" to StringPair("Global Jurisdictions & UN Hub", "உலகளாவிய சட்ட எல்லைகள் & ஐ.நா. மையம்"),
        "global_hub_title" to StringPair("Global Jurisdictions & UN Legal Hub", "உலகளாவிய சட்ட எல்லைகள் & ஐ.நா. சட்ட மையம்"),
        "global_hub_subtitle" to StringPair("193 UN Nations, P5 Powers, Common Law & Phased Diaspora Framework", "193 ஐ.நா. நாடுகள், P5 வல்லரசுகள் & மூன்று கட்ட உலகளாவிய சட்டக் கட்டமைப்பு"),
        "active_jurisdiction" to StringPair("Active Jurisdiction", "தற்போதைய சட்ட எல்லை"),
        "switch_jurisdiction" to StringPair("Switch Jurisdiction", "சட்ட எல்லையை மாற்றுக"),
        "set_as_active" to StringPair("Set as Active Jurisdiction", "முதன்மை சட்ட எல்லையாகத் தேர்ந்தெடுக்கவும்"),
        "tab_all_nations" to StringPair("All Nations (13)", "அனைத்து நாடுகள்"),
        "tab_phase_1" to StringPair("Phase 1: Common Law", "Phase 1: பொது சட்டம்"),
        "tab_phase_2" to StringPair("Phase 2: Europe", "Phase 2: ஐரோப்பா"),
        "tab_phase_3" to StringPair("Phase 3: Middle East", "Phase 3: மத்திய கிழக்கு"),
        "tab_un_p5" to StringPair("UN P5 & Strategic", "ஐ.நா. P5 & வல்லரசுகள்"),
        "tab_un_languages" to StringPair("UN 6 Languages", "ஐ.நா. 6 அதிகாரப்பூர்வ மொழிகள்"),
        "legal_system" to StringPair("Legal System", "சட்டக் கட்டமைப்பு"),
        "primary_legal_languages" to StringPair("Primary Legal Languages", "முதன்மை சட்ட மொழிகள்"),
        "apex_court" to StringPair("Apex Judicial Body", "உயர்நீதிமன்றம் / உச்ச நீதிமன்றம்"),
        "emergency_contacts" to StringPair("Emergency Helplines & Portals", "அவசர தொடர்பு & இணைய தளங்கள்"),
        "electronic_evidence_rule" to StringPair("Electronic Evidence Standard", "டிஜிட்டல் சான்றுகள் சட்ட விதிமுறை"),
        "cross_border_diaspora" to StringPair("Diaspora & Consular Relief", "டயஸ்போரா & தூதரக உதவிகள்"),
        "official_efiling_portal" to StringPair("Official e-Filing Portal", "அதிகாரப்பூர்வ நீதிமன்ற பதிவுத் தளம்"),
        "police_emergency" to StringPair("Police Emergency", "காவல்துறை அவசர எண்"),
        "cybercrime_helpline" to StringPair("Cyber Crime Hotline", "சைபர் கிரைம் உதவி எண்"),
        "labor_consumer_helpline" to StringPair("Labor / Consumer Help", "தொழிலாளர் / நுகர்வோர் உதவி"),
        "un_p5_badge" to StringPair("UN Security Council P5 Member", "ஐ.நா. பாதுகாப்பு கவுன்சில் P5 நிரந்தர நாடு"),
        "diaspora_support" to StringPair("Tamil & Indian Diaspora Support", "தமிழ் & இந்திய டயஸ்போரா ஆதரவு"),
        "jurisdiction_switched_success" to StringPair("Active jurisdiction changed to", "சட்ட எல்லை மாற்றப்பட்டது:")
    )
}
