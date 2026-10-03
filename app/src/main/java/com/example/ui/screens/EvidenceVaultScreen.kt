package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CaseEntity
import com.example.data.local.EvidenceArtifactEntity
import com.example.domain.model.EvidenceCategory
import com.example.domain.model.LanguagePreference
import com.example.ui.components.NyayaTopBar
import com.example.ui.theme.DeepIndigoSlatePrimary
import com.example.ui.theme.PaleSandstoneVariant
import com.example.ui.theme.PrimaryContainerSlate
import com.example.ui.theme.SageGreenSuccessContainer
import com.example.ui.theme.SageGreenSuccessText
import com.example.ui.theme.SaffronAmberWarningContainer
import com.example.ui.theme.SaffronAmberWarningText
import com.example.ui.theme.TerracottaAccentSecondary
import com.example.ui.theme.WarmIvorySurface
import com.example.ui.theme.nyayaOutlinedTextFieldColors
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceVaultScreen(
    currentLanguage: LanguagePreference,
    caseEntity: CaseEntity?,
    artifactsFlow: Flow<List<EvidenceArtifactEntity>>,
    onAddArtifact: (name: String, category: EvidenceCategory, notes: String?) -> Unit,
    onDeleteArtifact: (artifactId: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val artifacts by artifactsFlow.collectAsState(initial = emptyList())
    var selectedCategoryFilter by remember { mutableStateOf<EvidenceCategory?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog state
    var artifactName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EvidenceCategory.PAYMENT_PROOF) }
    var artifactNotes by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val filteredArtifacts = if (selectedCategoryFilter == null) artifacts else artifacts.filter { it.category == selectedCategoryFilter }

    // Check missing proofs
    val presentCategories = artifacts.map { it.category }.toSet()
    val missingPayment = !presentCategories.contains(EvidenceCategory.PAYMENT_PROOF)
    val missingWritten = !presentCategories.contains(EvidenceCategory.WRITTEN_COMMUNICATION)

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (currentLanguage == LanguagePreference.TAMIL) "சான்றுகள் பெட்டகம் (Vault)" else "Encrypted Evidence Vault",
                subtitle = caseEntity?.title ?: "Integrity Verification (IT Act Sec 65B)",
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
                modifier = Modifier.testTag("add_artifact_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Artifact")
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
            // 1. Vault Hardware SHA-256 Integrity Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = DeepIndigoSlatePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "டிஜிட்டல் ஒருமைப்பாடு சான்றிதழ்" else "Cryptographic Vault Sealing",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            Text(
                                text = "Every artifact is hashed via SHA-256 upon intake. Admissible in Indian Courts under Bharatiya Sakshya Adhiniyam.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // 2. Missing Evidence Warning Pills if any
            if (missingPayment || missingWritten) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SaffronAmberWarningContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SaffronAmberWarningText.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = SaffronAmberWarningText, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "முக்கிய சான்றுகள் விடுபட்டுள்ளன:" else "Recommended Missing Evidentiary Artifacts:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronAmberWarningText
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (missingPayment) {
                                Text("• Missing: UPI / Bank Transaction UTR Statement / Invoice Receipt", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = SaffronAmberWarningText))
                            }
                            if (missingWritten) {
                                Text("• Missing: Written Notice / Email Demand / WhatsApp Grievance", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = SaffronAmberWarningText))
                            }
                        }
                    }
                }
            }

            // 3. Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All (${artifacts.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepIndigoSlatePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(EvidenceCategory.values().toList()) { cat ->
                        val count = artifacts.count { it.category == cat }
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat },
                            label = { Text("${if (currentLanguage == LanguagePreference.TAMIL) cat.labelTa else cat.labelEn.take(15)} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepIndigoSlatePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 4. Artifacts List
            if (filteredArtifacts.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "சான்றுகள் எதுவும் இல்லை. புதிய ஆவணத்தை சேர்க்கவும் (+)." else "No evidence artifacts found in this category. Tap (+) to securely add.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredArtifacts) { artifact ->
                    ArtifactCard(
                        artifact = artifact,
                        currentLanguage = currentLanguage,
                        onDelete = { onDeleteArtifact(artifact.artifactId) },
                        modifier = Modifier.testTag("artifact_item_${artifact.artifactId.take(6)}")
                    )
                }
            }
        }
    }

    // Add Artifact Modal Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == LanguagePreference.TAMIL) "புதிய சான்றை சேர்க்க" else "Add Evidentiary Artifact to Vault",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = artifactName,
                        onValueChange = { artifactName = it },
                        label = { Text(if (currentLanguage == LanguagePreference.TAMIL) "ஆவணப் பெயர் / தலைப்பு" else "Document Title (e.g. Bank Statement)") },
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("artifact_name_input")
                    )

                    // Category Selector
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (currentLanguage == LanguagePreference.TAMIL) selectedCategory.labelTa else selectedCategory.labelEn,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (currentLanguage == LanguagePreference.TAMIL) "சான்று வகை" else "Evidence Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            EvidenceCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(if (currentLanguage == LanguagePreference.TAMIL) cat.labelTa else cat.labelEn) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = artifactNotes,
                        onValueChange = { artifactNotes = it },
                        label = { Text(if (currentLanguage == LanguagePreference.TAMIL) "குறிப்புகள் (விருப்பத்தேர்வு)" else "Explanatory Notes (Optional)") },
                        maxLines = 2,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("artifact_notes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = if (artifactName.isNotBlank()) artifactName else "Evidence Artifact"
                        onAddArtifact(name, selectedCategory, artifactNotes.ifBlank { null })
                        showAddDialog = false
                        artifactName = ""
                        artifactNotes = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                    modifier = Modifier.testTag("confirm_add_artifact_button")
                ) {
                    Text(if (currentLanguage == LanguagePreference.TAMIL) "சேர்க்க" else "Save Artifact")
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
private fun ArtifactCard(
    artifact: EvidenceArtifactEntity,
    currentLanguage: LanguagePreference,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(artifact.uploadTimestamp))
    val (icon, tint) = when (artifact.category) {
        EvidenceCategory.PAYMENT_PROOF -> Icons.Default.Payment to TerracottaAccentSecondary
        EvidenceCategory.WRITTEN_COMMUNICATION -> Icons.Default.Description to DeepIndigoSlatePrimary
        EvidenceCategory.DAMAGE_PHOTO -> Icons.Default.Photo to TerracottaAccentSecondary
        else -> Icons.Default.Shield to DeepIndigoSlatePrimary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                    Text(
                        text = artifact.fileName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = if (currentLanguage == LanguagePreference.TAMIL) artifact.category.labelTa else artifact.category.labelEn,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = DeepIndigoSlatePrimary
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            if (!artifact.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = artifact.notes,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SHA-256 Hash and Timestamp Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = SageGreenSuccessText,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "SHA-256: ${artifact.sha256Hash.take(12)}...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SageGreenSuccessText
                        )
                    )
                }

                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
