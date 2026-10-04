package com.justra.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.EvidenceCategory
import com.justra.app.domain.model.RiskLevel
import com.justra.app.domain.model.SenderRole

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey val caseId: String,
    val title: String,
    val disputeCategory: DisputeCategory,
    val status: String = "ACTIVE",
    val incidentDate: String? = null,
    val opposingParty: String? = null,
    val estimatedClaimAmount: String? = null,
    val factualSummary: String? = null,
    val demandedRelief: String? = null,
    val recipientAuthority: String? = null,
    val subjectLine: String? = null,
    val generatedComplaintDraft: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val messageId: String,
    val caseId: String? = null,
    val userId: String = "local_user",
    val senderRole: SenderRole,
    val timestamp: Long = System.currentTimeMillis(),
    val messageContent: String,
    val extractedEntityKey: String? = null,
    val extractedEntityValue: String? = null,
    val isActionableProof: Boolean = false
)

@Entity(tableName = "action_steps")
data class ActionStepEntity(
    @PrimaryKey val stepId: String,
    val caseId: String,
    val stepNumber: Int,
    val actionTitleEn: String,
    val actionTitleTa: String,
    val descriptionEn: String,
    val descriptionTa: String,
    val targetAuthority: String,
    val isCompleted: Boolean = false,
    val directActionUrl: String? = null,
    val requiredDocumentList: List<String> = emptyList()
)

@Entity(tableName = "evidence_artifacts")
data class EvidenceArtifactEntity(
    @PrimaryKey val artifactId: String,
    val caseId: String,
    val fileName: String,
    val fileUri: String,
    val mimeType: String,
    val category: EvidenceCategory,
    val sha256Hash: String,
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)

@Entity(tableName = "timeline_events")
data class TimelineEventEntity(
    @PrimaryKey val eventId: String,
    val caseId: String,
    val eventDate: String,
    val eventTitle: String,
    val description: String,
    val isInferred: Boolean = false,
    val sourceReference: String? = null,
    val isVerified: Boolean = false
)

@Entity(tableName = "scam_incidents")
data class ScamIncidentEntity(
    @PrimaryKey val incidentId: String,
    val analyzedText: String,
    val riskLevel: RiskLevel,
    val isScamDetected: Boolean,
    val detectedRedFlags: List<String>,
    val safetyGuidance: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "share_tokens")
data class ShareTokenEntity(
    @PrimaryKey val tokenId: String,
    val caseId: String,
    val tokenCode: String,
    val signatureHash: String,
    val expiresAt: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val advocateNotes: String? = null
)

@Entity(tableName = "world_wide_law_sources")
data class WorldWideLawSourceEntity(
    @PrimaryKey val sourceId: String,
    val countryCode: String,
    val countryNameEn: String,
    val countryNameTa: String,
    val regionName: String,
    val name: String,
    val url: String,
    val dataTypes: List<String>,
    val status: String,
    val licenseId: String? = null,
    val licenseName: String? = null,
    val auth: String = "none",
    val notes: String? = null,
    val lawEnforcementDomain: String? = null,
    val collectionScriptPath: String? = null,
    val isPinnedOffline: Boolean = false,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "world_wide_law_docs")
data class WorldWideLawDocEntity(
    @PrimaryKey val docId: String,
    val sourceId: String,
    val countryCode: String,
    val docType: String,
    val title: String,
    val text: String,
    val date: String? = null,
    val url: String,
    val keyHoldingsEn: String? = null,
    val keyHoldingsTa: String? = null,
    val lawEnforcementSubject: String? = null,
    val isSavedToVault: Boolean = false,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_legal_documents")
data class UserLegalDocumentEntity(
    @PrimaryKey val documentId: String,
    val caseId: String? = null,
    val title: String,
    val documentType: String, // e.g. "LEGAL_NOTICE", "COMPLAINT_PETITION", "RTI_APPLICATION", "CONSUMER_GRIEVANCE", "TENANCY_NOTICE", "AFFIDAVIT", "COMMERCIAL_DEMAND"
    val disputeCategory: DisputeCategory,
    val content: String,
    val recipientParty: String? = null,
    val jurisdictionCourt: String? = null,
    val statutoryActRef: String? = null,
    val language: String = "EN",
    val isDraft: Boolean = true,
    val isFinalized: Boolean = false,
    val exportPdfPath: String? = null,
    val fileSizeBytes: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)

@Entity(tableName = "draft_templates")
data class DraftTemplateEntity(
    @PrimaryKey val templateId: String,
    val templateTitleEn: String,
    val templateTitleTa: String,
    val disputeCategory: DisputeCategory,
    val documentType: String,
    val statuteRef: String,
    val descriptionEn: String,
    val descriptionTa: String,
    val templateBodyEn: String,
    val templateBodyTa: String,
    val variablePlaceholders: List<String> = emptyList(),
    val applicableForums: List<String> = emptyList(),
    val isSystemDefault: Boolean = true,
    val isFavorite: Boolean = false,
    val usageCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
