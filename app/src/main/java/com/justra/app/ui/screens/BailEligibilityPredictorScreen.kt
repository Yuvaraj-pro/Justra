package com.justra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BailEligibilityPredictorScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var offenseType by remember { mutableStateOf(0) } // 0: Bailable, 1: Non-Bailable
    var custodyDays by remember { mutableStateOf("15") }
    var maxPunishmentYears by remember { mutableStateOf("7") }
    var isFirstTimeOffender by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "ஜாமீன் தகுதி கணிப்பான்" else "Bail Eligibility & BNSS 479 Analyzer",
                subtitle = if (isTa) "BNSS பிரிவு 479 & 480 கீழ் ஜாமீன் தகுதி" else "Sec 479 Statutory Bail & Antecedent Assessment",
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            com.justra.app.ui.components.StatutoryDisclaimerBanner(language = currentLanguage)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SovereignNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "⚖️ BNSS 2023 பிரிவு 479 சட்டப்பூர்வ ஜாமீன் உரிமை" else "⚖️ BNSS 2023 Sec 479 Statutory Bail Right",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "முதல் முறை குற்றஞ்சாட்டப்பட்டவர் அதிகபட்ச தண்டனையில் 1/3 பங்கு காவலில் இருந்தால் கட்டாய பிணை உரிமை உண்டு." else "First-time undertrials having completed 1/3rd of max imprisonment are entitled to mandatory statutory bail.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { com.justra.app.util.ActionUtils.dialEmergencyHelpline(context, "15100") },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTerracotta),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isTa) "அரசு இலவச சட்ட உதவி உதவி எண்: 15100" else "Govt NALSA Free Legal Aid Helpline: 15100")
                    }
                }
            }

            Text(
                text = if (isTa) "வழக்கு விவரங்களை உள்ளிடவும்" else "Input Case Parameters",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
            )

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (isTa) "குற்ற வகை (Offense Nature):" else "Offense Classification:", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = offenseType == 0,
                            onClick = { offenseType = 0 },
                            label = { Text(if (isTa) "பிணையில் வரக்கூடியது (Bailable)" else "Bailable Offense") }
                        )
                        FilterChip(
                            selected = offenseType == 1,
                            onClick = { offenseType = 1 },
                            label = { Text(if (isTa) "பிணையில் வர முடியாதது (Non-Bailable)" else "Non-Bailable Offense") }
                        )
                    }

                    OutlinedTextField(
                        value = custodyDays,
                        onValueChange = { custodyDays = it },
                        label = { Text(if (isTa) "காவலில் உள்ள நாட்கள் (Days in Custody)" else "Days Spent in Custody") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )

                    OutlinedTextField(
                        value = maxPunishmentYears,
                        onValueChange = { maxPunishmentYears = it },
                        label = { Text(if (isTa) "அதிகபட்ச தண்டனை ஆண்டுகள் (Max Imprisonment)" else "Max Prescribed Imprisonment (Years)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isFirstTimeOffender, onCheckedChange = { isFirstTimeOffender = it })
                        Text(if (isTa) "முதல் முறை குற்றஞ்சாட்டப்பட்டவர் (First-Time Offender)" else "First-Time Offender (No Prior Conviction)")
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AccentTerracotta)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isTa) "ஜாமீன் தகுதி பகுப்பாய்வு முடிவுகள்" else "Bail Eligibility Analysis", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (offenseType == 0) {
                        Text(if (isTa) "✅ பிரிவு 478 BNSS கீழ் காவல் நிலையத்தில் ஜாமீன் பெறுவது சட்டப்பூர்வ உரிமை. காவல் அதிகாரி பிணை வழங்க வேண்டும்." else "✅ Right to Bail under Sec 478 BNSS is absolute. Police officer/Magistrate MUST grant bail upon execution of bond.", color = SovereignNavy)
                    } else {
                        val custody = custodyDays.toIntOrNull() ?: 0
                        val maxYears = maxPunishmentYears.toIntOrNull() ?: 7
                        val thresholdDays = if (isFirstTimeOffender) (maxYears * 365 / 3) else (maxYears * 365 / 2)

                        if (custody >= thresholdDays) {
                            Text(if (isTa) "🎉 BNSS 479 கட்டாய பிணை தகுதி: காவலில் உள்ள காலம் ($custody நாட்கள்) தேவையான $thresholdDays நாட்களை தாண்டியுள்ளது. நீதிமன்றம் பிணை வழங்க கட்டுப்பட்டது." else "🎉 Mandatory Statutory Bail Eligible: Time spent ($custody days) exceeds the required threshold ($thresholdDays days) under Sec 479 BNSS.", color = SovereignNavy)
                        } else {
                            Text(if (isTa) "⚠️ நீதிமன்றத்தின் விவேக அதிகாரம்: BNSS பிரிவு 480 கீழ் வழக்கறிஞர் மூலம் பிணை மனு தாக்கல் செய்யவும். தேவையான காலம்: $thresholdDays நாட்கள் ($custody நாட்கள் முடிந்துள்ளது)." else "⚠️ Judicial Discretionary Bail: File Bail Application under Sec 480 BNSS through Legal Counsel. Threshold for mandatory bail is $thresholdDays days (Current: $custody days).", color = SovereignNavy)
                        }
                    }
                }
            }
        }
    }
}
