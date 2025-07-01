package com.hexagraph.jagrati_android.ui.screens.permissions

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.permission.PermissionResponse
import com.hexagraph.jagrati_android.model.permission.PermissionWithRolesListResponse
import com.hexagraph.jagrati_android.model.permission.PermissionWithRolesResponse
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.repository.permission.PermissionRepository
import com.hexagraph.jagrati_android.repository.role.RoleRepository
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

data class ManagePermissionsUiState(
    val isLoading: Boolean = false,
    val permissions: List<PermissionWithRolesResponse> = emptyList(),
    val filteredPermissions: List<PermissionWithRolesResponse> = emptyList(),
    val searchQuery: String = "",
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class ManagePermissionsViewModel(
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository
) : BaseViewModel<ManagePermissionsUiState>() {

    private val _uiState = MutableStateFlow(ManagePermissionsUiState())
    override val uiState: StateFlow<ManagePermissionsUiState> = createUiStateFlow()

    init {
        loadPermissions()
    }

    override fun createUiStateFlow(): StateFlow<ManagePermissionsUiState> {
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
            initialValue = ManagePermissionsUiState()
        )
    }

    fun loadPermissions() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            permissionRepository.getAllPermissionsWithRoles().collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val permissions = result.data?.permissions ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                permissions = permissions,
                                filteredPermissions = applySearch(permissions, it.searchQuery)
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
        _uiState.update {
            val filteredPermissions = applySearch(_uiState.value.permissions, query)
            it.copy(
                searchQuery = query,
                filteredPermissions = filteredPermissions
            )
        }
    }

    private fun applySearch(permissions: List<PermissionWithRolesResponse>, query: String): List<PermissionWithRolesResponse> {
        if (query.isBlank()) return permissions

        return permissions.filter { permission ->
            permission.name.contains(query, ignoreCase = true) ||
            permission.module.contains(query, ignoreCase = true) ||
            permission.action.contains(query, ignoreCase = true) ||
            permission.description?.contains(query, ignoreCase = true) == true
        }
    }
}
