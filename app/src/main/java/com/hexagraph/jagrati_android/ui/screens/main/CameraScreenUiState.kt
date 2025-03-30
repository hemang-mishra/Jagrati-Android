package com.hexagraph.jagrati_android.ui.screens.main

import androidx.camera.core.CameraSelector
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.ui.screens.omniscan.OmniScanUseCases

data class OmniScanUIState(
    val cameraScreenUiState: CameraScreenUiState = CameraScreenUiState(),
    val allFaces: List<FaceInfo> = emptyList(),
    val allProcessedFaces: List<ProcessedImage> = emptyList(),
)

data class CameraScreenUiState(
    val lensFacing : Int = CameraSelector.LENS_FACING_BACK,
    val currentImage: ProcessedImage = ProcessedImage(),
    val recognizedImage: ProcessedImage = ProcessedImage(),
    val useCases: OmniScanUseCases = OmniScanUseCases.RECOGNIZE_A_PERSON,
    val registerNewStudent: Boolean = false,
    val imageAboutToBeSaved: ProcessedImage = ProcessedImage(),
    val savedPeople: List<ProcessedImage> = emptyList()
)
