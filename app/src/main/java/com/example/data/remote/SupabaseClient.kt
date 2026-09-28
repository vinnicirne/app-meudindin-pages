package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    val BASE_URL: String = try {
        BuildConfig.SUPABASE_URL.takeIf { it.isNotBlank() }
            ?: ""
    } catch (_: Exception) {
        ""
    }

    val PUBLISHABLE_KEY: String = try {
        BuildConfig.SUPABASE_PUBLISHABLE_KEY.takeIf { it.isNotBlank() }
            ?: ""
    } catch (_: Exception) {
        ""
    }

    val SECRET_KEY: String = try {
        BuildConfig.SUPABASE_SECRET_KEY.takeIf { it.isNotBlank() }
            ?: ""
    } catch (_: Exception) {
        ""
    }

    // Dynamic bearer token for authenticated user
    @Volatile
    var userAccessToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        // Only set apikey if not explicitly provided
        if (original.header("apikey") == null) {
            builder.header("apikey", PUBLISHABLE_KEY)
        }

        if (original.header("Content-Type") == null) {
            builder.header("Content-Type", "application/json")
        }

        if (original.header("Accept") == null) {
            builder.header("Accept", "application/json")
        }

        // Only set Authorization if not explicitly provided
        if (original.header("Authorization") == null) {
            val tokenToUse = userAccessToken ?: SECRET_KEY
            builder.header("Authorization", "Bearer $tokenToUse")
        }

        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(if (BASE_URL.endsWith("/")) BASE_URL else "$BASE_URL/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: SupabaseApiService by lazy {
        retrofit.create(SupabaseApiService::class.java)
    }

    val authService: SupabaseAuthService by lazy {
        retrofit.create(SupabaseAuthService::class.java)
    }
}
