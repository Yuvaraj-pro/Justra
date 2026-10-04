package com.justra.app.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.util.BiometricAuthHelper
import com.justra.app.util.SecurityPreferences
import kotlinx.coroutines.delay

private fun Context.findFragmentActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun AuthScreen(
    currentLanguage: LanguagePreference,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val secPrefs = remember(context) { SecurityPreferences.getInstance(context) }
    val isTa = currentLanguage == LanguagePreference.TAMIL

    var pinState by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lockoutSeconds by remember { mutableStateOf(0) }
    var isBiometricAvailable by remember {
        mutableStateOf(BiometricAuthHelper.checkBiometricStatus(context) == BiometricAuthHelper.BiometricCapability.AVAILABLE)
    }

    val expectedPinLength = if (secPrefs.hasEnrolledPin()) secPrefs.getEnrolledPinLength() else 6

    // Lockout countdown timer loop
    LaunchedEffect(Unit) {
        while (true) {
            val expiry = secPrefs.getLockoutExpiryTimestamp()
            val now = System.currentTimeMillis()
            if (expiry > now) {
                val remaining = (((expiry - now) / 1000) + 1).toInt()
                lockoutSeconds = remaining
                errorMessage = if (isTa) 
                    "பல முறை தவறான PIN. தயவுசெய்து $remaining விநாடிகள் காத்திருக்கவும்." 
                else 
                    "Too many incorrect attempts. Please wait $remaining seconds."
            } else {
                if (lockoutSeconds > 0) {
                    lockoutSeconds = 0
                    errorMessage = null
                    secPrefs.resetFailedAttempts()
                }
            }
            delay(500L)
        }
    }

    val triggerBiometric = {
        if (activity != null && isBiometricAvailable && lockoutSeconds == 0) {
            BiometricAuthHelper.launchBiometricPrompt(
                activity = activity,
                language = currentLanguage,
                onSuccess = {
                    secPrefs.resetFailedAttempts()
                    onAuthenticated()
                },
                onError = { _, err -> errorMessage = err },
                onNegativeButtonTapped = { }
            )
        }
    }

    LaunchedEffect(Unit) {
        if (isBiometricAvailable && lockoutSeconds == 0) {
            triggerBiometric()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars,
        containerColor = Color(0xFF0F1E36)
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
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5A93C).copy(alpha = 0.15f))
                        .border(2.dp, Color(0xFFE5A93C), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFE5A93C),
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isTa) "ஜஸ்ட்ரா தனிப்பட்ட பெட்டகம்" else "JUSTRA SECURE VAULT",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFAF7F2),
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = if (isTa) "கைரேகை அல்லது பாதுகாப்பு PIN மூலம் திறக்கவும்" else "Authenticate with Biometrics or Security PIN",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFAF7F2).copy(alpha = 0.7f)),
                    textAlign = TextAlign.Center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    repeat(expectedPinLength) { index ->
                        val isFilled = index < pinState.length
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) Color(0xFFE5A93C) else Color.White.copy(alpha = 0.2f)
                                )
                                .border(
                                    1.dp,
                                    if (isFilled) Color(0xFFE5A93C) else Color.White.copy(alpha = 0.4f),
                                    CircleShape
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (lockoutSeconds > 0) Color(0xFFFFA07A) else Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val digits = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BIO", "0", "DEL")
                )

                digits.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        row.forEach { key ->
                            val isKeyEnabled = lockoutSeconds == 0
                            Surface(
                                onClick = {
                                    if (lockoutSeconds > 0) return@Surface
                                    errorMessage = null
                                    when (key) {
                                        "DEL" -> if (pinState.isNotEmpty()) pinState = pinState.dropLast(1)
                                        "BIO" -> triggerBiometric()
                                        else -> {
                                            if (pinState.length < expectedPinLength) {
                                                val nextPin = pinState + key
                                                pinState = nextPin
                                                if (nextPin.length == expectedPinLength) {
                                                    if (secPrefs.hasEnrolledPin()) {
                                                        if (secPrefs.verifyPin(nextPin)) {
                                                            secPrefs.resetFailedAttempts()
                                                            onAuthenticated()
                                                        } else {
                                                            val failedCount = secPrefs.incrementFailedAttempts()
                                                            pinState = ""
                                                            if (failedCount >= 3) {
                                                                secPrefs.setLockoutExpiryTimestamp(System.currentTimeMillis() + 30000L)
                                                                lockoutSeconds = 30
                                                                errorMessage = if (isTa) 
                                                                    "பல முறை தவறான PIN. தயவுசெய்து 30 விநாடிகள் காத்திருக்கவும்." 
                                                                else 
                                                                    "Too many incorrect attempts. Please wait 30 seconds."
                                                            } else {
                                                                val remainingAttempts = 3 - failedCount
                                                                errorMessage = if (isTa) 
                                                                    "தவறான PIN. மீண்டும் முயற்சிக்கவும் ($remainingAttempts வாய்ப்புகள் பாக்கி)." 
                                                                else 
                                                                    "Incorrect PIN. Try again ($remainingAttempts attempts left)."
                                                            }
                                                        }
                                                    } else {
                                                        secPrefs.savePin(nextPin)
                                                        secPrefs.resetFailedAttempts()
                                                        Toast.makeText(
                                                            context, 
                                                            if (isTa) "PIN வெற்றிகரமாக பதிவு செய்யப்பட்டது" else "PIN Enrolled Successfully", 
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        onAuthenticated()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                enabled = isKeyEnabled,
                                shape = CircleShape,
                                color = if (isKeyEnabled) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.04f),
                                contentColor = if (isKeyEnabled) Color(0xFFFAF7F2) else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    when (key) {
                                        "BIO" -> Icon(
                                            Icons.Default.Fingerprint, 
                                            contentDescription = "Biometric", 
                                            tint = if (isKeyEnabled) Color(0xFFE5A93C) else Color(0xFFE5A93C).copy(alpha = 0.3f)
                                        )
                                        "DEL" -> Icon(
                                            Icons.AutoMirrored.Filled.Backspace, 
                                            contentDescription = "Delete", 
                                            tint = if (isKeyEnabled) Color(0xFFFAF7F2) else Color(0xFFFAF7F2).copy(alpha = 0.3f)
                                        )
                                        else -> Text(
                                            text = key, 
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isKeyEnabled) Color(0xFFFAF7F2) else Color.White.copy(alpha = 0.3f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
