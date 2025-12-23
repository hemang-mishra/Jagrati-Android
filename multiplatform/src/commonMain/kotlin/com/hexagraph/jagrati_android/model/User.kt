package com.hexagraph.jagrati_android.model

import com.hexagraph.jagrati_android.model.user.UserSummaryDTO

/**
 * Data class representing a user in the application.
 *
 * @property pid Unique identifier for the user
 * @property email Email address of the user
 * @property firstName Display first name of the user
 * @property lastName Display last name of the user
 * @property isEmailVerified Whether the user's email is verified
 * @property photoUrl URL to the user's profile photo
 */
data class User(
    val pid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isEmailVerified: Boolean = false,
    val photoUrl: String? = null
){
    fun toUserSummaryDTO(): UserSummaryDTO {
        return UserSummaryDTO(
            pid = pid,
            email = email,
            firstName = firstName,
            lastName = lastName,
            profileImageUrl = photoUrl
        )
    }
}