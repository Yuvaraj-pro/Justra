package com.justra.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.data.local.CaseEntity
import com.justra.app.data.local.TimelineEventEntity
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.ReadinessMetric
import com.justra.app.ui.components.CircularReadinessGauge
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.SaffronAmberWarningContainer
import com.justra.app.ui.theme.SaffronAmberWarningText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import kotlinx.coroutines.flow.Flow

@Composable
fun TimelineReadinessScreen(
    currentLanguage: LanguagePreference,
    caseEntity: CaseEntity?,
    eventsFlow: Flow<List<TimelineEventEntity>>,
    evidenceCountFlow: Flow<Int>,
    onAddEvent: (date: String, title: String, description: String, isInferred: Boolean) -> Unit,
    onDeleteEvent: (eventId: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by eventsFlow.collectAsState(initial = emptyList())
    val evidenceCount by evidenceCountFlow.collectAsState(initial = 0)
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog state
    var eventDate by remember { mutableStateOf("") }
    var eventTitle by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }

    // Compute Readiness Score (0 - 100)
    var score = 0
    val hasChronology = events.isNotEmpty()
    if (hasChronology) score += 20

    val hasEvidence = evidenceCount > 0
    if (hasEvidence) score += 25

    val hasOpposing = !caseEntity?.opposingParty.isNullOrBlank()
    if (hasOpposing) score += 20

    val hasNotice = caseEntity?.generatedComplaintDraft?.isNotBlank() == true
    if (hasNotice) score += 15

    val hasStatute = caseEntity?.disputeCategory != null
    if (hasStatute) score += 20

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (currentLanguage == LanguagePreference.TAMIL) "காலவரிசை & தயார்நிலை" else "Timeline & Case Readiness",
                subtitle = caseEntity?.title ?: "Chronological Verification",
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TerracottaAccentSecondary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_timeline_event_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        },
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Overall Readiness Gauge Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularReadinessGauge(score = score, sizeDp = 84)

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (score >= 80) "Court / Forum Ready" else if (score >= 50) "Substantial Readiness" else "Intake in Progress",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL)
                                    "நீதிமன்றம் அல்லது தீர்ப்பாயத்தில் முறையீடு செய்வதற்கான முழுமையான சான்று நிலை."
                                else
                                    "Comprehensive readiness score based on documented facts, preserved proof & statutory mapping.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // 2. Readiness Metric Breakdown Checklist
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (currentLanguage == LanguagePreference.TAMIL) "தயார்நிலை சரிபார்ப்பு பட்டியல்" else "Readiness Verification Checklist",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        MetricRow(
                            title = if (currentLanguage == LanguagePreference.TAMIL) ReadinessMetric.FACTUAL_CHRONOLOGY.titleTa else ReadinessMetric.FACTUAL_CHRONOLOGY.titleEn,
                            isVerified = hasChronology,
                            weight = 20
                        )
                        MetricRow(
                            title = if (currentLanguage == LanguagePreference.TAMIL) ReadinessMetric.PAYMENT_OR_LOSS_PROOF.titleTa else ReadinessMetric.PAYMENT_OR_LOSS_PROOF.titleEn,
                            isVerified = hasEvidence,
                            weight = 25
                        )
                        MetricRow(
                            title = if (currentLanguage == LanguagePreference.TAMIL) ReadinessMetric.OPPOSING_PARTY_IDENTIFIED.titleTa else ReadinessMetric.OPPOSING_PARTY_IDENTIFIED.titleEn,
                            isVerified = hasOpposing,
                            weight = 20
                        )
                        MetricRow(
                            title = if (currentLanguage == LanguagePreference.TAMIL) ReadinessMetric.FORMAL_NOTICE_SENT.titleTa else ReadinessMetric.FORMAL_NOTICE_SENT.titleEn,
                            isVerified = hasNotice,
                            weight = 15
                        )
                        MetricRow(
                            title = if (currentLanguage == LanguagePreference.TAMIL) ReadinessMetric.STATUTORY_JURISDICTION_MAPPED.titleTa else ReadinessMetric.STATUTORY_JURISDICTION_MAPPED.titleEn,
                            isVerified = hasStatute,
                            weight = 20
                        )
                    }
                }
            }

            // 3. Timeline Events Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (currentLanguage == LanguagePreference.TAMIL) "சம்பவங்களின் காலவரிசை" else "Chronological Fact Chain",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                    Text(
                        text = "${events.size} Nodes",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                    )
                }
            }

            // 4. Events Timeline List
            if (events.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "காலவரிசை நிகழ்வுகள் எதுவும் இல்லை. (+) பொத்தானை அழுத்தி புதிய நிகழ்வை சேர்க்கவும்." else "No chronological events recorded. Tap (+) to anchor an incident date.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            } else {
                items(events) { event ->
                    TimelineNodeCard(
                        event = event,
                        currentLanguage = currentLanguage,
                        onDelete = { onDeleteEvent(event.eventId) }
                    )
                }
            }
        }
    }

    // Add Timeline Event Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == LanguagePreference.TAMIL) "புதிய காலவரிசை நிகழ்வை சேர்க்க" else "Add Timeline Incident Node",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = { eventDate = it },
                        label = { Text(if (currentLanguage == LanguagePreference.TAMIL) "தேதி (எ.கா: 12/04/2024)" else "Date (e.g. 12/04/2024)") },
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text(if (currentLanguage == LanguagePreference.TAMIL) "நிகழ்வு தலைப்பு" else "Event Title (e.g. Payment transferred)") },
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eventDescription,
                        onValueChange = { eventDescription = it },
                        label = { Text(if (currentLanguage == LanguagePreference.TAMIL) "விளக்கம் / ஆதாரம்" else "Description / Evidence Context") },
                        maxLines = 3,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val date = if (eventDate.isNotBlank()) eventDate else "Current Date"
                        val title = if (eventTitle.isNotBlank()) eventTitle else "Fact Event"
                        onAddEvent(date, title, eventDescription, false)
                        showAddDialog = false
                        eventDate = ""
                        eventTitle = ""
                        eventDescription = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary)
                ) {
                    Text(if (currentLanguage == LanguagePreference.TAMIL) "சேர்க்க" else "Add Node")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(if (currentLanguage == LanguagePreference.TAMIL) "ரத்து" else "Cancel")
                }
            },
            containerColor = WarmIvorySurface
        )
    }
}

@Composable
private fun MetricRow(
    title: String,
    isVerified: Boolean,
    weight: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = null,
                tint = if (isVerified) SageGreenSuccessText else SaffronAmberWarningText,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isVerified) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isVerified) DeepIndigoSlatePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Text(
            text = "+$weight%",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isVerified) SageGreenSuccessText else Color.Gray
            )
        )
    }
}

@Composable
private fun TimelineNodeCard(
    event: TimelineEventEntity,
    currentLanguage: LanguagePreference,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Node circle & line
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 12.dp, top = 2.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (event.isInferred) SaffronAmberWarningContainer else DeepIndigoSlatePrimary,
                    modifier = Modifier.size(16.dp)
                ) {}
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.eventDate,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TerracottaAccentSecondary
                        )
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = event.eventTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    )
                )

                if (event.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (event.sourceReference != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Source: ${event.sourceReference}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = DeepIndigoSlatePrimary.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }
}
