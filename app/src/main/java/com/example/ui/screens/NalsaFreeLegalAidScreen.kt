package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.DeepIndigoSlatePrimary
import com.example.ui.theme.PaleSandstoneVariant
import com.example.ui.theme.PrimaryContainerSlate
import com.example.ui.theme.SageGreenSuccessContainer
import com.example.ui.theme.SageGreenSuccessText
import com.example.ui.theme.TerracottaAccentSecondary
import com.example.ui.theme.WarmIvorySurface
import com.example.ui.theme.nyayaOutlinedTextFieldColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NalsaFreeLegalAidScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val currentDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    // Eligibility Checkbox Criteria (Section 12 of Legal Services Authorities Act, 1987)
    var isWomanOrChild by remember { mutableStateOf(false) }
    var isScStMember by remember { mutableStateOf(false) }
    var isIndustrialWorkman by remember { mutableStateOf(false) }
    var isDisasterVictim by remember { mutableStateOf(false) }
    var isUnderCustody by remember { mutableStateOf(false) }
    var isIncomeBelowThreshold by remember { mutableStateOf(true) }

    val isEligible = isWomanOrChild || isScStMember || isIndustrialWorkman || isDisasterVictim || isUnderCustody || isIncomeBelowThreshold

    // Lok Adalat Conciliation Application Form
    var applicantName by remember { mutableStateOf("M. Kasilingam") }
    var respondentName by remember { mutableStateOf("National Insurance Co. Ltd.") }
    var disputeSubject by remember { mutableStateOf("Accident Compensation & Policy Claim Settlement") }
    var settlementOfferAmount by remember { mutableStateOf("3,50,000") }

    val lokAdalatDraft = buildLokAdalatApplication(
        applicantName = applicantName,
        respondentName = respondentName,
        disputeSubject = disputeSubject,
        settlementOfferAmount = settlementOfferAmount,
        dateStr = currentDateStr,
        isTa = isTa
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "இலவச சட்ட உதவி & லோக் அதாலத்" else "NALSA Free Legal Aid & Lok Adalat",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) "சட்டப் பணிகள் ஆணைக்குழு சட்டம் 1987 (பிரிவு 12 & 19)" else "Section 12 Statutory Screener & Pre-Litigation Conciliation",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("nalsa_back_btn")) {
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
            // NALSA 15100 Helpline Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                    border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTa) "தேசிய சட்ட உதவி அவசர எண்: 15100" else "National Legal Aid Helpline: 15100 (24x7)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                            )
                            Text(
                                text = if (isTa) "அரசு செலவில் இலவச வழக்கறிஞர் உதவி மற்றும் ஆலோசனை" else "Free legal representation by appointed legal aid panel advocates",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15100"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("call_15100_btn")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("15100")
                        }
                    }
                }
            }

            // Eligibility Screener Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isTa) "பிரிவு 12 இலவச சட்ட உதவி தகுதி சோதனை" else "Section 12 Statutory Eligibility Screener",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )
                        Text(
                            text = if (isTa) "கீழ்க்கண்ட ஏதேனும் ஒன்றில் பொருந்தினால் நீங்கள் இலவச வழக்கறிஞர் மற்றும் நீதிமன்ற கட்டண விலக்கு பெற தகுதியானவர்:"
                                   else "Check any category applicable to you for 100% free legal representation & fee exemption:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        ScreenerCheckbox(
                            checked = isWomanOrChild,
                            onCheckedChange = { isWomanOrChild = it },
                            label = if (isTa) "பெண்கள் அல்லது குழந்தைகள் (Women or Children - Sec 12(c))" else "Women or Children (Sec 12(c))"
                        )
                        ScreenerCheckbox(
                            checked = isScStMember,
                            onCheckedChange = { isScStMember = it },
                            label = if (isTa) "தாழ்த்தப்பட்ட / பழங்குடியின வகுப்பினர் (SC / ST - Sec 12(a))" else "Scheduled Caste or Scheduled Tribe (Sec 12(a))"
                        )
                        ScreenerCheckbox(
                            checked = isIndustrialWorkman,
                            onCheckedChange = { isIndustrialWorkman = it },
                            label = if (isTa) "தொழில்துறை தொழிலாளி (Industrial Workman - Sec 12(e))" else "Industrial Workman (Sec 12(e))"
                        )
                        ScreenerCheckbox(
                            checked = isDisasterVictim,
                            onCheckedChange = { isDisasterVictim = it },
                            label = if (isTa) "பேரிடர் அல்லது வன்முறையால் பாதிக்கப்பட்டவர் (Sec 12(d))" else "Victim of Mass Disaster, Violence or Flood (Sec 12(d))"
                        )
                        ScreenerCheckbox(
                            checked = isUnderCustody,
                            onCheckedChange = { isUnderCustody = it },
                            label = if (isTa) "காவலில் உள்ள நபர் (Person in Custody / Jail - Sec 12(g))" else "Person in Custody / Under-Trial (Sec 12(g))"
                        )
                        ScreenerCheckbox(
                            checked = isIncomeBelowThreshold,
                            onCheckedChange = { isIncomeBelowThreshold = it },
                            label = if (isTa) "ஆண்டு வருமானம் ₹3 லட்சத்திற்கு கீழ் உள்ளவர் (Income < ₹3L - Sec 12(h))" else "Annual Income below ₹3,00,000 (TN State Limit - Sec 12(h))"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isEligible) SageGreenSuccessContainer else PaleSandstoneVariant,
                            border = BorderStroke(1.dp, if (isEligible) SageGreenSuccessText else DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isEligible) SageGreenSuccessText else DeepIndigoSlatePrimary
                                )
                                Text(
                                    text = if (isEligible) {
                                        if (isTa) "வாழ்த்துகள்! நீங்கள் TNSLSA மூலம் 100% இலவச வழக்கறிஞர் சேவை பெற தகுதியுடையவர்."
                                        else "Eligible: You qualify for Free Legal Aid under Section 12 of LSA Act, 1987."
                                    } else {
                                        if (isTa) "தகுதியைப் பெற மேலே உள்ள ஏதேனும் ஒரு பிரிவைத் தேர்வு செய்யவும்."
                                        else "Select relevant category to verify legal aid eligibility."
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isEligible) SageGreenSuccessText else DeepIndigoSlatePrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Lok Adalat Conciliation Generator
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null, tint = TerracottaAccentSecondary)
                            Text(
                                text = if (isTa) "லோக் அதாலத் சமரச மனு தயாரிப்பான்" else "Lok Adalat Pre-Litigation Application",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isTa) "லோக் அதாலத் தீர்ப்புக்கு நீதிமன்ற கட்டணம் கிடையாது; இதன் தீர்ப்பை எதிர்த்து மேல்முறையீடு இல்லை (இறுதியானது)."
                                   else "Under Sec 21 of LSA Act, Lok Adalat award is deemed a Civil Court decree with zero court fee and finality (no appeal).",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = applicantName,
                            onValueChange = { applicantName = it },
                            label = { Text(if (isTa) "மனுதாரர் பெயர்" else "Applicant Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = respondentName,
                            onValueChange = { respondentName = it },
                            label = { Text(if (isTa) "எதிர்மனுதாரர் / நிறுவனம்" else "Respondent / Opposite Party") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = disputeSubject,
                            onValueChange = { disputeSubject = it },
                            label = { Text(if (isTa) "சர்ச்சை பொருள் / விபரம்" else "Subject Matter of Dispute") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = settlementOfferAmount,
                            onValueChange = { settlementOfferAmount = it },
                            label = { Text(if (isTa) "முன்மொழியப்படும் சமரசத் தொகை (₹)" else "Proposed Settlement Sum (₹)") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PaleSandstoneVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lokAdalatDraft,
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
                                val clip = ClipData.newPlainText("Lok Adalat Application", lokAdalatDraft)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (isTa) "மனு நகலெடுக்கப்பட்டது" else "Lok Adalat application copied", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("copy_lok_adalat_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "சமரச மனுவை நகலெடு" else "Copy Lok Adalat Application")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenerCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = DeepIndigoSlatePrimary)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
        )
    }
}

private fun buildLokAdalatApplication(
    applicantName: String,
    respondentName: String,
    disputeSubject: String,
    settlementOfferAmount: String,
    dateStr: String,
    isTa: Boolean
): String {
    return """
BEFORE THE DISTRICT LEGAL SERVICES AUTHORITY (DLSA) / TALUK LEGAL SERVICES COMMITTEE
PRE-LITIGATION CONCILIATION PETITION UNDER SECTION 19 & 20 OF THE LEGAL SERVICES AUTHORITIES ACT, 1987

PETITIONER / APPLICANT:
$applicantName

VERSUS

RESPONDENT / OPPOSING PARTY:
$respondentName

SUBJECT: APPLICATION FOR PRE-LITIGATION CONCILIATION & AMICABLE SETTLEMENT IN RESPECT OF $disputeSubject

Most Respectfully Showeth:

1. That a bona fide dispute has arisen between the Petitioner and Respondent regarding:
   $disputeSubject

2. That the Petitioner desires to resolve the said dispute amicably without resorting to protracted adversarial litigation, and is willing to accept a mutually agreeable settlement in the sum of INR $settlementOfferAmount/- (Rupees $settlementOfferAmount only).

3. That under Section 20(2) of the Legal Services Authorities Act, 1987, this Hon'ble Authority has the statutory power to issue notice to the respondent and conduct conciliation proceedings to arrive at a compromised settlement.

PRAYER:
Wherefore, the Petitioner respectfully prays that this Hon'ble Authority may be pleased to:
a) Issue pre-litigation notice to the Respondent for appearance before the Lok Adalat Bench;
b) Facilitate amicable conciliation and pass an Award in terms of Section 21 of the Legal Services Authorities Act, 1987;
c) Pass such further orders as this Hon'ble Authority deems fit.

Date: $dateStr
Place: Chennai

PETITIONER
($applicantName)
""".trimIndent()
}
