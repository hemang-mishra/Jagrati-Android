package com.hexagraph.jagrati_android.ui.screens.facedata

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.ImageKitResponse
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import com.hexagraph.jagrati_android.model.student.toUpdateStudentRequest
import com.hexagraph.jagrati_android.model.toUpdateVolunteerRequest
import com.hexagraph.jagrati_android.repository.auth.StudentRepository
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRepository
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionService
import com.hexagraph.jagrati_android.service.image_service.ImageKitService
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.Utils
import com.hexagraph.jagrati_android.util.Utils.bitmapToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executor


class FaceDataRegisterViewModel(
    private val context: Application,
    private val appPreferences: AppPreferences,
    private val omniScanRepository: OmniScanRepository,
    private val imageKitService: ImageKitService,
    private val studentDao: StudentDao,
    private val volunteerDao: VolunteerDao,
    private val studentRepository: StudentRepository,
    private val volunteerRepository: VolunteerRepository,
    private val pid: String
) : BaseViewModel<FaceDataRegisterUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private var student: Student? = null
    private var volunteer: Volunteer? = null
    private var acceptingCaptureFromCamera = true
    private val _pid = MutableStateFlow(pid)
    private val _capturedImage = MutableStateFlow<ProcessedImage?>(null)
    private val _isCameraActive = MutableStateFlow(true)
    private val _personName = MutableStateFlow("Unknown")

    override val uiState: StateFlow<FaceDataRegisterUiState> = createUiStateFlow()

    init {
        loadPersonDetails()
    }

    override fun createUiStateFlow(): StateFlow<FaceDataRegisterUiState> {
        return combine(
            combine(
                _isLoading,
                _pid,
                _capturedImage,
                _isCameraActive,
                _personName
            ) { isLoading, pid, capturedImage, isCameraActive, personName ->
                FaceDataRegisterUiState(
                    isLoading = isLoading,
                    pid = pid,
                    capturedImage = capturedImage,
                    isCameraActive = isCameraActive,
                    personName = personName,
                    error = null,
                    successMessage = null
                )
            },
            combine(
                errorFlow,
                successMsgFlow
            ) { error, successMessage ->
                Pair(error, successMessage)
            }
        ) { state, ( error, successMessage) ->
            state.copy(
                error = error,
                successMessage = successMessage
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FaceDataRegisterUiState(pid = pid)
        )
    }

    private fun loadPersonDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val volunteer = volunteerDao.getVolunteer(pid)
                Log.d("FaceDataRegisterVM", "loadPersonDetails: volunteer=$volunteer")
                if (volunteer != null) {
                    this@FaceDataRegisterViewModel.volunteer = volunteer
                    _personName.update { "${volunteer.firstName} ${volunteer.lastName}" }
                }


                val student = studentDao.getStudentDetailsByPid(pid)
                Log.d("FaceDataRegisterVM", "loadPersonDetails: student=$student")
                if (student != null) {
                    this@FaceDataRegisterViewModel.student = student
                    _personName.update { "${student.firstName} ${student.lastName}" }
                    return@launch
                }

            } catch (e: Exception) {
                _personName.update { "Unknown" }
                Log.d("FaceDataRegisterVM", "loadPersonDetails: error=${e.message}")
            }
        }
    }

    fun getImageAnalyzer(lensFacing: Int, paint: Paint, cameraExecutor: Executor): ImageAnalysis.Analyzer {
        return omniScanRepository.imageAnalyzer(lensFacing, paint, cameraExecutor) { result ->
            result.onSuccess { processedImage ->
                if(acceptingCaptureFromCamera)
                _capturedImage.update { processedImage.copy(pid = pid) }
            }.onFailure { error ->
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = error.message
                })
            }
        }
    }

    fun captureFace() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                val currentImage = _capturedImage.value
                if (currentImage == null) {
                    emitError(ResponseError.UNKNOWN.apply {
                        actualResponse = "No face detected"
                    })
                    return@launch
                }
                val imageWithPid = currentImage.copy(pid = pid, name = _personName.value)
                _capturedImage.update { imageWithPid }
                _isCameraActive.update { false }
            } catch (e: Exception) {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = e.message
                })
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun retakePhoto() {
        _capturedImage.update { null }
        _isCameraActive.update { true }
    }

    fun discardCapture() {
        _capturedImage.update { null }
        _isCameraActive.update { true }
    }

    fun processImageFromGallery(bitmap: Bitmap, paint: Paint, onSuccess: () -> Unit, onNoFaceDetected: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                val result = omniScanRepository.processImageFromBitmap(bitmap, paint)

                result.onSuccess { processedImage ->
                    if(processedImage.face == null) {
                        onNoFaceDetected()
                        return@onSuccess
                    }
                    val imageWithPid = processedImage.copy(pid = pid, name = _personName.value)
                    acceptingCaptureFromCamera = false
                    _capturedImage.update { imageWithPid }
                    _isCameraActive.update { false }
                    onSuccess()
                }.onFailure { error ->
                    emitError(ResponseError.UNKNOWN.apply {
                        actualResponse = error.message ?: "Failed to process image"
                    })
                }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun saveFaceData(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                val processedImage = _capturedImage.value
                if (processedImage == null) {
                    emitError(ResponseError.UNKNOWN.apply {
                        actualResponse = "No captured image"
                    })
                    return@launch
                }

                saveImagesOnImageKit(processedImage) { imageResponse ->
                    viewModelScope.launch(Dispatchers.IO) {
                        saveFaceDataOnServer(imageResponse) {
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = e.message
                })
                _isLoading.update { false }
            }
        }
    }

    private suspend fun saveFaceDataOnServer(imageResponse: ImageKitResponse, onSuccess: () -> Unit) {
        try {
            if(student != null){
                val updatedStudent = student!!.copy(
                    profilePic = imageResponse
                )
                val result = studentRepository.updateStudent(updatedStudent.toUpdateStudentRequest())
                result.collect { resource ->
                    if(resource.isSuccess){
                        studentDao.upsertStudentDetails(updatedStudent)
                        saveFaceLocallyAfterServer(_capturedImage.value!!, onSuccess)
                    } else if(resource.isFailed) {
                        val error = resource.error
                        emitError(ResponseError.UNKNOWN.apply {
                            actualResponse = error?.actualResponse ?: "Failed to save on server"
                        })
                        _isLoading.update { false }
                    }
                }
            }
            if(volunteer != null){
                val updatedVolunteer = volunteer!!.copy(
                    profilePic = imageResponse
                )
                val result = volunteerRepository.updateMyDetails(updatedVolunteer.toUpdateVolunteerRequest())
                result.collect { resource ->
                    if(resource.isSuccess){
                        volunteerDao.upsertVolunteer(updatedVolunteer)
                        saveFaceLocallyAfterServer(_capturedImage.value!!, onSuccess)
                    } else if(resource.isFailed) {
                        val error = resource.error
                        emitError(ResponseError.UNKNOWN.apply {
                            actualResponse = error?.actualResponse ?: "Failed to save on server"
                        })
                        _isLoading.update { false }
                    }
                }
            }
        } catch (e: Exception) {
            emitError(ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            })
            _isLoading.update { false }
        }
    }

    private suspend fun saveFaceLocallyAfterServer(processedImage: ProcessedImage, onSuccess: () -> Unit) {
        try {
            val result = omniScanRepository.saveFaceLocally(processedImage)
            result.onSuccess {
                emitMsg("Face data saved successfully")
                _isLoading.update { false }
                onSuccess()
            }.onFailure { error ->
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = error.message
                })
                _isLoading.update { false }
            }
        } catch (e: Exception) {
            emitError(ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            })
            _isLoading.update { false }
        }
    }

    private suspend fun saveImagesOnImageKit(
        processedImage: ProcessedImage,
        onSuccess: (imageResponse: ImageKitResponse) -> Unit
    ) {
        try {
            val imageResponse = processedImage.image?.let { bitmap ->
                // Compress bitmap to target size (50 KB) and get file directly
                val targetBytes = 25 * 1024L
                val imageFile = Utils.compressBitmapToFile(
                    context = context,
                    bitmap = bitmap,
                    fileName = processedImage.faceInfo.imageFileName,
                    targetBytes = targetBytes
                )

                if (imageFile == null) {
                    Log.e("FaceDataRegisterVM", "Failed to compress image to target size")
                    return@let null
                }

                Log.d("FaceDataRegisterVM", "Compressed image to ${imageFile.length()} bytes (target: $targetBytes)")
                imageKitService.uploadImageFromFile(context, imageFile)
            }

            if(imageResponse?.isSuccess == true) {
                onSuccess(
                    imageResponse.getOrNull() ?: ImageKitResponse()
                )
            } else {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = "Failed to upload images to ImageKit"
                })
                _isLoading.update { false }
            }
        } catch (e: Exception) {
            emitError(ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            })
            _isLoading.update { false }
        }
    }
}
