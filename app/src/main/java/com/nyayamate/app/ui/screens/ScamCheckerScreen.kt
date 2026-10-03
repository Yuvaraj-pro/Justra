package com.nyayamate.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.api.GeminiApiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.nyayamate.app.utils.ActionUtils
import com.nyayamate.app.utils.InputType
import com.nyayamate.app.utils.ScamInputValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class ScamAnalysisResult(
    val status: String,
    val riskLevel: String,
    val isLegitimateLink: Boolean,
    val threatType: String,
    val indicators: List<String>,
    val actionText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamCheckerScreen(
    apiKey: String = BuildConfig.GEMINI_API_KEY,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var inputQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var analysisResult by remember { mutableStateOf<ScamAnalysisResult?>(null) }

    val resolvedApiKey = if (apiKey.isNotBlank()) apiKey else BuildConfig.GEMINI_API_KEY

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("மோசடி கண்டறிதல் (Scam & Fraud Checker)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F1E36))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFAF7F2))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF7F2))
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "சந்தேகத்திற்குரிய லிங்க் (Link) அல்லது மெசேஜை உள்ளிடவும்:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF14181F)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputQuery,
                onValueChange = {
                    inputQuery = it
                    validationError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                placeholder = {
                    Text("எ.கா: https://sbi-kyc-check.xyz அல்லது உங்களுக்கு வந்த SMS மெசேஜ்...", color = Color(0xFF5A606A))
                },
                textStyle = TextStyle(color = Color(0xFF14181F), fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF14181F),
                    unfocusedTextColor = Color(0xFF14181F),
                    focusedContainerColor = Color(0xFFF3ECE1),
                    unfocusedContainerColor = Color(0xFFF3ECE1),
                    cursorColor = Color(0xFF0F1E36),
                    focusedBorderColor = Color(0xFF0F1E36),
                    unfocusedBorderColor = Color(0xFFD4CAB8)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            validationError?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = it, color = Color(0xFFB3261E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val inputType = ScamInputValidator.determineInputType(inputQuery)
                    if (inputType == InputType.INVALID_GIBBERISH) {
                        validationError = "தவறான உள்ளீடு: அர்த்தமற்ற எழுத்துக்களைத் தவிர்த்து, முழுமையான Link அல்லது SMS உள்ளிடவும்."
                        analysisResult = null
                        return@Button
                    }

                    isLoading = true
                    validationError = null

                    scope.launch {
                        try {
                            val systemPrompt = """
                            You are Justra's Automated Scam, Fraud, and Malicious Link Detection Engine.
                            Analyze citizen-submitted texts, payment links, APK offers, and SMS messages.

                            ================================================================================
                            STRICT CLASSIFICATION TAXONOMY:
                            ================================================================================
                            1. RANDOM GIBBERISH / NON-LINK INPUTS:
                               - If the user provides random letters/numbers without coherent sentences, valid URLs, or actionable fraud context:
                                 * Set `riskLevel` to "UNVERIFIED_INPUT" or "INVALID_QUERY".
                                 * Set `isLegitimateLink` to false.
                                 * DO NOT classify this as "LOW_RISK" or "SAFE". Inform the user that the input is unrecognized gibberish.

                            2. HIGH-RISK SIGNALS (RISK_LEVEL: "CRITICAL" / "HIGH"):
                               - Brand impersonation (e.g., "sbi-kyc-update.xyz", "indiapost-tracking.top").
                               - Demands for immediate remote access tools (AnyDesk, TeamViewer, RustDesk).
                               - Electricity bill disconnection threats within 2 hours.
                               - Lottery, lottery scratch cards, part-time Telegram review tasks, or crypto doubling.
                               - Unsolicited APK download links sent over WhatsApp or SMS.

                            3. STRUCTURED JSON OUTPUT ONLY:
                            {
                              "status": "VALID_ANALYSIS | INVALID_INPUT",
                              "riskLevel": "CRITICAL | HIGH | MEDIUM | LOW | INVALID_QUERY",
                              "isLegitimateLink": false,
                              "detectedThreatType": "Brand Impersonation | Phishing Link | Fake Task Scam | Unknown/Gibberish",
                              "indicators": ["indicator 1", "indicator 2"],
                              "recommendedAction": "Actionable guidance in simple English and Tamil",
                              "emergencyDial": "1930"
                            }
                            """.trimIndent()

                            val prompt = """
                            Analyze this user text for scam indicators:
                            "$inputQuery"
                            Return structured JSON matching the defined schema.
                            """.trimIndent()

                            val request = GeminiRequest(
                                contents = listOf(
                                    GeminiContent(
                                        parts = listOf(GeminiPart(text = prompt)),
                                        role = "user"
                                    )
                                ),
                                generationConfig = GeminiGenerationConfig(
                                    temperature = 0.1f,
                                    responseMimeType = "application/json"
                                ),
                                systemInstruction = GeminiContent(
                                    parts = listOf(GeminiPart(text = systemPrompt))
                                )
                            )

                            val response = withContext(Dispatchers.IO) {
                                GeminiApiClient.service.generateContent(resolvedApiKey, request)
                            }
                            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "{}"
                            val cleanedJson = rawText.trim()
                                .removePrefix("```json")
                                .removePrefix("```")
                                .removeSuffix("```")
                                .trim()
                            val json = JSONObject(cleanedJson)
                            val indicatorsList = mutableListOf<String>()
                            val indicatorsArray = json.optJSONArray("indicators")
                            if (indicatorsArray != null) {
                                for (i in 0 until indicatorsArray.length()) {
                                    indicatorsList.add(indicatorsArray.getString(i))
                                }
                            }

                            analysisResult = ScamAnalysisResult(
                                status = json.optString("status", "VALID_ANALYSIS"),
                                riskLevel = json.optString("riskLevel", "UNKNOWN"),
                                isLegitimateLink = json.optBoolean("isLegitimateLink", false),
                                threatType = json.optString("detectedThreatType", "Unverified Query"),
                                indicators = indicatorsList,
                                actionText = json.optString("recommendedAction", "எந்தவொரு தனிப்பட்ட விவரங்களையும் பகிர வேண்டாம்.")
                            )
                        } catch (e: Exception) {
                            validationError = "பகுப்பாய்வு தோல்வி அடைந்தது: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = inputQuery.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("பாதுகாப்பைச் சரிபார் (Analyze Risk)", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            analysisResult?.let { res ->
                val isHighRisk = res.riskLevel.equals("CRITICAL", ignoreCase = true) || 
                                 res.riskLevel.equals("HIGH", ignoreCase = true)
                val cardBg = if (isHighRisk) Color(0xFFFCDAD4) else Color(0xFFD1E7D0)
                val badgeColor = if (isHighRisk) Color(0xFF5E130A) else Color(0xFF0F3815)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, badgeColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ஆபத்து நிலை: ${res.riskLevel}",
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                fontSize = 15.sp
                            )
                            Icon(Icons.Default.Warning, contentDescription = null, tint = badgeColor)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("மோசடி வகை:", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                        Text(res.threatType, color = Color(0xFF14181F), fontSize = 14.sp)

                        if (res.indicators.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("முக்கிய காரணங்கள்:", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                            res.indicators.forEach { indicator ->
                                Text("• $indicator", color = Color(0xFF14181F), fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("பரிந்துரைக்கப்படும் நடவடிக்கை:", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                        Text(res.actionText, color = Color(0xFF14181F), fontSize = 13.sp)

                        if (isHighRisk) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { ActionUtils.dialEmergencyHelpline(context, "1930") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("உடனடி புகார்: 1930 ஐ அழைக்கவும்", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
