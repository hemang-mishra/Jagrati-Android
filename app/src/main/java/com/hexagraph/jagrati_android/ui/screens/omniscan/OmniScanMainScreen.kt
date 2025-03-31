package com.hexagraph.jagrati_android.ui.screens.omniscan

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexagraph.jagrati_android.ui.screens.addStudent.AddStudentScreen
import com.hexagraph.jagrati_android.ui.screens.main.OmniScreens

@Composable
fun OmniScanMainScreen(
    omniScanViewModel: OmniScanViewModel = hiltViewModel(),
    useCases: OmniScanUseCases,
    onExit: () -> Unit,
){
    val context = LocalContext.current
    val uiState by omniScanViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        omniScanViewModel.initializeOmniScan(context, useCases)
    }
    Box(modifier = Modifier
        .fillMaxSize()) {
        AnimatedContent(
            uiState.cameraScreenUiState.currentOmniScreens
        ) {screen->
            when (screen) {
                OmniScreens.CAMERA_SCREEN -> {
                    OmniScanCameraScreen(
                        omniScanViewModel,
                        onExit
                    )
                }
                OmniScreens.ADD_MANUALLY_SCREEN -> {
                    AddManuallyScreen(omniScanViewModel){
                        omniScanViewModel.navigate(OmniScreens.CAMERA_SCREEN)
                    }
                }
                OmniScreens.CONFIRMATION_SCREEN -> {
                    OmniScanConfirmationScreen(omniScanViewModel) {
                        //Callback
                    }
                }
                OmniScreens.REGISTER_SCREEN -> {
                    AddStudentScreen(
                        onPressBack = {
                            omniScanViewModel.navigate(OmniScreens.CAMERA_SCREEN)
                        },
                        isFacialDataAvailable = true,
                        onSuccessAddition = {
                        omniScanViewModel.onSuccessfulRegister(it)
                    })
                }
            }
        }

    }
}