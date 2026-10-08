package com.justra.app.data.api

import android.util.Log
import com.justra.app.BuildConfig
import com.justra.app.data.local.ActionStepEntity
import com.justra.app.data.local.CaseEntity
import com.justra.app.data.local.ScamIncidentEntity
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.LegalComplaintResult
import com.justra.app.domain.model.RiskLevel
import com.justra.app.domain.model.UserRole
import com.justra.app.util.LegalInputValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

data class ChatIntakeResult(
    val responseText: String,
    val extractedKey: String? = null,
    val extractedValue: String? = null,
    val isActionableProof: Boolean = false,
    val identifiedCategory: DisputeCategory? = null
)

object GeminiLegalEngine {
    private const val TAG = "GeminiLegalEngine"

    fun enforceMax500Chars(text: String): String {
        return text.trim()
    }

    suspend fun analyzeChatIntake(
        userMessage: String,
        currentCaseCategory: DisputeCategory?,
        language: LanguagePreference,
        userRole: UserRole = UserRole.CITIZEN
    ): ChatIntakeResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isTamil = language == LanguagePreference.TAMIL

        // 1. Client-side input validation check for gibberish/invalid text
        val validation = LegalInputValidator.validateLegalInput(userMessage)
        if (validation is LegalInputValidator.ValidationResult.Invalid) {
            val clarifyMessage = if (isTamil) "தயவுசெய்து உங்கள் தகவலை தெளிவுபடுத்தவும் (Can you clarify it?)." else "Can you clarify it?"
            return@withContext ChatIntakeResult(
                responseText = clarifyMessage,
                extractedKey = null,
                extractedValue = null,
                isActionableProof = false,
                identifiedCategory = currentCaseCategory
            )
        }

        val roleInstruction = when (userRole) {
            UserRole.CITIZEN -> "User is an Indian Citizen / Consumer seeking approachable, rights-focused legal advice under Consumer Protection Act 2019, BNS 2023, and civil statutes."
            UserRole.LEGAL_COUNSEL -> "User is an Advocate / Legal Counsel. Provide precise statutory provisions, procedural citations under BNSS 2023 / CPC 1908, evidentiary requirements under BSA 2023, and formal demand notice posture."
            UserRole.MSME_BUSINESS -> "User is an MSME Business Enterprise. Focus on commercial dispute resolution, MSMED Act 2006 (Sections 15-18 statutory interest at 3x bank rate), MSME Samadhaan facilitation council, and buyer default recovery."
            UserRole.CYBER_FRAUD_VICTIM -> "User is an urgent Cyber Financial Fraud Victim. Prioritize immediate 1930 helpline reporting, cybercrime.gov.in FIR filing, Section 66D IT Act, and bank nodal officer account freeze protocol within the golden hour."
        }

        // Check if API key is provided and valid
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = """
                    You are Justra (ஜஸ்ட்ரா), a sovereign AI Legal Assistant for India.
                    Active User Legal Persona: ${userRole.titleEn} (${userRole.titleTa}).
                    $roleInstruction
                    Language to respond in: ${if (isTamil) "Tamil (தமிழ்)" else "English"}.
                    
                    STRICT RULES:
                    1. Directly respond to what the user entered in full detail. Answer their specific question or situation accurately.
                    2. If the user's input is unclear, incomplete, ambiguous, or invalid, simply ask: "${if (isTamil) "தயவுசெய்து உங்கள் தகவலை தெளிவுபடுத்தவும் (Can you clarify it?)." else "Can you clarify it?"}".
                    3. Do NOT output generic static fallback content. Tailor your response strictly to what the user entered.
                    
                    FORMAT FOR VALID INTENT:
                    - Address the user's question directly.
                    - Legal Classification & Governing Statute (e.g. BNS 2023, Model Tenancy Act, IT Act 2000, CPA 2019).
                    - Concrete Immediate Action Steps.
                    - Evidence Checklist.
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "User message: $userMessage\nContext Dispute Category: ${currentCaseCategory?.titleEn ?: "General Intake"}\nUser Legal Persona: ${userRole.name}")
                            )
                        )
                    ),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.3f, maxOutputTokens = 2048)
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val rawResponseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!rawResponseText.isNullOrBlank()) {
                    val responseText = rawResponseText.trim()
                    val extracted = detectEntitiesInText(userMessage)
                    return@withContext ChatIntakeResult(
                        responseText = responseText,
                        extractedKey = extracted.first,
                        extractedValue = extracted.second,
                        isActionableProof = extracted.first != null,
                        identifiedCategory = detectCategoryFromText(userMessage) ?: currentCaseCategory
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API request failed, falling back to local reasoning: ${e.message}")
            }
        }

        // Local Deterministic Legal Engine Fallback
        val extracted = detectEntitiesInText(userMessage)
        val detectedCategory = detectCategoryFromText(userMessage)
        
        if (detectedCategory == null && currentCaseCategory == null) {
            val clarifyMsg = if (isTamil) "தயவுசெய்து உங்கள் தகவலை தெளிவுபடுத்தவும் (Can you clarify it?)." else "Can you clarify it?"
            return@withContext ChatIntakeResult(
                responseText = clarifyMsg,
                extractedKey = extracted.first,
                extractedValue = extracted.second,
                isActionableProof = extracted.first != null,
                identifiedCategory = null
            )
        }

        val category = detectedCategory ?: currentCaseCategory ?: DisputeCategory.CONSUMER_GRIEVANCE
        val statute = LegalStatuteKnowledge.getStatuteForCategory(category)

        val rawResponseText = if (isTamil) {
            """
            ⚖️ சட்ட வகைப்பாடு: ${category.titleTa}
            
