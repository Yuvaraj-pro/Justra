package com.nyayamate.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.util.LegalInputValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class AccurateLegalResult(
    val status: String,
    val category: String,
    val specificLaw: String,
    val targetAuthority: String,
    val immediateStep: String,
    val formalDraft: String,
    val tamilGuidance: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalProblemIntakeScreen(
    apiKey: String = BuildConfig.GEMINI_API_KEY,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var userInput by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var legalResult by remember { mutableStateOf<AccurateLegalResult?>(null) }

    val resolvedApiKey = if (apiKey.isNotBlank()) apiKey else BuildConfig.GEMINI_API_KEY

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("சட்ட ஆலோசனை (Legal Intake)", fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36), fontSize = 18.sp) },
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
                text = "உங்கள் பிரச்சனையை விவரிக்கவும்:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF14181F)
            )
            Text(
                text = "எ.கா: வாடகை அட்வான்ஸ் தரவில்லை, 2 மாத சம்பளம் வரவில்லை, ஆன்லைன் பண மோசடி...",
                fontSize = 12.sp,
                color = Color(0xFF5A606A),
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            // High Contrast Text Area
            OutlinedTextField(
                value = userInput,
                onValueChange = {
                    userInput = it
                    validationError = null // Type பண்ணும்போது error மறையும்
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = {
                    Text(
                        "நடந்த சம்பவத்தை தெளிவாக இங்கு தட்டச்சு செய்யவும்...",
                        color = Color(0xFF5A606A)
                    )
                },
                textStyle = TextStyle(
                    color = Color(0xFF14181F), // ஆழமான கறுப்பு நிற எழுத்துக்கள் (100% விசிபிள்)
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
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

            // Validation Error Display
            validationError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFCDAD4), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = it, color = Color(0xFF5E130A), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = {
                    // 1. உள்ளூர் சரிபார்ப்பு (Gibberish Check)
                    when (val check = LegalInputValidator.validateLegalInput(userInput)) {
                        is LegalInputValidator.ValidationResult.Invalid -> {
                            validationError = check.errorMessageEn
                            legalResult = null
                            return@Button
                        }
                        is LegalInputValidator.ValidationResult.Valid -> {
                            validationError = null
                        }
                    }

                    // 2. Gemini API Call
                    isLoading = true
                    scope.launch {
                        try {
                            val systemPrompt = """
                            You are Justra's Dynamic Legal Adjudication Engine.
                            STRICT RULE: NEVER DEFAULT TO CONSUMER LAW UNLESS GOODS/SELLER DEFECT IS EXPLICITLY MENTIONED.
                            
                            Classify the incident based strictly on facts:
                            1. Landlord / Rent / Advance Deposit refusal -> Model Tenancy Act / Rent Authority
                            2. Unpaid Salary / Gratuity / Illegal Termination -> Payment of Wages Act, 1936 / Labor Commissioner
                            3. UPI / Phishing / Telegram Job Scam / Bank fraud -> Information Technology Act 2000 (Sec 43/66) & BNS 318 (Helpline 1930)
                            4. Product defect / Warranty failure -> Consumer Protection Act, 2019 (e-Daakhil)
                            5. Physical threat / Blackmail / Assault -> Bharatiya Nyaya Sanhita (BNS) & 112
                            
                            If the input is nonsensical or lacks legal facts, return status: "INVALID_GRIEVANCE".
                            
                            Respond strictly in JSON:
                            {
                              "status": "VALID_GRIEVANCE | INVALID_GRIEVANCE",
                              "category": "Tenancy | Labor | Cybercrime | Consumer | Criminal | Property",
                              "specificLaw": "Exact statutory section and act name",
                              "targetAuthority": "Exact portal, court, or officer",
                              "immediateStep": "Immediate evidentiary step",
                              "formalDraft": "Draft subject and fact summary",
                              "tamilGuidance": "தமிழில் நேரடி 2-வரி ஆலோசனை"
                            }
                            """.trimIndent()

                            val prompt = "Actual citizen complaint text:\n\"$userInput\""

                            val request = GeminiRequest(
                                contents = listOf(
                                    GeminiContent(
                                        parts = listOf(GeminiPart(text = prompt)),
                                        role = "user"
                                    )
                                ),
                                generationConfig = GeminiGenerationConfig(
                                    temperature = 0.1f, // Hallucination மற்றும் Defaulting-ஐ தடுக்க குறைந்த டெம்பரேச்சர்
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

                            if (json.optString("status") == "INVALID_GRIEVANCE") {
                                validationError = "சட்ட விவரங்கள் போதுமானதாக இல்லை. என்ன நடந்தது என்பதை விளக்கமாக எழுதவும்."
                                legalResult = null
                            } else {
                                legalResult = AccurateLegalResult(
                                    status = json.optString("status", "VALID_GRIEVANCE"),
                                    category = json.optString("category", "பொது சட்டம்"),
                                    specificLaw = json.optString("specificLaw", "சட்டப்பிரிவு அடையாளம் காணப்பட்டது"),
                                    targetAuthority = json.optString("targetAuthority", "சம்பந்தப்பட்ட துறை"),
                                    immediateStep = json.optString("immediateStep", "ஆதாரங்களை பாதுகாக்கவும்"),
                                    formalDraft = json.optString("formalDraft", userInput),
                                    tamilGuidance = json.optString("tamilGuidance", "உங்கள் மனு பரிசீலனைக்கு தயாராக உள்ளது.")
                                )
                            }
                        } catch (e: Exception) {
                            validationError = "பகுப்பாய்வு பிழை: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = userInput.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("சட்டத்தை ஆராய் (Analyze Law)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Result Card
            legalResult?.let { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.5.dp, Color(0xFF0F1E36)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "பிரிவு: ${res.category.uppercase()}",
                                color = Color(0xFFC85A32),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            Surface(
                                color = Color(0xFFD1E7D0),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "சரியான சட்டம்",
                                    color = Color(0xFF0F3815),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("பொருந்தும் சட்டம் (Applicable Law):", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                        Text(res.specificLaw, color = Color(0xFF0F1E36), fontWeight = FontWeight.Bold, fontSize = 15.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("புகார் அளிக்க வேண்டிய இடம் (Authority / Portal):", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                        Text(res.targetAuthority, color = Color(0xFF4A4E57), fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("உடனடி நடவடிக்கை:", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                        Text(res.immediateStep, color = Color(0xFF14181F), fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = Color(0xFFDCE6F2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 வழிகாட்டுதல்: ${res.tamilGuidance}",
                                color = Color(0xFF0B1B3D),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
