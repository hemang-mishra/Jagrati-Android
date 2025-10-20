package com.hexagraph.jagrati_android.ui.screens.group

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.group.GroupResponse
import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.NameDescriptionRequest
import com.hexagraph.jagrati_android.repository.auth.GroupRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroupManagementUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val groups: List<GroupResponse> = emptyList(),
    val error: ResponseError? = null,
    val successMessage: String? = null,
    val showAddDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val selectedGroup: GroupResponse? = null
)

class GroupManagementViewModel(
    private val groupRepository: GroupRepository,
    private val groupsDao: GroupsDao
) : BaseViewModel<GroupManagementUiState>() {

    private val _uiState = MutableStateFlow(GroupManagementUiState())
    override val uiState: StateFlow<GroupManagementUiState> = createUiStateFlow()

    init {
        loadGroups()
    }

    override fun createUiStateFlow(): StateFlow<GroupManagementUiState> {
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
            initialValue = GroupManagementUiState()
        )
    }

    fun loadGroups(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true) }
            }

            groupRepository.getAllActiveGroups().collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { response ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    groups = response.groups
                                )
                            }
                            saveGroupsToPreferences(response.groups)
                        }
                    }
                    resource.isFailed -> {
                        _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                        emitError(resource.error)
                    }
                    resource.isLoading -> {
                    }
                }
            }
        }
    }

    fun addGroup(groupName: String, groupDescription: String) {
        if (groupName.isBlank()) {
            emitError(ResponseError.BAD_REQUEST.apply { actualResponse = "Group name cannot be empty" })
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            groupRepository.addGroup(NameDescriptionRequest(groupName, groupDescription)).collect { resource ->
                when {
                    resource.isSuccess -> {
                        emitMsg("Group added successfully")
                        _uiState.update { it.copy(isLoading = false, showAddDialog = false) }
                        loadGroups()
                    }
                    resource.isFailed -> {
                        _uiState.update { it.copy(isLoading = false) }
                        emitError(resource.error)
                    }
                    resource.isLoading -> {
                    }
                }
            }
        }
    }

    fun deleteGroup(groupId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            groupRepository.removeGroup(LongRequest(groupId)).collect { resource ->
                when {
                    resource.isSuccess -> {
                        emitMsg("Group removed successfully")
                        _uiState.update { it.copy(isLoading = false, showDeleteDialog = false, selectedGroup = null) }
                        loadGroups()
                    }
                    resource.isFailed -> {
                        _uiState.update { it.copy(isLoading = false) }
                        emitError(resource.error)
                    }
                    resource.isLoading -> {
                    }
                }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showDeleteDialog(group: GroupResponse) {
        _uiState.update { it.copy(selectedGroup = group, showDeleteDialog = true) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, selectedGroup = null) }
    }

    private suspend fun saveGroupsToPreferences(groups: List<GroupResponse>) {
        try {
            groups.forEach { groupsDao.upsertGroup(Groups(
                id = it.id,
                name = it.data
            )) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

