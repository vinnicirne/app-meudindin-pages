package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AuthPreferences
import com.example.data.local.UserSession
import com.example.data.remote.SupabaseAuthService
import com.example.data.remote.SupabaseClient
import com.example.data.remote.dto.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authPreferences: AuthPreferences,
    private val authService: SupabaseAuthService = SupabaseClient.authService
) {

    private val _currentUser = MutableStateFlow<UserSession?>(authPreferences.getSession())
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    init {
        // Initialize Supabase token from saved session if exists
        authPreferences.getSession()?.accessToken?.let { token ->
            SupabaseClient.userAccessToken = token
        }
    }

    suspend fun login(email: String, password: String): Result<UserSession> = withContext(Dispatchers.IO) {
        try {
            val trimmedEmail = email.trim()
            val response = authService.login(LoginRequest(trimmedEmail, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val user = body.user
                val token = body.accessToken
                if (user != null && token != null) {
                    val isAdmin = user.isAdmin
                    authPreferences.saveSession(
                        userId = user.id,
                        email = user.email ?: trimmedEmail,
                        accessToken = token,
                        refreshToken = body.refreshToken,
                        isAdmin = isAdmin
                    )
                    SupabaseClient.userAccessToken = token
                    val session = UserSession(user.id, user.email ?: trimmedEmail, token, body.refreshToken, isAdmin)
                    _currentUser.value = session
                    return@withContext Result.success(session)
                }
            }

            val err = response.errorBody()?.string() ?: ""
            Log.i("AuthRepository", "Login response info: code ${response.code()} body: $err")
            val message = when {
                err.contains("email_not_confirmed") -> "E-mail não confirmado no Supabase. Aguarde alguns instantes ou confirme seu e-mail."
                err.contains("invalid_credentials") || response.code() == 400 -> "E-mail ou senha incorretos."
                response.code() == 429 -> "Muitas tentativas em pouco tempo. Aguarde um instante e tente novamente."
                else -> "Erro ao entrar: (${response.code()})"
            }
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Log.i("AuthRepository", "Login notice: ${e.message}")
            Result.failure(Exception("Falha na conexão: ${e.localizedMessage ?: "Erro de rede"}"))
        }
    }

    suspend fun signup(email: String, password: String): Result<UserSession> = withContext(Dispatchers.IO) {
        try {
            val trimmedEmail = email.trim()

            // Com confirmação de e-mail desativada no Supabase, o signup retorna sessão imediatamente
            val signupResp = authService.signup(LoginRequest(trimmedEmail, password))

            if (signupResp.isSuccessful && signupResp.body() != null) {
                val body = signupResp.body()!!
                val user = body.user
                val token = body.accessToken

                // Se já retornou sessão, usar diretamente (confirmação desativada)
                if (user != null && token != null) {
                    val isAdmin = user.isAdmin
                    authPreferences.saveSession(
                        userId = user.id,
                        email = user.email ?: trimmedEmail,
                        accessToken = token,
                        refreshToken = body.refreshToken,
                        isAdmin = isAdmin
                    )
                    SupabaseClient.userAccessToken = token
                    val session = UserSession(user.id, user.email ?: trimmedEmail, token, body.refreshToken, isAdmin)
                    _currentUser.value = session
                    return@withContext Result.success(session)
                }

                // Fallback: tentar login logo após o signup
                return@withContext login(trimmedEmail, password)
            }

            val err = signupResp.errorBody()?.string() ?: ""
            Log.i("AuthRepository", "Signup code ${signupResp.code()}: $err")
            val message = when {
                signupResp.code() == 429 || err.contains("rate limit") ->
                    "Muitas tentativas. Aguarde alguns instantes e tente novamente."
                err.contains("already registered") || err.contains("already been registered") ->
                    "Este e-mail já está cadastrado. Faça login na aba 'Entrar'."
                err.contains("weak_password") ->
                    "A senha deve ter no mínimo 6 caracteres."
                err.contains("invalid_email") ->
                    "E-mail inválido. Verifique e tente novamente."
                else -> "Erro ao cadastrar: (${signupResp.code()})"
            }
            Result.failure(Exception(message))

        } catch (e: Exception) {
            Log.i("AuthRepository", "Signup notice: ${e.message}")
            Result.failure(Exception("Falha ao registrar: ${e.localizedMessage ?: "Erro de rede"}"))
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            authService.logout()
        } catch (e: Exception) {
            Log.d("AuthRepository", "Logout remote notice: ${e.message}")
        }
        SupabaseClient.userAccessToken = null
        authPreferences.clearSession()
        _currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val prefs = AuthPreferences.getInstance(context)
                val instance = AuthRepository(prefs)
                INSTANCE = instance
                instance
            }
        }
    }
}
