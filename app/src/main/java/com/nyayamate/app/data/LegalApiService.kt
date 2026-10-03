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
import org.json.JSONObject

data class DynamicLawResponse(
    val category: String,
    val applicableLaw: String,
    val authority: String,
    val immediateNextStep: String,
    val formalDraftSubject: String,
    val formalDraftFacts: String,
    val tamilExplanation: String
)

class LegalApiService(private val apiKey: String = BuildConfig.GEMINI_API_KEY) {

    private companion object {
        const val TAG = "LegalApiService"
        val SYSTEM_PROMPT = """
            You are Justra's Real-Time Dynamic Legal Classifier under Indian Law.
            STRICT RULE: NEVER DEFAULT TO CONSUMER LAW UNLESS IT IS A GOODS/SELLER DEFECT.
            You MUST dynamically analyze the exact factual narrative provided by the user and map it strictly:

            1. Landlord / Rent / Advance Deposit / Eviction:
               -> Law: Model Tenancy Act / State Rent Control Act
               -> Authority: Rent Authority / Rent Court / Civil Court

            2. Unpaid Salary / Wage Delay / Wrongful Termination / PF:
               -> Law: Payment of Wages Act, 1936 / Industrial Disputes Act
               -> Authority: Labor Commissioner Office / Labor Court

            3. Online Scam / Phishing / OTP Fraud / Cyber Harassment:
               -> Law: Information Technology Act, 2000 (Section 43/66) & Section 318 BNS
               -> Authority: National Cyber Crime Portal (cybercrime.gov.in / 1930 Helpline)

            4. Defective Product / Warranty Refusal / Non-delivery / Fake Seller:
               -> Law: Consumer Protection Act, 2019
               -> Authority: e-Daakhil Portal / National Consumer Helpline (1915)

            5. Threat / Physical Assault / Extortion / Cheating:
               -> Law: Bharatiya Nyaya Sanhita (BNS) & BNSS
               -> Authority: Local Police Station (FIR) / 112 Helpline

            Respond ONLY in this JSON structure:
            {
              "category": "string",
              "applicableLaw": "string (Exact sections and Act)",
              "authority": "string (Exact portal or officer)",
              "immediateNextStep": "string (Practical 1-2 steps)",
              "formalDraftSubject": "string (Dynamic subject based on facts)",
              "formalDraftFacts": "string (Summary of user's actual facts)",
              "tamilExplanation": "string (Clear 2 lines in Tamil explaining what to do)"
            }
        """.trimIndent()
    }

    private val worldwideClassifier = WorldwideLegalClassifier(apiKey)

    suspend fun analyzeWorldwideGrievance(
        userProblem: String,
        userContext: UserLegalContext? = null
    ): WorldwideLegalResponse = worldwideClassifier.analyzeCase(userProblem, userContext)

