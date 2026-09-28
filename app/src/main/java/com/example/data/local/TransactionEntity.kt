package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "RECEITA" or "DESPESA"
    val categoryId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val recurrenceType: String = "UNICA", // "UNICA", "FIXA", "PARCELADA"
    val installmentNumber: Int = 1,
    val totalInstallments: Int = 1,
    val seriesId: String? = null
) {
    val transactionType: TransactionType
        get() = try {
            TransactionType.valueOf(type)
        } catch (_: Exception) {
            TransactionType.DESPESA
        }

    val isInstallment: Boolean
        get() = recurrenceType == "PARCELADA" || (recurrenceType != "FIXA" && totalInstallments > 1)

    val isFixedRecurring: Boolean
        get() = recurrenceType == "FIXA"
}
