package com.hexagraph.jagrati_android.ui.viewmodels.auth

import com.hexagraph.jagrati_android.model.AuthResult

/**
 * Data class representing the UI state for the login screen.
 *
 * @property email Current email input
 * @property password Current password input
 * @property isPasswordVisible Whether the password is visible
 * @property loginState Current state of the login process
 * @property googleSignInState Current state of the Google sign-in process
 * @property error Current error message
 * @property successMsg Current success message
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val loginState: AuthResult = AuthResult.Initial,
    val googleSignInState: AuthResult = AuthResult.Initial,
    val error: com.hexagraph.jagrati_android.model.ResponseError? = null,
    val successMsg: String? = null
)
