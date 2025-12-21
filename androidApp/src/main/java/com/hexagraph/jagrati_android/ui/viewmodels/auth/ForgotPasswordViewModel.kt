package com.hexagraph.jagrati_android.ui.viewmodels.auth

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel that handles forgot password logic.
 */

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<ForgotPasswordUiState>() {

    private val _resetPasswordState = MutableStateFlow<AuthResult>(AuthResult.Initial)
    private val _email = MutableStateFlow("")

    override val uiState: StateFlow<ForgotPasswordUiState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<ForgotPasswordUiState> {
        return combine(
            _email,
            _resetPasswordState,
            errorFlow,
            successMsgFlow
        ) { values ->
            // Access array elements by index instead of destructuring
            ForgotPasswordUiState(
                email = values[0] as String,
                resetPasswordState = values[1] as AuthResult,
                error = values[2] as? ResponseError,
                successMsg = values[3] as? String
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ForgotPasswordUiState()
        )
    }

    /**
     * Updates the email value.
     * @param email New email value
     */
    fun updateEmail(email: String) {
        _email.value = email
    }

    /**
     * Validates the email format.
     * @return true if email is valid, false otherwise
     */
    fun isEmailValid(): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return _email.value.matches(emailPattern.toRegex())
    }

    /**
     * Sends a password reset email.
     */
    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            _resetPasswordState.value = AuthResult.Loading

            if (_email.value.isBlank()) {
                emitError(ResponseError.UNKNOWN.apply { actualResponse = "Email cannot be empty" })
                _resetPasswordState.value = AuthResult.Error("Email cannot be empty")
                return@launch
            }

            if (!isEmailValid()) {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = "Please enter a valid email address"
                })
                _resetPasswordState.value = AuthResult.Error("Please enter a valid email address")
                return@launch
            }

            authRepository.sendPasswordResetEmail(_email.value)
                .collectLatest { result ->
                    _resetPasswordState.value = result
                    if (result is AuthResult.Error) {
                        emitError(ResponseError.UNKNOWN.apply { actualResponse = result.message })
                    } else if (result is AuthResult.Success) {
                        emitMsg("Password reset email sent successfully")
                    }
                }
        }
    }

    /**
     * Resets the password reset state.
     */
    fun resetPasswordResetState() {
        _resetPasswordState.value = AuthResult.Initial
    }

    val email: StateFlow<String> get() = _email
    val resetPasswordState: StateFlow<AuthResult> get() = _resetPasswordState
}
