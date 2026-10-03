package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import com.example.util.LocationJurisdictionManager
import com.example.util.JurisdictionSelectionMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.LegalAnalysisResponse
import com.example.data.LegalApiService
import com.example.ui.theme.AccentTerracotta
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.SandstoneCard
import com.example.ui.theme.SoftNavyContainer
import com.example.ui.theme.SovereignNavy
import com.example.ui.theme.TextOnAlertCrimson
import com.example.ui.theme.TextOnSageGreen
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VerifiedSageGreen
import com.example.ui.theme.WarmCanvasBg
import com.example.ui.theme.nyayaOutlinedTextFieldColors
import com.example.util.LegalInputValidator
import com.example.utils.ActionUtils
import com.example.utils.VoiceToTextManager
import kotlinx.coroutines.launch

/**
 * Module 4: Unified Legal Intake & Dynamic Statutory Classifier Screen.
 *
 * Provides real-time speech streaming with bilingual Tamil/English recognition,
 * deterministic client-side heuristic gibberish gating, high-contrast inputs,
 * and dynamic statutory analysis via LegalApiService without default fallback to Consumer Act.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAndVoiceScreen(
    onNavigateBack: () -> Unit,
    initialPrompt: String = "",
    onSaveToCase: ((LegalAnalysisResponse) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Voice recognition manager
    val voiceManager = remember { VoiceToTextManager(context) }
    DisposableEffect(voiceManager) {
        onDispose {
            voiceManager.destroy()
        }
    }

    val isListening by voiceManager.isListening.collectAsState()
    val spokenText by voiceManager.spokenText.collectAsState()
    val voiceError by voiceManager.errorState.collectAsState()

    // Screen text state (editable two-stage intake)
    var transcribedText by remember { mutableStateOf(initialPrompt) }
    var selectedLocale by remember { mutableStateOf(VoiceToTextManager.LOCALE_TAMIL) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var analysisResult by remember { mutableStateOf<LegalAnalysisResponse?>(null) }

    // Jurisdiction Manager & Dual-Mode State
    val jurisdictionManager = remember { LocationJurisdictionManager(context) }
    val currentJurisdiction by jurisdictionManager.currentJurisdiction.collectAsState()
    val selectionMode by jurisdictionManager.selectionMode.collectAsState()
    var showJurisdictionDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            coroutineScope.launch {
                jurisdictionManager.detectAutoLocation()
            }
        } else {
            Toast.makeText(context, "Location permission is required for Auto-Jurisdiction", Toast.LENGTH_SHORT).show()
        }
    }

    // Synchronize streaming voice syllables into editable input box
    LaunchedEffect(spokenText) {
        if (spokenText.isNotEmpty()) {
            transcribedText = spokenText
            validationError = null
        }
    }

    // Handle voice error notification
    LaunchedEffect(voiceError) {
        voiceError?.let { err ->
            snackbarHostState.showSnackbar(err)
        }
    }

    // Audio permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager.startListening(selectedLocale)
        } else {
            Toast.makeText(context, "Microphone permission is required for voice intake", Toast.LENGTH_LONG).show()
        }
    }

    // Infinite pulsing animation for mic button when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    val isTa = selectedLocale == VoiceToTextManager.LOCALE_TAMIL

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "குரல் & நேரடி சட்ட ஆய்வு" else "Voice & Statutory Analysis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = SovereignNavy
                        )
                        Text(
                            text = "Voice Legal Intake & Statutory Classifier",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("nav_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SovereignNavy
                        )
                    }
                },
                actions = {
                    // Bilingual language toggle
                    OutlinedButton(
                        onClick = {
                            selectedLocale = if (selectedLocale == VoiceToTextManager.LOCALE_TAMIL) {
                                VoiceToTextManager.LOCALE_ENGLISH
                            } else {
                                VoiceToTextManager.LOCALE_TAMIL
                            }
                            if (isListening) {
                                voiceManager.stopListening()
                                voiceManager.startListening(selectedLocale)
                            }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("locale_toggle_button"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SovereignNavy
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderStroke)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            modifier = Modifier.size(16.dp),
                            tint = SovereignNavy
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedLocale == VoiceToTextManager.LOCALE_TAMIL) "தமிழ் (IN)" else "English (IN)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WarmCanvasBg
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = WarmCanvasBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Instruction Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderStroke)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isTa) "உங்கள் பிரச்சனையை தமிழில் அல்லது ஆங்கிலத்தில் விவரிக்கவும்" else "Describe your grievance in Tamil or English",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SovereignNavy
                    )
                    Text(
                        text = if (isTa) "வாடகை பாக்கி, சம்பள நிலுவை, UPI மோசடி அல்லது மிரட்டல் போன்ற விபரங்களை பேசவும். எங்கள் சட்ட இயந்திரம் சரியான சட்டப் பிரிவை கண்டறியும்."
                        else "Speak or type details such as tenancy disputes, salary arrears, UPI fraud, or threats. Our statutory engine maps exact Indian Acts dynamically.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
            }

            // Dual-Mode Jurisdiction Selector Chip / Pill
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showJurisdictionDialog = true }
                    .testTag("jurisdiction_selector_pill"),
                colors = CardDefaults.cardColors(containerColor = SoftNavyContainer),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderStroke)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Jurisdiction",
                            tint = SovereignNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${if (isTa) "அதிகார வரம்பு:" else "Jurisdiction:"} ",
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${currentJurisdiction.flagEmoji} ${if (isTa) currentJurisdiction.countryNameTa else currentJurisdiction.countryName}",
                            fontSize = 13.sp,
                            color = SovereignNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (selectionMode == JurisdictionSelectionMode.AUTO_GPS) AccentTerracotta else SovereignNavy,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (selectionMode == JurisdictionSelectionMode.AUTO_GPS) "GPS AUTO" else "MANUAL",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Editable Two-Stage Intake Text Box
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTa) "பிரச்சனை விபரம் (Editable Narrative):" else "Dispute Details (Editable Narrative):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryDark
                    )

                    if (transcribedText.isNotBlank()) {
                        Text(
                            text = "${transcribedText.split("\\s+".toRegex()).size} words",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = transcribedText,
                    onValueChange = {
                        transcribedText = it
                        validationError = null
                        voiceManager.setSpokenText(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .testTag("complaint_text_field"),
                    placeholder = {
                        Text(
                            text = if (isListening) {
                                if (isTa) "கேட்கிறது... தயங்காமல் பேசுங்கள்..." else "Listening live syllables..."
                            } else {
                                if (isTa) "மைக் பட்டனை அழுத்தி பேசவும் அல்லது தட்டச்சு செய்யவும்... (எ.கா: வாடகை அட்வான்ஸ் தரவில்லை / சம்பளம் வழங்கவில்லை / UPI மோசடி)"
                                else "Tap mic or type details... (e.g., Landlord withholding rent deposit / unpaid wages / UPI scam)"
                            },
                            color = Color(0xFF5A606A),
                            fontSize = 13.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = nyayaOutlinedTextFieldColors(),
                    maxLines = 8
                )
            }

            // Validation Warning Banner (Halts gibberish locally)
            if (validationError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCDAD4)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertCrimson),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = AlertCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = validationError ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5E130A)
                        )
                    }
                }
            }

            // Action Buttons Row (Speak/Stop Mic & Analyze & Generate)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Button (Terracotta / Crimson pulsing)
                Button(
                    onClick = {
                        validationError = null
                        if (isListening) {
                            voiceManager.stopListening()
                        } else {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                voiceManager.startListening(selectedLocale)
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .scale(if (isListening) micPulseScale else 1f)
                        .testTag("voice_toggle_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening) AlertCrimson else AccentTerracotta,
                        contentColor = if (isListening) TextOnAlertCrimson else Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop Mic" else "Start Mic",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isListening) {
                            if (isTa) "பேச்சை நிறுத்து" else "Stop Mic"
                        } else {
                            if (isTa) "குரலில் பேசு" else "Speak"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // "ஆராய்ந்து சட்டம் சொல்" (Analyze & Generate) Button with LegalInputValidator Gate
                Button(
                    onClick = {
                        validationError = null
                        val trimmed = transcribedText.trim()
                        if (trimmed.isBlank()) {
                            validationError = if (isTa) "தயவுசெய்து உங்கள் பிரச்சனையை கூறவும் அல்லது தட்டச்சு செய்யவும்" else "Please provide your grievance details"
                            return@Button
                        }

                        // Client-side Heuristic Gate for Gibberish and Random Text
                        val validation = LegalInputValidator.validateLegalInput(trimmed)
                        if (validation is LegalInputValidator.ValidationResult.Invalid) {
                            validationError = if (isTa) validation.errorMessageTa else validation.errorMessageEn
                            return@Button
                        }

                        if (isListening) {
                            voiceManager.stopListening()
                        }

                        isAnalyzing = true
                        analysisResult = null

                        coroutineScope.launch {
                            try {
                                val result = LegalApiService.analyzeLegalGrievance(
                                    narrativeText = trimmed,
                                    preferredLanguage = if (selectedLocale == VoiceToTextManager.LOCALE_TAMIL) "ta" else "en",
                                    jurisdictionCode = currentJurisdiction.countryCode
                                )
                                analysisResult = result
                            } catch (e: Exception) {
                                val errMsg = if (isTa) "ஆய்வு தோல்வி அடைந்தது: ${e.localizedMessage ?: "பிணைய பிழை"}" else "Analysis failed: ${e.localizedMessage ?: "Network error"}"
                                validationError = errMsg
                                snackbarHostState.showSnackbar(errMsg)
                            } finally {
                                isAnalyzing = false
                            }
                        }
                    },
                    enabled = !isAnalyzing && transcribedText.isNotBlank(),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(52.dp)
                        .testTag("analyze_generate_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SovereignNavy,
                        contentColor = Color.White
                    )
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTa) "ஆராய்கிறது..." else "Analyzing...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Analyze",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTa) "ஆராய்ந்து சட்டம் சொல்" else "Analyze Legal Code",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Dynamic Output Card
            AnimatedVisibility(
                visible = analysisResult != null,
                enter = fadeIn() + slideInVertically()
            ) {
                analysisResult?.let { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("statutory_result_card"),
                        colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorderStroke)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Header Row: Category Badge & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = res.category,
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy,
                                    fontSize = 16.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .background(VerifiedSageGreen, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = TextOnSageGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isTa) "சட்டம் உறுதி செய்யப்பட்டது" else "Statutory Law Verified",
                                            color = TextOnSageGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = CardBorderStroke)

                            // Applicable Law
                            Column {
                                Text(
                                    text = if (isTa) "பொருந்தும் சட்டம் (Applicable Law):" else "Applicable Law:",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = res.applicableLaw,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SovereignNavy,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Authority / Redressal Forum
                            Column {
                                Text(
                                    text = if (isTa) "புகார் அளிக்க வேண்டிய இடம் (Target Channel):" else "Jurisdictional Authority:",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = res.authority,
                                    color = TextSecondaryDark,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Immediate Next Step
                            Column {
                                Text(
                                    text = if (isTa) "உடனடி முதல் கட்ட நடவடிக்கை (Immediate Action):" else "Immediate Action Required:",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = res.immediateNextStep,
                                    color = TextSecondaryDark,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            HorizontalDivider(color = CardBorderStroke)

                            // Formal Complaint Draft Section
                            Column {
                                Text(
                                    text = if (isTa) "உங்களுக்கான பிரத்யேக வரைவு (Generated Legal Draft):" else "Generated Formal Legal Draft:",
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${if (isTa) "பொருள் (Subject):" else "Subject:"} ${res.formalDraftSubject}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryDark,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.formalDraftFacts,
                                    color = TextSecondaryDark,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${if (isTa) "கோரப்படும் தீர்வு (Relief):" else "Demanded Relief:"} ${res.demandedRelief}",
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimaryDark,
                                    fontSize = 13.sp
                                )
                            }

                            // Tamil Explanation Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SoftNavyContainer, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "💡 ${if (isTa) "சட்ட ஆலோசனை:" else "Legal Guidance:"} ${res.explanationTamil}",
                                    color = SovereignNavy,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Action Buttons inside Output Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Copy Draft
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val fullDraft = """
                                            SUBJECT: ${res.formalDraftSubject}
                                            APPLICABLE LAW: ${res.applicableLaw}
                                            AUTHORITY: ${res.authority}
                                            
                                            FACTS:
                                            ${res.formalDraftFacts}
                                            
                                            RELIEF DEMANDED:
                                            ${res.demandedRelief}
                                        """.trimIndent()
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Legal Draft", fullDraft))
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(if (isTa) "வரைவு நகலெடுக்கப்பட்டது" else "Legal draft copied to clipboard")
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SovereignNavy),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderStroke)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isTa) "நகலெடு" else "Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Open Official Redressal Portal
                                Button(
                                    onClick = {
                                        val portalUrl = when {
                                            res.category.contains("Cyber", ignoreCase = true) -> "https://cybercrime.gov.in"
                                            res.category.contains("Rental", ignoreCase = true) || res.category.contains("Tenancy", ignoreCase = true) -> "https://www.tenancy.tn.gov.in"
                                            res.category.contains("Employment", ignoreCase = true) || res.category.contains("Salary", ignoreCase = true) -> "https://samadhan.labour.gov.in"
                                            res.category.contains("Consumer", ignoreCase = true) -> "https://edaakhil.nic.in"
                                            else -> "https://nalsa.gov.in"
                                        }
                                        ActionUtils.openWebUrl(context, portalUrl)
                                    },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SovereignNavy,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInBrowser,
                                        contentDescription = "Portal",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isTa) "போர்டலுக்கு செல்" else "Open Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

    // Dual-Mode Jurisdiction Picker Modal Dialog
    if (showJurisdictionDialog) {
        AlertDialog(
            onDismissRequest = { showJurisdictionDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = SovereignNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTa) "சட்ட அதிகார வரம்பை தேர்வு செய்க" else "Select Sovereign Jurisdiction",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SovereignNavy
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Auto GPS Button
                    OutlinedButton(
                        onClick = {
                            showJurisdictionDialog = false
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                coroutineScope.launch { jurisdictionManager.detectAutoLocation() }
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SovereignNavy)
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AccentTerracotta, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTa) "📍 தானியங்கி GPS இருப்பிடம் (Auto GPS)" else "📍 Auto-Detect via GPS / Network",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = CardBorderStroke)

                    Text(
                        text = if (isTa) "உலகளாவிய நாடுகள் (Global Override):" else "Indexed World Jurisdictions:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    LazyColumn(
                        modifier = Modifier.height(240.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(LocationJurisdictionManager.WORLD_JURISDICTIONS) { item ->
                            val isSelected = currentJurisdiction.countryCode == item.countryCode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) SoftNavyContainer else Color.Transparent)
                                    .clickable {
                                        jurisdictionManager.setManualJurisdiction(item.countryCode)
                                        showJurisdictionDialog = false
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = item.flagEmoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isTa) item.countryNameTa else item.countryName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignNavy
                                        )
                                        Text(
                                            text = item.legalSystemName,
                                            fontSize = 10.sp,
                                            color = TextSecondaryDark
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showJurisdictionDialog = false }) {
                    Text(text = if (isTa) "மூடு" else "Close", fontWeight = FontWeight.Bold, color = SovereignNavy)
                }
            },
            containerColor = SandstoneCard,
            shape = RoundedCornerShape(14.dp)
        )
    }
}
}
}
