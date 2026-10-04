package com.justra.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RtiDraftingWizardScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val currentDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val departments = listOf(
        "TANGEDCO (Electricity Board, Tamil Nadu)",
        "Greater Chennai Corporation / Municipal Administration",
        "Tamil Nadu Police Department (DGP Office)",
        "Revenue Department & District Collectorate",
        "Public Works Department (PWD & Highways)",
        "Food Safety & Consumer Protection Dept",
        "Central Public Information Officer (CPIO - Central Govt)"
    )
    var selectedDept by remember { mutableStateOf(departments[0]) }
    var isDeptDropdownExpanded by remember { mutableStateOf(false) }

    var applicantName by remember { mutableStateOf("S. Murugan") }
    var applicantAddress by remember { mutableStateOf("12/4, Gandhi Street, Velachery, Chennai - 600042") }
    var applicantPhone by remember { mutableStateOf("+91 9876543210") }

    val queries = remember {
        mutableStateListOf(
            "Please provide certified copies of all inspection reports concerning road repair tender ref #TN/2025/RD/884.",
            "Please state the total statutory funds allocated and disbursed to the contractor as of today."
        )
    }
    var newQueryText by remember { mutableStateOf("") }

    var isLifeAndLiberty by remember { mutableStateOf(false) }
    var isBplApplicant by remember { mutableStateOf(false) }
    var bplCardNumber by remember { mutableStateOf("") }

    val generatedRtiText = buildRtiApplication(
        applicantName = applicantName,
        applicantAddress = applicantAddress,
        applicantPhone = applicantPhone,
        department = selectedDept,
        queries = queries,
        isLifeAndLiberty = isLifeAndLiberty,
        isBplApplicant = isBplApplicant,
        bplCardNumber = bplCardNumber,
        dateStr = currentDateStr,
        isTa = isTa
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "RTI மனு & மேல்முறையீடு தயாரிப்பான்" else "RTI Application & Appeal Wizard",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) "தகவல் அறியும் உரிமைச் சட்டம் 2005 (பிரிவு 6(1) & 19)" else "Right to Information Act, 2005 (Form 'A' & 30-Day Tracker)",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("rti_back_btn")) {
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
            // Statutory Alert Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLifeAndLiberty) HennaRedAlertContainer else PrimaryContainerSlate
                    ),
                    border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isLifeAndLiberty) HennaRedAlertText else DeepIndigoSlatePrimary
                        )
                        Column {
                            Text(
                                text = if (isLifeAndLiberty) {
                                    if (isTa) "உயிர் மற்றும் தனிநபர் சுதந்திரம் (48 மணி நேர காலக்கெடு)" else "Life & Liberty Clause Active: Mandatory 48-Hour Deadline (Sec 7(1))"
                                } else {
                                    if (isTa) "சாதாரண RTI மனு: 30 நாட்கள் சட்டப்பூர்வ காலக்கெடு" else "Standard RTI: 30 Days Statutory Limit (Sec 7(1)) | Fee: ₹10 Court Fee Stamp"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLifeAndLiberty) HennaRedAlertText else DeepIndigoSlatePrimary
                                )
                            )
                            Text(
                                text = if (isTa) "PIO தகவல் வழங்கத் தவறினால் நாள் ஒன்றுக்கு ₹250 அபராதம் (பிரிவு 20)."
                                       else "Sec 20 penalizes PIO ₹250/day up to ₹25,000 for mala fide refusal or delay.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }

            // Input Details
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
                            text = if (isTa) "அரசுத் துறை & மனுதாரர் விவரங்கள்" else "Public Authority & Applicant Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        ExposedDropdownMenuBox(
                            expanded = isDeptDropdownExpanded,
                            onExpandedChange = { isDeptDropdownExpanded = !isDeptDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedDept,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isTa) "அரசுத் துறை / பொதுத் தகவல் அலுவலர்" else "Public Authority (PIO Department)") },
                                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDeptDropdownExpanded) },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isDeptDropdownExpanded,
                                onDismissRequest = { isDeptDropdownExpanded = false }
                            ) {
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept) },
                                        onClick = {
                                            selectedDept = dept
                                            isDeptDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = applicantName,
                            onValueChange = { applicantName = it },
                            label = { Text(if (isTa) "மனுதாரர் பெயர்" else "Applicant Full Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("rti_name_input")
                        )

                        OutlinedTextField(
                            value = applicantAddress,
                            onValueChange = { applicantAddress = it },
                            label = { Text(if (isTa) "முகவரி & அஞ்சல் குறியீடு" else "Address & Pincode") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isLifeAndLiberty,
                                onCheckedChange = { isLifeAndLiberty = it },
                                colors = CheckboxDefaults.colors(checkedColor = DeepIndigoSlatePrimary)
                            )
                            Text(
                                text = if (isTa) "உயிர் மற்றும் தனிநபர் சுதந்திரம் சார்ந்ததா? (48 மணி நேரம்)" else "Concerns Life & Liberty of Person (Sec 7(1) - 48h)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isBplApplicant,
                                onCheckedChange = { isBplApplicant = it },
                                colors = CheckboxDefaults.colors(checkedColor = DeepIndigoSlatePrimary)
                            )
                            Text(
                                text = if (isTa) "வறுமைக் கோட்டிற்கு கீழ் உள்ளவர் (BPL - கட்டணம் இல்லை)?" else "Below Poverty Line (BPL - Exempt from ₹10 Fee)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (isBplApplicant) {
                            OutlinedTextField(
                                value = bplCardNumber,
                                onValueChange = { bplCardNumber = it },
                                label = { Text(if (isTa) "BPL / குடும்ப அட்டை எண்" else "BPL Ration Card Number") },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Queries Builder
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isTa) "கேட்கப்படும் தகவல்கள் (RTI Queries)" else "Specific Information Requested (Numbered Queries)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        queries.forEachIndexed { idx, q ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${idx + 1}. ",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = q,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { queries.removeAt(idx) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TerracottaAccentSecondary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = newQueryText,
                                onValueChange = { newQueryText = it },
                                placeholder = { Text(if (isTa) "புதிய கேள்வியை உள்ளிடவும்..." else "Enter specific question...") },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.weight(1f).testTag("rti_new_query_input")
                            )
                            Button(
                                onClick = {
                                    if (newQueryText.isNotBlank()) {
                                        queries.add(newQueryText.trim())
                                        newQueryText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
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
                        Text(
                            text = if (isTa) "தயாரான RTI மனு (படிவம் 'A')" else "Generated Form 'A' Application",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PaleSandstoneVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedRtiText,
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
                                val clip = ClipData.newPlainText("RTI Application", generatedRtiText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (isTa) "RTI மனு நகலெடுக்கப்பட்டது" else "RTI Application copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("copy_rti_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "RTI மனுவை நகலெடு" else "Copy RTI Application")
                        }
                    }
                }
            }
        }
    }
}

private fun buildRtiApplication(
    applicantName: String,
    applicantAddress: String,
    applicantPhone: String,
    department: String,
    queries: List<String>,
    isLifeAndLiberty: Boolean,
    isBplApplicant: Boolean,
    bplCardNumber: String,
    dateStr: String,
    isTa: Boolean
): String {
    val feeClause = if (isBplApplicant) {
        "Applicant is Below Poverty Line (BPL Card No: $bplCardNumber) and is exempted from application fee under Sec 7(5)."
    } else {
        "Court Fee Stamp / IPO of ₹10/- is affixed herewith towards statutory application fee under Section 6(1)."
    }

    val urgencyClause = if (isLifeAndLiberty) {
        "**NOTE: THIS APPLICATION CONCERNS LIFE AND PERSONAL LIBERTY OF A CITIZEN. PLEASE PROVIDE INFORMATION WITHIN 48 HOURS UNDER PROVISO TO SECTION 7(1).**\n"
    } else ""

    val queriesFormatted = queries.mapIndexed { idx, q -> "${idx + 1}. $q" }.joinToString("\n")

    return """
FORM 'A'
APPLICATION FOR INFORMATION UNDER SECTION 6(1) OF THE RIGHT TO INFORMATION ACT, 2005

To:
The Public Information Officer (PIO) / Assistant PIO,
$department

$urgencyClause
1. Full Name of the Applicant : $applicantName
2. Address for Correspondence : $applicantAddress
3. Contact Telephone / Mobile : $applicantPhone
4. Citizenship : Citizen of India

5. Particulars of Information Required:
$queriesFormatted

6. Period for which information is sought : Recent / Up to $dateStr
7. Statutory Application Fee :
   $feeClause

8. Mode of Delivery Preferred : Certified Hard Copy via Registered Speed Post AD / In-Person

I hereby state that the information sought does not fall within the exemptions contained in Section 8 or 9 of the RTI Act, 2005 and to the best of my knowledge it pertains to your office.

Place: Chennai
Date: $dateStr

SIGNATURE OF THE APPLICANT
($applicantName)
""".trimIndent()
}
