package com.hexagraph.jagrati_android.ui.viewmodels.auth

import com.hexagraph.jagrati_android.model.AuthResult

/**
 * Data class representing the UI state for the forgot password screen.
 *
 * @property email Current email input
 * @property resetPasswordState Current state of the password reset process
 * @property error Current error message
 * @property successMsg Current success message
 */
data class ForgotPasswordUiState(
    val email: String = "",
    val resetPasswordState: AuthResult = AuthResult.Initial,
    val error: com.hexagraph.jagrati_android.model.ResponseError? = null,
    val successMsg: String? = null
)
