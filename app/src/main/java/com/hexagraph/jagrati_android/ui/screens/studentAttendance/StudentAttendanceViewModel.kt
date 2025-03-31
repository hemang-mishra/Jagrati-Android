package com.hexagraph.jagrati_android.ui.screens.studentAttendance

import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StudentAttendanceViewModel() : BaseViewModel<StudentAttendanceUIState>() {

    override val uiState: StateFlow<StudentAttendanceUIState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<StudentAttendanceUIState> = MutableStateFlow(
        StudentAttendanceUIState())

}