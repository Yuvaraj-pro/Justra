package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.DeepIndigoSlatePrimary
import com.example.ui.theme.LegalAmberSecondary
import com.example.ui.theme.PaleSandstoneVariant
import com.example.ui.theme.PrimaryContainerSlate
import com.example.ui.theme.SageGreenSuccessContainer
import com.example.ui.theme.SageGreenSuccessText
import com.example.ui.theme.SaffronAmberWarningContainer
import com.example.ui.theme.SaffronAmberWarningText
import com.example.ui.theme.TerracottaAccentSecondary
import com.example.ui.theme.WarmIvorySurface
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Interactive Real-Time Visual Feedback Waveform Component using Canvas API.
 * Renders multi-layered harmonic sine waves, dynamic frequency spectrum bars,
 * interactive touch ripples, and live Tamil audio input processing confirmation.
 */
@Composable
fun InteractiveTamilVoiceWaveformVisualizer(
    isRecording: Boolean,
    currentLanguage: LanguagePreference,
    amplitudes: List<Float> = emptyList(),
    onStopRecording: () -> Unit,
    onCancelRecording: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    // Infinite transitions for continuous fluid wave physics
    val infiniteTransition = rememberInfiniteTransition(label = "TamilWaveformPhysics")

    val phaseShift1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PrimaryPhase"
    )

    val phaseShift2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -(2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SecondaryPhase"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CenterPulseGlow"
    )

    // Interactive user touch point modulation
    var touchX by remember { mutableFloatStateOf(-1f) }
    var touchY by remember { mutableFloatStateOf(-1f) }
    var touchModulationFactor by remember { mutableFloatStateOf(1f) }

    // Dynamic decibel simulation from amplitude list
    val currentDb = remember(amplitudes, touchModulationFactor) {
        val avgAmp = if (amplitudes.isEmpty()) 0.45f else amplitudes.average().toFloat()
        ((avgAmp * 50f + 35f) * touchModulationFactor).coerceIn(30f, 92f).toInt()
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
        border = BorderStroke(1.5.dp, TerracottaAccentSecondary.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_voice_waveform_visualizer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Status Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TerracottaAccentSecondary,
                        modifier = Modifier.size(10.dp)
                    ) {}
                    Text(
                        text = if (isTa) "தமிழ் குரல் செயலாக்கம்..." else "Tamil Voice Processing...",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PaleSandstoneVariant,
                    border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "$currentDb dB • ${if (isTa) "நேரலை உள்ளீடு" else "Live Audio"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Canvas Waveform Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DeepIndigoSlatePrimary)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                touchX = offset.x
                                touchY = offset.y
                                touchModulationFactor = 1.4f
                                tryAwaitRelease()
                                touchModulationFactor = 1f
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                touchX = change.position.x
                                touchY = change.position.y
                                touchModulationFactor = 1.3f
                            },
                            onDragEnd = {
                                touchModulationFactor = 1f
                            }
                        )
                    }
                    .testTag("waveform_canvas_surface")
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val midY = canvasHeight / 2f

                    // 1. Draw subtle background coordinate grid lines
                    val gridLines = 5
                    for (i in 1..gridLines) {
                        val y = (canvasHeight / (gridLines + 1)) * i
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f
                        )
                    }

                    // 2. Draw animated frequency equalizer bars across width
                    val barCount = 28
                    val barSpacing = canvasWidth / barCount
                    val barWidth = barSpacing * 0.55f

                    for (i in 0 until barCount) {
                        val sampleAmp = if (amplitudes.isNotEmpty() && i < amplitudes.size) {
                            amplitudes[i % amplitudes.size]
                        } else {
                            val normX = i.toFloat() / barCount
                            abs(sin(normX * 4 * PI + phaseShift1).toFloat()) * 0.7f + 0.2f
                        }

                        // Touch proximity boost
                        val barCenterX = i * barSpacing + barWidth / 2f
                        val touchDist = if (touchX >= 0) abs(touchX - barCenterX) else 9999f
                        val touchBoost = if (touchDist < 120f) (1f - (touchDist / 120f)) * 0.5f else 0f

                        val barHeight = ((sampleAmp + touchBoost) * (canvasHeight * 0.75f) * touchModulationFactor)
                            .coerceIn(8f, canvasHeight - 16f)
                        val topY = midY - (barHeight / 2f)

                        val barBrush = Brush.verticalGradient(
                            colors = listOf(
                                LegalAmberSecondary,
                                TerracottaAccentSecondary,
                                Color(0xFF64B5F6)
                            ),
                            startY = topY,
                            endY = topY + barHeight
                        )

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(i * barSpacing + (barSpacing - barWidth) / 2f, topY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                            alpha = 0.65f
                        )
                    }

                    // 3. Draw Primary Continuous Harmonic Sine Wave
                    val path1 = Path()
                    val path2 = Path()
                    val steps = 80
                    val stepWidth = canvasWidth / steps

                    for (step in 0..steps) {
                        val x = step * stepWidth
                        val progress = step.toFloat() / steps

                        // Envelope window to taper ends
                        val envelope = sin(progress * PI).toFloat()

                        val y1 = midY + sin((progress * 3 * PI) + phaseShift1).toFloat() * (canvasHeight * 0.35f * envelope * touchModulationFactor)
                        val y2 = midY + sin((progress * 4.5 * PI) + phaseShift2).toFloat() * (canvasHeight * 0.25f * envelope * touchModulationFactor)

                        if (step == 0) {
                            path1.moveTo(x, y1)
                            path2.moveTo(x, y2)
                        } else {
                            path1.lineTo(x, y1)
                            path2.lineTo(x, y2)
                        }
                    }

                    // Draw primary gradient wave
                    drawPath(
                        path = path1,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFCC80),
                                LegalAmberSecondary,
                                Color(0xFF80D8FF),
                                LegalAmberSecondary
                            )
                        ),
                        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                    )

                    // Draw secondary subtle harmonic wave
                    drawPath(
                        path = path2,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                TerracottaAccentSecondary,
                                Color(0xFF81D4FA),
                                TerracottaAccentSecondary
                            )
                        ),
                        style = Stroke(width = 2f, cap = StrokeCap.Round),
                        alpha = 0.7f
                    )

                    // 4. Draw Center Microphone Radial Pulse Glow
                    val centerRadius = 24f * pulseGlow
                    drawCircle(
                        color = LegalAmberSecondary.copy(alpha = 0.25f),
                        radius = centerRadius,
                        center = Offset(canvasWidth / 2f, midY)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f),
                        radius = 4f,
                        center = Offset(canvasWidth / 2f, midY)
                    )

                    // 5. If user touches the canvas, draw interactive ripple
                    if (touchX >= 0 && touchY >= 0) {
                        drawCircle(
                            color = Color(0xFF80D8FF).copy(alpha = 0.4f),
                            radius = 36f * touchModulationFactor,
                            center = Offset(touchX, touchY),
                            style = Stroke(width = 2f)
                        )
                    }
                }

                // Interactive Overlay Instruction Hint
                Text(
                    text = if (isTa) "தொட்டு அலைவரிசை உணர்திறனை மாற்றவும்" else "Tap/Drag to modulate sensitivity",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onCancelRecording != null) {
                    OutlinedButton(
                        onClick = onCancelRecording,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepIndigoSlatePrimary),
                        border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("cancel_voice_recording_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isTa) "ரத்து செய்" else "Cancel")
                    }
                }

                Button(
                    onClick = onStopRecording,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepIndigoSlatePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("finalize_voice_recording_button")
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(20.dp), tint = LegalAmberSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTa) "உள்ளீட்டை முடிக்கவும்" else "Done / Process Voice",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

/**
 * Compact Inline Canvas Waveform for message bar or top header intake feedback.
 */
@Composable
fun CanvasVoiceWaveformInline(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "InlineWaveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Phase"
    )

    Canvas(
        modifier = modifier
            .width(100.dp)
            .height(28.dp)
            .testTag("canvas_voice_waveform_inline")
    ) {
        val width = size.width
        val height = size.height
        val midY = height / 2f
        val bars = 12
        val barSpacing = width / bars
        val barWidth = barSpacing * 0.6f

        for (i in 0 until bars) {
            val amp = if (amplitudes.isNotEmpty() && i < amplitudes.size) {
                amplitudes[i % amplitudes.size]
            } else {
                val progress = i.toFloat() / bars
                abs(sin(progress * 3 * PI + phase).toFloat()) * 0.8f + 0.2f
            }

            val barHeight = (amp * height).coerceIn(4f, height)
            val topY = midY - (barHeight / 2f)

            drawRoundRect(
                color = TerracottaAccentSecondary,
                topLeft = Offset(i * barSpacing, topY),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
            )
        }
    }
}
