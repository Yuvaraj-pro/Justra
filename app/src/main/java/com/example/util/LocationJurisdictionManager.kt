package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * World Jurisdiction Data model representing statutory legal frameworks,
 * country-specific emergency helplines, and flag tokens.
 */
data class JurisdictionData(
    val countryCode: String,
    val countryName: String,
    val countryNameTa: String,
    val flagEmoji: String,
    val legalSystemName: String,
    val emergencyHelpline: String,
    val helplineName: String,
    val primaryStatutes: List<String>,
    val defaultLanguageCode: String = "en-US"
)

enum class JurisdictionSelectionMode {
    AUTO_GPS,
    MANUAL_OVERRIDE
}

/**
 * LocationJurisdictionManager: Dual-Mode Jurisdiction Engine for Justra.
 * 
 * 1. AUTO JURISDICTION MODE: Queries Android FusedLocationProviderClient + Geocoder
 *    to resolve host country ISO-3166 code gracefully.
 * 2. MANUAL JURISDICTION OVERRIDE: Allows citizens, expats, and cross-border travelers
 *    to select ANY country worldwide from an indexed repository.
 */
class LocationJurisdictionManager(private val context: Context) {

    companion object {
        private const val TAG = "LocationJurisdiction"

        val WORLD_JURISDICTIONS = listOf(
            JurisdictionData(
                countryCode = "IN",
                countryName = "India",
                countryNameTa = "இந்தியா",
                flagEmoji = "🇮🇳",
                legalSystemName = "BNS / BNSS / Statutory Acts 2023",
                emergencyHelpline = "1930",
                helplineName = "1930 Cyber / 112 Police",
                primaryStatutes = listOf("Bharatiya Nyaya Sanhita (BNS)", "Model Tenancy Act", "IT Act 2000", "Consumer Protection Act 2019"),
                defaultLanguageCode = "ta-IN"
            ),
            JurisdictionData(
                countryCode = "GB",
                countryName = "United Kingdom",
                countryNameTa = "ஐக்கிய இராச்சியம் (UK)",
                flagEmoji = "🇬🇧",
                legalSystemName = "UK Parliament & Common Law",
                emergencyHelpline = "999",
                helplineName = "999 Emergency / Action Fraud",
                primaryStatutes = listOf("Consumer Rights Act 2015", "Housing Act", "Employment Rights Act 1996", "Online Safety Act"),
                defaultLanguageCode = "en-IN"
            ),
            JurisdictionData(
                countryCode = "US",
                countryName = "United States",
                countryNameTa = "அமெரிக்கா (USA)",
                flagEmoji = "🇺🇸",
                legalSystemName = "US Federal & State Common Law",
                emergencyHelpline = "911",
                helplineName = "911 Emergency / IC3 Cyber",
                primaryStatutes = listOf("Uniform Residential Landlord Act", "Fair Labor Standards Act", "FTC Act", "Title 18 Cyber Crimes"),
                defaultLanguageCode = "en-US"
            ),
            JurisdictionData(
                countryCode = "SG",
                countryName = "Singapore",
                countryNameTa = "சிங்கப்பூர்",
                flagEmoji = "🇸🇬",
                legalSystemName = "Singapore Statutory Framework",
                emergencyHelpline = "999",
                helplineName = "999 Police / 1800-221-4444 Anti-Scam",
                primaryStatutes = listOf("Penal Code 1871", "Employment Act", "Protection from Harassment Act", "Personal Data Protection Act"),
                defaultLanguageCode = "en-IN"
            ),
            JurisdictionData(
                countryCode = "AE",
                countryName = "United Arab Emirates",
                countryNameTa = "ஐக்கிய அரபு அமீரகம் (UAE)",
                flagEmoji = "🇦🇪",
                legalSystemName = "UAE Civil Law & Federal Decrees",
                emergencyHelpline = "999",
                helplineName = "999 Police / 800-2626 Dubai Police",
                primaryStatutes = listOf("Federal Decree Law No. 33 (Labor)", "Federal Decree Law No. 34 (Cybercrime)", "Rental Dispute Settlement Center"),
                defaultLanguageCode = "en-US"
            ),
            JurisdictionData(
                countryCode = "DE",
                countryName = "Germany",
                countryNameTa = "ஜெர்மனி",
                flagEmoji = "🇩🇪",
                legalSystemName = "Bürgerliches Gesetzbuch (BGB) & EU Law",
                emergencyHelpline = "110",
                helplineName = "110 Police / 112 Emergency",
                primaryStatutes = listOf("BGB Civil Code", "Kündigungsschutzgesetz (KSchG)", "DSGVO (GDPR)", "NetzDG"),
                defaultLanguageCode = "en-US"
            ),
            JurisdictionData(
                countryCode = "FR",
                countryName = "France",
                countryNameTa = "பிரான்ஸ்",
                flagEmoji = "🇫🇷",
                legalSystemName = "Code Civil & European Directives",
                emergencyHelpline = "17",
                helplineName = "17 Police / 112 EU Emergency",
                primaryStatutes = listOf("Code Civil", "Code du Travail", "Loi du 6 Juillet 1989 (Bail)", "Code de la Consommation"),
                defaultLanguageCode = "en-US"
            ),
            JurisdictionData(
                countryCode = "JP",
                countryName = "Japan",
                countryNameTa = "ஜப்பான்",
                flagEmoji = "🇯🇵",
                legalSystemName = "Six Codes of Japan (六法)",
                emergencyHelpline = "110",
                helplineName = "110 Police / 119 Ambulance",
                primaryStatutes = listOf("Civil Code (民法)", "Land and Building Lease Act", "Labor Standards Act", "Act on Protection of Personal Information"),
                defaultLanguageCode = "en-US"
            ),
            JurisdictionData(
                countryCode = "CA",
                countryName = "Canada",
                countryNameTa = "கனடா",
                flagEmoji = "🇨🇦",
                legalSystemName = "Canadian Common Law & Civil Code",
                emergencyHelpline = "911",
                helplineName = "911 Emergency / CAFC Anti-Fraud",
                primaryStatutes = listOf("Criminal Code of Canada", "Residential Tenancies Act", "Canada Labour Code", "PIPEDA"),
                defaultLanguageCode = "en-US"
            ),
            JurisdictionData(
                countryCode = "AU",
                countryName = "Australia",
                countryNameTa = "ஆஸ்திரேலியா",
                flagEmoji = "🇦🇺",
                legalSystemName = "Australian Commonwealth & State Law",
                emergencyHelpline = "000",
                helplineName = "000 Emergency / Scamwatch",
                primaryStatutes = listOf("Australian Consumer Law (ACL)", "Residential Tenancies Act", "Fair Work Act 2009", "Privacy Act 1988"),
                defaultLanguageCode = "en-US"
            )
        )
    }

