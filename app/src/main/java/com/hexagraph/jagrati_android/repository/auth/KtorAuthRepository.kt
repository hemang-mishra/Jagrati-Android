package com.hexagraph.jagrati_android.repository.auth

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.hexagraph.jagrati_android.service.auth.KtorAuthService
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.auth.ForgotPasswordRequest
import com.hexagraph.jagrati_android.model.auth.GoogleLoginRequest
import com.hexagraph.jagrati_android.model.auth.LoginRequest
import com.hexagraph.jagrati_android.model.auth.RegisterRequest
import com.hexagraph.jagrati_android.model.auth.ResendVerificationRequest
import com.hexagraph.jagrati_android.model.databases.PrimaryDatabase
import com.hexagraph.jagrati_android.model.village.StringRequest
import com.hexagraph.jagrati_android.usecases.sync.DataSyncUseCase
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.FirbaseAuthJwtUtils
import com.hexagraph.jagrati_android.util.Utils
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Implementation of AuthRepository using Spring Boot backend with Ktor client.
 */
class KtorAuthRepository(
    private val authService: KtorAuthService,
    private val appPreferences: AppPreferences,
    private val client: HttpClient,
    private val database: PrimaryDatabase,
    private val dataSyncUseCase: DataSyncUseCase
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = appPreferences.userDetails.getFlow()

    override fun isUserAuthenticated(): Boolean = runCatching {
        runBlocking {
            appPreferences.isAuthenticated.get()
        }
    }.getOrDefault(false)

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<AuthResult> = flow {
        emit(AuthResult.Loading)
        val deviceToken = FirebaseMessaging.getInstance().token.await()
        val loginRequest = LoginRequest(email = email, password = password, deviceToken)
        val response = safeApiCall {
            authService.login(loginRequest)
        }

        when {
            response.isSuccess && response.data != null -> {
                // Save tokens
                appPreferences.saveTokens(response.data.accessToken, response.data.refreshToken)
                dataSyncUseCase.subscribeToSyncTopic()
                refreshTokens()
                // Create user object
                val user = User(
                    pid = "", // We don't have the user ID from the token response
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

        // Decode the ID token to extract user information
        val tokenPayload = FirbaseAuthJwtUtils.decodeJwtToken(idToken)
        Log.d("KtorAuthRepository", "Google ID Token payload: $tokenPayload")
        if(tokenPayload?.email?.let { Utils.isCollegeEmailId(it) } == false){
            emit(AuthResult.Error("Please use your college email ID to sign in."))
            return@flow
        }

        val deviceToken = FirebaseMessaging.getInstance().token.await()
        val googleLoginRequest = GoogleLoginRequest(idToken = idToken, deviceToken = deviceToken)
        val response = safeApiCall { authService.loginWithGoogle(googleLoginRequest) }

        when {
            response.isSuccess && response.data != null -> {
                // Save tokens
                appPreferences.saveTokens(response.data.accessToken, response.data.refreshToken)
                dataSyncUseCase.subscribeToSyncTopic()
                refreshTokens()
                // Create user object - we'll need to get user details from the token or make another API call
                val user = User(
                    pid = "", // We don't have the user ID from the token response
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

        val email = appPreferences.userDetails.get()?.email

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
        dataSyncUseCase.unsubscribeFromSyncTopic()
        withContext(Dispatchers.Default) {
            database.clearAll()
        }
        //Logout in background as we don't need to wait for response as it not compulsory currently
        CoroutineScope(Dispatchers.Default).launch {
            val deviceId = FirebaseMessaging.getInstance().token.await()
            val response = safeApiCall { authService.logout(StringRequest(deviceId)) }
            Log.d("KtorAuthRepository", "Logout response: $response")
        }
        appPreferences.clearAll()

        refreshTokens()
    }

    private fun refreshTokens(){
        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .first().clearToken()
    }
}
