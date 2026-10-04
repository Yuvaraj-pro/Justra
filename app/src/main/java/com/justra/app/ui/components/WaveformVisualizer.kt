package com.justra.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.LegalAmberLight
import com.justra.app.ui.theme.LegalAmberSecondary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom Canvas-based WaveformVisualizer Composable that observes real-time amplitude data
 * from the microphone during audio recording to provide immediate visual feedback.
 *
 * Visual Features:
 * - Dynamic harmonic sine wave curves driven by incoming audio decibels/amplitudes.
 * - Symmetrical frequency spectrum analyzer bars rendered via DrawScope with gradient brushes.
 * - Pulsing microphone center halo that expands proportionally to instant speech volume.
 * - Live decibel (dB) and input frequency simulation indicators.
 * - Smooth dampening and interpolations across amplitude transitions.
 */
@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    isRecording: Boolean = true,
    language: LanguagePreference = LanguagePreference.ENGLISH,
    modifier: Modifier = Modifier
) {
    val isTa = language == LanguagePreference.TAMIL

    // Phase animation for continuous fluid wave oscillation
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformPhysicsTransition")

    val wavePhase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SineWavePhasePrimary"
    )

    val wavePhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -(2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SineWavePhaseSecondary"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MicHaloPulse"
    )

    // Animated smoothed amplitude envelope for responsive visual changes
    val latestAmplitude = amplitudes.lastOrNull() ?: 0.35f
    val smoothedAmplitude = remember { Animatable(0.35f) }

    LaunchedEffect(latestAmplitude) {
        smoothedAmplitude.animateTo(
            targetValue = latestAmplitude.coerceIn(0.1f, 1.0f),
            animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing)
        )
    }

    val currentAmp = smoothedAmplitude.value
    val calculatedDb = (currentAmp * 55f + 35f).toInt().coerceIn(30, 95)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DeepIndigoSlatePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("custom_canvas_waveform_visualizer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Live Recording & Decibel Feedback
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TerracottaAccentSecondary)
                    )
                    Text(
                        text = if (isTa) "நேரடி குரல் அலைவரிசை (Microphone Active)" else "Real-Time Microphone Waveform",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SageGreenSuccessContainer.copy(alpha = 0.9f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = SageGreenSuccessText,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "$calculatedDb dB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SageGreenSuccessText,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Custom Canvas Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A)) // Deep Slate Enclave Canvas
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .testTag("waveform_canvas_surface")
                ) {
                    val width = size.width
                    val height = size.height
                    val midY = height / 2f
                    val effectiveAmp = currentAmp

                    // 1. Grid / Baseline reference lines
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, midY),
                        end = Offset(width, midY),
                        strokeWidth = 1f
                    )

                    // 2. Multi-layered Fluid Sinusoidal Waveforms (Background Harmonics)
                    val primaryWavePath = Path()
                    val secondaryWavePath = Path()
                    val wavePoints = 80
                    val dx = width / wavePoints

                    primaryWavePath.moveTo(0f, midY)
                    secondaryWavePath.moveTo(0f, midY)

                    for (i in 0..wavePoints) {
                        val x = i * dx
                        val progress = i.toFloat() / wavePoints

                        // Windowing bell curve so wave tapers gently at the left & right edges
                        val envelope = sin(progress * PI).toFloat()

                        val y1 = midY + sin(progress * 4 * PI + wavePhase1).toFloat() *
                                (height * 0.38f * effectiveAmp * envelope)

                        val y2 = midY + cos(progress * 3 * PI + wavePhase2).toFloat() *
                                (height * 0.28f * effectiveAmp * envelope)

                        primaryWavePath.lineTo(x, y1)
                        secondaryWavePath.lineTo(x, y2)
                    }

                    // Draw Harmonic Waves with Glowing Shaders
                    drawPath(
                        path = secondaryWavePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF64748B).copy(alpha = 0.3f),
                                LegalAmberSecondary.copy(alpha = 0.6f),
                                Color(0xFF64748B).copy(alpha = 0.3f)
                            )
                        ),
                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                    )

                    drawPath(
                        path = primaryWavePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                TerracottaAccentSecondary.copy(alpha = 0.4f),
                                TerracottaAccentSecondary,
                                LegalAmberLight,
                                TerracottaAccentSecondary.copy(alpha = 0.4f)
                            )
                        ),
                        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                    )

                    // 3. Symmetrical Center-Out Frequency Spectrum Bars
                    val numBars = 32
                    val barWidth = 3.5.dp.toPx()
                    val spacing = (width - (numBars * barWidth)) / (numBars + 1)

                    val barBrush = Brush.verticalGradient(
                        colors = listOf(
                            LegalAmberLight,
                            TerracottaAccentSecondary,
                            Color(0xFFE64A19)
                        )
                    )

                    for (i in 0 until numBars) {
                        val barIndexFromCenter = abs(i - (numBars / 2))
                        val centerWeight = 1.0f - (barIndexFromCenter.toFloat() / (numBars / 2)) * 0.45f

                        // Extract amplitude from input list or compute wave sample
                        val sampleAmp = if (amplitudes.isNotEmpty()) {
                            amplitudes[i % amplitudes.size]
                        } else {
                            (sin(i * 0.4f + wavePhase1).toFloat().coerceAtLeast(0.1f) * effectiveAmp)
                        }

                        val barHeight = ((sampleAmp * height * 0.72f * centerWeight)).coerceIn(4.dp.toPx(), height * 0.85f)
                        val barX = spacing + i * (barWidth + spacing)
                        val topY = midY - (barHeight / 2f)

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(barX, topY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }

                    // 4. Center Mic Focal Glow Pulse
                    val centerFocalRadius = (16.dp.toPx() * pulseGlow * effectiveAmp).coerceIn(10.dp.toPx(), 28.dp.toPx())
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                TerracottaAccentSecondary.copy(alpha = 0.45f),
                                Color.Transparent
                            ),
                            center = Offset(width / 2f, midY),
                            radius = centerFocalRadius * 1.6f
                        ),
                        radius = centerFocalRadius * 1.6f,
                        center = Offset(width / 2f, midY)
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(width / 2f, midY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle status prompt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isTa) "பேசவும்... உங்கள் குரல் சட்டரீதியாக பதிவு செய்யப்படுகிறது" else "Speak clearly... capturing audio evidence & facts",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )

                Text(
                    text = "${amplitudes.size} frames",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}
