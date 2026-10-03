package com.example.domain.model

/**
 * High-precision legal complaint result dynamically synthesized by NyayaMate
 * strictly choosing the applicable law according to the user's grievance facts.
 */
data class LegalComplaintResult(
    val category: String,
    val law: String,
    val authority: String,
    val subject: String,
    val facts: String,
    val relief: String,
    val explanationTamil: String
)
