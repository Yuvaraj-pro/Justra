package com.justra.app.ui.screens.complaint

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.UserRole
import com.justra.app.ui.theme.DeepHennaAlertContainer
import com.justra.app.ui.theme.CardBorderStroke
import com.justra.app.ui.theme.DeepImperialNavy
import com.justra.app.ui.theme.MutedAmberWarningContainer
import com.justra.app.ui.theme.OnDeepHennaAlert
import com.justra.app.ui.theme.OnImperialNavy
import com.justra.app.ui.theme.OnMutedAmberWarning
import com.justra.app.ui.theme.OnParchmentText
import com.justra.app.ui.theme.OnSageHerbSuccess
import com.justra.app.ui.theme.OnSandstoneSurfaceVariant
import com.justra.app.ui.theme.ParchmentOutline
import com.justra.app.ui.theme.PrimaryNavyContainer
import com.justra.app.ui.theme.SageHerbSuccessContainer
import com.justra.app.ui.theme.SandstoneCard
import com.justra.app.ui.theme.SandstoneSurface
import com.justra.app.ui.theme.SovereignNavy
import com.justra.app.ui.theme.WarmParchmentBase
import com.justra.app.ui.theme.WarmTerracotta
import com.justra.app.ui.voice.LiveWaveformCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceComplaintRegistrationScreen(
    onNavigateBack: () -> Unit,
    onCaseCreated: (caseId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComplaintVoiceViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val language by viewModel.selectedLanguage.collectAsState()
    val isTa = language == LanguagePreference.TAMIL

    val uiState by viewModel.uiState.collectAsState()
    val transcript by viewModel.editableTranscript.collectAsState()
    val waveforms by viewModel.liveWaveforms.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var isEditingGeneratedDraft by remember { mutableStateOf(false) }
    var userEditedDraft by remember { mutableStateOf("") }
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceIntake()
        } else {
            Toast.makeText(
                context,
                if (isTa) "குரல் பதிவு செய்ய அனுமதி தேவை (Microphone permission required)" else "RECORD_AUDIO permission is required for voice intake",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            try {
                viewModel.voiceManager.stopListening()
                viewModel.audioRecorderHelper.stopRecording()
            } catch (e: Exception) {
                // Ignore disposal errors safely
            }
        }
    }

    val toggleRecording = {
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (uiState is ComplaintVoiceUiState.Recording) {
            viewModel.stopVoiceIntake()
        } else {
            if (hasPermission) {
                viewModel.startVoiceIntake()
            } else {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "புகார் பதிவு செய்தல்" else "Register Legal Dispute",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepImperialNavy,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        Text(
                            text = if (isTa) "குரல்வழி சட்ட மனு உருவாக்கம்" else "Voice-Driven Legal Notice Drafting",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = OnSandstoneSurfaceVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("voice_complaint_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepImperialNavy
                        )
                    }
                },
                actions = {
                    // Language Switch Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SandstoneSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                        modifier = Modifier
                            .clickable { viewModel.toggleLanguage() }
                            .padding(end = 12.dp)
                            .testTag("voice_complaint_language_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = WarmTerracotta,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isTa) "தமிழ் (ta)" else "English (en)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WarmParchmentBase
                )
            )
        },
        containerColor = WarmParchmentBase,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Introductory Guidance Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SandstoneSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryNavyContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    tint = DeepImperialNavy,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isTa) "சட்ட மனு முறைமை" else "Statutory Legal Notice Generator",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepImperialNavy
                                    )
                                )
                                Text(
                                    text = if (isTa) {
                                        "1. தகவல்களைத் தட்டச்சு செய்க -> 2. மனுவை சரிபார்க்கவும் -> 3. சட்டம் சார்ந்த அதிகாரப்பூர்வ மனுவைப் பெறுங்கள்."
                                    } else {
                                        "1. Enter dispute facts -> 2. Validate narrative -> 3. Generate formal Indian statutory notice."
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = OnSandstoneSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }

                // Gemini Live Audio STT Microphone Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState is ComplaintVoiceUiState.Recording) Color(0xFFFFE8E0) else SandstoneSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (uiState is ComplaintVoiceUiState.Recording) WarmTerracotta else ParchmentOutline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (uiState is ComplaintVoiceUiState.Recording) {
                                    if (isTa) "நேரலை குரல் பதிவு பெறப்படுகிறது... (English / தமிழ் / Tanglish)" else "Streaming Live Audio... Speak in English, Tamil, or Tanglish"
                                } else {
                                    if (isTa) "குரல் மூலம் கூற நுண்ஒலியை அழுத்தவும்" else "Tap Microphone for Gemini Live STT"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy,
                                    lineHeight = 20.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Responsive Mic Trigger Button with Pulsing Scale Animation
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .scale(if (uiState is ComplaintVoiceUiState.Recording) pulseScale else 1.0f)
                                    .clip(CircleShape)
                                    .background(if (uiState is ComplaintVoiceUiState.Recording) WarmTerracotta else SovereignNavy)
                                    .clickable { toggleRecording() }
                                    .testTag("voice_complaint_mic_button")
                            ) {
                                Icon(
                                    imageVector = if (uiState is ComplaintVoiceUiState.Recording) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Microphone STT",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            if (uiState is ComplaintVoiceUiState.Recording) {
                                Spacer(modifier = Modifier.height(12.dp))
                                LiveWaveformCanvas(
                                    amplitudes = waveforms,
                                    isRecording = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                )
                            }
                        }
                    }
                }

                // Live Editable Transcript / Text Statement Panel
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SandstoneSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isTa) "பிரச்சனை விபரம் (Dispute Narrative)" else "Dispute Statement & Facts",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepImperialNavy
                                    )
                                )

                                if (transcript.isNotBlank()) {
                                    TextButton(
                                        onClick = { viewModel.updateTranscript("") },
                                        modifier = Modifier.testTag("voice_complaint_clear_transcript")
                                    ) {
                                        Text(
                                            text = if (isTa) "அழி" else "Clear",
                                            fontSize = 11.sp,
                                            color = WarmTerracotta
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = transcript,
                                onValueChange = { viewModel.updateTranscript(it) },
                                placeholder = {
                                    Text(
                                        text = if (isTa) {
                                            "எடுத்துக்காட்டு: 'நேற்று ஒரு கடைக்காரர் எனக்கு பழுதான பொருளை விற்றுவிட்டார். பணத்தைத் திரும்ப தர மறுக்கிறார்...'"
                                        } else {
                                            "Example: 'I purchased a defective appliance from the merchant on 12th Oct, but they refused warranty refund of Rs 15,000...'"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(color = OnSandstoneSurfaceVariant)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("voice_complaint_transcript_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = DeepImperialNavy,
                                    unfocusedBorderColor = ParchmentOutline,
                                    focusedTextColor = OnParchmentText,
                                    unfocusedTextColor = OnParchmentText
                                )
                            )
                        }
                    }
                }

            // Confirm & Generate Action Button with Pure White High-Contrast Typography
            item {
                AnimatedVisibility(
                    visible = transcript.isNotBlank() && uiState !is ComplaintVoiceUiState.Generating,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = { viewModel.generateComplaintFromTranscript() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SovereignNavy,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_and_generate_complaint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTa) "உறுதிசெய்து மனுவை உருவாக்குங்கள்" else "Confirm & Generate Legal Notice",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Loading / Generating State
            if (uiState is ComplaintVoiceUiState.Generating) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = DeepImperialNavy,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isTa) "சட்ட மனு உருவாக்கப்படுகிறது..." else "Synthesizing Statutory Complaint...",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepImperialNavy
                                    )
                                )
                                Text(
                                    text = if (isTa) "பிரிவுகள் மற்றும் நிவாரணம் சேர்க்கப்படுகிறது" else "Mapping facts to BNS & Consumer Protection Acts",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = DeepImperialNavy
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Error Display
            if (uiState is ComplaintVoiceUiState.Error) {
                val error = (uiState as ComplaintVoiceUiState.Error).error
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepHennaAlertContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = OnDeepHennaAlert,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall.copy(color = OnDeepHennaAlert)
                            )
                        }
                    }
                }
            }

            // Generated Complaint Review Card
            if (uiState is ComplaintVoiceUiState.Success) {
                val complaint = (uiState as ComplaintVoiceUiState.Success).complaint
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepImperialNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generated_complaint_review_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SageHerbSuccessContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = OnSageHerbSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isTa) "மனு தயார் (Dynamic)" else "Notice Generated",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = OnSageHerbSuccess
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = complaint.date,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = OnSandstoneSurfaceVariant
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = complaint.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = DeepImperialNavy
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Metadata rows
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SandstoneSurface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "🏛️ ${if (isTa) "அதிகார வரம்பு" else "Forum"}: ${complaint.recipientAuthority}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepImperialNavy
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "👤 ${if (isTa) "எதிர் தரப்பினர்" else "Opposing Party"}: ${complaint.opposingParty}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = OnSandstoneSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Persona Re-draft Switcher
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isTa) "வடிவமைக்கப்பட்டுள்ள நிலை:" else "Drafted Legal Persona:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepImperialNavy
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PrimaryNavyContainer
                                ) {
                                    Text(
                                        text = if (isTa) currentRole.titleTa else currentRole.titleEn,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepImperialNavy,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                UserRole.values().forEach { role ->
                                    val isCurrent = currentRole == role
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCurrent) DeepImperialNavy else SandstoneSurface,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) DeepImperialNavy else ParchmentOutline),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.setRole(role) }
                                            .testTag("redraft_role_chip_${role.id}")
                                    ) {
                                        Text(
                                            text = when (role) {
                                                UserRole.CITIZEN -> if (isTa) "குடிமகன்" else "Citizen"
                                                UserRole.LEGAL_COUNSEL -> if (isTa) "வக்கீல்" else "Counsel"
                                                UserRole.MSME_BUSINESS -> if (isTa) "MSME" else "MSME"
                                                UserRole.CYBER_FRAUD_VICTIM -> if (isTa) "சைபர்" else "Cyber"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isCurrent) Color.White else DeepImperialNavy
                                            ),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Statutory grounds chips
                            Text(
                                text = if (isTa) "சட்டப்பிரிவுகள்:" else "Statutory Violations:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                complaint.statutoryGrounds.forEach { ground ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PrimaryNavyContainer
                                    ) {
                                        Text(
                                            text = "⚖️ $ground",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = DeepImperialNavy,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = ParchmentOutline)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Full Draft Display or Edit Mode
                            if (isEditingGeneratedDraft) {
                                OutlinedTextField(
                                    value = userEditedDraft.ifBlank { complaint.fullDraftText },
                                    onValueChange = { userEditedDraft = it },
                                    label = { Text("Edit Legal Notice Text", color = DeepImperialNavy) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                        .testTag("complaint_draft_editor"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = DeepImperialNavy,
                                        unfocusedBorderColor = ParchmentOutline,
                                        focusedTextColor = OnParchmentText,
                                        unfocusedTextColor = OnParchmentText
                                    )
                                )
                            } else {
                                Text(
                                    text = userEditedDraft.ifBlank { complaint.fullDraftText },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = OnParchmentText,
                                        lineHeight = 16.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SandstoneSurface, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons Row: Edit, Copy, Save / Export
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (isEditingGeneratedDraft) {
                                            isEditingGeneratedDraft = false
                                        } else {
                                            if (userEditedDraft.isBlank()) {
                                                userEditedDraft = complaint.fullDraftText
                                            }
                                            isEditingGeneratedDraft = true
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("complaint_edit_toggle_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isEditingGeneratedDraft) Icons.Default.Save else Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = DeepImperialNavy,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEditingGeneratedDraft) "Done" else "Edit",
                                        fontSize = 11.sp,
                                        color = DeepImperialNavy
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        val textToCopy = userEditedDraft.ifBlank { complaint.fullDraftText }
                                        clipboardManager.setText(AnnotatedString(textToCopy))
                                        Toast.makeText(context, "Complaint copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("complaint_copy_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = DeepImperialNavy,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Copy",
                                        fontSize = 11.sp,
                                        color = DeepImperialNavy
                                    )
                                }

                                Button(
                                    onClick = {
                                        val finalDraft = userEditedDraft.ifBlank { complaint.fullDraftText }
                                        val finalComplaint = complaint.copy(fullDraftText = finalDraft)
                                        viewModel.saveAsActiveCase(finalComplaint) { createdCaseId ->
                                            Toast.makeText(context, "Case created successfully!", Toast.LENGTH_LONG).show()
                                            onCaseCreated(createdCaseId)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFFFDBCF),
                                        contentColor = DeepImperialNavy
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepImperialNavy),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .testTag("complaint_save_active_case_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Policy,
                                        contentDescription = null,
                                        tint = DeepImperialNavy,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTa) "வழக்காகப் பதிவு செய்" else "Open Case",
                                        fontSize = 11.sp,
                                        color = DeepImperialNavy
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
}
@Composable
private fun TextButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick, modifier = modifier) {
        content()
    }
}
