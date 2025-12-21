package com.hexagraph.jagrati_android.ui.screens.details_sync

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.usecases.sync.DataSyncUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for user details screen that loads at app startup.
 * Fetches user permissions and other details and stores them in AppPreferences.
 */
class DetailsSyncViewModel(
    private val syncUseCase: DataSyncUseCase
) : BaseViewModel<DetailsSyncUiState>() {

    private val _uiState = MutableStateFlow(DetailsSyncUiState())
    override val uiState: StateFlow<DetailsSyncUiState> = createUiStateFlow()

    init {
        fetchUserDetails()
    }

    override fun createUiStateFlow(): StateFlow<DetailsSyncUiState> {
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
            initialValue = DetailsSyncUiState())
    }

    /**
     * Fetches user details including permissions from the server
     * and stores them in AppPreferences
     */
    fun fetchUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            //Clearing old error/message
            clearErrorFlow()
            clearMsgFlow()
            _uiState.update { it.copy(isLoading = true) }
            syncUseCase.fetchUserDetails(
                onSuccessfulFetch = { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            userDetails = data.userDetails,
                            roles = data.roles,
                            isVolunteer = data.isVolunteer
                        )
                    }
                    emitMsg("User details fetched successfully")
                },
                onError = { msg ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false
                        )
                    }
                    emitError(msg)
                }
            )
        }
    }
}
