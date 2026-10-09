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
fun CyberBullyingPoshScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "பணியிட பாலியல் துன்புறுத்தல் POSH & சைபர் ஸ்டாக்கிங்" else "POSH Act 2013 & Cyber Stalking Cell",
                subtitle = if (isTa) "பணியிட பாதுகாப்பு & இணைய துன்புறுத்தல் புகார்" else "Internal Complaints Committee (ICC) & Cyber Cell",
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
                        text = if (isTa) "🛡️ POSH சட்டம் 2013 & BNS பிரிவு 78 சைபர் ஸ்டாக்கிங்" else "🛡️ POSH Act 2013 & BNS Sec 78 Cyber Stalking",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "பணியிடத்தில் 10+ ஊழியர்கள் இருந்தால் ICC கவுன்சில் அமைப்பது சட்டப்பூர்வ கடமை. ஆன்லைன் மிரட்டல் மீது BNS 78 பாயும்." else "Mandatory Internal Complaints Committee (ICC) for employers with 10+ staff. Cyber stalking carries up to 3 yrs under BNS 78.",
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
                    Text(if (isTa) "உடனடி சட்ட நடவடிக்கை பாதைகள்:" else "Immediate Statutory Action Protocols:", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    Text(if (isTa) "1. ICC கமிட்டியிடம் 90 நாட்களுக்குள் எழுத்துப்பூர்வ புகார் அளித்தல்." else "1. Submit formal complaint to Workplace ICC within 90 days of incident.")
                    Text(if (isTa) "2. அவசர உதவிக்கு 1930 / 181 பெண்கள் உதவி எண்ணை அழைத்தல்." else "2. Emergency Cyber Helpline 1930 / Women Helpline 181.")
                    Text(if (isTa) "3. திரைப் படபடப்பு (Screenshots) மற்றும் வாட்ஸ்அப் அரட்டைகளை 65B முறைப்படி சேமித்தல்." else "3. Preserve chat history & screenshots for Sec 65B BSA Evidence Certificate.")
                }
            }
        }
    }
}
