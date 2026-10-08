package com.justra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.justra.app.data.local.CaseEntity
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.UserRole
import com.justra.app.ui.components.AppBottomNavBar
import com.justra.app.ui.components.CircularReadinessGauge
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCasesHubScreen(
    currentLanguage: LanguagePreference,
    activeCases: List<CaseEntity>,
    userRole: UserRole,
    onToggleLanguage: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onNavigateToActionNavigator: (caseId: String) -> Unit,
    onNavigateToComplaint: (caseId: String) -> Unit,
    onNavigateToEvidenceVault: (caseId: String) -> Unit,
    onNavigateToTimeline: (caseId: String) -> Unit,
    onNavigateToCounselHandoff: (caseId: String) -> Unit,
    onCreateNewDispute: (
        title: String,
        category: DisputeCategory,
        incidentDate: String?,
        opposingParty: String?,
        estimatedAmount: String?,
        summary: String?,
        relief: String?,
        userRole: UserRole?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewCaseDialog by remember { mutableStateOf(false) }
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "எனது வழக்குகள்" else "My Cases Hub",
                currentLanguage = currentLanguage,
                unreadNotifications = 0,
                onToggleLanguage = onToggleLanguage,
                onNotificationClick = { onNavigateToRoute("notifications_center") },
                onSettingsClick = { onNavigateToRoute("account_settings") }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentRoute = "my_cases_hub",
                onNavigateTo = onNavigateToRoute,
                currentLanguage = currentLanguage
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewCaseDialog = true },
                containerColor = SovereignNavy,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        text = if (isTa) "புதிய வழக்கு" else "New Dispute",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("create_dispute_fab")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isTa) "வழக்கு மேலாண்மை மையம்" else "My Disputes & Legal Tools",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy,
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Text(
                        text = if (isTa) "5 பிரத்யேக சட்ட கருவிகள் கார்டு ஒவ்வொன்றிலும் கிடைக்கும்" else "Each dossier includes 5 legal preparation action tools",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (activeCases.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isTa) "வழக்குகள் பதிவு செய்யப்படவில்லை" else "No Active Legal Dossiers",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTa) "கீழே உள்ள 'புதிய வழக்கு' பொத்தானை கிளிக் செய்து புதிய வழக்கு தொடங்கவும்." else "Tap 'New Dispute' below to initiate a legal complaint file with step-by-step guidance.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(activeCases, key = { it.caseId }) { caseItem ->
                        CaseCardWithTools(
                            caseEntity = caseItem,
                            currentLanguage = currentLanguage,
                            onOpenNavigator = { onNavigateToActionNavigator(caseItem.caseId) },
                            onOpenComplaint = { onNavigateToComplaint(caseItem.caseId) },
                            onOpenVault = { onNavigateToEvidenceVault(caseItem.caseId) },
                            onOpenTimeline = { onNavigateToTimeline(caseItem.caseId) },
                            onOpenCounsel = { onNavigateToCounselHandoff(caseItem.caseId) }
                        )
                    }
                }
            }
        }
    }

    if (showNewCaseDialog) {
        CreateCaseModalDialog(
            currentLanguage = currentLanguage,
            userRole = userRole,
            onDismiss = { showNewCaseDialog = false },
            onCreate = { title, category, incidentDate, opposingParty, estimatedAmount, summary, relief ->
                onCreateNewDispute(title, category, incidentDate, opposingParty, estimatedAmount, summary, relief, userRole)
                showNewCaseDialog = false
            }
        )
    }
}

@Composable
private fun CaseCardWithTools(
    caseEntity: CaseEntity,
    currentLanguage: LanguagePreference,
    onOpenNavigator: () -> Unit,
    onOpenComplaint: () -> Unit,
    onOpenVault: () -> Unit,
    onOpenTimeline: () -> Unit,
    onOpenCounsel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(shape = RoundedCornerShape(6.dp), color = SoftNavyContainer) {
                    Text(
                        text = if (isTa) caseEntity.disputeCategory.titleTa else caseEntity.disputeCategory.titleEn,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SovereignNavy),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "ID: " + caseEntity.caseId.take(8).uppercase(),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = caseEntity.title, style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = SovereignNavy), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (caseEntity.opposingParty != null) {
                        Text(text = "vs. " + caseEntity.opposingParty, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary))
                    }
                }
                CircularReadinessGauge(score = if (caseEntity.generatedComplaintDraft != null) 90 else 60, sizeDp = 48)
            }
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = if (isTa) "வழக்கு கருவிகள் (5 Tools):" else "Per-Case Action Suite:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                CaseToolButton(icon = Icons.AutoMirrored.Filled.Assignment, label = if (isTa) "படிகள்" else "Steps", onClick = onOpenNavigator, modifier = Modifier.weight(1f))
                CaseToolButton(icon = Icons.Default.Policy, label = if (isTa) "நோட்டீஸ்" else "Notice", onClick = onOpenComplaint, modifier = Modifier.weight(1f))
                CaseToolButton(icon = Icons.Default.Folder, label = if (isTa) "ஆவணங்கள்" else "Vault", onClick = onOpenVault, modifier = Modifier.weight(1f))
                CaseToolButton(icon = Icons.Default.Timeline, label = if (isTa) "காலவரிசை" else "Timeline", onClick = onOpenTimeline, modifier = Modifier.weight(1f))
                CaseToolButton(icon = Icons.Default.Shield, label = if (isTa) "வக்கீல்" else "Counsel", onClick = onOpenCounsel, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CaseToolButton(
    icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp), color = WarmCanvasBg, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)), modifier = modifier.clip(RoundedCornerShape(10.dp)).clickable { onClick() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)) {
            Icon(imageVector = icon, contentDescription = label, tint = SovereignNavy, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SovereignNavy), maxLines = 1)
        }
    }
}

@Composable
private fun CreateCaseModalDialog(
    currentLanguage: LanguagePreference, userRole: UserRole, onDismiss: () -> Unit,
    onCreate: (title: String, category: DisputeCategory, incidentDate: String?, opposingParty: String?, estimatedAmount: String?, summary: String?, relief: String?) -> Unit
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var title by remember { mutableStateOf("") }
    var opposingParty by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DisputeCategory.TENANCY_RENT) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (isTa) "புதிய சட்ட தகராறு பதிவு" else "Create New Dispute", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text(if (isTa) "வழக்கு தலைப்பு" else "Dispute Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = opposingParty, onValueChange = { opposingParty = it }, label = { Text(if (isTa) "எதிர் தரப்பினர் பெயர்" else "Opposing Party Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text(if (isTa) "கோரப்படும் தொகை" else "Claim Amount") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text(if (isTa) "வழக்கு சுருக்கம்" else "Factual Summary") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) { onCreate(title, selectedCategory, null, opposingParty.ifBlank { null }, amount.ifBlank { null }, summary.ifBlank { null }, null) } }, colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy)) {
                Text(if (isTa) "உருவாக்கு" else "Create Case", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isTa) "ரத்து" else "Cancel")
            }
        }
    )
}
