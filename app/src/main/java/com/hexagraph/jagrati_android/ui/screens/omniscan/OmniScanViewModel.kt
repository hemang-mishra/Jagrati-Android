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
import com.hexagraph.jagrati_android.model.dao.StudentDetailsDao
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.service.FaceRecognitionService
import com.hexagraph.jagrati_android.ui.screens.main.AddManuallyUIState
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.ui.screens.main.CameraScreenUiState
import com.hexagraph.jagrati_android.ui.screens.main.OmniScanUIState
import com.hexagraph.jagrati_android.ui.screens.main.OmniScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class OmniScanViewModel @Inject constructor(
    private val omniScanRepository: OmniScanRepository,
    private val faceRecognitionService: FaceRecognitionService,
    private val studentDetailsDao: StudentDetailsDao
) : BaseViewModel<OmniScanUIState>() {
    private val allStudentDetails = studentDetailsDao.getAllStudentDetails()
    private val studentSearchQueryInput = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private val studentSearchQuery = studentSearchQueryInput.asStateFlow().debounce(500)
    private val semaphore = Semaphore(1)
    private val facesFlow = omniScanRepository.faces
    private val processedFacesFlow = MutableStateFlow(emptyList<ProcessedImage>())
    private val cameraScreenUiState = MutableStateFlow(CameraScreenUiState())
    private val addManuallyUIStateFlow = MutableStateFlow(AddManuallyUIState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            allStudentDetails.collectLatest {
                addManuallyUIStateFlow.emit(
                    addManuallyUIStateFlow.value.copy(
                        queriedData = it,
                        queryString = ""
                    )
                )
            }
            studentSearchQuery.collectLatest {
                addManuallyUIStateFlow.emit(
                    addManuallyUIStateFlow.value.copy(
                        queriedData = studentDetailsDao.getStudentDetailsByQuery(it)
                    )
                )
            }
        }
    }


    fun initializeOmniScan(
        context: Context,
        useCases: OmniScanUseCases,
        initialList: List<ProcessedImage>
    ) {
//        val currentList = uiState.value.cameraScreenUiState.savedPeople.toMutableList()
//        initialList.forEach { currentList.add(it) }
        viewModelScope.launch(Dispatchers.IO) {
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
                    useCases = useCases,
                    savedPeople = initialList
                )
            )
            facesFlow.collectLatest { faces ->
                for (face in faces) {
                    if (processedFacesFlow.value.find { it.pid == face.pid } == null) {
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
                val data = result.getOrNull() ?: return@runCatching
                viewModelScope.launch(Dispatchers.IO) {
                    if (semaphore.tryAcquire()) {
                        Log.i("Face Captured", "start")
                        var recognizedImage: ProcessedImage? = null
                        data.landmarks = data.face?.allLandmarks ?: listOf()
//                       delay(1000)
//                        data.spoof = faceRecognitionService.mobileNet(data, context).getOrNull()
                        recognizedImage = faceRecognitionService.recognizeFace(
                            data,
                            listOf(uiState.value.cameraScreenUiState.recognizedImage),
                            context
                        )
                        if (recognizedImage?.similarity == null || recognizedImage.matchesCriteria)
                            recognizedImage = null
                        if (recognizedImage == null) {
                            recognizedImage = faceRecognitionService.recognizeFace(
                                data,
                                processedFacesFlow.value,
                                context
                            )
                            if (recognizedImage?.similarity == null || recognizedImage?.similarity!! < 0.45f)
                                recognizedImage = null
                        }
                        cameraScreenUiState.value = cameraScreenUiState.value.copy(
                            currentImage = data,
                            recognizedImage = recognizedImage ?: ProcessedImage()
                        )
                        Log.i("Face Captured", "Face camptured ${data}")
                        semaphore.release()
                    } else {
                        Log.i("Face Captured", "semaphore not acquired")
                        cameraScreenUiState.value = cameraScreenUiState.value.copy(
                            currentImage = data
                        )
                    }
                }
            }.onFailure {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = it.message
                })
            }

        }
    }

    fun onSuccessfulRegister(studentDetails: StudentDetails) {
        val currentList = uiState.value.cameraScreenUiState.savedPeople.toMutableList()
        currentList.add(
            uiState.value.cameraScreenUiState.imageAboutToBeSaved.copy(
                pid = studentDetails.pid,
                name = studentDetails.firstName
            )
        )
        viewModelScope.launch {
            val result = omniScanRepository.saveFace(
                uiState.value.cameraScreenUiState.imageAboutToBeSaved.copy(
                    pid = studentDetails.pid,
                    name = studentDetails.firstName,
                )
            )
            if (result.isSuccess) {
                Log.i(
                    "ViewModel",
                    "Student details saved successfully $studentDetails in all databases"
                )
            } else {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = "Student details could not be $studentDetails in all databases"
                })
                Log.i("ViewModel", "Student details could not be $studentDetails in all databases")
            }
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
                    currentOmniScreens = OmniScreens.CAMERA_SCREEN,
                    savedPeople = currentList
                )
            )
        }
    }

    fun addFace() {
        if (uiState.value.cameraScreenUiState.currentImage.faceBitmap == null || uiState.value.cameraScreenUiState.recognizedImage.matchesCriteria) {

            return
        }
        viewModelScope.launch {
            //New person
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
//                    registerNewStudent = true,
                    currentOmniScreens = OmniScreens.REGISTER_SCREEN,
                    imageAboutToBeSaved = cameraScreenUiState.value.currentImage
                )
            )
        }

    }

    //Called only when currentface->faceBitmap is not null
    fun onClickOk() {
        viewModelScope.launch {
            if (uiState.value.cameraScreenUiState.useCases in listOf(
                    OmniScanUseCases.STUDENT_ATTENDANCE,
                    OmniScanUseCases.VOLUNTEER_ATTENDANCE
                )
            ) {
                if (uiState.value.cameraScreenUiState.recognizedImage.faceBitmap == null) {
                    //New person
                    cameraScreenUiState.emit(
                        cameraScreenUiState.value.copy(
                            currentOmniScreens = OmniScreens.REGISTER_SCREEN,
                            imageAboutToBeSaved = cameraScreenUiState.value.currentImage
                        )
                    )
                } else {
                    val currentList = cameraScreenUiState.value.savedPeople.toMutableList()
                    if (currentList.find { it.pid == cameraScreenUiState.value.recognizedImage.pid } != null) {
                        emitMsg("Already in list")
                        return@launch
                    }
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

    fun imageSearchQuery(query: String) {
        viewModelScope.launch {
            addManuallyUIStateFlow.emit(
                addManuallyUIStateFlow.value.copy(
                    queryString = query
                )
            )
            studentSearchQueryInput.emit(query)
        }
    }

    fun navigate(omniScreens: OmniScreens) {
        viewModelScope.launch {
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
                    currentOmniScreens = omniScreens
                )
            )
        }
    }

    fun selectFace(processedImage: ProcessedImage) {
        val selectedPeople = cameraScreenUiState.value.selectedPeople.toMutableList()
        if (selectedPeople.find { it.pid == processedImage.pid } == null) {
            selectedPeople.add(processedImage)
        } else {
            selectedPeople.remove(processedImage)
        }
        viewModelScope.launch {
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
                    selectedPeople = selectedPeople
                )
            )
        }
    }

    fun deleteSelectedFaces() {
        val savedPeople = cameraScreenUiState.value.savedPeople.toMutableList()
        val selectedPeople = cameraScreenUiState.value.selectedPeople
        savedPeople.removeIf { it.pid in selectedPeople.map { it.pid } }
        viewModelScope.launch {
            cameraScreenUiState.emit(
                cameraScreenUiState.value.copy(
                    savedPeople = savedPeople,
                    selectedPeople = emptyList()
                )
            )
        }
    }

    fun onSelectStudentInAddManuallyScreen(studentDetails: StudentDetails) {
        val processedImage = processedFacesFlow.value.find { it.pid == studentDetails.pid }
        val images = cameraScreenUiState.value.savedPeople.toMutableList()
        if (processedImage != null) {
            if (images.find { it.pid == processedImage.pid } == null)
                images.add(processedImage)
            viewModelScope.launch {
                cameraScreenUiState.emit(
                    cameraScreenUiState.value.copy(
                        savedPeople = images,
                        currentOmniScreens = OmniScreens.CAMERA_SCREEN
                    )
                )
            }
        }
    }

    override fun createUiStateFlow(): StateFlow<OmniScanUIState> {
        return combine(
            cameraScreenUiState,
            facesFlow,
            processedFacesFlow,
            addManuallyUIStateFlow,
            successMsgFlow
        ) { cameraUIState, faces, processedFaces, addManuallyUIState, msg ->
            OmniScanUIState(
                message = msg,
                cameraScreenUiState = cameraUIState,
                allFaces = faces,
                allProcessedFaces = processedFaces,
                addManuallyUIState = addManuallyUIState
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            OmniScanUIState()
        )
    }
}