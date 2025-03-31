package com.hexagraph.jagrati_android.ui.screens.main

import androidx.camera.core.CameraSelector
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanUseCases

data class OmniScanUIState(
    val cameraScreenUiState: CameraScreenUiState = CameraScreenUiState(),
    val allFaces: List<FaceInfo> = emptyList(),
    val allProcessedFaces: List<ProcessedImage> = emptyList(),
    val addManuallyUIState: AddManuallyUIState = AddManuallyUIState(),
)

data class CameraScreenUiState(
    val lensFacing : Int = CameraSelector.LENS_FACING_BACK,
    val currentImage: ProcessedImage = ProcessedImage(),
    val recognizedImage: ProcessedImage = ProcessedImage(),
    val useCases: OmniScanUseCases = OmniScanUseCases.RECOGNIZE_A_PERSON,
    val imageAboutToBeSaved: ProcessedImage = ProcessedImage(),
    val savedPeople: List<ProcessedImage> = emptyList(),
    val selectedPeople  : List<ProcessedImage> = emptyList(),
    val currentOmniScreens: OmniScreens = OmniScreens.CAMERA_SCREEN
)

data class AddManuallyUIState(
    val queryString: String = "",
    val queriedData: List<StudentDetails> = emptyList(),
)



enum class OmniScreens{
    CAMERA_SCREEN,
    ADD_MANUALLY_SCREEN,
    CONFIRMATION_SCREEN,
    REGISTER_SCREEN
}
