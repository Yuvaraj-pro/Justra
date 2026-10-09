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
fun ChequeBounceNoticeWizardScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var chequeAmount by remember { mutableStateOf("150000") }
    var chequeNumber by remember { mutableStateOf("458921") }
    var bankName by remember { mutableStateOf("State Bank of India") }
    var returnReason by remember { mutableStateOf("Funds Insufficient (Sec 138)") }
    var drawerName by remember { mutableStateOf("Ramesh Kumar") }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "செக் மோசடி 138 அறிவிப்பு வழிகாட்டி" else "Sec 138 Cheque Bounce Notice Builder",
                subtitle = if (isTa) "மாற்றுச்சீட்டுச் சட்டம் பிரிவு 138 சட்டப்பூர்வ அறிவிப்பு" else "Negotiable Instruments Act 1881 Statutory Notice",
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
                        text = if (isTa) "📜 30 நாட்களுக்குள் சட்டப்பூர்வ அறிவிப்பு கட்டாயம்" else "📜 Mandatory 30-Day Demand Notice Rule",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "செக் திரும்பிய வங்கியின் Memo கிடைத்த 30 நாட்களுக்குள் 15 நாட்கள் அவகாச அறிவிப்பு அனுப்ப வேண்டும்." else "Notice must be dispatched within 30 days of Bank Memo receipt, demanding repayment within 15 days.",
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
                        value = drawerName,
                        onValueChange = { drawerName = it },
                        label = { Text(if (isTa) "செக் வழங்கியவர் பெயர் (Drawer)" else "Drawer Name (Opposing Party)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = chequeAmount,
                        onValueChange = { chequeAmount = it },
                        label = { Text(if (isTa) "செக் தொகை (Cheque Amount ₹)" else "Cheque Amount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = chequeNumber,
                        onValueChange = { chequeNumber = it },
                        label = { Text(if (isTa) "செக் எண் (Cheque Number)" else "Cheque No.") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = justraOutlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text(if (isTa) "வங்கி பெயர் (Drawn Bank)" else "Drawn Bank Name") },
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
                    Text(if (isTa) "தயாரிக்கப்பட்ட 138 சட்ட அறிவிப்பு வரைவு" else "Generated Statutory Demand Draft", fontWeight = FontWeight.Bold, color = SovereignNavy)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "LEGAL DEMAND NOTICE UNDER SECTION 138 OF NEGOTIABLE INSTRUMENTS ACT, 1881\n\nTo: $drawerName\nSub: Dishonour of Cheque No. $chequeNumber drawn on $bankName for ₹$chequeAmount.\n\nTake Notice that Cheque No. $chequeNumber for ₹$chequeAmount was dishonoured with memo 'Insufficiency of Funds'. You are hereby called upon to pay ₹$chequeAmount within 15 days of receipt of this notice, failing which criminal proceedings under Section 138 NI Act shall be instituted in competent Judicial Magistrate Court.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF14181F)
                    )
                }
            }
        }
    }
}
