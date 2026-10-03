package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.nyayaOutlinedTextFieldColors

data class StatuteMappingItem(
    val ipcSection: String,
    val bnsSection: String,
    val offenceNameEn: String,
    val offenceNameTa: String,
    val punishmentEn: String,
    val punishmentTa: String,
    val isBailable: Boolean,
    val isCognizable: Boolean,
    val keyChangeEn: String,
    val keyChangeTa: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BnsIpcTransitionScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var searchQuery by remember { mutableStateOf("") }

    val mappings = remember {
        listOf(
            StatuteMappingItem(
                ipcSection = "IPC Section 420",
                bnsSection = "BNS Section 318(4)",
                offenceNameEn = "Cheating and dishonestly inducing delivery of property",
                offenceNameTa = "ஏமாற்றுதல் மற்றும் நேர்மையற்ற முறையில் சொத்து பறித்தல்",
                punishmentEn = "Imprisonment up to 7 years + Fine",
                punishmentTa = "7 ஆண்டுகள் வரை சிறைத்தண்டனை + அபராதம்",
                isBailable = false,
                isCognizable = true,
                keyChangeEn = "Scope expanded to cover electronic/digital financial deception explicitly.",
                keyChangeTa = "டிஜிட்டல் மற்றும் நிதி மோசடிகளும் வெளிப்படையாக சேர்க்கப்பட்டுள்ளன."
            ),
            StatuteMappingItem(
                ipcSection = "IPC Section 302",
                bnsSection = "BNS Section 103(1)",
                offenceNameEn = "Punishment for Murder",
                offenceNameTa = "கொலைக்கான தண்டனை",
                punishmentEn = "Death or Imprisonment for life + Fine",
                punishmentTa = "மரண தண்டனை அல்லது ஆயுள் தண்டனை + அபராதம்",
                isBailable = false,
                isCognizable = true,
                keyChangeEn = "Mob lynching defined under Sec 103(2) with separate stringent death/life penalty.",
                keyChangeTa = "கும்பல் வன்முறை கொலைக்கு பிரிவு 103(2)-ன் கீழ் தனியான கடுமையான தண்டனை."
            ),
            StatuteMappingItem(
                ipcSection = "IPC Section 378 / 379",
                bnsSection = "BNS Section 303(2) & 304",
                offenceNameEn = "Theft & Snatching",
                offenceNameTa = "திருட்டு மற்றும் சங்கிலி/பொருள் பறித்தல் (Snatching)",
                punishmentEn = "Up to 3 years / Snatching up to 3 years + Fine",
                punishmentTa = "3 ஆண்டுகள் வரை சிறை / பறித்தலுக்கு 3 ஆண்டுகள் சிறை + அபராதம்",
                isBailable = false,
                isCognizable = true,
                keyChangeEn = "Snatching introduced as a distinct statutory offence for the first time in Indian law.",
                keyChangeTa = "சங்கிலி பறித்தல் முதன்முறையாக தனி சட்டப்பிரிவாக உருவாக்கப்பட்டுள்ளது."
            ),
            StatuteMappingItem(
                ipcSection = "IPC Section 498A",
                bnsSection = "BNS Section 85 & 86",
                offenceNameEn = "Cruelty by husband or relatives of husband",
                offenceNameTa = "கணவர் அல்லது அவரது குடும்பத்தினரால் இழைக்கப்படும் கொடுமை",
                punishmentEn = "Imprisonment up to 3 years + Fine",
                punishmentTa = "3 ஆண்டுகள் வரை சிறைத்தண்டனை + அபராதம்",
                isBailable = false,
                isCognizable = true,
                keyChangeEn = "Split into two sections: Sec 85 (Cruelty) and Sec 86 (Explanation & Mental harassment).",
                keyChangeTa = "உடல் மற்றும் மன உளைச்சல் கொடுமை தனித்தனியாக வரையறுக்கப்பட்டுள்ளது."
            ),
            StatuteMappingItem(
                ipcSection = "IPC Section 506",
                bnsSection = "BNS Section 351",
                offenceNameEn = "Criminal Intimidation",
                offenceNameTa = "குற்றவியல் மிரட்டல் (மிரட்டுதல்)",
                punishmentEn = "Up to 2 years / Threat of death up to 7 years",
                punishmentTa = "2 ஆண்டுகள் வரை / கொலை மிரட்டலுக்கு 7 ஆண்டுகள் வரை",
                isBailable = true,
                isCognizable = false,
                keyChangeEn = "Includes cyber harassment, extortion via online channels and anonymous threats.",
                keyChangeTa = "இணையவழி அச்சுறுத்தல் மற்றும் மிரட்டல்களும் இதில் அடங்கும்."
            ),
            StatuteMappingItem(
                ipcSection = "CrPC Section 154",
                bnsSection = "BNSS Section 173",
                offenceNameEn = "Information in cognizable cases (Zero FIR & e-FIR)",
                offenceNameTa = "ஜீரோ எஃப்.ஐ.ஆர் (Zero FIR) & மின்னணு எஃப்.ஐ.ஆர்",
                punishmentEn = "Mandatory Zero FIR across any police station regardless of jurisdiction",
                punishmentTa = "எல்லை வரம்பின்றி எந்த காவல் நிலையத்திலும் ஜீரோ FIR பதிவு செய்வது கட்டாயம்",
                isBailable = true,
                isCognizable = true,
                keyChangeEn = "Zero FIR and e-FIR formally codified with 3-day window for electronic signing.",
                keyChangeTa = "மின்னணு எஃப்.ஐ.ஆர் பதிவு செய்த 3 நாட்களுக்குள் கையெழுத்திட வேண்டும்."
            ),
            StatuteMappingItem(
                ipcSection = "Indian Evidence Act Sec 65B",
                bnsSection = "BSA Section 63",
                offenceNameEn = "Admissibility of Electronic Records",
                offenceNameTa = "மின்னணு ஆவணங்கள் மற்றும் சான்றுகளின் ஏற்புத்தன்மை",
                punishmentEn = "Statutory Certificate by Person in Charge of Device",
                punishmentTa = "சாதன உரிமையாளரின் சட்டப்பூர்வ பிரிவு 63 சான்றிதழ்",
                isBailable = true,
                isCognizable = true,
                keyChangeEn = "Electronic records given equal status as primary documentary evidence.",
                keyChangeTa = "மின்னணு ஆவணங்களுக்கு முதன்மை ஆவண சான்றுக்கு இணையான அந்தஸ்து வழங்கப்பட்டுள்ளது."
            )
        )
    }

    val filteredMappings = mappings.filter {
        it.ipcSection.contains(searchQuery, ignoreCase = true) ||
        it.bnsSection.contains(searchQuery, ignoreCase = true) ||
        it.offenceNameEn.contains(searchQuery, ignoreCase = true) ||
        it.offenceNameTa.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "BNS 2023 vs IPC 1860 சட்ட ஒப்பீடு" else "BNS 2023 vs IPC 1860 Navigator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1E36)
                            )
                        )
                        Text(
                            text = if (isTa) "புதிய குற்றவியல் சட்டங்கள் (BNS, BNSS & BSA)" else "Bharatiya Nyaya Sanhita & Bharatiya Nagarik Suraksha Sanhita",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF4A4E57))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("bns_ipc_back_btn")) {
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isTa) "பிரிவு எண் அல்லது குற்றத்தின் பெயரைத் தேடவும் (எ.கா: 420, 302, Cheating, Zero FIR)..." else "Search Section or Offence (e.g., 420, 302, Murder, Zero FIR, Theft)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF0F1E36)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF0F1E36))
                        }
                    }
                },
                colors = nyayaOutlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth().testTag("bns_search_input")
            )

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMappings) { item ->
                    StatuteMappingCard(item = item, isTa = isTa)
                }
            }
        }
    }
}