    // USER INPUT-ஐ டைரக்டாக வாங்கும் ஃபங்ஷன்
    suspend fun analyzeGrievance(userProblem: String): DynamicLawResponse = withContext(Dispatchers.IO) {
        val trimmed = userProblem.trim()
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = "User Complaint:\n\"$trimmed\""
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt)),
                            role = "user"
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.2f,
                        maxOutputTokens = 2048,
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

                val json = JSONObject(cleaned)
                return@withContext DynamicLawResponse(
                    category = json.optString("category", "General Legal Issue"),
                    applicableLaw = json.optString("applicableLaw", "சட்டப் பிரிவு ஆராயப்படுகிறது"),
                    authority = json.optString("authority", "சம்பந்தப்பட்ட குறைதீர்ப்பு மையம்"),
                    immediateNextStep = json.optString("immediateNextStep", "ஆதாரங்களை உடனடியாகப் பத்திரப்படுத்தவும்."),
                    formalDraftSubject = json.optString("formalDraftSubject", "புகார் மனு"),
                    formalDraftFacts = json.optString("formalDraftFacts", trimmed),
                    tamilExplanation = json.optString("tamilExplanation", "உங்கள் பிரச்சனையை தீர்க்க தேவையான நடவடிக்கைகள்.")
                )
            } catch (e: Exception) {
                Log.e(TAG, "API Error: ${e.message}", e)
            }
        }

        // Offline deterministic fallback (strictly maps category correctly)
        fallbackAnalyze(trimmed)
    }

    private fun fallbackAnalyze(userProblem: String): DynamicLawResponse {
        val lower = userProblem.lowercase()
        return when {
            // 1. Rent / Tenancy
            lower.contains("rent") || lower.contains("landlord") || lower.contains("tenant") ||
            lower.contains("deposit") || lower.contains("advance") || lower.contains("evict") ||
            lower.contains("வாடகை") || lower.contains("அட்வான்ஸ்") || lower.contains("வீட்டு உரிமையாளர்") -> {
                DynamicLawResponse(
                    category = "வாடகை மற்றும் குத்தகை விவகாரம் (Tenancy Dispute)",
                    applicableLaw = "Model Tenancy Act / தமிழ்நாடு நில உரிமையாளர் மற்றும் வாடகைதாரர் சட்டம், 2017",
                    authority = "மாவட்ட வாடகை அதிகாரம் (Rent Authority / Rent Court) / உரிமையியல் நீதிமன்றம்",
                    immediateNextStep = "வாடகை ஒப்பந்தம் (Rental Agreement) மற்றும் வங்கி பரிவர்த்தனை சான்றுகளை சேகரித்து வாடகை ஆணையரிடம் மனு அளிக்கவும்.",
                    formalDraftSubject = "வாடகை முன்பணத்தை (Advance Deposit) திருப்பித் தராமல் அத்துமீறும் உரிமையாளர் மீது நடவடிக்கை கோருதல்",
                    formalDraftFacts = userProblem,
                    tamilExplanation = "வீட்டு உரிமையாளர் அட்வான்ஸ் தொகையை தராமல் அலைக்கழித்தால் வாடகை நீதிமன்றத்தில் (Rent Court) புகார் செய்து தொகையை சட்டப்பூர்வமாக வசூலிக்கலாம்."
                )
            }

            // 2. Salary / Labor
            lower.contains("salary") || lower.contains("wage") || lower.contains("unpaid") ||
            lower.contains("fired") || lower.contains("terminate") || lower.contains("provident") ||
            lower.contains("pf") || lower.contains("சம்பளம்") || lower.contains("ஊதியம்") ||
            lower.contains("வேலை நீக்கம்") || lower.contains("நிறுவனம்") -> {
                DynamicLawResponse(
                    category = "தொழிலாளர் மற்றும் ஊதிய பாக்கி (Labor & Salary)",
                    applicableLaw = "Payment of Wages Act, 1936 & Industrial Disputes Act, 1947",
                    authority = "தொழிலாளர் ஆணையர் அலுவலகம் (Labour Commissioner Office) / தொழிலாளர் நீதிமன்றம்",
                    immediateNextStep = "பணி நியமன ஆணை (Appointment Letter), சம்பள சீட்டு (Pay slips) மற்றும் வங்கி கணக்கு விவரங்களுடன் தொழிலாளர் அலுவலரிடம் மனு அளிக்கவும்.",
                    formalDraftSubject = "சட்டவிரோதமாக நிறுத்தி வைக்கப்பட்டுள்ள சம்பள பாக்கியை வட்டியுடன் பெற்றுத்தர கோருதல்",
                    formalDraftFacts = userProblem,
                    tamilExplanation = "நியாயமற்ற முறையில் சம்பளத்தை பிடித்தம் செய்த நிறுவனத்திற்கு எதிராக தொழிலாளர் ஆணையரிடம் முறையிட்டு உங்கள் ஊதியத்தை பெற்றுக்கொள்ளலாம்."
                )
            }

            // 3. Cyber / OTP / Scam
            lower.contains("scam") || lower.contains("phishing") || lower.contains("otp") ||
            lower.contains("upi") || lower.contains("cyber") || lower.contains("hacked") ||
            lower.contains("fraud") || lower.contains("மோசடி") || lower.contains("பணம் பறிப்பு") ||
            lower.contains("ஆன்லைன் ஏமாற்று") -> {
                DynamicLawResponse(
                    category = "இணையக் குற்ற மோசடி (Cyber Fraud)",
                    applicableLaw = "Information Technology Act, 2000 (Section 43/66) & Section 318 (Cheating), Bharatiya Nyaya Sanhita (BNS), 2023",
                    authority = "தேசிய இணையக் குற்ற போர்ட்டல் (cybercrime.gov.in) & 1930 அவசர உதவி எண்",
                    immediateNextStep = "உடனடியாக 1930 எண்ணை அழைத்து வங்கி கணக்கை முடக்க (Freeze) செய்து, cybercrime.gov.in-ல் பரிவர்த்தனை ஐடியுடன் புகார் பதிவு செய்யவும்.",
                    formalDraftSubject = "அவசர இணைய நிதி மோசடி புகார்: வங்கி பரிவர்த்தனை மற்றும் மோசடி நபரின் கணக்கை முடக்குதல்",
                    formalDraftFacts = userProblem,
                    tamilExplanation = "ஆன்லைன் மோசடி நடந்த உடனே 1930 எண்ணில் தொடர்பு கொண்டு வங்கியுடன் இணைந்து பணத்தை மீட்க துரித நடவடிக்கை எடுக்கவும்."
                )
            }

            // 5. Threat / Assault / Extortion
            lower.contains("threat") || lower.contains("assault") || lower.contains("extort") ||
            lower.contains("beat") || lower.contains("attack") || lower.contains("kill") ||
            lower.contains("மிரட்டல்") || lower.contains("தாக்குதல்") || lower.contains("அடிதடி") ||
            lower.contains("கொலை மிரட்டல்") -> {
                DynamicLawResponse(
                    category = "குற்றவியல் அச்சுறுத்தல் (Criminal Intimidation & Threat)",
                    applicableLaw = "Bharatiya Nyaya Sanhita (BNS), 2023 (Section 115, 308, 351) & BNSS, 2023",
                    authority = "உள்ளூர் காவல் நிலையம் (Jurisdictional Police Station) / 112 அவசர உதவி எண்",
                    immediateNextStep = "அருகில் உள்ள காவல் நிலையத்தில் உடனடியாக Zero FIR பதிவு செய்து எழுத்துப்பூர்வ ரசீது (CSR) பெற்றுக்கொள்ளவும்.",
                    formalDraftSubject = "காவல்துறை அவசர புகார் மனு: உயிருக்கு ஆபத்தான மிரட்டல் மற்றும் சட்டவிரோத தாக்குதல் மீது நடவடிக்கை",
                    formalDraftFacts = userProblem,
                    tamilExplanation = "நேரடி அச்சுறுத்தல் அல்லது தாக்குதல் குறித்து உடனடியாக 112 அழைக்கவும் அல்லது காவல் நிலையத்தில் Zero FIR பதிவு செய்யவும்."
                )
            }

            // 4. Consumer (Default only if defective product or goods/service defect)
            else -> {
                DynamicLawResponse(
                    category = "நுகர்வோர் குறைபாடு (Consumer Grievance)",
                    applicableLaw = "Consumer Protection Act, 2019 (Section 35 & 2(47))",
                    authority = "தேசிய நுகர்வோர் உதவி மையம் (NCH 1915) & e-Daakhil நுகர்வோர் ஆணையம்",
                    immediateNextStep = "பொருள் வாங்கிய ரசீது (Invoice), உத்திரவாத அட்டை (Warranty Card) மற்றும் குறைபாடுள்ள ஆதாரங்களுடன் நுகர்வோர் போர்ட்டலில் புகார் அளிக்கவும்.",
                    formalDraftSubject = "குறைபாடுள்ள பொருள் மற்றும் சேவை மறுப்பிற்கு நஷ்டஈடு கோரும் நுகர்வோர் மனு",
                    formalDraftFacts = userProblem,
                    tamilExplanation = "வாங்கிய பொருளில் குறைபாடு அல்லது போலியான உத்தரவாதம் குறித்து 1915 எண்ணில் அல்லது e-Daakhil இணையத்தில் வழக்கு தொடரலாம்."
                )
            }
        }
    }
}
