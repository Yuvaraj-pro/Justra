package com.example.ui.screens.citizenship

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DisputeCategory
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.DeepImperialNavy
import com.example.ui.theme.OnImperialNavy
import com.example.ui.theme.OnParchmentText
import com.example.ui.theme.OnSageHerbSuccess
import com.example.ui.theme.OnSandstoneSurfaceVariant
import com.example.ui.theme.ParchmentOutline
import com.example.ui.theme.PrimaryNavyContainer
import com.example.ui.theme.SageHerbSuccessContainer
import com.example.ui.theme.SandstoneSurface
import com.example.ui.theme.WarmParchmentBase
import com.example.ui.theme.WarmTerracotta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalTopicDetailScreen(
    topicId: String,
    currentLanguage: LanguagePreference,
    onNavigateBack: () -> Unit,
    onInitiateGrievance: (category: DisputeCategory) -> Unit,
    onNavigateToTimeline: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    val matchedCategory = DisputeCategory.values().find {
        it.id.equals(topicId, ignoreCase = true) || it.name.equals(topicId, ignoreCase = true)
    } ?: DisputeCategory.CONSUMER_GRIEVANCE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) matchedCategory.titleTa else matchedCategory.titleEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepImperialNavy,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        Text(
                            text = "Statutory Grounds & Procedural Remedies",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = OnSandstoneSurfaceVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("topic_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepImperialNavy
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier.testTag("topic_detail_language_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Toggle Language",
                            tint = DeepImperialNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmParchmentBase)
            )
        },
        containerColor = WarmParchmentBase,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Legal Classification Header Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DeepImperialNavy.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DeepImperialNavy
                            ) {
                                Text(
                                    text = "STATUTORY JURISDICTION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnImperialNavy
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Text(
                                text = "Code: ${matchedCategory.id.uppercase()}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = DeepImperialNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isTa) matchedCategory.descriptionTa else matchedCategory.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepImperialNavy,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "⚖️ ${matchedCategory.relevantAct}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmTerracotta
                            )
                        )
                    }
                }
            }

            // Procedural Direct Action Row
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onInitiateGrievance(matchedCategory) },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepImperialNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("topic_initiate_complaint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = OnImperialNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTa) "மனு தொடங்கு" else "Draft Notice",
                            fontSize = 12.sp,
                            color = OnImperialNavy
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateToTimeline,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("topic_view_timeline_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = DeepImperialNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTa) "காலவரிசை" else "Timeline",
                            fontSize = 12.sp,
                            color = DeepImperialNavy
                        )
                    }
                }
            }

            // Key Rights and Procedural Checkpoints
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isTa) "முக்கிய சட்ட உரிமைகள் & நடைமுறைகள்" else "Substantive Rights & Mandatory Steps",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepImperialNavy
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val checkpoints = listOf(
                            "1. Preserve All Communications" to "Retain invoices, transaction receipts, call records, and emails in Justra Evidence Vault.",
                            "2. Statutory Limitation Period" to "Ensure notice or petition is filed within the prescribed limitation (e.g., 2 years for Consumer Protection, 3 years for debt/claims).",
                            "3. Formal Notice Before Action" to "Issue a structured 15-day statutory demand notice before initiating formal proceedings in court or commission.",
                            "4. Jurisdiction & Appropriate Forum" to "File before the competent Territorial & Pecuniary authority based on your residence or cause of action."
                        )

                        checkpoints.forEach { (title, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = OnSageHerbSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepImperialNavy
                                        )
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = OnSandstoneSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
