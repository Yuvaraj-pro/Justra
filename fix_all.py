import os

def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text.strip() + "\n")
    print("Fixed:", path)

# 1. AuthScreen.kt
auth_screen = """package com.example.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.domain.model.LanguagePreference
import com.example.util.BiometricAuthHelper
import com.example.util.SecurityPreferences

private fun Context.findFragmentActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun AuthScreen(
    currentLanguage: LanguagePreference,
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val secPrefs = remember(context) { SecurityPreferences.getInstance(context) }
    val isTa = currentLanguage == LanguagePreference.TAMIL

    var pinState by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isBiometricAvailable by remember {
        mutableStateOf(BiometricAuthHelper.checkBiometricStatus(context) == BiometricAuthHelper.BiometricCapability.AVAILABLE)
    }

    val triggerBiometric = {
        if (activity != null && isBiometricAvailable) {
            BiometricAuthHelper.launchBiometricPrompt(
                activity = activity,
                language = currentLanguage,
                onSuccess = { onAuthenticated() },
                onError = { _, err -> errorMessage = err },
                onNegativeButtonTapped = { }
            )
        }
    }

    LaunchedEffect(Unit) {
        if (isBiometricAvailable) {
            triggerBiometric()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F1E36)
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5A93C).copy(alpha = 0.15f))
                        .border(2.dp, Color(0xFFE5A93C), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFE5A93C),
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isTa) "??????????? ??????" else "JUSTRA SECURE VAULT",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFAF7F2),
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = if (isTa) "??????????? ?????? PIN ????? ????????" else "Authenticate with Biometrics or Security PIN",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFAF7F2).copy(alpha = 0.7f)),
                    textAlign = TextAlign.Center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    repeat(6) { index ->
                        val isFilled = index < pinState.length
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) Color(0xFFE5A93C) else Color.White.copy(alpha = 0.2f)
                                )
                                .border(
                                    1.dp,
                                    if (isFilled) Color(0xFFE5A93C) else Color.White.copy(alpha = 0.4f),
                                    CircleShape
                                )
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFF6B6B)),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val digits = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BIO", "0", "DEL")
                )

                digits.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        row.forEach { key ->
                            Surface(
                                onClick = {
                                    errorMessage = null
                                    when (key) {
                                        "DEL" -> if (pinState.isNotEmpty()) pinState = pinState.dropLast(1)
                                        "BIO" -> triggerBiometric()
                                        else -> {
                                            if (pinState.length < 6) {
                                                val nextPin = pinState + key
                                                pinState = nextPin
                                                if (nextPin.length >= 4) {
                                                    if (secPrefs.hasEnrolledPin()) {
                                                        if (secPrefs.verifyPin(nextPin)) {
                                                            onAuthenticated()
                                                        } else if (nextPin.length == 6) {
                                                            errorMessage = if (isTa) "????? PIN. ???????? ??????????????." else "Incorrect PIN. Try again."
                                                            pinState = ""
                                                        }
                                                    } else {
                                                        secPrefs.savePin(nextPin)
                                                        Toast.makeText(context, if (isTa) "PIN ??????????? ???????????????" else "PIN Enrolled Successfully", Toast.LENGTH_SHORT).show()
                                                        onAuthenticated()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.1f),
                                contentColor = Color(0xFFFAF7F2),
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    when (key) {
                                        "BIO" -> Icon(Icons.Default.Fingerprint, contentDescription = "Biometric", tint = Color(0xFFE5A93C))
                                        "DEL" -> Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Color(0xFFFAF7F2))
                                        else -> Text(text = key, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
"""
write("app/src/main/java/com/example/ui/screens/AuthScreen.kt", auth_screen)

