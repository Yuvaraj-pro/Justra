package com.justra.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.*
import com.justra.app.ui.viewmodel.NyayaMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val securityManager = viewModel.securityManager

    // Security Preferences State
    var biometricEnabled by remember { mutableStateOf(securityManager.isBiometricLockEnabled()) }
    var autoLockTimeout by remember { mutableStateOf(securityManager.getAutoLockTimeoutMinutes()) }
    var stealthModeEnabled by remember { mutableStateOf(securityManager.isStealthModeEnabled()) }

    // Language & Interface Preferences
    var dualSubtitleEnabled by remember { mutableStateOf(securityManager.isDualSubtitleEnabled()) }

    // Privacy & Data Sovereignty State
    var aiTelemetryEnabled by remember { mutableStateOf(securityManager.isAiTelemetryEnabled()) }
    var autoPurgeDocsEnabled by remember { mutableStateOf(securityManager.isAutoPurgeDocsEnabled()) }
    var offlineForced by remember { mutableStateOf(securityManager.isOfflineModeForced()) }

    // Dialog States
    var showPinChangeDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showLegalDisclaimer by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsOfService by remember { mutableStateOf(false) }
    var exportedJsonString by remember { mutableStateOf("") }
    var deleteConfirmationInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var pinVisibility by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTa) "செயலி அமைப்புகள்" else "App Settings & Security",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SovereignNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvorySurface)
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier.testTag("settings_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
        ) {
            // 1. Language & Preference Settings
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSectionHeader(
                            icon = Icons.Default.Language,
                            title = if (isTa) "மொழி & இடைமுகம்" else "Language & Interface"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Language Selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleLanguage() }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isTa) "முதன்மை பயன்பாட்டு மொழி" else "Primary App Language",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = if (isTa) "ஆங்கிலம் / தமிழ் இடைமுகம்" else "English / Tamil interface switching",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SovereignNavy
                            ) {
                                Text(
                                    text = if (isTa) "தமிழ் (Selected)" else "English (Selected)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))

                        // Dual Subtitles
                        SettingsSwitchRow(
                            title = if (isTa) "இருமொழி துணைத்தலைப்புகள்" else "Dual Subtitles Display",
                            subtitle = if (isTa) "தமிழ் மற்றும் ஆங்கிலத்தில் சட்ட விபரங்களை காண்க" else "Show both English & Tamil titles side-by-side",
                            checked = dualSubtitleEnabled,
                            onCheckedChange = {
                                dualSubtitleEnabled = it
                                securityManager.setDualSubtitleEnabled(it)
                            }
                        )
                    }
                }
            }

            // 2. Security & Biometric Lock Settings
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSectionHeader(
                            icon = Icons.Default.Security,
                            title = if (isTa) "பாதுகாப்பு & பயோமெட்ரிக் பூட்டு" else "Security & Biometric Lock"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Biometric Switch
                        SettingsSwitchRow(
                            title = if (isTa) "பயோமெட்ரிக் / கைரேகை பூட்டு" else "Fingerprint / Biometric Lock",
                            subtitle = if (isTa) "செயலியை திறக்க சாதன கைரேகையை பயன்படுத்தவும்" else "Require fingerprint scan to open app",
                            checked = biometricEnabled,
                            onCheckedChange = {
                                biometricEnabled = it
                                securityManager.setBiometricLockEnabled(it)
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))

                        // Reset Security PIN
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPinChangeDialog = true }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isTa) "பாதுகாப்பு PIN மாற்று" else "Change Passcode / PIN",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = if (isTa) "4-இலக்க அவசர பயன்பாட்டு PIN" else "Update 4-digit master PIN",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                                )
                            }
                            Icon(imageVector = Icons.Default.Key, contentDescription = "PIN", tint = SovereignNavy)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))

                        // Stealth Mode
                        SettingsSwitchRow(
                            title = if (isTa) "மறைமுக பயன்முறை (Stealth Mode)" else "Stealth Privacy Screen",
                            subtitle = if (isTa) "சமீபத்திய பயன்பாட்டு திரை காட்சிகளை தடுக்கும்" else "Prevent screenshots & mask recent apps preview",
                            checked = stealthModeEnabled,
                            onCheckedChange = {
                                stealthModeEnabled = it
                                securityManager.setStealthModeEnabled(it)
                            }
                        )
                    }
                }
            }

            // 3. Privacy & Data Sovereignty Settings
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSectionHeader(
                            icon = Icons.Default.Policy,
                            title = if (isTa) "தனியுரிமை & AI தரவு பாதுகாப்பு" else "Privacy & AI Data Sovereignty"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // AI Telemetry
                        SettingsSwitchRow(
                            title = if (isTa) "அநாமதேய AI முன்னேற்ற தரவு" else "Anonymous AI Quality Telemetry",
                            subtitle = if (isTa) "சட்ட மாதிரி துல்லியத்தை மேம்படுத்த உதவுகிறது" else "Allow anonymous metrics to improve Gemini AI responses",
                            checked = aiTelemetryEnabled,
                            onCheckedChange = {
                                aiTelemetryEnabled = it
                                securityManager.setAiTelemetryEnabled(it)
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))

                        // Offline Mode Forced
                        SettingsSwitchRow(
                            title = if (isTa) "ஆஃப்லைன் பயன்முறையை கட்டாயப்படுத்து" else "Force Offline Local AI Engine",
                            subtitle = if (isTa) "அனைத்து சட்ட பகுப்பாய்வுகளையும் சாதனத்தில் மட்டும் இயக்கும்" else "Process all legal analysis on-device only",
                            checked = offlineForced,
                            onCheckedChange = {
                                offlineForced = it
                                securityManager.setOfflineModeForced(it)
                            }
                        )
                    }
                }
            }

            // 4. Data Export & Account Reset
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSectionHeader(
                            icon = Icons.Default.Download,
                            title = if (isTa) "காப்புப்பிரதி & கணக்கு மீட்டமைப்பு" else "Backup & Data Management"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Export Dossiers
                        OutlinedButton(
                            onClick = {
                                exportedJsonString = securityManager.exportAllUserDataJson()
                                showExportDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = "Export")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "வழக்கு காப்புப்பிரதியை நகலெடு (Export JSON)" else "Export Dossiers & Evidence JSON",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Delete Data
                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = HennaRedAlertText),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Delete")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "அனைத்து உள்ளூர் தரவுகளையும் நீக்கு" else "Clear Local App Storage & Reset",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 5. Legal & Compliance Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSectionHeader(
                            icon = Icons.Default.Info,
                            title = if (isTa) "சட்ட பொறுப்புத்துறப்பு & தகவல்" else "Legal Compliance & Info"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLegalDisclaimer = true }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTa) "சட்ட பொறுப்புத்துறப்பு (Legal Disclaimer)" else "Judicial AI Disclaimer",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            )
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = SovereignNavy)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.Gray.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPrivacyPolicy = true }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTa) "தனியுரிமை கொள்கை (Privacy Policy)" else "Privacy Policy & Zero-Knowledge Architecture",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            )
                            Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = SovereignNavy)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // App Version Badge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Justra AI v2.4.0 • Gemini 3.5 Flash Engine Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondaryDark,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // PIN Reset Dialog
    if (showPinChangeDialog) {
        AlertDialog(
            onDismissRequest = { showPinChangeDialog = false },
            title = { Text(if (isTa) "புதிய PIN அமைக்க" else "Set New Security PIN") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4) newPinInput = it },
                        label = { Text(if (isTa) "4-இலக்க புதிய PIN" else "New 4-digit PIN") },
                        visualTransformation = if (pinVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { if (it.length <= 4) confirmPinInput = it },
                        label = { Text(if (isTa) "உறுதிப்படுத்தவும்" else "Confirm PIN") },
                        visualTransformation = if (pinVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPinInput.length == 4 && newPinInput == confirmPinInput) {
                        securityManager.saveSecurityPin(newPinInput)
                        Toast.makeText(context, if (isTa) "PIN புதுப்பிக்கப்பட்டது" else "PIN Updated!", Toast.LENGTH_SHORT).show()
                        showPinChangeDialog = false
                    } else {
                        Toast.makeText(context, if (isTa) "PIN பொருந்தவில்லை" else "PINs do not match", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(if (isTa) "சேமி" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinChangeDialog = false }) {
                    Text(if (isTa) "ரத்து" else "Cancel")
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(if (isTa) "தரவு காப்புப்பிரதி" else "Export Encrypted Data") },
            text = {
                Text(if (isTa) "உங்கள் வழக்கு விபரங்கள் JSON வடிவில் உருவாக்கப்பட்டுள்ளது. அதை நகலெடுத்து பாதுகாப்பாக வைக்கலாம்." else "Your encrypted dossiers are prepared in JSON format. Copy to clipboard to backup.")
            },
            confirmButton = {
                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Justra Export", exportedJsonString))
                    Toast.makeText(context, if (isTa) "JSON நகலெடுக்கப்பட்டது!" else "JSON copied to Clipboard!", Toast.LENGTH_SHORT).show()
                    showExportDialog = false
                }) {
                    Text(if (isTa) "நகலெடு (Copy)" else "Copy JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text(if (isTa) "மூடு" else "Close")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(if (isTa) "அனைத்து தரவுகளையும் நீக்கவா?" else "Clear All App Data?") },
            text = {
                Column {
                    Text(if (isTa) "இந்த நடவடிக்கை உங்கள் உள்ளூர் வழக்குகள் மற்றும் அமைப்புகளை நீக்கும்." else "This will permanently wipe local cases, biometrics, and preferences.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(if (isTa) "உறுதிப்படுத்த 'DELETE' என தட்டச்சு செய்க:" else "Type 'DELETE' to confirm:")
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = deleteConfirmationInput,
                        onValueChange = { deleteConfirmationInput = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (deleteConfirmationInput == "DELETE") {
                            securityManager.clearAllUserData()
                            Toast.makeText(context, if (isTa) "தரவு மீட்டமைக்கப்பட்டது" else "Data Wiped!", Toast.LENGTH_SHORT).show()
                            showDeleteConfirmDialog = false
                            onNavigateToLogin()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = HennaRedAlertText)
                ) {
                    Text(if (isTa) "நீக்கு (Wipe)" else "Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(if (isTa) "ரத்து" else "Cancel")
                }
            }
        )
    }

    // Legal Disclaimer Dialog
    if (showLegalDisclaimer) {
        AlertDialog(
            onDismissRequest = { showLegalDisclaimer = false },
            title = { Text(if (isTa) "சட்ட பொறுப்புத்துறப்பு" else "Judicial AI Disclaimer") },
            text = {
                Text(
                    text = if (isTa)
                        "Justra AI ஒரு வழிகாட்டல் அமைப்பாகும். இது அதிகாரப்பூர்வ வழக்கறிஞர் ஆலோசனையை மாற்றாது. முக்கியமான சட்ட நடவடிக்கைகளுக்கு வழக்கறிஞரை அணுகவும்."
                    else
                        "Justra AI provides legal informational dossiers and assistance under Indian Law. It does not substitute professional legal counsel or advocate representation."
                )
            },
            confirmButton = {
                TextButton(onClick = { showLegalDisclaimer = false }) {
                    Text(if (isTa) "புரிந்தது" else "Understood")
                }
            }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicy) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicy = false },
            title = { Text(if (isTa) "தனியுரிமை கொள்கை" else "Zero-Knowledge Architecture") },
            text = {
                Text(
                    text = if (isTa)
                        "உங்கள் சான்றுகள் மற்றும் சான்றாவணங்கள் உங்கள் சாதனத்தில் மட்டுமே சேமிக்கப்படும்."
                    else
                        "All evidence files and case files remain encrypted locally on your Android device."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicy = false }) {
                    Text(if (isTa) "மூடு" else "Close")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = SovereignNavy)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SovereignNavy
            )
        )
    }
}
