import os

onboarding_code = '''package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class OnboardingPageData(
    val id: Int,
    val titleEn: String,
    val subtitleEn: String,
    val icon: ImageVector
)

val ONBOARDING_PAGES = listOf(
    OnboardingPageData(
        1,
        "Multilingual Voice Intake",
        "Real-time Tamil (ta-IN) and English (en-IN) streaming legal consultation.",
        Icons.Default.Mic
    ),
    OnboardingPageData(
        2,
        "Dynamic Statutory Adjudication",
        "Comprehensive adjudication under BNS 2023, CPA 2019, and Model Tenancy Act.",
        Icons.Default.Gavel
    ),
    OnboardingPageData(
        3,
        "Cryptographic Evidence Vault",
        "Tamper-proof SHA-256 hashing admissible under Section 65B IT Act / Section 63 BSA.",
        Icons.Default.FolderShared
    ),
    OnboardingPageData(
        4,
        "Smart Petition Generator",
        "Procedural case readiness score and automatic court-ready notice generation.",
        Icons.Default.Description
    ),
    OnboardingPageData(
        5,
        "Emergency Golden Hour Protection",
        "Instant helpline dispatch to 1930 Cybercrime, 112 SOS, and 1915 Consumer Help.",
        Icons.Default.Shield
    )
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentPageIndex) {
        delay(3000L)
        if (currentPageIndex < ONBOARDING_PAGES.size - 1) {
            currentPageIndex += 1
        } else {
            onOnboardingFinished()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F1E36)
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = Color(0xFFE5A93C),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "JUSTRA",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFAF7F2),
                        letterSpacing = 1.sp
                    )
                )
            }

            val currentPage = ONBOARDING_PAGES[currentPageIndex]

            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn(animationSpec = androidx.compose.animation.core.tween(500)) togetherWith fadeOut(animationSpec = androidx.compose.animation.core.tween(500)) },
                label = "OnboardingPage"
            ) { page ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14181F)),
                    border = BorderStroke(1.dp, Color(0xFFE5A93C).copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5A93C).copy(alpha = 0.15f))
                                .border(2.dp, Color(0xFFE5A93C), CircleShape)
                        ) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null,
                                tint = Color(0xFFE5A93C),
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = page.titleEn,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFAF7F2),
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = page.subtitleEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFFAF7F2).copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                ONBOARDING_PAGES.forEachIndexed { index, _ ->
                    val isSelected = index == currentPageIndex
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFFE5A93C) else Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}
'''

with open('app/src/main/java/com/example/ui/screens/OnboardingScreen.kt', 'w', encoding='utf-8') as f:
    f.write(onboarding_code.strip() + '\n')
print("Created OnboardingScreen.kt")
