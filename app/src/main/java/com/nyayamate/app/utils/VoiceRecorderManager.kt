package com.nyayamate.app.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceRecorderManager(private val context: Context) : RecognitionListener {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null

    private val _spokenText = MutableStateFlow("")
    val spokenText = _spokenText.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun startListening(languageCode: String = "ta-IN") {
        mainHandler.post {
            try {
                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    _errorMessage.value = "Speech recognition service is not available on this device."
                    Log.e("JustraVoice", "Speech recognition unavailable")
                    return@post
                }

                // Clean up any stale session before starting
                stopListeningInternal()

                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(this@VoiceRecorderManager)
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    // Helps with continuous streaming
                    putExtra("android.speech.extra.DICTATION_MODE", true)
                }

                speechRecognizer?.startListening(intent)
                _isListening.value = true
                _errorMessage.value = null
                Log.d("JustraVoice", "Listening started with locale: $languageCode")
            } catch (e: Exception) {
                _isListening.value = false
                _errorMessage.value = "Failed to start listening: ${e.localizedMessage}"
                Log.e("JustraVoice", "Exception starting recognition", e)
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            stopListeningInternal()
        }
    }

    private fun stopListeningInternal() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e("JustraVoice", "Error cleaning up recognizer", e)
        } finally {
            _isListening.value = false
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _spokenText.value = matches[0]
            Log.d("JustraVoice", "Partial result: ${matches[0]}")
        }
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _spokenText.value = matches[0]
            Log.d("JustraVoice", "Final result: ${matches[0]}")
        }
        _isListening.value = false
    }

    override fun onError(error: Int) {
        val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check microphone."
            SpeechRecognizer.ERROR_CLIENT -> "Client error. Please try again."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
            SpeechRecognizer.ERROR_NETWORK -> "Network connection required for voice recognition."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timed out. Try again."
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy. Resetting..."
            SpeechRecognizer.ERROR_SERVER -> "Server error from speech service."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected within time window."
            else -> "Voice recognition error (Code: $error)"
        }
        Log.e("JustraVoice", "SpeechRecognizer Error: $message ($error)")
        
        // Reset state so UI is never stuck in recording state
        _isListening.value = false
        if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            _errorMessage.value = message
        }
    }

    override fun onReadyForSpeech(params: Bundle?) { Log.d("JustraVoice", "Ready for speech") }
    override fun onBeginningOfSpeech() { Log.d("JustraVoice", "User began speaking") }
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() { 
        Log.d("JustraVoice", "User stopped speaking")
        _isListening.value = false 
    }
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
