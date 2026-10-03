package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.domain.model.DisputeCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY updatedAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE status = 'ACTIVE' ORDER BY updatedAt DESC")
    fun getActiveCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    fun getCaseById(caseId: String): Flow<CaseEntity?>

    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    suspend fun getCaseByIdOnce(caseId: String): CaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseEntity: CaseEntity)

    @Update
    suspend fun updateCase(caseEntity: CaseEntity)

    @Delete
    suspend fun deleteCase(caseEntity: CaseEntity)

    @Query("DELETE FROM cases WHERE caseId = :caseId")
    suspend fun deleteCaseById(caseId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE caseId = :caseId OR (caseId IS NULL AND :caseId IS NULL) ORDER BY timestamp ASC")
    fun getMessagesForCase(caseId: String?): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE messageId = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM chat_messages WHERE caseId = :caseId")
    suspend fun clearMessagesForCase(caseId: String)
}

@Dao
interface ActionStepDao {
    @Query("SELECT * FROM action_steps WHERE caseId = :caseId ORDER BY stepNumber ASC")
    fun getStepsForCase(caseId: String): Flow<List<ActionStepEntity>>

    @Query("SELECT * FROM action_steps WHERE caseId = :caseId ORDER BY stepNumber ASC")
    suspend fun getStepsForCaseOnce(caseId: String): List<ActionStepEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<ActionStepEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: ActionStepEntity)

    @Update
    suspend fun updateStep(step: ActionStepEntity)

    @Query("UPDATE action_steps SET isCompleted = :completed WHERE stepId = :stepId")
    suspend fun setStepCompletion(stepId: String, completed: Boolean)
}

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence_artifacts WHERE caseId = :caseId ORDER BY uploadTimestamp DESC")
    fun getArtifactsForCase(caseId: String): Flow<List<EvidenceArtifactEntity>>

    @Query("SELECT COUNT(*) FROM evidence_artifacts WHERE caseId = :caseId")
    fun getArtifactCountForCase(caseId: String): Flow<Int>

    @Query("SELECT * FROM evidence_artifacts ORDER BY uploadTimestamp DESC")
    fun getAllArtifacts(): Flow<List<EvidenceArtifactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifact(artifact: EvidenceArtifactEntity)

    @Delete
    suspend fun deleteArtifact(artifact: EvidenceArtifactEntity)

    @Query("DELETE FROM evidence_artifacts WHERE artifactId = :artifactId")
    suspend fun deleteArtifactById(artifactId: String)
}

@Dao
interface TimelineDao {
    @Query("SELECT * FROM timeline_events WHERE caseId = :caseId ORDER BY eventDate ASC")
    fun getEventsForCase(caseId: String): Flow<List<TimelineEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TimelineEventEntity)

    @Update
    suspend fun updateEvent(event: TimelineEventEntity)

    @Delete
    suspend fun deleteEvent(event: TimelineEventEntity)

    @Query("DELETE FROM timeline_events WHERE eventId = :eventId")
    suspend fun deleteEventById(eventId: String)
}

@Dao
interface ScamDao {
    @Query("SELECT * FROM scam_incidents ORDER BY timestamp DESC")
    fun getAllScamIncidents(): Flow<List<ScamIncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScamIncident(incident: ScamIncidentEntity)

    @Query("DELETE FROM scam_incidents WHERE incidentId = :id")
    suspend fun deleteScamIncident(id: String)
}

@Dao
interface ShareTokenDao {
    @Query("SELECT * FROM share_tokens WHERE caseId = :caseId ORDER BY createdAt DESC")
    fun getTokensForCase(caseId: String): Flow<List<ShareTokenEntity>>

    @Query("SELECT * FROM share_tokens WHERE tokenCode = :tokenCode LIMIT 1")
    suspend fun getTokenByCode(tokenCode: String): ShareTokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: ShareTokenEntity)

    @Query("UPDATE share_tokens SET advocateNotes = :notes WHERE tokenId = :tokenId")
    suspend fun updateAdvocateNotes(tokenId: String, notes: String)
}

@Dao
interface WorldWideLawDao {
    @Query("SELECT * FROM world_wide_law_sources ORDER BY countryCode ASC, name ASC")
    fun getAllSources(): Flow<List<WorldWideLawSourceEntity>>

    @Query("SELECT * FROM world_wide_law_sources WHERE countryCode = :countryCode ORDER BY name ASC")
    fun getSourcesByCountry(countryCode: String): Flow<List<WorldWideLawSourceEntity>>

    @Query("SELECT * FROM world_wide_law_sources WHERE regionName = :regionName ORDER BY countryCode ASC")
    fun getSourcesByRegion(regionName: String): Flow<List<WorldWideLawSourceEntity>>

    @Query("SELECT * FROM world_wide_law_sources WHERE isPinnedOffline = 1")
    fun getPinnedSources(): Flow<List<WorldWideLawSourceEntity>>

    @Query("SELECT * FROM world_wide_law_sources WHERE sourceId = :sourceId LIMIT 1")
    suspend fun getSourceById(sourceId: String): WorldWideLawSourceEntity?

    @Query("SELECT COUNT(*) FROM world_wide_law_sources")
    suspend fun getSourceCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(sources: List<WorldWideLawSourceEntity>)

    @Update
    suspend fun updateSource(source: WorldWideLawSourceEntity)

    @Query("UPDATE world_wide_law_sources SET isPinnedOffline = :isPinned WHERE sourceId = :sourceId")
    suspend fun setSourcePinned(sourceId: String, isPinned: Boolean)

