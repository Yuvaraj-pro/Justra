package com.justra.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.justra.app.domain.model.LanguagePreference
import java.io.InputStream
// Charsets is available via kotlin.text.Charsets (no import needed in Kotlin stdlib)
import java.security.MessageDigest
import java.util.UUID

class SecurityManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("justra_secure_vault_prefs", Context.MODE_PRIVATE)

    val appContext: Context = context.applicationContext

    companion object {
        private const val KEY_LANG = "pref_user_language"
        private const val KEY_STATUTORY_CONSENT = "pref_statutory_consent_timestamp"
        private const val KEY_BIOMETRIC_ENABLED = "pref_biometric_lock_enabled"
        private const val KEY_IS_LOGGED_IN = "pref_is_authenticated"
        private const val KEY_USER_DISPLAY_NAME = "pref_user_display_name"
        private const val KEY_USER_PHONE = "pref_user_phone_number"
        private const val KEY_MASTER_VAULT_KEY_DERIVED = "pref_vault_key_hash"
        private const val KEY_USER_ROLE = "pref_active_user_role"
        private const val KEY_VAULT_PIN = "pref_vault_pin_hash"
        private const val KEY_READ_NOTIFICATIONS = "pref_read_notification_ids"
        private const val KEY_AUTO_LOCK_TIMEOUT = "pref_auto_lock_timeout_min"
        private const val KEY_DUAL_SUBTITLE = "pref_dual_subtitle_enabled"
        private const val KEY_AI_TELEMETRY = "pref_ai_telemetry_enabled"
        private const val KEY_AUTO_PURGE_DOCS = "pref_auto_purge_docs_enabled"
        private const val KEY_OFFLINE_MODE_FORCED = "pref_offline_mode_forced"
        private const val KEY_SELECTED_DISTRICT = "pref_selected_district"
        private const val KEY_ADVOCATE_ENROLL_ID = "pref_advocate_enroll_id"
        private const val KEY_STEALTH_MODE_ENABLED = "pref_stealth_mode_enabled"
        private const val KEY_SELECTED_JURISDICTION = "pref_selected_jurisdiction_code"
        private const val KEY_LAST_BACKGROUND_TIMESTAMP = "pref_last_background_timestamp"
        private const val KEY_LAST_ACTIVE_TIMESTAMP = "pref_last_active_timestamp"
        private const val KEY_LEGAL_DISCLAIMER_ACCEPTED = "pref_legal_disclaimer_accepted"
        private const val KEY_THEME_MODE = "pref_theme_mode"
    }

    fun getThemeMode(): Int {
        return prefs.getInt(KEY_THEME_MODE, 1)
    }

    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
    }

    fun recordAppBackgrounded(timestamp: Long = System.currentTimeMillis()) {
        prefs.edit().putLong(KEY_LAST_BACKGROUND_TIMESTAMP, timestamp).apply()
    }

    fun getLastBackgroundTimestamp(): Long {
        return prefs.getLong(KEY_LAST_BACKGROUND_TIMESTAMP, 0L)
    }

    fun getLastActiveTimestamp(): Long {
        return prefs.getLong(KEY_LAST_ACTIVE_TIMESTAMP, System.currentTimeMillis())
    }

    fun shouldTriggerReauth(currentTime: Long = System.currentTimeMillis()): Boolean {
        if (!isBiometricLockEnabled() || !hasAcceptedStatutoryConsent()) {
            return false
        }
        val bgTime = getLastBackgroundTimestamp()
        if (bgTime <= 0L) return false
        val elapsed = currentTime - bgTime
        val timeoutMs = getAutoLockTimeoutMinutes() * 60 * 1000L
        return elapsed >= timeoutMs
    }

    fun onReauthSuccess() {
        prefs.edit()
            .putLong(KEY_LAST_BACKGROUND_TIMESTAMP, 0L)
            .putLong(KEY_LAST_ACTIVE_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }


    fun getAutoLockTimeoutMinutes(): Int {
        return prefs.getInt(KEY_AUTO_LOCK_TIMEOUT, 5)
    }

    fun setAutoLockTimeoutMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_AUTO_LOCK_TIMEOUT, minutes).apply()
    }

    fun isDualSubtitleEnabled(): Boolean {
        return prefs.getBoolean(KEY_DUAL_SUBTITLE, true)
    }

    fun setDualSubtitleEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DUAL_SUBTITLE, enabled).apply()
    }

    fun isAiTelemetryEnabled(): Boolean {
        return prefs.getBoolean(KEY_AI_TELEMETRY, false)
    }

    fun setAiTelemetryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AI_TELEMETRY, enabled).apply()
    }

    fun isAutoPurgeDocsEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_PURGE_DOCS, false)
    }

    fun setAutoPurgeDocsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_PURGE_DOCS, enabled).apply()
    }

    fun isOfflineModeForced(): Boolean {
        return prefs.getBoolean(KEY_OFFLINE_MODE_FORCED, false)
    }

    fun setOfflineModeForced(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_OFFLINE_MODE_FORCED, enabled).apply()
    }

    fun getSelectedDistrict(): String {
        return prefs.getString(KEY_SELECTED_DISTRICT, "") ?: ""
    }

    fun setSelectedDistrict(district: String) {
        prefs.edit().putString(KEY_SELECTED_DISTRICT, district).apply()
    }

    fun getAdvocateEnrollmentId(): String {
        return prefs.getString(KEY_ADVOCATE_ENROLL_ID, "") ?: ""
    }

    fun setAdvocateEnrollmentId(id: String) {
        prefs.edit().putString(KEY_ADVOCATE_ENROLL_ID, id).apply()
    }

    fun isStealthModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_STEALTH_MODE_ENABLED, false)
    }

    fun setStealthModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_STEALTH_MODE_ENABLED, enabled).apply()
    }

    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }

    fun getUserRole(): com.justra.app.domain.model.UserRole {
        val roleId = prefs.getString(KEY_USER_ROLE, com.justra.app.domain.model.UserRole.CITIZEN.id)
        return com.justra.app.domain.model.UserRole.values().firstOrNull { it.id == roleId }
            ?: com.justra.app.domain.model.UserRole.CITIZEN
    }

    fun setUserRole(role: com.justra.app.domain.model.UserRole) {
        prefs.edit().putString(KEY_USER_ROLE, role.id).apply()
    }

    fun hasVaultPin(): Boolean {
        return prefs.getString(KEY_VAULT_PIN, null) != null
    }

    fun setVaultPin(pin: String) {
        val pinHash = calculateSha256("JUSTRA_PIN_$pin")
        prefs.edit().putString(KEY_VAULT_PIN, pinHash).apply()
    }

    fun verifyVaultPin(inputPin: String): Boolean {
        val savedHash = prefs.getString(KEY_VAULT_PIN, null)
        if (savedHash == null) {
            // Default first-time PIN accept any >= 4 digits or "1234"
            return inputPin.length >= 4
        }
        val inputHash = calculateSha256("JUSTRA_PIN_$inputPin")
        return inputHash == savedHash
    }

    fun getReadNotificationIds(): Set<String> {
        return prefs.getStringSet(KEY_READ_NOTIFICATIONS, emptySet()) ?: emptySet()
    }

    fun markNotificationRead(notificationId: String) {
        val current = getReadNotificationIds().toMutableSet()
        current.add(notificationId)
        prefs.edit().putStringSet(KEY_READ_NOTIFICATIONS, current).apply()
    }

    fun markAllNotificationsRead(ids: List<String>) {
        val current = getReadNotificationIds().toMutableSet()
        current.addAll(ids)
        prefs.edit().putStringSet(KEY_READ_NOTIFICATIONS, current).apply()
    }


    fun getLanguagePreference(): LanguagePreference {
        val code = prefs.getString(KEY_LANG, LanguagePreference.ENGLISH.code)
        return if (code == LanguagePreference.TAMIL.code) LanguagePreference.TAMIL else LanguagePreference.ENGLISH
    }

    fun setLanguagePreference(language: LanguagePreference) {
        prefs.edit().putString(KEY_LANG, language.code).apply()
    }

    fun hasAcceptedStatutoryConsent(): Boolean {
        return prefs.getLong(KEY_STATUTORY_CONSENT, 0L) > 0L
    }

    fun recordStatutoryConsent() {
        prefs.edit().putLong(KEY_STATUTORY_CONSENT, System.currentTimeMillis()).apply()
    }

    fun isLegalDisclaimerAccepted(): Boolean {
        return prefs.getBoolean(KEY_LEGAL_DISCLAIMER_ACCEPTED, false)
    }

    fun setLegalDisclaimerAccepted(accepted: Boolean) {
        prefs.edit().putBoolean(KEY_LEGAL_DISCLAIMER_ACCEPTED, accepted).apply()
    }

    fun getStatutoryConsentTimestamp(): Long {
        return prefs.getLong(KEY_STATUTORY_CONSENT, 0L)
    }

    fun isBiometricLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
    }

    fun setBiometricLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isAuthenticated(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setAuthenticated(auth: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, auth).apply()
    }

    fun getUserDisplayName(): String {
        return prefs.getString(KEY_USER_DISPLAY_NAME, "") ?: ""
    }

    fun setUserDisplayName(name: String) {
        prefs.edit().putString(KEY_USER_DISPLAY_NAME, name).apply()
    }

    fun getUserPhoneNumber(): String {
        return prefs.getString(KEY_USER_PHONE, "") ?: ""
    }

    fun setUserPhoneNumber(phone: String) {
        prefs.edit().putString(KEY_USER_PHONE, phone).apply()
    }

    fun getSelectedJurisdictionCode(): String {
        return prefs.getString(KEY_SELECTED_JURISDICTION, "IN") ?: "IN"
    }

    fun setSelectedJurisdictionCode(code: String) {
        prefs.edit().putString(KEY_SELECTED_JURISDICTION, code).apply()
    }

    fun getVaultKeyHash(): String {
        var hash = prefs.getString(KEY_MASTER_VAULT_KEY_DERIVED, null)
        if (hash == null) {
            hash = calculateSha256("JUSTRA-KEYSTORE-MASTER-" + UUID.randomUUID().toString())
            prefs.edit().putString(KEY_MASTER_VAULT_KEY_DERIVED, hash).apply()
        }
        return hash
    }

    fun calculateSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun calculateStreamSha256(inputStream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun saveUserProfile(displayName: String, phoneNumber: String, district: String, advocateEnrollmentId: String) {
        prefs.edit()
            .putString(KEY_USER_DISPLAY_NAME, displayName)
            .putString(KEY_USER_PHONE, phoneNumber)
            .putString(KEY_SELECTED_DISTRICT, district)
            .putString(KEY_ADVOCATE_ENROLL_ID, advocateEnrollmentId)
            .apply()
    }

    fun saveSecurityPin(pin: String) {
        setVaultPin(pin)
    }

    fun clearAllUserData() {
        clearAllPreferences()
    }

    fun exportAllUserDataJson(): String {
        val name = getUserDisplayName()
        val phone = getUserPhoneNumber()
        val role = getUserRole().name
        val district = getSelectedDistrict()
        val enrollId = getAdvocateEnrollmentId()
        val lang = getLanguagePreference().code
        return """
            {
              "exportTimestamp": ${System.currentTimeMillis()},
              "appName": "Justra Sovereign Legal Engine",
              "userProfile": {
                "displayName": "$name",
                "phone": "$phone",
                "activeRole": "$role",
                "district": "$district",
                "advocateEnrollmentId": "$enrollId",
                "language": "$lang"
              },
              "vaultStatus": {
                "hasPin": ${hasVaultPin()},
                "biometricEnabled": ${isBiometricLockEnabled()}
              }
            }
        """.trimIndent()
    }
}
