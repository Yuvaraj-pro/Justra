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
fun RERAHomebuyerDisputeScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var totalPaid by remember { mutableStateOf("5000000") }
    var delayMonths by remember { mutableStateOf("18") }

    val interestRate = 10.75 // MCLR + 2%
    val calculatedCompensation = remember(totalPaid, delayMonths) {
        val paid = totalPaid.toDoubleOrNull() ?: 0.0
        val months = delayMonths.toDoubleOrNull() ?: 0.0
        (paid * (interestRate / 100) * (months / 12))
    }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "RERA ரியல் எஸ்டேட் தாமத இழப்பீடு கணக்கீடு" else "RERA Homebuyer Delay Penalty Calculator",
                subtitle = if (isTa) "RERA சட்டம் 2016 பிரிவு 18 கட்டட தாமத வட்டி கோரிக்கை" else "Real Estate (Regulation and Development) Act 2016 Sec 18",
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
                        text = if (isTa) "🏠 RERA பிரிவு 18: முழுப்பணம் திரும்பப் பெறுதல் அல்லது மாத வட்டி" else "🏠 RERA Sec 18 Statutory Interest & Refund Right",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "ஒப்பந்தப்படி வீடு ஒப்படைக்கப்படாவிட்டால், செலுத்திய தொகையை வட்டியுடன் திரும்பப் பெறலாம் அல்லது தாமத காலத்திற்கு வட்டி பெறலாம்." else "Homebuyers can demand 100% refund with interest OR monthly interest compensation for every month of delay under Sec 18 RERA.",
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = totalPaid,
                        onValueChange = { totalPaid = it },
                        label = { Text(if (isTa) "பில்டருக்கு செலுத்திய மொத்த தொகை (₹)" else "Total Amount Paid to Builder (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = delayMonths,
                        onValueChange = { delayMonths = it },
                        label = { Text(if (isTa) "தாமத மாதங்கள் (Delay Months)" else "Months of Delay past Agreed Possession Date") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (isTa) "RERA சட்டப்பூர்வ தாமத இழப்பீட்டு தொகை:" else "Calculated RERA Statutory Delay Compensation:", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹ ${String.format("%.2f", calculatedCompensation)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = AccentTerracotta)
                    )
                }
            }
        }
    }
}
