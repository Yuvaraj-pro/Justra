package com.justra.app.data

import android.util.Log
import com.justra.app.BuildConfig
import com.justra.app.data.api.GeminiApiClient
import com.justra.app.data.api.GeminiContent
import com.justra.app.data.api.GeminiGenerationConfig
import com.justra.app.data.api.GeminiPart
import com.justra.app.data.api.GeminiRequest
import com.justra.app.domain.model.LegalComplaintResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class LegalAnalysisResponse(
    @Json(name = "category") val category: String,
    @Json(name = "applicableLaw") val applicableLaw: String,
    @Json(name = "authority") val authority: String,
    @Json(name = "immediateNextStep") val immediateNextStep: String,
    @Json(name = "formalDraftSubject") val formalDraftSubject: String,
    @Json(name = "formalDraftFacts") val formalDraftFacts: String,
    @Json(name = "demandedRelief") val demandedRelief: String,
    @Json(name = "explanationTamil") val explanationTamil: String
) {
    fun toLegalComplaintResult(): LegalComplaintResult = LegalComplaintResult(
        category = category,
        law = applicableLaw,
        authority = authority,
        subject = formalDraftSubject,
        facts = formalDraftFacts,
        relief = demandedRelief,
        explanationTamil = explanationTamil
    )
}

/**
 * Dynamic Legal Classifier & Drafting Service for Indian Jurisprudence.
 * Eliminates static boilerplate responses by evaluating specific facts
 * against statutory frameworks:
 * - Tenancy: Model Tenancy Act / Rent Control Acts
 * - Employment: Payment of Wages Act, 1936 / Industrial Disputes Act
 * - Cyber/UPI: IT Act 2000 (Sec 43/66) & Section 318 BNS (Helpline 1930)
 * - Consumer: Consumer Protection Act, 2019 (e-Daakhil / 1915)
 * - Criminal: Bharatiya Nyaya Sanhita (BNS, 2023) & BNSS (Police 112)
 */
object LegalApiService {
    private const val TAG = "LegalApiService"

