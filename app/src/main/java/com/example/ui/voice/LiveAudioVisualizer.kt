package com.example.ui.voice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.DeepHennaAlertContainer
import com.example.ui.theme.DeepImperialNavy
import com.example.ui.theme.OnDeepHennaAlert
import com.example.ui.theme.OnImperialNavy
import com.example.ui.theme.ParchmentOutline
import com.example.ui.theme.PrimaryNavyLight
import com.example.ui.theme.SandstoneSurface
import com.example.ui.theme.WarmParchmentBase
import com.example.ui.theme.WarmTerracotta
import kotlin.math.abs

/**
 * Live audio visualizer canvas displaying real-time hardware RMS amplitude bars.
 * Center vocal frequencies are illuminated in Warm Terracotta (#B85028) while sidebands
 * are rendered in Deep Imperial Navy.
 */
@Composable
fun LiveWaveformCanvas(
    amplitudes: List<Float>,
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 90.dp,
    barWidth: Dp = 4.dp,
    barSpacing: Dp = 3.dp,
    activeColor: Color = WarmTerracotta,
    inactiveColor: Color = PrimaryNavyLight
) {
    val displayAmplitudes = if (amplitudes.isEmpty()) List(35) { 0.08f } else amplitudes

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(WarmParchmentBase)
            .border(1.dp, ParchmentOutline, RoundedCornerShape(14.dp))
            .semantics { contentDescription = "Live voice amplitude frequency visualization" }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("live_waveform_canvas")) {
            val totalWidth = size.width
            val canvasHeight = size.height
            val centerY = canvasHeight / 2f

            val widthPerBar = barWidth.toPx()
            val spacingPx = barSpacing.toPx()
            val singleBarPitch = widthPerBar + spacingPx
            val maxBars = (totalWidth / singleBarPitch).toInt().coerceAtLeast(10)

            val sampled = if (displayAmplitudes.size > maxBars) {
                displayAmplitudes.takeLast(maxBars)
            } else {
                displayAmplitudes
            }

            val totalVisualWidth = sampled.size * singleBarPitch - spacingPx
            var startX = (totalWidth - totalVisualWidth) / 2f
            if (startX < 0f) startX = 0f

            val centerIndex = sampled.size / 2

            sampled.forEachIndexed { index, amp ->
                val barHeight = ((amp.coerceIn(0.06f, 1.0f)) * (canvasHeight * 0.85f)).coerceAtLeast(4.dp.toPx())
                val topY = centerY - (barHeight / 2f)

                // Center 35% of bars highlight the core vocal frequencies in Warm Terracotta
                val distanceFromCenter = abs(index - centerIndex)
                val isCenterVocalZone = distanceFromCenter <= (sampled.size * 0.18f)

                val barColor = when {
                    !isRecording -> inactiveColor.copy(alpha = 0.35f)
                    isCenterVocalZone -> activeColor
                    else -> DeepImperialNavy.copy(alpha = 0.85f)
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x = startX + index * singleBarPitch, y = topY),
                    size = Size(width = widthPerBar, height = barHeight),
                    cornerRadius = CornerRadius(widthPerBar / 2f, widthPerBar / 2f)
                )
            }
        }
    }
}

/**
 * Integrated Audio Visualizer component with built-in permission request,
 * lifecycle management, and status indicators.
 */
@Composable
fun HardwareAudioVisualizerEngine(
    onAmplitudeChange: (List<Float>) -> Unit = {},
    isAutoRecord: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    val recorderHelper = remember { AudioRecorderHelper(context) }
    val amplitudes by recorderHelper.amplitudeFlow.collectAsState()
    val isRecording by recorderHelper.isRecording.collectAsState()
    val errorMessage by recorderHelper.errorMessage.collectAsState()

    DisposableEffect(Unit) {
        if (isAutoRecord && hasPermission) {
            recorderHelper.startRecording()
        }
        onDispose {
            recorderHelper.release()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SandstoneSurface)
            .border(1.dp, ParchmentOutline, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!hasPermission) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DeepHennaAlertContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = OnDeepHennaAlert,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Microphone Permission Required",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnDeepHennaAlert
                            )
                        )
                        Text(
                            text = "Access is needed to record your voice intake securely on-device.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = OnDeepHennaAlert
                            )
                        )
                    }
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmTerracotta),
                        modifier = Modifier.testTag("request_mic_permission_button")
                    ) {
                        Text("Grant", fontSize = 12.sp)
                    }
                }
            }
        } else {
            // Header with status indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) WarmTerracotta else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRecording) "Hardware Live Stream Active" else "Microphone Standby",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepImperialNavy
                        )
                    )
                }

                Text(
                    text = "PCM 16-Bit Mono @ 16kHz",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas visualization
            LiveWaveformCanvas(
                amplitudes = amplitudes,
                isRecording = isRecording
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(color = OnDeepHennaAlert)
                )
            }
        }
    }
}
