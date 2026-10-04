package com.justra.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CaseEntity::class,
        ChatMessageEntity::class,
        ActionStepEntity::class,
        EvidenceArtifactEntity::class,
        TimelineEventEntity::class,
        ScamIncidentEntity::class,
        ShareTokenEntity::class,
        WorldWideLawSourceEntity::class,
        WorldWideLawDocEntity::class,
        UserLegalDocumentEntity::class,
        DraftTemplateEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NyayaDatabase : RoomDatabase() {
    abstract fun caseDao(): CaseDao
    abstract fun chatDao(): ChatDao
    abstract fun actionStepDao(): ActionStepDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun timelineDao(): TimelineDao
    abstract fun scamDao(): ScamDao
    abstract fun shareTokenDao(): ShareTokenDao
    abstract fun worldWideLawDao(): WorldWideLawDao
    abstract fun legalDocumentDao(): LegalDocumentDao
    abstract fun draftTemplateDao(): DraftTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: NyayaDatabase? = null

        fun getDatabase(context: Context): NyayaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NyayaDatabase::class.java,
                    "justra_legal_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
