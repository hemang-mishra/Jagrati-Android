package com.hexagraph.jagrati_android.api

import android.util.Log
import com.hexagraph.jagrati_android.model.auth.RefreshRequest
import com.hexagraph.jagrati_android.util.AppPreferences
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Authentication provider for Ktor client.
 * Handles token refresh and authentication headers.
 */
class AuthProvider(
    private val appPreferences: AppPreferences,
    private val baseUrl: String
) {
    /**
     * Configure bearer authentication for a HttpClient
     * This should be used with HttpClient configuration block
     * Example usage:
     *
     * HttpClient {
     *     install(Auth) {
     *         configureBearerAuth()
     *     }
     * }
     */
    fun Auth.configureBearerAuth() {
        bearer {
            // Provide the tokens
            loadTokens {
                runBlocking {
                    val accessToken = appPreferences.accessToken.get()
                    val refreshToken = appPreferences.refreshToken.get()
                    Log.d(
                        "AuthProvider",
                        "Loaded tokens: accessToken=$accessToken, refreshToken=$refreshToken"
                    )
                    if (accessToken != null && refreshToken != null) {
                        BearerTokens(accessToken, refreshToken)
                    } else {
                        null
                    }
                }
            }

            // Handle 401 Unauthorized responses
            refreshTokens {
                val oldRefreshToken = runBlocking { appPreferences.refreshToken.get() } ?: ""
                Log.d("AuthProvider", "Token refresh triggered")

                try {
                    val tokenResponse = client.post("$baseUrl/api/auth/refresh") {
                        contentType(ContentType.Application.Json)
                        setBody(RefreshRequest(refreshToken = oldRefreshToken))
                    }.body<com.hexagraph.jagrati_android.model.auth.TokenPair>()

                    // Save the new tokens
                    runBlocking {
                        appPreferences.saveTokens(
                            tokenResponse.accessToken,
                            tokenResponse.refreshToken
                        )
                    }

                    // Return the new token pair
                    BearerTokens(
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken
                    )
                } catch (e: Exception) {
                    // If refresh fails, clear tokens and return null to force re-login
                    Log.e("AuthProvider", "Failed to refresh token", e)
                    runBlocking {
                        appPreferences.clearTokens()
                    }
                    null
                }
            }

            // Customize which requests should be authenticated
            sendWithoutRequest { request ->
                // Don't add auth headers for login/register endpoints
                val path = request.url.encodedPath
                if (
                    path.contains("/api/auth/login") ||
                    path.contains("/api/auth/register")
                ) {
                    false
                } else
                    true
            }
        }
    }
}
