package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.nyayaOutlinedTextFieldColors
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtFeeCalculatorScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val indianCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = if (isTa) listOf("நுகர்வோர் தீர்ப்பாயம்", "சிவில் நீதிமன்றம்", "MSME வட்டி") 
                    else listOf("Consumer Forum", "Civil Suit", "MSMED 3x Interest")

    // Consumer Tab State
    var consumerClaimInput by remember { mutableStateOf("450000") }

    // Civil Suit Tab State
    var suitValuationInput by remember { mutableStateOf("1200000") }
    var isWomanLitigant by remember { mutableStateOf(false) }
    var isBplCertificateHolder by remember { mutableStateOf(false) }

    // MSME Tab State
    var invoicePrincipalInput by remember { mutableStateOf("250000") }
    var delayedDaysInput by remember { mutableStateOf("120") }
    var rbiBankRateInput by remember { mutableStateOf("6.5") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "நீதிமன்ற கட்டண & அதிகார வரம்பு கணக்கீடு" else "Court Fee & Jurisdiction Calculator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1E36)
                            )
                        )
                        Text(
                            text = if (isTa) "CPA 2019, TN Court Fees Act & MSMED Act" else "Statutory Pecuniary Slabs & Ad Valorem",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF4A4E57))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("court_fee_back_btn")) {
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
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFFFAF7F2),
                contentColor = Color(0xFF0F1E36),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFFC05621)
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) Color(0xFF0F1E36) else Color(0xFF4A4E57)
                                )
                            )
                        },
                        modifier = Modifier.testTag("court_fee_tab_$index")
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // CONSUMER TAB
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = if (isTa) "நுகர்வோர் பாதுகாப்பு சட்டம் 2019 - கட்டண விவரம்" else "Consumer Protection Act, 2019 (Section 35 & 47)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F1E36)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isTa) "பொருட்களின் மதிப்பு + இழப்பீட்டுத் தொகையை உள்ளிடவும்." else "Enter the value of goods/services plus aggregate compensation claimed.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    OutlinedTextField(
                                        value = consumerClaimInput,
                                        onValueChange = { consumerClaimInput = it.filter { ch -> ch.isDigit() } },
                                        label = { Text(if (isTa) "கோரப்படும் மொத்தத் தொகை (₹)" else "Total Claim Amount (₹)") },
                                        leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF0F1E36)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = nyayaOutlinedTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth().testTag("consumer_claim_input")
                                    )
                                }
                            }
                        }

                        val claimAmount = consumerClaimInput.toDoubleOrNull() ?: 0.0
                        val (forumName, statutoryFee, sectionRef) = calculateConsumerForum(claimAmount, isTa)

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFF0F1E36))
                                        Text(
                                            text = if (isTa) "அதிகார வரம்பு & கட்டண முடிவு" else "Jurisdictional Determination",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F1E36)
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    CalculationRow(
                                        label = if (isTa) "உரிய தீர்ப்பாயம் (Appropriate Forum):" else "Appropriate Forum:",
                                        value = forumName,
                                        isHighlighted = true
                                    )
                                    CalculationRow(
                                        label = if (isTa) "அரசு நீதிமன்றக் கட்டணம் (Court Fee):" else "Statutory Court Fee:",
                                        value = if (statutoryFee == 0.0) if (isTa) "₹0 (இலவசம்)" else "₹0 (Exempt)" else indianCurrencyFormat.format(statutoryFee),
                                        isHighlighted = true
                                    )
                                    CalculationRow(
                                        label = if (isTa) "சட்டப்பிரிவு குறிப்பு (Statutory Section):" else "Governing Section:",
                                        value = sectionRef
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Court Fee Calculation", "Claim: ₹$claimAmount\nForum: $forumName\nFee: ₹$statutoryFee\nRef: $sectionRef")
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, if (isTa) "விவரங்கள் நகலெடுக்கப்பட்டது" else "Calculation summary copied", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("copy_consumer_calc_btn")
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isTa) "கணக்கீட்டுச் சுருக்கத்தை நகலெடு" else "Copy Jurisdictional Summary")
                                    }
                                }
                            }
                        }

                        item {
                            ConsumerFeeGuideCard(isTa)
                        }
                    }

                    1 -> {
                        // CIVIL SUIT TAB
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = if (isTa) "தமிழ்நாடு நீதிமன்ற கட்டணச் சட்டம் 1955" else "Civil Court Valuation (TN Court Fees Act, 1955)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F1E36)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = suitValuationInput,
                                        onValueChange = { suitValuationInput = it.filter { ch -> ch.isDigit() } },
                                        label = { Text(if (isTa) "வழக்கின் பண மதிப்பு (Suit Valuation ₹)" else "Valuation of Suit (₹)") },
                                        leadingIcon = { Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF0F1E36)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = nyayaOutlinedTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth().testTag("civil_suit_input")
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked = isWomanLitigant,
                                            onCheckedChange = { isWomanLitigant = it },
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0F1E36))
                                        )
                                        Text(
                                            text = if (isTa) "பெண் வழக்காளியா? (NALSA கட்டண விலக்கு பரிசீலனை)" else "Woman Litigant (NALSA Sec 12 Exemption Check)",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF14181F))
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked = isBplCertificateHolder,
                                            onCheckedChange = { isBplCertificateHolder = it },
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0F1E36))
                                        )
                                        Text(
                                            text = if (isTa) "வறுமைக் கோட்டிற்கு கீழ் உள்ளவரா (BPL Card)?" else "Below Poverty Line (BPL / Indigent Litigant)",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF14181F))
                                        )
                                    }
                                }
                            }
                        }

                        val valuation = suitValuationInput.toDoubleOrNull() ?: 0.0
                        val civilCourtFee = calculateCivilCourtFee(valuation, isWomanLitigant || isBplCertificateHolder)

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = if (isTa) "சிவில் நீதிமன்ற கட்டண விவரம்" else "Civil Fee & Jurisdiction",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F1E36)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    CalculationRow(
                                        label = if (isTa) "உரிய நீதிமன்றம்:" else "Jurisdictional Court:",
                                        value = getCivilCourtForum(valuation, isTa)
                                    )
                                    CalculationRow(
                                        label = if (isTa) "மதிப்பிடப்பட்ட நீதிமன்றக் கட்டணம்:" else "Estimated Ad Valorem Fee:",
                                        value = if (civilCourtFee == 0.0) if (isTa) "₹0 (விலக்கு அளிக்கப்பட்டது)" else "₹0 (NALSA Exempt / Indigent)" else indianCurrencyFormat.format(civilCourtFee),
                                        isHighlighted = true
                                    )
                                    CalculationRow(
                                        label = if (isTa) "வழக்கு வகை:" else "Court Category:",
                                        value = if (valuation > 300000) if (isTa) "வணிக நீதிமன்ற வரம்பு (Commercial Court)" else "Commercial Division Eligible" else if (isTa) "வழக்கமான சிவில் நீதிமன்றம்" else "Standard Civil Court"
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // MSME TAB
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Percent, contentDescription = null, tint = Color(0xFFB45309))
                                        Text(
                                            text = if (isTa) "MSMED சட்டம் 2006 (பிரிவு 16 வட்டி கணக்கீடு)" else "MSMED Act, 2006 (Section 16 Statutory Interest)",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF78350F)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isTa) "சட்டப்படி 45 நாட்களுக்கு மேல் தாமதமாகும் நிலுவைத் தொகைக்கு RBI வங்கி வீதத்தை விட 3 மடங்கு கூட்டு வட்டி (மாதாந்திர கூட்டு வட்டி) பெற உரிமை உண்டு." 
                                               else "Under Section 16, buyer is liable to pay compound interest with monthly rests at 3 times of RBI Bank Rate from the appointed day.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4E57))
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = invoicePrincipalInput,
                                        onValueChange = { invoicePrincipalInput = it.filter { ch -> ch.isDigit() } },
                                        label = { Text(if (isTa) "ரசீது அசல் தொகை (Principal ₹)" else "Invoice Principal Amount (₹)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = nyayaOutlinedTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth().testTag("msme_principal_input")
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = delayedDaysInput,
                                            onValueChange = { delayedDaysInput = it.filter { ch -> ch.isDigit() } },
                                            label = { Text(if (isTa) "தாமத நாட்கள்" else "Delay Days") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = nyayaOutlinedTextFieldColors(),
                                            modifier = Modifier.weight(1f).testTag("msme_days_input")
                                        )
                                        OutlinedTextField(
                                            value = rbiBankRateInput,
                                            onValueChange = { rbiBankRateInput = it },
                                            label = { Text(if (isTa) "RBI வீதம் (%)" else "RBI Rate (%)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            colors = nyayaOutlinedTextFieldColors(),
                                            modifier = Modifier.weight(1f).testTag("msme_rbi_rate_input")
                                        )
                                    }
                                }
                            }
                        }

                        val principal = invoicePrincipalInput.toDoubleOrNull() ?: 0.0
                        val delayDays = delayedDaysInput.toDoubleOrNull() ?: 0.0
                        val rbiRate = rbiBankRateInput.toDoubleOrNull() ?: 6.5
                        val statutoryInterestRate = rbiRate * 3.0 // 3x RBI Bank Rate
                        val totalInterest = calculateMsmeInterest(principal, delayDays, statutoryInterestRate)
                        val totalPayable = principal + totalInterest

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = if (isTa) "சட்டப்பூர்வ மீட்புத் தொகை விவரம்" else "Statutory Recovery Statement",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F1E36)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    CalculationRow(
                                        label = if (isTa) "சட்டப்பிரிவு 16 வட்டி விகிதம் (3x RBI):" else "Section 16 Statutory Rate (3x RBI):",
                                        value = "$statutoryInterestRate % p.a."
                                    )
                                    CalculationRow(
                                        label = if (isTa) "சேர்ந்த வட்டித் தொகை:" else "Accrued Compound Interest:",
                                        value = indianCurrencyFormat.format(totalInterest),
                                        isHighlighted = true
                                    )
                                    CalculationRow(
                                        label = if (isTa) "மொத்த கோரப்படும் தொகை (அசல் + வட்டி):" else "Total Statutory Claim (Principal + Int):",
                                        value = indianCurrencyFormat.format(totalPayable),
                                        isHighlighted = true
                                    )
                                    CalculationRow(
                                        label = if (isTa) "விசாரணை மன்றம்:" else "Dispute Forum:",
                                        value = if (isTa) "MSEFC (சிறு குறு தொழில் சமரச கவுன்சில்)" else "MSEFC Samadhaan Portal"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculationRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
                color = Color(0xFF14181F)
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) Color(0xFF0F1E36) else Color(0xFF4A4E57)
            )
        )
    }
}

