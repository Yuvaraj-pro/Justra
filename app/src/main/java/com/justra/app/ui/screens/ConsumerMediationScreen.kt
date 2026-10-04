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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsumerMediationScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val currentDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val grievanceTypes = listOf(
        "Deficiency of Service (Flight Cancellation / Airline Delay)",
        "E-Commerce Defective Product / Refusal to Refund",
        "Insurance Claim Repudiation (Health / Motor)",
        "Unfair Trade Practice (Misleading Advertising / Hidden Surcharges)",
        "Real Estate Flat Handover Delay / Builder Dispute"
    )
    var selectedGrievanceType by remember { mutableStateOf(grievanceTypes[0]) }
    var isGrievanceDropdownExpanded by remember { mutableStateOf(false) }

    var consumerName by remember { mutableStateOf("K. Soundararajan") }
    var companyName by remember { mutableStateOf("ABC Supermarket & E-Retail Private Limited") }
    var invoiceOrderRef by remember { mutableStateOf("Order #IN-99410882 dated 14/01/2026") }
    var requestedSettlement by remember { mutableStateOf("Full Refund of ₹42,500 + ₹10,000 Compensation for mental agony") }

    val mediationConsentDraft = buildConsumerMediationDraft(
        consumerName = consumerName,
        companyName = companyName,
        grievanceType = selectedGrievanceType,
        invoiceOrderRef = invoiceOrderRef,
        requestedSettlement = requestedSettlement,
        dateStr = currentDateStr,
        isTa = isTa
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "நுகர்வோர் சமரச தீர்வு & e-Daakhil" else "Consumer Mediation & e-Daakhil",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1E36)
                            )
                        )
                        Text(
                            text = if (isTa) "நுகர்வோர் பாதுகாப்பு சட்டம் 2019 (அத்தியாயம் V)" else "Chapter V Statutory Mediation & NCH 1915 Relay",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF4A4E57))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("mediation_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F1E36))
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF3ECE1),
                        border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleLanguage() }
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (isTa) "தமிழ்" else "English",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1E36)
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFAF7F2))
            )
        },
        containerColor = Color(0xFFFAF7F2),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // NCH 1915 & e-Daakhil Portals Row
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isTa) "தேசிய நுகர்வோர் உதவி எண்: 1915 (NCH)" else "National Consumer Helpline: 1915 (NCH)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                                )
                                Text(
                                    text = if (isTa) "நுகர்வோர் விவகார அமைச்சகத்தின் நேரடி குறைதீர்ப்பு தளம்" else "Department of Consumer Affairs grievance redressal",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                )
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1915"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("call_1915_btn")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("1915")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://edaakhil.nic.in/"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC05621)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "e-Daakhil ஆன்லைன் நீதிமன்ற தாக்கல் போர்ட்டலைத் திற" else "Launch Official e-Daakhil Filing Portal")
                        }
                    }
                }
            }

            // Mediation Input Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isTa) "நுகர்வோர் சமரச தீர்வு விவரங்கள்" else "Chapter V Mediation Application Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                        )

                        ExposedDropdownMenuBox(
                            expanded = isGrievanceDropdownExpanded,
                            onExpandedChange = { isGrievanceDropdownExpanded = !isGrievanceDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedGrievanceType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isTa) "குறைதீர்ப்பு வகை" else "Grievance Classification") },
                                leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF0F1E36)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGrievanceDropdownExpanded) },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isGrievanceDropdownExpanded,
                                onDismissRequest = { isGrievanceDropdownExpanded = false }
                            ) {
                                grievanceTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type, color = Color(0xFF14181F)) },
                                        onClick = {
                                            selectedGrievanceType = type
                                            isGrievanceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = consumerName,
                            onValueChange = { consumerName = it },
                            label = { Text(if (isTa) "நுகர்வோர் பெயர்" else "Consumer / Complainant Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("mediation_name_input")
                        )

                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text(if (isTa) "வணிகர் / நிறுவனத்தின் பெயர்" else "Opposite Party (Vendor / E-Commerce Entity)") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = invoiceOrderRef,
                            onValueChange = { invoiceOrderRef = it },
                            label = { Text(if (isTa) "ரசீது / ஆர்டர் எண் & தேதி" else "Invoice / Order / Policy Reference") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = requestedSettlement,
                            onValueChange = { requestedSettlement = it },
                            label = { Text(if (isTa) "கோரப்படும் சமரச தீர்வு (பணம் / மாற்றுப் பொருள்)" else "Terms of Settlement Sought") },
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
                    border = BorderStroke(1.5.dp, Color(0xFF0F1E36)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isTa) "சமரச ஒப்புதல் மனு வரைவு" else "Mediation Application Form",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
                            )
                            Icon(Icons.Default.Handshake, contentDescription = null, tint = Color(0xFF0F1E36))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF3ECE1),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = mediationConsentDraft,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF14181F)
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Consumer Mediation Consent", mediationConsentDraft)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (isTa) "சமரச மனு நகலெடுக்கப்பட்டது" else "Mediation draft copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("copy_mediation_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "சமரச மனுவை நகலெடு" else "Copy Mediation Petition")
                        }
                    }
                }
            }
        }
    }
}

private fun buildConsumerMediationDraft(
    consumerName: String,
    companyName: String,
    grievanceType: String,
    invoiceOrderRef: String,
    requestedSettlement: String,
    dateStr: String,
    isTa: Boolean
): String {
    return """
BEFORE THE CONSUMER MEDIATION CELL ATTACHED TO THE DISTRICT CONSUMER DISPUTES REDRESSAL COMMISSION
APPLICATION UNDER SECTION 37 & CHAPTER V OF THE CONSUMER PROTECTION ACT, 2019

IN THE MATTER OF:
$consumerName (Complainant / Consumer)
VERSUS
$companyName (Opposite Party / Trader)

SUBJECT: JOINT CONSENT & APPLICATION FOR REFERENCE OF CONSUMER DISPUTE TO STATUTORY MEDIATION CELL

1. That the Complainant purchased goods / subscribed to services from the Opposite Party under:
   Reference: $invoiceOrderRef

2. That a consumer grievance regarding '$grievanceType' has arisen between the parties.

3. That under Section 37(1) of the Consumer Protection Act, 2019, the Complainant hereby gives unconditional written consent for reference of this consumer dispute to the Consumer Mediation Cell attached to this Hon'ble Commission for time-bound resolution within 30 days.

4. The Complainant proposes the following reasonable settlement terms for consideration by the Mediator:
   $requestedSettlement

PRAYER:
It is respectfully prayed that this Hon'ble Commission may be pleased to refer the dispute to a nominated Mediator in accordance with the Consumer Protection (Mediation) Rules, 2020.

Date: $dateStr
Place: Chennai

SIGNATURE OF THE CONSUMER / COMPLAINANT
($consumerName)
""".trimIndent()
}
