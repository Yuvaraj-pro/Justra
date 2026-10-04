package com.justra.app.domain.model

enum class WorldRegion(
    val titleEn: String,
    val titleTa: String,
    val descriptionEn: String,
    val descriptionTa: String
) {
    ALL(
        titleEn = "All Jurisdictions",
        titleTa = "அனைத்து சட்ட எல்லைகள்",
        descriptionEn = "Global open legal data from 110+ countries",
        descriptionTa = "110+ நாடுகளின் உலகளாவிய திறந்தநிலை சட்டத் தரவுகள்"
    ),
    ASIA_PACIFIC(
        titleEn = "Asia-Pacific",
        titleTa = "ஆசிய-பசிபிக்",
        descriptionEn = "India, Singapore, Australia, Japan, China, New Zealand, etc.",
        descriptionTa = "இந்தியா, சிங்கப்பூர், ஆஸ்திரேலியா, ஜப்பான், சீனா போன்றவை"
    ),
    NORTH_AMERICA(
        titleEn = "North America",
        titleTa = "வட அமெரிக்கா",
        descriptionEn = "United States federal/state courts & Canada provincial statutes",
        descriptionTa = "அமெரிக்க கூட்டாட்சி/மாநில நீதிமன்றங்கள் & கனடா சட்டங்கள்"
    ),
    UK_COUNCIL_OF_EUROPE(
        titleEn = "UK & Council of Europe",
        titleTa = "இங்கிலாந்து & ஐரோப்பிய கவுன்சில்",
        descriptionEn = "United Kingdom (England, Scotland, Wales, NI), Turkey, Ukraine",
        descriptionTa = "இங்கிலாந்து, வேல்ஸ், ஸ்காட்லாந்து, துருக்கி, உக்ரைன்"
    ),
    EU_MEMBER_STATES(
        titleEn = "EU Member States",
        titleTa = "ஐரோப்பிய ஒன்றிய நாடுகள்",
        descriptionEn = "France, Germany, Italy, Spain, Netherlands, Austria, etc.",
        descriptionTa = "பிரான்ஸ், ஜெர்மனி, இத்தாலி, ஸ்பெயின், நெதர்லாந்து போன்றவை"
    ),
    MIDDLE_EAST_AFRICA(
        titleEn = "Middle East & Africa",
        titleTa = "மத்திய கிழக்கு & ஆப்பிரிக்கா",
        descriptionEn = "UAE (DIFC/ADGM), Saudi Arabia, South Africa, Nigeria, Egypt",
        descriptionTa = "ஐக்கிய அரபு அமீரகம், சவுதி அரேபியா, தென் ஆப்பிரிக்கா, நைஜீரியா"
    ),
    LATIN_AMERICA(
        titleEn = "Latin America",
        titleTa = "லத்தீன் அமெரிக்கா",
        descriptionEn = "Brazil, Mexico, Argentina, Chile, Colombia, Peru",
        descriptionTa = "பிரேசில், மெக்சிகோ, அர்ஜென்டினா, சிலி போன்றவை"
    ),
    INTERNATIONAL_UN(
        titleEn = "International & UN",
        titleTa = "சர்வதேச & ஐ.நா. தீர்ப்பாயங்கள்",
        descriptionEn = "ICJ, ICC, CJEU (Curia), ECHR, WTO and multilateral treaties",
        descriptionTa = "சர்வதேச நீதிமன்றம் (ICJ), ஐரோப்பிய ஒன்றிய நீதிமன்றம் (Curia)"
    )
}

enum class WorldWideLawDataType(
    val code: String,
    val titleEn: String,
    val titleTa: String
) {
    LEGISLATION("legislation", "Legislation & Acts", "சட்டங்கள் & குறியீடுகள்"),
    CASE_LAW("case_law", "Case Law & Judgments", "நீதிமன்றத் தீர்ப்புகள்"),
    ENFORCEMENT_SANCTIONS("enforcement", "Enforcement & Sanctions", "ஒழுங்குமுறை நடவடிக்கைகள் & தடைகள்"),
    DOCTRINE("doctrine", "Regulatory Doctrine & Circulars", "சுற்றறிக்கைகள் & வழிகாட்டுதல்கள்"),
    OFFICIAL_GAZETTE("gazette", "Official Gazettes", "அரசிதழ் அறிவிப்புகள்")
}

enum class SourceScriptStatus(
    val titleEn: String,
    val titleTa: String,
    val colorHex: Long
) {
    COMPLETE("Script Complete", "ஸ்கிரிப்ட் தயார்", 0xFF2E7D32),
    TESTED("Sample Tested", "மாதிரி சோதிக்கப்பட்டது", 0xFF0277BD),
    IN_PROGRESS("In Progress", "செயலில் உள்ளது", 0xFFF57C00),
    BLOCKED("Needs Browser/SPA Fix", "உலாவி தானியங்கு தேவை", 0xFFC62828)
}

data class WorldWideLawSource(
    val id: String,                         // e.g. "IN/CCI", "FR/LegifranceCodes", "UK/CaseLaw"
    val countryCode: String,                // e.g. "IN", "FR", "UK", "US", "DE", "SG", "AU"
    val countryNameEn: String,
    val countryNameTa: String,
    val flagEmoji: String,
    val region: WorldRegion,
    val name: String,
    val url: String,
    val dataTypes: List<WorldWideLawDataType>,
    val status: SourceScriptStatus,
    val blockedReason: String? = null,
    val commercialUse: Boolean = true,
    val licenseId: String? = "ogd",
    val licenseName: String? = "Open Government Data",
    val auth: String = "none",
    val notes: String,
    val lawEnforcementDomain: String,       // e.g. "Competition & Fair Trade", "Financial & Securities", "Judiciary"
    val collectionScriptPath: String,       // e.g. "sources/IN/CCI/bootstrap.py"
    val isPinnedOffline: Boolean = false
)

data class WorldWideLawDocument(
    val id: String,                         // e.g. "uksc/2026/8", "in-cci-2024-sec3"
    val sourceId: String,                   // e.g. "UK/CaseLaw"
    val countryCode: String,
    val flagEmoji: String,
    val type: String,                       // "case_law", "legislation", "enforcement"
    val title: String,
    val text: String,
    val date: String,
    val url: String,
    val keyHoldingsEn: String,
    val keyHoldingsTa: String,
    val lawEnforcementSubject: String,
    val isSavedToVault: Boolean = false
)

data class WorldWideLawStatistics(
    val totalCountries: Int = 110,
    val totalManifestJurisdictions: Int = 237,
    val totalCollectionScripts: Int = 960,
    val totalManifestEndpoints: Int = 3413,
    val indexedDocumentsCount: String = "16,000,000+",
    val liveDashboardUrl: String = "https://legaldatahunter.com",
    val githubRepoUrl: String = "https://github.com/worldwidelaw/legal-sources"
)