@Composable
private fun ConsumerFeeGuideCard(isTa: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
        border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isTa) "நுகர்வோர் தீர்ப்பாய சட்டப்பூர்வ அட்டவணை" else "Statutory Slabs Reference (CPA 2019)",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F1E36))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isTa) "• ₹5 லட்சம் வரை: கட்டணம் இல்லை (₹0)\n• ₹5L முதல் ₹10L வரை: ₹200\n• ₹10L முதல் ₹20L வரை: ₹400\n• ₹20L முதல் ₹50L வரை: ₹1,000 (மாவட்ட ஆணையம்)\n• ₹50L முதல் ₹2 கோடி வரை: மாநில ஆணையம் (₹2,000-₹5,000)\n• ₹2 கோடிக்கு மேல்: தேசிய ஆணையம் (NCDRC ₹7,500)"
                       else "• Up to ₹5 Lakhs: Nil (₹0)\n• ₹5L to ₹10L: ₹200\n• ₹10L to ₹20L: ₹400\n• ₹20L to ₹50L: ₹1,000 (District Commission)\n• ₹50L to ₹2 Crore: State Commission (₹2,000-₹5,000)\n• Above ₹2 Crore: National Commission (NCDRC ₹7,500)",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp, color = Color(0xFF4A4E57))
            )
        }
    }
}

