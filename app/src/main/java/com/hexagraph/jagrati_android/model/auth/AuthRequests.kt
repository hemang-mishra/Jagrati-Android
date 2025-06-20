package com.hexagraph.jagrati_android.model.auth

/**
 * Data classes for authentication requests to the Spring Boot backend.
 */

/**
 * Request for user login with email and password.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Request for refreshing authentication tokens.
 */
data class RefreshRequest(
    val refreshToken: String
)

/**
 * Request for user registration.
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

/**
 * Request for resending verification email.
 */
data class ResendVerificationRequest(
    val email: String
)

/**
 * Request for initiating password reset.
 */
data class ForgotPasswordRequest(
    val email: String
)

/**
 * Request for Google login.
 */
data class GoogleLoginRequest(
    val idToken: String
)