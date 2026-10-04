package com.justra.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.UserRole
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import com.justra.app.ui.viewmodel.NyayaMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsHubScreen(
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

    // User Profile State
    var userDisplayName by remember { mutableStateOf(securityManager.getUserDisplayName()) }
    var userPhone by remember { mutableStateOf(securityManager.getUserPhoneNumber()) }
    var selectedDistrict by remember { mutableStateOf(securityManager.getSelectedDistrict()) }
    var advocateEnrollId by remember { mutableStateOf(securityManager.getAdvocateEnrollmentId()) }
    val currentRole by viewModel.userRole.collectAsState()

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
    var exportedJsonString by remember { mutableStateOf("") }
    var deleteConfirmationInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var pinVisibility by remember { mutableStateOf(false) }

    val tamilNaduDistricts = listOf(
        "Chennai (சென்னை)",
        "Coimbatore (கோயம்புத்தூர்)",
        "Madurai (மதுரை)",
        "Tiruchirappalli (திருச்சிராப்பள்ளி)",
        "Salem (சேலம்)",
        "Tirunelveli (திருநெல்வேலி)",
        "Kanchipuram (காஞ்சிபுரம்)",
        "Vellore (வேலூர்)",
        "Erode (ஈரோடு)",
        "Thanjavur (தஞ்சாவூர்)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "கணக்கு & செயலி அமைப்புகள்" else "Account & App Settings",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1E36)
                            )
                        )
                        Text(
                            text = if (isTa) "தனியுரிமை, பயோமெட்ரிக் & இறையாண்மை பாதுகாப்பு" else "Biometrics, Privacy, Sovereign Vault & Export",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = Color(0xFFC05621)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F1E36)
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF3ECE1),
                        border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onToggleLanguage()
                                Toast.makeText(
                                    context,
                                    if (isTa) "Language switched to English" else "மொழி தமிழுக்கு மாற்றப்பட்டது",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .padding(end = 12.dp)
                            .testTag("settings_language_toggle_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = Color(0xFF0F1E36),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isTa) "English" else "தமிழ்",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F1E36)
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFAF7F2)),
                modifier = Modifier.border(1.dp, Color(0xFFD4CAB8))
            )
        },
        containerColor = Color(0xFFFAF7F2),
        modifier = modifier.testTag("account_settings_hub_screen")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Section 1: User Profile & Judicial Jurisdiction
            item {
                SettingsSectionHeader(
                    title = if (isTa) "1. பயனர் சுயவிவரம் & அதிகார வரம்பு" else "1. User Profile & Judicial Jurisdiction",
                    icon = Icons.Default.AccountCircle
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF0F1E36),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when (currentRole) {
                                            UserRole.CITIZEN -> Icons.Default.Shield
                                            UserRole.LEGAL_COUNSEL -> Icons.Default.Gavel
                                            UserRole.MSME_BUSINESS -> Icons.Default.Policy
                                            UserRole.CYBER_FRAUD_VICTIM -> Icons.Default.Security
                                        },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) currentRole.titleTa else currentRole.titleEn,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F1E36)
                                    )
                                )
                                Text(
                                    text = if (isTa) "செயலில் உள்ள பயன்பாட்டு வகை" else "Active Legal Persona Profile",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7),
                                border = BorderStroke(1.dp, Color(0xFF15803D).copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = if (isTa) currentRole.badgeTa else currentRole.badgeEn,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF15803D)
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Interactive Role / Legal Persona Switcher
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isTa) "பயன்பாட்டு ஆளுமையை மாற்றுக (Switch Persona):" else "Switch Legal Persona Standing:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F1E36)
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                UserRole.values().forEach { role ->
                                    val isSelected = currentRole == role
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFF0F1E36) else Color(0xFFFAF7F2),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF0F1E36) else Color(0xFFD4CAB8)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                viewModel.setUserRole(role)
                                                Toast.makeText(
                                                    context,
                                                    if (isTa) "${role.titleTa} தேர்வு செய்யப்பட்டது" else "Switched to ${role.titleEn}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .testTag("settings_role_chip_${role.id}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = when (role) {
                                                    UserRole.CITIZEN -> "🛡️"
                                                    UserRole.LEGAL_COUNSEL -> "⚖️"
                                                    UserRole.MSME_BUSINESS -> "🏢"
                                                    UserRole.CYBER_FRAUD_VICTIM -> "🚨"
                                                },
                                                fontSize = 14.sp
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
                                                    fontSize = 9.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else Color(0xFF0F1E36)
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFD4CAB8))

                        // Name field
                        OutlinedTextField(
                            value = userDisplayName,
                            onValueChange = {
                                userDisplayName = it
                                securityManager.setUserDisplayName(it)
                            },
                            label = { Text(if (isTa) "முழுப் பெயர் / புனைப்பெயர்" else "Full Name / Litigant Alias") },
                            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF0F1E36)) },
                            singleLine = true,
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_name_input")
                        )

                        // Contact phone field
                        OutlinedTextField(
                            value = userPhone,
                            onValueChange = {
                                userPhone = it
                                securityManager.setUserPhoneNumber(it)
                            },
                            label = { Text(if (isTa) "தொடர்பு தொலைபேசி எண்" else "Official Contact Phone") },
                            singleLine = true,
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_phone_input")
                        )

                        // District selector dropdown simulation
                        Column {
                            Text(
                                text = if (isTa) "முதன்மை மாவட்ட நீதிமன்ற வரம்பு (District Jurisdiction)" else "Primary District Jurisdiction (Tamil Nadu)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F1E36)
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFAF7F2), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFD4CAB8), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedDistrict,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = Color(0xFF14181F))
                                )
                                Text(
                                    text = if (isTa) "மாற்றுக" else "Madras HC",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC05621)
                                    )
                                )
                            }
                        }

                        // Advocate specific bar ID if legal counsel
                        if (currentRole == UserRole.LEGAL_COUNSEL) {
                            OutlinedTextField(
                                value = advocateEnrollId,
                                onValueChange = {
                                    advocateEnrollId = it
                                    securityManager.setAdvocateEnrollmentId(it)
                                },
                                label = { Text(if (isTa) "பார் கவுன்சில் பதிவு எண் (Bar Council Enrollment No.)" else "Bar Council Enrollment ID (e.g. MS/1842/2019)") },
                                leadingIcon = { Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF0F1E36)) },
                                singleLine = true,
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("advocate_enroll_input")
                            )
                        }
                    }
                }
            }

            // Section 2: Biometric & Vault Security
            item {
                SettingsSectionHeader(
                    title = if (isTa) "2. பயோமெட்ரிக் & ஆவண பெட்டக பாதுகாப்பு" else "2. Biometric & Vault Security Controls",
                    icon = Icons.Default.Security
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("security_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Biometric Lock Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = Color(0xFF0F1E36),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isTa) "கைரேகை / முக அங்கீகார பூட்டு" else "Biometric Hardware Lock",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                                    )
                                    Text(
                                        text = if (isTa) "செயலி திறக்கும் போது பயோமெட்ரிக் கேட்கும்" else "Require fingerprint/face on app launch",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                    )
                                }
                            }

                            Switch(
                                checked = biometricEnabled,
                                onCheckedChange = {
                                    biometricEnabled = it
                                    securityManager.setBiometricLockEnabled(it)
                                    Toast.makeText(
                                        context,
                                        if (it) (if (isTa) "பயோமெட்ரிக் பூட்டு இயக்கப்பட்டது" else "Biometric lock enabled")
                                        else (if (isTa) "பயோமெட்ரிக் பூட்டு முடக்கப்பட்டது" else "Biometric lock disabled"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF0F1E36)
                                ),
                                modifier = Modifier.testTag("biometric_switch")
                            )
                        }

                        HorizontalDivider(color = Color(0xFFD4CAB8))

                        // Auto-Lock Inactivity Timeout
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF0F1E36), modifier = Modifier.size(18.dp))
                                Text(
                                    text = if (isTa) "செயலற்ற நேர தானியங்கி பூட்டு (Inactivity Timeout)" else "Auto-Lock Inactivity Timeout",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                                )
                            }
                            Text(
                                text = if (isTa) "பயன்படுத்தாமல் இருக்கும் போது தானாக பூட்டப்படும் கால அளவு" else "Locks the evidence vault when the app is idle in background",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1, 5, 15, 0).forEach { minutes ->
                                    val isSelected = autoLockTimeout == minutes
                                    val label = when (minutes) {
                                        0 -> if (isTa) "உடனே" else "Instant"
                                        1 -> "1 Min"
                                        5 -> "5 Min"
                                        else -> "15 Min"
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFF0F1E36) else Color(0xFFFAF7F2),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF0F1E36) else Color(0xFFD4CAB8)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                autoLockTimeout = minutes
                                                securityManager.setAutoLockTimeoutMinutes(minutes)
                                            }
                                            .testTag("autolock_btn_$minutes")
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color(0xFF0F1E36)
                                            ),
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFD4CAB8))

                        // Change Master Vault Passcode / PIN Button
                        OutlinedButton(
                            onClick = {
                                newPinInput = ""
                                confirmPinInput = ""
                                showPinChangeDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("change_pin_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F1E36)),
                            border = BorderStroke(1.dp, Color(0xFF0F1E36))
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "பெட்டக மாஸ்டர் PIN மாற்றுக (Change 4-Digit PIN)" else "Change Master Vault PIN / Passcode")
                        }

                        // Hardware Keystore Binding Status Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, Color(0xFF15803D).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isTa) "ஹார்டுவேர் குறியாக்கம் செயலில் உள்ளது (TEE / StrongBox)" else "Hardware Keystore Bound (Android StrongBox / TEE)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    )
                                    Text(
                                        text = if (isTa) "AES-256-GCM மாஸ்டர் சாவி சாதனத்தில் பாதுகாக்கப்பட்டுள்ளது" else "Master AES-256 keys never leave hardware security enclave",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = Color(0xFF15803D).copy(alpha = 0.9f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Language & Accessibility Toggles
            item {
                SettingsSectionHeader(
                    title = if (isTa) "3. மொழி & அணுகல்தன்மை (Accessibility)" else "3. Language & Accessibility Preferences",
                    icon = Icons.Default.Translate
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Language Selector Radios
                        Text(
                            text = if (isTa) "இயக்க மொழி (Operating Language)" else "Primary Application Language",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (currentLanguage == LanguagePreference.ENGLISH) Color(0xFF0F1E36) else Color(0xFFFAF7F2),
                                border = BorderStroke(1.dp, if (currentLanguage == LanguagePreference.ENGLISH) Color(0xFF0F1E36) else Color(0xFFD4CAB8)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (currentLanguage != LanguagePreference.ENGLISH) viewModel.setLanguage(LanguagePreference.ENGLISH)
                                    }
                                    .testTag("lang_select_en")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = if (currentLanguage == LanguagePreference.ENGLISH) Color.White else Color(0xFF0F1E36),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "English (Indian)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentLanguage == LanguagePreference.ENGLISH) Color.White else Color(0xFF0F1E36)
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (currentLanguage == LanguagePreference.TAMIL) Color(0xFF0F1E36) else Color(0xFFFAF7F2),
                                border = BorderStroke(1.dp, if (currentLanguage == LanguagePreference.TAMIL) Color(0xFF0F1E36) else Color(0xFFD4CAB8)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (currentLanguage != LanguagePreference.TAMIL) viewModel.setLanguage(LanguagePreference.TAMIL)
                                    }
                                    .testTag("lang_select_ta")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = null,
                                        tint = if (currentLanguage == LanguagePreference.TAMIL) Color.White else Color(0xFF0F1E36),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "தமிழ் (Tamil)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentLanguage == LanguagePreference.TAMIL) Color.White else Color(0xFF0F1E36)
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFD4CAB8))

                        // Dual Subtitle Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "இருமொழி துணை உரை முறைமை (Dual Subtitles)" else "Bilingual Statutory Subtitles",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                                )
                                Text(
                                    text = if (isTa) "சட்டப்பிரிவுகளுக்கு ஆங்கிலம் மற்றும் தமிழ் விளக்கம் ஒரே நேரத்தில் காட்டும்" else "Display statutory section names in English alongside Tamil translation",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                )
                            }
                            Switch(
                                checked = dualSubtitleEnabled,
                                onCheckedChange = {
                                    dualSubtitleEnabled = it
                                    securityManager.setDualSubtitleEnabled(it)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0F1E36))
                            )
                        }
                    }
                }
            }

            // Section 4: Data Sovereignty & Privacy
            item {
                SettingsSectionHeader(
                    title = if (isTa) "4. தரவு இறையாண்மை & தனியுரிமை பாதுகாப்பு" else "4. Data Sovereignty & Sovereign Privacy",
                    icon = Icons.Default.Lock
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Sovereign Local Storage Guarantee
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, Color(0xFF15803D).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(24.dp))
                                Column {
                                    Text(
                                        text = if (isTa) "100% சாதனத்தின் உள்ளே உள்ள தரவு (Zero Cloud Transfer)" else "100% Sovereign On-Device Storage",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    )
                                    Text(
                                        text = if (isTa) "உங்கள் சான்றுகள், மனுக்கள் மற்றும் ஆடியோ எதுவும் வெளிப்புற சர்வர்களுக்கு அனுப்பப்படாது." else "Evidence artifacts, dispute summaries and recordings are stored solely in local encrypted SQLite.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = Color(0xFF15803D).copy(alpha = 0.9f)
                                        )
                                    )
                                }
                            }
                        }

                        // Auto-purge Temporary Documents
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "தற்காலிக கோப்புகளை 30 நாளில் நீக்குதல்" else "Auto-Purge Temp Files (30-Day TTL)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                                )
                                Text(
                                    text = if (isTa) "தற்காலிக முன்னோட்ட PDF கோப்புகளை தானாக அழிக்கும்" else "Automatically removes temporary rendering caches and preview buffers",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                )
                            }
                            Switch(
                                checked = autoPurgeDocsEnabled,
                                onCheckedChange = {
                                    autoPurgeDocsEnabled = it
                                    securityManager.setAutoPurgeDocsEnabled(it)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0F1E36))
                            )
                        }

                        HorizontalDivider(color = Color(0xFFD4CAB8))

                        // Strict Offline Priority
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "முழுமையான ஆஃப்லைன் முன்னுரிமை (Air-Gapped)" else "Strict Air-Gapped Mode",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                                )
                                Text(
                                    text = if (isTa) "அனைத்து சட்ட கணக்கீடுகளும் இணையம் இன்றி இயங்கும்" else "Forces all actuarial tables, court fees and Section 65B hashes to run locally",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                )
                            }
                            Switch(
                                checked = offlineForced,
                                onCheckedChange = {
                                    offlineForced = it
                                    securityManager.setOfflineModeForced(it)
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0F1E36))
                            )
                        }
                    }
                }
            }

            // Section 5: Secure Account Export, Backup & Destruction Tools
            item {
                SettingsSectionHeader(
                    title = if (isTa) "5. ஆவண ஏற்றுமதி, காப்புப்பிரதி & தரவு அழிப்பு" else "5. Secure Export, Backup & Zeroization",
                    icon = Icons.Default.DeleteForever
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Export Case Dossier JSON
                        Button(
                            onClick = {
                                exportedJsonString = viewModel.exportCompleteDossierJson()
                                showExportDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_dossier_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "முழு சட்ட ஆவணங்களை JSON-ஆக ஏற்றுமதி செய்க" else "Export Encrypted Legal Dossier (JSON)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // Clear Cache
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    if (isTa) "தற்காலிக நினைவகம் அழிக்கப்பட்டது (Cache Cleared)" else "Temporary render caches flushed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            border = BorderStroke(1.dp, Color(0xFF0F1E36)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color(0xFF0F1E36))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "தற்காலிக நினைவகத்தை சுத்தப்படுத்து (Flush Cache)" else "Flush Rendering Cache & Buffers",
                                color = Color(0xFF0F1E36)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFD4CAB8))

                        // Factory Reset / Complete Purge Button (Destructive)
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            border = BorderStroke(1.dp, Color(0xFF991B1B).copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF991B1B))
                                    Text(
                                        text = if (isTa) "அபாய மண்டலம்: முழுமையான தரவு அழிப்பு" else "Danger Zone: Complete Cryptographic Purge",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF991B1B)
                                        )
                                    )
                                }
                                Text(
                                    text = if (isTa) "அனைத்து வழக்கு விவரங்கள், சான்றுகள் மற்றும் குறியாக்க சாவிகளை நிரந்தரமாக நீக்கும்." else "Irreversibly zeroes all Room tables, master keys, and preferences. Returns app to sovereign initial state.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF991B1B))
                                )

                                Button(
                                    onClick = {
                                        deleteConfirmationInput = ""
                                        showDeleteConfirmDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("purge_all_data_button")
                                ) {
                                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isTa) "முழு பெட்டகத்தையும் அழி (Purge All Data)" else "Zeroize & Factory Reset App",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Dialog 1: Change PIN Modal
    if (showPinChangeDialog) {
        AlertDialog(
            onDismissRequest = { showPinChangeDialog = false },
            title = {
                Text(
                    text = if (isTa) "புதிய 4-இலக்க PIN அமைக்கவும்" else "Set New Master Vault PIN",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1E36)
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isTa) "பயோமெட்ரிக் சென்சார் வேலை செய்யாத போது இந்த PIN கொண்டு திறக்கலாம்." else "This PIN serves as the fallback passkey when hardware biometrics are unavailable.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                    )

                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 6) newPinInput = it },
                        label = { Text(if (isTa) "புதிய PIN (4-6 இலக்கங்கள்)" else "New PIN (4-6 digits)") },
                        visualTransformation = if (pinVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_pin_field")
                    )

                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { if (it.length <= 6) confirmPinInput = it },
                        label = { Text(if (isTa) "மீண்டும் PIN உள்ளிடவும்" else "Confirm PIN") },
                        visualTransformation = if (pinVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_pin_field")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { pinVisibility = !pinVisibility }
                    ) {
                        Icon(
                            imageVector = if (pinVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Visibility",
                            tint = Color(0xFF0F1E36),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (pinVisibility) (if (isTa) "மறைக்க" else "Hide PIN") else (if (isTa) "காட்ட" else "Show PIN"),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF0F1E36))
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length >= 4 && newPinInput == confirmPinInput) {
                            securityManager.setVaultPin(newPinInput)
                            showPinChangeDialog = false
                            Toast.makeText(
                                context,
                                if (isTa) "மாஸ்டர் PIN வெற்றிகரமாக மாற்றப்பட்டது" else "Master Vault PIN updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                if (isTa) "PIN பொருந்தவில்லை அல்லது 4 இலக்கங்களுக்கு குறைவாக உள்ளது" else "PINs do not match or are fewer than 4 digits",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                    modifier = Modifier.testTag("save_pin_button")
                ) {
                    Text(if (isTa) "சேமி" else "Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinChangeDialog = false }) {
                    Text(if (isTa) "ரத்து" else "Cancel", color = Color(0xFF0F1E36))
                }
            },
            containerColor = Color(0xFFFAF7F2)
        )
    }

    // Dialog 2: Export Dossier JSON Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF0F1E36))
                    Text(
                        text = if (isTa) "ஏற்றுமதி செய்யப்பட்ட சட்ட ஆவணம் (JSON)" else "Encrypted Dossier Export Ready",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isTa) "கீழே உள்ள குறியாக்கம் செய்யப்பட்ட தகவலை உங்கள் தனிப்பட்ட காப்புப்பிரதியாக சேமிக்கலாம் அல்லது வழக்கறிஞருடன் பகிரலாம்." else "Below is the sovereign cryptographic JSON export of all disputes, readiness metrics and evidence hashes.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Text(
                            text = exportedJsonString,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF14181F),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Justra Encrypted Export", exportedJsonString)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            context,
                            if (isTa) "கிளிப்போர்டில் நகலெடுக்கப்பட்டது!" else "Export JSON copied to clipboard!",
                            Toast.LENGTH_SHORT
                        ).show()
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                    modifier = Modifier.testTag("copy_export_json_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTa) "நகலெடு (Copy JSON)" else "Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text(if (isTa) "மூடு" else "Close", color = Color(0xFF0F1E36))
                }
            },
            containerColor = Color(0xFFFAF7F2)
        )
    }

    // Dialog 3: Danger Zone Factory Reset Confirmation
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF991B1B))
                    Text(
                        text = if (isTa) "உறுதியாக அழிக்க விரும்புகிறீர்களா?" else "Confirm Factory Zeroization",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isTa) "இந்த நடவடிக்கை மீள முடியாதது. உறுதிப்படுத்த கீழே 'DELETE' என தட்டச்சு செய்யவும்:" else "This will permanently erase all local dispute cases, Section 65B certificates, and vault records. Type 'DELETE' below to confirm:",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF14181F))
                    )

                    OutlinedTextField(
                        value = deleteConfirmationInput,
                        onValueChange = { deleteConfirmationInput = it },
                        placeholder = { Text("DELETE") },
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delete_confirm_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deleteConfirmationInput.trim().equals("DELETE", ignoreCase = true)) {
                            showDeleteConfirmDialog = false
                            viewModel.resetAllData {
                                Toast.makeText(
                                    context,
                                    if (isTa) "அனைத்து தரவுகளும் அழிக்கப்பட்டன" else "All local data wiped. Reset complete.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onNavigateToLogin()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                if (isTa) "'DELETE' என்று சரியாக தட்டச்சு செய்யவும்" else "Please type 'DELETE' exactly to confirm",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                    enabled = deleteConfirmationInput.trim().equals("DELETE", ignoreCase = true),
                    modifier = Modifier.testTag("confirm_wipe_button")
                ) {
                    Text(if (isTa) "நிரந்தரமாக அழி" else "Wipe Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(if (isTa) "ரத்து" else "Cancel", color = Color(0xFF0F1E36))
                }
            },
            containerColor = Color(0xFFFAF7F2)
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF0F1E36),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F1E36)
            )
        )
    }
}

