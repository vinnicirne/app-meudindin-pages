package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class AdminCreateUserRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "email_confirm") val emailConfirm: Boolean = true
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "user") val user: SupabaseUserDto? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUserDto(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "app_metadata") val appMetadata: Map<String, Any?>? = null,
    @Json(name = "user_metadata") val userMetadata: Map<String, Any?>? = null
) {
    val isAdmin: Boolean
        get() {
            val appRole = appMetadata?.get("role")?.toString()?.lowercase()
            val appIsAdmin = appMetadata?.get("is_admin") == true || appMetadata?.get("is_admin")?.toString()?.toBoolean() == true
            val userRole = userMetadata?.get("role")?.toString()?.lowercase()
            val userIsAdmin = userMetadata?.get("is_admin") == true || userMetadata?.get("is_admin")?.toString()?.toBoolean() == true
            return appRole == "admin" || appIsAdmin || userRole == "admin" || userIsAdmin ||
                    email.equals("viniciuscirne@gmail.com", ignoreCase = true)
        }
}
