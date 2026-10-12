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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.data.local.CaseEntity
import com.justra.app.data.local.EvidenceArtifactEntity
import com.justra.app.domain.model.EvidenceCategory
import com.justra.app.domain.model.LanguagePreference
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
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
    onAddArtifact: (name: String, category: EvidenceCategory, notes: String?, fileBytes: ByteArray?, mimeType: String?) -> Unit,
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

    // Camera & Document Picker State
    var selectedFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedMimeType by remember { mutableStateOf<String?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var showSourcePicker by remember { mutableStateOf(false) }
    var showCameraPermissionDialog by remember { mutableStateOf(false) }
    var showDocumentPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Calculate SHA-256 hash of file content
    fun calculateFileSha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    // Save file to app private storage and return path
    fun saveFileToPrivateStorage(bytes: ByteArray, fileName: String): String? {
        return try {
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { it.write(bytes) }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    // Handle file selection result — declared BEFORE launchers that reference it
    fun onFileSelected(uri: android.net.Uri, mimeType: String?) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            val ext = when {
                mimeType?.contains("image") == true -> ".jpg"
                mimeType?.contains("pdf") == true -> ".pdf"
                else -> ".bin"
            }
            val fileName = "evidence_${System.currentTimeMillis()}$ext"
            selectedFileBytes = bytes
            selectedMimeType = mimeType ?: "application/octet-stream"
            selectedFileName = fileName
            // Auto-fill artifact name from file name without extension
            artifactName = fileName.removeSuffix(ext)
            showAddDialog = true
        }
    }

    // Activity Result Launchers (must be at top level of composable)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.extras?.get("data")?.let { bitmap ->
                val bytes = ByteArrayOutputStream().apply {
                    (bitmap as android.graphics.Bitmap).compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, this)
                }.toByteArray()
                val fileName = "photo_${System.currentTimeMillis()}.jpg"
                selectedFileBytes = bytes
                selectedMimeType = "image/jpeg"
                selectedFileName = fileName
                artifactName = "Camera Photo ${SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date())}"
                showAddDialog = true
            }
        }
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val mimeType = context.contentResolver.getType(uri)
                onFileSelected(uri, mimeType)
            }
        }
    }

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
                onClick = { showSourcePicker = true },
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
                        border = BorderStroke(1.dp, SaffronAmberWarningText.copy(alpha = 0.3f)),
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

    // Source Picker Dialog (Camera vs Document)
    if (showSourcePicker) {
        AlertDialog(
            onDismissRequest = { showSourcePicker = false },
            title = {
                Text(
                    text = if (currentLanguage == LanguagePreference.TAMIL) "சான்று மூலத்தைத் தேர்வு செய்க" else "Select Evidence Source",
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
                    // Camera Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .clickable {
                                showSourcePicker = false
                                val intent = android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    cameraLauncher.launch(intent)
                                } else {
                                    showCameraPermissionDialog = true
                                }
                            }
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = DeepIndigoSlatePrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "கேமரா (புகைப்படம் எடுக்க)" else "Camera (Take Photo)",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                            )
                            Text(
                                text = if (currentLanguage == LanguagePreference.TAMIL) "உடனடி புகைப்படம் அல்லது ஸ்கேன்" else "Instant photo or document scan",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF4A4E57), modifier = Modifier.size(20.dp))
                    }

                    HorizontalDivider(color = Color(0xFF4A4E57).copy(alpha = 0.2f))

                    // Document/File Picker Option
                    OutlinedButton(
                        onClick = {
                            showSourcePicker = false
                            val intent = android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT).apply {
                                type = "*/*"
                                addCategory(android.content.Intent.CATEGORY_OPENABLE)
                                putExtra(android.content.Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf", "text/*", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                            }
                            documentLauncher.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("document_option_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = DeepIndigoSlatePrimary, modifier = Modifier.size(24.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "ஆவணம் / கோப்பு தேர்வு (PDF, Image, Doc)" else "Document / File Picker (PDF, Image, Doc)",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                                )
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "கொடுக்குள் சேமித்திருக்கும் ஆவணத்தைத் தேர்வு செய்க" else "Select existing files from device storage",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF4A4E57), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourcePicker = false }) {
                    Text(if (currentLanguage == LanguagePreference.TAMIL) "ரத்து" else "Cancel")
                }
            },
            containerColor = WarmIvorySurface
        )
    }

    // Camera Permission Dialog
    if (showCameraPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showCameraPermissionDialog = false },
            title = { Text(if (currentLanguage == LanguagePreference.TAMIL) "கேமரா அனுமதி தேவை" else "Camera Permission Required") },
            text = { Text(if (currentLanguage == LanguagePreference.TAMIL) "புகைப்படம் எடுக்க கேமரா அணுகல் அனுமதிக்கவும்." else "Please grant camera permission to capture photos.") },
            confirmButton = {
                Button(onClick = {
                    showCameraPermissionDialog = false
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }) { Text(if (currentLanguage == LanguagePreference.TAMIL) "அனுமதி வழங்கு" else "Grant Permission") }
            },
            dismissButton = {
                TextButton(onClick = { showCameraPermissionDialog = false }) { Text(if (currentLanguage == LanguagePreference.TAMIL) "ரத்து" else "Cancel") }
            },
            containerColor = WarmIvorySurface
        )
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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
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
                        val fileHash = selectedFileBytes?.let { calculateFileSha256(it) }
                        val filePath = selectedFileBytes?.let { bytes ->
                            saveFileToPrivateStorage(bytes, selectedFileName ?: "evidence_${System.currentTimeMillis()}.bin")
                        }
                        onAddArtifact(
                            name,
                            selectedCategory,
                            artifactNotes.ifBlank { null },
                            selectedFileBytes,
                            selectedMimeType
                        )
                        showAddDialog = false
                        artifactName = ""
                        artifactNotes = ""
                        selectedFileBytes = null
                        selectedMimeType = null
                        selectedFileName = null
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
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