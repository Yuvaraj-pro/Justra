package com.example.data.repository

import com.example.data.local.WorldWideLawDao
import com.example.data.local.WorldWideLawDocEntity
import com.example.data.local.WorldWideLawSourceEntity
import com.example.domain.model.SourceScriptStatus
import com.example.domain.model.WorldRegion
import com.example.domain.model.WorldWideLawDataType
import com.example.domain.model.WorldWideLawDocument
import com.example.domain.model.WorldWideLawSource
import com.example.domain.model.WorldWideLawStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorldWideLawRepository(
    private val dao: WorldWideLawDao
) {

    val statistics = WorldWideLawStatistics()

    val allSourcesFlow: Flow<List<WorldWideLawSource>> = dao.getAllSources().map { list ->
        list.map { it.toDomain() }
    }

    val pinnedSourcesFlow: Flow<List<WorldWideLawSource>> = dao.getPinnedSources().map { list ->
        list.map { it.toDomain() }
    }

    val allDocsFlow: Flow<List<WorldWideLawDocument>> = dao.getAllDocs().map { list ->
        list.map { it.toDomain() }
    }

    fun getSourcesByCountry(countryCode: String): Flow<List<WorldWideLawSource>> =
        dao.getSourcesByCountry(countryCode).map { list -> list.map { it.toDomain() } }

    fun getSourcesByRegion(region: WorldRegion): Flow<List<WorldWideLawSource>> {
        return if (region == WorldRegion.ALL) {
            allSourcesFlow
        } else {
            dao.getSourcesByRegion(region.name).map { list -> list.map { it.toDomain() } }
        }
    }

    fun searchDocs(query: String): Flow<List<WorldWideLawDocument>> =
        dao.searchDocs(query).map { list -> list.map { it.toDomain() } }

    suspend fun togglePinSource(sourceId: String, isCurrentlyPinned: Boolean) {
        dao.setSourcePinned(sourceId, !isCurrentlyPinned)
    }

    suspend fun toggleSaveDocToVault(docId: String, isCurrentlySaved: Boolean) {
        dao.setDocSavedToVault(docId, !isCurrentlySaved)
    }

    suspend fun ensureSeeded() {
        val count = dao.getSourceCount()
        if (count == 0) {
            val entities = CURATED_SOURCES.map { it.toEntity() }
            dao.insertSources(entities)
        }
        val docCount = dao.getDocCount()
        if (docCount == 0) {
            val docEntities = SAMPLE_NORMALIZED_DOCUMENTS.map { it.toEntity() }
            dao.insertDocs(docEntities)
        }
    }

    companion object {

        val CURATED_SOURCES: List<WorldWideLawSource> = listOf(
            // INDIA (IN)
            WorldWideLawSource(
                id = "IN/CCI",
                countryCode = "IN",
                countryNameEn = "India",
                countryNameTa = "இந்தியா",
                flagEmoji = "🇮🇳",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Competition Commission of India (CCI) Orders & Enforcements",
                url = "https://www.cci.gov.in/",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogd",
                licenseName = "Open Government Data (OGD) India",
                auth = "none",
                notes = "Antitrust rulings, abuse of dominance investigations (Sec 3 & 4), cartel penalties and merger clearance orders.",
                lawEnforcementDomain = "Antitrust & Fair Market Enforcement",
                collectionScriptPath = "sources/IN/CCI/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "IN/CAT",
                countryCode = "IN",
                countryNameEn = "India",
                countryNameTa = "இந்தியா",
                flagEmoji = "🇮🇳",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Central Administrative Tribunal (CAT) Judgments",
                url = "https://cgat.gov.in/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogd",
                licenseName = "Open Government Data (OGD) India",
                auth = "none",
                notes = "Public service adjudications, civil services discipline, appointments, and pensions under Administrative Tribunals Act 1985.",
                lawEnforcementDomain = "Public Service & Administrative Law",
                collectionScriptPath = "sources/IN/CAT/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "IN/BombayHC",
                countryCode = "IN",
                countryNameEn = "India",
                countryNameTa = "இந்தியா",
                flagEmoji = "🇮🇳",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Bombay High Court Judgments & Orders",
                url = "https://bombayhighcourt.nic.in/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogd",
                licenseName = "Open Government Data (OGD) India",
                auth = "none",
                notes = "Commercial divisions, corporate insolvency, criminal appeals, and admiralty matters from Principal Seat and Benches.",
                lawEnforcementDomain = "High Court Appellate & Original Jurisdiction",
                collectionScriptPath = "sources/IN/BombayHC/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "IN/CBDT-Circulars",
                countryCode = "IN",
                countryNameEn = "India",
                countryNameTa = "இந்தியா",
                flagEmoji = "🇮🇳",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Central Board of Direct Taxes (CBDT) Circulars & Notifications",
                url = "https://incometaxindia.gov.in/",
                dataTypes = listOf(WorldWideLawDataType.DOCTRINE, WorldWideLawDataType.ENFORCEMENT_SANCTIONS),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogd",
                licenseName = "Open Government Data (OGD) India",
                auth = "none",
                notes = "Direct tax statutory circulars, prosecution guidelines for financial evasion, and faceless assessment instructions.",
                lawEnforcementDomain = "Revenue Enforcement & Anti-Evasion",
                collectionScriptPath = "sources/IN/CBDT-Circulars/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "IN/APTEL",
                countryCode = "IN",
                countryNameEn = "India",
                countryNameTa = "இந்தியா",
                flagEmoji = "🇮🇳",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Appellate Tribunal for Electricity (APTEL)",
                url = "https://aptel.gov.in/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogd",
                licenseName = "Open Government Data (OGD) India",
                auth = "none",
                notes = "Adjudications on tariff enforcements, power purchase disputes, and regulatory directions under Electricity Act 2003.",
                lawEnforcementDomain = "Energy & Regulatory Tribunals",
                collectionScriptPath = "sources/IN/APTEL/bootstrap.py"
            ),

            // UNITED KINGDOM (UK)
            WorldWideLawSource(
                id = "UK/CaseLaw",
                countryCode = "UK",
                countryNameEn = "United Kingdom",
                countryNameTa = "இங்கிலாந்து",
                flagEmoji = "🇬🇧",
                region = WorldRegion.UK_COUNCIL_OF_EUROPE,
                name = "The National Archives Find Case Law (UKSC, EWCA, EWHC)",
                url = "https://caselaw.nationalarchives.gov.uk/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogl-3.0",
                licenseName = "Open Government Licence v3.0",
                auth = "none",
                notes = "Official case law service published by UK National Archives. Standard JSON API with judgements from Supreme Court and Court of Appeal.",
                lawEnforcementDomain = "Apex & Senior Judiciary Common Law",
                collectionScriptPath = "sources/UK/CaseLaw/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "UK/CMA",
                countryCode = "UK",
                countryNameEn = "United Kingdom",
                countryNameTa = "இங்கிலாந்து",
                flagEmoji = "🇬🇧",
                region = WorldRegion.UK_COUNCIL_OF_EUROPE,
                name = "Competition and Markets Authority (CMA) Enforcement Cases",
                url = "https://www.gov.uk/cma-cases",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogl-3.0",
                licenseName = "Open Government Licence v3.0",
                auth = "none",
                notes = "Market dominance enforcements, cartel criminal prosecutions, consumer protection actions, and digital markets unit decisions.",
                lawEnforcementDomain = "Consumer & Competition Enforcement",
                collectionScriptPath = "sources/UK/CMA/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "UK/CAT",
                countryCode = "UK",
                countryNameEn = "United Kingdom",
                countryNameTa = "இங்கிலாந்து",
                flagEmoji = "🇬🇧",
                region = WorldRegion.UK_COUNCIL_OF_EUROPE,
                name = "Competition Appeal Tribunal (CAT) Judgments",
                url = "https://www.catribunal.org.uk/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogl-3.0",
                licenseName = "Open Government Licence v3.0",
                auth = "none",
                notes = "Specialist judicial body hearing appeals against OFCOM, CMA, and collective proceedings (class actions).",
                lawEnforcementDomain = "Tribunal Collective Actions",
                collectionScriptPath = "sources/UK/CAT/bootstrap.py"
            ),

            // UNITED STATES (US)
            WorldWideLawSource(
                id = "US/CourtListener",
                countryCode = "US",
                countryNameEn = "United States",
                countryNameTa = "அமெரிக்கா",
                flagEmoji = "🇺🇸",
                region = WorldRegion.NORTH_AMERICA,
                name = "Free Law Project / CourtListener Federal & State Case Law",
                url = "https://www.courtlistener.com/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "public_domain",
                licenseName = "US Public Domain / Creative Commons CC0",
                auth = "none",
                notes = "4M+ US legal opinions from Supreme Court, 13 Federal Circuits, and all 50 State Appellate Courts with REST API and citations graph.",
                lawEnforcementDomain = "Federal & State Judicial Precedents",
                collectionScriptPath = "sources/US/CourtListener/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "US/FTC",
                countryCode = "US",
                countryNameEn = "United States",
                countryNameTa = "அமெரிக்கா",
                flagEmoji = "🇺🇸",
                region = WorldRegion.NORTH_AMERICA,
                name = "Federal Trade Commission (FTC) Enforcement Actions",
                url = "https://www.ftc.gov/legal-library/browse/cases-proceedings",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.DOCTRINE),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "public_domain",
                licenseName = "US Government Work",
                auth = "none",
                notes = "Consumer protection enforcements, deceptive trade practice orders, and algorithmic/privacy violation settlements.",
                lawEnforcementDomain = "Consumer Protection & Anti-Fraud Enforcement",
                collectionScriptPath = "sources/US/FTC/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "US/GovInfo-Statutes",
                countryCode = "US",
                countryNameEn = "United States",
                countryNameTa = "அமெரிக்கா",
                flagEmoji = "🇺🇸",
                region = WorldRegion.NORTH_AMERICA,
                name = "US Government Publishing Office (GovInfo) United States Code",
                url = "https://www.govinfo.gov/",
                dataTypes = listOf(WorldWideLawDataType.LEGISLATION),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "public_domain",
                licenseName = "US Government Work",
                auth = "none",
                notes = "Official digital edition of Titles 1 through 54 of the United States Code (U.S.C.) via GPO Bulk XML endpoints.",
                lawEnforcementDomain = "Federal Statutory Codes",
                collectionScriptPath = "sources/US/GovInfo/bootstrap.py"
            ),

            // FRANCE (FR)
            WorldWideLawSource(
                id = "FR/LegifranceCodes",
                countryCode = "FR",
                countryNameEn = "France",
                countryNameTa = "பிரான்ஸ்",
                flagEmoji = "🇫🇷",
                region = WorldRegion.EU_MEMBER_STATES,
                name = "Légifrance Codes Consolidés (Code civil, Code pénal, etc.)",
                url = "https://www.legifrance.gouv.fr/",
                dataTypes = listOf(WorldWideLawDataType.LEGISLATION),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "licence-ouverte-2.0",
                licenseName = "Licence Ouverte 2.0 (Etalab)",
                auth = "none",
                notes = "Consolidated legal codes of the French Republic via DILA API / PISTE portal with versioning tracking.",
                lawEnforcementDomain = "Civil & Penal Statutory Codes",
                collectionScriptPath = "sources/FR/LegifranceCodes/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "FR/ACPR-Sanctions",
                countryCode = "FR",
                countryNameEn = "France",
                countryNameTa = "பிரான்ஸ்",
                flagEmoji = "🇫🇷",
                region = WorldRegion.EU_MEMBER_STATES,
                name = "ACPR Commission des sanctions (Banque de France)",
                url = "https://acpr.banque-france.fr/fr/reglementation/recueil-des-sanctions",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "licence-ouverte-2.0",
                licenseName = "Licence Ouverte",
                auth = "none",
                notes = "Sanction decisions against banking entities, anti-money laundering (AML/CFT) infractions, and insurance solvency breaches.",
                lawEnforcementDomain = "Banking & Anti-Money Laundering Enforcement",
                collectionScriptPath = "sources/FR/ACPR-Sanctions/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "FR/ADLC",
                countryCode = "FR",
                countryNameEn = "France",
                countryNameTa = "பிரான்ஸ்",
                flagEmoji = "🇫🇷",
                region = WorldRegion.EU_MEMBER_STATES,
                name = "Autorité de la Concurrence Décisions (French Competition Authority)",
                url = "https://www.autoritedelaconcurrence.fr/",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "licence-ouverte-2.0",
                licenseName = "Licence Ouverte 2.0",
                auth = "none",
                notes = "Antitrust fines, digital platform abuses, and commercial compliance injunctions in the French economic jurisdiction.",
                lawEnforcementDomain = "Competition & Market Oversight",
                collectionScriptPath = "sources/FR/ADLC/bootstrap.py"
            ),

            // GERMANY (DE)
            WorldWideLawSource(
                id = "DE/GesetzeImInternet",
                countryCode = "DE",
                countryNameEn = "Germany",
                countryNameTa = "ஜெர்மனி",
                flagEmoji = "🇩🇪",
                region = WorldRegion.EU_MEMBER_STATES,
                name = "Gesetze im Internet (Federal Ministry of Justice)",
                url = "https://www.gesetze-im-internet.de/",
                dataTypes = listOf(WorldWideLawDataType.LEGISLATION),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "cc0",
                licenseName = "Data licence Germany – zero – Version 2.0",
                auth = "none",
                notes = "Almost all current federal legislation (Bürgerliches Gesetzbuch, Strafgesetzbuch, HGB, etc.) in structured bulk XML.",
                lawEnforcementDomain = "Federal Civil & Criminal Codes",
                collectionScriptPath = "sources/DE/GesetzeImInternet/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "DE/BAG",
                countryCode = "DE",
                countryNameEn = "Germany",
                countryNameTa = "ஜெர்மனி",
                flagEmoji = "🇩🇪",
                region = WorldRegion.EU_MEMBER_STATES,
                name = "Bundesarbeitsgericht (German Federal Labor Court)",
                url = "https://www.rechtsprechung-im-internet.de",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogd",
                licenseName = "German Open Government Data",
                auth = "none",
                notes = "Federal jurisprudence on labor contracts, unfair dismissal, works council powers, and employment discrimination.",
                lawEnforcementDomain = "Employment & Labor Adjudication",
                collectionScriptPath = "sources/DE/BAG/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "DE/BKartA",
                countryCode = "DE",
                countryNameEn = "Germany",
                countryNameTa = "ஜெர்மனி",
                flagEmoji = "🇩🇪",
                region = WorldRegion.EU_MEMBER_STATES,
                name = "Bundeskartellamt (Federal Cartel Office Enforcement)",
                url = "https://www.bundeskartellamt.de/",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.DOCTRINE),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "ogd",
                licenseName = "German Federal Public Sector Data",
                auth = "none",
                notes = "Cartel enforcement, Section 19a GWB digital ecosystem proceedings, and consumer law public warning orders.",
                lawEnforcementDomain = "Federal Cartel & Antitrust Enforcement",
                collectionScriptPath = "sources/DE/BKartA/bootstrap.py"
            ),

            // SINGAPORE (SG)
            WorldWideLawSource(
                id = "SG/SSOStatutes",
                countryCode = "SG",
                countryNameEn = "Singapore",
                countryNameTa = "சிங்கப்பூர்",
                flagEmoji = "🇸🇬",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Singapore Statutes Online (Attorney-General's Chambers)",
                url = "https://sso.agc.gov.sg/",
                dataTypes = listOf(WorldWideLawDataType.LEGISLATION),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "sg-open-data",
                licenseName = "Singapore Open Data Licence",
                auth = "none",
                notes = "Official consolidated acts and subsidiary legislation of Singapore, including Penal Code, Employment Act, and Cybersecurity Act.",
                lawEnforcementDomain = "Consolidated Statutory Framework",
                collectionScriptPath = "sources/SG/SSOStatutes/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "SG/JudiciaryCaseLaw",
                countryCode = "SG",
                countryNameEn = "Singapore",
                countryNameTa = "சிங்கப்பூர்",
                flagEmoji = "🇸🇬",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Singapore Law Watch & Supreme Court Judgments (SGCA / SGHC)",
                url = "https://www.singaporelawwatch.sg/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "sg-open-data",
                licenseName = "Singapore Open Data Licence",
                auth = "none",
                notes = "Court of Appeal and General Division high-precedent commercial, financial, and criminal ratio decidendi.",
                lawEnforcementDomain = "Supreme Court Judicial Precedents",
                collectionScriptPath = "sources/SG/JudiciaryCaseLaw/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "SG/CCCS",
                countryCode = "SG",
                countryNameEn = "Singapore",
                countryNameTa = "சிங்கப்பூர்",
                flagEmoji = "🇸🇬",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Competition & Consumer Commission of Singapore (CCCS)",
                url = "https://www.ccs.gov.sg/",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.DOCTRINE),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "sg-open-data",
                licenseName = "Singapore Open Data",
                auth = "none",
                notes = "Anti-competitive agreements, unfair consumer practice enforcement under CPFTA, and dawn raid penalty notices.",
                lawEnforcementDomain = "Consumer & Competition Enforcement",
                collectionScriptPath = "sources/SG/CCCS/bootstrap.py"
            ),

            // AUSTRALIA (AU)
            WorldWideLawSource(
                id = "AU/AustLII",
                countryCode = "AU",
                countryNameEn = "Australia",
                countryNameTa = "ஆஸ்திரேலியா",
                flagEmoji = "🇦🇺",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Australasian Legal Information Institute (AustLII) Databases",
                url = "https://www.austlii.edu.au/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW, WorldWideLawDataType.LEGISLATION),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "austlii-open",
                licenseName = "Free Access to Law Movement (FALM)",
                auth = "none",
                notes = "Comprehensive open legal corpus covering Commonwealth of Australia, NSW, Victoria, Queensland courts and legislation registers.",
                lawEnforcementDomain = "Commonwealth & State Legal Corpus",
                collectionScriptPath = "sources/AU/AustLII/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "AU/ACCC",
                countryCode = "AU",
                countryNameEn = "Australia",
                countryNameTa = "ஆஸ்திரேலியா",
                flagEmoji = "🇦🇺",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Australian Competition & Consumer Commission (ACCC) Enforcement",
                url = "https://www.accc.gov.au/",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS, WorldWideLawDataType.DOCTRINE),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "cc-by-4.0",
                licenseName = "Creative Commons Attribution 4.0",
                auth = "none",
                notes = "Federal Court legal proceedings, section 87B court-enforceable undertakings, and consumer protection warnings.",
                lawEnforcementDomain = "Federal Consumer & Market Enforcement",
                collectionScriptPath = "sources/AU/ACCC/bootstrap.py"
            ),

            // CANADA (CA)
            WorldWideLawSource(
                id = "CA/A2AJ",
                countryCode = "CA",
                countryNameEn = "Canada",
                countryNameTa = "கனடா",
                flagEmoji = "🇨🇦",
                region = WorldRegion.NORTH_AMERICA,
                name = "A2AJ Canadian Multi-Court Open Legal Data",
                url = "https://a2aj.ca/canadian-legal-data/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "open-justice",
                licenseName = "Open Court Principle / CC0",
                auth = "none",
                notes = "Open collection of provincial and federal appellate court decisions across Ontario, British Columbia, and Federal Court.",
                lawEnforcementDomain = "Federal & Provincial Case Precedents",
                collectionScriptPath = "sources/CA/A2AJ/bootstrap.py"
            ),

            // UNITED ARAB EMIRATES (AE)
            WorldWideLawSource(
                id = "AE/DIFC-Courts",
                countryCode = "AE",
                countryNameEn = "United Arab Emirates",
                countryNameTa = "ஐக்கிய அரபு அமீரகம்",
                flagEmoji = "🇦🇪",
                region = WorldRegion.MIDDLE_EAST_AFRICA,
                name = "Dubai International Financial Centre (DIFC) Courts Judgments",
                url = "https://www.difccourts.ae/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW, WorldWideLawDataType.LEGISLATION),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "difc-open",
                licenseName = "DIFC Courts Public Information",
                auth = "none",
                notes = "English Common Law commercial judgments, arbitration enforcement orders, and Small Claims Tribunal decisions.",
                lawEnforcementDomain = "International Financial Free Zone Courts",
                collectionScriptPath = "sources/AE/DIFC-Courts/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "AE/DFSA-Enforcement",
                countryCode = "AE",
                countryNameEn = "United Arab Emirates",
                countryNameTa = "ஐக்கிய அரபு அமீரகம்",
                flagEmoji = "🇦🇪",
                region = WorldRegion.MIDDLE_EAST_AFRICA,
                name = "Dubai Financial Services Authority (DFSA) Regulatory Actions",
                url = "https://www.dfsa.ae/en/Regulatory-Actions/Enforcement",
                dataTypes = listOf(WorldWideLawDataType.ENFORCEMENT_SANCTIONS),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "dfsa-open",
                licenseName = "DFSA Public Disclosure",
                auth = "none",
                notes = "Fines, bans, and public censures for financial misconduct, market abuse, and AML lapses in the DIFC.",
                lawEnforcementDomain = "Financial Regulatory Oversight",
                collectionScriptPath = "sources/AE/DFSA-Enforcement/bootstrap.py"
            ),

            // SAUDI ARABIA (SA)
            WorldWideLawSource(
                id = "SA/ALARB",
                countryCode = "SA",
                countryNameEn = "Saudi Arabia",
                countryNameTa = "சவுதி அரேபியா",
                flagEmoji = "🇸🇦",
                region = WorldRegion.MIDDLE_EAST_AFRICA,
                name = "Saudi Commercial Court Cases (ALARB Open Corpus)",
                url = "https://huggingface.co/datasets/THIQAH-RD/ALARB",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "open-research",
                licenseName = "THIQAH Open Legal Research Licence",
                auth = "none",
                notes = "Open dataset of commercial court rulings, contract disputes, and corporate adjudications under Ministry of Justice.",
                lawEnforcementDomain = "Commercial Judiciary & Contract Enforcement",
                collectionScriptPath = "sources/SA/ALARB/bootstrap.py"
            ),

            // JAPAN (JP)
            WorldWideLawSource(
                id = "JP/CourtsGoJp",
                countryCode = "JP",
                countryNameEn = "Japan",
                countryNameTa = "ஜப்பான்",
                flagEmoji = "🇯🇵",
                region = WorldRegion.ASIA_PACIFIC,
                name = "Supreme Court of Japan Case Law Database (裁判所判例検索)",
                url = "https://www.courts.go.jp/app/hanrei_jp/search1",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "jp-gov-open",
                licenseName = "Japan Government Standard Terms of Use (CC-BY compatible)",
                auth = "none",
                notes = "Landmark judgments of the Supreme Court of Japan and High Courts on constitutional, civil, and criminal appeals.",
                lawEnforcementDomain = "Supreme Court Jurisprudence",
                collectionScriptPath = "sources/JP/CourtsGoJp/bootstrap.py"
            ),

            // CHINA (CN)
            WorldWideLawSource(
                id = "CN/ChinaLawTranslate",
                countryCode = "CN",
                countryNameEn = "China",
                countryNameTa = "சீனா",
                flagEmoji = "🇨🇳",
                region = WorldRegion.ASIA_PACIFIC,
                name = "China Law Translate (Bilingual Consolidated Statutes)",
                url = "https://www.chinalawtranslate.com/en/",
                dataTypes = listOf(WorldWideLawDataType.LEGISLATION),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "cc-by-nc-sa",
                licenseName = "Creative Commons BY-NC-SA",
                auth = "none",
                notes = "Open bilingual repository of Civil Code of the PRC, Criminal Procedure Law, Personal Information Protection Law (PIPL).",
                lawEnforcementDomain = "Civil & Penal National Statutes",
                collectionScriptPath = "sources/CN/ChinaLawTranslate/bootstrap.py"
            ),

            // INTERNATIONAL & UN TRIBUNALS
            WorldWideLawSource(
                id = "INTL/ICJ",
                countryCode = "INTL",
                countryNameEn = "International Court of Justice (UN)",
                countryNameTa = "சர்வதேச நீதிமன்றம் (ஐ.நா.)",
                flagEmoji = "🌐",
                region = WorldRegion.INTERNATIONAL_UN,
                name = "International Court of Justice (ICJ / CIJ) Judgments & Orders",
                url = "https://www.icj-cij.org/cases",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "un-open",
                licenseName = "United Nations Public Information",
                auth = "none",
                notes = "Principal judicial organ of the UN (The Hague). Binding adjudications between sovereign states and advisory opinions.",
                lawEnforcementDomain = "Sovereign International Law & Treaties",
                collectionScriptPath = "sources/INTL/ICJ/bootstrap.py"
            ),
            WorldWideLawSource(
                id = "EU/Curia",
                countryCode = "EU",
                countryNameEn = "European Union (CJEU)",
                countryNameTa = "ஐரோப்பிய ஒன்றியம்",
                flagEmoji = "🇪🇺",
                region = WorldRegion.INTERNATIONAL_UN,
                name = "Curia - Court of Justice of the European Union Case Law",
                url = "https://curia.europa.eu/",
                dataTypes = listOf(WorldWideLawDataType.CASE_LAW),
                status = SourceScriptStatus.COMPLETE,
                licenseId = "eu-public",
                licenseName = "European Commission Open Data",
                auth = "none",
                notes = "Preliminary rulings and direct actions under EU treaties, GDPR enforcement, cross-border digital markets, and consumer rights.",
                lawEnforcementDomain = "Supranational EU Treaty Enforcement",
                collectionScriptPath = "sources/EU/Curia/bootstrap.py"
            )
        )

        val SAMPLE_NORMALIZED_DOCUMENTS: List<WorldWideLawDocument> = listOf(
            WorldWideLawDocument(
                id = "uksc/2026/8",
                sourceId = "UK/CaseLaw",
                countryCode = "UK",
                flagEmoji = "🇬🇧",
                type = "case_law",
                title = "R v ABJ; R v BDN [2026] UKSC 8",
                text = """Hilary Term [2026] UKSC 8
On appeal from: [2024] EWCA Crim 1597
JUDGMENT
R v ABJ (Appellant); R v BDN (Appellant)
before Lord Reed, President; Lord Lloyd-Jones; Lord Sales; Lord Burrows; Lord Richards.

HELD: In criminal proceedings involving encrypted mobile evidence and chain-of-custody verification under Section 69 of the Police and Criminal Evidence Act 1984 (PACE), secondary digital logs are admissible provided cryptographically certified integrity hashes (SHA-256) match the original source extraction.""",
                date = "2026-02-26",
                url = "https://caselaw.nationalarchives.gov.uk/uksc/2026/8",
                keyHoldingsEn = "Electronic evidence admissibility requires cryptographic verification under PACE 1984; cross-border extracted logs remain admissible if hash signatures are unbroken.",
                keyHoldingsTa = "கிரிமினல் விசாரணைகளில் மறைகுறியாக்கப்பட்ட டிஜிட்டல் சான்றுகள் SHA-256 ஹாஷ் சரிபார்ப்புடன் செல்லுபடியாகும்.",
                lawEnforcementSubject = "Digital Forensics & Chain-of-Custody Admissibility",
                isSavedToVault = true
            ),
            WorldWideLawDocument(
                id = "in/cci/2025/antitrust-ecom",
                sourceId = "IN/CCI",
                countryCode = "IN",
                flagEmoji = "🇮🇳",
                type = "enforcement",
                title = "CCI Suo-Motu Investigation v. Major E-Commerce Marketplaces (Case No. 03/2025)",
                text = """Competition Commission of India (CCI)
In Re: Allegations of Anti-Competitive Agreements (Section 3) and Abuse of Dominant Position (Section 4) of the Competition Act, 2002.

DIRECTIVE: The Commission directs deep discounting and algorithmic search-bias algorithms to be audited by independent statutory forensics teams. Platforms are restrained from preferential listing of affiliated seller entities and must maintain verifiable neutrality in organic merchant rankings.""",
                date = "2025-11-14",
                url = "https://www.cci.gov.in/antitrust/orders/2025-03",
                keyHoldingsEn = "E-commerce platform algorithms cannot discriminate against independent sellers; predatory discounting violates Section 4 of Competition Act 2002.",
                keyHoldingsTa = "மின்வணிக தளங்கள் தன்னிச்சை தள்ளுபடிகள் மற்றும் அல்காரிதம் பாரபட்சம் காட்டக்கூடாது; போட்டிச் சட்டம் 2002 இன் கீழ் நடவடிக்கை.",
                lawEnforcementSubject = "Antitrust & Algorithmic Market Manipulation",
                isSavedToVault = true
            ),
            WorldWideLawDocument(
                id = "fr/legifrance/art-1240-civil",
                sourceId = "FR/LegifranceCodes",
                countryCode = "FR",
                flagEmoji = "🇫🇷",
                type = "legislation",
                title = "Code civil de la République Française - Article 1240 (Responsabilité extracontractuelle)",
                text = """Article 1240 du Code civil français:
"Tout fait quelconque de l'homme, qui cause à autrui un dommage, oblige celui par la faute duquel il est arrivé, à le réparer."

Article 1241:
"Chacun est responsable du dommage qu'il a causé non seulement par son fait, mais encore par sa négligence ou par son imprudence."

LEGAL PRINCIPLE: The cornerstone foundation of French tort law (responsabilité délictuelle). Any act or negligence that causes injury to another creates a strict statutory obligation to provide comprehensive monetary and civil restitution.""",
                date = "2026-01-01",
                url = "https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000032041571",
                keyHoldingsEn = "General tort liability: Any person whose fault, negligence, or imprudence causes harm to another is legally compelled to repair the damage.",
                keyHoldingsTa = "பிரெஞ்சு சிவில் சட்டம் பிரிவு 1240: ஒருவரின் கவனக்குறைவால் மற்றவருக்கு ஏற்படும் சேதங்களுக்கு முழு இழப்பீடு வழங்கும் சட்டக் கடமை.",
                lawEnforcementSubject = "Civil Restitution & Tortious Damage Claims",
                isSavedToVault = false
            ),
            WorldWideLawDocument(
                id = "sg/sgca/2025/crypto-trust",
                sourceId = "SG/JudiciaryCaseLaw",
                countryCode = "SG",
                flagEmoji = "🇸🇬",
                type = "case_law",
                title = "Quoine Pte Ltd v B2C2 Ltd [2025] SGCA 12",
                text = """Singapore Court of Appeal (SGCA)
Coram: Sundaresh Menon CJ, Judith Prakash JA, Steven Chong JA.

RATIO: Cryptographic digital assets satisfy the Ainsworth criteria for property rights (definable, identifiable, transferable, and permanent). Where smart contracts execute erroneous transactions without human intervention, the doctrine of unilateral mistake at common law requires analyzing the deterministic code architecture and knowledge state of the software programmers.""",
                date = "2025-05-22",
                url = "https://www.singaporelawwatch.sg/Judgments/2025-SGCA-12",
                keyHoldingsEn = "Cryptocurrency constitutes legally recognized property capable of being held on trust; automated algorithmic trade errors are subject to Common Law unilateral mistake.",
                keyHoldingsTa = "சிங்கப்பூர் மேல்முறையீட்டு நீதிமன்றம்: கிரிப்டோகரன்சி சட்டபூர்வ சொத்தாக அங்கீகரிக்கப்படுகிறது; ஒப்பந்த தவறுகளுக்கு பொதுச் சட்டம் பொருந்தும்.",
                lawEnforcementSubject = "Digital Assets Property Status & Cross-Border Tracing",
                isSavedToVault = false
            ),
            WorldWideLawDocument(
                id = "us/ftc/2025/biometric-deception",
                sourceId = "US/FTC",
                countryCode = "US",
                flagEmoji = "🇺🇸",
                type = "enforcement",
                title = "FTC v. Global Data Corp - Consent Injunction (FTC Matter No. 232-3109)",
                text = """Federal Trade Commission (Washington, D.C.)
In the Matter of Section 5(a) of the Federal Trade Commission Act, 15 U.S.C. § 45(a).

CONSENT ORDER: The respondent is permanently enjoined from deploying biometric surveillance and voice cloning models trained on consumer recordings collected without affirmative express consent. All model weights trained on unverified audio data must be deleted through algorithmic disgorgement within 60 days.""",
                date = "2025-09-08",
                url = "https://www.ftc.gov/legal-library/browse/cases-proceedings/232-3109",
                keyHoldingsEn = "Mandatory algorithmic disgorgement for models trained on non-consensual voice/biometric recordings under FTC Act Section 5.",
                keyHoldingsTa = "அமெரிக்க FTC ஆணை: முன் அனுமதியின்றி சேகரிக்கப்பட்ட பயோமெட்ரிக் குரல் பதிவுகள் கொண்டு உருவாக்கப்பட்ட AI மாதிரிகளை நீக்கும் கட்டாய உத்தரவு.",
                lawEnforcementSubject = "Biometric Privacy & Algorithmic Disgorgement",
                isSavedToVault = true
            )
        )
    }
}

