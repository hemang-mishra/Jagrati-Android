package com.hexagraph.jagrati_android.ui.viewmodels.auth

import com.hexagraph.jagrati_android.model.AuthResult

/**
 * Data class representing the UI state for the sign up screen.
 *
 * @property email Current email input
 * @property firstName Current first name input
 * @property lastName Current last name input
 * @property password Current password input
 * @property confirmPassword Current confirm password input
 * @property isPasswordVisible Whether the password is visible
 * @property isConfirmPasswordVisible Whether the confirm password is visible
 * @property signUpState Current state of the sign up process
 * @property error Current error message
 * @property successMsg Current success message
 */
data class SignUpUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val signUpState: AuthResult = AuthResult.Initial,
    val error: com.hexagraph.jagrati_android.model.ResponseError? = null,
    val successMsg: String? = null
)
