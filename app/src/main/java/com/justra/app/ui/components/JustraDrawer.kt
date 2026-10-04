package com.justra.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.justra.app.domain.model.LanguagePreference

data class DrawerMenuItem(
    val id: Int,
    val route: String,
    val titleEn: String,
    val titleTa: String,
    val icon: ImageVector,
    val category: String
)

val JUSTRA_DRAWER_ITEMS = listOf(
    // Core Navigation
    DrawerMenuItem(1, "home", "Dashboard", "முகப்பு பலகை", Icons.Default.Home, "Core"),
    DrawerMenuItem(2, "chat", "AI Legal Intake", "AI சட்ட சேர்க்கை", Icons.AutoMirrored.Filled.Chat, "Core"),
    DrawerMenuItem(3, "scam_checker", "Scam Checker", "மோசடி பரிசோதனை", Icons.Default.Shield, "Core"),
    DrawerMenuItem(4, "evidence_vault", "Evidence Vault", "சான்றுகளின் பெட்டகம்", Icons.Default.FolderShared, "Core"),

    // Case Workflow & Action
    DrawerMenuItem(5, "action_navigator", "Action Steps / Legal Navigator", "வழக்கு நடவடிக்கை வழிகாட்டி", Icons.Default.Timeline, "Workflow"),
    DrawerMenuItem(6, "smart_complaint", "Draft Complaint", "ஸ்மார்ட் புகார் வரைவு", Icons.Default.Description, "Workflow"),
    DrawerMenuItem(7, "timeline_readiness", "Timeline & Readiness", "காலவரிசை & தயார்நிலை", Icons.Default.Checklist, "Workflow"),
    DrawerMenuItem(8, "counsel_handoff", "Counsel Handoff", "வழக்கறிஞர் ஒப்படைப்பு", Icons.Default.Handshake, "Workflow"),

    // Security & Preferences
    DrawerMenuItem(9, "security_settings", "Vault Security", "பெட்டக பாதுகாப்பு", Icons.Default.Lock, "Settings"),
    DrawerMenuItem(10, "language_consent", "Select Operating Language", "இயக்க மொழி", Icons.Default.Language, "Settings"),
    DrawerMenuItem(13, "account_settings", "Account & App Settings", "கணக்கு அமைப்புகள்", Icons.Default.Settings, "Settings"),
    DrawerMenuItem(14, "notifications_center", "Notifications & Reminders", "அறிவிப்புகள் & நினைவூட்டல்கள்", Icons.Default.NotificationsActive, "Settings"),

    // Statutory Tools & Evidence Compliance
    DrawerMenuItem(11, "court_fee_calculator", "Court Fee & Jurisdiction", "நீதிமன்றக் கட்டணம் கணக்கீடு", Icons.Default.Calculate, "Tools"),
    DrawerMenuItem(12, "section_65b_certificate", "Section 65B / 63 BSA", "65B / 63 BSA சான்றிதழ்", Icons.Default.VerifiedUser, "Tools"),
    DrawerMenuItem(15, "rti_drafting_wizard", "RTI Application & Appeals", "தகவல் அறியும் உரிமை (RTI)", Icons.Default.Policy, "Tools"),
    DrawerMenuItem(16, "legal_notice_composer", "Legal Demand Notice", "சட்டப்பூர்வ அறிவிப்பு வரைவு", Icons.Default.Gavel, "Tools"),
    DrawerMenuItem(17, "bns_ipc_transition", "BNS vs IPC Navigator", "BNS vs IPC ஒப்பீடு", Icons.AutoMirrored.Filled.CompareArrows, "Tools"),
    DrawerMenuItem(18, "nalsa_free_legal_aid", "NALSA Free Legal Aid", "???? ???? ???? (NALSA)", Icons.Default.Support, "Tools"),
    DrawerMenuItem(19, "motor_accident_mact", "MACT Accident Claim", "???? ??????? ????????", Icons.Default.CarCrash, "Tools"),
    DrawerMenuItem(20, "consumer_mediation", "Consumer Mediation & e-Daakhil", "????????? ??????? & e-Daakhil", Icons.Default.ShoppingBag, "Tools"),
    DrawerMenuItem(21, "women_domestic_rights", "Women's Rights & PoSH", "??????? ????? & PoSH", Icons.Default.Female, "Tools"),
    DrawerMenuItem(22, "cyber_crime_dossier", "Cyber Crime Dossier", "????? ??????? ?????", Icons.Default.Security, "Tools")
)

@Composable
fun JustraDrawerContent(
    currentRoute: String?,
    currentLanguage: LanguagePreference,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val categories = listOf("Core", "Workflow", "Tools", "Settings")

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF0F1E36),
        drawerContentColor = Color(0xFFFAF7F2),
        modifier = modifier.width(320.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = Color(0xFFE5A93C),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "JUSTRA",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFAF7F2)
                        )
                    )
                    Text(
                        text = if (isTa) "JusTra சட்ட உதவியாளர்" else "JusTra Legal Assistant",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE5A93C))
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = 12.dp))

            categories.forEach { cat ->
                val groupItems = JUSTRA_DRAWER_ITEMS.filter { it.category == cat }
                if (groupItems.isNotEmpty()) {
                    Text(
                        text = when(cat) {
                            "Core" -> if (isTa) "??????? ?????????" else "Core Features"
                            "Workflow" -> if (isTa) "?????? ???????????" else "Case Workflow"
                            "Tools" -> if (isTa) "?????? ????????" else "Statutory Tools"
                            else -> if (isTa) "?????????? & ??????????" else "Security & Settings"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFE5A93C),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                    )

                    groupItems.forEach { item ->
                        val isSelected = currentRoute == item.route || currentRoute?.startsWith(item.route) == true
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF0F1E36) else Color(0xFFFAF7F2)
                                )
                            },
                            label = {
                                Text(
                                    text = if (isTa) item.titleTa else item.titleEn,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF0F1E36) else Color(0xFFFAF7F2)
                                    )
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                onNavigate(item.route)
                                onCloseDrawer()
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFFE5A93C),
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
