package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.service.auth.KtorAuthService
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.auth.ForgotPasswordRequest
import com.hexagraph.jagrati_android.model.auth.GoogleLoginRequest
import com.hexagraph.jagrati_android.model.auth.LoginRequest
import com.hexagraph.jagrati_android.model.auth.RefreshRequest
import com.hexagraph.jagrati_android.model.auth.RegisterRequest
import com.hexagraph.jagrati_android.model.auth.ResendVerificationRequest
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * Implementation of AuthRepository using Spring Boot backend with Ktor client.
 */
class KtorAuthRepository(
    private val authService: KtorAuthService,
    private val appPreferences: AppPreferences
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = appPreferences.userDetails

    override fun isUserAuthenticated(): Boolean = runCatching {
        kotlinx.coroutines.runBlocking { appPreferences.isAuthenticated().first() }
    }.getOrDefault(false)

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)

        val loginRequest = LoginRequest(email = email, password = password)
        val response = safeApiCall {
            authService.login(loginRequest)
        }

        when {
            response.isSuccess && response.data != null -> {
                // Save tokens
                appPreferences.saveTokens(response.data.accessToken, response.data.refreshToken)

                // Create user object
                val user = User(
                    uid = "", // We don't have the user ID from the token response
                    email = email,
                    firstName = "", // We don't have the display name from the token response
                    lastName = "", // We don't have the last name from the token response
                    isEmailVerified = true, // Assuming the user is verified if login succeeds
                    photoUrl = ""
                )

                appPreferences.saveUserDetails(user.toUserSummaryDTO())
                emit(AuthResult.Success(user))
            }
            response.error?.actualResponse?.contains("verification", ignoreCase = true) == true -> {
                emit(AuthResult.VerificationNeeded(email))
            }
            else -> {
                emit(AuthResult.Error(response.error?.toast ?: "Authentication failed"))
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)

        val googleLoginRequest = GoogleLoginRequest(idToken = idToken)
        val response = safeApiCall { authService.loginWithGoogle(googleLoginRequest) }

        when {
            response.isSuccess && response.data != null -> {
                // Save tokens
                appPreferences.saveTokens(response.data.accessToken, response.data.refreshToken)

                // Create user object - we'll need to get user details from the token or make another API call
                val user = User(
                    uid = "", // We don't have the user ID from the token response
                    email = "", // We don't have the email from the token response
                    firstName = "", // We don't have the display name from the token response
                    lastName = "", // We don't have the last name from the token response
                    isEmailVerified = true, // Assuming the user is verified if Google login succeeds
                    photoUrl = ""
                )

                // Save user info
                appPreferences.saveUserDetails(user.toUserSummaryDTO())

                emit(AuthResult.Success(user))
            }
            else -> {
                emit(AuthResult.Error(response.error?.toast ?: "Google authentication failed"))
            }
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        displayName: String
    ): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)

        // Split display name into first and last name
        val names = displayName.split(" ", limit = 2)
        val firstName = names[0]
        val lastName = if (names.size > 1) names[1] else ""

        val registerRequest = RegisterRequest(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName
        )

        val response = safeApiCall { authService.register(registerRequest) }

        when {
            response.isSuccess && response.data != null -> {
                // Registration successful, but user needs to verify email
                emit(AuthResult.VerificationNeeded(email))
            }
            else -> {
                emit(AuthResult.Error(response.error?.toast ?: "User creation failed"))
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)

        val forgotPasswordRequest = ForgotPasswordRequest(email = email)
        val response = safeApiCall { authService.forgotPassword(forgotPasswordRequest) }

        when {
            response.isSuccess -> {
                emit(AuthResult.Success(User(email = email)))
            }
            else -> {
                emit(AuthResult.Error(response.error?.toast ?: "Failed to send password reset email"))
            }
        }
    }

    override suspend fun sendEmailVerification(): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)

        val email = appPreferences.userDetails.first()?.email

        if (email != null) {
            val resendVerificationRequest = ResendVerificationRequest(email = email)
            val response = safeApiCall { authService.resendVerification(resendVerificationRequest) }

            when {
                response.isSuccess -> {
                    emit(AuthResult.VerificationNeeded(email))
                }
                else -> {
                    emit(AuthResult.Error(response.error?.toast ?: "Failed to send email verification"))
                }
            }
        } else {
            emit(AuthResult.Error("No user is signed in"))
        }
    }

    override suspend fun sendEmailVerification(email: String): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)

        val resendVerificationRequest = ResendVerificationRequest(email = email)
        val response = safeApiCall { authService.resendVerification(resendVerificationRequest) }

        when {
            response.isSuccess -> {
                emit(AuthResult.VerificationNeeded(email))
            }
            else -> {
                emit(AuthResult.Error(response.error?.toast ?: "Failed to send email verification"))
            }
        }
    }

    override suspend fun signOut() {
        // Clear tokens and user info from DataStore
        appPreferences.clearAll()
    }
}
