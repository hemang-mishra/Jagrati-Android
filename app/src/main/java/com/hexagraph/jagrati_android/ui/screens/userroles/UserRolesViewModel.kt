package com.hexagraph.jagrati_android.ui.screens.userroles

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
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
        refresh()
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

    private fun applyLocalFilter(query: String, users: List<UserWithRolesResponse>): List<UserWithRolesResponse> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return users

        return users.filter { user ->
            val fullName = "${user.firstName} ${user.lastName}".trim().lowercase()
            val email = user.email.trim().lowercase()
            fullName.contains(q) || email.contains(q)
        }
    }

    /**
     * Pull-to-refresh / manual reload of the full list.
     * Search is applied locally on top of the fetched list.
     */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            // Always fetch the full list; search is local.
            userRepository.getAllUsers(search = null).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val users = result.data?.users.orEmpty()
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                users = users,
                                filteredUsers = applyLocalFilter(current.searchQuery, users)
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

    /**
     * Called from the search text field. No network requests.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredUsers = applyLocalFilter(query, current.users)
            )
        }
    }

    fun loadUsers() = refresh()
}
