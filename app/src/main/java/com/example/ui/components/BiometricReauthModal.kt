package com.example.ui.components

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.DeepIndigoSlatePrimary
import com.example.ui.theme.HennaRedAlertContainer
import com.example.ui.theme.HennaRedAlertText
import com.example.ui.theme.LegalAmberSecondary
import com.example.ui.theme.PaleSandstoneVariant
import com.example.ui.theme.PrimaryContainerSlate
import com.example.ui.theme.SageGreenSuccessContainer
import com.example.ui.theme.SageGreenSuccessText
import com.example.ui.theme.TerracottaAccentSecondary
import com.example.ui.theme.WarmIvorySurface
import com.example.util.BiometricAuthHelper
import com.example.util.LocalizationManager

private fun Context.findFragmentActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

/**
 * Biometric Re-Authentication Strategy Dialog / Modal using androidx.biometric.
 * Prompts the user with a fresh biometric challenge or PIN verification
 * whenever the app returns to the foreground after being inactive for > 5 minutes.
 */
@Composable
fun BiometricReauthModal(
    currentLanguage: LanguagePreference,
    onUnlockWithBiometric: () -> Unit,
    onVerifyPin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val biometricStatus = remember(context) { BiometricAuthHelper.checkBiometricStatus(context) }

    var showPinFallback by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var biometricErrorMessage by remember { mutableStateOf<String?>(null) }
    val isTa = currentLanguage == LanguagePreference.TAMIL

    // Auto-launch biometric prompt when re-auth modal opens if hardware is available
    LaunchedEffect(Unit) {
        if (activity != null && biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE) {
            BiometricAuthHelper.launchBiometricPrompt(
                activity = activity,
                language = currentLanguage,
                customTitle = if (isTa) "மீண்டும் அங்கீகரிக்கவும்" else "Re-authenticate Document Vault",
                customSubtitle = if (isTa) "5 நிமிட செயலற்ற தன்மைக்குப் பிறகு பாதுகாப்பு பூட்டு" else "5-minute inactivity security lock",
                onSuccess = {
                    onUnlockWithBiometric()
                },
                onError = { _, msg ->
                    biometricErrorMessage = msg
                },
                onNegativeButtonTapped = {
                    showPinFallback = true
                }
            )
        }
    }

    Dialog(
        onDismissRequest = { /* Non-dismissible without authentication */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                border = BorderStroke(2.dp, TerracottaAccentSecondary),
                modifier = modifier
                    .fillMaxWidth()
                    .testTag("biometric_reauth_modal")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Security Shield Emblem with Inactivity Timer Badge
                    Surface(
                        shape = CircleShape,
                        color = PaleSandstoneVariant,
                        border = BorderStroke(2.dp, TerracottaAccentSecondary),
                        modifier = Modifier.size(76.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Vault Locked",
                                tint = DeepIndigoSlatePrimary,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Inactivity Protection Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HennaRedAlertContainer,
                        border = BorderStroke(1.dp, HennaRedAlertText.copy(alpha = 0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = HennaRedAlertText,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isTa) "5 நிமிட செயலற்ற பாதுகாப்பு பூட்டு" else "5-Min Inactivity Auto-Lock",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HennaRedAlertText
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = LocalizationManager.getString("reauth_title", currentLanguage),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = LocalizationManager.getString("reauth_desc", currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!showPinFallback) {
                        // Primary Biometric Quick CTA
                        Button(
                            onClick = {
                                if (activity != null && biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE) {
                                    BiometricAuthHelper.launchBiometricPrompt(
                                        activity = activity,
                                        language = currentLanguage,
                                        customTitle = if (isTa) "மீண்டும் அங்கீகரிக்கவும்" else "Re-authenticate Document Vault",
                                        customSubtitle = if (isTa) "5 நிமிட செயலற்ற தன்மைக்குப் பிறகு பாதுகாப்பு பூட்டு" else "5-minute inactivity security lock",
                                        onSuccess = {
                                            onUnlockWithBiometric()
                                        },
                                        onError = { _, msg ->
                                            biometricErrorMessage = msg
                                        },
                                        onNegativeButtonTapped = {
                                            showPinFallback = true
                                        }
                                    )
                                } else {
                                    onUnlockWithBiometric()
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepIndigoSlatePrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("reauth_biometric_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Sensor",
                                tint = LegalAmberSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = LocalizationManager.getString("reauth_cta", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (biometricErrorMessage != null) {
                            Text(
                                text = biometricErrorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { showPinFallback = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepIndigoSlatePrimary),
                            border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reauth_use_pin_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "PIN Fallback",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LocalizationManager.getString("reauth_pin_cta", currentLanguage),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    } else {
                        // PIN Entry Field Fallback
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = {
                                    if (it.length <= 6) {
                                        pinInput = it
                                        pinError = false
                                    }
                                },
                                label = { Text(if (isTa) "4-6 இலக்க மாஸ்டர் PIN" else "4-6 Digit Master PIN") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                isError = pinError,
                                supportingText = {
                                    if (pinError) {
                                        Text(
                                            text = if (isTa) "தவறான PIN. மீண்டும் முயற்சிக்கவும்." else "Incorrect PIN. Please retry.",
                                            color = HennaRedAlertText
                                        )
                                    } else {
                                        Text(
                                            text = if (isTa) "முன்னிருப்பு PIN: 1234 (அல்லது உங்கள் சொந்த PIN)" else "Default PIN: 1234 (or your configured PIN)"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reauth_pin_textfield")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        showPinFallback = false
                                        pinInput = ""
                                        pinError = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Text(if (isTa) "பயோமெட்ரிக்" else "Biometrics")
                                }

                                Button(
                                    onClick = {
                                        val success = onVerifyPin(pinInput)
                                        if (!success) {
                                            pinError = true
                                        }
                                    },
                                    enabled = pinInput.length >= 4,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                                    modifier = Modifier.weight(1.2f).height(48.dp).testTag("reauth_pin_submit_button")
                                ) {
                                    Text(if (isTa) "திறக்கவும்" else "Unlock")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hardware Enclave Verification Notice
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SageGreenSuccessText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isTa) "Hardware Keystore / TEE குறியாக்கம் செயலில் உள்ளது" else "Protected by Hardware Keystore / androidx.biometric",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = SageGreenSuccessText,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
