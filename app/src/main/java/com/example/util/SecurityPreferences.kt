package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

/**
 * Secure encrypted storage for PIN authentication and biometric security tokens.
 * Backed by AndroidX EncryptedSharedPreferences with AES-256 GCM master key encryption.
 */
class SecurityPreferences private constructor(context: Context) {

    companion object {
        private const val TAG = "SecurityPreferences"
        private const val PREF_FILE_NAME = "justra_encrypted_security_prefs"
        private const val KEY_ENROLLED_PIN_HASH = "sec_enrolled_pin_sha256"
        private const val KEY_ENROLLED_PIN_LENGTH = "sec_enrolled_pin_length"
        private const val KEY_FAILED_ATTEMPTS = "sec_failed_attempts"
        private const val KEY_LOCKOUT_EXPIRY = "sec_lockout_expiry_timestamp"
        private const val KEY_PIN_SET_TIMESTAMP = "sec_pin_enrolled_at"
        private const val KEY_BIOMETRIC_ENABLED = "sec_biometric_enabled"

        @Volatile
        private var INSTANCE: SecurityPreferences? = null

        fun getInstance(context: Context): SecurityPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecurityPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREF_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize EncryptedSharedPreferences, falling back to private prefs", e)
            context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Checks if user has completed first-time PIN enrollment.
     */
    fun hasEnrolledPin(): Boolean {
        val savedHash = prefs.getString(KEY_ENROLLED_PIN_HASH, null)
        return !savedHash.isNullOrBlank()
    }

    fun getEnrolledPinLength(): Int {
        return prefs.getInt(KEY_ENROLLED_PIN_LENGTH, 6)
    }

    /**
     * Validates and saves a new 4 to 6 digit numeric PIN.
     * @return true if valid and saved, false if invalid format (must be 4 to 6 digits).
     */
    fun savePin(pin: String): Boolean {
        if (!isValidPinFormat(pin)) {
            Log.w(TAG, "Rejecting PIN setup: length must be between 4 and 6 numeric digits")
            return false
        }
        val hashedPin = hashPin(pin)
        prefs.edit()
            .putString(KEY_ENROLLED_PIN_HASH, hashedPin)
            .putInt(KEY_ENROLLED_PIN_LENGTH, pin.length)
            .putLong(KEY_PIN_SET_TIMESTAMP, System.currentTimeMillis())
            .apply()
        Log.d(TAG, "PIN successfully enrolled and encrypted.")
        return true
    }

    /**
     * Verifies user input against enrolled encrypted PIN.
     * @return true if match, false otherwise.
     */
    fun verifyPin(inputPin: String): Boolean {
        val savedHash = prefs.getString(KEY_ENROLLED_PIN_HASH, null) ?: return false
        if (inputPin.isBlank()) return false
        val inputHash = hashPin(inputPin)
        return savedHash == inputHash
    }

    fun getFailedAttempts(): Int {
        return prefs.getInt(KEY_FAILED_ATTEMPTS, 0)
    }

    fun incrementFailedAttempts(): Int {
        val count = getFailedAttempts() + 1
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, count).apply()
        return count
    }

    fun resetFailedAttempts() {
        prefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_EXPIRY, 0L)
            .apply()
    }

    fun getLockoutExpiryTimestamp(): Long {
        return prefs.getLong(KEY_LOCKOUT_EXPIRY, 0L)
    }

    fun setLockoutExpiryTimestamp(expiry: Long) {
        prefs.edit().putLong(KEY_LOCKOUT_EXPIRY, expiry).apply()
    }

    /**
     * Validates whether input PIN meets strict 4 to 6 numeric digit constraint.
     */
    fun isValidPinFormat(pin: String): Boolean {
        return pin.length in 4..6 && pin.all { it.isDigit() }
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun clearPin() {
        prefs.edit()
            .remove(KEY_ENROLLED_PIN_HASH)
            .remove(KEY_ENROLLED_PIN_LENGTH)
            .remove(KEY_PIN_SET_TIMESTAMP)
            .remove(KEY_FAILED_ATTEMPTS)
            .remove(KEY_LOCKOUT_EXPIRY)
            .apply()
    }

    fun getSelectedJurisdictionCode(): String {
        return prefs.getString("sec_selected_jurisdiction_code", "IN") ?: "IN"
    }

    fun setSelectedJurisdictionCode(code: String) {
        prefs.edit().putString("sec_selected_jurisdiction_code", code).apply()
    }

    private fun hashPin(pin: String): String {
        val input = "JUSTRA_ENCRYPTED_PIN_SALT_$pin"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
