package com.hexagraph.jagrati_android.di

import android.content.Context
import com.hexagraph.jagrati_android.api.AuthProvider
import com.hexagraph.jagrati_android.api.KtorAuthService
import com.hexagraph.jagrati_android.util.AppPreferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private const val BASE_URL = "https://19f2-2409-40e3-26-46b5-67fd-2080-66cd-87ea.ngrok-free.app"
private const val TIMEOUT = 6000L

val networkModule = module {

    // Create AuthProvider
    single { AuthProvider(get(), BASE_URL) }

    // Create main HttpClient with auth
    single {
        val authProvider = get<AuthProvider>()

        HttpClient(Android) {
            // Install ContentNegotiation to handle JSON serialization
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // Configure timeout
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT
                connectTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }

            // Add logging
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        android.util.Log.d("KtorClient", message)
                    }
                }
                level = LogLevel.ALL
            }

            // Default request configuration
            defaultRequest {
                contentType(ContentType.Application.Json)
            }

            // Configure authentication with token refresh
            install(Auth) {
                with(authProvider) {
                    configureBearerAuth()
                }
            }
        }
    }

    // Provide KtorAuthService
    single { KtorAuthService(get(), BASE_URL) }
}