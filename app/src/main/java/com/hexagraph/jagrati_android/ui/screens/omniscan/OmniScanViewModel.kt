package com.hexagraph.jagrati_android.ui.screens.omniscan

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.service.FaceRecognitionService
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.ui.screens.main.CameraScreenUiState
import com.hexagraph.jagrati_android.ui.screens.main.OmniScanUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class OmniScanViewModel @Inject constructor(
    private val omniScanRepository: OmniScanRepository,
    private val faceRecognitionService: FaceRecognitionService
) : BaseViewModel<OmniScanUIState>() {
    private val facesFlow = omniScanRepository.faces
    private val processedFacesFlow = MutableStateFlow(emptyList<ProcessedImage>())
    private val cameraScreenUiState = MutableStateFlow(CameraScreenUiState())

    fun initializeOmniScan(context: Context, useCases: OmniScanUseCases) {
        viewModelScope.launch(Dispatchers.IO) {
            cameraScreenUiState.emit(cameraScreenUiState.value.copy(useCases = useCases))
            facesFlow.collectLatest { faces->
                for(face in faces) {
                    if(processedFacesFlow.value.find { it.pid == face.pid } == null) {
                        val list = processedFacesFlow.value.toMutableList()
                        list.add(face.processedImage(context))
                        processedFacesFlow.emit(list)
                    }
                }
            }
        }
    }

    fun getImageAnalyzer(context: Context, executor: Executor): ImageAnalysis.Analyzer {
        return omniScanRepository.imageAnalyzer(
            cameraScreenUiState.value.lensFacing,
            Paint().apply {
                strokeWidth = 1f
                color = Color.CYAN
            },
            executor
        ) { result ->
            runCatching {
                var recognizedImage: ProcessedImage? = null
                val data = result.getOrNull() ?: return@runCatching
                data.landmarks = data.face?.allLandmarks ?: listOf()
                viewModelScope.launch(Dispatchers.IO) {
                    val c1 = async {data.spoof = faceRecognitionService.mobileNet(data, context).getOrNull()}
                    val c2 = async {
                        recognizedImage = faceRecognitionService.recognizeFace(
                            data,
                            processedFacesFlow.value,
                            context
                        )
                        if(recognizedImage?.similarity == null || recognizedImage?.similarity!! < 0.45f)
                            recognizedImage = null
                    }
                    c1.await()
                    c2.await()
                    cameraScreenUiState.value = cameraScreenUiState.value.copy(currentImage = data, recognizedImage = recognizedImage?: ProcessedImage())
                }
                Log.i("Face Captured", "Face camptured ${data}")
            }.onFailure {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = it.message
                })
            }

        }
    }

    fun onSuccessfulRegister(studentDetails: StudentDetails){
        viewModelScope.launch {
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
                    registerNewStudent = false
                )
            )
            omniScanRepository.saveFace(uiState.value.cameraScreenUiState.imageAboutToBeSaved.copy(
                pid = studentDetails.pid,
                name = studentDetails.firstName
            ))
        }
    }

    //Called only when currentface->faceBitmap is not null
    fun onDone() {
        viewModelScope.launch {
            if(uiState.value.cameraScreenUiState.useCases in listOf(OmniScanUseCases.STUDENT_ATTENDANCE,
                    OmniScanUseCases.VOLUNTEER_ATTENDANCE)){
                if(uiState.value.cameraScreenUiState.recognizedImage.faceBitmap == null){
                    //New person
                    cameraScreenUiState.emit(
                        cameraScreenUiState.value.copy(
                            registerNewStudent = true,
                            imageAboutToBeSaved = cameraScreenUiState.value.currentImage
                        )
                    )
                }else{
                    val currentList = cameraScreenUiState.value.savedPeople.toMutableList()
                    currentList.add(cameraScreenUiState.value.recognizedImage)
                    cameraScreenUiState.emit(
                        cameraScreenUiState.value.copy(
                            savedPeople = currentList,
                        )
                    )
                }
            }
        }
    }

    fun flipCamera(onFlip: (CameraSelector) -> Unit) {
        val newLens: CameraSelector =
            if (cameraScreenUiState.value.lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
        onFlip(newLens)
        viewModelScope.launch {
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
                    lensFacing = if (cameraScreenUiState.value.lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                )
            )
        }
    }

    override val uiState: StateFlow<OmniScanUIState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<OmniScanUIState> {
        return combine(cameraScreenUiState, facesFlow, processedFacesFlow) { cameraUIState, faces, processedFaces ->
            OmniScanUIState(
                cameraScreenUiState = cameraUIState,
                allFaces = faces,
                allProcessedFaces = processedFaces
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            OmniScanUIState()
        )
    }

}