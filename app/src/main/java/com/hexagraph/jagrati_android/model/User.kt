package com.hexagraph.jagrati_android.model

/**
 * Data class representing a user in the application.
 *
 * @property uid Unique identifier for the user
 * @property email Email address of the user
 * @property displayName Display name of the user
 * @property isEmailVerified Whether the user's email is verified
 * @property photoUrl URL to the user's profile photo
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val isEmailVerified: Boolean = false,
    val photoUrl: String = ""
)