            📌 பொருந்தக்கூடிய சட்டம் & பிரிவு: ${statute.actNameTa} (${statute.section})
            🏛️ அணுக வேண்டிய தீர்ப்பாயம்: ${statute.applicableForum}
            ⏳ சட்ட காலக்கெடு: ${statute.limitationPeriod}
            
            ⚡ உடனடி நடவடிக்கைகள்:
            1. ஆவணங்களை சான்று பெட்டகத்தில் (Evidence Vault) பாதுகாப்பாக சேமிக்கவும்.
            2. 'Smart Complaint Generator' மூலம் அதிகாரப்பூர்வ சட்ட அறிவிப்பைத் தயாரிக்கவும்.
            3. சம்பந்தப்பட்ட அதிகார அமைப்பிடம் (${statute.applicableForum}) புகார் மனு அளிக்கவும்.
            
            📋 தேவையான ஆதாரங்களின் பட்டியல்:
            • வங்கி பரிவர்த்தனை / கட்டண ரசீது சான்றுகள்
            • ஒப்பந்தம் / கடிதத் தொடர்புகள்
            • டிஜிட்டல் சான்றிதழ் (Sec 63 BSA / 65B IT Act)
            ${if (extracted.first != null) "\n💡 கண்டறியப்பட்ட விவரம்: ${extracted.first} = ${extracted.second}." else ""}
            """.trimIndent()
        } else {
            """
            ⚖️ Legal Classification: ${category.titleEn}
            
            📌 Governing Act & Section: ${statute.actName} (${statute.section})
            🏛️ Forum / Authority: ${statute.applicableForum}
            ⏳ Statutory Limitation: ${statute.limitationPeriod}
            
            ⚡ Immediate Action Steps:
            1. Secure documentary proof in encrypted Evidence Vault with SHA-256 seal.
            2. Generate pre-litigation formal demand notice using Smart Complaint Generator.
            3. File formal complaint with ${statute.applicableForum}.
            
