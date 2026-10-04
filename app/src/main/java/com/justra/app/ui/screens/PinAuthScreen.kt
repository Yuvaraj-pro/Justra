package com.justra.app.ui.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.justra.app.ui.components.JustraBrandLogo
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.justra.app.ui.theme.AlertCrimson
import com.justra.app.ui.theme.CardBorderStroke
import com.justra.app.ui.theme.SandstoneCard
import com.justra.app.ui.theme.SovereignNavy
import com.justra.app.ui.theme.TextPrimaryDark
import com.justra.app.ui.theme.TextSecondaryDark
import com.justra.app.ui.theme.VerifiedSageGreen
import com.justra.app.ui.theme.WarmCanvasBg
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import com.justra.app.util.BilingualStrings
import com.justra.app.util.BiometricAuthHelper
import com.justra.app.util.SecurityPreferences

private fun Context.findFragmentActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

/**
 * Robust PIN Authentication & Enrollment Gate.
 * Handles both first-time PIN enrollment (create & confirm 4-6 digits)
 * and returning user unlock with biometric prompt fallback.
 */
@Composable
fun PinAuthScreen(
    currentLanguage: LanguagePreference,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val secPrefs = remember(context) { SecurityPreferences.getInstance(context) }

    val hasEnrolledPin = remember { secPrefs.hasEnrolledPin() }
    val isTa = currentLanguage == LanguagePreference.TAMIL

    // State for Enrollment flow
    var isConfirmingPinStep by remember { mutableStateOf(false) }
    var initialPinState by remember { mutableStateOf("") }
    var pinInputState by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var biometricStatus by remember { mutableStateOf(BiometricAuthHelper.checkBiometricStatus(context)) }

    // Auto-prompt Biometric for returning users if available
    LaunchedEffect(hasEnrolledPin) {
        if (hasEnrolledPin && activity != null && biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE) {
            BiometricAuthHelper.launchBiometricPrompt(
                activity = activity,
                language = currentLanguage,
                onSuccess = { onAuthenticated() },
                onError = { _, _ -> /* Stay on PIN screen on cancel/fail */ },
                onNegativeButtonTapped = { /* Stay on PIN screen */ }
            )
        }
    }

    Scaffold(
        containerColor = WarmCanvasBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Security Shield Icon with Justra Brand Logo
            JustraBrandLogo(
                size = 72.dp,
                showWordmark = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title & Instructions
            val titleText = when {
                !hasEnrolledPin && !isConfirmingPinStep -> if (isTa) "பாதுகாப்பு PIN உருவாக்கம்" else "Create Security PIN"
                !hasEnrolledPin && isConfirmingPinStep -> if (isTa) "PIN-ஐ உறுதிப்படுத்தவும்" else "Confirm Security PIN"
                else -> if (isTa) "பெட்டகத்தை திறக்கவும்" else "Unlock Justra Vault"
            }

            val descText = when {
                !hasEnrolledPin && !isConfirmingPinStep -> if (isTa) "பயன்பாட்டைப் பாதுகாக்க 4 முதல் 6 இலக்க PIN உள்ளிடவும்" else "Set a 4 to 6 digit numeric PIN to protect your app"
                !hasEnrolledPin && isConfirmingPinStep -> if (isTa) "உறுதிப்படுத்த மீண்டும் 4-6 இலக்க PIN-ஐ உள்ளிடவும்" else "Re-enter your 4-6 digit PIN to confirm enrollment"
                else -> if (isTa) "தொடர உங்கள் 4-6 இலக்க PIN-ஐ உள்ளிடவும்" else "Enter your established 4 to 6 digit PIN to unlock"
            }

            Text(
                text = titleText,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = SovereignNavy,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = descText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            // Security Status Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SovereignNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (!hasEnrolledPin) {
                            if (isTa) "முதல் முறை அமைப்பு: AES-256 குறியாக்கம்" else "First-Time Setup: AES-256 Encrypted Storage"
                        } else {
                            if (isTa) "பாதுகாப்பான PIN சான்றளிப்பு" else "Encrypted PIN Protection Active"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
                        )
                    )
                }
            }

            // PIN Outlined Input Field
            OutlinedTextField(
                value = pinInputState,
                onValueChange = { newValue ->
                    if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                        pinInputState = newValue
                        errorMessage = null
                    }
                },
                label = {
                    Text(
                        text = if (isTa) "PIN (4-6 இலக்கங்கள்)" else "Enter PIN (4-6 digits)",
                        color = TextSecondaryDark
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = nyayaOutlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pin_auth_input_field")
            )

            // Error Display Banner
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AlertCrimson,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Submit Button
            Button(
                onClick = {
                    val input = pinInputState.trim()
                    if (!secPrefs.isValidPinFormat(input)) {
                        errorMessage = if (isTa) "PIN 4 முதல் 6 இலக்கங்கள் மட்டுமே இருக்க வேண்டும்" else "PIN length must be strictly between 4 and 6 digits"
                        return@Button
                    }

                    if (!hasEnrolledPin) {
                        // First Time Enrollment Workflow
                        if (!isConfirmingPinStep) {
                            initialPinState = input
                            pinInputState = ""
                            isConfirmingPinStep = true
                            errorMessage = null
                        } else {
                            if (input == initialPinState) {
                                val saved = secPrefs.savePin(input)
                                if (saved) {
                                    onAuthenticated()
                                } else {
                                    errorMessage = if (isTa) "PIN சேமிப்பதில் பிழை" else "Failed to save encrypted PIN"
                                }
                            } else {
                                errorMessage = if (isTa) "PIN அமையவில்லை! மீண்டும் முயற்சிக்குக" else "PINs do not match. Please try again."
                                isConfirmingPinStep = false
                                initialPinState = ""
                                pinInputState = ""
                            }
                        }
                    } else {
                        // Returning User Unlock Workflow
                        val isValid = secPrefs.verifyPin(input)
                        if (isValid) {
                            onAuthenticated()
                        } else {
                            errorMessage = if (isTa) "தவறான PIN! மீண்டும் முயற்சிக்கவும்." else "Incorrect PIN. Access denied."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SovereignNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("pin_auth_submit_button")
            ) {
                Text(
                    text = when {
                        !hasEnrolledPin && !isConfirmingPinStep -> if (isTa) "அடுத்தது (Confirm PIN)" else "Next: Confirm PIN"
                        !hasEnrolledPin && isConfirmingPinStep -> if (isTa) "PIN சேமித்து திறக்க" else "Save & Unlock App"
                        else -> if (isTa) "திறக்கவும் (Unlock)" else "Unlock Vault"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Biometric Fallback Trigger (if returning user and hardware supports)
            if (hasEnrolledPin && biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = {
                        if (activity != null) {
                            BiometricAuthHelper.launchBiometricPrompt(
                                activity = activity,
                                language = currentLanguage,
                                onSuccess = { onAuthenticated() },
                                onError = { _, msg -> errorMessage = msg },
                                onNegativeButtonTapped = { /* Stay on PIN screen */ }
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("biometric_fallback_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = SovereignNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isTa) "கைரேகை / Biometric முறை" else "Use Biometric Auth",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = SovereignNavy,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
