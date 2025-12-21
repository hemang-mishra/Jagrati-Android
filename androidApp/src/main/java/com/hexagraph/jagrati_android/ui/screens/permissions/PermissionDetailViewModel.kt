package com.hexagraph.jagrati_android.ui.screens.permissions

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.permission.PermissionResponse
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

data class PermissionDetailUiState(
    val isLoading: Boolean = false,
    val permissionId: Long = 0,
    val permission: PermissionResponse? = null,
    val assignedRoles: List<RoleSummaryResponse> = emptyList(),
    val availableRoles: List<RoleSummaryResponse> = emptyList(),
    val showRoleSelectionDialog: Boolean = false,
    val roleAssignmentLoading: Boolean = false,
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class PermissionDetailViewModel(
    private val permissionId: Long,
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository
) : BaseViewModel<PermissionDetailUiState>() {

    private val _uiState = MutableStateFlow(PermissionDetailUiState(permissionId = permissionId))
    override val uiState: StateFlow<PermissionDetailUiState> = createUiStateFlow()

    init {
        loadPermissionDetails()
    }

    override fun createUiStateFlow(): StateFlow<PermissionDetailUiState> {
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
            initialValue = PermissionDetailUiState(permissionId = permissionId)
        )
    }

    fun loadPermissionDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            // Load permission details
            permissionRepository.getPermissionById(permissionId).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(
                                permission = result.data
                            )
                        }
                        // After loading permission, load its roles
                        loadPermissionRoles()
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update { it.copy(isLoading = false) }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        // Loading state already set
                    }
                }
            }
        }
    }

    private fun loadPermissionRoles() {
        viewModelScope.launch(Dispatchers.IO) {
            permissionRepository.getRolesForPermission(permissionId).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val assignedRoles = result.data ?: emptyList()
                        _uiState.update {
                            it.copy(
                                assignedRoles = assignedRoles
                            )
                        }
                        // After loading assigned roles, load all roles to determine which ones can be assigned
                        loadAvailableRoles(assignedRoles)
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update { it.copy(isLoading = false) }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        // Loading state already set
                    }
                }
            }
        }
    }

    private fun loadAvailableRoles(assignedRoles: List<RoleSummaryResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            roleRepository.getAllRoles().collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val allRoles = result.data?.roles?.filter { it.isActive } ?: emptyList()
                        val assignedRoleIds = assignedRoles.map { it.id }
                        val availableRoles = allRoles.filter { role -> role.id !in assignedRoleIds }

                        _uiState.update { it ->
                            it.copy(
                                isLoading = false,
                                availableRoles = availableRoles.map { role->
                                    RoleSummaryResponse(
                                        id = role.id,
                                        name = role.name,
                                        description = role.description,
                                        isActive = role.isActive
                                    )
                                }
                            )
                        }
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update { it.copy(isLoading = false) }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        // Loading state already set
                    }
                }
            }
        }
    }

    fun showRoleSelectionDialog() {
        _uiState.update { it.copy(showRoleSelectionDialog = true) }
    }

    fun hideRoleSelectionDialog() {
        _uiState.update { it.copy(showRoleSelectionDialog = false) }
    }

    fun assignRoleToPermission(roleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(roleAssignmentLoading = true) }

            permissionRepository.assignRoleToPermission(permissionId, roleId).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(
                                roleAssignmentLoading = false,
                                showRoleSelectionDialog = false
                            )
                        }
                        emitMsg("Role assigned successfully to permission")

                        // Reload roles for this permission
                        loadPermissionRoles()
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update {
                            it.copy(
                                roleAssignmentLoading = false
                            )
                        }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        _uiState.update { it.copy(roleAssignmentLoading = true) }
                    }
                }
            }
        }
    }

    fun removeRoleFromPermission(roleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(roleAssignmentLoading = true) }

            permissionRepository.removeRoleFromPermission(permissionId, roleId).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(roleAssignmentLoading = false)
                        }
                        emitMsg("Role removed successfully from permission")

                        // Reload roles for this permission
                        loadPermissionRoles()
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update {
                            it.copy(
                                roleAssignmentLoading = false
                            )
                        }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        _uiState.update { it.copy(roleAssignmentLoading = true) }
                    }
                }
            }
        }
    }
}
