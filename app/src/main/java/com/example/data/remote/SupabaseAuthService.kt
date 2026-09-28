package com.example.data.remote

import com.example.data.remote.dto.AdminCreateUserRequest
import com.example.data.remote.dto.AuthResponse
import com.example.data.remote.dto.LoginRequest
import com.example.data.remote.dto.SupabaseUserDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SupabaseAuthService {

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/v1/signup")
    suspend fun signup(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/v1/admin/users")
    suspend fun adminCreateUser(
        @Header("apikey") adminApiKey: String,
        @Header("Authorization") adminAuth: String,
        @Body request: AdminCreateUserRequest
    ): Response<SupabaseUserDto>

    @GET("auth/v1/user")
    suspend fun getUser(): Response<SupabaseUserDto>

    @POST("auth/v1/logout")
    suspend fun logout(): Response<ResponseBody>
}
