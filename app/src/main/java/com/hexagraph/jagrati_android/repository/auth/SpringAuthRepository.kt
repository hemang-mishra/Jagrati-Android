package com.hexagraph.jagrati_android.repository.auth

import android.content.SharedPreferences
import com.hexagraph.jagrati_android.api.AuthService
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.auth.ForgotPasswordRequest
import com.hexagraph.jagrati_android.model.auth.GoogleLoginRequest
import com.hexagraph.jagrati_android.model.auth.LoginRequest
import com.hexagraph.jagrati_android.model.auth.RefreshRequest
import com.hexagraph.jagrati_android.model.auth.RegisterRequest
import com.hexagraph.jagrati_android.model.auth.ResendVerificationRequest
import com.hexagraph.jagrati_android.util.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository using Spring Boot backend.
 */
@Singleton
class SpringAuthRepository @Inject constructor(
    private val authService: AuthService,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_DISPLAY_NAME = "user_display_name"
        private const val KEY_USER_EMAIL_VERIFIED = "user_email_verified"
        private const val KEY_USER_PHOTO_URL = "user_photo_url"
    }

    override fun getCurrentUser(): Flow<User?> = flow {
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        if (userId != null) {
            val user = User(
                uid = userId,
                email = sharedPreferences.getString(KEY_USER_EMAIL, "") ?: "",
                displayName = sharedPreferences.getString(KEY_USER_DISPLAY_NAME, "") ?: "",
                isEmailVerified = sharedPreferences.getBoolean(KEY_USER_EMAIL_VERIFIED, false),
                photoUrl = sharedPreferences.getString(KEY_USER_PHOTO_URL, "") ?: ""
            )
            emit(user)
        } else {
            emit(null)
        }
    }

    override fun isUserAuthenticated(): Boolean {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null) != null
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)

        val loginRequest = LoginRequest(email = email, password = password)
        val response = Utils.parseResponse { authService.login(loginRequest) }

        when {
            response.isSuccess && response.data != null -> {
                // Save tokens
                saveTokens(response.data.accessToken, response.data.refreshToken)

                // Create user object
                val user = User(
                    uid = "", // We don't have the user ID from the token response
                    email = email,
                    displayName = "", // We don't have the display name from the token response
                    isEmailVerified = true, // Assuming the user is verified if login succeeds
                    photoUrl = ""
                )

                // Save user info
                saveUserInfo(user)

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
        val response = Utils.parseResponse { authService.loginWithGoogle(googleLoginRequest) }

        when {
            response.isSuccess && response.data != null -> {
                // Save tokens
                saveTokens(response.data.accessToken, response.data.refreshToken)

                // Create user object - we'll need to get user details from the token or make another API call
                val user = User(
                    uid = "", // We don't have the user ID from the token response
                    email = "", // We don't have the email from the token response
                    displayName = "", // We don't have the display name from the token response
                    isEmailVerified = true, // Assuming the user is verified if Google login succeeds
                    photoUrl = ""
                )

                // Save user info
                saveUserInfo(user)

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

        val response = Utils.parseResponse { authService.register(registerRequest) }

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
        val response = Utils.parseResponse { authService.forgotPassword(forgotPasswordRequest) }

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

        val email = sharedPreferences.getString(KEY_USER_EMAIL, null)
        if (email != null) {
            val resendVerificationRequest = ResendVerificationRequest(email = email)
            val response = Utils.parseResponse { authService.resendVerification(resendVerificationRequest) }

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
        val response = Utils.parseResponse { authService.resendVerification(resendVerificationRequest) }

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
        // Clear tokens and user info from SharedPreferences
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_DISPLAY_NAME)
            .remove(KEY_USER_EMAIL_VERIFIED)
            .remove(KEY_USER_PHOTO_URL)
            .apply()
    }

    /**
     * Save authentication tokens to SharedPreferences.
     */
    private fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    /**
     * Save user information to SharedPreferences.
     */
    private fun saveUserInfo(user: User) {
        sharedPreferences.edit()
            .putString(KEY_USER_ID, user.uid)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_DISPLAY_NAME, user.displayName)
            .putBoolean(KEY_USER_EMAIL_VERIFIED, user.isEmailVerified)
            .putString(KEY_USER_PHOTO_URL, user.photoUrl)
            .apply()
    }
}
