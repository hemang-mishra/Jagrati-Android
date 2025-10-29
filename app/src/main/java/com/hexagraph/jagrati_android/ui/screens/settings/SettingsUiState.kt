package com.hexagraph.jagrati_android.ui.screens.settings

data class SettingsUiState(
    val appVersion: String = "",
    val isCheckingForUpdate: Boolean = false,
    val updateAvailable: Boolean = false,
    val updateMessage: String? = null,
    val errorMessage: String? = null
)
