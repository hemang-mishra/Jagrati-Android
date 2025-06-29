package com.hexagraph.jagrati_android.model.auth

import kotlinx.serialization.Serializable

/**
 * Data classes for authentication responses from the Spring Boot backend.
 */

/**
 * Response for user registration.
 */
@Serializable
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
@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)

/**
 * Response for generic message responses.
 */
@Serializable
data class MessageResponse(
    val message: String
)

/**
 * Response for Google login URL.
 */
@Serializable
data class GoogleLoginUrlResponse(
    val url: String
)