package com.hexagraph.jagrati_android.ui.screens.attendance

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceRequest
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VillageDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionService
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

class AttendanceMarkingViewModel(
    private val context: Application,
    private val omniScanRepository: OmniScanRepository,
    private val faceRecognitionService: FaceRecognitionService,
    private val faceInfoDao: FaceInfoDao,
    private val studentDao: StudentDao,
    private val volunteerDao: VolunteerDao,
    private val villageDao: VillageDao,
    private val groupsDao: GroupsDao,
    private val attendanceRepository: AttendanceRepository
) : BaseViewModel<AttendanceMarkingUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _capturedImage = MutableStateFlow<ProcessedImage?>(null)
    private val _isCameraActive = MutableStateFlow(true)
    private val _recognizedFaces = MutableStateFlow<List<RecognizedPerson>>(emptyList())
    private val _liveRecognizedFaces = MutableStateFlow<List<RecognizedPerson>>(emptyList())
    private val _showBottomSheet = MutableStateFlow(false)
    private val _isMarkingAttendance = MutableStateFlow(false)
    private var acceptingCaptureFromCamera = true

    private val semaphore = Semaphore(1)

    override val uiState: StateFlow<AttendanceMarkingUiState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<AttendanceMarkingUiState> {
        return combine(
            combine(
                _isLoading,
                _capturedImage,
                _isCameraActive,
                _recognizedFaces,
                _liveRecognizedFaces
            ) { isLoading, capturedImage, isCameraActive, recognizedFaces, liveRecognizedFaces ->
                AttendanceMarkingUiState(
                    isLoading = isLoading,
                    capturedImage = capturedImage,
                    isCameraActive = isCameraActive,
                    recognizedFaces = recognizedFaces,
                    liveRecognizedFaces = liveRecognizedFaces,
                    showBottomSheet = false,
                    isMarkingAttendance = false,
                    error = null,
                    successMessage = null
                )
            },
            combine(
                _showBottomSheet,
                _isMarkingAttendance,
                errorFlow,
                successMsgFlow
            ) { showBottomSheet, isMarkingAttendance, error, successMessage ->
                listOf(showBottomSheet, isMarkingAttendance, error, successMessage)
            }
        ) { state, extras ->
            state.copy(
                showBottomSheet = extras[0] as Boolean,
                isMarkingAttendance = extras[1] as Boolean,
                error = extras[2] as? ResponseError,
                successMessage = extras[3] as? String
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceMarkingUiState()
        )
    }

    fun getImageAnalyzer(lensFacing: Int, paint: Paint, executor: Executor): ImageAnalysis.Analyzer {
        return omniScanRepository.imageAnalyzer(
            lensFacing = lensFacing,
            paint = paint,
            cameraExecutor = executor,
            onFaceInfo = { result ->
                result.onSuccess {
                    processedImage ->
                    viewModelScope.launch(Dispatchers.IO) {
                        val acquired = semaphore.tryAcquire()
                        if (!acquired) return@launch

                        try {
                            if (acceptingCaptureFromCamera && !_showBottomSheet.value) {
                                recognizeFacesLive(processedImage)
                            }
                        } catch (e: Exception) {
                            Log.e("AttendanceMarkingViewModel", "Live recognition failed: ${e.message}")
                        } finally {
                            semaphore.release()
                        }
                    }


                }.onFailure {
                    Log.e("AttendanceMarkingViewModel", "Face detection failed: ${it.message}")
                }
            }
        )
    }

    private suspend fun recognizeFacesLive(processedImage: ProcessedImage) {
            try {
                val facePids = faceInfoDao.facePIDsList()
                if (facePids.isEmpty() || processedImage.faceBitmap == null) {
                    _liveRecognizedFaces.update { emptyList() }
                    return
                }

                val results = faceRecognitionService.recognizeFace(
                    processedImage,
                    facePids,
                    context
                )

                Log.d("AttendanceMarkingViewModel", "Live recognition results: $results")

                if (results != null && results.isNotEmpty()) {
                    val topMatches = results.filter { it.matchesCriteria }.take(3)
                    val recognizedPersons = topMatches.mapNotNull { result ->
                        getPersonDetails(result.pid, result.similarity)
                    }
                    _liveRecognizedFaces.update { recognizedPersons }
                } else {
                    _liveRecognizedFaces.update { emptyList() }
                }
            } catch (e: Exception) {
                Log.e("AttendanceMarkingViewModel", "Face recognition failed: ${e.message}")
                _liveRecognizedFaces.update { emptyList() }
        }
    }

    fun captureFace() {
        acceptingCaptureFromCamera = false
        _isCameraActive.update { false }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.update { true }

                val capturedImage = _capturedImage.value
                if (capturedImage?.faceBitmap == null) {
                    emitError(ResponseError.BAD_REQUEST.apply {
                        actualResponse = "No face detected. Please try again."
                    })
                    retakePhoto()
                    return@launch
                }

                val facePids = faceInfoDao.facePIDsList()
                if (facePids.isEmpty()) {
                    emitError(ResponseError.BAD_REQUEST.apply {
                        actualResponse = "No registered faces found in the database."
                    })
                    retakePhoto()
                    return@launch
                }

                val results = faceRecognitionService.recognizeFace(
                    capturedImage,
                    facePids,
                    context
                )

                if (results.isNullOrEmpty()) {
                    emitError(ResponseError.BAD_REQUEST.apply {
                        actualResponse = "No matching faces found."
                    })
                    retakePhoto()
                    return@launch
                }

                val matchingResults = results.filter { it.matchesCriteria }
                if (matchingResults.isEmpty()) {
                    emitError(ResponseError.BAD_REQUEST.apply {
                        actualResponse = "No faces matched the recognition threshold."
                    })
                    retakePhoto()
                    return@launch
                }

                val recognizedPersons = matchingResults.mapNotNull { result ->
                    getPersonDetails(result.pid, result.similarity)
                }

                _recognizedFaces.update { recognizedPersons }
                _showBottomSheet.update { true }

            } catch (e: Exception) {
                Log.e("AttendanceMarkingViewModel", "Capture failed: ${e.message}")
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = e.message ?: "Failed to recognize face"
                })
                retakePhoto()
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun processImageFromGallery(
        bitmap: Bitmap,
        paint: Paint,
        onNoFaceDetected: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.update { true }
                acceptingCaptureFromCamera = false

                val result = omniScanRepository.processImageFromBitmap(bitmap, paint)

                result.onSuccess { processedImage ->
                    if (processedImage.faceBitmap == null) {
                        withContext(Dispatchers.Main) {
                            onNoFaceDetected()
                        }
                        retakePhoto()
                        return@launch
                    }

                    _capturedImage.update { processedImage }
                    _isCameraActive.update { false }

                    val facePids = faceInfoDao.facePIDsList()
                    if (facePids.isEmpty()) {
                        emitError(ResponseError.BAD_REQUEST.apply {
                            actualResponse = "No registered faces found."
                        })
                        retakePhoto()
                        return@launch
                    }

                    val faceResults = faceRecognitionService.recognizeFace(
                        processedImage,
                        facePids,
                        context
                    )

                    if (faceResults.isNullOrEmpty()) {
                        emitError(ResponseError.BAD_REQUEST.apply {
                            actualResponse = "No matching faces found."
                        })
                        retakePhoto()
                        return@launch
                    }

                    val matchingResults = faceResults.filter { it.matchesCriteria }
                    if (matchingResults.isEmpty()) {
                        emitError(ResponseError.BAD_REQUEST.apply {
                            actualResponse = "No faces matched the recognition threshold."
                        })
                        retakePhoto()
                        return@launch
                    }

                    val recognizedPersons = matchingResults.mapNotNull { result ->
                        getPersonDetails(result.pid, result.similarity)
                    }

                    _recognizedFaces.update { recognizedPersons }
                    _showBottomSheet.update { true }

                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }.onFailure {
                    withContext(Dispatchers.Main) {
                        onNoFaceDetected()
                    }
                    retakePhoto()
                }
            } catch (e: Exception) {
                Log.e("AttendanceMarkingViewModel", "Gallery image processing failed: ${e.message}")
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = e.message ?: "Failed to process image"
                })
                retakePhoto()
            } finally {
                _isLoading.update { false }
            }
        }
    }

    private suspend fun getPersonDetails(pid: String, similarity: Float): RecognizedPerson? {
        return withContext(Dispatchers.IO) {
            try {
                val student = studentDao.getStudentDetailsByPid(pid)
                if (student != null) {
                    val villageName = student.villageId?.let { villageDao.getVillage(it)?.name } ?: "Unknown"
                    val groupName = student.groupId?.let { groupsDao.getGroup(it)?.name } ?: "Unknown"
                    return@withContext RecognizedPerson(
                        pid = pid,
                        name = "${student.firstName} ${student.lastName}",
                        isStudent = true,
                        similarity = similarity,
                        subtitle = "$villageName • $groupName",
                        profileImageUrl = student.profilePic?.url
                    )
                }

                val volunteer = volunteerDao.getVolunteer(pid)
                if (volunteer != null) {
                    return@withContext RecognizedPerson(
                        pid = pid,
                        name = "${volunteer.firstName} ${volunteer.lastName}",
                        isStudent = false,
                        similarity = similarity,
                        subtitle = "${volunteer.rollNumber ?: "N/A"} • ${volunteer.batch ?: "N/A"}",
                        profileImageUrl = volunteer.profilePic?.url
                    )
                }

                null
            } catch (e: Exception) {
                Log.e("AttendanceMarkingViewModel", "Failed to get person details: ${e.message}")
                null
            }
        }
    }

    fun markAttendance(pid: String, isStudent: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isMarkingAttendance.update { true }

                val currentDate = Utils.timestamp().split(" ")[0]
                val request = BulkAttendanceRequest(
                    date = currentDate,
                    pids = listOf(pid)
                )

                val result = if (isStudent) {
                    attendanceRepository.markStudentAttendanceBulk(request)
                } else {
                    attendanceRepository.markVolunteerAttendanceBulk(request)
                }

                result.collect { resource ->
                    if (resource.isSuccess) {
                        val response = resource.data
                        if (response?.inserted == 1) {
                            emitMsg("Attendance marked successfully!")
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                            dismissBottomSheet()
                            retakePhoto()
                        } else if (response?.skippedExisting == 1) {
                            emitError(ResponseError.BAD_REQUEST.apply {
                                actualResponse = "Attendance already marked for today"
                            })
                        } else {
                            emitError(ResponseError.BAD_REQUEST.apply {
                                actualResponse = "Failed to mark attendance"
                            })
                        }
                    } else if (resource.isFailed) {
                        emitError(resource.error)
                    }
                }
            } catch (e: Exception) {
                Log.e("AttendanceMarkingViewModel", "Failed to mark attendance: ${e.message}")
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = e.message ?: "Failed to mark attendance"
                })
            } finally {
                _isMarkingAttendance.update { false }
            }
        }
    }

    fun retakePhoto() {
        acceptingCaptureFromCamera = true
        _isCameraActive.update { true }
        _capturedImage.update { null }
        _recognizedFaces.update { emptyList() }
        _liveRecognizedFaces.update { emptyList() }
        _showBottomSheet.update { false }
    }

    fun dismissBottomSheet() {
        _showBottomSheet.update { false }
    }

    fun updateCapturedImage(image: ProcessedImage) {
        _capturedImage.update { image }
    }
}