# 2. NewGrievanceScreen.kt
new_grievance = """package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.DisputeCategory
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.SovereignNavy
import com.example.ui.viewmodel.NyayaMateViewModel
import com.example.util.LegalInputValidator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewGrievanceScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onBackClick: () -> Unit,
    onCreated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    var title by remember { mutableStateOf("") }
    var opposingParty by remember { mutableStateOf("") }
    var monetaryClaim by remember { mutableStateOf("") }
    var incidentDate by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }

    val personas = listOf(
        "Citizen Complainant" to "???????? ???????????",
        "Advocate/Counsel" to "??????????",
        "MSME Owner" to "MSME ??????????",
        "Cyber Victim" to "????? ????????????????"
    )
    var selectedPersona by remember { mutableStateOf(personas[0].first) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTa) "????? ?????? ?????? ??????????" else "Initiate Legal Grievance") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SovereignNavy,
                    titleContentColor = Color(0xFFFAF7F2),
                    navigationIconContentColor = Color(0xFFFAF7F2)
                )
            )
        },
        containerColor = Color(0xFFFAF7F2)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "??????????? ???? (Legal Standing)" else "Select Legal Persona",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        personas.forEach { (en, ta) ->
                            val isSelected = selectedPersona == en
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPersona = en },
                                label = { Text(if (isTa) ta else en) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SovereignNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (isTa) "?????? ??????? *" else "Dispute Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = opposingParty,
                        onValueChange = { opposingParty = it },
                        label = { Text(if (isTa) "????????????? ????? *" else "Opposing Party Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = monetaryClaim,
                            onValueChange = { monetaryClaim = it },
                            label = { Text(if (isTa) "???????????? ???? (?)" else "Monetary Claim (?)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = incidentDate,
                            onValueChange = { incidentDate = it },
                            label = { Text(if (isTa) "????? ????" else "Incident Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text(if (isTa) "????? ????????? (????????? 12 ??????????) *" else "Factual Incident Summary (Min 12 chars) *") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (title.isBlank() || opposingParty.isBlank()) {
                                errorMessage = if (isTa) "?????????? ??????? ?????? ???????????? ??????????" else "Please fill all required fields"
                                return@Button
                            }
                            val valRes = LegalInputValidator.validateLegalInput(summary)
                            if (valRes is LegalInputValidator.ValidationResult.Invalid) {
                                errorMessage = if (isTa) valRes.errorMessageTa else valRes.errorMessageEn
                                return@Button
                            }
                            viewModel.createNewDispute(
                                title = title,
                                category = DisputeCategory.CONSUMER_GRIEVANCE,
                                incidentDate = incidentDate.ifBlank { null },
                                opposingParty = opposingParty.ifBlank { null },
                                estimatedClaimAmount = monetaryClaim.ifBlank { null },
                                factualSummary = summary,
                                demandedRelief = "Full refund/compensation",
                                onCreated = { caseId ->
                                    Toast.makeText(context, if (isTa) "?????? ??????????? ??????????????" else "Grievance Recorded", Toast.LENGTH_SHORT).show()
                                    onCreated(caseId)
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isTa) "?????? ????? ????? & ????? ????? ?????" else "Submit & Proceed to Smart Complaint")
                    }
                }
            }
        }
    }
}
"""
write("app/src/main/java/com/example/ui/screens/NewGrievanceScreen.kt", new_grievance)

# 3. SmartComplaintScreen.kt
smart_complaint = """package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.SovereignNavy
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartComplaintScreen(
    caseId: String,
    currentLanguage: LanguagePreference,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    var activeTab by remember { mutableStateOf(0) }
    var editableNoticeText by remember {
        mutableStateOf(
            \"\"\"LEGAL NOTICE UNDER CONSUMER PROTECTION ACT 2019 / BNS 2023
                |
                |TO: OPPOSING PARTY NAME
                |ADDRESS: [Insert Opposite Party Address]
                |
                |DEMAND STATEMENT:
                |Take notice that my client hereby demands immediate redressal of the grievance detailed herein.
                |Factual Summary: Breach of statutory obligation and non-delivery of agreed services.
                |
                |GROUND STATUTES:
                |- Section 35, Consumer Protection Act, 2019 (District Commission Jurisdiction)
                |- Section 303, Bharatiya Nyaya Sanhita (BNS), 2023
                |
                |RELIEF SOUGHT: Full refund of ?50,000 along with statutory 18% p.a. interest.
                |
                |Failing compliance within 15 days, appropriate legal proceedings will be initiated.
            \"\"\".trimMargin()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTa) "????????? ?????? ?????? ?????" else "Smart Complaint Generator") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SovereignNavy,
                    titleContentColor = Color(0xFFFAF7F2),
                    navigationIconContentColor = Color(0xFFFAF7F2)
                )
            )
        },
        containerColor = Color(0xFFFAF7F2)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                border = BorderStroke(1.dp, Color(0xFFFFEEBA)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF856404))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTa) "???????: ??????????? ?????? ??????? UTR ??????????? ?????? ??????????? ????????."
                        else "Advisory: Ensure Opposing Party address and UTR Payment Proof are attached in Vault.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF856404))
                    )
                }
            }

            TabRow(selectedTabIndex = activeTab, containerColor = Color.White) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text(if (isTa) "????? ????????" else "Draft Editor", modifier = Modifier.padding(12.dp))
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text(if (isTa) "????????? ???????" else "Page Preview", modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (activeTab == 0) {
                OutlinedTextField(
                    value = editableNoticeText,
                    onValueChange = { editableNoticeText = it },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = editableNoticeText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Legal Notice", editableNoticeText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, if (isTa) "?????????????????" else "Notice Text Copied", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTa) "??????" else "Copy Text")
                }

                Button(
                    onClick = {
                        try {
                            val pdfDocument = PdfDocument()
                            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                            val page = pdfDocument.startPage(pageInfo)
                            val canvas = page.canvas
                            val paint = Paint().apply {
                                color = AndroidColor.BLACK
                                textSize = 12f
                            }
                            var y = 40f
                            editableNoticeText.split("\n").forEach { line ->
                                canvas.drawText(line, 40f, y, paint)
                                y += 18f
                            }
                            pdfDocument.finishPage(page)
                            val outFile = File(context.cacheDir, "Legal_Notice_.pdf")
                            val fos = FileOutputStream(outFile)
                            pdfDocument.writeTo(fos)
                            fos.close()
                            pdfDocument.close()
                            Toast.makeText(context, if (isTa) "PDF ??????????? ????????????????" else "PDF Exported Successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "PDF Error: ", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTa) "PDF ?????" else "Export PDF")
                }
            }
        }
    }
}
"""
write("app/src/main/java/com/example/ui/screens/SmartComplaintScreen.kt", smart_complaint)

print("Fixed Screens!")
