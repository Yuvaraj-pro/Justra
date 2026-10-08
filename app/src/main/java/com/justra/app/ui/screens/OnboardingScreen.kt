package com.justra.app.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val id: Int,
    val titleEn: String,
    val subtitleEn: String,
    val icon: ImageVector
)

val ONBOARDING_PAGES = listOf(
    OnboardingPageData(
        1,
        "Voice & Text Intake",
        "Real-time Tamil (ta-IN) and English (en-IN) legal consultation intake.",
        Icons.Default.Mic
    ),
    OnboardingPageData(
        2,
        "Statutory Adjudication",
        "Comprehensive statutory breakdown under BNS 2023, CPA 2019, and Model Tenancy Act.",
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
        "Procedural case readiness score and automatic court-ready legal notice drafting.",
        Icons.Default.Description
    ),
    OnboardingPageData(
        5,
        "Emergency Golden Hour Protection",
        "Instant helpline dispatch to 1930 Cybercrime, 112 SOS, and 1915 Consumer Helpline.",
        Icons.Default.Shield
    )
)

/**
 * OnboardingScreen with smooth HorizontalPager page swipe support
 * and runtime Android permissions request launcher (Microphone, Location, Notifications).
 */
@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGES.size })

    // Build permissions list based on Android API level
    val permissionsToRequest = remember {
        mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }

    // Permission launcher for microphone, location & notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        onOnboardingFinished()
    }

    fun finishWithPermissions() {
        permissionLauncher.launch(permissionsToRequest)
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
                .padding(24.dp)
        ) {
            // Top Bar: Brand Logo & Skip Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                if (pagerState.currentPage < ONBOARDING_PAGES.size - 1) {
                    TextButton(onClick = { finishWithPermissions() }) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color(0xFFE5A93C),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Swipeable Horizontal Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = ONBOARDING_PAGES[pageIndex]
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14181F)),
                        border = BorderStroke(1.dp, Color(0xFFE5A93C).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(28.dp)
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
            }

            // Page Indicator Dots & Navigation Control Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Interactive Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    ONBOARDING_PAGES.forEachIndexed { index, _ ->
                        val isSelected = index == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (isSelected) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color(0xFFE5A93C) else Color.White.copy(alpha = 0.3f)
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                // Next / Get Started Action Button
                val isLastPage = pagerState.currentPage == ONBOARDING_PAGES.size - 1
                Button(
                    onClick = {
                        if (isLastPage) {
                            finishWithPermissions()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5A93C),
                        contentColor = Color(0xFF0F1E36)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isLastPage) "Get Started & Grant Permissions" else "Next",
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
    }
}
