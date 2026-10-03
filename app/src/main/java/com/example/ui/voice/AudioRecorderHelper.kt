package com.example.ui.voice

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/**
 * Hardware-level microphone recorder and live RMS amplitude processor.
 * Replaces any simulated random audio generators with true AudioRecord PCM streaming.
 */
class AudioRecorderHelper(private val context: Context) {

    companion object {
        private const val TAG = "AudioRecorderHelper"
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_WINDOW_SIZE = 35
    }

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _amplitudeFlow = MutableStateFlow<List<Float>>(List(BUFFER_WINDOW_SIZE) { 0.08f })
    val amplitudeFlow: StateFlow<List<Float>> = _amplitudeFlow.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {
        if (!hasPermission()) {
            _errorMessage.value = "RECORD_AUDIO permission is required"
            return
        }

        if (_isRecording.value) return

        try {
            val minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
            )

            if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                _errorMessage.value = "Hardware audio buffer initialization failed"
                return
            }

            val bufferSize = (minBufferSize * 2).coerceAtLeast(1024)
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                _errorMessage.value = "AudioRecord failed to initialize"
                release()
                return
            }

            audioRecord?.startRecording()
            _isRecording.value = true
            _errorMessage.value = null

            recordingJob = scope.launch {
                val shortBuffer = ShortArray(bufferSize / 2)
                val bufferList = ArrayDeque<Float>(List(BUFFER_WINDOW_SIZE) { 0.08f })

                while (isActive && _isRecording.value) {
                    val readCount = audioRecord?.read(shortBuffer, 0, shortBuffer.size) ?: -1
                    if (readCount > 0) {
                        // Compute True RMS Amplitude: sqrt( 1/N * sum(x_i^2) )
                        var sumOfSquares = 0.0
                        for (i in 0 until readCount) {
                            val sample = shortBuffer[i]
                            sumOfSquares += sample * sample
                        }
                        val rms = sqrt(sumOfSquares / readCount)

                        // Normalize against peak voice dynamic range (~8000-14000 PCM short)
                        // Floor at 0.06f for pleasant idle resting wave, cap at 1.0f
                        val normalized = ((rms / 9000.0).toFloat() * 0.94f + 0.06f).coerceIn(0.06f, 1.0f)

                        if (bufferList.size >= BUFFER_WINDOW_SIZE) {
                            bufferList.removeFirst()
                        }
                        bufferList.addLast(normalized)

                        _amplitudeFlow.value = bufferList.toList()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in startRecording: ${e.message}", e)
            _errorMessage.value = "Microphone error: ${e.localizedMessage}"
            stopRecording()
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
        recordingJob = null
        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping AudioRecord: ${e.message}")
        }
    }

    fun release() {
        stopRecording()
        try {
            audioRecord?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing AudioRecord: ${e.message}")
        } finally {
            audioRecord = null
        }
    }
}
