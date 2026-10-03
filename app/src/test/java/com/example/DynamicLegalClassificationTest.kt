package com.example

import com.example.data.api.GeminiLegalEngine
import com.example.domain.model.LanguagePreference
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DynamicLegalClassificationTest {

    @Test
    fun testTenancyClassification() = runBlocking {
        val transcript = "My landlord is refusing to return my security deposit of Rs. 50,000 and threatening eviction."
        val result = GeminiLegalEngine.analyzeDynamicVoiceGrievance(transcript, LanguagePreference.TAMIL)

        assertEquals("Tenancy", result.category)
        assertTrue(result.law.contains("Model Tenancy Act") || result.law.contains("Tenancy"))
        assertTrue(result.authority.contains("Rent Authority") || result.authority.contains("Rent Court"))
        assertTrue(result.subject.isNotEmpty())
        assertTrue(result.relief.isNotEmpty())
        assertTrue(result.explanationTamil.isNotEmpty())
    }

    @Test
    fun testEmploymentClassification() = runBlocking {
        val transcript = "My employer has delayed my salary and unpaid wages for 3 months amounting to INR 75,000."
        val result = GeminiLegalEngine.analyzeDynamicVoiceGrievance(transcript, LanguagePreference.TAMIL)

        assertEquals("Employment", result.category)
        assertTrue(result.law.contains("Payment of Wages Act") || result.law.contains("Industrial Disputes Act"))
        assertTrue(result.authority.contains("Labour") || result.authority.contains("Labor") || result.authority.contains("Samadhan"))
        assertTrue(result.subject.isNotEmpty())
        assertTrue(result.relief.isNotEmpty())
    }

    @Test
    fun testCyberFraudClassification() = runBlocking {
        val transcript = "I received a fake call with OTP request and lost 15000 rupees in a UPI phishing scam."
        val result = GeminiLegalEngine.analyzeDynamicVoiceGrievance(transcript, LanguagePreference.TAMIL)

        assertEquals("Cyber", result.category)
        assertTrue(result.law.contains("Information Technology Act") || result.law.contains("318"))
        assertTrue(result.authority.contains("1930") || result.authority.contains("cybercrime.gov.in"))
    }

    @Test
    fun testCriminalClassification() = runBlocking {
        val transcript = "The opposing party came with weapons, gave me death threat and physical assault."
        val result = GeminiLegalEngine.analyzeDynamicVoiceGrievance(transcript, LanguagePreference.TAMIL)

        assertEquals("Criminal", result.category)
        assertTrue(result.law.contains("Bharatiya Nyaya Sanhita") || result.law.contains("BNS"))
        assertTrue(result.authority.contains("Police Station") || result.authority.contains("CCTNS"))
    }

    @Test
    fun testConsumerClassification() = runBlocking {
        val transcript = "I ordered a mobile from Amazon which was defective and delivery refused to refund my Rs 12000."
        val result = GeminiLegalEngine.analyzeDynamicVoiceGrievance(transcript, LanguagePreference.TAMIL)

        assertEquals("Consumer", result.category)
        assertTrue(result.law.contains("Consumer Protection Act"))
        assertTrue(result.authority.contains("Consumer") || result.authority.contains("e-Daakhil"))
    }
}
