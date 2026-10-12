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
fun GratuityPFCalculatorScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var lastSalary by remember { mutableStateOf("45000") }
    var serviceYears by remember { mutableStateOf("6") }

    val gratuityAmount = remember(lastSalary, serviceYears) {
        val sal = lastSalary.toDoubleOrNull() ?: 0.0
        val yrs = serviceYears.toDoubleOrNull() ?: 0.0
        if (yrs >= 5.0) (sal * 15 / 26) * yrs else 0.0
    }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "பணிக்கொடை (Gratuity) & PF கணக்கீடு" else "Gratuity & EPF Interest Claim Calculator",
                subtitle = if (isTa) "பணிக்கொடை வழங்கல் சட்டம் 1972" else "Payment of Gratuity Act 1972 & EPF Act 1952",
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
                        text = if (isTa) "💼 5 ஆண்டுகள் முடித்த ஊழியருக்கு பணிக்கொடை கட்டாயம்" else "💼 Mandatory Gratuity upon 5 Years Service",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "நிறுவனம் 30 நாட்களுக்குள் பணிக்கொடை வழங்காவிட்டால் 10% வட்டியுடன் வழங்க வேண்டும்." else "Employer must pay gratuity within 30 days of exit, else simple interest (10%) applies under Sec 7(3A).",
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = lastSalary,
                        onValueChange = { lastSalary = it },
                        label = { Text(if (isTa) "கடைசி அடிப்படை சம்பளம் + DA (₹)" else "Last Drawn Basic + DA (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = serviceYears,
                        onValueChange = { serviceYears = it },
                        label = { Text(if (isTa) "பணிபுரிந்த ஆண்டுகள் (Service Years)" else "Completed Service Years") },
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
                    Text(if (isTa) "சட்டப்பூர்வ பணிக்கொடை தொகை:" else "Calculated Statutory Gratuity:", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹ ${String.format("%.2f", gratuityAmount)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = AccentTerracotta)
                    )
                }
            }
        }
    }
}
