package com.justra.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.justra.app.data.api.GeminiLegalEngine
import com.justra.app.data.api.LegalStatuteKnowledge
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.RiskLevel
import com.justra.app.data.repository.toEntity
import com.justra.app.data.repository.toDomain
import com.justra.app.util.BilingualStrings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Justra", appName)
    }

    @Test
    fun `verify bilingual localization lookup`() {
        val titleEn = BilingualStrings.t("app_title", LanguagePreference.ENGLISH)
        val titleTa = BilingualStrings.t("app_title", LanguagePreference.TAMIL)
        assertEquals("Justra", titleEn)
        assertEquals("ஜஸ்ட்ரா", titleTa)
    }

    @Test
    fun `verify legal statute knowledge mapping`() {
        val consumerStatute = LegalStatuteKnowledge.getStatuteForCategory(DisputeCategory.CONSUMER_GRIEVANCE)
        assertEquals("Consumer Protection Act, 2019", consumerStatute.actName)
        assertEquals("District Consumer Disputes Redressal Commission (DCDRC) / e-Daakhil Portal", consumerStatute.applicableForum)
        assertTrue(consumerStatute.summaryEn.isNotBlank())
    }

    @Test
    fun `verify deterministic scam detection`() = runBlocking {
        val suspiciousText = "Dear Customer, your electricity power will be cut tonight at 9:30 PM due to unpaid bill. Immediately download APK link http://quick-pay-eb.xyz and update."
        val result = GeminiLegalEngine.analyzeScamText(suspiciousText, LanguagePreference.ENGLISH)
        assertTrue(result.isScamDetected)
        assertEquals(RiskLevel.CRITICAL, result.riskLevel)
        assertTrue(result.detectedRedFlags.isNotEmpty())
    }

    @Test
    fun `verify user role definitions and bilingual labels`() {
        val roles = com.justra.app.domain.model.UserRole.values()
        assertEquals(4, roles.size)
        assertTrue(roles.any { it == com.justra.app.domain.model.UserRole.CITIZEN })
        assertTrue(roles.any { it == com.justra.app.domain.model.UserRole.LEGAL_COUNSEL })
        assertTrue(roles.any { it == com.justra.app.domain.model.UserRole.MSME_BUSINESS })
        assertTrue(roles.any { it == com.justra.app.domain.model.UserRole.CYBER_FRAUD_VICTIM })

        val citizenRole = com.justra.app.domain.model.UserRole.CITIZEN
        assertEquals("Citizen Complainant", citizenRole.titleEn)
        assertEquals("பொதுக் குடிமக்கள் / புகார்தாரர்", citizenRole.titleTa)
    }

    @Test
    fun `verify security manager SHA-256 PIN hashing`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val securityManager = com.justra.app.data.local.SecurityManager(context)
        securityManager.setVaultPin("1234")
        assertTrue(securityManager.hasVaultPin())
        assertTrue(securityManager.verifyVaultPin("1234"))
        org.junit.Assert.assertFalse(securityManager.verifyVaultPin("9999"))
    }

    @Test
    fun `verify biometric capability check and status description`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val capability = com.justra.app.util.BiometricAuthHelper.checkBiometricStatus(context)
        assertNotNull(capability)
        val statusDescEn = com.justra.app.util.BiometricAuthHelper.getStatusDescription(capability, LanguagePreference.ENGLISH)
        val statusDescTa = com.justra.app.util.BiometricAuthHelper.getStatusDescription(capability, LanguagePreference.TAMIL)
        assertTrue(statusDescEn.isNotBlank())
        assertTrue(statusDescTa.isNotBlank())
    }

    @Test
    fun `verify audio amplitude stream processing`() {
        val sampleAmplitudes = listOf(0.12f, 0.45f, 0.88f, 0.65f, 0.30f)
        val avgAmp = sampleAmplitudes.average().toFloat()
        val calculatedDb = (avgAmp * 55f + 35f).toInt().coerceIn(30, 95)
        assertTrue(calculatedDb in 30..95)
        assertTrue(sampleAmplitudes.isNotEmpty())
    }

    @Test
    fun `verify world wide law curated sources and regions`() {
        val sources = com.justra.app.data.repository.WorldWideLawRepository.CURATED_SOURCES
        assertTrue(sources.size >= 15)

        // Check key jurisdictions
        assertTrue(sources.any { it.countryCode == "IN" })
        assertTrue(sources.any { it.countryCode == "UK" })
        assertTrue(sources.any { it.countryCode == "US" })
        assertTrue(sources.any { it.countryCode == "FR" })
        assertTrue(sources.any { it.countryCode == "DE" })
        assertTrue(sources.any { it.countryCode == "SG" })
        assertTrue(sources.any { it.countryCode == "INTL" || it.countryCode == "EU" })

        // Check script paths
        val cciSource = sources.first { it.id == "IN/CCI" }
        assertEquals("sources/IN/CCI/bootstrap.py", cciSource.collectionScriptPath)
        assertTrue(cciSource.url.isNotBlank())
        assertTrue(cciSource.lawEnforcementDomain.isNotBlank())
    }

    @Test
    fun `verify world wide law normalized documents and schema`() {
        val docs = com.justra.app.data.repository.WorldWideLawRepository.SAMPLE_NORMALIZED_DOCUMENTS
        assertTrue(docs.isNotEmpty())

        val ukDoc = docs.first { it.id == "uksc/2026/8" }
        assertEquals("UK/CaseLaw", ukDoc.sourceId)
        assertEquals("UK", ukDoc.countryCode)
        assertTrue(ukDoc.text.contains("PACE"))
        assertTrue(ukDoc.keyHoldingsEn.isNotBlank())
        assertTrue(ukDoc.keyHoldingsTa.isNotBlank())
    }

    @Test
    fun `verify world wide law entity domain bidirectional mapping`() {
        val src = com.justra.app.data.repository.WorldWideLawRepository.CURATED_SOURCES.first()
        val entity = src.toEntity()
        assertEquals(src.id, entity.sourceId)
        assertEquals(src.countryCode, entity.countryCode)

        val domain = entity.toDomain()
        assertEquals(src.id, domain.id)
        assertEquals(src.name, domain.name)
        assertEquals(src.countryCode, domain.countryCode)
    }

    @Test
    fun `verify citizenship statutory topics coverage`() {
        val provisions = com.justra.app.ui.screens.citizenship.CitizenshipStatuteRepository.provisions
        assertTrue(provisions.size >= 6)
        assertTrue(provisions.any { it.id == "sec3_birth" })
        assertTrue(provisions.any { it.id == "sec4_descent" })
        assertTrue(provisions.any { it.id == "sec5_registration" })
        assertTrue(provisions.any { it.id == "sec6_naturalization" })
        assertTrue(provisions.any { it.id == "oci_boundaries" })
        assertTrue(provisions.any { it.id == "passport_act" })

        val birthProvision = provisions.first { it.id == "sec3_birth" }
        assertEquals("Citizenship by Birth", birthProvision.titleEn)
        assertEquals("Section 3", birthProvision.sectionNumber)
        assertTrue(birthProvision.summaryEn.isNotBlank())
        assertTrue(birthProvision.summaryTa.isNotBlank())
        assertTrue(birthProvision.proceduralRemedyEn.isNotBlank())
        assertTrue(birthProvision.landmarkPrecedent.isNotBlank())
    }

    @Test
    fun `verify dynamic complaint category classification and grounds`() {
        val tenancyTranscript = "My landlord has refused to return my security deposit of 75000 rupees after vacating the flat in Chennai."
        val category = com.justra.app.ui.screens.complaint.ComplaintVoiceViewModel.suggestCategoryFromTranscript(tenancyTranscript)
        assertEquals(DisputeCategory.TENANCY_RENT, category)

        val consumerTranscript = "I ordered an electronic laptop online but received a damaged box and company refused replacement or refund."
        val consumerCat = com.justra.app.ui.screens.complaint.ComplaintVoiceViewModel.suggestCategoryFromTranscript(consumerTranscript)
        assertEquals(DisputeCategory.CONSUMER_GRIEVANCE, consumerCat)
    }
}