    suspend fun analyzeLegalGrievance(
        narrativeText: String,
        preferredLanguage: String = "ta",
        jurisdictionCode: String = "IN"
    ): LegalAnalysisResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val trimmed = narrativeText.trim()

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = """
                    You are Justra (ஜஸ்ட்ரா), a Worldwide Senior Legal Counsel and Statutory Classifier.
                    Your duty is to accurately map the user's specific problem narrative to its genuine legal code under the targeted sovereign jurisdiction ($jurisdictionCode).
                    
                    ACTIVE JURISDICTION CONTEXT: $jurisdictionCode
                    STRICT WORLDWIDE JURISPRUDENCE RULES:
                    - INDIA (IN): Map to Bharatiya Nyaya Sanhita (BNS) / BNSS 2023, Model Tenancy Act, IT Act 2000 (Sec 43/66D, Helpline 1930), Consumer Protection Act 2019 (1915).
                    - UNITED KINGDOM (GB): Map to Consumer Rights Act 2015, Housing Act, Employment Rights Act 1996, Online Safety Act (Helpline 999 / Action Fraud).
                    - UNITED STATES (US): Map to Uniform Residential Landlord Act, FLSA (Wage & Hour Div 1-866-4-US-WAGE), FTC Act, Title 18 Cyber (Helpline 911 / IC3).
                    - SINGAPORE (SG): Map to Penal Code 1871, Employment Act, Personal Data Protection Act, Protection from Harassment Act (Helpline 999 / 1800-221-4444).
                    - UAE / MIDDLE EAST (AE): Map to Federal Decree Law No. 33 (Labor), Federal Decree Law No. 34 (Cybercrime), Rental Dispute Settlement Center (Helpline 999).
                    - OTHER NATIONS: Map strictly to the governing statutory acts, administrative tribunals, and emergency helplines of jurisdiction code $jurisdictionCode.

                    DO NOT default everything to the Consumer Protection Act.
                    You MUST return strict JSON matching this exact structure:
                    {
                      "category": "Rental Dispute" | "Employment & Salary" | "Cyber & Financial Fraud" | "Consumer Grievance" | "Criminal Threat & Safety",
                      "applicableLaw": "Full statutory title with specific sections of jurisdiction $jurisdictionCode",
                      "authority": "Exact government portal or tribunal name with local helpline number",
                      "immediateNextStep": "Immediate concrete action the citizen must take today",
                      "formalDraftSubject": "Formal subject line for legal notice/complaint",
                      "formalDraftFacts": "Concise factual statement summarizing the grievance in formal legal prose",
                      "demandedRelief": "Exact remedy, statutory interest, refund, or penalty demanded",
                      "explanationTamil": "தெளிவான தமிழ் விளக்கம் மற்றும் அடுத்த கட்ட நடவடிக்கை (Clear advice in user native language / Tamil)"
                    }
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = "Target Jurisdiction Code: $jurisdictionCode\nCitizen's Grievance: $trimmed\nPreferred Language: $preferredLanguage")),
                            role = "user"
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.15f,
                        maxOutputTokens = 2048,
                        responseMimeType = "application/json"
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = systemPrompt))
                    )
                )

                val response = GeminiApiClient.service.generateContent(apiKey, request)
                val rawJson = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!rawJson.isNullOrBlank()) {
                    val parsed = parseJsonResponse(rawJson)
                    if (parsed != null) {
                        return@withContext parsed
                    }
                }
                throw IllegalStateException("Received empty JSON payload from Gemini AI API")
            } catch (e: Exception) {
                Log.e(TAG, "Gemini API HTTP/Network Failure: ${e.javaClass.simpleName} - ${e.message}", e)
                throw e
            }
        }

        // Intelligent Offline Statutory Classifier for demo or no-key state
        classifyDynamicallyOffline(trimmed, preferredLanguage)
    }

    private fun parseJsonResponse(rawJson: String): LegalAnalysisResponse? {
        return try {
            val cleaned = rawJson.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleaned)
            LegalAnalysisResponse(
                category = json.optString("category", "Legal Grievance"),
                applicableLaw = json.optString("applicableLaw", "Indian Statutory Framework"),
                authority = json.optString("authority", "Jurisdictional Authority"),
                immediateNextStep = json.optString("immediateNextStep", "Issue formal written notice"),
                formalDraftSubject = json.optString("formalDraftSubject", "Legal Notice for Statutory Redressal"),
                formalDraftFacts = json.optString("formalDraftFacts", "Factual narrative regarding the dispute."),
                demandedRelief = json.optString("demandedRelief", "Immediate restitution and cessation of violation."),
                explanationTamil = json.optString("explanationTamil", "சட்ட ரீதியான அறிவிப்பு மற்றும் உரிய அதிகாரியிடம் புகார் அளிக்கவும்.")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini JSON: $rawJson", e)
            null
        }
    }

    /**
     * Precision offline classifier mapping keywords and context to exact statutory frameworks.
     */
    fun classifyDynamicallyOffline(narrative: String, preferredLanguage: String = "ta"): LegalAnalysisResponse {
        val textLower = narrative.lowercase()

        val isTheftOrRobbery = textLower.contains("theft") || textLower.contains("stolen") ||
                textLower.contains("stole") || textLower.contains("robbery") ||
                textLower.contains("snatching") || textLower.contains("thief") ||
                textLower.contains("burglar") || textLower.contains("bike") ||
                textLower.contains("vehicle") || textLower.contains("stolen bike") ||
                textLower.contains("திருட்டு") || textLower.contains("பைக்") ||
                textLower.contains("பொருட்கள் திருடு") || textLower.contains("களவு")

        val isRentOrTenancy = textLower.contains("rent") || textLower.contains("deposit") ||
                textLower.contains("tenant") || textLower.contains("landlord") ||
                textLower.contains("evict") || textLower.contains("owner") ||
                textLower.contains("flat") || textLower.contains("வாடகை") ||
                textLower.contains("முன்பணம்") || textLower.contains("வீட்டு")

        val isEmploymentOrSalary = textLower.contains("salary") || textLower.contains("wage") ||
                textLower.contains("employer") || textLower.contains("company") ||
                textLower.contains("boss") || textLower.contains("fired") ||
                textLower.contains("termination") || textLower.contains("bonus") ||
                textLower.contains("சம்பளம்") || textLower.contains("வேலை") ||
                textLower.contains("பணி")

        val isCyberOrUPI = textLower.contains("upi") || textLower.contains("otp") ||
                textLower.contains("scam") || textLower.contains("fraud") ||
                textLower.contains("hacked") || textLower.contains("phishing") ||
                textLower.contains("bank account") || textLower.contains("sim") ||
                textLower.contains("மோசடி") || textLower.contains("வங்கி") ||
                textLower.contains("பணம் திருட்டு")

        val isCriminalOrThreat = textLower.contains("threat") || textLower.contains("assault") ||
                textLower.contains("kill") || textLower.contains("blackmail") ||
                textLower.contains("abuse") || textLower.contains("harass") ||
                textLower.contains("police") || textLower.contains("மிரட்டல்") ||
                textLower.contains("தாக்குதல்") || textLower.contains("கொலை")

        return when {
            isTheftOrRobbery -> LegalAnalysisResponse(
                category = "Criminal Theft & Robbery",
                applicableLaw = "Bharatiya Nyaya Sanhita (BNS), 2023 (Section 303 - Theft, Section 309 - Robbery) & BNSS, 2023 (Section 173)",
                authority = "Station House Officer (SHO), Local Police Station / Emergency Police 112",
                immediateNextStep = "Report to local police immediately to register Zero FIR under Section 173 BNSS and secure CSR receipt.",
                formalDraftSubject = "Police Complaint: Immediate Registration of FIR under Section 303 BNS 2023 for Theft",
                formalDraftFacts = "The complainant's property/vehicle was stolen without consent by unknown/identified perpetrator, constituting a cognizable offense of Theft under BNS Section 303.",
                demandedRelief = "Immediate registration of regular FIR under BNS Section 303, investigation, recovery of stolen property, and arrest of culprit.",
                explanationTamil = "திருட்டு சம்பவத்திற்கு பாரதீய நியாய சன்ஹிதா (BNS 2023) பிரிவு 303 கீழ் காவல் நிலையத்தில் உடனடியாக எஃப்.ஐ.ஆர் (FIR) பதிவு செய்யவும். அவசர உதவிக்கு 112 எண்ணை அழைக்கலாம்."
            )
            isRentOrTenancy -> LegalAnalysisResponse(
                category = "Rental & Tenancy Dispute",
                applicableLaw = "Model Tenancy Act / TN Regulation of Rights and Responsibilities of Landlords and Tenants Act, 2017 (Sec 21 & 22)",
                authority = "Rent Authority / Rent Court (Sub-Divisional Magistrate)",
                immediateNextStep = "Issue 15-day statutory Demand Notice for refund of rental security deposit with 12% interest per annum.",
                formalDraftSubject = "Legal Demand Notice: Unlawful Withholding of Security Deposit and Claim for Immediate Refund",
                formalDraftFacts = "The complainant was a lawful tenant who duly surrendered vacant possession. Despite peaceful handover, the landlord unlawfully withheld the deposit balance in direct violation of statutory tenancy provisions.",
                demandedRelief = "Immediate refund of the full security deposit sum together with 18% penal interest and Rs. 25,000 towards mental agony.",
                explanationTamil = "வீட்டு வாடகை முன்பணத்தை சட்டவிரோதமாக பிடித்தம் செய்வது வாடகை சட்டப்படி குற்றமாகும். 15 நாள் காலக்கெடு விதித்து வக்கீல் நோட்டீஸ் அனுப்பி, வாடகை நீதிமன்றத்தில் (Rent Court) மனு தாக்கல் செய்யலாம்."
            )
            isEmploymentOrSalary -> LegalAnalysisResponse(
                category = "Employment & Unpaid Wages",
                applicableLaw = "Payment of Wages Act, 1936 (Section 15) & Industrial Disputes Act, 1947",
                authority = "Deputy Labour Commissioner / Labour Court",
                immediateNextStep = "Submit formal Form IV claim petition before the Authority under the Payment of Wages Act.",
                formalDraftSubject = "Statutory Claim Petition: Recovery of Arrears of Wages and Compensation under Section 15",
                formalDraftFacts = "The complainant rendered uninterrupted employment service. The employer unlawfully withheld salary/terminal dues exceeding the statutory 7-day period prescribed under the Payment of Wages Act.",
                demandedRelief = "Release of all pending arrears of wages along with statutory compensation up to 10 times the unpaid amount as mandated by law.",
                explanationTamil = "ஊதியத்தை உரிய தேதியில் தராமல் தாமதப்படுத்துவது அல்லது பிடித்தம் செய்வது சட்டப்படி குற்றமாகும். தொழிலாளர் நல ஆணையரிடம் (Labour Commissioner) புகார் அளிக்கலாம்; 10 மடங்கு இழப்பீடு கோர உரிமை உள்ளது."
            )
            isCyberOrUPI -> LegalAnalysisResponse(
                category = "Cyber Financial Fraud & Phishing",
                applicableLaw = "Information Technology Act, 2000 (Section 43, 66D) & Bharatiya Nyaya Sanhita (BNS), 2023 (Section 318 - Cheating)",
                authority = "National Cyber Crime Portal (cybercrime.gov.in / 1930 Helpline)",
                immediateNextStep = "Dial 1930 immediately within the golden hour to freeze beneficiary bank accounts, and register an online complaint.",
                formalDraftSubject = "Formal Cyber Crime Complaint: Urgent Freeze of Defrauded Funds and Registration of FIR",
                formalDraftFacts = "Unauthorized financial extraction occurred via deceptive cyber engineering/fraudulent UPI redirection without lawful consent of the account holder.",
                demandedRelief = "Urgent issuance of Section 94 BNSS notice to beneficiary banks to freeze stolen funds and initiate formal FIR under IT Act Section 66D.",
                explanationTamil = "உடனடியாக 1930 என்ற அவசர எண்ணை அழைத்து பணப் பரிவர்த்தனையை முடக்கச் செய்யுங்கள் (Freeze). cybercrime.gov.in தளத்தில் ஆதாரங்களுடன் புகார் பதிவு செய்து வங்கி மேலாளருக்கு நகல் அனுப்பவும்."
            )
            isCriminalOrThreat -> LegalAnalysisResponse(
                category = "Criminal Intimidation & Safety",
                applicableLaw = "Bharatiya Nyaya Sanhita (BNS), 2023 (Section 351 - Criminal Intimidation, Section 352 - Intentional Insult) & BNSS, 2023 (Section 173)",
                authority = "Station House Officer (SHO), Local Police Station / 112 Emergency Police",
                immediateNextStep = "Submit formal written complaint to SHO requesting immediate CSR / Zero FIR under Section 173 BNSS.",
                formalDraftSubject = "Formal Police Complaint: Immediate Registration of FIR for Criminal Threats and Extortion",
                formalDraftFacts = "The opposing party has issued unlawful threats of injury and physical harm, creating severe apprehension of danger to life and personal safety.",
                demandedRelief = "Registration of regular FIR under relevant sections of BNS, 2023, immediate preventive action under Section 126 BNSS, and police protection.",
                explanationTamil = "மிரட்டல் மற்றும் வன்முறை குறித்து உடனடியாக அருகிலுள்ள காவல் நிலையத்தில் எழுத்துப்பூர்வ புகார் அளித்து CSR ரசீது பெறவும். அவசரத்திற்கு 112 எண்ணை அழைக்கலாம்."
            )
            else -> LegalAnalysisResponse(
                category = "Consumer Grievance & Defective Goods/Services",
                applicableLaw = "Consumer Protection Act, 2019 (Section 2(7) - Deficient Service, Section 35)",
                authority = "District Consumer Disputes Redressal Commission (e-Daakhil Portal / National Consumer Helpline 1915)",
                immediateNextStep = "Dispatch 15-day pre-litigation legal demand notice requesting repair, replacement, or full refund.",
                formalDraftSubject = "Pre-Litigation Consumer Notice: Deficiency in Service and Demand for Immediate Rectification",
                formalDraftFacts = "The complainant purchased goods/services for valuable consideration. The provider failed to deliver the promised standard, resulting in quantifiable financial loss and hardship.",
                demandedRelief = "Full refund of paid consideration with 12% interest, plus Rs. 50,000 compensation for mental harassment and litigation costs.",
                explanationTamil = "பொருட்கள் அல்லது சேவையில் குறைபாடு இருப்பின், 15 நாட்கள் அவகாசத்தில் நோட்டீஸ் அனுப்பி, e-Daakhil இணையதளம் அல்லது 1915 மூலமாக நுகர்வோர் நீதிமன்றத்தில் வழக்கு தொடரலாம்."
            )
        }
    }
}