    // Docs
    @Query("SELECT * FROM world_wide_law_docs ORDER BY savedAt DESC")
    fun getAllDocs(): Flow<List<WorldWideLawDocEntity>>

    @Query("SELECT * FROM world_wide_law_docs WHERE sourceId = :sourceId")
    fun getDocsBySource(sourceId: String): Flow<List<WorldWideLawDocEntity>>

    @Query("SELECT * FROM world_wide_law_docs WHERE countryCode = :countryCode")
    fun getDocsByCountry(countryCode: String): Flow<List<WorldWideLawDocEntity>>

    @Query("SELECT * FROM world_wide_law_docs WHERE isSavedToVault = 1")
    fun getVaultDocs(): Flow<List<WorldWideLawDocEntity>>

    @Query("SELECT * FROM world_wide_law_docs WHERE title LIKE '%' || :query || '%' OR text LIKE '%' || :query || '%'")
    fun searchDocs(query: String): Flow<List<WorldWideLawDocEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocs(docs: List<WorldWideLawDocEntity>)

    @Query("UPDATE world_wide_law_docs SET isSavedToVault = :isSaved WHERE docId = :docId")
    suspend fun setDocSavedToVault(docId: String, isSaved: Boolean)

    @Query("SELECT COUNT(*) FROM world_wide_law_docs")
    suspend fun getDocCount(): Int
}

@Dao
interface LegalDocumentDao {
    @Query("SELECT * FROM user_legal_documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<UserLegalDocumentEntity>>

    @Query("SELECT * FROM user_legal_documents WHERE caseId = :caseId ORDER BY updatedAt DESC")
    fun getDocumentsForCase(caseId: String): Flow<List<UserLegalDocumentEntity>>

    @Query("SELECT * FROM user_legal_documents WHERE documentId = :documentId LIMIT 1")
    fun getDocumentById(documentId: String): Flow<UserLegalDocumentEntity?>

    @Query("SELECT * FROM user_legal_documents WHERE documentId = :documentId LIMIT 1")
    suspend fun getDocumentByIdOnce(documentId: String): UserLegalDocumentEntity?

    @Query("SELECT * FROM user_legal_documents WHERE documentType = :type ORDER BY updatedAt DESC")
    fun getDocumentsByType(type: String): Flow<List<UserLegalDocumentEntity>>

    @Query("SELECT * FROM user_legal_documents WHERE disputeCategory = :category ORDER BY updatedAt DESC")
    fun getDocumentsByCategory(category: DisputeCategory): Flow<List<UserLegalDocumentEntity>>

    @Query("SELECT * FROM user_legal_documents WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR statutoryActRef LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchDocuments(query: String): Flow<List<UserLegalDocumentEntity>>

    @Query("SELECT COUNT(*) FROM user_legal_documents")
    fun getDocumentCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_legal_documents")
    suspend fun getDocumentCountOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: UserLegalDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<UserLegalDocumentEntity>)

    @Update
    suspend fun updateDocument(document: UserLegalDocumentEntity)

    @Delete
    suspend fun deleteDocument(document: UserLegalDocumentEntity)

    @Query("DELETE FROM user_legal_documents WHERE documentId = :documentId")
    suspend fun deleteDocumentById(documentId: String)
}

@Dao
interface DraftTemplateDao {
    @Query("SELECT * FROM draft_templates ORDER BY isFavorite DESC, usageCount DESC, templateTitleEn ASC")
    fun getAllTemplates(): Flow<List<DraftTemplateEntity>>

    @Query("SELECT * FROM draft_templates WHERE disputeCategory = :category ORDER BY isFavorite DESC, templateTitleEn ASC")
    fun getTemplatesByCategory(category: DisputeCategory): Flow<List<DraftTemplateEntity>>

    @Query("SELECT * FROM draft_templates WHERE isFavorite = 1 ORDER BY templateTitleEn ASC")
    fun getFavoriteTemplates(): Flow<List<DraftTemplateEntity>>

    @Query("SELECT * FROM draft_templates WHERE templateId = :templateId LIMIT 1")
    fun getTemplateById(templateId: String): Flow<DraftTemplateEntity?>

    @Query("SELECT * FROM draft_templates WHERE templateId = :templateId LIMIT 1")
    suspend fun getTemplateByIdOnce(templateId: String): DraftTemplateEntity?

    @Query("SELECT * FROM draft_templates WHERE templateTitleEn LIKE '%' || :query || '%' OR templateTitleTa LIKE '%' || :query || '%' OR descriptionEn LIKE '%' || :query || '%' OR statuteRef LIKE '%' || :query || '%' ORDER BY templateTitleEn ASC")
    fun searchTemplates(query: String): Flow<List<DraftTemplateEntity>>

    @Query("SELECT COUNT(*) FROM draft_templates")
    suspend fun getTemplateCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: DraftTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<DraftTemplateEntity>)

    @Update
    suspend fun updateTemplate(template: DraftTemplateEntity)

    @Query("UPDATE draft_templates SET isFavorite = :isFavorite WHERE templateId = :templateId")
    suspend fun setTemplateFavorite(templateId: String, isFavorite: Boolean)

    @Query("UPDATE draft_templates SET usageCount = usageCount + 1, updatedAt = :timestamp WHERE templateId = :templateId")
    suspend fun incrementUsage(templateId: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteTemplate(template: DraftTemplateEntity)

    @Query("DELETE FROM draft_templates WHERE templateId = :templateId")
    suspend fun deleteTemplateById(templateId: String)
}

