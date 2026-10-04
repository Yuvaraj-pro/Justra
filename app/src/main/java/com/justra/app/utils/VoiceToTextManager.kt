package com.justra.app.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Dedicated Android SpeechRecognizer manager implementing RecognitionListener.
 * Runs strictly on Looper.getMainLooper() with real-time streaming speech-to-text
 * for Tamil (ta-IN) and Indian English (en-IN).
 */
class VoiceToTextManager(private val context: Context) : RecognitionListener {

    companion object {
        private const val TAG = "VoiceToTextManager"
        const val LOCALE_TAMIL = "ta-IN"
        const val LOCALE_ENGLISH = "en-IN"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

    private var accumulatedText = ""

    init {
        ensureRecognizerReady()
    }

    private fun ensureRecognizerReady() {
        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _errorState.value = "Speech recognition is not available on this device"
                return@post
            }

            try {
                if (speechRecognizer == null) {
                    val appContext = context.applicationContext ?: context
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
                        setRecognitionListener(this@VoiceToTextManager)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing SpeechRecognizer", e)
                _errorState.value = "Initialization failed: ${e.message}"
            }
        }
    }

    /**
     * Starts listening with partial results enabled for real-time transcription.
     * Runs strictly on Main Looper.
     * @param localeCode Either "ta-IN" or "en-IN"
     */
    fun startListening(localeCode: String = LOCALE_TAMIL) {
        _errorState.value = null
        val trimmed = _spokenText.value.trim()
        accumulatedText = if (trimmed.isNotEmpty()) "$trimmed " else ""

        mainHandler.post {
            ensureRecognizerReady()

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeCode)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, localeCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }

            try {
                speechRecognizer?.startListening(intent)
                _isListening.value = true
                Log.d(TAG, "Started speech recognition in $localeCode with live partial streaming")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start listening", e)
                _isListening.value = false
                _errorState.value = "Could not start microphone: ${e.message}"
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recognizer", e)
            } finally {
                _isListening.value = false
            }
        }
    }

    fun cancel() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
            } catch (e: Exception) {
                Log.e(TAG, "Error cancelling recognizer", e)
            } finally {
                _isListening.value = false
            }
        }
    }

    fun reset() {
        accumulatedText = ""
        _spokenText.value = ""
        _errorState.value = null
        _rmsDb.value = 0f
    }

    fun setSpokenText(newText: String) {
        accumulatedText = ""
        _spokenText.value = newText
    }

    /**
     * Strict lifecycle teardown. Destroys speech recognizer and releases resources on Main Looper.
     */
    fun destroy() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying speech recognizer", e)
            } finally {
                _isListening.value = false
            }
        }
    }

    // RecognitionListener Implementation

    override fun onReadyForSpeech(params: Bundle?) {
        _isListening.value = true
        _errorState.value = null
    }

    override fun onBeginningOfSpeech() {
        _isListening.value = true
    }

    override fun onRmsChanged(rmsdB: Float) {
        _rmsDb.value = rmsdB
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _isListening.value = false
    }

    override fun onError(error: Int) {
        // Guarantee UI never stays locked in listening mode
        _isListening.value = false

        val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check microphone."
            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio record permission required."
            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network connection timed out."
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Speak clearly into microphone."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice recognizer is busy. Resetting..."
            SpeechRecognizer.ERROR_SERVER -> "Server error. Try again."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard. Tap mic to retry."
            else -> "Speech recognition error code: $error"
        }
        Log.w(TAG, "SpeechRecognizer error: $message (code: $error)")

        if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
            cancel()
        }

        if (_spokenText.value.isBlank()) {
            _errorState.value = message
        }
    }

    override fun onResults(results: Bundle?) {
        _isListening.value = false
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val bestResult = matches?.firstOrNull()?.trim()
        if (!bestResult.isNullOrEmpty()) {
            _spokenText.value = accumulatedText + bestResult
            accumulatedText = _spokenText.value + " "
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val partialText = matches?.firstOrNull()?.trim()
        if (!partialText.isNullOrEmpty()) {
            _spokenText.value = accumulatedText + partialText
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}

/**
 * Remember and automatically manage lifecycle teardown for VoiceToTextManager.
 */
@Composable
fun rememberVoiceToTextManager(context: Context = LocalContext.current): VoiceToTextManager {
    val manager = remember(context) { VoiceToTextManager(context) }
    DisposableEffect(manager) {
        onDispose {
            manager.destroy()
        }
    }
    return manager
}
