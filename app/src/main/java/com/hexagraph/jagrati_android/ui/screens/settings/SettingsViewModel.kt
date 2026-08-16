package com.hexagraph.jagrati_android.ui.screens.settings

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.AppUpdateType
import com.hexagraph.jagrati_android.util.AppUpdateHelper
import com.hexagraph.jagrati_android.util.UpdateCheckResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    appVersion: String,
    private val updateHelper: AppUpdateHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(appVersion = appVersion))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var pendingUpdateInfo: AppUpdateInfo? = null

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isCheckingForUpdate = true, errorMessage = null, updateMessage = null)
            }

            when (val result = updateHelper.checkForUpdate()) {
                is UpdateCheckResult.Available -> {
                    pendingUpdateInfo = result.appUpdateInfo
                    _uiState.update {
                        it.copy(
                            isCheckingForUpdate = false,
                            pendingUpdateVersionCode = result.availableVersionCode
                        )
                    }
                }

                is UpdateCheckResult.InProgress -> {
                    pendingUpdateInfo = result.appUpdateInfo
                    _uiState.update {
                        it.copy(
                            isCheckingForUpdate = false,
                            updateMessage = "An update is already in progress."
                        )
                    }
                }

                UpdateCheckResult.Downloaded -> {
                    _uiState.update {
                        it.copy(
                            isCheckingForUpdate = false,
                            updateMessage = "An update is downloaded and ready to install."
                        )
                    }
                }

                UpdateCheckResult.NotAvailable -> {
                    _uiState.update {
                        it.copy(
                            isCheckingForUpdate = false,
                            updateMessage = "You're using the latest version."
                        )
                    }
                }

                is UpdateCheckResult.Failed -> {
                    _uiState.update {
                        it.copy(
                            isCheckingForUpdate = false,
                            errorMessage = "Unable to check for updates. Please try again later."
                        )
                    }
                }
            }
        }
    }

    fun confirmUpdate(updateLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        val info = pendingUpdateInfo
        _uiState.update { it.copy(pendingUpdateVersionCode = null) }

        if (info == null) {
            _uiState.update { it.copy(updateMessage = "Update is no longer available.") }
            return
        }

        val started = updateHelper.startUpdate(info, AppUpdateType.IMMEDIATE, updateLauncher)
        if (!started) {
            _uiState.update {
                it.copy(errorMessage = "Failed to start update. Please update from the Play Store.")
            }
        }
        pendingUpdateInfo = null
    }

    fun dismissUpdateDialog() {
        pendingUpdateInfo = null
        _uiState.update { it.copy(pendingUpdateVersionCode = null) }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(updateMessage = null, errorMessage = null)
        }
    }

    fun handleUpdateResult(success: Boolean) {
        _uiState.update {
            it.copy(
                updateMessage = if (success) {
                    "Update completed successfully!"
                } else {
                    "Update was cancelled or failed."
                }
            )
        }
    }
}
