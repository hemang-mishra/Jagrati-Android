package com.hexagraph.jagrati_android.ui.screens.details_sync

import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.model.user.UserSummaryDTO

/**
 * Represents the UI state for UserDetails screen
 */
data class DetailsSyncUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val userDetails: UserSummaryDTO? = null,
    val roles: List<RoleSummaryResponse> = emptyList(),
    val error: ResponseError? = null,
    val successMessage: String? = null
)
