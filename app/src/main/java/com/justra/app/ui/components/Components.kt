package com.justra.app.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.RiskLevel
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.HennaRedAlertContainer
import com.justra.app.ui.theme.HennaRedAlertText
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.SaffronAmberWarningContainer
import com.justra.app.ui.theme.SaffronAmberWarningText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.util.BilingualStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JustraTopBar(
    title: String,
    subtitle: String? = null,
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    unreadNotifications: Int = 0,
    onNotificationClick: (() -> Unit)? = null,
    onLockClick: (() -> Unit)? = null,
    onMenuNavigate: ((route: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF4A4E57)
                        ),
                        maxLines = 1
                    )
                }
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepIndigoSlatePrimary
                    )
                }
            } else {
                Box(
                    modifier = Modifier.padding(start = 12.dp, end = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    JustraBrandLogo(
                        size = 32.dp,
                        showWordmark = false
                    )
                }
            }
        },
        actions = {
            // Notifications button with badge
            if (onNotificationClick != null) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("top_bar_notification_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) {
                                Badge(
                                    containerColor = HennaRedAlertText,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = unreadNotifications.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = if (unreadNotifications > 0) HennaRedAlertText else DeepIndigoSlatePrimary
                        )
                    }
                }
            }

            // Language switch chip
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PaleSandstoneVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onToggleLanguage() }
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .testTag("language_toggle_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = DeepIndigoSlatePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = currentLanguage.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                }
            }

            // Lock Vault Button
            if (onLockClick != null) {
                IconButton(
                    onClick = onLockClick,
                    modifier = Modifier.testTag("top_bar_lock_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Application",
                        tint = DeepIndigoSlatePrimary
                    )
                }
            }


            // Menu Bar Dropdown Button
            if (onMenuNavigate != null) {
                Box {
                    IconButton(
                        onClick = { isMenuExpanded = !isMenuExpanded },
                        modifier = Modifier.testTag("top_bar_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Bar",
                            tint = DeepIndigoSlatePrimary
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier
                            .background(WarmIvorySurface)
                            .border(1.dp, PaleSandstoneVariant, RoundedCornerShape(8.dp))
                            .testTag("menubar_dropdown_menu")
                    ) {
                        Text(
                            text = BilingualStrings.t("menubar_title", currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TerracottaAccentSecondary
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        HorizontalDivider(color = PaleSandstoneVariant)

                        val menuItemColors = MenuDefaults.itemColors(
                            textColor = DeepIndigoSlatePrimary,
                            leadingIconColor = DeepIndigoSlatePrimary
                        )

                        DropdownMenuItem(
                            text = { Text("1. " + BilingualStrings.t("nav_home", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("home")
                            },
                            modifier = Modifier.testTag("menu_item_home")
                        )
                        DropdownMenuItem(
                            text = { Text("2. " + BilingualStrings.t("nav_chat", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("chat")
                            },
                            modifier = Modifier.testTag("menu_item_chat")
                        )
                        DropdownMenuItem(
                            text = { Text("3. " + BilingualStrings.t("nav_scam", currentLanguage), color = TerracottaAccentSecondary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = TerracottaAccentSecondary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("scam_checker")
                            },
                            modifier = Modifier.testTag("menu_item_scam")
                        )
                        DropdownMenuItem(
                            text = { Text("4. " + BilingualStrings.t("nav_vault", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("evidence_vault/case_consumer_01")
                            },
                            modifier = Modifier.testTag("menu_item_vault")
                        )
                        DropdownMenuItem(
                            text = { Text("5. " + BilingualStrings.t("nav_navigator", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Checklist, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("action_navigator/case_consumer_01")
                            },
                            modifier = Modifier.testTag("menu_item_navigator")
                        )
                        DropdownMenuItem(
                            text = { Text("6. " + BilingualStrings.t("nav_complaint", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("complaint_generator/case_consumer_01")
                            },
                            modifier = Modifier.testTag("menu_item_complaint")
                        )
                        DropdownMenuItem(
                            text = { Text("7. " + BilingualStrings.t("nav_timeline", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Timeline, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("timeline_readiness/case_consumer_01")
                            },
                            modifier = Modifier.testTag("menu_item_timeline")
                        )
                        DropdownMenuItem(
                            text = { Text("8. " + BilingualStrings.t("nav_counsel", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.FolderShared, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("counsel_handoff/case_consumer_01")
                            },
                            modifier = Modifier.testTag("menu_item_counsel")
                        )
                        DropdownMenuItem(
                            text = { Text("9. " + BilingualStrings.t("nav_auth", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("biometric_auth")
                            },
                            modifier = Modifier.testTag("menu_item_auth")
                        )
                        DropdownMenuItem(
                            text = { Text("10. " + BilingualStrings.t("select_language", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("language_consent")
                            },
                            modifier = Modifier.testTag("menu_item_consent")
                        )
                        DropdownMenuItem(
                            text = { Text("11. " + BilingualStrings.t("nav_court_fee", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Calculate, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("court_fee_calculator")
                            },
                            modifier = Modifier.testTag("menu_item_court_fee")
                        )
                        DropdownMenuItem(
                            text = { Text("12. " + BilingualStrings.t("nav_sec65b", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("section_65b_certificate")
                            },
                            modifier = Modifier.testTag("menu_item_sec65b")
                        )
                        DropdownMenuItem(
                            text = { Text("13. " + BilingualStrings.t("nav_account_settings", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("account_settings")
                            },
                            modifier = Modifier.testTag("menu_item_settings")
                        )
                        DropdownMenuItem(
                            text = { Text("14. " + BilingualStrings.t("nav_notifications_center", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("notifications_center")
                            },
                            modifier = Modifier.testTag("menu_item_notifications_center")
                        )
                        DropdownMenuItem(
                            text = { Text("15. " + BilingualStrings.t("nav_rti", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("rti_drafting_wizard")
                            },
                            modifier = Modifier.testTag("menu_item_rti")
                        )
                        DropdownMenuItem(
                            text = { Text("16. " + BilingualStrings.t("nav_legal_notice", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Gavel, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("legal_notice_composer")
                            },
                            modifier = Modifier.testTag("menu_item_notice")
                        )
                        DropdownMenuItem(
                            text = { Text("17. " + BilingualStrings.t("nav_bns_ipc", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Policy, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("bns_ipc_transition")
                            },
                            modifier = Modifier.testTag("menu_item_bns")
                        )
                        DropdownMenuItem(
                            text = { Text("18. " + BilingualStrings.t("nav_nalsa", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Handshake, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("nalsa_free_legal_aid")
                            },
                            modifier = Modifier.testTag("menu_item_nalsa")
                        )
                        DropdownMenuItem(
                            text = { Text("19. " + BilingualStrings.t("nav_mact", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("motor_accident_mact")
                            },
                            modifier = Modifier.testTag("menu_item_mact")
                        )
                        DropdownMenuItem(
                            text = { Text("20. " + BilingualStrings.t("nav_consumer_mediation", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Checklist, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("consumer_mediation")
                            },
                            modifier = Modifier.testTag("menu_item_consumer_med")
                        )
                        DropdownMenuItem(
                            text = { Text("21. " + BilingualStrings.t("nav_women_rights", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("women_domestic_rights")
                            },
                            modifier = Modifier.testTag("menu_item_women_rights")
                        )
                        DropdownMenuItem(
                            text = { Text("22. " + BilingualStrings.t("nav_cyber_cell", currentLanguage), color = DeepIndigoSlatePrimary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("cyber_crime_dossier")
                            },
                            modifier = Modifier.testTag("menu_item_cyber_dossier")
                        )
                        DropdownMenuItem(
                            text = { Text("23. " + BilingualStrings.t("nav_global_jurisdiction", currentLanguage), color = TerracottaAccentSecondary, fontWeight = FontWeight.Medium) },
                            leadingIcon = { Icon(Icons.Default.Policy, contentDescription = null, tint = TerracottaAccentSecondary) },
                            colors = menuItemColors,
                            onClick = {
                                isMenuExpanded = false
                                onMenuNavigate("global_jurisdiction")
                            },
                            modifier = Modifier.testTag("menu_item_global_jurisdiction")
                        )
                    }
                }
            }

            if (onLockClick != null) {
                IconButton(
                    onClick = onLockClick,
                    modifier = Modifier.testTag("lock_app_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock App",
                        tint = DeepIndigoSlatePrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = WarmIvorySurface
        ),
        modifier = modifier
    )
}

@Composable
fun NyayaTopBar(
    title: String,
    subtitle: String? = null,
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    unreadNotifications: Int = 0,
    onNotificationClick: (() -> Unit)? = null,
    onLockClick: (() -> Unit)? = null,
    onMenuNavigate: ((route: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    JustraTopBar(
        title = title,
        subtitle = subtitle,
        currentLanguage = currentLanguage,
        onToggleLanguage = onToggleLanguage,
        onBackClick = onBackClick,
        unreadNotifications = unreadNotifications,
        onNotificationClick = onNotificationClick,
        onLockClick = onLockClick,
        onMenuNavigate = onMenuNavigate,
        modifier = modifier
    )
}

@Composable
fun NyayaBottomMenuBar(
    currentRoute: String,
    currentLanguage: LanguagePreference,
    onNavigate: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppBottomNavBar(
        currentRoute = currentRoute,
        currentLanguage = currentLanguage,
        onNavigateTo = onNavigate,
        modifier = modifier
    )
}

@Composable
fun StatutoryDisclaimerBanner(
    language: LanguagePreference,
    modifier: Modifier = Modifier
) {
    val isTa = language == LanguagePreference.TAMIL

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Gavel,
                contentDescription = "Statutory Notice",
                tint = TerracottaAccentSecondary,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isTa) "⚖️ சட்டபூர்வ தகவல் அமைப்பு & வழக்கறிஞர் ஆலோசனை" else "⚖️ Statutory Informational System & Advocate Advisory",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isTa)
                        "ஜஸ்ட்ரா என்பது ஒரு AI உதவி சட்ட தகவல் மற்றும் வரைவு உதவி அமைப்பாகும். இது பிணைக்கப்பட்ட வழக்கறிஞர் ஆலோசனையை மாற்றாது. 100% துல்லியமான நீதிமன்ற ஆலோசன பெற தகுதிவாய்ந்த வழக்கறிஞரை தொடர்பு கொள்ளவும்."
                    else
                        "Justra is an AI-assisted statutory informational support system. It does not provide binding legal advice or replace a licensed Advocate. For 100% factual legal representation, consult a registered Advocate.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF4A4E57),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun EmergencyHelplineBar(
    language: LanguagePreference,
    onDial1930: () -> Unit,
    onDial112: () -> Unit,
    onDial1915: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HennaRedAlertContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, HennaRedAlertText.copy(alpha = 0.25f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("emergency_helpline_bar")
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Emergency Calls",
                    tint = HennaRedAlertText,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (language == LanguagePreference.TAMIL) "உடனடி அவசர உதவி எண்கள் (Direct Dialer)" else "Statutory Emergency Helplines (Direct OS Dialer)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = HennaRedAlertText
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1930 Cybercrime
                HelplineDialChip(
                    number = "1930",
                    label = if (language == LanguagePreference.TAMIL) "சைபர் மோசடி" else "Cybercrime 1930",
                    onClick = onDial1930,
                    modifier = Modifier.weight(1f)
                )
                // 112 Emergency
                HelplineDialChip(
                    number = "112",
                    label = if (language == LanguagePreference.TAMIL) "காவல்/அவசரம்" else "Emergency 112",
                    onClick = onDial112,
                    modifier = Modifier.weight(1f)
                )
                // 1915 Consumer
                HelplineDialChip(
                    number = "1915",
                    label = if (language == LanguagePreference.TAMIL) "நுகர்வோர்" else "Consumer 1915",
                    onClick = onDial1915,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HelplineDialChip(
    number: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, HennaRedAlertText.copy(alpha = 0.3f)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = HennaRedAlertText
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = HennaRedAlertText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CircularReadinessGauge(
    score: Int,
    modifier: Modifier = Modifier,
    sizeDp: Int = 80
) {
    val animatedColor = when {
        score >= 80 -> SageGreenSuccessText
        score >= 50 -> SaffronAmberWarningText
        else -> HennaRedAlertText
    }
    val trackColor = PaleSandstoneVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeDp.dp)
    ) {
        Canvas(modifier = Modifier.size(sizeDp.dp)) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background track
            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Progress Arc
            val sweep = (score / 100f) * 360f
            drawArc(
                color = animatedColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = DeepIndigoSlatePrimary
                )
            )
            Text(
                text = "Readiness",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@Composable
fun AudioWaveformVisualizer(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier
) {
    WaveformVisualizer(
        amplitudes = amplitudes,
        isRecording = true,
        modifier = modifier
    )
}

@Composable
fun RiskLevelBadge(riskLevel: RiskLevel, language: LanguagePreference) {
    val (bg, fg) = when (riskLevel) {
        RiskLevel.NONE -> SageGreenSuccessContainer to SageGreenSuccessText
        RiskLevel.LOW -> SageGreenSuccessContainer to SageGreenSuccessText
        RiskLevel.MEDIUM -> SaffronAmberWarningContainer to SaffronAmberWarningText
        RiskLevel.HIGH -> HennaRedAlertContainer to HennaRedAlertText
        RiskLevel.CRITICAL -> HennaRedAlertContainer to HennaRedAlertText
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, fg.copy(alpha = 0.3f)),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (riskLevel == RiskLevel.NONE || riskLevel == RiskLevel.LOW) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (language == LanguagePreference.TAMIL) riskLevel.labelTa else riskLevel.labelEn,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = fg
                )
            )
        }
    }
}

@Composable
fun NotificationCenterDialog(
    notifications: List<com.justra.app.domain.model.InAppNotificationItem>,
    currentLanguage: LanguagePreference,
    onDismiss: () -> Unit,
    onNotificationClick: (route: String, id: String) -> Unit,
    onMarkAllRead: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PaleSandstoneVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = TerracottaAccentSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = BilingualStrings.t("notifications_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Close",
                            tint = DeepIndigoSlatePrimary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = PaleSandstoneVariant)

                if (notifications.isEmpty()) {
                    Text(
                        text = BilingualStrings.t("notifications_empty", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        notifications.forEach { item ->
                            val isTa = currentLanguage == LanguagePreference.TAMIL
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (item.isUrgent) HennaRedAlertContainer.copy(alpha = 0.5f) else PaleSandstoneVariant
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (item.isUrgent) HennaRedAlertText.copy(alpha = 0.4f) else PaleSandstoneVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNotificationClick(item.targetRoute, item.id) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isTa) item.titleTa else item.titleEn,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.isUrgent) HennaRedAlertText else DeepIndigoSlatePrimary
                                            )
                                        )
                                        if (!item.isRead) {
                                            Surface(
                                                shape = CircleShape,
                                                color = TerracottaAccentSecondary,
                                                modifier = Modifier.size(8.dp)
                                            ) {}
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isTa) item.messageTa else item.messageEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    androidx.compose.material3.TextButton(
                        onClick = onMarkAllRead,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = BilingualStrings.t("mark_all_read", currentLanguage),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DeepIndigoSlatePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    role: com.justra.app.domain.model.UserRole,
    isSelected: Boolean,
    currentLanguage: LanguagePreference,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryContainerSlate else PaleSandstoneVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) DeepIndigoSlatePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("role_card_${role.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) DeepIndigoSlatePrimary else Color.White,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (role) {
                            com.justra.app.domain.model.UserRole.CITIZEN -> Icons.Default.Shield
                            com.justra.app.domain.model.UserRole.LEGAL_COUNSEL -> Icons.Default.Gavel
                            com.justra.app.domain.model.UserRole.MSME_BUSINESS -> Icons.Default.Policy
                            com.justra.app.domain.model.UserRole.CYBER_FRAUD_VICTIM -> Icons.Default.Security
                        },
                        contentDescription = null,
                        tint = if (isSelected) Color.White else DeepIndigoSlatePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isTa) role.titleTa else role.titleEn,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    )
                )
                Text(
                    text = if (isTa) role.subtitleTa else role.subtitleEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = DeepIndigoSlatePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

