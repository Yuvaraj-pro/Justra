package com.example.ui.screens.complaint

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
import com.example.domain.model.LanguagePreference
import com.example.domain.model.UserRole
import com.example.ui.theme.DeepHennaAlertContainer
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.DeepImperialNavy
import com.example.ui.theme.MutedAmberWarningContainer
import com.example.ui.theme.OnDeepHennaAlert
import com.example.ui.theme.OnImperialNavy
import com.example.ui.theme.OnMutedAmberWarning
import com.example.ui.theme.OnParchmentText
import com.example.ui.theme.OnSageHerbSuccess
import com.example.ui.theme.OnSandstoneSurfaceVariant
import com.example.ui.theme.ParchmentOutline
import com.example.ui.theme.PrimaryNavyContainer
import com.example.ui.theme.SageHerbSuccessContainer
import com.example.ui.theme.SandstoneCard
import com.example.ui.theme.SandstoneSurface
import com.example.ui.theme.SovereignNavy
import com.example.ui.theme.WarmParchmentBase
import com.example.ui.theme.WarmTerracotta
import com.example.ui.voice.LiveWaveformCanvas

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
    var activeTab by remember { mutableStateOf(0) }

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
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = SandstoneCard,
                contentColor = SovereignNavy,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorderStroke, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Text(
                            text = if (isTa) "குரல்வழி சட்டப் பகுப்பாய்வு" else "Voice AI Intake",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            text = if (isTa) "முழு வழக்குப் பதிவு" else "Full Case Filing",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            if (activeTab == 0) {
                VoiceComplaintScreen(
                    onNavigateBack = null,
                    onGenerateComplaint = { transcript, onResult ->
                        viewModel.analyzeVoiceGrievanceDynamic(transcript, onResult)
                    }
                )
            } else {
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
                                text = if (isTa) "குரல்வழி சட்ட மனு முறைமை" else "Two-Stage Voice Intake Pipeline",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy
                                )
                            )
                            Text(
                                text = if (isTa) {
                                    "1. மைக்ரோஃபோனை அழுத்திப் பேசுங்கள் -> 2. உரையைச் சரிபார்க்கவும் -> 3. சட்டம் சார்ந்த அதிகாரப்பூர்வ மனுவைப் பெறுங்கள்."
                                } else {
                                    "1. Speak grievance facts -> 2. Validate live transcript -> 3. Generate formal Indian statutory notice."
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

            // Legal Persona / Role Selector Panel
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_role_selector_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTa) "சட்ட ஆளுமை நிலை (Legal Persona):" else "Active Legal Persona:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimaryNavyContainer
                            ) {
                                Text(
                                    text = if (isTa) currentRole.badgeTa else currentRole.badgeEn,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepImperialNavy,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isTa) currentRole.subtitleTa else currentRole.subtitleEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = OnSandstoneSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Persona Choice Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            UserRole.values().forEach { role ->
                                val isSelected = currentRole == role
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) DeepImperialNavy else Color.White,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) DeepImperialNavy else ParchmentOutline
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setRole(role) }
                                        .testTag("voice_role_chip_${role.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = when (role) {
                                                UserRole.CITIZEN -> "🛡️"
                                                UserRole.LEGAL_COUNSEL -> "⚖️"
                                                UserRole.MSME_BUSINESS -> "🏢"
                                                UserRole.CYBER_FRAUD_VICTIM -> "🚨"
                                            },
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = when (role) {
                                                UserRole.CITIZEN -> if (isTa) "குடிமகன்" else "Citizen"
                                                UserRole.LEGAL_COUNSEL -> if (isTa) "வழக்கறிஞர்" else "Counsel"
                                                UserRole.MSME_BUSINESS -> if (isTa) "MSME" else "MSME"
                                                UserRole.CYBER_FRAUD_VICTIM -> if (isTa) "சைபர்" else "Cyber"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else DeepImperialNavy
                                            ),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Voice Control & Live Waveform Panel
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val isRecording = uiState is ComplaintVoiceUiState.Recording

                        Text(
                            text = if (isRecording) {
                                if (isTa) "பேசுங்கள், உங்கள் குரல் பதிவாகிறது..." else "Listening... Describe your legal problem"
                            } else {
                                if (isTa) "தொடங்க மைக்ரோஃபோனை அழுத்தவும்" else "Tap microphone to record statement"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isRecording) WarmTerracotta else DeepImperialNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pulsating Mic Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            if (isRecording) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .background(WarmTerracotta.copy(alpha = 0.22f))
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = if (isRecording) WarmTerracotta else DeepImperialNavy,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (isRecording) {
                                            viewModel.stopVoiceIntake()
                                        } else {
                                            viewModel.startVoiceIntake()
                                        }
                                    }
                                    .testTag("voice_complaint_mic_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = "Microphone Toggle",
                                        tint = OnImperialNavy,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Real-Time Hardware Waveform Visualizer Canvas
                        LiveWaveformCanvas(
                            amplitudes = waveforms,
                            isRecording = isRecording,
                            height = 64.dp
                        )
                    }
                }
            }

            // Live Editable Transcript Panel
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTa) "நேரடி உரை / சரிபார்ப்பு" else "Live Transcript & Review",
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
                                        "எடுத்துக்காட்டு: 'நேற்று ஒரு கடைக்காரர் எனக்கு பழுதான மடிக்கணினியை விற்றுவிட்டார். பணத்தைத் திரும்ப தர மறுக்கிறார்...'"
                                    } else {
                                        "Example: 'I purchased a defective appliance from merchant on 12th Oct, but they refused warranty and refund of Rs 15,000...'"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSandstoneSurfaceVariant)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
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

            // Confirm & Generate Action Button
            item {
                AnimatedVisibility(
                    visible = transcript.isNotBlank() && uiState !is ComplaintVoiceUiState.Generating,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = { viewModel.generateComplaintFromTranscript() },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_and_generate_complaint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = OnImperialNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTa) "உறுதிசெய்து மனுவை உருவாக்குங்கள்" else "Confirm & Generate Legal Notice",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnImperialNavy
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
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .testTag("complaint_save_active_case_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Policy,
                                        contentDescription = null,
                                        tint = OnImperialNavy,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTa) "வழக்காகப் பதிவு செய்" else "Open Case",
                                        fontSize = 11.sp,
                                        color = OnImperialNavy
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
}
@Composable
private fun TextButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick, modifier = modifier) {
        content()
    }
}
