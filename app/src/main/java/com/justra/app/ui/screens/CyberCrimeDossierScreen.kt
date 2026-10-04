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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.HennaRedAlertContainer
import com.justra.app.ui.theme.HennaRedAlertText
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberCrimeDossierScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val currentDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val fraudCategories = listOf(
        "Financial Cyber Fraud (UPI / APK Scam / OTP Phishing)",
        "Identity Theft, Impersonation & Deepfake Extortion",
        "Cyber Stalking, Defamation & Online Harassment",
        "Ransomware, Data Breach & Unauthorized Server Access"
    )
    var selectedCategory by remember { mutableStateOf(fraudCategories[0]) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    var victimName by remember { mutableStateOf("R. Swaminathan") }
    var victimMobile by remember { mutableStateOf("+91 9444123456") }
    var defraudedAmount by remember { mutableStateOf("84,500") }
    var suspectUpiOrAccount by remember { mutableStateOf("fraudster@okaxis / 9840192831") }
    var bankReferenceOrUtr by remember { mutableStateOf("UTR Ref #402319882341 (Bank: HDFC Bank)") }
    var incidentNarrative by remember { mutableStateOf("Received malicious APK link posing as Electricity Bill Payment update; upon installation ₹84,500 debited via unauthorized IMPS transfer.") }

    val cyberDossier = buildCyberCrimeComplaint(
        victimName = victimName,
        victimMobile = victimMobile,
        category = selectedCategory,
        defraudedAmount = defraudedAmount,
        suspectDetails = suspectUpiOrAccount,
        bankUtr = bankReferenceOrUtr,
        incidentNarrative = incidentNarrative,
        dateStr = currentDateStr,
        isTa = isTa
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "சைபர் குற்ற புகார் கோப்பு தயாரிப்பான்" else "Cyber Crime Dossier & 1930 Portal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) "தகவல் தொழில்நுட்ப சட்டம் 2000 (பிரிவு 66C, 66D, 43)" else "National Cyber Crime Reporting & Financial Fraud Freeze Relay",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("cyber_back_btn")) {
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
            // Golden Hour 1930 Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HennaRedAlertContainer),
                    border = BorderStroke(1.dp, HennaRedAlertText.copy(alpha = 0.3f)),
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
                                    text = if (isTa) "சைபர் நிதி மோசடி அவசர எண்: 1930" else "National Cyber Financial Helpline: 1930",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = HennaRedAlertText)
                                )
                                Text(
                                    text = if (isTa) "முதல் 2 மணி நேரத்திற்குள் (Golden Hour) 1930-க்கு அழைத்தால் வங்கி கணக்கு உடனடியாக முடக்கப்படும்!"
                                           else "Call 1930 within the 'Golden Hour' to trigger immediate inter-bank freeze of defrauded funds via CFCFRMS.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1930"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HennaRedAlertText),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("call_1930_btn")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("1930")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cybercrime.gov.in/"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "cybercrime.gov.in போர்ட்டலைத் திற" else "Open Official cybercrime.gov.in Portal")
                        }
                    }
                }
            }

            // Complaint Inputs
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
                            text = if (isTa) "மோசடி & பரிவர்த்தனை தகவல்கள்" else "Fraud & Transaction Evidence Data",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        ExposedDropdownMenuBox(
                            expanded = isCategoryDropdownExpanded,
                            onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isTa) "சைபர் குற்ற வகை" else "Cyber Offence Category") },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isCategoryDropdownExpanded,
                                onDismissRequest = { isCategoryDropdownExpanded = false }
                            ) {
                                fraudCategories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            selectedCategory = cat
                                            isCategoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = victimName,
                            onValueChange = { victimName = it },
                            label = { Text(if (isTa) "பாதிக்கப்பட்டவர் பெயர்" else "Victim / Complainant Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("cyber_victim_input")
                        )

                        OutlinedTextField(
                            value = victimMobile,
                            onValueChange = { victimMobile = it },
                            label = { Text(if (isTa) "கைபேசி எண்" else "Mobile Number") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = defraudedAmount,
                            onValueChange = { defraudedAmount = it },
                            label = { Text(if (isTa) "மோசடி செய்யப்பட்ட தொகை (₹)" else "Total Defrauded / Siphoned Sum (₹)") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = suspectUpiOrAccount,
                            onValueChange = { suspectUpiOrAccount = it },
                            label = { Text(if (isTa) "குற்றவாளியின் UPI ID / வங்கி கணக்கு" else "Beneficiary / Suspect UPI ID or Account") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = bankReferenceOrUtr,
                            onValueChange = { bankReferenceOrUtr = it },
                            label = { Text(if (isTa) "வங்கி UTR / பரிவர்த்தனை எண்" else "Bank IMPS / NEFT / UTR Transaction ID") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = incidentNarrative,
                            onValueChange = { incidentNarrative = it },
                            label = { Text(if (isTa) "மோசடி நடந்த முறை (Modus Operandi)" else "Detailed Modus Operandi & Timeline") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            }

            // Complaint Dossier Output
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.5.dp, DeepIndigoSlatePrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isTa) "சைபர் குற்ற புகார் கோப்பு வரைவு" else "Formal Cyber Crime Complaint Dossier",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PaleSandstoneVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = cyberDossier,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Cyber Crime Complaint", cyberDossier)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (isTa) "புகார் கோப்பு நகலெடுக்கப்பட்டது" else "Cyber crime dossier copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("copy_cyber_dossier_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "புகார் கோப்பை நகலெடு" else "Copy Complaint Dossier")
                        }
                    }
                }
            }
        }
    }
}

private fun buildCyberCrimeComplaint(
    victimName: String,
    victimMobile: String,
    category: String,
    defraudedAmount: String,
    suspectDetails: String,
    bankUtr: String,
    incidentNarrative: String,
    dateStr: String,
    isTa: Boolean
): String {
    return """
FORMAL COMPLAINT UNDER SECTIONS 43, 66, 66C, 66D OF THE INFORMATION TECHNOLOGY ACT, 2000
READ WITH SECTION 318(4) & 319(2) OF BHARATIYA NYAYA SANHITA (BNS), 2023

TO:
The Station House Officer / Inspector of Police,
Cyber Crime Police Station / National Cyber Crime Portal (MHA)

COMPLAINANT:
Name: $victimName
Mobile: $victimMobile
Date of Report: $dateStr

SUBJECT: COMPLAINT REGARDING $category & SIPHONING OF ₹$defraudedAmount/-

Respected Sir / Madam,

I am lodging this formal complaint regarding an act of cyber financial fraud / offence committed against me:

1. Category of Cyber Offence : $category
2. Defrauded Sum : INR $defraudedAmount/- (Rupees $defraudedAmount only)
3. Suspect Beneficiary Account / UPI : $suspectDetails
4. Transaction Reference / Bank UTR : $bankUtr

5. Modus Operandi & Factual Sequence:
   $incidentNarrative

6. Evidentiary Preservation:
   Screenshots of debit SMS alerts, transaction UTR slips, and communication logs are securely hashed (SHA-256) and preserved for electronic evidence admissibility under Section 63 of Bharatiya Sakshya Adhiniyam, 2023 (BSA).

PRAYER:
I pray that this Hon'ble Authority may be pleased to:
a) Register an FIR under relevant sections of the IT Act, 2000 and BNS, 2023;
b) Direct the nodal banking officer / beneficiary payment gateway to immediately freeze the beneficiary account containing the tainted funds;
c) Trace the IP logs and bring the perpetrators to justice.

Date: $dateStr
Place: Chennai

COMPLAINANT
($victimName)
""".trimIndent()
}
