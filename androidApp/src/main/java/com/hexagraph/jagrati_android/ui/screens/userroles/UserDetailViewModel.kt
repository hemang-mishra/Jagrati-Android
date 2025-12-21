package com.hexagraph.jagrati_android.ui.screens.userroles

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.model.role.RoleResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesResponse
import com.hexagraph.jagrati_android.repository.role.RoleRepository
import com.hexagraph.jagrati_android.repository.user.UserRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserDetailUiState(
    val isLoading: Boolean = false,
    val userPid: String = "",
    val user: UserWithRolesResponse? = null,
    val availableRoles: List<RoleResponse> = emptyList(),
    val showRoleSelectionDialog: Boolean = false,
    val roleAssignmentLoading: Boolean = false,
    val canDeleteUser: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class UserDetailViewModel(
    private val userPid: String,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel<UserDetailUiState>() {

    private val _uiState = MutableStateFlow(UserDetailUiState(userPid = userPid))
    override val uiState: StateFlow<UserDetailUiState> = createUiStateFlow()

    init {
        loadUserDetails()
        checkDeletePermission()
    }

    private fun checkDeletePermission() {
        viewModelScope.launch(Dispatchers.IO) {
            appPreferences.hasPermission(AllPermissions.USER_DELETE).collect { hasPermission ->
                _uiState.update { it.copy(canDeleteUser = hasPermission) }
            }
        }
    }

    override fun createUiStateFlow(): StateFlow<UserDetailUiState> {
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
            initialValue = UserDetailUiState(userPid = userPid)
        )
    }

    fun loadUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            // Load user details
            userRepository.getUserByPid(userPid).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(
                                user = result.data,
                                error = null
                            )
                        }
                        // After loading user, load available roles for assignment
                        loadAvailableRoles()
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

    private fun loadAvailableRoles() {
        viewModelScope.launch(Dispatchers.IO) {
            roleRepository.getAllRoles().collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val allRoles = result.data?.roles?.filter { it.isActive } ?: emptyList()
                        val userRoleIds = _uiState.value.user?.roles?.map { it.id } ?: emptyList()
                        val availableRoles = allRoles.filter { role -> role.id !in userRoleIds }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                availableRoles = availableRoles
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

    fun assignRoleToUser(roleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(roleAssignmentLoading = true) }

            userRepository.assignRoleToUser(userPid, roleId).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(
                                roleAssignmentLoading = false,
                                showRoleSelectionDialog = false
                            )
                        }
                        emitMsg("Role assigned successfully to user")

                        // Reload user details
                        loadUserDetails()
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

    fun removeRoleFromUser(roleId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(roleAssignmentLoading = true) }

            userRepository.removeRoleFromUser(userPid, roleId).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(roleAssignmentLoading = false)
                        }
                        emitMsg("Role removed successfully from user")

                        // Reload user details
                        loadUserDetails()
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

    fun showDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteUser(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isDeleting = true) }

            userRepository.deleteUser(userPid).collect { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _uiState.update {
                            it.copy(
                                isDeleting = false,
                                showDeleteDialog = false
                            )
                        }
                        emitMsg(result.data ?: "User deleted successfully")
                        onSuccess()
                    }
                    Resource.Status.FAILED -> {
                        _uiState.update {
                            it.copy(
                                isDeleting = false,
                                showDeleteDialog = false
                            )
                        }
                        emitError(result.error)
                    }
                    Resource.Status.LOADING -> {
                        _uiState.update { it.copy(isDeleting = true) }
                    }
                }
            }
        }
    }
}
