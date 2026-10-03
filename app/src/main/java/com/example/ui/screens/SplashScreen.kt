package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.util.BilingualStrings
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    currentLanguage: LanguagePreference,
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isReadyToProceed by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "halo_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    LaunchedEffect(Unit) {
        delay(300)
        progress = 0.35f
        delay(400)
        progress = 0.75f
        delay(400)
        progress = 1.0f
        delay(300)
        isReadyToProceed = true
        delay(500)
        onNavigateNext()
    }

    Surface(
        color = Color(0xFFFAF7F2),
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen_root")
            .clickable { onNavigateNext() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background subtle gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFAF7F2),
                                Color(0xFFF3ECE1),
                                Color(0xFFFAF7F2)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated Emblem with pulsing golden halo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(140.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFC05621).copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(130.dp)
                            .scale(pulseScale)
                    ) {}

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF3ECE1),
                        border = BorderStroke(2.5.dp, Color(0xFFC05621)),
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Justra Emblem",
                                tint = Color(0xFF0F1E36),
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = BilingualStrings.t("app_title", currentLanguage),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F1E36),
                        letterSpacing = 1.2.sp
                    )
                )

                Text(
                    text = BilingualStrings.t("splash_motto", currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFFC05621),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                Text(
                    text = BilingualStrings.t("app_sub", currentLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF4A4E57),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Progress indicator
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFC05621),
                        trackColor = Color(0xFFF3ECE1),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = BilingualStrings.t("splash_loading", currentLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF4A4E57)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Section 65B & Keystore Trust Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3ECE1),
                    border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Sec 65B IT Act Ready • SHA-256 Hardware Sealed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F1E36),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Direct tap continue button at bottom
            AnimatedVisibility(
                visible = isReadyToProceed,
                enter = fadeIn(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Button(
                    onClick = onNavigateNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E36)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("splash_enter_button")
                ) {
                    Text(
                        text = BilingualStrings.t("splash_tap_continue", currentLanguage),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

