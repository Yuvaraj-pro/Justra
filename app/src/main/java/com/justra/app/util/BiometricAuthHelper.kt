package com.justra.app.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricAuthHelper {

    fun isDeviceBiometricOrLockAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        val result = biometricManager.canAuthenticate(authenticators)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun checkBiometricStatus(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return biometricManager.canAuthenticate(authenticators)
    }

    fun getStatusDescription(status: Int, language: com.justra.app.domain.model.LanguagePreference): String {
        val isTa = language == com.justra.app.domain.model.LanguagePreference.TAMIL
        return when (status) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                if (isTa) "பயோமெட்ரிக் / சாதன பூட்டு உள்ளது" else "Biometric or Device Credential is focus ready"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                if (isTa) "பயோமெட்ரிக் வன்பொருள் இல்லை" else "No biometric hardware available"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                if (isTa) "பயோமெட்ரிக் வன்பொருள் தற்போது இயக்கத்தில் இல்லை" else "Biometric hardware currently unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                if (isTa) "பயோமெட்ரிக் பதிவு செய்யப்படவில்லை (PIN பயன்படுத்தவும்)" else "No biometrics enrolled on device (use App PIN)"
            else ->
                if (isTa) "பயோமெட்ரிக் நிலை: $status" else "Biometric status code: $status"
        }
    }

    fun canAuthenticateBiometricOnly(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun promptBiometricAuth(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFallbackToPin: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS
                ) {
                    onFallbackToPin()
                } else {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Biometric scan didn't match
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)

        val biometricOnlyAvailable = canAuthenticateBiometricOnly(activity)
        val canUseDeviceCredentials = isDeviceBiometricOrLockAvailable(activity)

        if (canUseDeviceCredentials && !biometricOnlyAvailable) {
            promptInfoBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            promptInfoBuilder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            promptInfoBuilder.setNegativeButtonText("Use App PIN")
        }

        try {
            biometricPrompt.authenticate(promptInfoBuilder.build())
        } catch (e: Exception) {
            onFallbackToPin()
        }
    }
}
