package com.hexagraph.jagrati_android.ui.viewmodels.auth

import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.model.User

/**
 * Data class representing the UI state for the auth screen.
 *
 * @property currentUser Current authenticated user
 * @property isAuthenticated Whether the user is authenticated
 * @property emailVerificationState Current state of the email verification process
 * @property error Current error message
 * @property successMsg Current success message
 */
data class AuthUiState(
    val currentUser: User? = null,
    val isAuthenticated: Boolean = false,
    val emailVerificationState: AuthResult = AuthResult.Initial,
    val error: com.hexagraph.jagrati_android.model.ResponseError? = null,
    val successMsg: String? = null
)
