package com.justra.app.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.data.local.CaseEntity
import com.justra.app.data.local.ShareTokenEntity
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.components.NyayaTopBar
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CounselHandoffScreen(
    currentLanguage: LanguagePreference,
    caseEntity: CaseEntity?,
    tokensFlow: Flow<List<ShareTokenEntity>>,
    onGenerateShareToken: (validityHours: Int) -> Unit,
    onAppendAdvocateNotes: (tokenId: String, notes: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val tokens by tokensFlow.collectAsState(initial = emptyList())

    var advocateNotesInput by remember { mutableStateOf("") }
    var selectedTokenForNotes by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (currentLanguage == LanguagePreference.TAMIL) "வழக்கறிஞர் இணைப்பு (Handoff)" else "Legal Counsel Handoff",
                subtitle = caseEntity?.title ?: "Cryptographic Sharing Protocol",
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        },
        containerColor = Color(0xFFFAF7F2),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Sovereign Advocate Handoff Protocol Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF0F1E36),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "வழக்கறிஞருக்கு பாதுகாப்பான பகிர்வு" else "Advocate-Client Read-Only Protocol",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F1E36)
                                    )
                                )
                                Text(
                                    text = "Generates a time-delimited, cryptographically signed briefcase with read-only access for verified advocates.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF4A4E57))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onGenerateShareToken(72) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("generate_share_token_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "72 மணிநேர அணுகல் குறியீட்டை உருவாக்கு" else "Generate 72h Signed Access Link",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Case Summary Brief Preview
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (currentLanguage == LanguagePreference.TAMIL) "சுருக்கமான வழக்கறிஞர் ஆவணம்" else "Synthesized Counsel Briefing",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1E36)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Title: ${caseEntity?.title ?: "N/A"}\n• Category: ${caseEntity?.disputeCategory?.titleEn ?: "N/A"}\n• Opposing: ${caseEntity?.opposingParty ?: "Under Investigation"}\n• Est. Amount: ${caseEntity?.estimatedClaimAmount ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 20.sp,
                                color = Color(0xFF14181F)
                            )
                        )
                    }
                }
            }

            // 3. Active Handoff Tokens List
            if (tokens.isNotEmpty()) {
                item {
                    Text(
                        text = if (currentLanguage == LanguagePreference.TAMIL) "செயலில் உள்ள பகிர்வு இணைப்புகள்" else "Active Handoff Access Tokens",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F1E36)
                        )
                    )
                }

                items(tokens) { token ->
                    TokenCard(
                        token = token,
                        currentLanguage = currentLanguage,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString("https://justra.app/counsel/view?token=${token.tokenCode}&sig=${token.signatureHash.take(16)}"))
                        },
                        onShare = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Justra Legal Brief Access Token for Advocate Review:\nCode: ${token.tokenCode}\nLink: https://justra.app/counsel/view?token=${token.tokenCode}\nValid until: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(token.expiresAt))}"
                                )
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Counsel Link"))
                        },
                        onAddNoteClick = {
                            selectedTokenForNotes = token.tokenId
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TokenCard(
    token: ShareTokenEntity,
    currentLanguage: LanguagePreference,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onAddNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expiry = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(token.expiresAt))
    val isExpired = System.currentTimeMillis() > token.expiresAt

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isExpired) Color(0xFFF0EDED) else Color(0xFFF3ECE1)),
        border = BorderStroke(1.dp, if (isExpired) Color.Gray else Color(0xFFD4CAB8)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F1E36),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = token.tokenCode,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isExpired) Color(0xFF991B1B) else Color(0xFF15803D),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isExpired) "Expired" else "Valid to $expiry",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isExpired) Color(0xFF991B1B) else Color(0xFF15803D)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Signature: ${token.signatureHash.take(20)}...",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF4A4E57)
                )
            )

            if (!token.advocateNotes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "⚖️ Advocate Advisory Notes:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                        )
                        Text(
                            text = token.advocateNotes,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF14181F))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF0F1E36)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0F1E36))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 12.sp, color = Color(0xFF0F1E36))
                }

                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC05621)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp)
                }
            }
        }
    }
}

