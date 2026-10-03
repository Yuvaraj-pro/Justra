package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.api.LegalStatuteKnowledge
import com.example.data.local.CaseEntity
import com.example.domain.model.LanguagePreference
import com.example.domain.model.UserRole
import com.example.ui.components.NyayaTopBar
import com.example.ui.theme.DeepIndigoSlatePrimary
import com.example.ui.theme.PaleSandstoneVariant
import com.example.ui.theme.PrimaryContainerSlate
import com.example.ui.theme.SaffronAmberWarningContainer
import com.example.ui.theme.SaffronAmberWarningText
import com.example.ui.theme.TerracottaAccentSecondary
import com.example.ui.theme.WarmIvorySurface
import com.example.ui.theme.nyayaOutlinedTextFieldColors
import java.io.File

@Composable
fun ComplaintGeneratorScreen(
    currentLanguage: LanguagePreference,
    caseEntity: CaseEntity?,
    userRole: UserRole = UserRole.CITIZEN,
    isGeneratingPdf: Boolean,
    lastGeneratedPdf: File?,
    onSaveComplaintDraft: (updatedText: String) -> Unit,
    onSwitchRoleAndRegenerate: (UserRole) -> Unit = {},
    onExportPdf: (context: Context, draftText: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Editor, 1: Formatted Preview
    var complaintText by remember(caseEntity?.generatedComplaintDraft) {
        mutableStateOf(caseEntity?.generatedComplaintDraft ?: "")
    }

    val statute = caseEntity?.let { LegalStatuteKnowledge.getStatuteForCategory(it.disputeCategory) }

    // Validation warnings for missing fields
    val missingItems = mutableListOf<String>()
    if (caseEntity?.opposingParty.isNullOrBlank()) missingItems.add("Opposing Party / Company Name")
    if (caseEntity?.incidentDate.isNullOrBlank()) missingItems.add("Incident / Transaction Date")
    if (caseEntity?.estimatedClaimAmount.isNullOrBlank()) missingItems.add("Quantified Loss / Disputed Amount")

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (currentLanguage == LanguagePreference.TAMIL) "புகார் மனு ஜெனரேட்டர்" else "Smart Complaint Generator",
                subtitle = caseEntity?.title ?: "Formal Legal Notice Draft",
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("complaint_screen_snackbar_host")
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DeepIndigoSlatePrimary,
                    contentColor = WarmIvorySurface,
                    actionColor = TerracottaAccentSecondary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Role / Legal Persona Selector & Regenerator Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PaleSandstoneVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complaint_role_selector_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = when (userRole) {
                                    UserRole.CITIZEN -> "🛡️"
                                    UserRole.LEGAL_COUNSEL -> "⚖️"
                                    UserRole.MSME_BUSINESS -> "🏢"
                                    UserRole.CYBER_FRAUD_VICTIM -> "🚨"
                                },
                                fontSize = 16.sp
                            )
                            Column {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "சட்ட மனு நிலை:" else "Active Legal Persona Draft:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) userRole.titleTa else userRole.titleEn,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepIndigoSlatePrimary
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryContainerSlate
                        ) {
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) userRole.badgeTa else userRole.badgeEn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (currentLanguage == LanguagePreference.TAMIL) "வேறு நிலைக்கு மாற்றுக (வழக்கறிஞர்/MSME/சைபர்):" else "Regenerate draft under a different legal standing:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = userRole == role
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) DeepIndigoSlatePrimary else PrimaryContainerSlate,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) DeepIndigoSlatePrimary else PaleSandstoneVariant
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onSwitchRoleAndRegenerate(role)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = if (currentLanguage == LanguagePreference.TAMIL) "புதிய சட்ட ஆவண வரைவு உருவாக்கப்பட்டது" else "New legal complaint draft generated",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                    .testTag("complaint_role_chip_${role.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (role) {
                                            UserRole.CITIZEN -> if (currentLanguage == LanguagePreference.TAMIL) "குடிமகன்" else "Citizen"
                                            UserRole.LEGAL_COUNSEL -> if (currentLanguage == LanguagePreference.TAMIL) "வக்கீல்" else "Counsel"
                                            UserRole.MSME_BUSINESS -> if (currentLanguage == LanguagePreference.TAMIL) "MSME" else "MSME"
                                            UserRole.CYBER_FRAUD_VICTIM -> if (currentLanguage == LanguagePreference.TAMIL) "சைபர்" else "Cyber"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else DeepIndigoSlatePrimary
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Statute Grounding Banner
            if (statute != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = DeepIndigoSlatePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Ground: ${statute.actName} (${statute.section})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Forum: ${statute.applicableForum}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }

            // Incomplete field warning
            if (missingItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SaffronAmberWarningContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SaffronAmberWarningText.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = SaffronAmberWarningText,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "முக்கிய விவரங்கள் விடுபட்டுள்ளன:" else "Advisory: Missing details in intake:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronAmberWarningText
                                )
                            )
                            Text(
                                text = missingItems.joinToString(", "),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = SaffronAmberWarningText
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector (Editor vs Formatted Preview)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PaleSandstoneVariant,
                contentColor = DeepIndigoSlatePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = TerracottaAccentSecondary
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (currentLanguage == LanguagePreference.TAMIL) "மனு திருத்து" else "Draft Editor")
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Preview, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (currentLanguage == LanguagePreference.TAMIL) "முன்னோட்டம்" else "Page Preview")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Draft Editor
                OutlinedTextField(
                    value = complaintText,
                    onValueChange = {
                        complaintText = it
                        onSaveComplaintDraft(it)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif,
                        lineHeight = 22.sp,
                        color = Color(0xFF14181F)
                    ),
                    colors = nyayaOutlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .testTag("complaint_draft_text_field")
                )
            } else {
                // Formatted Preview Layout (A4 styled card)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "FORMAL LEGAL GRIEVANCE PETITION",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = "Digitally Structured under Indian Evidence & IT Act (Sec 65B)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = TerracottaAccentSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = complaintText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Serif,
                                lineHeight = 20.sp,
                                color = Color(0xFF1E1E1E)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Export PDF Button
                Button(
                    onClick = {
                        if (caseEntity != null) {
                            onExportPdf(context, complaintText)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (currentLanguage == LanguagePreference.TAMIL) "சட்ட ஆவணம் PDF ஆக உருவாக்கப்படுகிறது..." else "Generating formal legal PDF...",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("export_pdf_button")
                ) {
                    if (isGeneratingPdf) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "PDF பதிவிறக்கு" else "Export PDF",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // Copy Text Button
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(complaintText))
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("copy_complaint_text_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = DeepIndigoSlatePrimary, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (currentLanguage == LanguagePreference.TAMIL) "உரையை நகலெடு" else "Copy Text",
                            style = MaterialTheme.typography.labelLarge.copy(color = DeepIndigoSlatePrimary)
                        )
                    }
                }
            }

            // Share Generated PDF if available
            if (lastGeneratedPdf != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryContainerSlate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            try {
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    lastGeneratedPdf
                                )
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Grievance PDF"))
                            } catch (_: Exception) {}
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = DeepIndigoSlatePrimary)
                        Column {
                            Text(
                                text = "PDF Ready: ${lastGeneratedPdf.name}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                            )
                            Text(
                                text = "Tap to share via Email / WhatsApp / Speed Post Print",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
