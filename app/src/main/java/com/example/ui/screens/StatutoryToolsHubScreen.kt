package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DisputeCategory
import com.example.domain.model.LanguagePreference
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.NyayaTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatutoryToolsHubScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onLockApp: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSelectTopic: (categoryKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var selectedTab by remember { mutableStateOf(0) }
    val categories = remember { DisputeCategory.values().toList() }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "சட்டக் கருவிகள்" else "Law & Tools",
                currentLanguage = currentLanguage,
                unreadNotifications = 0,
                onToggleLanguage = onToggleLanguage,
                onNotificationClick = { onNavigateToRoute("notifications_center") },
                onLockClick = onLockApp
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentRoute = "statutory_tools_hub",
                onNavigateTo = onNavigateToRoute,
                currentLanguage = currentLanguage
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isTa) "சட்டக் கருவிகள் & பகுதிகள்" else "Legal Tools and Law Library",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SovereignNavy,
                    fontFamily = FontFamily.Serif
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SovereignNavy
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isTa) "சட்டப் பகுதிகள்" else "Indian Law Categories", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (isTa) "சட்டக் கருவிகள் (4)" else "Legal Utilities (4)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (isTa) "உலகளாவிய சட்டம்" else "Global Law Hub", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(categories, key = { it.name }) { category ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectTopic(category.name) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isTa) category.titleTa else category.titleEn,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                                        )
                                        Text(
                                            text = if (isTa) category.descriptionTa else category.descriptionEn,
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        UtilityToolCard(
                            title = if (isTa) "நீதிமன்ற கட்டண கணக்கீடு" else "Court Fee Calculator",
                            subtitle = if (isTa) "மாநிலங்கள் வாரியாக நீதிமன்ற கட்டணம் மற்றும் முத்திரை வில்லை கணக்கிடுக" else "Calculate state-specific court fees & stamp duty",
                            icon = Icons.Default.Calculate,
                            onClick = { onNavigateToRoute("court_fee_calc") }
                        )
                        UtilityToolCard(
                            title = if (isTa) "பிரிவு 65B சான்றிதழ் வழிகாட்டி" else "65B Evidence Certificate Wizard",
                            subtitle = if (isTa) "டிஜிட்டல் ஆதாரங்களுக்கான 65B சான்றிதழ் உருவாக்கவும்" else "Generate mandatory Section 65B affidavit for digital evidence",
                            icon = Icons.Default.Verified,
                            onClick = { onNavigateToRoute("section_65b_wizard") }
                        )
                        UtilityToolCard(
                            title = if (isTa) "RTI மனு வழிகாட்டி" else "RTI Application Generator",
                            subtitle = if (isTa) "தகவல் அறியும் உரிமைச் சட்ட மனு உடனே தயார் செய்க" else "Draft official Right to Information queries instantly",
                            icon = Icons.Default.Description,
                            onClick = { onNavigateToRoute("rti_wizard") }
                        )
                        UtilityToolCard(
                            title = if (isTa) "BNS புதிய சட்ட மாற்றி" else "BNS / IPC Law Converter",
                            subtitle = if (isTa) "பழைய IPC பிரிவுகளை புதிய BNS பிரிவுகளாக மாற்றவும்" else "Convert old IPC sections into new Bharatiya Nyaya Sanhita (BNS) provisions",
                            icon = Icons.Default.Transform,
                            onClick = { onNavigateToRoute("bns_converter") }
                        )
                    }
                }
                2 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        UtilityToolCard(
                            title = if (isTa) "உலகளாவிய சட்ட தரவுத்தளம்" else "Global & Comparative Law Hub",
                            subtitle = if (isTa) "சர்வதேச சட்டங்கள் மற்றும் உரிமைகள் ஒப்பீடு" else "Compare Indian legal provisions with international human rights benchmarks",
                            icon = Icons.Default.Public,
                            onClick = { onNavigateToRoute("global_law_hub") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UtilityToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = SoftNavyContainer, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}
