package com.example.ui.screens.complaint

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiLegalEngine
import com.example.domain.model.LegalComplaintResult
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceComplaintScreen(
    onNavigateBack: (() -> Unit)? = null,
    onGenerateComplaint: ((transcript: String, onResult: (LegalComplaintResult) -> Unit) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isListening by remember { mutableStateOf(false) }
    var transcribedText by remember { mutableStateOf("") }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var complaintResult by remember { mutableStateOf<LegalComplaintResult?>(null) }

    // Initialize Speech Recognizer
    DisposableEffect(Unit) {
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer = recognizer
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) { isListening = false }

            // Live streaming transcript onto screen
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    transcribedText = matches[0]
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    transcribedText = matches[0]
                }
                isListening = false
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose {
            recognizer.destroy()
        }
    }

    val startVoiceInput = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN") // Default Tamil (Accepts English too)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer?.startListening(intent)
        isListening = true
    }

    val stopVoiceInput = {
        speechRecognizer?.stopListening()
        isListening = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCanvasBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // App Header with Back navigation if available
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SovereignNavy
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Column {
                Text(
                    text = "குரல் வழி புகார் பதிவு (Voice Grievance)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SovereignNavy
                )
                Text(
                    text = "உங்கள் பிரச்சனையை தமிழில் அல்லது ஆங்கிலத்தில் பேசுங்கள்.",
                    fontSize = 14.sp,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
            }
        }

        // Live Transcribed Text Box (High Contrast & Visible Text)
        OutlinedTextField(
            value = transcribedText,
            onValueChange = { transcribedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(SandstoneCard, RoundedCornerShape(12.dp)),
            placeholder = {
                Text(
                    text = if (isListening) "நீங்கள் பேசுவது இங்கே தட்டச்சாகிறது..." else "மைக் பட்டனை அழுத்திப் பேசவும் அல்லது இங்கு டைப் செய்யவும்...",
                    color = TextSecondaryDark
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark,
                focusedContainerColor = SandstoneCard,
                unfocusedContainerColor = SandstoneCard,
                focusedBorderColor = SovereignNavy,
                unfocusedBorderColor = CardBorderStroke
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Voice Action & Confirmation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic Record Button (Terracotta Accent)
            Button(
                onClick = { if (isListening) stopVoiceInput() else startVoiceInput() },
                colors = ButtonDefaults.buttonColors(containerColor = if (isListening) AlertCrimson else AccentTerracotta),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = if (isListening) TextOnAlertCrimson else androidx.compose.ui.graphics.Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isListening) "நிறுத்துக" else "பேசவும்",
                    color = if (isListening) TextOnAlertCrimson else androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Confirm & Generate Button (Deep Navy)
            Button(
                onClick = {
                    if (transcribedText.isNotBlank()) {
                        isProcessing = true
                        if (onGenerateComplaint != null) {
                            onGenerateComplaint(transcribedText) { result ->
                                complaintResult = result
                                isProcessing = false
                            }
                        } else {
                            coroutineScope.launch {
                                try {
                                    val result = GeminiLegalEngine.analyzeDynamicVoiceGrievance(transcribedText)
                                    complaintResult = result
                                } finally {
                                    isProcessing = false
                                }
                            }
                        }
                    }
                },
                enabled = transcribedText.isNotBlank() && !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1.3f)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Submit", tint = androidx.compose.ui.graphics.Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("சட்டத்தை அறி & புகார் செய்", color = androidx.compose.ui.graphics.Color.White, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic AI Output Card (Shows the EXACT Law & Complaint according to user problem)
        complaintResult?.let { res ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, SovereignNavy, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Category & Verified Badge (Sage Green with dark green text - 100% visible)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = res.category.uppercase(),
                            color = AccentTerracotta,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(VerifiedSageGreen, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "சட்டம் கண்டறியப்பட்டது",
                                color = TextOnSageGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Law Applied
                    Text(
                        text = "பொருந்தும் சட்டம் (Applicable Law):",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        fontSize = 14.sp
                    )
                    Text(
                        text = res.law,
                        color = SovereignNavy,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )

                    // Target Authority
                    Text(
                        text = "புகார் அளிக்க வேண்டிய இடம் (Target Channel):",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        fontSize = 14.sp
                    )
                    Text(
                        text = res.authority,
                        color = TextSecondaryDark,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    HorizontalDivider(color = CardBorderStroke)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Draft Complaint
                    Text(
                        text = "உங்களுக்கான பிரத்யேக புகார் வரைவு (Generated Draft):",
                        fontWeight = FontWeight.Bold,
                        color = SovereignNavy,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "பொருள்: ${res.subject}",
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Text(
                        text = res.facts,
                        color = TextSecondaryDark,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "கோரப்படும் தீர்வு: ${res.relief}",
                        fontWeight = FontWeight.Medium,
                        color = TextPrimaryDark,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                    )

                    // Tamil Explanation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftNavyContainer, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 அடுத்த கட்ட நடவடிக்கை: ${res.explanationTamil}",
                            color = OnNavyContainer,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
