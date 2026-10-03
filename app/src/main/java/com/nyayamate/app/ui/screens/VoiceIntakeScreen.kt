package com.nyayamate.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nyayamate.app.data.DynamicLawResponse
import com.nyayamate.app.data.LegalApiService
import com.nyayamate.app.data.UserLegalContext
import com.nyayamate.app.data.WorldwideLegalResponse
import com.nyayamate.app.utils.VoiceRecorderManager
import kotlinx.coroutines.launch

@Composable
fun VoiceIntakeScreen(
    onProcessedComplaint: (String) -> Unit
) {
    val context = LocalContext.current
    val voiceManager = remember { VoiceRecorderManager(context) }
    val apiService = remember { LegalApiService() }
    val scope = rememberCoroutineScope()

    val isListening by voiceManager.isListening.collectAsState()
    val spokenText by voiceManager.spokenText.collectAsState()
    val errorMessage by voiceManager.errorMessage.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var selectedLocale by remember { mutableStateOf("ta-IN") } // Default to Tamil
    var dynamicLawResult by remember { mutableStateOf<DynamicLawResponse?>(null) }
    var worldwideResult by remember { mutableStateOf<WorldwideLegalResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Keep editable text box updated as voice streams in
    LaunchedEffect(spokenText) {
        if (spokenText.isNotBlank()) {
            textInput = spokenText
        }
    }

    // Always release hardware mic when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            voiceManager.stopListening()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager.startListening(selectedLocale)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "குரல் வழி புகார் பதிவு (Voice Intake)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F1E36)
        )
        Text(
            text = "Speak in Tamil or English to automatically document your dispute.",
            fontSize = 13.sp,
            color = Color(0xFF4A4E57),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Language toggle buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedLocale == "ta-IN",
                onClick = { 
                    selectedLocale = "ta-IN"
                    if (isListening) {
                        voiceManager.stopListening()
                        voiceManager.startListening("ta-IN")
                    }
                },
                label = { Text("தமிழ் (Tamil)", textAlign = TextAlign.Center) }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterChip(
                selected = selectedLocale == "en-IN",
                onClick = { 
                    selectedLocale = "en-IN"
                    if (isListening) {
                        voiceManager.stopListening()
                        voiceManager.startListening("en-IN")
                    }
                },
                label = { Text("English (India)", textAlign = TextAlign.Center) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // High contrast readable live transcript box
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            placeholder = {
                Text(
                    if (isListening) "Listening... உங்கள் குரலை பதிவு செய்கிறது..." else "Tap the microphone below and describe the incident...",
                    color = Color(0xFF5A606A)
                )
            },
            textStyle = TextStyle(
                color = Color(0xFF14181F), // High contrast deep black
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

        // Error message banner if microphone or network fails
        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFFFCDAD4),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFF5E130A),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Mic / Stop Button
        IconButton(
            onClick = {
                if (isListening) {
                    voiceManager.stopListening()
                } else {
                    val hasMicPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasMicPermission) {
                        voiceManager.startListening(selectedLocale)
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            },
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = if (isListening) Color(0xFFB3261E) else Color(0xFFC85A32),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = "Microphone",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isListening) "Tap to Stop Listening" else "Tap Mic to Start Speaking",
            fontSize = 12.sp,
            color = Color(0xFF4A4E57)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Analyze Law Button (Dynamically calls WorldwideLegalClassifier)
        Button(
            onClick = {
                if (textInput.isNotBlank()) {
                    voiceManager.stopListening()
                    isLoading = true
                    scope.launch {
                        try {
                            val contextPref = UserLegalContext(
                                preferredLanguage = if (selectedLocale == "ta-IN") "ta" else "en"
                            )
                            val worldResp = apiService.analyzeWorldwideGrievance(textInput, contextPref)
                            worldwideResult = worldResp

                            // Synchronize quick view
                            dynamicLawResult = DynamicLawResponse(
                                category = worldResp.disputeClassification.primaryCategory + " - " + worldResp.disputeClassification.subCategory,
                                applicableLaw = worldResp.disputeClassification.applicableStatute,
                                authority = worldResp.disputeClassification.enforcingAuthorityOrPortal,
                                immediateNextStep = worldResp.localizedResponse.immediateNextStep,
                                formalDraftSubject = worldResp.formalComplaintDraft.subjectLine,
                                formalDraftFacts = worldResp.formalComplaintDraft.statementOfFacts,
                                tamilExplanation = worldResp.localizedResponse.simpleExplanation
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("Justra", "API Error: ${e.message}", e)
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            enabled = textInput.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("சட்டத்தை ஆராய் (Analyze Case)", color = Color.White, fontSize = 14.sp)
            }
        }

        // Worldwide Dynamic Case Analysis Display Card
        worldwideResult?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Jurisdiction & Legal Family Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val flag = when (result.jurisdiction.countryCode.uppercase()) {
                            "IN" -> "🇮🇳"
                            "US" -> "🇺🇸"
                            "GB" -> "🇬🇧"
                            "AU" -> "🇦🇺"
                            else -> "🌐"
                        }
                        Surface(
                            color = Color(0xFFDCE6F2),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "$flag ${result.jurisdiction.countryName} (${result.jurisdiction.legalFamily})",
                                color = Color(0xFF0F1E36),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFFFFDBCF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = result.disputeClassification.primaryCategory,
                                color = Color(0xFF3B1000),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = result.disputeClassification.subCategory,
                        color = Color(0xFFC85A32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Applicable Statute
                    Text(
                        text = "பொருந்தும் பிரத்யேக சட்டம் (Statute):",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14181F),
                        fontSize = 13.sp
                    )
                    Text(
                        text = result.disputeClassification.applicableStatute,
                        color = Color(0xFF0F1E36),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Enforcing Authority & Helpline
                    Text(
                        text = "அணுக வேண்டிய அதிகாரம்: ${result.disputeClassification.enforcingAuthorityOrPortal}",
                        color = Color(0xFF4A4E57),
                        fontSize = 13.sp
                    )
                    result.disputeClassification.helplineNumber?.let { helpline ->
                        Text(
                            text = "அவசர உதவி எண்: $helpline",
                            color = Color(0xFF0F1E36),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Case Readiness Score (Based strictly on factual completeness)
                    Text(
                        text = "வழக்கு தயார்நிலை (Factual Readiness): ${result.caseReadiness.readinessScorePercentage}%",
                        color = Color(0xFF14181F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    LinearProgressIndicator(
                        progress = { result.caseReadiness.readinessScorePercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(8.dp),
                        color = if (result.caseReadiness.readinessScorePercentage >= 70) Color(0xFF0F3815) else Color(0xFFC85A32),
                        trackColor = Color(0xFFD4CAB8)
                    )

                    // Action Steps
                    if (result.actionSteps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "அடுத்த கட்ட நடவடிக்கைகள் (Action Steps):",
                            color = Color(0xFF14181F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        result.actionSteps.forEach { step ->
                            Text(
                                text = "${step.stepOrder}. ${step.stepTitle}: ${step.instruction}",
                                color = Color(0xFF333842),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Localized explanation
                    Surface(
                        color = Color(0xFFD1E7D0),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = result.localizedResponse.simpleExplanation,
                                color = Color(0xFF0F3815),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "உடனடி நடவடிக்கை: ${result.localizedResponse.immediateNextStep}",
                                color = Color(0xFF0F3815),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Formal Complaint Draft Snippet
                    Text(
                        text = "மனு தலைப்பு (Draft Subject):",
                        color = Color(0xFF14181F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = result.formalComplaintDraft.subjectLine,
                        color = Color(0xFF4A4E57),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onProcessedComplaint(textInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("முழு சட்ட வரைவு & புகார் வழிகாட்டி (Proceed to Draft)", color = Color.White, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.disclaimer,
                        color = Color(0xFF5A606A),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
