package com.justra.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.justra.app.data.local.CaseEntity
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SaffronAmberWarningContainer
import com.justra.app.ui.theme.SaffronAmberWarningText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import com.justra.app.util.PdfExporter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalNoticeDisputeComposerScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val currentDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val noticeTypes = listOf(
        "Section 138 NI Act (Cheque Dishonour 15-Day Statutory Notice)",
        "Consumer Pre-Litigation Cease & Desist Notice (CPA 2019)",
        "Landlord Eviction & Rent Arrears Notice (TN Tenancy Act 2017)",
        "Commercial Contract Breach & Debt Recovery Demand Notice"
    )
    var selectedNoticeType by remember { mutableStateOf(noticeTypes[0]) }
    var isNoticeDropdownExpanded by remember { mutableStateOf(false) }

    var senderName by remember { mutableStateOf("V. Balasubramanian") }
    var senderAddress by remember { mutableStateOf("No. 18, TTK Road, Alwarpet, Chennai - 600018") }
    var receiverName by remember { mutableStateOf("M/s Horizon Traders & Co.") }
    var receiverAddress by remember { mutableStateOf("Shop No. 7, Ranganathan Street, T. Nagar, Chennai - 600017") }
    var claimAmount by remember { mutableStateOf("1,75,000") }
    var chequeOrTxnRef by remember { mutableStateOf("Cheque No. 440912 dated 10/01/2026 drawn on SBI") }
    var returnReason by remember { mutableStateOf("Funds Insufficient / Exceeds Arrangement (Return Memo dt 15/01/2026)") }

    val generatedNoticeText = buildLegalNoticeText(
        noticeType = selectedNoticeType,
        senderName = senderName,
        senderAddress = senderAddress,
        receiverName = receiverName,
        receiverAddress = receiverAddress,
        claimAmount = claimAmount,
        chequeOrTxnRef = chequeOrTxnRef,
        returnReason = returnReason,
        dateStr = currentDateStr,
        isTa = isTa
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "சட்டப்பூர்வ அறிவிப்பு தயாரிப்பான்" else "Statutory Legal Demand Notice Composer",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) "138 NI Act, வாடகை சட்டம் & நுகர்வோர் நோட்டீஸ்" else "15-Day RPAD Cure Period & Statutory Clauses",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("legal_notice_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepIndigoSlatePrimary)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PaleSandstoneVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleLanguage() }
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (isTa) "தமிழ்" else "English",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvorySurface)
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("legal_notice_snackbar_host")
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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SaffronAmberWarningContainer),
                    border = BorderStroke(1.dp, SaffronAmberWarningText.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = SaffronAmberWarningText)
                        Text(
                            text = if (isTa) "பிரிவு 138-ன் கீழ் காசோலை திரும்பிய 30 நாட்களுக்குள் நோட்டீஸ் அனுப்ப வேண்டும். எதிர் தரப்பினருக்கு 15 நாட்கள் அவகாசம் வழங்க வேண்டும்."
                                   else "Under Sec 138(b) of NI Act, demand notice must be dispatched within 30 days of bank return memo, granting mandatory 15 days to make payment.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SaffronAmberWarningText, lineHeight = 18.sp)
                        )
                    }
                }
            }

            // Input Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isTa) "நோட்டீஸ் வகை மற்றும் தரப்பினர் விவரங்கள்" else "Notice Classification & Party Information",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        ExposedDropdownMenuBox(
                            expanded = isNoticeDropdownExpanded,
                            onExpandedChange = { isNoticeDropdownExpanded = !isNoticeDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedNoticeType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isTa) "சட்ட அறிவிப்பு வகை" else "Statutory Notice Type") },
                                leadingIcon = { Icon(Icons.Default.Gavel, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isNoticeDropdownExpanded) },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isNoticeDropdownExpanded,
                                onDismissRequest = { isNoticeDropdownExpanded = false }
                            ) {
                                noticeTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedNoticeType = type
                                            isNoticeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = senderName,
                            onValueChange = { senderName = it },
                            label = { Text(if (isTa) "அனுப்புநர் (Sender / Complainant)" else "Sender / Complainant Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("notice_sender_input")
                        )

                        OutlinedTextField(
                            value = senderAddress,
                            onValueChange = { senderAddress = it },
                            label = { Text(if (isTa) "அனுப்புநர் முகவரி" else "Sender Address") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = receiverName,
                            onValueChange = { receiverName = it },
                            label = { Text(if (isTa) "பெறுநர் / எதிர்மனுதாரர்" else "Addressee / Defaulter Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = receiverAddress,
                            onValueChange = { receiverAddress = it },
                            label = { Text(if (isTa) "பெறுநர் முகவரி" else "Addressee Address") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = claimAmount,
                            onValueChange = { claimAmount = it },
                            label = { Text(if (isTa) "கோரப்படும் நிலுவைத் தொகை (₹)" else "Claim / Dishonoured Cheque Amount (₹)") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = chequeOrTxnRef,
                            onValueChange = { chequeOrTxnRef = it },
                            label = { Text(if (isTa) "காசோலை எண் / பரிவர்த்தனை குறிப்பு" else "Cheque / Transaction / Agreement Reference") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = returnReason,
                            onValueChange = { returnReason = it },
                            label = { Text(if (isTa) "வங்கி குறிப்பு / காரண விளக்கம்" else "Bank Return Reason / Breach Particulars") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Preview Draft
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.5.dp, DeepIndigoSlatePrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isTa) "சட்ட அறிவிப்பு வரைவு (RPAD Ready)" else "Legal Demand Notice Preview",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                            )
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = DeepIndigoSlatePrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PaleSandstoneVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedNoticeText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Copy Button
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Legal Demand Notice", generatedNoticeText)
                                    clipboard.setPrimaryClip(clip)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = if (isTa) "சட்டப்பூர்வ அறிவிப்பு நகலெடுக்கப்பட்டது" else "Legal Demand Notice copied to clipboard",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("copy_notice_btn")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isTa) "நகலெடு" else "Copy")
                            }

                            // Export PDF Button
                            Button(
                                onClick = {
                                    // Create a temporary case entity for PDF generation
                                    val tempCase = CaseEntity(
                                        caseId = "notice_${UUID.randomUUID().toString().take(8)}",
                                        title = "Legal Demand Notice - ${selectedNoticeType.take(30)}",
                                        disputeCategory = com.justra.app.domain.model.DisputeCategory.CONSUMER_GRIEVANCE,
                                        status = "DRAFT",
                                        generatedComplaintDraft = generatedNoticeText,
                                        createdAt = System.currentTimeMillis(),
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    PdfExporter.exportComplaintToPdf(
                                        context = context,
                                        caseEntity = tempCase,
                                        complaintText = generatedNoticeText,
                                        onSuccess = { file ->
                                            // Share the generated PDF
                                            val uri = FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.fileprovider",
                                                file
                                            )
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "application/pdf"
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            val chooser = Intent.createChooser(shareIntent, "Share Legal Notice PDF")
                                            context.startActivity(chooser)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = if (isTa) "PDF உருவாக்கப்பட்டு பகிருக்கப்படுகிறது" else "PDF generated and ready to share",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        },
                                        onError = { error ->
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = if (isTa) "PDF பிழை: $error" else "PDF Error: $error",
                                                    duration = SnackbarDuration.Long
                                                )
                                            }
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccentSecondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("export_pdf_btn")
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isTa) "PDF ஏற்றுமதி" else "Export PDF")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun buildLegalNoticeText(
    noticeType: String,
    senderName: String,
    senderAddress: String,
    receiverName: String,
    receiverAddress: String,
    claimAmount: String,
    chequeOrTxnRef: String,
    returnReason: String,
    dateStr: String,
    isTa: Boolean
): String {
    return """
BY REGISTERED POST ACKNOWLEDGMENT DUE (RPAD) & SPEED POST

DATE: $dateStr

TO:
$receiverName
$receiverAddress

FROM:
$senderName
$senderAddress

SUBJECT: STATUTORY DEMAND NOTICE UNDER $noticeType FOR RECOVERY OF ₹$claimAmount/-

Sir / Madam,

Under instructions and on behalf of my client / the sender ($senderName), I hereby serve upon you this Statutory Legal Demand Notice:

1. That you, the addressee, were legally indebted and liable to the sender for the discharge of enforceable debt / liability, towards which you issued / committed:
   Particulars: $chequeOrTxnRef
   Amount: INR $claimAmount/-

2. That upon presentation of the aforesaid negotiable instrument / transaction for realization, the same was returned dishonoured and unpaid with the bank endorsement:
   Reason: "$returnReason"

3. That by virtue of this Notice, you are hereby called upon to pay the said entire sum of INR $claimAmount/- (Rupees $claimAmount only) to the sender within a mandatory period of FIFTEEN (15) DAYS from the date of receipt of this notice.

4. Take note that in the event of your failure or neglect to comply with this statutory demand within the stipulated period of 15 days, my client shall initiate criminal prosecution under Section 138 of the Negotiable Instruments Act, 1881 / Section 318(4) of BNS 2023, along with civil proceedings for recovery with compound interest and legal costs, holding you solely responsible for all consequential costs.

Yours faithfully,

($senderName)
Complainant / Claim Holder
""".trimIndent()
}