private fun calculateConsumerForum(claimAmount: Double, isTa: Boolean): Triple<String, Double, String> {
    return when {
        claimAmount <= 500000 -> Triple(
            if (isTa) "மாவட்ட நுகர்வோர் ஆணையம் (DCDRC)" else "District Consumer Commission (DCDRC)",
            0.0,
            "CPA 2019 Sec 35 (Free for claims ≤ ₹5 Lakhs)"
        )
        claimAmount <= 1000000 -> Triple(
            if (isTa) "மாவட்ட நுகர்வோர் ஆணையம் (DCDRC)" else "District Consumer Commission (DCDRC)",
            200.0,
            "CPA 2019 Rule 7(1)"
        )
        claimAmount <= 2000000 -> Triple(
            if (isTa) "மாவட்ட நுகர்வோர் ஆணையம் (DCDRC)" else "District Consumer Commission (DCDRC)",
            400.0,
            "CPA 2019 Rule 7(1)"
        )
        claimAmount <= 5000000 -> Triple(
            if (isTa) "மாவட்ட நுகர்வோர் ஆணையம் (DCDRC)" else "District Consumer Commission (DCDRC)",
            1000.0,
            "CPA 2019 Sec 34 (Pecuniary Limit up to ₹50 Lakhs)"
        )
        claimAmount <= 20000000 -> Triple(
            if (isTa) "மாநில நுகர்வோர் ஆணையம் (SCDRC)" else "State Consumer Commission (SCDRC)",
            2500.0,
            "CPA 2019 Sec 47 (₹50 Lakhs to ₹2 Crore)"
        )
        else -> Triple(
            if (isTa) "தேசிய நுகர்வோர் ஆணையம் (NCDRC)" else "National Consumer Commission (NCDRC - New Delhi)",
            7500.0,
            "CPA 2019 Sec 58 (Claims above ₹2 Crore)"
        )
    }
}

