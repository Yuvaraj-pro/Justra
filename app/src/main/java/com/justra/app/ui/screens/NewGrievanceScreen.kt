package com.justra.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.SovereignNavy
import com.justra.app.ui.viewmodel.NyayaMateViewModel
import com.justra.app.util.LegalInputValidator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewGrievanceScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onBackClick: () -> Unit,
    onCreated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    var title by remember { mutableStateOf("") }
    var opposingParty by remember { mutableStateOf("") }
    var monetaryClaim by remember { mutableStateOf("") }
    var incidentDate by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }

    val personas = listOf(
        "Citizen Complainant" to "???????? ???????????",
        "Advocate/Counsel" to "??????????",
        "MSME Owner" to "MSME ??????????",
        "Cyber Victim" to "????? ????????????????"
    )
    var selectedPersona by remember { mutableStateOf(personas[0].first) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTa) "????? ?????? ?????? ??????????" else "Initiate Legal Grievance") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SovereignNavy,
                    titleContentColor = Color(0xFFFAF7F2),
                    navigationIconContentColor = Color(0xFFFAF7F2)
                )
            )
        },
        containerColor = Color(0xFFFAF7F2)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "??????????? ???? (Legal Standing)" else "Select Legal Persona",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        personas.forEach { (en, ta) ->
                            val isSelected = selectedPersona == en
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedPersona = en },
                                label = { Text(if (isTa) ta else en) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SovereignNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (isTa) "?????? ??????? *" else "Dispute Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = opposingParty,
                        onValueChange = { opposingParty = it },
                        label = { Text(if (isTa) "????????????? ????? *" else "Opposing Party Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = monetaryClaim,
                            onValueChange = { monetaryClaim = it },
                            label = { Text(if (isTa) "???????????? ???? (?)" else "Monetary Claim (?)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = incidentDate,
                            onValueChange = { incidentDate = it },
                            label = { Text(if (isTa) "????? ????" else "Incident Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text(if (isTa) "????? ????????? (????????? 12 ??????????) *" else "Factual Incident Summary (Min 12 chars) *") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        maxLines = 5
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (title.isBlank() || opposingParty.isBlank()) {
                                errorMessage = if (isTa) "?????????? ??????? ?????? ???????????? ??????????" else "Please fill all required fields"
                                return@Button
                            }
                            val valRes = LegalInputValidator.validateLegalInput(summary)
                            if (valRes is LegalInputValidator.ValidationResult.Invalid) {
                                errorMessage = if (isTa) valRes.errorMessageTa else valRes.errorMessageEn
                                return@Button
                            }
                            viewModel.createNewDispute(
                                title = title,
                                category = DisputeCategory.CONSUMER_GRIEVANCE,
                                incidentDate = incidentDate.ifBlank { null },
                                opposingParty = opposingParty.ifBlank { null },
                                estimatedClaimAmount = monetaryClaim.ifBlank { null },
                                factualSummary = summary,
                                demandedRelief = "Full refund/compensation",
                                onCreated = { caseId ->
                                    Toast.makeText(context, if (isTa) "?????? ??????????? ??????????????" else "Grievance Recorded", Toast.LENGTH_SHORT).show()
                                    onCreated(caseId)
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isTa) "?????? ????? ????? & ????? ????? ?????" else "Submit & Proceed to Smart Complaint")
                    }
                }
            }
        }
    }
}
