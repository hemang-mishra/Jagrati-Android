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
 * ViewModel that handles sign-up-specific logic.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<SignUpUiState>() {

    private val _signUpState = MutableStateFlow<AuthResult>(AuthResult.Initial)
    private val _email = MutableStateFlow("")
    private val _firstName = MutableStateFlow("")
    private val _lastName = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _confirmPassword = MutableStateFlow("")
    private val _isPasswordVisible = MutableStateFlow(false)
    private val _isConfirmPasswordVisible = MutableStateFlow(false)

    override val uiState: StateFlow<SignUpUiState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<SignUpUiState> {
        return combine(
            _email,
            _firstName,
            _lastName,
            _password,
            _confirmPassword,
            _isPasswordVisible,
            _isConfirmPasswordVisible,
            _signUpState,
            errorFlow,
            successMsgFlow
        ) { values ->
            // Access array elements by index instead of destructuring
            SignUpUiState(
                email = values[0] as String,
                firstName = values[1] as String,
                lastName = values[2] as String,
                password = values[3] as String,
                confirmPassword = values[4] as String,
                isPasswordVisible = values[5] as Boolean,
                isConfirmPasswordVisible = values[6] as Boolean,
                signUpState = values[7] as AuthResult,
                error = values[8] as? ResponseError,
                successMsg = values[9] as? String
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SignUpUiState()
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
     * Updates the first name value.
     * @param firstName New first name value
     */
    fun updateFirstName(firstName: String) {
        _firstName.value = firstName
    }

    /**
     * Updates the last name value.
     * @param lastName New last name value
     */
    fun updateLastName(lastName: String) {
        _lastName.value = lastName
    }

    /**
     * Updates the password value.
     * @param password New password value
     */
    fun updatePassword(password: String) {
        _password.value = password
    }

    /**
     * Updates the confirm password value.
     * @param confirmPassword New confirm password value
     */
    fun updateConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    /**
     * Toggles password visibility.
     */
    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    /**
     * Toggles confirm password visibility.
     */
    fun toggleConfirmPasswordVisibility() {
        _isConfirmPasswordVisible.value = !_isConfirmPasswordVisible.value
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
     * Validates the sign-up details.
     * @return Error message if validation fails, null if validation passes
     */
    fun validateSignUpDetails(): String? {
        if (_firstName.value.isBlank()) {
            return "First name cannot be empty"
        }

        if (_lastName.value.isBlank()) {
            return "Last name cannot be empty"
        }

        if (_password.value.length < 6) {
            return "Password must be at least 6 characters"
        }

        if (_password.value != _confirmPassword.value) {
            return "Passwords do not match"
        }

        return null
    }

    /**
     * Creates a new user account with email and password.
     */
    fun createUserWithEmailAndPassword() {
        viewModelScope.launch {
            _signUpState.value = AuthResult.Loading

            val validationError = validateSignUpDetails()
            if (validationError != null) {
                emitError(ResponseError.UNKNOWN.apply { actualResponse = validationError })
                _signUpState.value = AuthResult.Error(validationError)
                return@launch
            }

            // Combine first and last name for the display name
            val displayName = "${_firstName.value} ${_lastName.value}".trim()

            authRepository.createUserWithEmailAndPassword(
                _email.value,
                _password.value,
                displayName
            ).collectLatest { result ->
                _signUpState.value = result
                if (result is AuthResult.Error) {
                    emitError(ResponseError.UNKNOWN.apply { actualResponse = result.message })
                } else if (result is AuthResult.Success) {
                    emitMsg("Account created successfully")
                }
            }
        }
    }

    /**
     * Resets the sign-up state.
     */
    fun resetSignUpState() {
        _signUpState.value = AuthResult.Initial
    }

    // Expose state flows as read-only
    val email: StateFlow<String> get() = _email
    val firstName: StateFlow<String> get() = _firstName
    val lastName: StateFlow<String> get() = _lastName
    val password: StateFlow<String> get() = _password
    val confirmPassword: StateFlow<String> get() = _confirmPassword
    val isPasswordVisible: StateFlow<Boolean> get() = _isPasswordVisible
    val isConfirmPasswordVisible: StateFlow<Boolean> get() = _isConfirmPasswordVisible
    val signUpState: StateFlow<AuthResult> get() = _signUpState
}
