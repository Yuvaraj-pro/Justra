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
fun SeniorCitizenMaintenanceScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "மூத்த குடிமக்கள் பராமரிப்பு தீர்ப்பாயம்" else "Senior Citizens Maintenance Tribunal Wizard",
                subtitle = if (isTa) "பெற்றோர் மற்றும் மூத்த குடிமக்கள் நலச் சட்டம் 2007" else "MWPSC Act 2007 Maintenance & Property Protection",
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
                colors = CardDefaults.cardColors(containerColor = SovereignNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "👴 90 நாட்களுக்குள் பராமரிப்புத் தொகை & சொத்து ரத்து" else "👴 90-Day Maintenance Order & Property Transfer Revocation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "RDO தீர்ப்பாயத்தில் வழக்கறிஞர் இல்லாமல் மனு தாக்கல் செய்யலாம். மாதம் ரூ. 10,000 வரை ஜீவனாம்சம் பெற உரிமை." else "Direct petition to Sub-Divisional Magistrate (RDO Tribunal). Revoke gift deeds if children refuse maintenance under Sec 23.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
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
                    Text(if (isTa) "சட்டப்பூர்வ உரிமைகள்:" else "Statutory Protections Available:", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    Text(if (isTa) "• பிரிவு 23: பராமரிக்கத் தவறினால் தானமாக வழங்கப்பட்ட சொத்து பத்திரம் ரத்து செய்யப்படும்." else "• Sec 23: Gift/Transfer of property to heirs declared VOID if basic amenities denied.")
                    Text(if (isTa) "• பிரிவு 24: மூத்த குடிமக்களை கைவிடுவது குற்றவியல் குற்றம் (சிறை தண்டனை)." else "• Sec 24: Abandonment of senior citizen is a cognizable criminal offense.")
                    Text(if (isTa) "• அவசர உதவி: மூத்த குடிமக்கள் தேசிய உதவி எண் 14567." else "• Elderline Helpline: Dial 14567 for immediate assistance.")
                }
            }
        }
    }
}
