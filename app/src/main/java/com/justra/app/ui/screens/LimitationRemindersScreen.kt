package com.justra.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.SovereignNavy
import com.justra.app.ui.viewmodel.NyayaMateViewModel

data class LimitationClockItem(
    val id: String,
    val actNameEn: String,
    val actNameTa: String,
    val limitDays: Int,
    val descriptionEn: String,
    val descriptionTa: String
)

val LIMITATION_CLOCKS = listOf(
    LimitationClockItem(
        "sec138",
        "Sec 138 NI Act (Cheque Bounce)",
        "?????? 138 NI ?????? (?????? ???????????)",
        15,
        "15-day statutory notice cure window after cheque return memo",
        "?????? ????????? 15 ????????????? ??????????? ????????? ?????????? ????????"
    ),
    LimitationClockItem(
        "rti30",
        "RTI Act 2005 First Appeal",
        "????? ??????? ????? ?????? 2005",
        30,
        "30-day statutory response clock for Public Information Officer (PIO)",
        "30 ????????????? ????? ?????????????????? ????? ???????????? ?????????"
    ),
    LimitationClockItem(
        "mvact",
        "Motor Vehicles Act Claim",
        "???????? ???? ?????? ????????",
        180,
        "6-month limitation window for filing MACT accident claim petition",
        "??????? ????? 6 ?????????????? MACT ??? ??????? ????????? ????????"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitationRemindersScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTa) "??????????? ????????? ??????????????" else "Limitation Clocks & Reminders") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(LIMITATION_CLOCKS) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isTa) item.actNameTa else item.actNameEn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy
                                )
                            )
                            Badge(containerColor = Color(0xFFE5A93C)) {
                                Text(" Days", color = SovereignNavy, modifier = Modifier.padding(4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isTa) item.descriptionTa else item.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    if (isTa) "??????????? ?????? ?????????????? ( ???????)"
                                    else "Limitation Alarm Set for  Days",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "+ ??????????? ???" else "+ Set Limitation Reminder Alarm")
                        }
                    }
                }
            }
        }
    }
}
