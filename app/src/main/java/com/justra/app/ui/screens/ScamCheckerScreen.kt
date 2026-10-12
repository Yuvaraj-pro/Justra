package com.justra.app.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.data.local.ScamIncidentEntity
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.RiskLevel
import com.justra.app.ui.components.EmergencyHelplineBar
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.components.RiskLevelBadge
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.HennaRedAlertContainer
import com.justra.app.ui.theme.HennaRedAlertText
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScamCheckerScreen(
    currentLanguage: LanguagePreference,
    currentAnalysis: ScamIncidentEntity?,
    isAnalyzing: Boolean,
    historyList: List<ScamIncidentEntity>,
    onAnalyzeText: (String) -> Unit,
    onDeleteIncident: (String) -> Unit,
    onDialHelpline: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (currentLanguage == LanguagePreference.TAMIL) "மோசடி செய்தி சோதனை" else "Scam & Fraud Checker",
                subtitle = if (currentLanguage == LanguagePreference.TAMIL) "APK, SMS & மிரட்டல் செய்தி பகுப்பாய்வு" else "Threat Analysis & 1930 Relay",
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        },
        containerColor = WarmIvorySurface,
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
            // 1. Emergency 1930 Golden Hour Relay Box
            item {
                EmergencyHelplineBar(
                    language = currentLanguage,
                    onDial1930 = { onDialHelpline("1930") },
                    onDial112 = { onDialHelpline("112") },
                    onDial1915 = { onDialHelpline("1915") }
                )
            }

            // 2. Input Box with Paste Button
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "சந்தேகத்திற்குரிய செய்தியை உள்ளிடவும்" else "Input Suspicious Message / SMS / Link",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )

                            TextButton(
                                onClick = {
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        inputText = clip
                                    }
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = DeepIndigoSlatePrimary),
                                modifier = Modifier.testTag("paste_clipboard_button")
                            ) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, tint = DeepIndigoSlatePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "ஒட்டுக (Paste)" else "Paste",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL)
                                        "எ.கா: 'Dear Customer, your electricity bill is unpaid. Power cut tonight. Download APK link http://...' போன்ற செய்தியை இங்கு இடவும்."
                                    else
                                        "e.g., 'Dear customer, your bank account is blocked. Update PAN immediately by clicking http://... or calling...'",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 5,
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("scam_input_text_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onAnalyzeText(inputText)
                                }
                            },
                            enabled = inputText.isNotBlank() && !isAnalyzing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepIndigoSlatePrimary,
                                contentColor = Color.White,
                                disabledContainerColor = DeepIndigoSlatePrimary.copy(alpha = 0.4f),
                                disabledContentColor = Color.White.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("analyze_scam_button")
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = if (currentLanguage == LanguagePreference.TAMIL) "அச்சுறுத்தலை ஆய்வு செய்க" else "Analyze Threat Indicators",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Current Live Analysis Result Card
            if (currentAnalysis != null) {
                item {
                    val analysis = currentAnalysis
                    val isCritical = analysis.riskLevel == RiskLevel.HIGH || analysis.riskLevel == RiskLevel.CRITICAL
                    val containerColor = if (isCritical) HennaRedAlertContainer else PaleSandstoneVariant
                    val strokeColor = if (isCritical) HennaRedAlertText else DeepIndigoSlatePrimary

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, strokeColor.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scam_analysis_result_card")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (analysis.isScamDetected) "🚨 FRAUD / SCAM DETECTED" else "✅ NO IMMEDIATE THREAT",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isCritical) HennaRedAlertText else SageGreenSuccessText
                                    )
                                )
                                RiskLevelBadge(riskLevel = analysis.riskLevel, language = currentLanguage)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Red flags list
                            if (analysis.detectedRedFlags.isNotEmpty()) {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "கண்டறியப்பட்ட எச்சரிக்கை குறிகள் (Red Flags):" else "Detected Fraud Indicators & Red Flags:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                analysis.detectedRedFlags.forEach { flag ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Text("⚠️ ", fontSize = 12.sp)
                                        Text(flag, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = DeepIndigoSlatePrimary))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Safety steps list
                            if (analysis.safetyGuidance.isNotEmpty()) {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "உடனடி பாதுகாப்பு வழிகாட்டல்:" else "Immediate Protective Action:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                analysis.safetyGuidance.forEach { step ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Text("🛡️ ", fontSize = 12.sp)
                                        Text(step, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = DeepIndigoSlatePrimary))
                                    }
                                }
                            }

                            if (isCritical) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onDialHelpline("1930") },
                                    colors = ButtonDefaults.buttonColors(containerColor = HennaRedAlertText),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White)
                                        Text(
                                            text = if (currentLanguage == LanguagePreference.TAMIL) "1930 உதவி எண்ணை அழைக்கவும் (Golden Hour)" else "Emergency 1930 Call (Trigger Bank Freeze)",
                                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Past Scan History
            if (historyList.isNotEmpty()) {
                item {
                    Text(
                        text = if (currentLanguage == LanguagePreference.TAMIL) "முந்தைய சோதனைகள்" else "Analysis History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                }

                items(historyList) { item ->
                    HistoryCard(
                        incident = item,
                        currentLanguage = currentLanguage,
                        onDelete = { onDeleteIncident(item.incidentId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    incident: ScamIncidentEntity,
    currentLanguage: LanguagePreference,
    onDelete: () -> Unit
) {
    val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(incident.timestamp))
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                RiskLevelBadge(riskLevel = incident.riskLevel, language = currentLanguage)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = incident.analyzedText,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall.copy(color = DeepIndigoSlatePrimary)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
