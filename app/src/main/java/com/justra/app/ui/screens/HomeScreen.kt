package com.justra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.justra.app.data.local.CaseEntity
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.InAppNotificationItem
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.UserRole
import com.justra.app.ui.components.AppBottomNavBar
import com.justra.app.ui.components.CircularReadinessGauge
import com.justra.app.ui.components.EmergencyHelplineBar
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.theme.*
import com.justra.app.util.BilingualStrings
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentLanguage: LanguagePreference,
    activeCases: List<CaseEntity>,
    userRole: UserRole = UserRole.CITIZEN,
    notifications: List<InAppNotificationItem> = emptyList(),
    unreadNotificationsCount: Int = 0,
    documentFeedbackFlow: SharedFlow<String>? = null,
    onGenerateLegalDocument: ((title: String, category: DisputeCategory, onComplete: (String) -> Unit) -> Unit)? = null,
    onToggleLanguage: () -> Unit,
    onLockApp: () -> Unit,
    onSwitchRole: (UserRole) -> Unit = {},
    onMarkNotificationRead: (String) -> Unit = {},
    onMarkAllNotificationsRead: () -> Unit = {},
    onNavigateToChat: (initialPrompt: String?) -> Unit,
    onNavigateToCaseDetail: (caseId: String) -> Unit,
    onNavigateToActionNavigator: (caseId: String) -> Unit,
    onNavigateToComplaint: (caseId: String) -> Unit,
    onNavigateToEvidenceVault: (caseId: String) -> Unit,
    onNavigateToTimeline: (caseId: String) -> Unit,
    onNavigateToScamChecker: () -> Unit,
    onNavigateToCounselHandoff: (caseId: String) -> Unit,
    onCreateNewDispute: (title: String, category: DisputeCategory, incidentDate: String?, opposingParty: String?, estimatedAmount: String?, summary: String?, relief: String?, userRole: UserRole?) -> Unit,
    onDialHelpline: (number: String) -> Unit,
    onNavigateToRoute: ((String) -> Unit)? = null,
    onTopicClick: (categoryKey: String) -> Unit = {},
    onNavigateToVoice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = BilingualStrings.t("app_title", currentLanguage),
                currentLanguage = currentLanguage,
                unreadNotifications = unreadNotificationsCount,
                onToggleLanguage = onToggleLanguage,
                onNotificationClick = { onNavigateToRoute?.invoke("notifications_center") },
                onLockClick = onLockApp
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentRoute = "home",
                onNavigateTo = { route -> onNavigateToRoute?.invoke(route) },
                currentLanguage = currentLanguage
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // Section 1: Hero Identity Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SovereignNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "பாக்கெட் வழக்கறிஞர் & AI நீதி உதவியாளர்" else "Citizen Rights & AI Legal Assistant",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isTa) "இந்திய சட்ட பாதுகாப்பு | தமிழ் & ஆங்கிலம்" else "Structured Judicial Dossiers & Voice Assistant",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AccentTerracotta,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = userRole.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onNavigateToVoice,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentTerracotta),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTa) "குரல் உதவி" else "Voice Intake",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                            OutlinedButton(
                                onClick = { onNavigateToRoute?.invoke("my_cases_hub") },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTa) "எனது வழக்குகள்" else "My Cases",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Emergency Helpline Quick Bar
            item {
                EmergencyHelplineBar(
                    language = currentLanguage,
                    onDial1930 = { onDialHelpline("1930") },
                    onDial112 = { onDialHelpline("112") },
                    onDial1915 = { onDialHelpline("1915") }
                )
            }

            // Section 3: Priority Quick Action Portals
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isTa) "முதன்மை சேவைகள்" else "Quick Action Hub",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Card A: Scam Checker
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToScamChecker() }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = AccentTerracotta, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isTa) "மோசடி சரிபார்ப்பு" else "Scam Checker",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                                )
                                Text(
                                    text = if (isTa) "APK/SMS ஆய்வு" else "Detect Fraud",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        // Card B: AI Legal Chat
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToChat(null) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isTa) "AI சட்ட உரையாடல்" else "Legal AI Assistant",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                                )
                                Text(
                                    text = if (isTa) "சட்ட ஆலோசனை" else "Instant Legal Guidance",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Latest Active Case Spotlight
            item {
                Text(
                    text = if (isTa) "தற்போதைய வழக்கு நிலை" else "Active Dispute Readiness",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SovereignNavy
                    )
                )
            }

            item {
                val latestCase = activeCases.firstOrNull()
                if (latestCase != null) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = latestCase.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SovereignNavy
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isTa) latestCase.disputeCategory.titleTa else latestCase.disputeCategory.titleEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                }
                                CircularReadinessGauge(
                                    score = if (latestCase.generatedComplaintDraft != null) 90 else 60,
                                    sizeDp = 44
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onNavigateToRoute?.invoke("my_cases_hub") },
                                colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isTa) "எனது வழக்குகள் & 5 கருவிகள் காண" else "Open My Cases & 5 Legal Tools",
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "வழக்குகள் எதுவும் இல்லை" else "No Active Disputes",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                                )
                                Text(
                                    text = if (isTa) "புதிய சட்ட தகராறு பதிவிட My Cases செல்லவும்" else "Tap to initiate legal complaint dossier in My Cases hub",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }

            // Section 5: Statutory Knowledge & Tools Navigation Hub Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToRoute?.invoke("statutory_tools_hub") }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftNavyContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Gavel, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTa) "சட்டக் கருவிகள் & பகுதிகள்" else "Legal Tools & Statutory Library",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isTa) "வாடகை, தொழிலாளர், சைபர் சட்டம் & நீதிமன்ற கட்டண கணக்கீடு" else "Tenancy, Labour, Cyber law, Court Fee Calc & 65B Certificate",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
