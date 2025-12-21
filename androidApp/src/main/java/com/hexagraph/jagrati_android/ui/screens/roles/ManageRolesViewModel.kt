package com.hexagraph.jagrati_android.ui.screens.roles

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.role.RoleResponse
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

data class ManageRolesUiState(
    val isLoading: Boolean = false,
    val roles: List<RoleResponse> = emptyList(),
    val isBottomSheetVisible: Boolean = false,
    val roleToEdit: RoleResponse? = null,
    val isEditMode: Boolean = false,
    val roleName: String = "",
    val roleDescription: String = "",
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class ManageRolesViewModel(
    private val roleRepository: RoleRepository
) : BaseViewModel<ManageRolesUiState>() {

    private val _uiState = MutableStateFlow(ManageRolesUiState())
    override val uiState: StateFlow<ManageRolesUiState> = createUiStateFlow()

    init {
        loadRoles()
    }

    override fun createUiStateFlow(): StateFlow<ManageRolesUiState> {
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
            initialValue = ManageRolesUiState()
        )
    }

    fun loadRoles() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            roleRepository.getAllRoles().collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val activeRoles = result.data?.roles?.filter { it.isActive } ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                roles = activeRoles
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

    fun createRole() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val name = _uiState.value.roleName.trim()
            val description = _uiState.value.roleDescription.trim().takeIf { it.isNotEmpty() }

            if (name.isEmpty()) {
                emitError(ResponseError.BAD_REQUEST.apply { actualResponse = "Role name cannot be empty" })
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            roleRepository.createRole(name, description).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isBottomSheetVisible = false,
                                roleName = "",
                                roleDescription = ""
                            )
                        }
                        emitMsg("Role created successfully")
                        loadRoles()
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

    fun updateRole() {
        viewModelScope.launch(Dispatchers.IO) {
            val roleToEdit = _uiState.value.roleToEdit ?: return@launch
            _uiState.update { it.copy(isLoading = true) }

            val name = _uiState.value.roleName.trim()
            val description = _uiState.value.roleDescription.trim().takeIf { it.isNotEmpty() }

            if (name.isEmpty()) {
                emitError(ResponseError.BAD_REQUEST.apply { actualResponse = "Role name cannot be empty" })
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            roleRepository.updateRole(roleToEdit.id, name, description).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isBottomSheetVisible = false,
                                roleName = "",
                                roleDescription = "",
                                roleToEdit = null,
                                isEditMode = false
                            )
                        }
                        emitMsg("Role updated successfully")
                        loadRoles()
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

    fun deactivateRole(role: RoleResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            roleRepository.deactivateRole(role.id).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(isLoading = false)
                        }
                        emitMsg("Role deactivated successfully")
                        loadRoles()
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

    // Bottom sheet functions
    fun showAddRoleBottomSheet() {
        _uiState.update {
            it.copy(
                isBottomSheetVisible = true,
                roleName = "",
                roleDescription = "",
                isEditMode = false,
                roleToEdit = null
            )
        }
    }

    fun showEditRoleBottomSheet(role: RoleResponse) {
        _uiState.update {
            it.copy(
                isBottomSheetVisible = true,
                roleName = role.name,
                roleDescription = role.description ?: "",
                isEditMode = true,
                roleToEdit = role
            )
        }
    }

    fun hideBottomSheet() {
        _uiState.update {
            it.copy(
                isBottomSheetVisible = false,
                roleName = "",
                roleDescription = "",
                isEditMode = false,
                roleToEdit = null
            )
        }
    }

    fun updateRoleName(name: String) {
        _uiState.update {
            it.copy(roleName = name)
        }
    }

    fun updateRoleDescription(description: String) {
        _uiState.update {
            it.copy(roleDescription = description)
        }
    }

    fun saveRole() {
        if (_uiState.value.isEditMode) {
            updateRole()
        } else {
            createRole()
        }
    }
}
