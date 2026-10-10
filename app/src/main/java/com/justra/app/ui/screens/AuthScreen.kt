package com.justra.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.AccentTerracotta
import com.justra.app.ui.theme.SovereignNavy
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import com.justra.app.util.BiometricAuthHelper

@Composable
fun AuthScreen(
    currentLanguage: LanguagePreference,
    hasVaultPin: Boolean = false,
    onVerifyPin: (String) -> Boolean = { true },
    onSetVaultPin: (String) -> Unit = {},
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    var showPinDialog by remember { mutableStateOf(false) }
    var isCreatePinMode by remember { mutableStateOf(!hasVaultPin) }

    var pinInput by remember { mutableStateOf("") }
    var pinConfirmInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    fun launchBiometricOrPin() {
        val activity = context as? FragmentActivity
        val biometricsAvailable = BiometricAuthHelper.isDeviceBiometricOrLockAvailable(context)

        if (biometricsAvailable && activity != null) {
            BiometricAuthHelper.promptBiometricAuth(
                activity = activity,
                title = if (isTa) "ஜஸ்ட்ரா சட்ட பெட்டக அங்கீகாரம்" else "Justra Legal Vault Authentication",
                subtitle = if (isTa) "கைரேகை அல்லது சாதன பூட்டு மூலம் உள்ளே நுழையவும்" else "Verify identity using Biometrics or Device Screen Lock",
                onSuccess = {
                    onAuthenticated()
                },
                onError = { err ->
                    showPinDialog = true
                },
                onFallbackToPin = {
                    showPinDialog = true
                }
            )
        } else {
            showPinDialog = true
        }
    }

    LaunchedEffect(Unit) {
        launchBiometricOrPin()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        containerColor = SovereignNavy,
        modifier = modifier.testTag("auth_screen")
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(AccentTerracotta)
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Justra Logo",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (isTa) "ஜஸ்ட்ரா சட்ட தளம்" else "JUSTRA LEGAL PLATFORM",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isTa) "இந்திய குடிமக்களுக்கான சட்ட உதவி தளம்" else "Sovereign AI Legal Engine & Dispute Resolution",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f)),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { launchBiometricOrPin() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentTerracotta,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("auth_continue_button")
                ) {
                    Text(
                        text = if (isTa) "முகப்பிற்கு செல்லவும்" else "Continue to Justra",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                OutlinedButton(
                    onClick = { showPinDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("auth_pin_option_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pin,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTa) "செயலி PIN பயன்படுத்தி நுழைக" else "Unlock with In-App PIN",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }

    // In-App PIN Creation / Authentication Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SovereignNavy)
                    Text(
                        text = if (isCreatePinMode) {
                            if (isTa) "புதிய 4-இலக்க PIN உருவாக்கவும்" else "Create 4-Digit App PIN"
                        } else {
                            if (isTa) "பாதுகாப்பு PIN உள்ளிடவும்" else "Enter 4-Digit App PIN"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isCreatePinMode) {
                            if (isTa) "கைரேகை கிடைக்காதபோது செயலியை திறக்க 4-இலக்க PIN தேவை" else "Set up a 4-digit in-app security PIN as a fallback for app lock."
                        } else {
                            if (isTa) "தொடர உங்கள் 4-இலக்க PIN ஐ உள்ளிடவும்" else "Enter your 4-digit security PIN to unlock the Justra Legal Vault."
                        },
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                pinInput = it
                                pinError = null
                            }
                        },
                        label = { Text(if (isTa) "4-இலக்க PIN" else "4-Digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("auth_pin_input_field")
                    )

                    if (isCreatePinMode) {
                        OutlinedTextField(
                            value = pinConfirmInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    pinConfirmInput = it
                                    pinError = null
                                }
                            },
                            label = { Text(if (isTa) "PIN உறுதிப்படுத்தவும்" else "Confirm 4-Digit PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("auth_pin_confirm_input_field")
                        )
                    }

                    if (pinError != null) {
                        Text(
                            text = pinError!!,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isCreatePinMode) {
                            if (pinInput.length != 4) {
                                pinError = if (isTa) "சரியாக 4 இலக்கங்களை உள்ளிடவும்" else "PIN must be exactly 4 digits"
                            } else if (pinInput != pinConfirmInput) {
                                pinError = if (isTa) "PIN எண்கள் பொருந்தவில்லை" else "PINs do not match"
                            } else {
                                onSetVaultPin(pinInput)
                                showPinDialog = false
                                onAuthenticated()
                            }
                        } else {
                            if (pinInput.length != 4) {
                                pinError = if (isTa) "4 இலக்கங்களை உள்ளிடவும்" else "Enter 4 digits"
                            } else if (onVerifyPin(pinInput)) {
                                showPinDialog = false
                                onAuthenticated()
                            } else {
                                pinError = if (isTa) "தவறான PIN. மீண்டும் முயற்சிக்கவும்." else "Incorrect PIN. Please try again."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy, contentColor = Color.White)
                ) {
                    Text(if (isCreatePinMode) (if (isTa) "PIN சேமிக்க" else "Save & Unlock") else (if (isTa) "திறக்கவும்" else "Unlock"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text(if (isTa) "ரத்து" else "Cancel")
                }
            }
        )
    }
}
