package com.example.data.remote

import com.example.data.remote.dto.SupabaseBudgetDto
import com.example.data.remote.dto.SupabaseTransactionDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

    @GET("rest/v1/")
    suspend fun ping(): Response<ResponseBody>

    @GET("rest/v1/transactions?select=*&order=timestamp.desc")
    suspend fun getTransactions(): Response<List<SupabaseTransactionDto>>

    @POST("rest/v1/transactions")
    suspend fun createTransaction(
        @Header("Prefer") prefer: String = "return=representation",
        @Body transaction: SupabaseTransactionDto
    ): Response<List<SupabaseTransactionDto>>

    @PATCH("rest/v1/transactions")
    suspend fun updateTransaction(
        @Query("id") idQuery: String, // e.g. "eq.123"
        @Body transaction: SupabaseTransactionDto
    ): Response<ResponseBody>

    @DELETE("rest/v1/transactions")
    suspend fun deleteTransaction(
        @Query("id") idQuery: String // e.g. "eq.123"
    ): Response<ResponseBody>

    @GET("rest/v1/budgets?select=*")
    suspend fun getBudgets(): Response<List<SupabaseBudgetDto>>

    @POST("rest/v1/budgets")
    suspend fun upsertBudget(
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body budget: SupabaseBudgetDto
    ): Response<ResponseBody>

    @DELETE("rest/v1/budgets")
    suspend fun deleteBudget(
        @Query("category_id") categoryIdQuery: String // e.g. "eq.cat_alimentacao"
    ): Response<ResponseBody>
}
