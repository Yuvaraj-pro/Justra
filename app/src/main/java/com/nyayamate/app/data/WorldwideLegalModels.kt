package com.nyayamate.app.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class UserLegalContext(
    val countryCode: String? = null,
    val countryName: String? = null,
    val stateOrRegion: String? = null,
    val preferredLanguage: String = "ta"
)

@JsonClass(generateAdapter = true)
data class WorldwideLegalResponse(
    @Json(name = "jurisdiction")
    val jurisdiction: JurisdictionInfo,

    @Json(name = "disputeClassification")
    val disputeClassification: DisputeClassification,

    @Json(name = "caseReadiness")
    val caseReadiness: CaseReadiness,

    @Json(name = "actionSteps")
    val actionSteps: List<ActionStep>,

    @Json(name = "timelineMilestones")
    val timelineMilestones: List<TimelineMilestone>,

    @Json(name = "formalComplaintDraft")
    val formalComplaintDraft: FormalComplaintDraft,

    @Json(name = "localizedResponse")
    val localizedResponse: LocalizedResponse,

    @Json(name = "disclaimer")
    val disclaimer: String = "Justra provides automated legal information, case organization, and statutory procedural guidance. It does not constitute formal legal representation or an advocate-client relationship under the Advocates Act or local statutory regulations."
)

@JsonClass(generateAdapter = true)
data class JurisdictionInfo(
    @Json(name = "countryCode")
    val countryCode: String,

    @Json(name = "countryName")
    val countryName: String,

    @Json(name = "legalFamily")
    val legalFamily: String // "Common Law" | "Civil Law" | "Mixed"
)

@JsonClass(generateAdapter = true)
data class DisputeClassification(
    @Json(name = "primaryCategory")
    val primaryCategory: String, // "Tenancy" | "Employment" | "Cybercrime" | "Consumer" | "Criminal" | "Property" | "Other"

    @Json(name = "subCategory")
    val subCategory: String,

    @Json(name = "incidentSummary")
    val incidentSummary: String,

    @Json(name = "applicableStatute")
    val applicableStatute: String,

    @Json(name = "enforcingAuthorityOrPortal")
    val enforcingAuthorityOrPortal: String,

    @Json(name = "helplineNumber")
    val helplineNumber: String? = null
)

@JsonClass(generateAdapter = true)
data class CaseReadiness(
    @Json(name = "readinessScorePercentage")
    val readinessScorePercentage: Int,

    @Json(name = "availableFacts")
    val availableFacts: List<String>,

    @Json(name = "missingCriticalEvidence")
    val missingCriticalEvidence: List<String>
)

@JsonClass(generateAdapter = true)
data class ActionStep(
    @Json(name = "stepOrder")
    val stepOrder: Int,

    @Json(name = "stepTitle")
    val stepTitle: String,

    @Json(name = "instruction")
    val instruction: String,

    @Json(name = "portalUrlOrLocation")
    val portalUrlOrLocation: String? = null
)

@JsonClass(generateAdapter = true)
data class TimelineMilestone(
    @Json(name = "dateOrPeriod")
    val dateOrPeriod: String,

    @Json(name = "event")
    val event: String,

    @Json(name = "isInferred")
    val isInferred: Boolean = false
)

@JsonClass(generateAdapter = true)
data class FormalComplaintDraft(
    @Json(name = "subjectLine")
    val subjectLine: String,

    @Json(name = "addressedTo")
    val addressedTo: String,

    @Json(name = "statementOfFacts")
    val statementOfFacts: String,

    @Json(name = "demandedRelief")
    val demandedRelief: String
)

@JsonClass(generateAdapter = true)
data class LocalizedResponse(
    @Json(name = "language")
    val language: String, // "Tamil" | "English"

    @Json(name = "simpleExplanation")
    val simpleExplanation: String,

    @Json(name = "immediateNextStep")
    val immediateNextStep: String
)
