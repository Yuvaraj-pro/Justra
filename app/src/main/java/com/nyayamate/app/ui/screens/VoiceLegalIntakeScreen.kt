package com.nyayamate.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
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
import androidx.core.content.ContextCompat
import com.example.BuildConfig
import com.example.data.api.GeminiApiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// ரிசல்ட் மாடல்
data class LegalAnalysisOutput(
    val category: String,
    val applicableLaw: String,
    val authority: String,
    val immediateStep: String,
    val draftSubject: String,
    val draftFacts: String,
    val tamilAdvice: String
)

@Composable
fun VoiceLegalIntakeScreen(
    apiKey: String = BuildConfig.GEMINI_API_KEY,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var textInput by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var resultOutput by remember { mutableStateOf<LegalAnalysisOutput?>(null) }
    var speechError by remember { mutableStateOf<String?>(null) }

    // Android Speech Recognizer அமைப்பு
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                speechError = null
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(error: Int) {
                isListening = false
                speechError = "குரல் பதிவு பிழை (Error code: $error). மீண்டும் முயற்சிக்கவும்."
                Log.e("SpeechRecognizer", "Error: $error")
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    textInput = matches[0] // இறுதி பேச்சு Text Box-ல் சேரும்
                }
                isListening = false
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    textInput = matches[0] // பேசப் பேச உடனுக்குடன் திரையில் தட்டச்சாகும்
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(listener)

        onDispose {
            speechRecognizer.destroy()
        }
    }

    val startListening = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN") // தமிழ் + ஆங்கிலம் இரண்டுமே எடுக்கும்
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer.startListening(intent)
        isListening = true
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startListening() else speechError = "Microphone அனுமதி தேவை."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2)) // Canvas Background
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "குரல் வழி புகார் பதிவு & சட்ட பகுப்பாய்வு",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F1E36)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 100% Readable Text Box
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = {
                Text(
                    if (isListening) "நீங்கள் பேசுவது கேட்கிறது... திரையில் தோன்றும்..." else "பேச மைக் பட்டனை அழுத்தவும் அல்லது இங்கு தட்டச்சு செய்யவும்...",
                    color = Color(0xFF5A606A)
                )
            },
            textStyle = TextStyle(color = Color(0xFF14181F), fontSize = 15.sp, lineHeight = 22.sp),
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

        speechError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color(0xFFBA1A1A), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions: Voice Record & Analyze
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mic Record Button
            Button(
                onClick = {
                    if (isListening) {
                        speechRecognizer.stopListening()
                        isListening = false
                    } else {
                        val hasMic = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasMic) startListening() else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isListening) Color(0xFFBA1A1A) else Color(0xFFC85A32)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(if (isListening) Icons.Default.Stop else Icons.Default.Mic, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isListening) "நிறுத்துக" else "பேசவும்", color = Color.White, fontWeight = FontWeight.Bold)
            }

            // AI Analyze Button
            Button(
                onClick = {
                    if (textInput.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            try {
                                val systemPrompt = """
                                    You are Justra's Dynamic Legal Engine.
                                    Analyze the user's grievance and map to the exact legal statute under Indian Law or the relevant jurisdiction.
                                    Do not default to Consumer Protection Act unless it is a product/goods issue.
                                    Output ONLY valid JSON:
                                    {
                                      "category": "Tenancy | Labor | Cybercrime | Consumer | Criminal",
                                      "applicableLaw": "Exact statutory section and act name",
                                      "authority": "Exact grievance portal, commission, or court",
                                      "immediateStep": "Immediate evidentiary step",
                                      "draftSubject": "Formal subject line",
                                      "draftFacts": "Factual incident summary",
                                      "tamilAdvice": "தமிழில் நேரடி எளிய விளக்கம் (2 வரிகளில்)"
                                    }
                                """.trimIndent()

                                val keyToUse = if (apiKey.isNotBlank()) apiKey else BuildConfig.GEMINI_API_KEY
                                val request = GeminiRequest(
                                    contents = listOf(
                                        GeminiContent(
                                            parts = listOf(GeminiPart(text = "Grievance narrative: $textInput")),
                                            role = "user"
                                        )
                                    ),
                                    generationConfig = GeminiGenerationConfig(
                                        temperature = 0.2f,
                                        responseMimeType = "application/json"
                                    ),
                                    systemInstruction = GeminiContent(
                                        parts = listOf(GeminiPart(text = systemPrompt))
                                    )
                                )

                                val response = withContext(Dispatchers.IO) {
                                    GeminiApiClient.service.generateContent(keyToUse, request)
                                }
                                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "{}"
                                val cleanedJson = rawText.trim()
                                    .removePrefix("```json")
                                    .removePrefix("```")
                                    .removeSuffix("```")
                                    .trim()
                                val json = JSONObject(cleanedJson)

                                resultOutput = LegalAnalysisOutput(
                                    category = json.optString("category", "பொது சட்ட விவகாரம்"),
                                    applicableLaw = json.optString("applicableLaw", "சட்டப்பிரிவு கண்டறியப்பட்டது"),
                                    authority = json.optString("authority", "சம்பந்தப்பட்ட துறை"),
                                    immediateStep = json.optString("immediateStep", "ஆவணங்களை பாதுகாக்கவும்"),
                                    draftSubject = json.optString("draftSubject", "புகார் மனு"),
                                    draftFacts = json.optString("draftFacts", textInput),
                                    tamilAdvice = json.optString("tamilAdvice", "வழிகாட்டுதல் தயார்.")
                                )
                            } catch (e: Exception) {
                                Log.e("JustraAI", "Error parsing response", e)
                                speechError = "சட்டத்தை ஆராய்வதில் சிக்கல்: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = textInput.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1.3f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("சட்டத்தை அறி", color = Color.White, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 100% VISIBLE RESULT CARD
        resultOutput?.let { res ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                border = BorderStroke(1.5.dp, Color(0xFF0F1E36)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "பிரிவு: ${res.category.uppercase()}",
                        color = Color(0xFFC85A32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("பொருந்தும் சட்டம்:", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                    Text(res.applicableLaw, color = Color(0xFF0F1E36), fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("புகார் அளிக்க வேண்டிய இடம்:", fontWeight = FontWeight.Bold, color = Color(0xFF14181F), fontSize = 13.sp)
                    Text(res.authority, color = Color(0xFF4A4E57), fontSize = 14.sp)

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
                            text = "💡 ${res.tamilAdvice}",
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
