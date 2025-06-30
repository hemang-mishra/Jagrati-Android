package com.hexagraph.jagrati_android.ui.screens.management

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ManagementScreen
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ManagementUiState(
    val isLoading: Boolean = true,
    val sections: Map<String, List<ManagementScreenPermissionState>> = emptyMap(),
    val error: ResponseError? = null,
    val successMessage: String? = null
)

data class ManagementScreenPermissionState(
    val screen: ManagementScreen,
    val hasAllPermissions: Boolean
)

class ManagementViewModel(
    private val appPreferences: AppPreferences
) : BaseViewModel<ManagementUiState>() {

    private val _uiState = MutableStateFlow(ManagementUiState())
    override val uiState: StateFlow<ManagementUiState> = createUiStateFlow()

    init {
        loadManagementScreens()
    }

    override fun createUiStateFlow(): StateFlow<ManagementUiState> {
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
            initialValue = ManagementUiState()
        )
    }

    /**
     * Loads all management screens and checks if the user has permissions for each.
     */
    fun loadManagementScreens() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val managementScreenPermissions = mutableListOf<ManagementScreenPermissionState>()

            // Check permissions for each management screen
            for (screen in ManagementScreen.values()) {
                val hasAllRequiredPermissions = checkPermissionsForScreen(screen)
                managementScreenPermissions.add(
                    ManagementScreenPermissionState(
                        screen = screen,
                        hasAllPermissions = hasAllRequiredPermissions
                    )
                )
            }

            // Group screens by section
            val groupedSections = managementScreenPermissions
                .groupBy { it.screen.section }
                .filterValues { screenPermissions ->
                    // Only include sections with at least one accessible screen
                    screenPermissions.any { it.hasAllPermissions }
                }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                sections = groupedSections
            )
        }
    }

    /**
     * Checks if the user has all the permissions required for a specific screen.
     *
     * @param screen The management screen to check permissions for
     * @return true if user has all required permissions, false otherwise
     */
    private suspend fun checkPermissionsForScreen(screen: ManagementScreen): Boolean {
        // If no permissions required, always return true
        if (screen.permissionsRequired.isEmpty()) {
            return true
        }

        for (permission in screen.permissionsRequired) {
            val hasPermission = appPreferences.hasPermission(permission).first()
            if (!hasPermission) {
                return false
            }
        }

        return true
    }
}
