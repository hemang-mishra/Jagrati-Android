package com.hexagraph.jagrati_android.ui.screens.settings

data class SettingsUiState(
    val appVersion: String = "",
    val isCheckingForUpdate: Boolean = false,
    val pendingUpdateVersionCode: Int? = null,
    val updateMessage: String? = null,
    val errorMessage: String? = null
)
