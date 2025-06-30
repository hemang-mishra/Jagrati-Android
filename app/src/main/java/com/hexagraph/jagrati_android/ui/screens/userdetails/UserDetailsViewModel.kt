package com.hexagraph.jagrati_android.ui.screens.userdetails

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions
import com.hexagraph.jagrati_android.repository.user.UserRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for user details screen that loads at app startup.
 * Fetches user permissions and other details and stores them in AppPreferences.
 */
class UserDetailsViewModel(
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel<UserDetailsUiState>() {

    private val _uiState = MutableStateFlow(UserDetailsUiState())
    override val uiState: StateFlow<UserDetailsUiState> = createUiStateFlow()

    init {
        fetchUserDetails()
    }

    override fun createUiStateFlow(): StateFlow<UserDetailsUiState> {
        return combine(
            _uiState,
            errorFlow,
            successMsgFlow
        ) { state, error, successMsg ->
            state.copy(
                error = error,
                successMessage = successMsg
            )
        }.stateIn(scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserDetailsUiState())
    }

    /**
     * Fetches user details including permissions from the server
     * and stores them in AppPreferences
     */
    fun fetchUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.getCurrentUserPermissions().collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val data = result.data
                        if (data != null) {
                            processUserData(data)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    userDetails = data.userDetails,
                                    roles = data.roles
                                )
                            }
                            emitMsg("User details fetched successfully")
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = false
                                )
                            }
                            emitError(result.error ?: createGenericError("Failed to fetch user details"))
                        }
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = false
                            )
                        }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    /**
     * Creates a generic error with a message
     */
    private fun createGenericError(message: String) =
        com.hexagraph.jagrati_android.model.ResponseError.UNKNOWN.apply {
            actualResponse = message
        }

    /**
     * Process and store user details in AppPreferences
     */
    private suspend fun processUserData(data: UserDetailsWithRolesAndPermissions) {
        // Store user details in preferences
        appPreferences.saveUserDetails(data.userDetails)

        // Store user roles in preferences
        appPreferences.saveUserRoles(data.roles)

        // Map permission names to AllPermissions enum values
        val permissions = data.permissions.permissions.mapNotNull { permission ->
            try {
                // Try to find matching enum value by name
                AllPermissions.valueOf(permission.name)
                permission.name
            } catch (e: IllegalArgumentException) {
                // Skip permissions that don't match our enum
                null
            }
        }

        // Store permissions in AppPreferences
        appPreferences.saveUserPermissions(permissions)
    }
}
