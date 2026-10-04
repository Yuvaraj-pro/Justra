package com.justra.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
fun WomenDomesticRightsScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val currentDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    // Safety Camouflage Mode (disguises legal screen into a harmless grocery shopping list)
    var isCamouflageModeActive by remember { mutableStateOf(false) }

    // Relief Options under PWDVA 2005
    var needProtectionOrder by remember { mutableStateOf(true) } // Sec 18
    var needResidenceOrder by remember { mutableStateOf(true) }   // Sec 19
    var needMonetaryRelief by remember { mutableStateOf(true) }  // Sec 20
    var needChildCustody by remember { mutableStateOf(false) }    // Sec 21

    var complainantName by remember { mutableStateOf("S. Priyadharshini") }
    var respondentName by remember { mutableStateOf("R. Karthikeyan & In-Laws") }
    var incidentSummary by remember { mutableStateOf("Repeated verbal, emotional abuse and physical threats of forceful eviction from shared matrimonial residence.") }
    var interimMaintenanceSum by remember { mutableStateOf("25,000") }

    val dirDraft = buildDomesticIncidentReport(
        complainantName = complainantName,
        respondentName = respondentName,
        needProtection = needProtectionOrder,
        needResidence = needResidenceOrder,
        needMonetary = needMonetaryRelief,
        needCustody = needChildCustody,
        maintenanceSum = interimMaintenanceSum,
        incidentSummary = incidentSummary,
        dateStr = currentDateStr,
        isTa = isTa
    )

    if (isCamouflageModeActive) {
        // Camouflage Screen: Harmless Everyday Household / Grocery List
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Daily Grocery & Pantry List", style = MaterialTheme.typography.titleMedium) },
                    actions = {
                        IconButton(onClick = { isCamouflageModeActive = false }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Exit Camouflage")
                        }
                    }
                )
            },
            containerColor = Color(0xFFF9F9F9)
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("• Organic Brown Rice - 5 kg", style = MaterialTheme.typography.bodyLarge)
                Text("• Cold Pressed Sesame Oil - 2 Litres", style = MaterialTheme.typography.bodyLarge)
                Text("• Toor Dal & Green Moong - 1 kg each", style = MaterialTheme.typography.bodyLarge)
                Text("• Fresh Vegetables & Fruits", style = MaterialTheme.typography.bodyLarge)
                Text("• Laundry Detergent & Dishwash Gel", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { isCamouflageModeActive = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                    modifier = Modifier.fillMaxWidth().testTag("exit_camouflage_btn")
                ) {
                    Text("Return to Secure Mode")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "பெண்கள் பாதுகாப்பு & PoSH சட்ட மையம்" else "Women's Statutory Protection & PoSH",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) "குடும்ப வன்முறை பாதுகாப்பு சட்டம் 2005 & PoSH சட்டம் 2013" else "PWDVA 2005 DIR Form I & 1091 / 181 Direct Lifeline",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("women_rights_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepIndigoSlatePrimary)
                    }
                },
                actions = {
                    // Quick camouflage button for privacy
                    IconButton(
                        onClick = { isCamouflageModeActive = true },
                        modifier = Modifier.testTag("activate_camouflage_btn")
                    ) {
                        Icon(
                            Icons.Default.VisibilityOff,
                            contentDescription = "Disguise Screen",
                            tint = TerracottaAccentSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PaleSandstoneVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleLanguage() }
                            .padding(end = 8.dp)
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
            // Emergency Women Helpline Bar (1091 & 181)
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = HennaRedAlertText)
                            Text(
                                text = if (isTa) "24x7 பெண்கள் அவசர உதவி எண்கள் (SOS)" else "24x7 Statutory Women Emergency Helplines",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = HennaRedAlertText)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1091"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HennaRedAlertText),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("call_1091_btn")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("1091 (Police)")
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:181"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("call_181_btn")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("181 (Women Helpline)")
                            }
                        }
                    }
                }
            }

            // Relief Selector Card
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
                            text = if (isTa) "கோரப்படும் சட்டப்பூர்வ நிவாரணங்கள் (PWDVA 2005)" else "Statutory Reliefs Claimed under PWDVA 2005",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        ReliefCheckboxRow(
                            checked = needProtectionOrder,
                            onCheckedChange = { needProtectionOrder = it },
                            title = if (isTa) "பிரிவு 18: பாதுகாப்பு ஆணை (Protection Order)" else "Section 18: Protection Order (Restrain Violence/Contact)",
                            subtitle = if (isTa) "எதிர்மனுதாரர் தொடர்பு கொள்வதையோ துன்புறுத்துவதையோ தடை செய்தல்" else "Injunction against committing acts of domestic violence"
                        )

                        ReliefCheckboxRow(
                            checked = needResidenceOrder,
                            onCheckedChange = { needResidenceOrder = it },
                            title = if (isTa) "பிரிவு 19: வசிப்பிட உரிமை (Shared Household Residence Order)" else "Section 19: Residence Order (Right to Reside in Matrimonial Home)",
                            subtitle = if (isTa) "பகிரப்பட்ட இல்லத்திலிருந்து கட்டாய வெளியேற்றத்தை தடுத்தல்" else "Injunction against dispossessing victim from shared household"
                        )

                        ReliefCheckboxRow(
                            checked = needMonetaryRelief,
                            onCheckedChange = { needMonetaryRelief = it },
                            title = if (isTa) "பிரிவு 20: மாதாந்திர இடைக்கால ஜீவனாம்சம் (Monetary Relief)" else "Section 20: Monetary Relief & Interim Maintenance",
                            subtitle = if (isTa) "மருத்துவச் செலவுகள் மற்றும் மாதாந்திர பராமரிப்புத் தொகை" else "Payment for medical expenses and monthly maintenance"
                        )

                        ReliefCheckboxRow(
                            checked = needChildCustody,
                            onCheckedChange = { needChildCustody = it },
                            title = if (isTa) "பிரிவு 21: குழந்தைகளின் தற்காலிக காவல் உரிமை (Custody Order)" else "Section 21: Temporary Child Custody Order",
                            subtitle = if (isTa) "குழந்தைகளின் தற்காலிக பாதுகாப்பை தாயிடம் ஒப்படைத்தல்" else "Granting temporary custody of children to aggrieved person"
                        )
                    }
                }
            }

            // Particulars Form
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
                            text = if (isTa) "விவரங்கள் & சம்பவச் சுருக்கம்" else "Incident & Maintenance Particulars",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        OutlinedTextField(
                            value = complainantName,
                            onValueChange = { complainantName = it },
                            label = { Text(if (isTa) "பாதிக்கப்பட்ட பெண் பெயர்" else "Aggrieved Woman Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("women_name_input")
                        )

                        OutlinedTextField(
                            value = respondentName,
                            onValueChange = { respondentName = it },
                            label = { Text(if (isTa) "எதிர்மனுதாரர்(கள்) விவரம்" else "Respondent(s) Name / Relationship") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = interimMaintenanceSum,
                            onValueChange = { interimMaintenanceSum = it },
                            label = { Text(if (isTa) "கோரப்படும் மாதாந்திர பராமரிப்புத் தொகை (₹)" else "Interim Monthly Maintenance Claim (₹)") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = incidentSummary,
                            onValueChange = { incidentSummary = it },
                            label = { Text(if (isTa) "சம்பவ விவரம் / அச்சுறுத்தல் விபரம்" else "Brief Factual Statement of Domestic Violence") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
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
                        Text(
                            text = if (isTa) "குடும்பச் சம்பவ அறிக்கை (DIR படிவம் I)" else "Domestic Incident Report (DIR Form I)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PaleSandstoneVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = dirDraft,
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
                                val clip = ClipData.newPlainText("DIR Form I Application", dirDraft)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (isTa) "மனு நகலெடுக்கப்பட்டது" else "DIR Petition copied", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("copy_women_dir_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "மனுவை நகலெடு" else "Copy Form I DIR Petition")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReliefCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = DeepIndigoSlatePrimary)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

private fun buildDomesticIncidentReport(
    complainantName: String,
    respondentName: String,
    needProtection: Boolean,
    needResidence: Boolean,
    needMonetary: Boolean,
    needCustody: Boolean,
    maintenanceSum: String,
    incidentSummary: String,
    dateStr: String,
    isTa: Boolean
): String {
    val reliefs = buildList {
        if (needProtection) add("• Protection Order under Section 18 restraining Respondent from committing further domestic violence or entering place of employment.")
        if (needResidence) add("• Residence Order under Section 19 securing right to reside in shared household and restraining dispossession.")
        if (needMonetary) add("• Monetary Relief & Interim Maintenance under Section 20 in the sum of INR $maintenanceSum/- per month.")
        if (needCustody) add("• Temporary Child Custody Order under Section 21 granting interim custody of minor children.")
    }.joinToString("\n")

    return """
FORM I: DOMESTIC INCIDENT REPORT (DIR)
UNDER SECTION 9(b) AND 37(2)(c) OF THE PROTECTION OF WOMEN FROM DOMESTIC VIOLENCE ACT, 2005

BEFORE THE PROTECTION OFFICER / JUDICIAL MAGISTRATE COURT
IN THE MATTER OF:
$complainantName (Aggrieved Person)
VERSUS
$respondentName (Respondent(s))

1. Details of the Aggrieved Person : $complainantName
2. Details of the Respondent(s) : $respondentName
3. Brief Particulars of Domestic Violence :
   $incidentSummary

4. Statutory Orders Prayed For:
$reliefs

5. That under Section 12(1) and Section 23 of the Act, the Hon'ble Magistrate is empowered to pass ex-parte interim protection orders upon receipt of this Domestic Incident Report.

Place: Chennai
Date: $dateStr

SIGNATURE OF THE AGGRIEVED PERSON
($complainantName)
""".trimIndent()
}
