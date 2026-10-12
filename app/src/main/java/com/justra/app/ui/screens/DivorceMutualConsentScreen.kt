package com.justra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun DivorceMutualConsentScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "பரஸ்பர விவாகரத்து 13B வழிகாட்டி" else "Mutual Consent Divorce Roadmap (Sec 13B)",
                subtitle = if (isTa) "இந்து திருமணச் சட்டம் பிரிவு 13B / சிறப்பு திருமணச் சட்டம் 28" else "HMA Sec 13B / SMA Sec 28 Statutory Process",
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
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "🕊️ 6 மாதங்கள் காத்திருப்பு காலம் தள்ளுபடி சலுகை" else "🕊️ Mandatory Separation & Cooling-off Waiver",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "1 ஆண்டு பிரிந்து வாழ்ந்திருக்க வேண்டும். உச்ச நீதிமன்ற உத்தரவுப்படி 6 மாத காத்திருப்பு காலத்தை தள்ளுபடி செய்யலாம்." else "Must be living separately for 1+ years. Supreme Court (Amardeep Singh case) allows waiving 6-month cooling period.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (isTa) "2-கட்ட குடும்ப நீதிமன்ற நடைமுறை:" else "Two-Motion Family Court Workflow:", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    Text(if (isTa) "• First Motion: பரஸ்பர ஒப்பந்த மனு மற்றும் வாக்குமூலம் பதிவு செய்தல்." else "• First Motion: Joint Petition filing & recording of statements.")
                    Text(if (isTa) "• Second Motion: 6 மாதங்களுக்குள் இறுதி தீர்ப்பு மற்றும் விவாகரத்து ஆணை." else "• Second Motion: Final decree & dissolution order within statutory window.")
                }
            }
        }
    }
}
