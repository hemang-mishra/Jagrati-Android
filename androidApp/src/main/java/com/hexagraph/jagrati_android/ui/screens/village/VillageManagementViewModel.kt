package com.hexagraph.jagrati_android.ui.screens.village

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.model.dao.VillageDao
import com.hexagraph.jagrati_android.model.village.LongRequest
import com.hexagraph.jagrati_android.model.village.LongStringResponse
import com.hexagraph.jagrati_android.model.village.StringRequest
import com.hexagraph.jagrati_android.repository.auth.VillageRepository
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

data class VillageManagementUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val villages: List<LongStringResponse> = emptyList(),
    val error: ResponseError? = null,
    val successMessage: String? = null,
    val showAddDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val selectedVillage: LongStringResponse? = null
)

class VillageManagementViewModel(
    private val villageRepository: VillageRepository,
    private val villagesDao: VillageDao
) : BaseViewModel<VillageManagementUiState>() {

    private val _uiState = MutableStateFlow(VillageManagementUiState())
    override val uiState: StateFlow<VillageManagementUiState> = createUiStateFlow()

    init {
        loadVillages()
    }

    override fun createUiStateFlow(): StateFlow<VillageManagementUiState> {
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
            initialValue = VillageManagementUiState()
        )
    }

    fun loadVillages(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true) }
            }

            villageRepository.getAllActiveVillages().collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { response ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    villages = response.villages
                                )
                            }
                            saveVillagesToPreferences(response.villages)
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

    fun addVillage(villageName: String) {
        if (villageName.isBlank()) {
            emitError(ResponseError.BAD_REQUEST.apply { actualResponse = "Village name cannot be empty" })
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            villageRepository.addVillage(StringRequest(villageName)).collect { resource ->
                when {
                    resource.isSuccess -> {
                        emitMsg("Village added successfully")
                        _uiState.update { it.copy(isLoading = false, showAddDialog = false) }
                        loadVillages()
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

    fun deleteVillage(villageId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            villageRepository.removeVillage(LongRequest(villageId)).collect { resource ->
                when {
                    resource.isSuccess -> {
                        emitMsg("Village removed successfully")
                        _uiState.update { it.copy(isLoading = false, showDeleteDialog = false, selectedVillage = null) }
                        loadVillages()
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

    fun showDeleteDialog(village: LongStringResponse) {
        _uiState.update { it.copy(selectedVillage = village, showDeleteDialog = true) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, selectedVillage = null) }
    }

    private suspend fun saveVillagesToPreferences(villages: List<LongStringResponse>) {
        try {
            villages.forEach {
                villagesDao.upsertVillage(Village(
                    id = it.id,
                    name = it.data
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
