package com.example.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.domain.model.LanguagePreference

/**
 * Biometric Authentication Manager utilizing the official androidx.biometric library.
 * Provides hardware readiness evaluation, prompt initialization, and robust callback delegation
 * for securing the NyayaMate legal document vault and evidence repository.
 */
object BiometricAuthHelper {

    enum class BiometricCapability {
        AVAILABLE,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NOT_ENROLLED,
        SECURITY_UPDATE_REQUIRED,
        UNSUPPORTED
    }

    /**
     * Inspects device biometric sensors and enrollment status.
     */
    fun checkBiometricStatus(context: Context): BiometricCapability {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricCapability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricCapability.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricCapability.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricCapability.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricCapability.SECURITY_UPDATE_REQUIRED
            else -> BiometricCapability.UNSUPPORTED
        }
    }

    /**
     * Checks if biometric hardware is ready for immediate prompt authentication.
     */
    fun isBiometricReady(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val res = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        return res == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Launches the system BiometricPrompt modal.
     *
     * @param activity FragmentActivity hosting the prompt.
     * @param language Active UI language (Tamil or English).
     * @param onSuccess Callback triggered on successful hardware biometric / device credential match.
     * @param onError Callback with error code and localized message.
     * @param onFailed Callback on unrecognised biometric attempt.
     * @param onNegativeButtonTapped Callback if user chooses the negative button ("Use Vault PIN").
     */
    fun launchBiometricPrompt(
        activity: FragmentActivity,
        language: LanguagePreference = LanguagePreference.ENGLISH,
        customTitle: String? = null,
        customSubtitle: String? = null,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit,
        onFailed: () -> Unit = {},
        onNegativeButtonTapped: () -> Unit = {}
    ) {
        val isTa = language == LanguagePreference.TAMIL

        val title = customTitle ?: if (isTa) {
            "ஜஸ்ட்ரா ஆவணப் பெட்டகத்தை திறக்கவும்"
        } else {
            "Unlock Justra Document Vault"
        }

        val subtitle = customSubtitle ?: if (isTa) {
            "பயோமெட்ரிக் கைரேகை அல்லது முக அங்கீகாரம்"
        } else {
            "Biometric Fingerprint / Face Recognition"
        }

        val description = if (isTa) {
            "சட்ட ஆவணங்கள், 65B சான்றிதழ்கள் மற்றும் வழக்குகளைப் பாதுகாக்க அங்கீகரிக்கவும்."
        } else {
            "Verify your identity to access privileged case evidence and Section 65B legal records."
        }

        val negativeButtonText = if (isTa) "பாதுகாப்பு PIN பயன்பாடு" else "Use Vault PIN"

        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess(result)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    onNegativeButtonTapped()
                } else {
                    onError(errorCode, errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Localized description of biometric capability status for UI diagnostics.
     */
    fun getStatusDescription(capability: BiometricCapability, language: LanguagePreference): String {
        val isTa = language == LanguagePreference.TAMIL
        return when (capability) {
            BiometricCapability.AVAILABLE ->
                if (isTa) "பயோமெட்ரிக் சென்சார் செயலில் உள்ளது (Biometric Ready)" else "Hardware Keystore Biometrics Ready"
            BiometricCapability.NOT_ENROLLED ->
                if (isTa) "கைரேகை பதிவு செய்யப்படவில்லை. சாதன அமைப்புகளில் கைரேகையை சேர்க்கவும்." else "No biometrics enrolled on this device. Please enroll in device Settings."
            BiometricCapability.NO_HARDWARE ->
                if (isTa) "சாதனத்தில் பயோமெட்ரிக் சென்சார் இல்லை. PIN முறையைப் பயன்படுத்தவும்." else "No biometric hardware detected. Vault secured via master PIN."
            BiometricCapability.HARDWARE_UNAVAILABLE ->
                if (isTa) "பயோமெட்ரிக் சென்சார் தற்போது கிடைக்கவில்லை." else "Biometric sensor currently unavailable. Try again or use PIN."
            BiometricCapability.SECURITY_UPDATE_REQUIRED ->
                if (isTa) "பாதுகாப்பு புதுப்பிப்பு தேவைப்படுகிறது." else "Security update required for biometric authentication."
            BiometricCapability.UNSUPPORTED ->
                if (isTa) "பயோமெட்ரிக் ஆதரிக்கப்படவில்லை." else "Biometrics not supported on this configuration."
        }
    }
}