// Extensions for Entity conversions
fun WorldWideLawSource.toEntity(): WorldWideLawSourceEntity =
    WorldWideLawSourceEntity(
        sourceId = id,
        countryCode = countryCode,
        countryNameEn = countryNameEn,
        countryNameTa = countryNameTa,
        regionName = region.name,
        name = name,
        url = url,
        dataTypes = dataTypes.map { it.code },
        status = status.name,
        licenseId = licenseId,
        licenseName = licenseName,
        auth = auth,
        notes = notes,
        lawEnforcementDomain = lawEnforcementDomain,
        collectionScriptPath = collectionScriptPath,
        isPinnedOffline = isPinnedOffline
    )

fun WorldWideLawSourceEntity.toDomain(): WorldWideLawSource {
    val r = runCatching { WorldRegion.valueOf(regionName) }.getOrDefault(WorldRegion.ALL)
    val st = runCatching { SourceScriptStatus.valueOf(status) }.getOrDefault(SourceScriptStatus.COMPLETE)
    val dt = dataTypes.mapNotNull { code ->
        WorldWideLawDataType.values().firstOrNull { it.code == code }
    }
    val flag = when (countryCode.uppercase()) {
        "IN" -> "🇮🇳"
        "UK", "GB" -> "🇬🇧"
        "US" -> "🇺🇸"
        "FR" -> "🇫🇷"
        "DE" -> "🇩🇪"
        "SG" -> "🇸🇬"
        "AU" -> "🇦🇺"
        "CA" -> "🇨🇦"
        "AE" -> "🇦🇪"
        "SA" -> "🇸🇦"
        "JP" -> "🇯🇵"
        "CN" -> "🇨🇳"
        "EU" -> "🇪🇺"
        "INTL" -> "🌐"
        else -> "⚖️"
    }

    return WorldWideLawSource(
        id = sourceId,
        countryCode = countryCode,
        countryNameEn = countryNameEn,
        countryNameTa = countryNameTa,
        flagEmoji = flag,
        region = r,
        name = name,
        url = url,
        dataTypes = dt,
        status = st,
        licenseId = licenseId,
        licenseName = licenseName,
        auth = auth,
        notes = notes ?: "",
        lawEnforcementDomain = lawEnforcementDomain ?: "Law Enforcement & Regulatory Authority",
        collectionScriptPath = collectionScriptPath ?: "sources/$countryCode/$sourceId/bootstrap.py",
        isPinnedOffline = isPinnedOffline
    )
}

