package com.hexagraph.jagrati_android.ui.screens.userroles

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.user.UserWithRolesListResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesResponse
import com.hexagraph.jagrati_android.repository.user.UserRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserRolesUiState(
    val isLoading: Boolean = false,
    val users: List<UserWithRolesResponse> = emptyList(),
    val filteredUsers: List<UserWithRolesResponse> = emptyList(),
    val searchQuery: String = "",
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class UserRolesViewModel(
    private val userRepository: UserRepository
) : BaseViewModel<UserRolesUiState>() {

    private val _uiState = MutableStateFlow(UserRolesUiState())
    override val uiState: StateFlow<UserRolesUiState> = createUiStateFlow()

    init {
        loadUsers()
    }

    override fun createUiStateFlow(): StateFlow<UserRolesUiState> {
        return combine(
            _uiState,
            errorFlow,
            successMsgFlow
        ) { state, error, successMsg ->
            state.copy(
                error = error,
                successMessage = successMsg
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserRolesUiState()
        )
    }

    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            userRepository.getAllUsers(_uiState.value.searchQuery.ifEmpty { null }).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val users = result.data?.users ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                users = users,
                                filteredUsers = users
                            )
                        }
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update { it.copy(isLoading = false) }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // If search query is updated, reload users with the search filter
        // This utilizes the server-side search capability
        loadUsers()
    }
}
