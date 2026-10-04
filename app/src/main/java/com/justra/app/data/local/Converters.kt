package com.justra.app.data.local

import androidx.room.TypeConverter
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.EvidenceCategory
import com.justra.app.domain.model.RiskLevel
import com.justra.app.domain.model.SenderRole
import java.util.Collections

class Converters {
    @TypeConverter
    fun fromDisputeCategory(category: DisputeCategory?): String? = category?.name

    @TypeConverter
    fun toDisputeCategory(value: String?): DisputeCategory? =
        value?.let { enumValueOf<DisputeCategory>(it) }

    @TypeConverter
    fun fromEvidenceCategory(category: EvidenceCategory?): String? = category?.name

    @TypeConverter
    fun toEvidenceCategory(value: String?): EvidenceCategory? =
        value?.let { enumValueOf<EvidenceCategory>(it) }

    @TypeConverter
    fun fromRiskLevel(level: RiskLevel?): String? = level?.name

    @TypeConverter
    fun toRiskLevel(value: String?): RiskLevel? =
        value?.let { enumValueOf<RiskLevel>(it) }

    @TypeConverter
    fun fromSenderRole(role: SenderRole?): String? = role?.name

    @TypeConverter
    fun toSenderRole(value: String?): SenderRole? =
        value?.let { enumValueOf<SenderRole>(it) }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.joinToString("|||")

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split("|||").filter { it.isNotEmpty() }
    }
}