    private val securityPreferences = SecurityPreferences.getInstance(context)

    private val _currentJurisdiction = MutableStateFlow(
        getJurisdictionByCode(securityPreferences.getSelectedJurisdictionCode())
    )
    val currentJurisdiction: StateFlow<JurisdictionData> = _currentJurisdiction.asStateFlow()

    private val _selectionMode = MutableStateFlow(JurisdictionSelectionMode.AUTO_GPS)
    val selectionMode: StateFlow<JurisdictionSelectionMode> = _selectionMode.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation.asStateFlow()

    suspend fun detectAutoLocation(): Boolean = withContext(Dispatchers.IO) {
        var resolvedSuccess = false
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            Log.w(TAG, "Location permission not granted.")
            return@withContext false
        }

        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val countryCode = addresses?.firstOrNull()?.countryCode?.uppercase()
                        if (!countryCode.isNullOrBlank()) {
                            val resolved = getJurisdictionByCode(countryCode)
                            _currentJurisdiction.value = resolved
                            _selectionMode.value = JurisdictionSelectionMode.AUTO_GPS
                            securityPreferences.setSelectedJurisdictionCode(resolved.countryCode)
                            resolvedSuccess = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Geocoder error: ${e.message}", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception accessing location client: ${e.message}", e)
        }
        resolvedSuccess
    }

    /**
     * Resolves location using FusedLocationProviderClient and Geocoder.
     * Fallback to default India (IN) or saved manual selection if permission is denied.
     */
    fun resolveCurrentLocation(context: Context, onResolved: ((JurisdictionData) -> Unit)? = null) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            Log.w(TAG, "Location permission not granted. Retaining current jurisdiction.")
            onResolved?.invoke(_currentJurisdiction.value)
            return
        }

        _isLoadingLocation.value = true
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val countryCode = addresses?.firstOrNull()?.countryCode?.uppercase()
                        if (!countryCode.isNullOrBlank()) {
                            val resolved = getJurisdictionByCode(countryCode)
                            _currentJurisdiction.value = resolved
                            _selectionMode.value = JurisdictionSelectionMode.AUTO_GPS
                            securityPreferences.setSelectedJurisdictionCode(resolved.countryCode)
                            Log.d(TAG, "Successfully resolved location: ${resolved.countryName} ($countryCode)")
                            onResolved?.invoke(resolved)
                        } else {
                            onResolved?.invoke(_currentJurisdiction.value)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Geocoder error: ${e.message}", e)
                        onResolved?.invoke(_currentJurisdiction.value)
                    }
                } else {
                    onResolved?.invoke(_currentJurisdiction.value)
                }
                _isLoadingLocation.value = false
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch GPS location: ${e.message}", e)
                _isLoadingLocation.value = false
                onResolved?.invoke(_currentJurisdiction.value)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception accessing location client: ${e.message}", e)
            _isLoadingLocation.value = false
            onResolved?.invoke(_currentJurisdiction.value)
        }
    }

    /**
     * Manually overrides current active jurisdiction worldwide.
     */
    fun setManualJurisdiction(countryCode: String) {
        val resolved = getJurisdictionByCode(countryCode)
        _selectionMode.value = JurisdictionSelectionMode.MANUAL_OVERRIDE
        _currentJurisdiction.value = resolved
        securityPreferences.setSelectedJurisdictionCode(resolved.countryCode)
        Log.d(TAG, "Manual jurisdiction override set to: ${resolved.countryName} (${resolved.countryCode})")
    }

    /**
     * Toggles between Auto-GPS Mode and Manual Override Mode.
     */
    fun setSelectionMode(mode: JurisdictionSelectionMode) {
        _selectionMode.value = mode
    }

    fun getJurisdictionByCode(code: String): JurisdictionData {
        return WORLD_JURISDICTIONS.firstOrNull { it.countryCode.equals(code, ignoreCase = true) }
            ?: WORLD_JURISDICTIONS.first { it.countryCode == "IN" }
    }
}
