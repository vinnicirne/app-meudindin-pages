package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

data class UserSession(
    val userId: String,
    val email: String,
    val accessToken: String?,
    val refreshToken: String?,
    val isAdmin: Boolean = false
)

class AuthPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("finanzt_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_IS_ADMIN = "is_admin"

        @Volatile
        private var INSTANCE: AuthPreferences? = null

        fun getInstance(context: Context): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    fun saveSession(userId: String, email: String, accessToken: String?, refreshToken: String?, isAdmin: Boolean = false) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply()
    }

    fun getSession(): UserSession? {
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val token = prefs.getString(KEY_ACCESS_TOKEN, null)
        val refresh = prefs.getString(KEY_REFRESH_TOKEN, null)
        val isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false) || email.equals("viniciuscirne@gmail.com", ignoreCase = true)
        return UserSession(userId, email, token, refresh, isAdmin)
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getString(KEY_USER_ID, null) != null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
