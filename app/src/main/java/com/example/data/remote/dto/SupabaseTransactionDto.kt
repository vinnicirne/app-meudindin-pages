package com.example.data.remote.dto

import com.example.data.local.TransactionEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseTransactionDto(
    @Json(name = "id")
    val id: Long? = null,
    @Json(name = "title")
    val title: String,
    @Json(name = "amount")
    val amount: Double,
    @Json(name = "type")
    val type: String,
    @Json(name = "category_id")
    val categoryId: String,
    @Json(name = "timestamp")
    val timestamp: Long,
    @Json(name = "notes")
    val notes: String? = ""
) {
    fun toEntity(): TransactionEntity {
        // Extract recurrence metadata if stored in notes or title
        val rawNotes = notes ?: ""
        val (recType, instNum, totalInst) = when {
            rawNotes.contains("[PARCELA") || title.contains("Parcela ") -> {
                val instRegex = Regex("""(?:\[PARCELA\s*|Parcela\s*)(\d+)/(\d+)""")
                val match = instRegex.find(rawNotes) ?: instRegex.find(title)
                if (match != null) {
                    val num = match.groupValues[1].toIntOrNull() ?: 1
                    val tot = match.groupValues[2].toIntOrNull() ?: 1
                    Triple("PARCELADA", num, tot)
                } else {
                    Triple("PARCELADA", 1, 1)
                }
            }
            rawNotes.contains("[FIXA]") || rawNotes.contains("[RECORRENTE]") -> {
                Triple("FIXA", 1, 1)
            }
            else -> Triple("UNICA", 1, 1)
        }

        return TransactionEntity(
            id = id ?: 0L,
            title = title,
            amount = amount,
            type = type,
            categoryId = categoryId,
            timestamp = timestamp,
            notes = rawNotes,
            recurrenceType = recType,
            installmentNumber = instNum,
            totalInstallments = totalInst
        )
    }

    companion object {
        fun fromEntity(entity: TransactionEntity, includeId: Boolean = true): SupabaseTransactionDto {
            // Include helpful recurrence tag in notes for clarity
            val enrichedNotes = buildString {
                if (entity.recurrenceType == "PARCELADA") {
                    append("[PARCELA ${entity.installmentNumber}/${entity.totalInstallments}] ")
                } else if (entity.recurrenceType == "FIXA") {
                    append("[FIXA] ")
                }
                append(entity.notes)
            }.trim()

            return SupabaseTransactionDto(
                id = if (includeId && entity.id > 0) entity.id else null,
                title = entity.title,
                amount = entity.amount,
                type = entity.type,
                categoryId = entity.categoryId,
                timestamp = entity.timestamp,
                notes = enrichedNotes
            )
        }
    }
}
