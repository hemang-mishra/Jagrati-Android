package com.hexagraph.jagrati_android.api

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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit service interface for authentication API calls.
 */
interface AuthService {
    
    /**
     * Register a new user.
     * 
     * @param request Registration details
     * @return Response containing the registered user details
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    /**
     * Login with email and password.
     * 
     * @param request Login credentials
     * @return Response containing authentication tokens
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<TokenPair>
    
    /**
     * Refresh authentication tokens.
     * 
     * @param request Refresh token
     * @return Response containing new authentication tokens
     */
    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<TokenPair>
    
    /**
     * Resend verification email.
     * 
     * @param request Email to send verification to
     * @return Response containing status message
     */
    @POST("api/auth/resend-verification")
    suspend fun resendVerification(@Body request: ResendVerificationRequest): Response<MessageResponse>
    
    /**
     * Initiate password reset.
     * 
     * @param request Email to send password reset to
     * @return Response containing status message
     */
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>
    
    /**
     * Get Google login URL for web clients.
     * 
     * @return Response containing the Google login URL
     */
    @GET("api/auth/google-login-url")
    suspend fun getGoogleLoginUrl(): Response<GoogleLoginUrlResponse>
    
    /**
     * Login with Google ID token.
     * 
     * @param request Google ID token
     * @return Response containing authentication tokens
     */
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<TokenPair>
}