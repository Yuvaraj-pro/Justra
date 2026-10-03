import os

def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text.strip() + "\n")
    print("Created:", path)

# 5. SmartComplaintScreen.kt
smart_complaint = """package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.util.PdfExporter

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
                        PdfExporter.exportLegalNoticePdf(context, "Legal_Notice_.pdf", editableNoticeText)
                        Toast.makeText(context, if (isTa) "PDF ?? ?????????????????" else "Exported PDF to Downloads", Toast.LENGTH_SHORT).show()
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

# 6. LimitationRemindersScreen.kt
limitation_reminders = """package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.SovereignNavy
import com.example.ui.viewmodel.NyayaMateViewModel

data class LimitationClockItem(
    val id: String,
    val actNameEn: String,
    val actNameTa: String,
    val limitDays: Int,
    val descriptionEn: String,
    val descriptionTa: String
)

val LIMITATION_CLOCKS = listOf(
    LimitationClockItem(
        "sec138",
        "Sec 138 NI Act (Cheque Bounce)",
        "?????? 138 NI ?????? (?????? ???????????)",
        15,
        "15-day statutory notice cure window after cheque return memo",
        "?????? ????????? 15 ????????????? ??????????? ????????? ?????????? ????????"
    ),
    LimitationClockItem(
        "rti30",
        "RTI Act 2005 First Appeal",
        "????? ??????? ????? ?????? 2005",
        30,
        "30-day statutory response clock for Public Information Officer (PIO)",
        "30 ????????????? ????? ?????????????????? ????? ???????????? ?????????"
    ),
    LimitationClockItem(
        "mvact",
        "Motor Vehicles Act Claim",
        "???????? ???? ?????? ????????",
        180,
        "6-month limitation window for filing MACT accident claim petition",
        "??????? ????? 6 ?????????????? MACT ??? ??????? ????????? ????????"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitationRemindersScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTa) "??????????? ????????? ??????????????" else "Limitation Clocks & Reminders") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(LIMITATION_CLOCKS) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isTa) item.actNameTa else item.actNameEn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy
                                )
                            )
                            Badge(containerColor = Color(0xFFE5A93C)) {
                                Text(" Days", color = SovereignNavy, modifier = Modifier.padding(4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isTa) item.descriptionTa else item.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    if (isTa) "??????????? ?????? ?????????????? ( ???????)"
                                    else "Limitation Alarm Set for  Days",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "+ ??????????? ???" else "+ Set Limitation Reminder Alarm")
                        }
                    }
                }
            }
        }
    }
}
"""
write("app/src/main/java/com/example/ui/screens/LimitationRemindersScreen.kt", limitation_reminders)
