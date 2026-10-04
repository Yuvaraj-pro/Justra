package com.justra.app.ui.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    data class PartialResult(val text: String) : VoiceState()
    data class FinalResult(val text: String) : VoiceState()
    data class Error(val message: String, val errorCode: Int? = null) : VoiceState()
}

/**
 * Android SpeechRecognizer wrapper providing reactive streaming transcription
 * for bilingual Tamil (ta-IN) and Indian English (en-IN) legal voice intakes.
 */
class VoiceInputManager(private val context: Context) {

    companion object {
        private const val TAG = "VoiceInputManager"
    }

    private var speechRecognizer: SpeechRecognizer? = null

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    private val _liveRmsDb = MutableStateFlow(0f)
    val liveRmsDb: StateFlow<Float> = _liveRmsDb.asStateFlow()

    init {
        initializeRecognizer()
    }

    private fun initializeRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createListener())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize SpeechRecognizer: ${e.message}")
            }
        } else {
            _voiceState.value = VoiceState.Error("Device speech recognition service unavailable")
        }
    }

    private fun createListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _voiceState.value = VoiceState.Listening
        }

        override fun onBeginningOfSpeech() {
            _voiceState.value = VoiceState.Listening
        }

        override fun onRmsChanged(rmsdB: Float) {
            _liveRmsDb.value = rmsdB
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            // Processing
        }

        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service is busy. Retrying..."
                SpeechRecognizer.ERROR_SERVER -> "Recognition server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                else -> "Speech error ($error)"
            }
            Log.w(TAG, "SpeechRecognizer error: $message ($error)")
            _voiceState.value = VoiceState.Error(message, error)
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matches?.firstOrNull() ?: ""
            if (recognizedText.isNotBlank()) {
                val current = _liveTranscript.value
                val fullText = if (current.isBlank()) recognizedText else "$current $recognizedText"
                _liveTranscript.value = fullText.trim()
                _voiceState.value = VoiceState.FinalResult(fullText.trim())
            } else {
                _voiceState.value = VoiceState.Idle
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val partialText = partials?.firstOrNull() ?: ""
            if (partialText.isNotBlank()) {
                val currentPrefix = _liveTranscript.value
                val display = if (currentPrefix.isBlank()) partialText else "$currentPrefix $partialText"
                _voiceState.value = VoiceState.PartialResult(display)
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    /**
     * Start live speech recognition.
     * @param localeCode e.g. "ta-IN" for Tamil or "en-IN" for Indian English
     */
    fun startListening(localeCode: String = "ta-IN") {
        try {
            if (speechRecognizer == null) {
                initializeRecognizer()
            }
            if (!SpeechRecognizer.isRecognitionAvailable(context) || speechRecognizer == null) {
                _voiceState.value = VoiceState.Error("Speech recognition unavailable on this device. You can type your legal grievance directly.")
                return
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeCode)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, localeCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }

            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.Listening
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking startListening: ${e.message}")
            _voiceState.value = VoiceState.Error("Could not start microphone: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping speechRecognizer: ${e.message}")
        }
    }

    fun updateManualTranscript(text: String) {
        _liveTranscript.value = text
        _voiceState.value = VoiceState.FinalResult(text)
    }

    fun clearTranscript() {
        _liveTranscript.value = ""
        _voiceState.value = VoiceState.Idle
    }

    fun destroy() {
        try {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.w(TAG, "Error destroying speechRecognizer: ${e.message}")
        } finally {
            speechRecognizer = null
            _voiceState.value = VoiceState.Idle
        }
    }
}
