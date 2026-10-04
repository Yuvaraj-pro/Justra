package com.justra.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SaffronAmberWarningContainer
import com.justra.app.ui.theme.SaffronAmberWarningText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorAccidentMactScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val indianCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    val caseTypes = listOf(
        "Fatal Accident (Loss of Dependency - Sarla Verma Matrix)",
        "Permanent Disability / Injury Claim",
        "Hit and Run Solatium Scheme (Sec 161 MV Act - ₹2,00,000)"
    )
    var selectedCaseType by remember { mutableStateOf(caseTypes[0]) }
    var isCaseTypeDropdownExpanded by remember { mutableStateOf(false) }

    var victimAgeInput by remember { mutableStateOf("32") }
    var monthlyIncomeInput by remember { mutableStateOf("30000") }
    var numberOfDependentsInput by remember { mutableStateOf("3") }
    var medicalExpensesInput by remember { mutableStateOf("150000") }
    var disabilityPercentageInput by remember { mutableStateOf("20") }

    val age = victimAgeInput.toIntOrNull() ?: 30
    val monthlyIncome = monthlyIncomeInput.toDoubleOrNull() ?: 0.0
    val dependents = numberOfDependentsInput.toIntOrNull() ?: 3
    val medicalExp = medicalExpensesInput.toDoubleOrNull() ?: 0.0
    val disabilityPct = disabilityPercentageInput.toDoubleOrNull() ?: 0.0

    val mactCompensation = calculateMactCompensation(
        caseType = selectedCaseType,
        age = age,
        monthlyIncome = monthlyIncome,
        dependents = dependents,
        medicalExpenses = medicalExp,
        disabilityPercentage = disabilityPct
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "வாகன விபத்து இழப்பீடு (MACT)" else "MACT Accident Claim Calculator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) "மோட்டார் வாகனச் சட்டம் 1988 & சரளா வர்மா ஃபார்முலா" else "Sarla Verma & Pranay Sethi Supreme Court Formula",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("mact_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepIndigoSlatePrimary)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PaleSandstoneVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleLanguage() }
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (isTa) "தமிழ்" else "English",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvorySurface)
            )
        },
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Statutory Alert
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                    border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CarCrash, contentDescription = null, tint = DeepIndigoSlatePrimary)
                        Text(
                            text = if (isTa) "விபத்து நடந்த 90 நாட்களுக்குள் காவல்துறை e-DAR (மின்னணு விரிவான விபத்து அறிக்கை) MACT தீர்ப்பாயத்தில் சமர்ப்பிக்க வேண்டும்."
                                   else "Under MV Act Sec 166(4), police must file e-DAR (Electronic Detailed Accident Report) within 90 days of accident.",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepIndigoSlatePrimary, lineHeight = 18.sp)
                        )
                    }
                }
            }

            // Inputs Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isTa) "விபத்து & வருமான விவரங்கள்" else "Accident & Income Parameters",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        ExposedDropdownMenuBox(
                            expanded = isCaseTypeDropdownExpanded,
                            onExpandedChange = { isCaseTypeDropdownExpanded = !isCaseTypeDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedCaseType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isTa) "விபத்து இழப்பீடு வகை" else "Claim Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCaseTypeDropdownExpanded) },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isCaseTypeDropdownExpanded,
                                onDismissRequest = { isCaseTypeDropdownExpanded = false }
                            ) {
                                caseTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedCaseType = type
                                            isCaseTypeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = victimAgeInput,
                                onValueChange = { victimAgeInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text(if (isTa) "வயது (Age)" else "Victim Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.weight(1f).testTag("mact_age_input")
                            )
                            OutlinedTextField(
                                value = numberOfDependentsInput,
                                onValueChange = { numberOfDependentsInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text(if (isTa) "சார்ந்திருப்போர்" else "Dependents") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = monthlyIncomeInput,
                            onValueChange = { monthlyIncomeInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text(if (isTa) "மாதாந்திர வருமானம் (Monthly Income ₹)" else "Monthly Income (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = medicalExpensesInput,
                            onValueChange = { medicalExpensesInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text(if (isTa) "மருத்துவ சிகிச்சை செலவு (Medical Bills ₹)" else "Medical Expenses (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (selectedCaseType.contains("Disability")) {
                            OutlinedTextField(
                                value = disabilityPercentageInput,
                                onValueChange = { disabilityPercentageInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text(if (isTa) "ஊனமுற்ற சதவீதம் (Disability %)" else "Disability Percentage (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Results Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                    border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isTa) "மதிப்பிடப்பட்ட MACT இழப்பீட்டுத் தொகை" else "Estimated MACT Award Computation",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        CalculationLine(label = if (isTa) "பயன்படுத்தப்பட்ட பெருக்கல் காரணி (Multiplier):" else "Applicable Multiplier:", value = "${mactCompensation.multiplier}")
                        CalculationLine(label = if (isTa) "எதிர்கால வளர்ச்சி வாய்ப்பு (Future Prospects):" else "Future Prospects Addition:", value = "${(mactCompensation.futureProspectsPct * 100).toInt()}%")
                        CalculationLine(label = if (isTa) "குடும்பச் சார்பு இழப்பு (Loss of Dependency):" else "Loss of Dependency:", value = indianCurrencyFormat.format(mactCompensation.lossOfDependency))
                        CalculationLine(label = if (isTa) "வழக்கமான இழப்பீடுகள் (கன்சார்டியம் & ஈமச்சடங்கு):" else "Conventional Heads (Consortium/Funeral):", value = indianCurrencyFormat.format(mactCompensation.conventionalHeads))
                        CalculationLine(label = if (isTa) "மருத்துவச் செலவுகள்:" else "Medical Reimbursement:", value = indianCurrencyFormat.format(mactCompensation.medicalReimbursement))

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isTa) "மொத்த கோரத்தக்க இழப்பீடு:" else "Total Claimable Compensation:",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                            )
                            Text(
                                text = indianCurrencyFormat.format(mactCompensation.totalCompensation),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TerracottaAccentSecondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MACT Computation", "Total MACT Compensation: ${indianCurrencyFormat.format(mactCompensation.totalCompensation)}\nMultiplier: ${mactCompensation.multiplier}\nLoss of Dependency: ${indianCurrencyFormat.format(mactCompensation.lossOfDependency)}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (isTa) "கணக்கீடு நகலெடுக்கப்பட்டது" else "Calculation copied", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("copy_mact_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "இழப்பீட்டு கணக்கீட்டை நகலெடு" else "Copy MACT Calculation Summary")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculationLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary))
    }
}

data class MactCalculationResult(
    val multiplier: Int,
    val futureProspectsPct: Double,
    val lossOfDependency: Double,
    val conventionalHeads: Double,
    val medicalReimbursement: Double,
    val totalCompensation: Double
)

private fun calculateMactCompensation(
    caseType: String,
    age: Int,
    monthlyIncome: Double,
    dependents: Int,
    medicalExpenses: Double,
    disabilityPercentage: Double
): MactCalculationResult {
    if (caseType.contains("Hit and Run")) {
        return MactCalculationResult(
            multiplier = 0,
            futureProspectsPct = 0.0,
            lossOfDependency = 200000.0,
            conventionalHeads = 0.0,
            medicalReimbursement = medicalExpenses,
            totalCompensation = 200000.0 + medicalExpenses
        )
    }

    // Sarla Verma Multiplier
    val multiplier = when {
        age <= 25 -> 18
        age <= 30 -> 17
        age <= 35 -> 16
        age <= 40 -> 15
        age <= 45 -> 14
        age <= 50 -> 13
        age <= 55 -> 11
        age <= 60 -> 9
        age <= 65 -> 7
        else -> 5
    }

    // Pranay Sethi Future Prospects
    val futureProspectsPct = when {
        age < 40 -> 0.40
        age <= 50 -> 0.25
        age <= 60 -> 0.10
        else -> 0.0
    }

    // Deduction for personal expenses based on dependents
    val personalDeductionFraction = when {
        dependents <= 1 -> 0.50
        dependents in 2..3 -> 0.33
        dependents in 4..6 -> 0.25
        else -> 0.20
    }

    val annualIncome = monthlyIncome * 12.0
    val incomeWithProspects = annualIncome * (1.0 + futureProspectsPct)
    val dependencyIncomePerYear = incomeWithProspects * (1.0 - personalDeductionFraction)

    val lossOfDependency = if (caseType.contains("Disability")) {
        (incomeWithProspects * multiplier) * (disabilityPercentage / 100.0)
    } else {
        dependencyIncomePerYear * multiplier
    }

    val conventionalHeads = 77000.0 // Loss of estate (16.5k) + Consortium (44k) + Funeral (16.5k) under Pranay Sethi adjusted
    val total = lossOfDependency + conventionalHeads + medicalExpenses

    return MactCalculationResult(
        multiplier = multiplier,
        futureProspectsPct = futureProspectsPct,
        lossOfDependency = lossOfDependency,
        conventionalHeads = conventionalHeads,
        medicalReimbursement = medicalExpenses,
        totalCompensation = total
    )
}
