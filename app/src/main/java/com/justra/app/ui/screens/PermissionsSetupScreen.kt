package com.justra.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface

data class PermissionItemData(
    val titleEn: String,
    val titleTa: String,
    val descriptionEn: String,
    val descriptionTa: String,
    val icon: ImageVector,
    val isGranted: Boolean
)

/**
 * Dedicated Onboarding Android Permissions Screen.
 * Prompts the user for Microphone, Location, and Notification permissions
 * with clear rationale for statutory compliance.
 */
@Composable
fun PermissionsSetupScreen(
    currentLanguage: LanguagePreference,
    onPermissionsFinished: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTamil = currentLanguage == LanguagePreference.TAMIL

    // Permission state checkers
    fun isMicGranted(): Boolean = ContextCompat.checkSelfPermission(
        context, Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    fun isLocationGranted(): Boolean = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    fun isNotificationGranted(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true

    var micStatus by remember { mutableStateOf(isMicGranted()) }
    var locationStatus by remember { mutableStateOf(isLocationGranted()) }
    var notifStatus by remember { mutableStateOf(isNotificationGranted()) }

    fun refreshPermissionStatuses() {
        micStatus = isMicGranted()
        locationStatus = isLocationGranted()
        notifStatus = isNotificationGranted()
    }

    val permissionsToRequest = remember {
        mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        refreshPermissionStatuses()
        onPermissionsFinished()
    }

    val allGranted = micStatus && locationStatus && notifStatus

    Scaffold(
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Header Finial Emblem
            Surface(
                shape = CircleShape,
                color = PaleSandstoneVariant,
                border = BorderStroke(2.dp, TerracottaAccentSecondary),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = DeepIndigoSlatePrimary,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isTamil) "அனுமதிகள் அமைப்பு" else "Android App Permissions",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigoSlatePrimary,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = if (isTamil)
                    "ஜஸ்ட்ரா சட்ட உதவிகள், நேரலை குரல் பதிவு மற்றும் எல்லை கண்டறிதல் சரியாக செயல்பட கீழ்க்காணும் அனுமதிகள் தேவை."
                else
                    "Justra requires access permissions for live voice consultation, regional jurisdiction mapping, and statutory deadline alerts.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF4A4E57),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            // Permission Cards
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Card 1: Microphone
                PermissionCard(
                    title = if (isTamil) "மைக்ரோஃபோன் அனுமதி (Voice STT)" else "Microphone Access",
                    description = if (isTamil) "நேரலை குரல் பதிவு மற்றும் சட்ட உரையாடல்களுக்கு தேவை." else "Required for live voice intake and Tamil/English speech-to-text legal consultation.",
                    icon = Icons.Default.Mic,
                    isGranted = micStatus
                )

                // Card 2: Location
                PermissionCard(
                    title = if (isTamil) "இருப்பிடம் & சட்ட எல்லை (Location)" else "Location & Jurisdiction",
                    description = if (isTamil) "மாநில வாடகை சட்டங்கள் மற்றும் உயர் நீதிமன்ற எல்லையைக் கண்டறிய தேவை." else "Used for auto-detecting regional statutory codes (BNS, Model Tenancy Act, Rent Control).",
                    icon = Icons.Default.LocationOn,
                    isGranted = locationStatus
                )

                // Card 3: Notifications
                PermissionCard(
                    title = if (isTamil) "அறிவிப்புகள் (Deadline Alerts)" else "Notifications & Emergency Alerts",
                    description = if (isTamil) "வழக்கு காலக்கெடு மற்றும் சைபர் கிரைம் உதவி அறிவிப்புகளுக்கு தேவை." else "Notifies you before statutory limitation expiry dates and 1930 Cybercrime updates.",
                    icon = Icons.Default.Notifications,
                    isGranted = notifStatus
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Action Button
            Button(
                onClick = {
                    if (allGranted) {
                        onPermissionsFinished()
                    } else {
                        permissionLauncher.launch(permissionsToRequest)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepIndigoSlatePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (allGranted) {
                            if (isTamil) "அனைத்தும் அனுமதிக்கப்பட்டது - தொடர்க" else "All Permissions Granted — Continue"
                        } else {
                            if (isTamil) "அனுமதிகளை வழங்கவும் (Grant All Permissions)" else "Grant All Android Permissions"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Icon(
                        imageVector = if (allGranted) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Skip / Later Option
            if (!allGranted) {
                TextButton(
                    onClick = onPermissionsFinished,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = if (isTamil) "பின்னர் அனுமதிக்கவும் (Skip for Now)" else "Skip for Now",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF6B7280))
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isGranted) SageGreenSuccessContainer else WarmIvorySurface,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isGranted) SageGreenSuccessText else DeepIndigoSlatePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                    if (isGranted) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SageGreenSuccessContainer,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "Granted ✓",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreenSuccessText
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF4A4E57),
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