@Composable
private fun StatuteMappingCard(
    item: StatuteMappingItem,
    isTa: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
        border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Section Badges
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // IPC badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE2D8C7)
                ) {
                    Text(
                        text = item.ipcSection,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36)),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFFC05621), modifier = Modifier.size(16.dp))

                // BNS badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = item.bnsSection,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF15803D)),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isTa) item.offenceNameTa else item.offenceNameEn,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1E36)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (item.isBailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (item.isBailable) (if (isTa) "ஜாமீன் உண்டு (Bailable)" else "Bailable") else (if (isTa) "ஜாமீன் இல்லை (Non-Bailable)" else "Non-Bailable"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (item.isBailable) Color(0xFF15803D) else Color(0xFF991B1B)
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFE2D8C7)
                ) {
                    Text(
                        text = if (item.isCognizable) (if (isTa) "கைது அதிகாரம் (Cognizable)" else "Cognizable") else (if (isTa) "அனுமதி தேவை (Non-Cognizable)" else "Non-Cognizable"),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36)),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "⚖️ ${if (isTa) item.punishmentTa else item.punishmentEn}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFC05621)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFFD4CAB8))
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "✨ ${if (isTa) item.keyChangeTa else item.keyChangeEn}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF4A4E57),
                    lineHeight = 16.sp
                )
            )
        }
    }
}

