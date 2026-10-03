package com.nyayamate.app.data

import android.util.Log
import com.example.BuildConfig
import com.example.data.api.GeminiApiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class WorldwideLegalClassifier(private val apiKey: String = BuildConfig.GEMINI_API_KEY) {

    companion object {
        private const val TAG = "WorldwideLegalClassifier"

        val SYSTEM_PROMPT = """
            You are the Worldwide Legal Classifier, Statutory Analysis, and Case Organization Engine for the "Justra" (ஜஸ்ட்ரா) platform.
            Your primary mission is to dynamically identify the user's jurisdiction (country), analyze their specific grievance, match it with the EXACT governing law of that jurisdiction, and generate structured complaint data.

            CRITICAL OPERATIONAL RULES & ZERO-DUPLICATION POLICY:
            1. STRICT ZERO-BOILERPLATE / ZERO-STATIC LAW RULE:
               - NEVER repeat the same legal act across different grievance categories.
               - If the user reports an unpaid rental deposit, you MUST NOT cite the Consumer Protection Act.
               - Categorize and map each incident dynamically to its exact statutory domain:
                 * Tenancy / Rent / Eviction -> Tenancy / Rent Control / Housing Acts
                 * Unpaid Wages / Job Dispute / Wrongful Termination -> Labor / Employment / Wages Acts
                 * Online Banking Fraud / Phishing / OTP Theft -> Information Technology & Cybercrime Acts
                 * Faulty Goods / Delivery Failure / Service Deficiency -> Consumer Protection / Sale of Goods Acts
                 * Harassment / Assault / Threat / Extortion -> Penal / Criminal Codes

            2. DYNAMIC WORLDWIDE JURISDICTION ENGINE:
               - Detect the user's country from the provided user context payload or infer it from the incident context (default to India if unstated).
               - Apply the statutory framework strictly of that sovereign territory:
                 * INDIA (IN):
                   - Tenancy: Model Tenancy Act / State Rent Control Acts (Authority: Rent Authority / Rent Court)
                   - Employment: Payment of Wages Act 1936, Industrial Disputes Act (Authority: Labor Commissioner)
                   - Cybercrime: Information Technology Act 2000 (Sec 43/66), BNS Section 318 (Authority: National Cyber Crime Portal 1930)
                   - Consumer: Consumer Protection Act 2019 (Authority: e-Daakhil / NCH 1915)
                   - Criminal: Bharatiya Nyaya Sanhita (BNS) & BNSS 2023 (Authority: Police / 112)
                 * UNITED STATES (US):
                   - Tenancy: Uniform Residential Landlord and Tenant Act (URLTA) / State Housing Codes
                   - Employment: Fair Labor Standards Act (FLSA), Title VII, State Labor Boards
                   - Cyber: Computer Fraud and Abuse Act (CFAA), FTC Act, IC3 portal
                   - Consumer: FTC Act, Uniform Commercial Code (UCC)
                 * UNITED KINGDOM (UK):
                   - Tenancy: Housing Act 1988, Landlord and Tenant Act
                   - Employment: Employment Rights Act 1996, ACAS
                   - Cyber: Computer Misuse Act 1990, Action Fraud UK
                   - Consumer: Consumer Rights Act 2015
                 * AUSTRALIA (AU):
                   - Consumer: Competition and Consumer Act 2010 (Australian Consumer Law / ACCC)
                   - Tenancy: Residential Tenancies Acts (State-based: NCAT, VCAT, etc.)
                   - Employment: Fair Work Act 2009 (Fair Work Ombudsman)
                   - Cyber: Privacy Act 1988, ReportCyber (cyber.gov.au)
                 * OTHER COUNTRIES:
                   - Dynamically pull the primary statutory act and national grievance authority belonging specifically to that detected country.

            3. LOCALIZATION & BILINGUAL OUTPUT:
               - If the user writes or speaks in Tamil or Tanglish, return the `localizedResponse` in clear, conversational Tamil, while keeping formal statutory names and section titles bilingual for administrative filing.
               - If in English, return all text in professional plain English.

            4. NO SPECULATIVE WIN-RATES:
               - Calculate `readinessScorePercentage` (0 to 100) strictly based on factual completeness:
                 * Identifiable parties named: +20%
                 * Transaction proofs/receipts/agreements mentioned: +30%
                 * Communication records/notices cited: +25%
                 * Clear chronological timeline: +25%
               - NEVER predict judicial win chances.

            REQUIRED JSON OUTPUT SCHEMA:
            {
              "jurisdiction": {
                "countryCode": "string",
                "countryName": "string",
                "legalFamily": "Common Law | Civil Law | Mixed"
              },
              "disputeClassification": {
                "primaryCategory": "Tenancy | Employment | Cybercrime | Consumer | Criminal | Property | Other",
                "subCategory": "string",
                "incidentSummary": "string",
                "applicableStatute": "Exact Act and Section Name",
                "enforcingAuthorityOrPortal": "Exact official portal, tribunal, or department name",
                "helplineNumber": "string or null"
              },
              "caseReadiness": {
                "readinessScorePercentage": 0,
                "availableFacts": ["string"],
                "missingCriticalEvidence": ["string"]
              },
              "actionSteps": [
                {
                  "stepOrder": 1,
                  "stepTitle": "string",
                  "instruction": "string",
                  "portalUrlOrLocation": "string or null"
                }
              ],
              "timelineMilestones": [
                {
                  "dateOrPeriod": "string",
                  "event": "string",
                  "isInferred": false
                }
              ],
              "formalComplaintDraft": {
                "subjectLine": "string",
                "addressedTo": "string",
                "statementOfFacts": "string",
                "demandedRelief": "string"
              },
              "localizedResponse": {
                "language": "Tamil | English",
                "simpleExplanation": "string (பயனருக்கான எளிய நேரடி விளக்கம்)",
                "immediateNextStep": "string (இப்போது உடனடியாக செய்ய வேண்டிய முதல் காரியம்)"
              },
              "disclaimer": "Justra provides automated legal information, case organization, and statutory procedural guidance. It does not constitute formal legal representation or an advocate-client relationship under the Advocates Act or local statutory regulations."
            }
        """.trimIndent()
    }

    suspend fun analyzeCase(
        userProblem: String,
        userContext: UserLegalContext? = null
    ): WorldwideLegalResponse = withContext(Dispatchers.IO) {
        val trimmed = userProblem.trim()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val contextPayload = JSONObject().apply {
                    put("countryCode", userContext?.countryCode ?: "AUTO_DETECT")
                    put("countryName", userContext?.countryName ?: "AUTO_DETECT")
                    put("stateOrRegion", userContext?.stateOrRegion ?: "AUTO_DETECT")
                    put("preferredLanguage", userContext?.preferredLanguage ?: "ta")
                }

                val prompt = """
                    User Context: $contextPayload
                    Incident Narrative:
                    "$trimmed"
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt)),
                            role = "user"
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.2f,
                        responseMimeType = "application/json"
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = SYSTEM_PROMPT))
                    )
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "{}"
                val cleaned = responseText.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                return@withContext parseJsonResponse(cleaned, trimmed, userContext)
            } catch (e: Exception) {
                Log.e(TAG, "Worldwide legal analysis API call failed: ${e.message}", e)
            }
        }

        fallbackAnalyze(trimmed, userContext)
    }

    private fun parseJsonResponse(
        jsonString: String,
        fallbackText: String,
        userContext: UserLegalContext?
    ): WorldwideLegalResponse {
        val root = JSONObject(jsonString)

        val jurObj = root.optJSONObject("jurisdiction") ?: JSONObject()
        val jurisdiction = JurisdictionInfo(
            countryCode = jurObj.optString("countryCode", userContext?.countryCode ?: "IN"),
            countryName = jurObj.optString("countryName", userContext?.countryName ?: "India"),
            legalFamily = jurObj.optString("legalFamily", "Common Law")
        )

        val dispObj = root.optJSONObject("disputeClassification") ?: JSONObject()
        val dispute = DisputeClassification(
            primaryCategory = dispObj.optString("primaryCategory", "Other"),
            subCategory = dispObj.optString("subCategory", "General Legal Grievance"),
            incidentSummary = dispObj.optString("incidentSummary", fallbackText),
            applicableStatute = dispObj.optString("applicableStatute", "Relevant Statutory Framework"),
            enforcingAuthorityOrPortal = dispObj.optString("enforcingAuthorityOrPortal", "Jurisdictional Authority"),
            helplineNumber = if (dispObj.has("helplineNumber") && !dispObj.isNull("helplineNumber")) dispObj.optString("helplineNumber") else null
        )

        val readyObj = root.optJSONObject("caseReadiness") ?: JSONObject()
        val availableFacts = jsonArrayToStringList(readyObj.optJSONArray("availableFacts"))
        val missingEvidence = jsonArrayToStringList(readyObj.optJSONArray("missingCriticalEvidence"))
        val caseReadiness = CaseReadiness(
            readinessScorePercentage = readyObj.optInt("readinessScorePercentage", 45),
            availableFacts = if (availableFacts.isNotEmpty()) availableFacts else listOf("Incident narrative recorded"),
            missingCriticalEvidence = if (missingEvidence.isNotEmpty()) missingEvidence else listOf("Written transactional evidence")
        )

        val actionStepsArray = root.optJSONArray("actionSteps")
        val actionSteps = mutableListOf<ActionStep>()
        if (actionStepsArray != null) {
            for (i in 0 until actionStepsArray.length()) {
                val step = actionStepsArray.optJSONObject(i) ?: continue
                actionSteps.add(
                    ActionStep(
                        stepOrder = step.optInt("stepOrder", i + 1),
                        stepTitle = step.optString("stepTitle", "Step ${i + 1}"),
                        instruction = step.optString("instruction", ""),
                        portalUrlOrLocation = if (step.has("portalUrlOrLocation") && !step.isNull("portalUrlOrLocation")) step.optString("portalUrlOrLocation") else null
                    )
                )
            }
        }

        val timelineArray = root.optJSONArray("timelineMilestones")
        val timelineMilestones = mutableListOf<TimelineMilestone>()
        if (timelineArray != null) {
            for (i in 0 until timelineArray.length()) {
                val item = timelineArray.optJSONObject(i) ?: continue
                timelineMilestones.add(
                    TimelineMilestone(
                        dateOrPeriod = item.optString("dateOrPeriod", "Initial Incident"),
                        event = item.optString("event", ""),
                        isInferred = item.optBoolean("isInferred", false)
                    )
                )
            }
        }

        val draftObj = root.optJSONObject("formalComplaintDraft") ?: JSONObject()
        val complaintDraft = FormalComplaintDraft(
            subjectLine = draftObj.optString("subjectLine", "Formal Legal Grievance"),
            addressedTo = draftObj.optString("addressedTo", dispute.enforcingAuthorityOrPortal),
            statementOfFacts = draftObj.optString("statementOfFacts", fallbackText),
            demandedRelief = draftObj.optString("demandedRelief", "Immediate statutory intervention and appropriate redressal.")
        )

        val locObj = root.optJSONObject("localizedResponse") ?: JSONObject()
        val isTamil = userContext?.preferredLanguage?.startsWith("ta") == true || containsTamilCharacters(fallbackText)
        val localized = LocalizedResponse(
            language = locObj.optString("language", if (isTamil) "Tamil" else "English"),
            simpleExplanation = locObj.optString("simpleExplanation", "உங்கள் புகார் சட்ட ரீதியாக ஆராயப்பட்டு வகைப்படுத்தப்பட்டுள்ளது."),
            immediateNextStep = locObj.optString("immediateNextStep", "ஆவணங்களை தயார் செய்து சம்பந்தப்பட்ட அதிகாரியிடம் சமர்ப்பிக்கவும்.")
        )

        return WorldwideLegalResponse(
            jurisdiction = jurisdiction,
            disputeClassification = dispute,
            caseReadiness = caseReadiness,
            actionSteps = if (actionSteps.isNotEmpty()) actionSteps else listOf(
                ActionStep(1, "Preserve Evidence", "Secure all communications and payment records.", dispute.enforcingAuthorityOrPortal)
            ),
            timelineMilestones = if (timelineMilestones.isNotEmpty()) timelineMilestones else listOf(
                TimelineMilestone("Date of Incident", "Dispute arose as described", false)
            ),
            formalComplaintDraft = complaintDraft,
            localizedResponse = localized,
            disclaimer = root.optString(
                "disclaimer",
                "Justra provides automated legal information, case organization, and statutory procedural guidance. It does not constitute formal legal representation or an advocate-client relationship under the Advocates Act or local statutory regulations."
            )
        )
    }

    private fun jsonArrayToStringList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            val item = array.optString(i, "")
            if (item.isNotBlank()) list.add(item)
        }
        return list
    }

    private fun containsTamilCharacters(text: String): Boolean {
        return text.any { it in '\u0B80'..'\u0BFF' }
    }

    private fun fallbackAnalyze(userProblem: String, userContext: UserLegalContext?): WorldwideLegalResponse {
        val lower = userProblem.lowercase()
        val isTamil = userContext?.preferredLanguage?.startsWith("ta") == true || containsTamilCharacters(userProblem)

        // 1. Detect Country
        val country = when {
            userContext?.countryCode != null -> userContext.countryCode.uppercase()
            lower.contains("usa") || lower.contains("united states") || lower.contains("california") || lower.contains("new york") || lower.contains("texas") || lower.contains("dollar") -> "US"
            lower.contains("uk") || lower.contains("united kingdom") || lower.contains("london") || lower.contains("england") || lower.contains("pound") -> "GB"
            lower.contains("australia") || lower.contains("sydney") || lower.contains("melbourne") || lower.contains("aud") -> "AU"
            else -> "IN" // Default India
        }

        // 2. Classify Category
        val isTenancy = lower.contains("rent") || lower.contains("landlord") || lower.contains("tenant") ||
                lower.contains("deposit") || lower.contains("advance") || lower.contains("evict") ||
                lower.contains("lease") || lower.contains("வாடகை") || lower.contains("அட்வான்ஸ்") || lower.contains("வீட்டு உரிமையாளர்")

        val isLabor = lower.contains("salary") || lower.contains("wage") || lower.contains("unpaid") ||
                lower.contains("employer") || lower.contains("boss") || lower.contains("fired") ||
                lower.contains("terminated") || lower.contains("pf") || lower.contains("provident") ||
                lower.contains("சம்பளம்") || lower.contains("ஊதியம்") || lower.contains("வேலை நீக்கம்")

        val isCyber = lower.contains("scam") || lower.contains("phishing") || lower.contains("otp") ||
                lower.contains("upi") || lower.contains("cyber") || lower.contains("hacked") ||
                lower.contains("fraud") || lower.contains("wire") || lower.contains("credit card") ||
                lower.contains("மோசடி") || lower.contains("பணம் பறிப்பு")

        val isCriminal = lower.contains("threat") || lower.contains("assault") || lower.contains("extort") ||
                lower.contains("beat") || lower.contains("kill") || lower.contains("weapon") ||
                lower.contains("stalking") || lower.contains("மிரட்டல்") || lower.contains("தாக்குதல்")

        // Calculate factual readiness score (parties: 20, proofs: 30, communication: 25, timeline: 25)
        var score = 20 // incident described
        val availableFacts = mutableListOf<String>("Grievance factual narrative recorded")
        val missingEvidence = mutableListOf<String>()

        if (lower.contains("paid") || lower.contains("rs") || lower.contains("$") || lower.contains("receipt") || lower.contains("bank") || lower.contains("rupees")) {
            score += 30
            availableFacts.add("Financial transaction and monetary claim quantified")
        } else {
            missingEvidence.add("Bank statements / transaction receipts")
        }

        if (lower.contains("message") || lower.contains("whatsapp") || lower.contains("call") || lower.contains("email") || lower.contains("notice")) {
            score += 25
            availableFacts.add("Written communications and notice trail available")
        } else {
            missingEvidence.add("Written demand notice or correspondence history")
        }

        if (lower.contains("yesterday") || lower.contains("month") || lower.contains("date") || lower.contains("days") || lower.contains("ago") || lower.contains("202")) {
            score += 25
            availableFacts.add("Timeline sequence established")
        } else {
            missingEvidence.add("Exact dates of transaction / dispute milestones")
        }

        return when (country) {
            "US" -> createUSResponse(isTenancy, isLabor, isCyber, isCriminal, userProblem, score, availableFacts, missingEvidence, isTamil)
            "GB" -> createUKResponse(isTenancy, isLabor, isCyber, isCriminal, userProblem, score, availableFacts, missingEvidence, isTamil)
            "AU" -> createAUResponse(isTenancy, isLabor, isCyber, isCriminal, userProblem, score, availableFacts, missingEvidence, isTamil)
            else -> createINResponse(isTenancy, isLabor, isCyber, isCriminal, userProblem, score, availableFacts, missingEvidence, isTamil)
        }
    }

    private fun createINResponse(
        isTenancy: Boolean, isLabor: Boolean, isCyber: Boolean, isCriminal: Boolean,
        facts: String, score: Int, availableFacts: List<String>, missingEvidence: List<String>, isTamil: Boolean
    ): WorldwideLegalResponse {
        val jurisdiction = JurisdictionInfo("IN", "India", "Common Law")

        return when {
            isTenancy -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Tenancy",
                    subCategory = "Security Deposit Refund & Unlawful Eviction",
                    incidentSummary = facts,
                    applicableStatute = "Model Tenancy Act / Tamil Nadu Regulation of Rights and Responsibilities of Landlords and Tenants Act, 2017",
                    enforcingAuthorityOrPortal = "Rent Authority / Rent Court / Civil Court",
                    helplineNumber = null
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "Serve Legal Notice", "Issue a 15-day formal demand notice for deposit refund with interest.", "Postal / Registered AD"),
                    ActionStep(2, "File Petition before Rent Authority", "Lodge a petition under the State Tenancy Act for recovery of advance deposit.", "District Rent Court")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Move-out Date", "Tenancy concluded and keys handed over", false),
                    TimelineMilestone("Deposit Retention", "Landlord refused or withheld refund", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Formal Complaint for Recovery of Withheld Security Deposit and Unlawful Tenancy Dispute",
                    addressedTo = "The Rent Authority / Rent Tribunal",
                    statementOfFacts = facts,
                    demandedRelief = "Immediate order directing full refund of security deposit with 18% p.a. statutory interest and damages for harassment."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "வீட்டு வாடகை முன்பணத்தை தராமல் இழுத்தடிப்பது வாடகை சட்டப்படி தவறானது. நுகர்வோர் நீதிமன்றம் செல்லாமல் வாடகை அதிகாரியிடம் (Rent Authority) முறையிட வேண்டும்." else "Withholding rental security deposit is governed strictly by the State Rent Control / Tenancy Act. Petitions must be submitted before the Rent Authority.",
                    immediateNextStep = if (isTamil) "வாடகை ஒப்பந்தம் மற்றும் வங்கி ரசீதுகளுடன் 15 நாள் காலக்கெடு விதித்து வக்கீல் நோட்டீஸ் அனுப்பவும்." else "Dispatch a 15-day formal demand notice before approaching the Rent Court."
                )
            )

            isLabor -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Employment",
                    subCategory = "Non-Payment of Wages & Wrongful Termination",
                    incidentSummary = facts,
                    applicableStatute = "Payment of Wages Act, 1936 (Section 15) & Industrial Disputes Act, 1947",
                    enforcingAuthorityOrPortal = "Office of the Labour Commissioner / Labour Court",
                    helplineNumber = "1800-425-4566"
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "Issue Written Wage Demand", "Demand unpaid salary with salary slips and attendance records.", "Employer HR / Management"),
                    ActionStep(2, "Lodge Claim with Labour Officer", "File Form A under Section 15 of Payment of Wages Act for delayed remuneration.", "Labour Commission Portal")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Employment Period", "Active service rendered", false),
                    TimelineMilestone("Wage Default", "Salary withheld without statutory justification", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Application under Section 15 of Payment of Wages Act for Recovery of Delayed Wages",
                    addressedTo = "The Authority under the Payment of Wages Act / Labour Commissioner",
                    statementOfFacts = facts,
                    demandedRelief = "Payment of all outstanding salary dues along with maximum statutory compensation up to 10 times the amount."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "ஊழியரின் நியாயமான சம்பளத்தை நிறுத்துவது தொழிலாளர் சட்டப்படி குற்றமாகும். தொழிலாளர் ஆணையரிடம் மனு செய்யலாம்." else "Non-payment of earned wages violates the Payment of Wages Act. File directly with the Labour Commissioner.",
                    immediateNextStep = if (isTamil) "பணி நியமன கடிதம் மற்றும் வங்கி கணக்கு அறிக்கையுடன் தொழிலாளர் நீதிமன்றத்தில் மனு தாக்கல் செய்யவும்." else "Collect appointment letter and bank statements to file a complaint before the Labour Court."
                )
            )

            isCyber -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Cybercrime",
                    subCategory = "Online Financial Phishing & Unauthorized UPI Fraud",
                    incidentSummary = facts,
                    applicableStatute = "Information Technology Act, 2000 (Section 43/66D) & Section 318 (Cheating), Bharatiya Nyaya Sanhita (BNS), 2023",
                    enforcingAuthorityOrPortal = "National Cyber Crime Reporting Portal (cybercrime.gov.in)",
                    helplineNumber = "1930"
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "Call 1930 Helpline Immediately", "Trigger golden-hour account freeze for beneficiary bank accounts.", "Helpline 1930"),
                    ActionStep(2, "Register Cybercrime Complaint", "File complaint with transaction UTR numbers and screenshots.", "https://cybercrime.gov.in")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Transaction Execution", "Unauthorized debit or fraudulent transfer occurred", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Urgent Complaint on Cyber Financial Fraud and Online Impersonation",
                    addressedTo = "The Cyber Crime Investigation Cell / Station House Officer",
                    statementOfFacts = facts,
                    demandedRelief = "Immediate freezing of accused bank accounts, investigation under Sec 66D IT Act and recovery of defrauded funds."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "இணைய மோசடி நடந்த உடனே 1930 அவசர எண்ணை அழைத்து வங்கி கணக்கை முடக்க வேண்டும்." else "Cyber fraud must be reported immediately via 1930 to freeze the fraudster's beneficiary account.",
                    immediateNextStep = if (isTamil) "1930 எண்ணை அழைத்து வங்கி பரிவர்த்தனை ஐடி வழங்கி cybercrime.gov.in-ல் உடனே பதிவு செய்யவும்." else "Dial 1930 with transaction UTR numbers and file on cybercrime.gov.in."
                )
            )

            isCriminal -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Criminal",
                    subCategory = "Criminal Intimidation, Assault & Extortion",
                    incidentSummary = facts,
                    applicableStatute = "Bharatiya Nyaya Sanhita (BNS), 2023 (Sections 115, 308, 351) & BNSS, 2023",
                    enforcingAuthorityOrPortal = "Jurisdictional Police Station / 112 National Emergency",
                    helplineNumber = "112"
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "Emergency Reporting", "Call 112 for immediate dispatch or physical safety.", "112 Emergency"),
                    ActionStep(2, "Lodge FIR / Zero FIR", "Submit written report and obtain signed CSR / FIR acknowledgement.", "Local Police Station")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Incident of Threat", "Occurrence of unlawful intimidation or assault", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Formal Complaint for Registration of First Information Report (FIR) under BNS, 2023",
                    addressedTo = "The Station House Officer (SHO)",
                    statementOfFacts = facts,
                    demandedRelief = "Immediate registration of FIR under relevant sections of Bharatiya Nyaya Sanhita and protection of life and liberty."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "நேரடி மிரட்டல் மற்றும் தாக்குதல் குற்றவியல் சட்டம் BNS-ன் கீழ் வருகிறது. காவல் நிலையத்தில் FIR பதிவு செய்ய வேண்டும்." else "Direct threats and assault fall strictly under the Bharatiya Nyaya Sanhita. File an FIR with the police.",
                    immediateNextStep = if (isTamil) "காவல் நிலையம் சென்று Zero FIR பதிவு செய்து CSR ரசீதை கேட்டு வாங்கவும்." else "Visit the nearest police station to lodge an FIR and obtain a signed copy."
                )
            )

            else -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Consumer",
                    subCategory = "Deficiency in Service & Defective Goods",
                    incidentSummary = facts,
                    applicableStatute = "Consumer Protection Act, 2019 (Sections 2(47), 35 & 84)",
                    enforcingAuthorityOrPortal = "National Consumer Disputes Redressal Commission / e-Daakhil Portal",
                    helplineNumber = "1915"
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "Lodge Grievance with NCH", "Register online consumer complaint with invoice & warranty cards.", "consumerhelpline.gov.in / 1915"),
                    ActionStep(2, "E-File on e-Daakhil", "Lodge formal consumer dispute before District Consumer Forum.", "edaakhil.nic.in")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Purchase Date", "Goods or service procured under valid transaction", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Consumer Complaint under Section 35 of Consumer Protection Act, 2019 for Deficient Goods/Services",
                    addressedTo = "The District Consumer Disputes Redressal Commission",
                    statementOfFacts = facts,
                    demandedRelief = "Full refund of purchase value, replacement of defective article, and compensation for mental agony."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "வாங்கிய பொருளில் குறைபாடு இருந்தால் நுகர்வோர் பாதுகாப்பு சட்டத்தின் கீழ் இழப்பீடு பெறலாம்." else "Defective goods and service deficiency are protected under the Consumer Protection Act, 2019.",
                    immediateNextStep = if (isTamil) "ரசீது மற்றும் வாரண்டி அட்டையுடன் 1915 எண்ணில் அழைக்கவும் அல்லது e-Daakhil-ல் வழக்கு தொடரவும்." else "Call 1915 or register your dispute on the e-Daakhil consumer portal."
                )
            )
        }
    }

    private fun createUSResponse(
        isTenancy: Boolean, isLabor: Boolean, isCyber: Boolean, isCriminal: Boolean,
        facts: String, score: Int, availableFacts: List<String>, missingEvidence: List<String>, isTamil: Boolean
    ): WorldwideLegalResponse {
        val jurisdiction = JurisdictionInfo("US", "United States", "Common Law")

        return when {
            isTenancy -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Tenancy",
                    subCategory = "Security Deposit Withholding & Habitability Violations",
                    incidentSummary = facts,
                    applicableStatute = "Uniform Residential Landlord and Tenant Act (URLTA) & State Landlord-Tenant Codes",
                    enforcingAuthorityOrPortal = "Local Housing Authority / Small Claims Court",
                    helplineNumber = null
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "Demand Letter", "Send a formal demand letter via certified mail citing statutory timeline (usually 21-30 days).", "Certified Mail"),
                    ActionStep(2, "Small Claims Filing", "File a statement of claim in County Small Claims Court for bad-faith retention.", "Small Claims Division")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Lease Termination", "Tenant vacated and provided forwarding address", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Formal Claim for Wrongful Withholding of Security Deposit and Statutory Penalties",
                    addressedTo = "The Small Claims Court / Municipal Court",
                    statementOfFacts = facts,
                    demandedRelief = "Return of full security deposit plus statutory double/triple damages for bad-faith retention."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "அமெரிக்காவில் வாடகை அட்வான்ஸ் தொகையை நிறுத்துவது மாநில வாடகை குத்தகை சட்டத்தின் கீழ் சிறு வழக்குகள் நீதிமன்றத்திற்கு (Small Claims Court) உரியது." else "Withheld deposits in the US fall under State Landlord-Tenant Codes and Small Claims Court.",
                    immediateNextStep = if (isTamil) "அங்கீகரிக்கப்பட்ட தபாலில் (Certified Mail) அதிகாரப்பூர்வ கோரிக்கை கடிதம் அனுப்பவும்." else "Send a certified demand letter citing your state's security deposit statute."
                )
            )

            isLabor -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Employment",
                    subCategory = "Wage Theft & Overtime / FLSA Violations",
                    incidentSummary = facts,
                    applicableStatute = "Fair Labor Standards Act (FLSA), 29 U.S.C. § 201 et seq. & State Labor Code",
                    enforcingAuthorityOrPortal = "U.S. Department of Labor (Wage and Hour Division) / State Labor Commissioner",
                    helplineNumber = "1-866-4-US-WAGE"
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "File DOL Complaint", "Submit Form WH-4 to the Wage and Hour Division.", "dol.gov/agencies/whd"),
                    ActionStep(2, "State Labor Claim", "File a wage claim with your state's Department of Labor.", "State Labor Board")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Pay Period Default", "Hours worked without statutory compensation", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Complaint for Unpaid Wages and Liquidated Damages under the Fair Labor Standards Act",
                    addressedTo = "U.S. Department of Labor / Wage and Hour Division",
                    statementOfFacts = facts,
                    demandedRelief = "Recovery of back wages plus an equal amount in liquidated damages and attorney fees."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "அமெரிக்காவில் ஊதிய பாக்கி Fair Labor Standards Act (FLSA) கீழ் தொழிலாளர் துறையால் (DOL) விசாரிக்கப்படும்." else "Wage theft in the US is strictly governed by the FLSA and enforced by the Department of Labor.",
                    immediateNextStep = if (isTamil) "பணிநேர பதிவுகளுடன் DOL-ல் ஆன்லைனில் புகார் அளிக்கவும்." else "File a complaint with the US Department of Labor Wage and Hour Division."
                )
            )

            isCyber -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Cybercrime",
                    subCategory = "Wire Fraud & Phishing Identity Theft",
                    incidentSummary = facts,
                    applicableStatute = "Computer Fraud and Abuse Act (CFAA), 18 U.S.C. § 1030 & FTC Act",
                    enforcingAuthorityOrPortal = "FBI Internet Crime Complaint Center (IC3) & FTC",
                    helplineNumber = "1-877-FTC-HELP"
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "File IC3 Report", "Lodge immediate report on the FBI IC3 portal for wire recall.", "ic3.gov"),
                    ActionStep(2, "Report Identity Theft", "Create an official recovery plan on IdentityTheft.gov.", "IdentityTheft.gov")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Fraudulent Transfer", "Unauthorized funds transfer occurred", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Internet Crime Complaint: Unauthorized Electronic Funds Transfer and Fraud",
                    addressedTo = "Internet Crime Complaint Center (IC3) / Federal Bureau of Investigation",
                    statementOfFacts = facts,
                    demandedRelief = "Investigation into electronic wire fraud and facilitation of financial freeze."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "அமெரிக்காவில் இணைய நிதி மோசடிக்கு FBI-ன் IC3 மற்றும் FTC போர்ட்டலில் புகார் செய்ய வேண்டும்." else "Report US internet and banking scams immediately to the FBI IC3 portal.",
                    immediateNextStep = if (isTamil) "ic3.gov இணையதளத்தில் பரிவர்த்தனை ஆதாரங்களுடன் உடனடியாக புகார் செய்யவும்." else "Submit a detailed fraud report on ic3.gov."
                )
            )

            else -> WorldwideLegalResponse(
                jurisdiction = jurisdiction,
                disputeClassification = DisputeClassification(
                    primaryCategory = "Consumer",
                    subCategory = "Deceptive Trade Practices & Product Liability",
                    incidentSummary = facts,
                    applicableStatute = "Federal Trade Commission Act (15 U.S.C. § 45) & Uniform Commercial Code (UCC Art. 2)",
                    enforcingAuthorityOrPortal = "Federal Trade Commission (FTC) & State Attorney General (Consumer Protection)",
                    helplineNumber = "1-877-382-4357"
                ),
                caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
                actionSteps = listOf(
                    ActionStep(1, "FTC Report", "Report unfair or deceptive commercial practices to the FTC.", "reportfraud.ftc.gov"),
                    ActionStep(2, "Attorney General Complaint", "File consumer grievance with state AG office.", "State AG Consumer Division")
                ),
                timelineMilestones = listOf(
                    TimelineMilestone("Purchase Date", "Transaction finalized with vendor", false)
                ),
                formalComplaintDraft = FormalComplaintDraft(
                    subjectLine = "Consumer Complaint for Deceptive Trade Practice and Failure of Merchantability",
                    addressedTo = "Office of the Attorney General - Consumer Protection Division",
                    statementOfFacts = facts,
                    demandedRelief = "Full restitution of purchase price and injunctive relief against fraudulent merchant."
                ),
                localizedResponse = LocalizedResponse(
                    language = if (isTamil) "Tamil" else "English",
                    simpleExplanation = if (isTamil) "அமெரிக்காவில் நுகர்வோர் பிரச்சனைகள் FTC மற்றும் மாநில Attorney General நுகர்வோர் பிரிவின் கீழ் வரும்." else "Consumer complaints in the US fall under FTC guidelines and State AG Consumer Protection Divisions.",
                    immediateNextStep = if (isTamil) "reportfraud.ftc.gov போர்ட்டலில் ரசீதுடன் புகார் அளிக்கவும்." else "File a complaint on reportfraud.ftc.gov."
                )
            )
        }
    }

    private fun createUKResponse(
        isTenancy: Boolean, isLabor: Boolean, isCyber: Boolean, isCriminal: Boolean,
        facts: String, score: Int, availableFacts: List<String>, missingEvidence: List<String>, isTamil: Boolean
    ): WorldwideLegalResponse {
        val jurisdiction = JurisdictionInfo("GB", "United Kingdom", "Common Law")

        return WorldwideLegalResponse(
            jurisdiction = jurisdiction,
            disputeClassification = DisputeClassification(
                primaryCategory = if (isTenancy) "Tenancy" else if (isLabor) "Employment" else if (isCyber) "Cybercrime" else "Consumer",
                subCategory = if (isTenancy) "Tenancy Deposit Scheme (TDS) Dispute" else if (isLabor) "Unlawful Deduction from Wages" else if (isCyber) "Online Fraud & Scam" else "Defective Goods / Consumer Rights",
                incidentSummary = facts,
                applicableStatute = if (isTenancy) "Housing Act 2004 (Tenancy Deposit Protection) & Tenant Fees Act 2019"
                else if (isLabor) "Employment Rights Act 1996 (Part II - Protection of Wages)"
                else if (isCyber) "Computer Misuse Act 1990 & Fraud Act 2006"
                else "Consumer Rights Act 2015",
                enforcingAuthorityOrPortal = if (isTenancy) "Tenancy Deposit Scheme (TDS) / County Court"
                else if (isLabor) "Advisory, Conciliation and Arbitration Service (ACAS) / Employment Tribunal"
                else if (isCyber) "Action Fraud UK / National Fraud Intelligence Bureau"
                else "Citizens Advice Consumer Service / Trading Standards",
                helplineNumber = if (isCyber) "0300 123 2040" else null
            ),
            caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
            actionSteps = listOf(
                ActionStep(1, "Notify Authorized Authority", "Register dispute with the designated UK statutory agency.", if (isCyber) "actionfraud.police.uk" else "gov.uk"),
                ActionStep(2, "Tribunal / Small Claims Procedure", "Initiate formal resolution or court action.", "gov.uk/court-claim")
            ),
            timelineMilestones = listOf(
                TimelineMilestone("Date of Incident", "Dispute arose in jurisdiction", false)
            ),
            formalComplaintDraft = FormalComplaintDraft(
                subjectLine = "Formal Statutory Complaint under UK Jurisprudence",
                addressedTo = "The Authorized Dispute Officer / Tribunal",
                statementOfFacts = facts,
                demandedRelief = "Full financial restitution and statutory penalty compliance."
            ),
            localizedResponse = LocalizedResponse(
                language = if (isTamil) "Tamil" else "English",
                simpleExplanation = if (isTamil) "இங்கிலாந்தில் இக்குறைபாடு அந்நாட்டு சட்டப்படியான பிரத்யேக குறைதீர்ப்பு அமைப்புக்கு உரியது." else "Under UK law, this matter is governed by specific statutory authorities.",
                immediateNextStep = if (isTamil) "சம்பந்தப்பட்ட UK போர்ட்டலில் ஆவணங்களுடன் புகார் பதிவு செய்யவும்." else "Submit a complaint to the relevant UK statutory body."
            )
        )
    }

    private fun createAUResponse(
        isTenancy: Boolean, isLabor: Boolean, isCyber: Boolean, isCriminal: Boolean,
        facts: String, score: Int, availableFacts: List<String>, missingEvidence: List<String>, isTamil: Boolean
    ): WorldwideLegalResponse {
        val jurisdiction = JurisdictionInfo("AU", "Australia", "Common Law")

        return WorldwideLegalResponse(
            jurisdiction = jurisdiction,
            disputeClassification = DisputeClassification(
                primaryCategory = if (isTenancy) "Tenancy" else if (isLabor) "Employment" else if (isCyber) "Cybercrime" else "Consumer",
                subCategory = if (isTenancy) "Rental Bond Recovery" else if (isLabor) "Fair Work Unpaid Entitlements" else if (isCyber) "Cyber Scams" else "Australian Consumer Law Guarantees",
                incidentSummary = facts,
                applicableStatute = if (isTenancy) "Residential Tenancies Act (State Tribunals)"
                else if (isLabor) "Fair Work Act 2009"
                else if (isCyber) "Privacy Act 1988 & Criminal Code Act 1995"
                else "Competition and Consumer Act 2010 (Australian Consumer Law)",
                enforcingAuthorityOrPortal = if (isTenancy) "NCAT / VCAT / QCAT / State Bond Authority"
                else if (isLabor) "Fair Work Ombudsman (fairwork.gov.au)"
                else if (isCyber) "ReportCyber (cyber.gov.au) & Scamwatch"
                else "Australian Competition and Consumer Commission (ACCC)",
                helplineNumber = if (isLabor) "13 13 94" else null
            ),
            caseReadiness = CaseReadiness(score, availableFacts, missingEvidence),
            actionSteps = listOf(
                ActionStep(1, "Lodge with Authority", "File dispute with Australian regulatory portal.", if (isLabor) "fairwork.gov.au" else "consumer.gov.au"),
                ActionStep(2, "Tribunal Action", "File claim in state administrative tribunal (e.g. NCAT/VCAT).", "State Tribunal")
            ),
            timelineMilestones = listOf(
                TimelineMilestone("Date of Incident", "Occurrence of dispute in Australia", false)
            ),
            formalComplaintDraft = FormalComplaintDraft(
                subjectLine = "Formal Notice of Claim under Australian Law",
                addressedTo = "The Registrar / Commissioner",
                statementOfFacts = facts,
                demandedRelief = "Remedy of breach, statutory compensation and enforcement of consumer/worker guarantees."
            ),
            localizedResponse = LocalizedResponse(
                language = if (isTamil) "Tamil" else "English",
                simpleExplanation = if (isTamil) "ஆஸ்திரேலியாவில் இக்குறைபாடு ஆஸ்திரேலிய சட்டப்படியான அமைப்பால் கண்காணிக்கப்படுகிறது." else "This dispute is covered under Australian statutory protections.",
                immediateNextStep = if (isTamil) "ஆஸ்திரேலிய அதிகாரப்பூர்வ இணையதளத்தில் பதிவு செய்யவும்." else "Lodge your grievance with the designated Australian authority."
            )
        )
    }
}
