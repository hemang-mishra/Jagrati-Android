package com.hexagraph.jagrati_android.ui.viewmodels.auth

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.repository.auth.AuthRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that handles login-specific logic.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginUiState>() {

    private val _loginState = MutableStateFlow<AuthResult>(AuthResult.Initial)
    private val _googleSignInState = MutableStateFlow<AuthResult>(AuthResult.Initial)
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _isPasswordVisible = MutableStateFlow(false)

    override val uiState: StateFlow<LoginUiState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<LoginUiState> {
        return combine(
            _email,
            _password,
            _isPasswordVisible,
            _loginState,
            _googleSignInState,
            errorFlow,
            successMsgFlow
        ) { values ->
            // Access array elements by index instead of destructuring
            LoginUiState(
                email = values[0] as String,
                password = values[1] as String,
                isPasswordVisible = values[2] as Boolean,
                loginState = values[3] as AuthResult,
                googleSignInState = values[4] as AuthResult,
                error = values[5] as? ResponseError,
                successMsg = values[6] as? String
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LoginUiState()
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
     * Updates the password value.
     * @param password New password value
     */
    fun updatePassword(password: String) {
        _password.value = password
    }

    /**
     * Toggles password visibility.
     */
    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    /**
     * Signs in with email and password.
     */
    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            _loginState.value = AuthResult.Loading

            if (_email.value.isBlank()) {
                emitError(ResponseError.UNKNOWN.apply { actualResponse = "Email cannot be empty" })
                _loginState.value = AuthResult.Error("Email cannot be empty")
                return@launch
            }

            if (_password.value.isBlank()) {
                emitError(ResponseError.UNKNOWN.apply { actualResponse = "Password cannot be empty" })
                _loginState.value = AuthResult.Error("Password cannot be empty")
                return@launch
            }

            authRepository.signInWithEmailAndPassword(_email.value, _password.value)
                .collectLatest { result ->
                    _loginState.value = result
                    if (result is AuthResult.Error) {
                        emitError(ResponseError.UNKNOWN.apply { actualResponse = result.message })
                    } else if (result is AuthResult.Success) {
                        emitMsg("Login successful")
                    }
                }
        }
    }

    /**
     * Signs in with Google.
     * @param idToken Google ID token
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _googleSignInState.value = AuthResult.Loading

            authRepository.signInWithGoogle(idToken)
                .collectLatest { result ->
                    _googleSignInState.value = result
                    if (result is AuthResult.Error) {
                        emitError(ResponseError.UNKNOWN.apply { actualResponse = result.message })
                    } else if (result is AuthResult.Success) {
                        emitMsg("Google sign-in successful")
                    }
                }
        }
    }

    /**
     * Resets the login state.
     */
    fun resetLoginState() {
        _loginState.value = AuthResult.Initial
    }

    /**
     * Resets the Google sign-in state.
     */
    fun resetGoogleSignInState() {
        _googleSignInState.value = AuthResult.Initial
    }

    // Expose state flows as read-only
    val email: StateFlow<String> get() = _email
    val password: StateFlow<String> get() = _password
    val isPasswordVisible: StateFlow<Boolean> get() = _isPasswordVisible
    val loginState: StateFlow<AuthResult> get() = _loginState
    val googleSignInState: StateFlow<AuthResult> get() = _googleSignInState
}
