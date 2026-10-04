package com.justra.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.util.BilingualStrings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun LandingScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onGetStarted: () -> Unit,
    onLoginClick: () -> Unit,
    onDialEmergency1930: () -> Unit,
    onCategoryClick: (DisputeCategory) -> Unit,
    onMenuNavigate: ((route: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var expandedRightIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = BilingualStrings.t("app_title", currentLanguage),
                subtitle = BilingualStrings.t("app_sub", currentLanguage),
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onMenuNavigate = onMenuNavigate,
                modifier = Modifier.testTag("landing_top_bar")
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFFFAF7F2),
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF0F1E36)),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("landing_login_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF0F1E36),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isTa) "உள்நுழைக" else "Login",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F1E36)
                                )
                            )
                        }
                    }

                    Button(
                        onClick = onGetStarted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F1E36),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                            .testTag("landing_get_started_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isTa) "தொடங்குக" else "Get Started",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFFAF7F2),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("landing_scroll_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Banner with Deep Palette & High Legibility
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.5.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFC05621),
                                modifier = Modifier.size(10.dp)
                            ) {}
                            Text(
                                text = if (isTa) "இறையாண்மை சட்டப் பாதுகாப்பு மேடை" else "Sovereign On-Device Legal AI",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC05621)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = BilingualStrings.t("landing_hero_title", currentLanguage),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F1E36),
                                lineHeight = 28.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = BilingualStrings.t("landing_hero_sub", currentLanguage),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF4A4E57),
                                lineHeight = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Highlights Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFAF7F2),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "8+",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F1E36)
                                        )
                                    )
                                    Text(
                                        text = if (isTa) "இந்திய சட்டங்கள்" else "Statutes Mapped",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF4A4E57)
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFAF7F2),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Sec 65B",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    )
                                    Text(
                                        text = if (isTa) "சான்று உறுதி" else "SHA-256 Vault",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF4A4E57)
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFAF7F2),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "100%",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC05621)
                                        )
                                    )
                                    Text(
                                        text = if (isTa) "இருமொழி AI" else "Tamil & English",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF4A4E57)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // High-contrast Emergency 1930 Cyber Callout
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    border = BorderStroke(1.dp, Color(0xFF991B1B).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDialEmergency1930() }
                        .testTag("landing_emergency_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF991B1B),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Emergency Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTa) "சைபர் நிதி மோசடி? 1930 பொன்மணி நேரம் (Golden Hour)" else "Cyber Financial Scam? 1930 Golden Hour Relay",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                            )
                            Text(
                                text = if (isTa) "பணம் பறிபோனால் முதல் 24 மணிநேரத்தில் டயல் செய்து முடக்கவும்" else "Direct OS dialer to freeze unauthorized bank & UPI transactions immediately",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4A4E57)
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF991B1B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 3-Step Sovereign Legal Flow
            item {
                Text(
                    text = if (isTa) "3 எளிய படிநிலைகளில் சட்ட நடவடிக்கை" else "3-Step Sovereign Redressal Lifecycle",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1E36)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StepChip(
                        number = "1",
                        title = if (isTa) "குரல்/எழுத்து உட்கிரகிப்பு" else "Intake & Triage",
                        subtitle = if (isTa) "AI பகுப்பாய்வு" else "AI Extraction",
                        modifier = Modifier.weight(1f)
                    )
                    StepChip(
                        number = "2",
                        title = if (isTa) "சான்று பெட்டகம்" else "Sec 65B Vault",
                        subtitle = if (isTa) "SHA-256 முத்திரை" else "Crypto Seal",
                        modifier = Modifier.weight(1f)
                    )
                    StepChip(
                        number = "3",
                        title = if (isTa) "நீதிமன்ற மனு" else "Statutory Draft",
                        subtitle = if (isTa) "PDF பதிவிறக்கம்" else "Signed Export",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Genuine Citizen Statutory Rights & Procedural Protections Section
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF0F1E36),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = BilingualStrings.t("citizen_rights_title", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F1E36)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val citizenRights = listOf(
                            Triple(
                                if (isTa) "1. சட்டப்பூர்வ நோட்டீஸ் உரிமை (Sec 138 NI Act / CPA 2019)" else "1. Right to Statutory Demand Notice (Sec 138 NI Act / CPA 2019)",
                                if (isTa) "காசோலை பவுன்ஸ் மற்றும் நுகர்வோர் வழக்கிற்கு முன் கட்டாய 15 நாள் வக்கீல் நோட்டீஸ் அனுப்பும் உரிமை." else "Enforces mandatory 15-day pre-litigation notice before filing cheque bounce or consumer defect cases.",
                                Icons.Default.Gavel
                            ),
                            Triple(
                                if (isTa) "2. தகவல் அறியும் உரிமை (RTI Act 2005 Sec 6)" else "2. Right to Information (RTI Act 2005 Sec 6)",
                                if (isTa) "அரசு அலுவலக ஆவணங்களை 30 நாட்களுக்குள் பெறும் குடிமக்களின் சட்டப்பூர்வ உரிமை." else "Empowers citizens to demand public records with a strict 30-day statutory clock for PIO responses.",
                                Icons.Default.CheckCircle
                            ),
                            Triple(
                                if (isTa) "3. மின்னணு சான்று பாதுகாப்பு (BSA 2023 Sec 63 / IT Act Sec 65B)" else "3. Right to Evidentiary Integrity (Bharatiya Sakshya Adhiniyam Sec 63 / IT Act Sec 65B)",
                                if (isTa) "நீதிமன்றத்தில் டிஜிட்டல் சான்றுகளின் உண்மைத்தன்மையை உறுதி செய்யும் குறியாக்க சான்றிதழ் உரிமை." else "Mandates cryptographic timestamping & certificate validation for digital evidence admissible in court.",
                                Icons.Default.Security
                            ),
                            Triple(
                                if (isTa) "4. உடனடி சைபர் நிதி முடக்கம் (1930 Golden Hour Protocol)" else "4. Right to Immediate Cyber Freeze (1930 Golden Hour Protocol)",
                                if (isTa) "சைபர் மோசடி நடந்த 24 மணிநேரத்திற்குள் 1930 மூலம் வங்கி கணக்கை உடனடியாக முடக்கும் உரிமை." else "Immediate OS dialer and bank relay to freeze fraudulent digital transactions within the golden hour.",
                                Icons.Default.Warning
                            ),
                            Triple(
                                if (isTa) "5. வாடகைதாரர் வெளியேற்ற பாதுகாப்பு (Model Tenancy Act)" else "5. Tenant Eviction Protection (Model Tenancy Act)",
                                if (isTa) "நீதிமன்ற உத்தரவின்றி வாடகைதாரரை தன்னிச்சையாக வெளியேற்றுவதைத் தடுக்கும் சட்டப் பாதுகாப்பு." else "Prevents arbitrary rent escalation and unlawful eviction without formal Rent Authority orders.",
                                Icons.Default.Lock
                            )
                        )

                        citizenRights.forEachIndexed { index, (title, desc, icon) ->
                            val isExpanded = expandedRightIndex == index
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
                                border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { expandedRightIndex = if (isExpanded) null else index }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = Color(0xFF0F1E36),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F1E36)
                                                )
                                            )
                                        }
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = Color(0xFF4A4E57)
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = fadeIn() + expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF4A4E57)
                                            ),
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Grouped Dispute Categories with Progressive Disclosure
            item {
                Text(
                    text = if (isTa) "சட்ட உதவிப் பிரிவுகள் (Dispute Categories)" else "Statutory Dispute Categories",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1E36)
                    )
                )
            }

            items(DisputeCategory.values().toList()) { category ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(category) }
                        .testTag("landing_category_${category.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFAF7F2),
                            border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = Color(0xFF0F1E36),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTa) category.titleTa else category.titleEn,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F1E36)
                                )
                            )
                            Text(
                                text = if (isTa) category.descriptionTa else category.descriptionEn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4A4E57)
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = category.relevantAct,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFC05621),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF0F1E36),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepChip(
    number: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)),
        border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF0F1E36),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F1E36),
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = Color(0xFF4A4E57),
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
        }
    }
}

