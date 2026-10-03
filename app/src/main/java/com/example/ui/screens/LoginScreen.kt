package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.domain.model.UserRole
import com.example.ui.components.NyayaTopBar
import com.example.ui.components.RoleSelectionCard
import com.example.ui.theme.DeepIndigoSlatePrimary
import com.example.ui.theme.PaleSandstoneVariant
import com.example.ui.theme.PrimaryContainerSlate
import com.example.ui.theme.SageGreenSuccessContainer
import com.example.ui.theme.SageGreenSuccessText
import com.example.ui.theme.TerracottaAccentSecondary
import com.example.ui.theme.WarmIvorySurface
import com.example.ui.theme.nyayaOutlinedTextFieldColors
import com.example.util.BilingualStrings

@Composable
fun LoginScreen(
    currentLanguage: LanguagePreference,
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    vaultHashSnippet: String,
    onAuthenticated: () -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onVerifyPin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var showPinEntry by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = BilingualStrings.t("login_title", currentLanguage),
                subtitle = BilingualStrings.t("login_subtitle", currentLanguage),
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick,
                modifier = Modifier.testTag("login_top_bar")
            )
        },
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("login_screen_column"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Vault Keystore Security Header
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryContainerSlate,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = DeepIndigoSlatePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTa) "ஆவணப் பெட்டக குறியாக்கம்" else "Hardware Sealed Keystore",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            Text(
                                text = "SHA-256: ${vaultHashSnippet.take(16)}...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SageGreenSuccessContainer
                        ) {
                            Text(
                                text = "LOCKED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreenSuccessText,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Select Role / User Profile
            item {
                Text(
                    text = BilingualStrings.t("select_role", currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(UserRole.values().size) { index ->
                val role = UserRole.values()[index]
                RoleSelectionCard(
                    role = role,
                    isSelected = selectedRole == role,
                    currentLanguage = currentLanguage,
                    onSelect = { onRoleSelected(role) }
                )
            }

            // Authentication Action Trigger Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = PaleSandstoneVariant)
                Spacer(modifier = Modifier.height(8.dp))

                if (!showPinEntry) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onAuthenticated,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepIndigoSlatePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("login_biometric_button")
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
                                    text = BilingualStrings.t("biometric_unlock", currentLanguage) + " (${if (isTa) selectedRole.badgeTa else selectedRole.badgeEn})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showPinEntry = true },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_use_pin_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pin,
                                    contentDescription = null,
                                    tint = DeepIndigoSlatePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = BilingualStrings.t("use_pin", currentLanguage),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = DeepIndigoSlatePrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                if (it.length <= 6) {
                                    pinInput = it
                                    pinError = null
                                }
                            },
                            label = { Text(if (isTa) "பாதுகாப்பு PIN (4-6 இலக்கம்)" else "Security PIN (4-6 digits)") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_pin_input")
                        )

                        if (pinError != null) {
                            Text(
                                text = pinError ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                            )
                        }

                        Button(
                            onClick = {
                                if (pinInput.length >= 4 && onVerifyPin(pinInput)) {
                                    onAuthenticated()
                                } else {
                                    pinError = if (isTa) "தவறான PIN. மீண்டும் முயற்சிக்கவும்." else "Invalid PIN passcode. Please try again."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_pin_submit_button")
                        ) {
                            Text(
                                text = if (isTa) "பெட்டகத்தை திறக்க (${selectedRole.badgeTa})" else "Unlock Vault (${selectedRole.badgeEn})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        OutlinedButton(
                            onClick = { showPinEntry = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isTa) "கைரேகை முறைக்கு மாற்றுக" else "Back to Biometrics")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
