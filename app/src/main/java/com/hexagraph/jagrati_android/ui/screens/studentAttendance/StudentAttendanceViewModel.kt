package com.hexagraph.jagrati_android.ui.screens.studentAttendance

import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import kotlinx.coroutines.flow.StateFlow

class StudentAttendanceViewModel(override val uiState: StateFlow<StudentAttendanceUIState>) : BaseViewModel<StudentAttendanceUIState>() {
    override fun createUiStateFlow(): StateFlow<StudentAttendanceUIState> {
        TODO("Not yet implemented")
    }

}