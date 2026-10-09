package com.justra.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    // Notification Preferences
    var caseUpdatesEnabled by remember { mutableStateOf(true) }
    var legalRemindersEnabled by remember { mutableStateOf(true) }

    // Theme state (0: System Default, 1: Light Mode, 2: Dark Mode)
    var selectedThemeMode by remember { mutableStateOf(1) } // Default to Light Mode as per design

    // Dialog States
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTa) "அமைப்புகள்" else "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
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
                            tint = SovereignNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmCanvasBg)
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
            // 1. Notification Preferences Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                    border = BorderStroke(1.dp, CardBorderStroke),
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
                                tint = SovereignNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "அறிவிப்பு முன்னுரிமைகள்" else "Notification Preferences",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy,
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
                                        color = SovereignNavy
                                    )
                                )
                                Text(
                                    text = if (isTa) "நீதிமன்ற நிலை மற்றும் மனு நிலை மாற்ற அறிவிப்புகள்" else "Receive notifications when case hearing dates or status change",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                                )
                            }
                            Switch(
                                checked = caseUpdatesEnabled,
                                onCheckedChange = { caseUpdatesEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SovereignNavy
                                )
                            )
                        }

                        HorizontalDivider(color = CardBorderStroke.copy(alpha = 0.5f))

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
                                        color = SovereignNavy
                                    )
                                )
                                Text(
                                    text = if (isTa) "காலக்கெடு சட்டம் சார்ந்த அவசர எச்சரிக்கைகள்" else "Statutory limitation period countdown and notice response deadlines",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                                )
                            }
                            Switch(
                                checked = legalRemindersEnabled,
                                onCheckedChange = { legalRemindersEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SovereignNavy
                                )
                            )
                        }
                    }
                }
            }

            // 2. Appearance & Theme Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = SovereignNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "தோற்றம் & தீம் (Appearance & Theme)" else "Appearance & Theme",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy,
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
                                        onClick = { selectedThemeMode = mode },
                                        colors = RadioButtonDefaults.colors(selectedColor = SovereignNavy)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (selectedThemeMode == mode) FontWeight.Bold else FontWeight.Normal,
                                            color = SovereignNavy
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Legal & Terms Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                    border = BorderStroke(1.dp, CardBorderStroke),
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
                                tint = SovereignNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "சட்டப்பூர்வ கொள்கைகள்" else "Legal & Compliance",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy,
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
                                        color = SovereignNavy
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = SovereignNavy
                                )
                            }
                        }

                        HorizontalDivider(color = CardBorderStroke.copy(alpha = 0.5f))

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
                                        color = SovereignNavy
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = SovereignNavy
                                )
                            }
                        }
                    }
                }
            }

            // 4. App Version & Build Information Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                    border = BorderStroke(1.dp, CardBorderStroke),
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
                            tint = SovereignNavy,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Justra v1.0.0",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy
                                )
                            )
                            Text(
                                text = if (isTa) "பாரதிய நியாய சன்ஹிதா (BNS 2023) ஆதரவு இயந்திரம்" else "Sovereign AI Legal Engine • Build 2026.10",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                            )
                        }
                    }
                }
            }
        }
    }

    // Legal Disclaimer Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy, contentColor = Color.White)
                ) {
                    Text(if (isTa) "புரிந்தது" else "I Understand", color = Color.White)
                }
            },
            title = {
                Text(
                    text = if (isTa) "சட்ட மறுப்பு & சேவை விதிமுறைகள்" else "Legal Disclaimer & Terms of Service",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                )
            },
            text = {
                Text(
                    text = if (isTa) {
                        "ஜஸ்ட்ரா (Justra) செயற்கை நுண்ணறிவு சட்ட உதவி தளம் மட்டுமே. இது சட்ட விழிப்புணர்வு மற்றும் மனு உருவாக்கத்திற்கு உதவுகிறது. இது வழக்கறிஞரின் நேரடி ஆலோசனையை மாற்ற முடியாது."
                    } else {
                        "Justra provides automated legal intake, statutory mapping, and draft generation under Indian statutes (BNS 2023, BNSS 2023, BSA 2023, CPA 2019). Information generated does not constitute formal attorney-client legal advice."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark)
                )
            },
            containerColor = SandstoneCard
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy, contentColor = Color.White)
                ) {
                    Text(if (isTa) "மூடு" else "Close", color = Color.White)
                }
            },
            title = {
                Text(
                    text = if (isTa) "தனியுரிமைக் கொள்கை" else "Privacy Policy",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                )
            },
            text = {
                Text(
                    text = if (isTa) {
                        "உங்கள் தகவல்கள் அனைத்தும் உங்கள் சாதனத்திலேயே பாதுகாப்பாக சேமிக்கப்படும். எந்தவொரு வெளி நபர்களுக்கும் தகவல்கள் பகிரப்படாது."
                    } else {
                        "Justra prioritizes your privacy. User profiles, case records, and dispute intakes are persisted locally using secure storage. No personal identification data is sold or transmitted to unauthorized third parties."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark)
                )
            },
            containerColor = SandstoneCard
        )
    }
}