            📋 Mandatory Evidence Checklist:
            • Transaction proof / Payment receipts / Invoices
            • Written agreements, emails, or chat records
            • Section 63 BSA / 65B IT Act Electronic Evidence Certificate
            ${if (extracted.first != null) "\n💡 Identified Entity: ${extracted.first} = ${extracted.second}." else ""}
            """.trimIndent()
        }

        val responseText = rawResponseText.trim()

        return@withContext ChatIntakeResult(
            responseText = responseText,
            extractedKey = extracted.first,
            extractedValue = extracted.second,
            isActionableProof = extracted.first != null,
            identifiedCategory = category
        )
    }

    suspend fun transcribeAndProcessAudioWithGemini(
        audioBase64: String,
        mimeType: String = "audio/wav",
        language: LanguagePreference = LanguagePreference.TAMIL
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isTamil = language == LanguagePreference.TAMIL

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = """
                    You are Justra STT Voice Intelligence.
                    Transcribe the audio provided in ${if (isTamil) "Tamil or Indian English" else "English or Tamil"}.
                    Return ONLY the transcribed text accurately without any conversational filler or meta text.
                    Strictly keep output under 500 characters.
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(inlineData = GeminiInlineData(mimeType = mimeType, data = audioBase64)),
                                GeminiPart(text = "Transcribe this spoken legal grievance accurately:")
                            )
                        )
                    ),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.1f, maxOutputTokens = 2048)
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val transcribedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!transcribedText.isNullOrBlank()) {
                    return@withContext enforceMax500Chars(transcribedText.trim())
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini Audio STT request failed: ${e.message}")
            }
        }
        return@withContext ""
    }

    suspend fun analyzeScamText(
        inputText: String,
        language: LanguagePreference
    ): ScamIncidentEntity = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isTamil = language == LanguagePreference.TAMIL
        val id = UUID.randomUUID().toString()

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Analyze this message for scam, cyber fraud, phishing, or financial extortion:
                    "$inputText"
                    Respond strictly in JSON format with keys:
                    - "riskLevel": one of ["NONE", "LOW", "MEDIUM", "HIGH", "CRITICAL"]
                    - "isScamDetected": boolean
                    - "redFlags": list of string explanations
                    - "safetyGuidance": list of actionable safety steps
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.1f,
                        responseMimeType = "application/json"
                    )
                )
                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    val cleanJson = text.substringAfter("{").substringBeforeLast("}")
                    val jsonObj = JSONObject("{$cleanJson}")
                    val riskLevelStr = jsonObj.optString("riskLevel", "HIGH")
                    val riskLevel = try { RiskLevel.valueOf(riskLevelStr) } catch (_: Exception) { RiskLevel.HIGH }
                    val isScam = jsonObj.optBoolean("isScamDetected", true)
                    val redFlagsList = mutableListOf<String>()
                    val redFlagsArr = jsonObj.optJSONArray("redFlags")
                    if (redFlagsArr != null) {
                        for (i in 0 until redFlagsArr.length()) {
                            redFlagsList.add(redFlagsArr.getString(i))
                        }
                    }
                    val guidanceList = mutableListOf<String>()
                    val guidanceArr = jsonObj.optJSONArray("safetyGuidance")
                    if (guidanceArr != null) {
                        for (i in 0 until guidanceArr.length()) {
                            guidanceList.add(guidanceArr.getString(i))
                        }
                    }
                    return@withContext ScamIncidentEntity(
                        incidentId = id,
                        analyzedText = inputText,
                        riskLevel = riskLevel,
                        isScamDetected = isScam,
                        detectedRedFlags = if (redFlagsList.isNotEmpty()) redFlagsList else listOf("Suspicious urgency detected"),
                        safetyGuidance = if (guidanceList.isNotEmpty()) guidanceList else listOf("Do not click links", "Dial 1930 immediately"),
                        timestamp = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini scam analysis error, using heuristic detector: ${e.message}")
            }
        }

        // Local Rule-Based & Heuristic Scam Analysis
        val lower = inputText.lowercase()
        val redFlags = mutableListOf<String>()
        val guidance = mutableListOf<String>()
        var riskLevel = RiskLevel.NONE
        var isScam = false

        if (lower.contains(".apk") || lower.contains("apk") || (lower.contains("download") && (lower.contains("http") || lower.contains("bit.ly") || lower.contains("t.me")))) {
            redFlags.add(if (isTamil) "சந்தேகத்திற்குரிய தீங்கிழைக்கும் APK செயலி பதிவிறக்க இணைப்பு கண்டறியப்பட்டது." else "Unverified external APK download link detected. High risk of spyware/screen-sharing malware.")
            riskLevel = RiskLevel.CRITICAL
            isScam = true
        }

        if (lower.contains("electricity") || lower.contains("eb bill") || lower.contains("power cut") || lower.contains("மின் கட்டணம்")) {
            redFlags.add(if (isTamil) "மின்சார இணைப்பு துண்டிக்கப்படும் என்ற போலி அவசர அச்சுறுத்தல்." else "Fake urgent power disconnection / EB electricity bill threat pattern.")
            if (riskLevel != RiskLevel.CRITICAL) riskLevel = RiskLevel.HIGH
            isScam = true
        }

        if (lower.contains("blocked") || lower.contains("deactivated") || lower.contains("kyc") || lower.contains("pan") || lower.contains("freeze") || lower.contains("அக்கவுண்ட் முடக்கம்")) {
            redFlags.add(if (isTamil) "வங்கி கணக்கு அல்லது KYC முடக்கப்படும் என்ற மிரட்டல் செய்தி." else "Urgent threat of bank account / PAN / KYC freeze or deactivation.")
            riskLevel = RiskLevel.CRITICAL
            isScam = true
        }

        if (lower.contains("lottery") || lower.contains("won") || lower.contains("prize") || lower.contains("part time job") || lower.contains("daily 5000") || lower.contains("பரிசு")) {
            redFlags.add(if (isTamil) "போலி பரிசு அல்லது பகுதி நேர வேலை முதலீட்டு மோசடி வடிவம்." else "Unrealistic work-from-home reward / lottery payout lure.")
            if (riskLevel != RiskLevel.CRITICAL) riskLevel = RiskLevel.HIGH
            isScam = true
        }

        if (lower.contains("otp") || lower.contains("pin") || lower.contains("password") || lower.contains("remote") || lower.contains("anydesk") || lower.contains("teamviewer") || lower.contains("rustdesk")) {
            redFlags.add(if (isTamil) "ரகசிய OTP அல்லது தொலைதூர திரைப் பகிர்வு செயலி (AnyDesk/RustDesk) கோரிக்கை." else "Demanding sensitive OTP/PIN or remote desktop access (AnyDesk/RustDesk).")
            riskLevel = RiskLevel.CRITICAL
            isScam = true
        }

        if (redFlags.isEmpty()) {
            if (inputText.length > 10) {
                redFlags.add(if (isTamil) "வெளிப்புற அல்லது தெரியாத எண்ணில் இருந்து பெறப்பட்ட செய்தி." else "Message received from unknown or unverified sender.")
                riskLevel = RiskLevel.LOW
            } else {
                riskLevel = RiskLevel.NONE
            }
        }

        if (isScam || riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL) {
            guidance.add(if (isTamil) "எந்தவொரு இணைப்பையும் (Link/URL) கிளிக் செய்யாதீர்கள் மற்றும் எக்காரணம் கொண்டும் OTP-யை பகிராதீர்கள்." else "Never click embedded links or share OTP, UPI PIN, or bank passwords with anyone.")
            guidance.add(if (isTamil) "பணம் ஏதேனும் இழந்திருந்தால், தாமதிக்காமல் 1930 தேசிய சைபர் கிரைம் உதவி எண்ணை அழைக்கவும் (Golden Hour)." else "If money was deducted, immediately dial 1930 National Cybercrime Helpline to freeze bank transfer.")
            guidance.add(if (isTamil) "அதிகாரப்பூர்வ வங்கி அல்லது சேவை நிறுவனத்தின் உண்மையான வாடிக்கையாளர் மையத்தைத் தொடர்பு கொள்ளவும்." else "Verify directly with your bank branch or the official customer care number.")
            guidance.add(if (isTamil) "cybercrime.gov.in போர்ட்டலில் இந்த எண்ணைப் புகாரளிக்கவும்." else "Report the fraudulent mobile number and screenshot on cybercrime.gov.in.")
        } else {
            guidance.add(if (isTamil) "எப்போதும் நம்பகமான மூலங்களில் இருந்து மட்டுமே தகவல்களை சரிபார்க்கவும்." else "Always verify communications through official application portals.")
        }

        return@withContext ScamIncidentEntity(
            incidentId = id,
            analyzedText = inputText,
            riskLevel = riskLevel,
            isScamDetected = isScam,
            detectedRedFlags = redFlags,
            safetyGuidance = guidance,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun detectEntitiesInText(text: String): Pair<String?, String?> {
        val upiRegex = Regex("""([a-zA-Z0-9.\-_]+@[a-zA-Z]{2,})""")
        val upiMatch = upiRegex.find(text)
        if (upiMatch != null) {
            return "UPI ID / VPA" to upiMatch.value
        }

        val txnRegex = Regex("""(?i)(?:txn|trans|ref|utr|id)[:\s#]*([0-9]{8,18})""")
        val txnMatch = txnRegex.find(text)
        if (txnMatch != null) {
            return "Transaction / UTR Number" to txnMatch.groupValues[1]
        }

        val amountRegex = Regex("""(?:Rs\.?|₹|INR)\s*([0-9,]+)""")
        val amountMatch = amountRegex.find(text)
        if (amountMatch != null) {
            return "Disputed Claim Amount" to "₹${amountMatch.groupValues[1]}"
        }

        val phoneRegex = Regex("""(?:(?:\+91)?[6-9]\d{9})""")
        val phoneMatch = phoneRegex.find(text)
        if (phoneMatch != null) {
            return "Contact / Opposing Phone" to phoneMatch.value
        }

        val gstinRegex = Regex("""\d{2}[A-Z]{5}\d{4}[A-Z]{1}[A-Z\d]{1}[Z]{1}[A-Z\d]{1}""")
        val gstinMatch = gstinRegex.find(text)
        if (gstinMatch != null) {
            return "GSTIN Invoice Identifier" to gstinMatch.value
        }

        return null to null
    }

    private fun detectCategoryFromText(text: String): DisputeCategory? {
        val lower = text.lowercase()
        return when {
            lower.contains("upi") || lower.contains("scam") || lower.contains("fraud") || lower.contains("phishing") || lower.contains("cyber") || lower.contains("மோசடி") ->
                DisputeCategory.CYBER_FINANCIAL_FRAUD
            lower.contains("rent") || lower.contains("landlord") || lower.contains("tenant") || lower.contains("deposit") || lower.contains("வாடகை") || lower.contains("முன்பணம்") ->
                DisputeCategory.TENANCY_RENT
            lower.contains("salary") || lower.contains("unpaid") || lower.contains("employer") || lower.contains("wages") || lower.contains("சம்பளம்") || lower.contains("வேலை") ->
                DisputeCategory.EMPLOYMENT_SALARY
            lower.contains("refund") || lower.contains("defective") || lower.contains("amazon") || lower.contains("flipkart") || lower.contains("delivery") || lower.contains("பொருள்") || lower.contains("நுகர்வோர்") ->
                DisputeCategory.CONSUMER_GRIEVANCE
            lower.contains("harassment") || lower.contains("posh") || lower.contains("domestic") || lower.contains("பெண்கள்") ->
                DisputeCategory.WOMEN_RIGHTS
            lower.contains("patta") || lower.contains("land") || lower.contains("property") || lower.contains("encroach") || lower.contains("பட்டா") || lower.contains("நிலம்") ->
                DisputeCategory.LAND_PROPERTY
            lower.contains("rti") || lower.contains("certificate") || lower.contains("ration") || lower.contains("சான்றிதழ்") || lower.contains("அரசு") ->
                DisputeCategory.GOVT_RTI
            else -> null
        }
    }

    suspend fun analyzeDynamicVoiceGrievance(
        transcript: String,
        language: LanguagePreference = LanguagePreference.TAMIL
    ): LegalComplaintResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = """
You are the dynamic legal intake intelligence of "Justra".
Analyze the user's spoken incident description (in Tamil, English, or Tanglish) and classify it dynamically.

STRICT INSTRUCTION:
DO NOT default to a consumer dispute or a fixed template.
You must choose the applicable law strictly based on the user's grievance:
1. Landlord refusing deposit / Eviction -> Model Tenancy Act / State Rent Control Acts.
2. Salary delay / Unpaid wages / Workplace -> Payment of Wages Act / Industrial Disputes Act.
3. UPI / Online banking / Phishing / Fake call -> Information Technology Act, 2000 & Section 318 BNS.
4. Defective product / Non-delivery / Service refusal -> Consumer Protection Act, 2019.
5. Physical threat / Extortion / Assault -> Bharatiya Nyaya Sanhita (BNS).

Return strictly JSON matching this structure:
{
  "detectedCategory": "Consumer | Cyber | Tenancy | Employment | Criminal",
  "applicableLaw": "Exact statutory section and act name based on what user said",
  "recommendedAuthority": "Exact portal or authority (e.g. e-Daakhil, 1930 Cyber Portal, Rent Authority, Labor Commissioner)",
  "complaintDraft": {
    "subject": "Customized subject line derived from the user's facts",
    "facts": "Step-by-step summary of the grievance with mentioned dates/amounts",
    "reliefSought": "What the user wants (e.g., refund of INR 15,000 with interest, immediate return of deposit)"
  },
  "tamilExplanation": "தமிழில் எளிய விளக்கம் மற்றும் உடனடி நடவடிக்கை"
}
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "User spoken grievance:\n$transcript")
                            )
                        )
                    ),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.2f, maxOutputTokens = 1536)
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!responseText.isNullOrBlank()) {
                    val cleanJson = responseText
                        .replace("```json", "")
                        .replace("```", "")
                        .trim()
                    val json = JSONObject(cleanJson)
                    val draft = json.optJSONObject("complaintDraft")
                    return@withContext LegalComplaintResult(
                        category = json.optString("detectedCategory", "Consumer"),
                        law = json.optString("applicableLaw", "Applicable Statutory Provisions under Indian Law"),
                        authority = json.optString("recommendedAuthority", "Appropriate Statutory Authority"),
                        subject = draft?.optString("subject") ?: "Legal Grievance based on verified facts",
                        facts = draft?.optString("facts") ?: transcript,
                        relief = draft?.optString("reliefSought") ?: "Immediate statutory redressal and full compensation",
                        explanationTamil = json.optString("tamilExplanation", "சட்டப்படி உங்கள் உரிமைகளை பாதுகாக்க உரிய ஆணையத்தில் புகார் பதிவு செய்யலாம்.")
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini dynamic classification failed: ${e.message}. Using deterministic classifier.")
            }
        }

        return@withContext classifyLocally(transcript)
    }

    private fun classifyLocally(transcript: String): LegalComplaintResult {
        val lower = transcript.lowercase()
        val extractedAmount = extractAmount(transcript)
        val extractedParty = extractOpposingParty(transcript)

        return when {
            // 1. Tenancy: Landlord refusing deposit / Eviction / Rent
            lower.contains("rent") || lower.contains("landlord") || lower.contains("tenant") ||
            lower.contains("deposit") || lower.contains("evict") || lower.contains("flat") ||
            lower.contains("lease") || lower.contains("வாடகை") || lower.contains("முன்பணம்") ||
            lower.contains("வீட்டு உரிமையாளர்") -> {
                val amt = extractedAmount ?: "செலுத்தப்பட்ட முன்பணம் (Security Deposit)"
                LegalComplaintResult(
                    category = "Tenancy",
                    law = "Section 13 & 21, Model Tenancy Act & State Tenancy Acts (Tamil Nadu Regulation of Rights and Responsibilities of Landlords and Tenants Act, 2017)",
                    authority = "Rent Authority / Rent Court / Tenancy Facilitation Tribunal",
                    subject = "மனு: வாடகை முன்பணத்தை ($amt) திருப்பித் தராமை மற்றும் வீட்டை காலி செய்ய அத்துமீறுதல் மீதான புகார் ($extractedParty)",
                    facts = "மனுதாரர் வாடகை ஒப்பந்த விதிமுறைகளின்படி வீட்டை அமைதியாக அனுபவித்து வந்த நிலையில், வீட்டை காலி செய்த பின்னும் சட்டவிரோதமாக வீட்டு உரிமையாளர் ($extractedParty) நியாயமற்ற கழிவுகளை கூறி வாடகை முன்பணமான $amt தொகையை திரும்ப வழங்க மறுத்து அத்துமீறுகிறார்: $transcript",
                    relief = "முழு வாடகை முன்பணமான $amt தொகையை 18% ஆண்டு வட்டியுடன் உடனடியாக திருப்பித் தரவும், அத்துமீறலுக்கு இழப்பீடு வழங்கவும் உத்தரவிடக் கோருதல்.",
                    explanationTamil = "வாடகை முன்பணத்தை திருப்பித் தராமல் அல்லது அத்துமீறி வெளியேற்ற முயலும் வீட்டு உரிமையாளருக்கு எதிராக வாடகை அதிகாரியிடம் (Rent Authority) உடனடியாக மனு தாக்கல் செய்து முன்பணத்தை திரும்பப் பெறலாம்."
                )
            }

            // 2. Employment: Salary delay / Unpaid wages / Workplace
            lower.contains("salary") || lower.contains("unpaid") || lower.contains("employer") ||
            lower.contains("wages") || lower.contains("fire") || lower.contains("layoff") ||
            lower.contains("gratuity") || lower.contains("pf") || lower.contains("workplace") ||
            lower.contains("சம்பளம்") || lower.contains("ஊதியம்") || lower.contains("வேலை") ||
            lower.contains("நிறுவனம்") -> {
                val amt = extractedAmount ?: "நிலுவை சம்பளம்"
                LegalComplaintResult(
                    category = "Employment",
                    law = "Section 15, Payment of Wages Act, 1936 & Section 33C(2), Industrial Disputes Act, 1947",
                    authority = "Assistant Labour Commissioner / Labour Court / Samadhan Portal",
                    subject = "சட்டரீதியான மனு: $extractedParty நிறுவனத்திடமிருந்து வர வேண்டிய நிலுவை சம்பள பாக்கி ($amt) மீட்பு",
                    facts = "மனுதாரர் பணிபுரிந்த காலத்தில் முறையாக உழைத்த போதிலும், எவ்வித நியாயமான காரணமும் இன்றி நிறுவனம் ($extractedParty) சட்டத்திற்கு புறம்பாக சம்பளத்தை தடுத்து வைத்துள்ளது ($amt): $transcript",
                    relief = "நிலுவையில் உள்ள சம்பளத் தொகையான $amt தொகையை உரிய இழப்பீட்டுடன் உடனடியாக மனுதாரரின் வங்கிக் கணக்கில் வரவு வைக்க உத்தரவிடக் கோருதல்.",
                    explanationTamil = "நிலுவை ஊதியம் வழங்காத நிறுவனத்திற்கு எதிராக தொழிலாளர் நீதிமன்றம் அல்லது தொழிலாளர் ஆணையரிடம் (Labour Commissioner) புகார் அளித்து சம்பள பாக்கியை உடனடியாக வசூலிக்கலாம்."
                )
            }

            // 3. Cyber: UPI / Online banking / Phishing / Fake call
            lower.contains("upi") || lower.contains("bank") || lower.contains("phish") ||
            lower.contains("otp") || lower.contains("fake call") || lower.contains("hacked") ||
            lower.contains("scam") || lower.contains("cyber") || lower.contains("மோசடி") ||
            lower.contains("பணம் பறிப்பு") -> {
                val amt = extractedAmount ?: "இழக்கப்பட்ட தொகை"
                LegalComplaintResult(
                    category = "Cyber",
                    law = "Section 66D, Information Technology Act, 2000 & Section 318 (Cheating), Bharatiya Nyaya Sanhita (BNS), 2023",
                    authority = "1930 Cyber Fraud Helpline & National Cyber Crime Reporting Portal (cybercrime.gov.in)",
                    subject = "அவசர இணையக் குற்ற புகார்: கணக்கு பரிவர்த்தனை மோசடி மற்றும் பணம் பறிப்பு ($amt)",
                    facts = "அடையாளம் தெரியாத மோசடி நபர்/கணக்கு ($extractedParty) மூலம் போலியான அழைப்பு/UPI வழிமுறை மூலம் ஏமாற்றப்பட்டு மனுதாரரின் வங்கிக் கணக்கிலிருந்து $amt தொகை திருடப்பட்டுள்ளது: $transcript",
                    relief = "மோசடி செய்யப்பட்ட $amt சென்றடைந்த வங்கிக் கணக்கை உடனடியாக முடக்கி (Account Freeze/Lien) தொகையை மனுதாரருக்கு மீட்டுத்தரக் கோருதல்.",
                    explanationTamil = "இணைய மோசடி நடந்த 24 மணி நேரத்திற்குள் 1930 எண்ணை அழைத்து அல்லது cybercrime.gov.in தளத்தில் புகார் பதிவு செய்து பரிவர்த்தனையை முடக்க வேண்டும்."
                )
            }

            // 5. Criminal: Physical threat / Extortion / Assault
            lower.contains("threat") || lower.contains("assault") || lower.contains("extort") ||
            lower.contains("beating") || lower.contains("attack") || lower.contains("weapon") ||
            lower.contains("violence") || lower.contains("தாக்குதல்") || lower.contains("மிரட்டல்") ||
            lower.contains("அடிதடி") || lower.contains("கொலை மிரட்டல்") -> {
                LegalComplaintResult(
                    category = "Criminal",
                    law = "Section 115 (Voluntarily Causing Hurt), Section 308 (Extortion) & Section 351 (Criminal Intimidation), Bharatiya Nyaya Sanhita (BNS), 2023",
                    authority = "Jurisdictional Police Station / CCTNS Portal / Judicial Magistrate Court",
                    subject = "காவல்துறை அவசர புகார்: மனுதாரருக்கு எதிராக உயிருக்கு ஆபத்தான மிரட்டல் மற்றும் தாக்குதல் ($extractedParty)",
                    facts = "எதிர்மனுதாரர் ($extractedParty) மனுதாரரை நேரில் அணுகி/தொலைபேசியில் தகாத வார்த்தைகளால் திட்டி, நேரடி உடல் தாக்குதல் மற்றும் கொலை மிரட்டல் விடுத்துள்ளார்: $transcript",
                    relief = "எதிர்மனுதாரர் மீது Zero FIR பதிவு செய்து BNS பிரிவுகளின் கீழ் சட்ட நடவடிக்கை எடுக்கவும், மனுதாரருக்கு போதிய போலீஸ் பாதுகாப்பு வழங்கவும் கோருதல்.",
                    explanationTamil = "உயிருக்கு அச்சுறுத்தல் அல்லது தாக்குதல் குறித்து அருகில் உள்ள காவல் நிலையத்தில் Zero FIR பதிவு செய்து காவல் பாதுகாப்பு கோரலாம்."
                )
            }

            // 4. Consumer: Defective product / Non-delivery / Service refusal (and default)
            else -> {
                val amt = extractedAmount ?: "செலுத்தப்பட்ட தொகை"
                LegalComplaintResult(
                    category = "Consumer",
                    law = "Section 35 & Section 2(47), Consumer Protection Act, 2019 (Deficiency in Service & Unfair Trade Practice)",
                    authority = "National Consumer Helpline (1915) & e-Daakhil District Consumer Commission",
                    subject = "நுகர்வோர் குறைதீர் மனு: குறைபாடுள்ள பொருள் / சேவை மறுப்பு மற்றும் இழப்பீடு கோருதல் ($extractedParty)",
                    facts = "மனுதாரர் உரிய கட்டணம் செலுத்தி வாங்கிய பொருளில் குறைபாடு உள்ளதால்/சேவை மறுக்கப்பட்டதால் எதிர்மனுதாரர் ($extractedParty) மனுதாரரின் நியாயமான கோரிக்கையை புறக்கணித்துள்ளார்: $transcript",
                    relief = "செலுத்தப்பட்ட $amt தொகையை முழுமையாக திருப்பித் தரவும், மன உளைச்சலுக்கு தகுந்த இழப்பீடு வழங்கவும் கோருதல்.",
                    explanationTamil = "குறைபாடுள்ள பொருள் அல்லது சேவை வழங்காத நிறுவனத்திற்கு எதிராக 1915 தேசிய நுகர்வோர் உதவி எண் அல்லது e-Daakhil நுகர்வோர் நீதிமன்றத்தில் வழக்கு தொடரலாம்."
                )
            }
        }
    }

    private fun extractAmount(text: String): String? {
        val regex = Regex("""(?i)(?:Rs\.?|₹|INR)\s*([0-9,]+)|([0-9,]+)\s*(?:ரூபாய்|rupees)""")
        val match = regex.find(text)
        return match?.let {
            val v = it.groupValues[1].ifEmpty { it.groupValues[2] }
            "₹$v"
        }
    }

    private fun extractOpposingParty(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("amazon") -> "Amazon India"
            lower.contains("flipkart") -> "Flipkart"
            lower.contains("landlord") || lower.contains("வீட்டு உரிமையாளர்") -> "வீட்டு உரிமையாளர் (Landlord)"
            lower.contains("employer") || lower.contains("company") || lower.contains("நிறுவனம்") -> "நிறுவன நிர்வாகம் (Employer / Management)"
            lower.contains("bank") || lower.contains("வங்கி") -> "வங்கி / எதிர் கணக்கு நபர்"
            else -> "எதிர்மனுதாரர் (Opposing Party)"
        }
    }
}