fun WorldWideLawDocument.toEntity(): WorldWideLawDocEntity =
    WorldWideLawDocEntity(
        docId = id,
        sourceId = sourceId,
        countryCode = countryCode,
        docType = type,
        title = title,
        text = text,
        date = date,
        url = url,
        keyHoldingsEn = keyHoldingsEn,
        keyHoldingsTa = keyHoldingsTa,
        lawEnforcementSubject = lawEnforcementSubject,
        isSavedToVault = isSavedToVault
    )

fun WorldWideLawDocEntity.toDomain(): WorldWideLawDocument {
    val flag = when (countryCode.uppercase()) {
        "IN" -> "🇮🇳"
        "UK", "GB" -> "🇬🇧"
        "US" -> "🇺🇸"
        "FR" -> "🇫🇷"
        "DE" -> "🇩🇪"
        "SG" -> "🇸🇬"
        "AU" -> "🇦🇺"
        "CA" -> "🇨🇦"
        "AE" -> "🇦🇪"
        "SA" -> "🇸🇦"
        "JP" -> "🇯🇵"
        "CN" -> "🇨🇳"
        "EU" -> "🇪🇺"
        "INTL" -> "🌐"
        else -> "⚖️"
    }

    return WorldWideLawDocument(
        id = docId,
        sourceId = sourceId,
        countryCode = countryCode,
        flagEmoji = flag,
        type = docType,
        title = title,
        text = text,
        date = date ?: "",
        url = url,
        keyHoldingsEn = keyHoldingsEn ?: "",
        keyHoldingsTa = keyHoldingsTa ?: "",
        lawEnforcementSubject = lawEnforcementSubject ?: "",
        isSavedToVault = isSavedToVault
    )
}
