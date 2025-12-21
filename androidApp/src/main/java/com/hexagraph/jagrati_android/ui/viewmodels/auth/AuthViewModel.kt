package com.hexagraph.jagrati_android.ui.viewmodels.auth

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.User
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
 * ViewModel that handles shared authentication logic.
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthUiState>() {

    private val _currentUser = MutableStateFlow<User?>(null)
    private val _isAuthenticated = MutableStateFlow(false)
    private val _emailVerificationState = MutableStateFlow<AuthResult>(AuthResult.Initial)

    override val uiState: StateFlow<AuthUiState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<AuthUiState> {
        return combine(
            _currentUser,
            _isAuthenticated,
            _emailVerificationState,
            errorFlow,
            successMsgFlow
        ) { values ->
            // Access array elements by index instead of destructuring
            AuthUiState(
                currentUser = values[0] as? User,
                isAuthenticated = values[1] as Boolean,
                emailVerificationState = values[2] as AuthResult,
                error = values[3] as? ResponseError,
                successMsg = values[4] as? String
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthUiState()
        )
    }

    init {
        checkAuthState()
    }

    /**
     * Checks the current authentication state.
     */
    private fun checkAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                _currentUser.value = user
                _isAuthenticated.value = user != null
            }
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    /**
     * Sends an email verification to the current user.
     */
    fun sendEmailVerification() {
        viewModelScope.launch {
            _emailVerificationState.value = AuthResult.Loading
            authRepository.sendEmailVerification().collectLatest { result ->
                _emailVerificationState.value = result
                if (result is AuthResult.Error) {
                    emitError(ResponseError.UNKNOWN.apply { actualResponse = result.message })
                } else if (result is AuthResult.Success) {
                    emitMsg("Verification email sent successfully")
                }
            }
        }
    }

    /**
     * Sends an email verification to the specified email address.
     * @param email Email address to send the verification link to
     */
    fun sendEmailVerification(email: String) {
        viewModelScope.launch {
            _emailVerificationState.value = AuthResult.Loading
            authRepository.sendEmailVerification(email).collectLatest { result ->
                _emailVerificationState.value = result
                if (result is AuthResult.Error) {
                    emitError(ResponseError.UNKNOWN.apply { actualResponse = result.message })
                } else if (result is AuthResult.Success) {
                    emitMsg("Verification email sent successfully")
                }
            }
        }
    }

    /**
     * Resets the email verification state.
     */
    fun resetEmailVerificationState() {
        _emailVerificationState.value = AuthResult.Initial
    }

    /**
     * Checks if the user is authenticated.
     * @return true if user is authenticated, false otherwise
     */
    fun isUserAuthenticated(): Boolean {
        return authRepository.isUserAuthenticated()
    }

    // Expose state flows as read-only
    val currentUser: StateFlow<User?> get() = _currentUser
    val isAuthenticated: StateFlow<Boolean> get() = _isAuthenticated
    val emailVerificationState: StateFlow<AuthResult> get() = _emailVerificationState
}
