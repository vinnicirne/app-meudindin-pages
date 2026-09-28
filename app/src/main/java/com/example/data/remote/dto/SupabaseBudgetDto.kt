package com.example.data.remote.dto

import com.example.data.local.BudgetEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseBudgetDto(
    @Json(name = "category_id")
    val categoryId: String,
    @Json(name = "monthly_limit")
    val monthlyLimit: Double
) {
    fun toEntity(): BudgetEntity {
        return BudgetEntity(
            categoryId = categoryId,
            monthlyLimit = monthlyLimit
        )
    }

    companion object {
        fun fromEntity(entity: BudgetEntity): SupabaseBudgetDto {
            return SupabaseBudgetDto(
                categoryId = entity.categoryId,
                monthlyLimit = entity.monthlyLimit
            )
        }
    }
}
