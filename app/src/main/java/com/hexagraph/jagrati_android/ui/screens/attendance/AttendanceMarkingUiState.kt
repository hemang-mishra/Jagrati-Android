package com.hexagraph.jagrati_android.ui.screens.attendance

import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.ResponseError

data class AttendanceMarkingUiState(
    val isLoading: Boolean = false,
    val capturedImage: ProcessedImage? = null,
    val isCameraActive: Boolean = true,
    val recognizedFaces: List<RecognizedPerson> = emptyList(),
    val liveRecognizedFaces: List<RecognizedPerson> = emptyList(),
    val showBottomSheet: Boolean = false,
    val isMarkingAttendance: Boolean = false,
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val error: ResponseError? = null,
    val successMessage: String? = null
)

data class RecognizedPerson(
    val pid: String,
    val name: String,
    val isStudent: Boolean,
    val similarity: Float,
    val subtitle: String = "",
    val profileImageUrl: String? = null
)
