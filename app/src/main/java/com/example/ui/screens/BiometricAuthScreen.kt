package com.example.ui.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.AccentTerracotta
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.SandstoneCard
import com.example.ui.theme.SovereignNavy
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VerifiedSageGreen
import com.example.ui.theme.WarmCanvasBg
import com.example.util.BilingualStrings
import com.example.util.BiometricAuthHelper
import com.example.util.SecurityPreferences

private fun Context.findFragmentActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun BiometricAuthScreen(
    currentLanguage: LanguagePreference,
    vaultHashSnippet: String,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val secPrefs = remember(context) { SecurityPreferences.getInstance(context) }

    var showPinFallback by remember { mutableStateOf(!secPrefs.hasEnrolledPin()) }
    var authError by remember { mutableStateOf<String?>(null) }
    var biometricStatus by remember { mutableStateOf(BiometricAuthHelper.checkBiometricStatus(context)) }

    // Auto-prompt biometric authentication if PIN is enrolled and biometrics are available
    LaunchedEffect(Unit) {
        if (secPrefs.hasEnrolledPin() && activity != null && biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE) {
            BiometricAuthHelper.launchBiometricPrompt(
                activity = activity,
                language = currentLanguage,
                onSuccess = { onAuthenticated() },
                onError = { _, msg -> authError = msg },
                onNegativeButtonTapped = { showPinFallback = true }
            )
        }
    }

    if (showPinFallback || !secPrefs.hasEnrolledPin()) {
        PinAuthScreen(
            currentLanguage = currentLanguage,
            onAuthenticated = onAuthenticated,
            modifier = modifier
        )
        return
    }

    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        containerColor = WarmCanvasBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Vault Security Shield Icon
            Surface(
                shape = CircleShape,
                color = SandstoneCard,
                border = BorderStroke(2.dp, AccentTerracotta),
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Vault Security Lock",
                        tint = SovereignNavy,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = BilingualStrings.t("unlock_title", currentLanguage),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = SovereignNavy,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = BilingualStrings.t("unlock_desc", currentLanguage),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            // Vault Hardware Keystore Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = SovereignNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = if (isTa) "ஹார்டுவேர் குறியாக்க பெட்டகம் (AES-256 GCM)" else "Hardware Master Key (AES-256 Encrypted)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SovereignNavy
                            )
                        )
                        Text(
                            text = "SHA-256: ${vaultHashSnippet.take(16)}...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondaryDark
                            )
                        )
                    }
                }
            }

            // Biometric Hardware Status Indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE)
                    VerifiedSageGreen else SandstoneCard,
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE)
                            Icons.Default.VerifiedUser else Icons.Default.Info,
                        contentDescription = null,
                        tint = SovereignNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = BiometricAuthHelper.getStatusDescription(biometricStatus, currentLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SovereignNavy,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Primary Biometric Trigger Button
            Button(
                onClick = {
                    authError = null
                    if (activity != null && biometricStatus == BiometricAuthHelper.BiometricCapability.AVAILABLE) {
                        BiometricAuthHelper.launchBiometricPrompt(
                            activity = activity,
                            language = currentLanguage,
                            onSuccess = { onAuthenticated() },
                            onError = { _, msg -> authError = msg },
                            onNegativeButtonTapped = { showPinFallback = true }
                        )
                    } else {
                        showPinFallback = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SovereignNavy,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("biometric_authenticate_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = BilingualStrings.t("biometric_unlock", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (authError != null) {
                Text(
                    text = authError ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PIN / Passcode fallback switch
            OutlinedButton(
                onClick = { showPinFallback = true },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("use_device_pin_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pin,
                        contentDescription = null,
                        tint = SovereignNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = BilingualStrings.t("use_pin", currentLanguage),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = SovereignNavy,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
