package com.justra.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.data.local.ChatMessageEntity
import com.justra.app.domain.model.AudioRecordingState
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.SenderRole
import com.justra.app.ui.components.AudioWaveformVisualizer
import com.justra.app.ui.components.InteractiveTamilVoiceWaveformVisualizer
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.TerracottaContainer
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.util.BilingualStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun ChatAssistantScreen(
    currentLanguage: LanguagePreference,
    messages: List<ChatMessageEntity>,
    recordingState: AudioRecordingState,
    audioWaveforms: List<Float>,
    initialPrompt: String?,
    liveTranscript: String = "",
    onSendMessage: (String) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onAddProofToVault: (key: String, value: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onClearChatHistory: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf(initialPrompt ?: "") }
    val listState = rememberLazyListState()

    LaunchedEffect(liveTranscript) {
        if (liveTranscript.isNotBlank()) {
            textInput = liveTranscript
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank()) {
            onSendMessage(initialPrompt)
            textInput = ""
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            NyayaTopBar(
                title = if (currentLanguage == LanguagePreference.TAMIL) "AI சட்ட உரையாடல்" else "Justra Legal Intake",
                subtitle = if (currentLanguage == LanguagePreference.TAMIL) "சட்டப்பிரிவு & சான்றுகள் பகுப்பாய்வு" else "Statute & Fact Categorization",
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                color = WarmIvorySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Inline live listening banner (no dialogue overlay)
                    AnimatedVisibility(visible = recordingState == AudioRecordingState.RECORDING) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFCDAD4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Live STT",
                                    tint = Color(0xFF5E130A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL)
                                        "கேட்கிறது... நேரடியாக தட்டச்சாகிறது..."
                                    else
                                        "Listening... Transcribing live into chat box...",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5E130A)
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Mic Button / Stop Button
                        if (recordingState == AudioRecordingState.RECORDING) {
                            IconButton(
                                onClick = onStopRecording,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(TerracottaAccentSecondary)
                                    .testTag("chat_stop_recording_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop",
                                    tint = Color.White
                                )
                            }
                        } else {
                            IconButton(
                                onClick = onStartRecording,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PaleSandstoneVariant)
                                    .testTag("chat_mic_record_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record Voice",
                                    tint = DeepIndigoSlatePrimary
                                )
                            }
                        }

                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "விவரங்களை தட்டச்சு செய்யவும்..." else "Type legal details...",
                                    color = Color(0xFF5A606A),
                                    fontSize = 14.sp
                                )
                            },
                            textStyle = TextStyle(
                                color = Color(0xFF14181F),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 22.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF14181F),
                                unfocusedTextColor = Color(0xFF14181F),
                                focusedContainerColor = Color(0xFFF3ECE1),
                                unfocusedContainerColor = Color(0xFFF3ECE1),
                                cursorColor = Color(0xFF0D1B2A),
                                focusedBorderColor = Color(0xFF0D1B2A),
                                unfocusedBorderColor = Color(0xFFD4CAB8),
                                focusedPlaceholderColor = Color(0xFF5A606A),
                                unfocusedPlaceholderColor = Color(0xFF5A606A)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_text_input_field")
                        )

                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    val send = textInput
                                    textInput = ""
                                    onSendMessage(send)
                                }
                            },
                            enabled = textInput.isNotBlank(),
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (textInput.isNotBlank()) DeepIndigoSlatePrimary else DeepIndigoSlatePrimary.copy(alpha = 0.3f))
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (messages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = PaleSandstoneVariant,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = DeepIndigoSlatePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (currentLanguage == LanguagePreference.TAMIL) "சட்ட விவரங்களை பகிரவும்" else "Begin Structured Legal Intake",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    )
                )

                Text(
                    text = if (currentLanguage == LanguagePreference.TAMIL)
                        "உங்கள் பிரச்சனையை விவரிக்கவும். சான்றுகள், காலக்கெடு மற்றும் தேவையான சட்ட அறிவிப்புகளை AI தயார் செய்யும்."
                    else
                        "Describe what happened. Justra will map applicable Indian Acts, detect evidence proof, and guide immediate actions.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF4A4E57),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 6.dp, start = 16.dp, end = 16.dp)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(messages) { msg ->
                    ChatBubble(
                        message = msg,
                        currentLanguage = currentLanguage,
                        onAddProofToVault = { key, value -> onAddProofToVault(key, value) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessageEntity,
    currentLanguage: LanguagePreference,
    onAddProofToVault: (key: String, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.senderRole == SenderRole.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bg = if (isUser) DeepIndigoSlatePrimary else PaleSandstoneVariant
    val textColor = if (isUser) Color.White else DeepIndigoSlatePrimary
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Column(
        horizontalAlignment = alignment,
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 16.dp
            ),
            color = bg,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null,
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Header (Assistant label)
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = TerracottaAccentSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Justra Legal Engine",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TerracottaAccentSecondary
                            )
                        )
                    }
                }

                Text(
                    text = message.messageContent,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textColor,
                        lineHeight = 22.sp
                    )
                )

                // Extracted actionable entity badge/chip
                if (message.extractedEntityKey != null && message.extractedEntityValue != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isUser) Color.White.copy(alpha = 0.15f) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaAccentSecondary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💡 ${message.extractedEntityKey}: ${message.extractedEntityValue}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) Color.White else DeepIndigoSlatePrimary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TerracottaContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        onAddProofToVault(message.extractedEntityKey, message.extractedEntityValue)
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = null,
                                        tint = TerracottaAccentSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (currentLanguage == LanguagePreference.TAMIL) "பெட்டகத்தில் சான்றாக சேர் (+)" else "Add Proof to Evidence Vault (+)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TerracottaAccentSecondary
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = if (isUser) Color.White.copy(alpha = 0.7f) else Color(0xFF5A606A)
                    ),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