private fun calculateCivilCourtFee(valuation: Double, isExempt: Boolean): Double {
    if (isExempt) return 0.0
    return when {
        valuation <= 100000 -> valuation * 0.03
        valuation <= 500000 -> 3000 + (valuation - 100000) * 0.05
        valuation <= 1000000 -> 23000 + (valuation - 500000) * 0.065
        else -> 55500 + (valuation - 1000000) * 0.075
    }
}

private fun getCivilCourtForum(valuation: Double, isTa: Boolean): String {
    return when {
        valuation <= 100000 -> if (isTa) "மாவட்ட முன்சீப் நீதிமன்றம் (District Munsif Court)" else "District Munsif Court (up to ₹1 Lakh)"
        valuation <= 1000000 -> if (isTa) "சார் நீதிமன்றம் (Subordinate Court)" else "Sub Court / Senior Civil Judge (up to ₹10 Lakhs)"
        else -> if (isTa) "மாவட்ட முதன்மை நீதிமன்றம் (Principal District Court)" else "Principal District Court / High Court Original Jurisdiction"
    }
}

private fun calculateMsmeInterest(principal: Double, delayDays: Double, annualRate: Double): Double {
    if (principal <= 0 || delayDays <= 0 || annualRate <= 0) return 0.0
    val months = delayDays / 30.0
    val monthlyRate = (annualRate / 100.0) / 12.0
    // Monthly compound interest formula: A = P * (1 + r)^n - P
    val amount = principal * Math.pow(1.0 + monthlyRate, months)
    return amount - principal
}

