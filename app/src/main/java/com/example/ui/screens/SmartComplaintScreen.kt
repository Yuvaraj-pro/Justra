package com.example.ui.screens

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
            "LEGAL NOTICE UNDER CONSUMER PROTECTION ACT 2019 / BNS 2023\n\n" +
            "TO: OPPOSING PARTY NAME\n" +
            "ADDRESS: [Insert Opposite Party Address]\n\n" +
            "DEMAND STATEMENT:\n" +
            "Take notice that my client hereby demands immediate redressal of the grievance detailed herein.\n" +
            "Factual Summary: Breach of statutory obligation and non-delivery of agreed services.\n\n" +
            "GROUND STATUTES:\n" +
            "- Section 35, Consumer Protection Act, 2019 (District Commission Jurisdiction)\n" +
            "- Section 303, Bharatiya Nyaya Sanhita (BNS), 2023\n\n" +
            "RELIEF SOUGHT: Full refund of 50000 along with statutory 18% p.a. interest.\n\n" +
            "Failing compliance within 15 days, appropriate legal proceedings will be initiated."
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
                            editableNoticeText.lines().forEach { line ->
                                canvas.drawText(line, 40f, y, paint)
                                y += 18f
                            }
                            pdfDocument.finishPage(page)
                            val outFile = File(context.cacheDir, "Legal_Notice_" + caseId + ".pdf")
                            val fos = FileOutputStream(outFile)
                            pdfDocument.writeTo(fos)
                            fos.close()
                            pdfDocument.close()
                            Toast.makeText(context, if (isTa) "PDF ??????????? ????????????????" else "PDF Exported Successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "PDF Error", Toast.LENGTH_SHORT).show()
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
