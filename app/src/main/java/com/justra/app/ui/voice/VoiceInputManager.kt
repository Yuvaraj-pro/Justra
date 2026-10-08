package com.justra.app.ui.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.justra.app.domain.model.LanguagePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    data class PartialResult(val text: String) : VoiceState()
    data class FinalResult(val text: String) : VoiceState()
    data class Error(
        val message: String,
        val errorCode: Int? = null,
        val isPermissionDenied: Boolean = false
    ) : VoiceState()
}

/**
 * Android SpeechRecognizer wrapper providing reactive streaming transcription
 * for bilingual Tamil (ta-IN) and Indian English (en-IN) legal voice intakes.
 *
 * Language-specific STT behavior:
 *  - English mode → en-IN primary, en-US fallback. Transcribes into Latin/English script.
 *    NEVER phonetically transliterates English words into Tamil script.
 *  - Tamil mode  → ta-IN. Transcribes Tamil speech directly into Tamil script.
 *
 * EXTRA_PARTIAL_RESULTS = true streams text in real-time into the transcript box.
 */
class VoiceInputManager(private val context: Context) {

    companion object {
        private const val TAG = "VoiceInputManager"

        /** Returns the primary BCP-47 locale code for the given [LanguagePreference]. */
        fun localeCodeFor(language: LanguagePreference): String = when (language) {
            LanguagePreference.TAMIL -> "ta-IN"
            LanguagePreference.ENGLISH -> "en-IN"
        }

        /**
         * Returns the fallback locale used when the primary locale returns no result.
         * For English: en-US fallback guarantees Latin script output.
         */
        fun fallbackLocaleFor(language: LanguagePreference): String = when (language) {
            LanguagePreference.TAMIL -> "ta-IN"   // no meaningful fallback for Tamil
            LanguagePreference.ENGLISH -> "en-US"
        }
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var activeLanguage: LanguagePreference = LanguagePreference.ENGLISH

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
            _voiceState.value = VoiceState.Error(
                message = "Device speech recognition service unavailable",
                isPermissionDenied = false
            )
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
            // Transcribing state handled by onResults / onError callbacks
        }

        override fun onError(error: Int) {
            val isPermissionDenied = error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check device microphone."
                SpeechRecognizer.ERROR_CLIENT -> "Client error. Please try again."
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                    "Microphone permission is required. Please grant RECORD_AUDIO permission in Settings."
                SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout during recognition."
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service busy. Please try again."
                SpeechRecognizer.ERROR_SERVER -> "Recognition server error."
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected within timeout."
                else -> "Speech recognition error (code $error)."
            }
            Log.w(TAG, "SpeechRecognizer error: $message ($error)")
            _voiceState.value = VoiceState.Error(
                message = message,
                errorCode = error,
                isPermissionDenied = isPermissionDenied
            )
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
     * Start live speech recognition for the given [language].
     *
     * English mode → EXTRA_LANGUAGE = "en-IN", EXTRA_LANGUAGE_PREFERENCE = "en-US"
     *   This forces the recognizer to output Latin-script English, NEVER Tamil script.
     *
     * Tamil mode   → EXTRA_LANGUAGE = "ta-IN". Outputs Tamil Unicode script.
     *
     * EXTRA_PARTIAL_RESULTS = true streams live text into the UI textbox.
     */
    fun startListening(language: LanguagePreference) {
        activeLanguage = language
        startListeningWithLocale(localeCodeFor(language), fallbackLocaleFor(language))
    }

    /**
     * Legacy overload accepting a raw locale code string (e.g., "ta-IN").
     * Prefer [startListening(LanguagePreference)] for correct fallback behavior.
     */
    fun startListening(localeCode: String = "en-IN") {
        val fallback = if (localeCode.startsWith("ta")) "ta-IN" else "en-US"
        startListeningWithLocale(localeCode, fallback)
    }

    private fun startListeningWithLocale(primaryLocale: String, fallbackLocale: String) {
        try {
            if (speechRecognizer == null) initializeRecognizer()

            if (!SpeechRecognizer.isRecognitionAvailable(context) || speechRecognizer == null) {
                _voiceState.value = VoiceState.Error(
                    message = "Speech recognition is unavailable on this device. " +
                              "You can type your legal grievance directly.",
                    isPermissionDenied = false
                )
                return
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                         RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

                // PRIMARY locale → forces script (Latin for en-IN, Tamil for ta-IN)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, primaryLocale)

                // PREFERENCE → fallback used if primary returns empty
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, fallbackLocale)

                // Restrict so recognizer does not switch to a different script
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, primaryLocale)

                // Stream partial results for real-time UI feedback
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }

            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.Listening
        } catch (e: SecurityException) {
            // RECORD_AUDIO permission explicitly denied at runtime
            Log.e(TAG, "RECORD_AUDIO permission denied: ${e.message}")
            _voiceState.value = VoiceState.Error(
                message = "Microphone permission denied. Please grant RECORD_AUDIO permission in app Settings.",
                isPermissionDenied = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking startListening: ${e.message}")
            _voiceState.value = VoiceState.Error(
                message = "Could not start microphone: ${e.localizedMessage}",
                isPermissionDenied = false
            )
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
