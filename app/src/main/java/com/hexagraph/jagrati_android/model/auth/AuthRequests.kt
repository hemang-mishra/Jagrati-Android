package com.hexagraph.jagrati_android.model.auth

import kotlinx.serialization.Serializable

/**
 * Data classes for authentication requests to the Spring Boot backend.
 */

/**
 * Request for user login with email and password.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val deviceToken: String
)

/**
 * Request for refreshing authentication tokens.
 */
@Serializable
data class RefreshRequest(
    val refreshToken: String,
    val deviceToken: String
)

/**
 * Request for user registration.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

/**
 * Request for resending verification email.
 */
@Serializable
data class ResendVerificationRequest(
    val email: String
)

/**
 * Request for initiating password reset.
 */
@Serializable
data class ForgotPasswordRequest(
    val email: String
)

/**
 * Request for Google login.
 */
@Serializable
data class GoogleLoginRequest(
    val idToken: String,
    val deviceToken: String
)