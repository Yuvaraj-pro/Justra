package com.justra.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import com.justra.app.ui.viewmodel.NyayaMateViewModel
import com.justra.app.util.BiometricAuthHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    // Security & App Lock states
    val isAppLockEnabled by viewModel.isBiometricLockEnabled.collectAsState()
    val hasVaultPin by viewModel.hasVaultPin.collectAsState()

    val isDeviceBiometricOnly = remember(context) { BiometricAuthHelper.canAuthenticateBiometricOnly(context) }
    val isNativeDeviceLockAvailable = remember(context) { BiometricAuthHelper.isDeviceBiometricOrLockAvailable(context) }

    // Display Change PIN option if in-app PIN is active or biometrics are not strictly native device biometrics only
    val showChangePinOption = !isDeviceBiometricOnly || hasVaultPin

    var showChangePinDialog by remember { mutableStateOf(false) }
    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmNewPinInput by remember { mutableStateOf("") }
    var pinDialogError by remember { mutableStateOf<String?>(null) }

    // Notification Preferences
    var caseUpdatesEnabled by remember { mutableStateOf(true) }
    var legalRemindersEnabled by remember { mutableStateOf(true) }

    // Theme state (0: System Default, 1: Light Mode, 2: Dark Mode)
    val selectedThemeMode by viewModel.themeMode.collectAsState()

    // Dialog States
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val cardBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTa) "அமைப்புகள்" else "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
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
            // 1. Security & Privacy Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = cardBorder,
                    modifier = Modifier.fillMaxWidth().testTag("settings_security_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "பாதுகாப்பு & தனியுரிமை" else "Security & Privacy",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }

                        // App Lock Toggle (Biometrics / PIN)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "செயலி பூட்டு (பயோமெட்ரிக்ஸ் / PIN)" else "Enable App Lock (Biometrics / PIN)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = if (isTa) "செயலியை திறக்க கைரேகை அல்லது 4-இலக்க PIN ஐ கோரவும்" else "Require Biometric or PIN authentication upon app launch and 5-min timeout",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Switch(
                                checked = isAppLockEnabled,
                                onCheckedChange = { viewModel.setBiometricLockEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("settings_app_lock_switch")
                            )
                        }

                        // Change PIN Option (Only displayed if in-app PIN logic is active)
                        if (showChangePinOption) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            TextButton(
                                onClick = {
                                    currentPinInput = ""
                                    newPinInput = ""
                                    confirmNewPinInput = ""
                                    pinDialogError = null
                                    showChangePinDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_change_pin_button")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Pin,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (hasVaultPin) {
                                                if (isTa) "செயலி PIN மாற்று" else "Change App PIN"
                                            } else {
                                                if (isTa) "செயலி PIN உருவாக்கு" else "Setup App PIN"
                                            },
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Notification Preferences Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = cardBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "அறிவிப்பு முன்னுரிமைகள்" else "Notification Preferences",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }

                        // Case Progress Updates Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "வழக்கு நேரலை முன்னேற்றங்கள்" else "Case Progress Updates",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = if (isTa) "நீதிமன்ற நிலை மற்றும் மனு நிலை மாற்ற அறிவிப்புகள்" else "Receive notifications when case hearing dates or status change",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Switch(
                                checked = caseUpdatesEnabled,
                                onCheckedChange = { caseUpdatesEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Legal Reminders Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "சட்ட காலக்கெடு நினைவூட்டல்கள்" else "Legal Reminders & Limitation Alerts",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = if (isTa) "காலக்கெடு சட்டம் சார்ந்த அவசர எச்சரிக்கைகள்" else "Statutory limitation period countdown and notice response deadlines",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Switch(
                                checked = legalRemindersEnabled,
                                onCheckedChange = { legalRemindersEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            // 3. Appearance & Theme Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = cardBorder,
                    modifier = Modifier.fillMaxWidth().testTag("settings_theme_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "தோற்றம் & தீம் (Appearance & Theme)" else "Appearance & Theme",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }

                        val themeOptions = listOf(
                            0 to if (isTa) "கணினி அமைப்பு (System Default)" else "System Default",
                            1 to if (isTa) "ஒளி தீம் (Light Mode)" else "Light Mode",
                            2 to if (isTa) "இருண்ட தீம் (Dark Mode)" else "Dark Mode"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            themeOptions.forEach { (mode, label) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = selectedThemeMode == mode,
                                        onClick = { viewModel.setThemeMode(mode) },
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.testTag("settings_theme_radio_$mode")
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (selectedThemeMode == mode) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Legal & Terms Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = cardBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "சட்டப்பூர்வ கொள்கைகள்" else "Legal & Compliance",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }

                        // Terms of Service Button
                        TextButton(
                            onClick = { showTermsDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_terms_button")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isTa) "சட்ட மறுப்பு & சேவை விதிமுறைகள்" else "Legal Disclaimer & Terms of Service",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Privacy Policy Button
                        TextButton(
                            onClick = { showPrivacyDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_privacy_button")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isTa) "தனியுரிமைக் கொள்கை" else "Privacy Policy",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 5. App Version & Build Information Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = cardBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Justra v2.1.3",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = if (isTa) "பாரதிய நியாய சன்ஹிதா (BNS 2023) ஆதரவு இயந்திரம்" else "Sovereign AI Legal Engine • Build 2026.10",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }
    }

    // Change / Setup App PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = {
                Text(
                    text = if (hasVaultPin) (if (isTa) "செயலி PIN மாற்று" else "Change App PIN") else (if (isTa) "செயலி PIN அமை" else "Setup App PIN"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (hasVaultPin) {
                        OutlinedTextField(
                            value = currentPinInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    currentPinInput = it
                                    pinDialogError = null
                                }
                            },
                            label = { Text(if (isTa) "தற்போதைய PIN" else "Current PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("settings_current_pin_input")
                        )
                    }

                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                newPinInput = it
                                pinDialogError = null
                            }
                        },
                        label = { Text(if (isTa) "புதிய 4-இலக்க PIN" else "New 4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("settings_new_pin_input")
                    )

                    OutlinedTextField(
                        value = confirmNewPinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                confirmNewPinInput = it
                                pinDialogError = null
                            }
                        },
                        label = { Text(if (isTa) "புதிய PIN ஐ உறுதிசெய்க" else "Confirm New PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("settings_confirm_new_pin_input")
                    )

                    if (pinDialogError != null) {
                        Text(
                            text = pinDialogError!!,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (hasVaultPin && !viewModel.verifyPinForReauth(currentPinInput)) {
                            pinDialogError = if (isTa) "தற்போதைய PIN தவறானது" else "Current PIN is incorrect"
                        } else if (newPinInput.length != 4) {
                            pinDialogError = if (isTa) "புதிய PIN 4 இலக்கமாக இருக்க வேண்டும்" else "New PIN must be 4 digits"
                        } else if (newPinInput != confirmNewPinInput) {
                            pinDialogError = if (isTa) "புதிய PIN எண்கள் பொருந்தவில்லை" else "New PINs do not match"
                        } else {
                            viewModel.setVaultPin(newPinInput)
                            showChangePinDialog = false
                            Toast.makeText(context, if (isTa) "PIN வெற்றிகரமாக மாற்றப்பட்டது" else "App PIN updated successfully", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(if (isTa) "சேமி" else "Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text(if (isTa) "ரத்து" else "Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Legal Disclaimer Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(if (isTa) "புரிந்தது" else "I Understand")
                }
            },
            title = {
                Text(
                    text = if (isTa) "சட்ட மறுப்பு & சேவை விதிமுறைகள்" else "Legal Disclaimer & Terms of Service",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
            },
            text = {
                Text(
                    text = if (isTa) {
                        "ஜஸ்ட்ரா (Justra) செயற்கை நுண்ணறிவு சட்ட உதவி தளம் மட்டுமே. இது சட்ட விழிப்புணர்வு மற்றும் மனு உருவாக்கத்திற்கு உதவுகிறது. இது வழக்கறிஞரின் நேரடி ஆலோசனையை மாற்ற முடியாது."
                    } else {
                        "Justra provides automated legal intake, statutory mapping, and draft generation under Indian statutes (BNS 2023, BNSS 2023, BSA 2023, CPA 2019). Information generated does not constitute formal attorney-client legal advice."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(if (isTa) "மூடு" else "Close")
                }
            },
            title = {
                Text(
                    text = if (isTa) "தனியுரிமைக் கொள்கை" else "Privacy Policy",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
            },
            text = {
                Text(
                    text = if (isTa) {
                        "உங்கள் தகவல்கள் அனைத்தும் உங்கள் சாதனத்திலேயே பாதுகாப்பாக சேமிக்கப்படும். எந்தவொரு வெளி நபர்களுக்கும் தகவல்கள் பகிரப்படாது."
                    } else {
                        "Justra prioritizes your privacy. User profiles, case records, and dispute intakes are persisted locally using secure storage. No personal identification data is sold or transmitted to unauthorized third parties."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
