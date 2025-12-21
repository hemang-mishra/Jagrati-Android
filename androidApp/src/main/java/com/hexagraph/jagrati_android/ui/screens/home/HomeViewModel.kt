package com.hexagraph.jagrati_android.ui.screens.home

import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel: BaseViewModel<HomeUIState>() {
    private val homeUIStateFlow = MutableStateFlow<HomeUIState>(HomeUIState())

    override val uiState: StateFlow<HomeUIState>
        get() = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<HomeUIState> {
        TODO("Not yet implemented")
    }
}