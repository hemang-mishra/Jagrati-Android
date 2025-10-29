package com.hexagraph.jagrati_android.ui.screens.settings

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.install.model.UpdateAvailability
import com.hexagraph.jagrati_android.util.AppUpdateHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SettingsViewModel(
    appVersion: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(appVersion = appVersion))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var updateHelper: AppUpdateHelper? = null


    fun setUpdateHelper(helper: AppUpdateHelper) {
        updateHelper = helper
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            if (updateHelper == null) {
                _uiState.update {
                    it.copy(errorMessage = "Update helper not initialized")
                }
                return@launch
            }

            _uiState.update { it.copy(isCheckingForUpdate = true, errorMessage = null, updateMessage = null) }

            try {
                val updateAvailability = updateHelper!!.getUpdateAvailability()

                when (updateAvailability) {
                    UpdateAvailability.UPDATE_AVAILABLE -> {
                        val versionCode = updateHelper!!.getAvailableVersionCode()
                        _uiState.update {
                            it.copy(
                                isCheckingForUpdate = false,
                                updateAvailable = true,
                                updateMessage = if (versionCode != null) {
                                    "Update available! Version $versionCode is ready to download."
                                } else {
                                    "A new update is available on Google Play Store."
                                }
                            )
                        }
                    }
                    UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                        _uiState.update {
                            it.copy(
                                isCheckingForUpdate = false,
                                updateAvailable = false,
                                updateMessage = "You're using the latest version."
                            )
                        }
                    }
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                        _uiState.update {
                            it.copy(
                                isCheckingForUpdate = false,
                                updateMessage = "An update is already in progress."
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isCheckingForUpdate = false,
                                updateMessage = "Unable to check for updates at this time."
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCheckingForUpdate = false,
                        errorMessage = "Error checking for updates: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }


    fun startUpdate(updateLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch {
            if (updateHelper == null) {
                _uiState.update {
                    it.copy(errorMessage = "Update helper not initialized")
                }
                return@launch
            }

            try {
                val updateInfo = updateHelper!!.checkForUpdate()

                if (updateInfo != null) {
                    val success = updateHelper!!.startImmediateUpdate(updateInfo, updateLauncher)
                    if (!success) {
                        _uiState.update {
                            it.copy(
                                errorMessage = "Failed to start update. Please try again from Play Store."
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            updateMessage = "Update is no longer available or not allowed."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to start update: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun resumeUpdate(updateLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch {
            if (updateHelper == null) {
                _uiState.update {
                    it.copy(errorMessage = "Update helper not initialized")
                }
                return@launch
            }

            try {
                val resumed = updateHelper!!.resumeUpdate(updateLauncher)
                if (!resumed) {
                    _uiState.update {
                        it.copy(
                            updateMessage = "No update in progress to resume."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to resume update: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
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

