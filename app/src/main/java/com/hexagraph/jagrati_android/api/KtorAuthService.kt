package com.hexagraph.jagrati_android.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.hexagraph.jagrati_android.model.auth.ForgotPasswordRequest
import com.hexagraph.jagrati_android.model.auth.GoogleLoginRequest
import com.hexagraph.jagrati_android.model.auth.GoogleLoginUrlResponse
import com.hexagraph.jagrati_android.model.auth.LoginRequest
import com.hexagraph.jagrati_android.model.auth.MessageResponse
import com.hexagraph.jagrati_android.model.auth.RefreshRequest
import com.hexagraph.jagrati_android.model.auth.RegisterRequest
import com.hexagraph.jagrati_android.model.auth.RegisterResponse
import com.hexagraph.jagrati_android.model.auth.ResendVerificationRequest
import com.hexagraph.jagrati_android.model.auth.TokenPair

/**
 * Ktor client implementation for authentication API calls.
 */
class KtorAuthService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    /**
     * Register a new user.
     *
     * @param request Registration details
     * @return Registered user details
     */
    suspend fun register(request: RegisterRequest): RegisterResponse {
        return client.post("$baseUrl/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Login with email and password.
     *
     * @param request Login credentials
     * @return Authentication tokens
     */
    suspend fun login(request: LoginRequest): TokenPair {
        return client.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Refresh authentication tokens.
     *
     * @param request Refresh token
     * @return New authentication tokens
     */
    suspend fun refresh(request: RefreshRequest): TokenPair {
        return client.post("$baseUrl/api/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Resend verification email.
     *
     * @param request Email to send verification to
     * @return Status message
     */
    suspend fun resendVerification(request: ResendVerificationRequest): MessageResponse {
        return client.post("$baseUrl/api/auth/resend-verification") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Initiate password reset.
     *
     * @param request Email to send password reset to
     * @return Status message
     */
    suspend fun forgotPassword(request: ForgotPasswordRequest): MessageResponse {
        return client.post("$baseUrl/api/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Get Google login URL for web clients.
     *
     * @return Google login URL
     */
    suspend fun getGoogleLoginUrl(): GoogleLoginUrlResponse {
        return client.get("$baseUrl/api/auth/google-login-url").body()
    }

    /**
     * Login with Google ID token.
     *
     * @param request Google ID token
     * @return Authentication tokens
     */
    suspend fun loginWithGoogle(request: GoogleLoginRequest): TokenPair {
        return client.post("$baseUrl/api/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
