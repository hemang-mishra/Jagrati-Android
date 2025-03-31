package com.hexagraph.jagrati_android.ui.screens.studentAttendance

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanMainScreen
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanUseCases

@Composable
fun StudentAttendanceScreen(
    viewModel: StudentAttendanceViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState
){
    OmniScanMainScreen(
        useCases = OmniScanUseCases.STUDENT_ATTENDANCE
    ) {

    }
}