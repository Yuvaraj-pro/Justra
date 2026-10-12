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
fun MedicalNegligenceClaimScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "மருத்துவ அலட்சிய இழப்பீடு மனு" else "Medical Negligence Compensation Assessor",
                subtitle = if (isTa) "நுகர்வோர் பாதுகாப்பு சட்டம் 2019 & NMC விதிகள்" else "Consumer Protection Act 2019 & Medical Council Guidelines",
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
                        text = if (isTa) "🏥 மருத்துவ ஆவணங்களை பெற நோயாளிக்கு முழு உரிமை" else "🏥 Statutory Right to Complete Medical Records",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "கேட்ட 72 மணி நேரத்திற்குள் மருத்துவமனை சிகிச்சை ஆவணங்களை வழங்க வேண்டும் (NMC பிரிவு 1.3.2)." else "Hospitals MUST provide complete medical case sheets & discharge summary within 72 hours of request.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                }
            }
        }
    }
}
