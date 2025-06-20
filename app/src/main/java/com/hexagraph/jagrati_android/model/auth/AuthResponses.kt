package com.hexagraph.jagrati_android.model.auth

/**
 * Data classes for authentication responses from the Spring Boot backend.
 */

/**
 * Response for user registration.
 */
data class RegisterResponse(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

/**
 * Response for token-based authentication.
 * Contains access token and refresh token.
 */
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

/**
 * Response for generic message responses.
 */
data class MessageResponse(
    val message: String
)

/**
 * Response for Google login URL.
 */
data class GoogleLoginUrlResponse(
    val url: String
)