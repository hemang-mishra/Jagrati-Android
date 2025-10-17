package com.hexagraph.jagrati_android.ui.screens.facedata

import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.ResponseError

data class FaceDataRegisterUiState(
    val isLoading: Boolean = false,
    val pid: String = "",
    val capturedImage: ProcessedImage? = null,
    val isCameraActive: Boolean = false,
    val personName: String = "Unknown",
    val error: ResponseError? = null,
    val successMessage: String? = null
)