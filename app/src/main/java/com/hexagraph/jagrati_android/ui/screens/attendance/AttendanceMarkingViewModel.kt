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
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.service.face_recognition.FaceRecognitionService
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.CrashlyticsHelper
import com.hexagraph.jagrati_android.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private val attendanceRepository: AttendanceRepository,
    private val appPreferences: AppPreferences,
    private val isSearching: Boolean,
    private val defaultDateMillis: Long
) : BaseViewModel<AttendanceMarkingUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _capturedImage = MutableStateFlow<ProcessedImage?>(null)
    private val _isCameraActive = MutableStateFlow(true)
    private val _recognizedFaces = MutableStateFlow<List<RecognizedPerson>>(emptyList())
    private val _liveRecognizedFaces = MutableStateFlow<List<RecognizedPerson>>(emptyList())
    private val _showBottomSheet = MutableStateFlow(false)
    private val _isMarkingAttendance = MutableStateFlow(false)
    private val _selectedDateMillis = MutableStateFlow(defaultDateMillis)
    private var acceptingCaptureFromCamera = true

    private val semaphore = Semaphore(1)
    private val liveFaceSemaphore = Semaphore(1)
    private var currentDetectionJob: Job? = null

    override val uiState: StateFlow<AttendanceMarkingUiState> = createUiStateFlow()
    private var hasVolunteerAttendancePermissions = false

    init {
        runBlocking {
            hasVolunteerAttendancePermissions = appPreferences.hasPermission(AllPermissions.ATTENDANCE_MARK_VOLUNTEER).first()
        }
    }

    override fun createUiStateFlow(): StateFlow<AttendanceMarkingUiState> {
        return combine(
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
                selectedDateMillis = _selectedDateMillis.value,
                showBottomSheet = _showBottomSheet.value,
                isMarkingAttendance = _isMarkingAttendance.value,
                error = errorFlow.value,
                successMessage = successMsgFlow.value,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceMarkingUiState(
                selectedDateMillis = defaultDateMillis
            )
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
                    val acquired = semaphore.tryAcquire()
                    if (!acquired) return@onSuccess
                    updateCapturedImage(processedImage)
                    currentDetectionJob?.cancel()
                    currentDetectionJob = viewModelScope.launch(Dispatchers.IO) {
                        try {
                            if (acceptingCaptureFromCamera && !_showBottomSheet.value) {
                                recognizeFacesLive(processedImage)
                            }
                        } catch (e: Exception) {
                            CrashlyticsHelper.logError("AttendanceMarkingViewModel", "Live recognition failed: ${e.message}")
                        } finally {
                            delay(300) // Throttle to avoid excessive processing
                            semaphore.release()
                        }
                    }


                }.onFailure {
                    CrashlyticsHelper.logError("AttendanceMarkingViewModel", "Face detection failed: ${it.message}")
                }
            }
        )
    }

    private suspend fun recognizeFacesLive(processedImage: ProcessedImage) {
        if(liveFaceSemaphore.tryAcquire()) {
            try {
                val facePids = getFaceIds()
                if (facePids.isEmpty() || processedImage.faceBitmap == null) {
                    _liveRecognizedFaces.update { emptyList() }
                    return
                }

                val results = faceRecognitionService.recognizeFace(
                    processedImage,
                    facePids,
                    context
                )

                CrashlyticsHelper.log("AttendanceMarkingViewModel", "Live recognition results: $results")

                if (results != null && results.isNotEmpty()) {
                    val topMatches = results.filter { it.matchesCriteria }
                    val recognizedPersons = topMatches.mapNotNull { result ->
                        getPersonDetails(result.pid, result.similarity)
                    }
                    _liveRecognizedFaces.update { recognizedPersons.sortedByDescending { it.similarity } }
                } else {
                    _liveRecognizedFaces.update { emptyList() }
                }
            } catch (e: Exception) {
                CrashlyticsHelper.logError("AttendanceMarkingViewModel", "Face recognition failed: ${e.message}")
//                _liveRecognizedFaces.update { emptyList() }
            } finally {
                liveFaceSemaphore.release()
            }
        }
    }

    fun captureFace() {
        val capturedImage = _capturedImage.value
        acceptingCaptureFromCamera = false
        _isCameraActive.update { false }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.update { true }

                if (capturedImage?.faceBitmap == null) {
                    emitError(ResponseError.BAD_REQUEST.apply {
                        actualResponse = "No face detected. Please try again."
                    })
                    retakePhoto()
                    return@launch
                }

                val facePids = getFaceIds()
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
                CrashlyticsHelper.logError("AttendanceMarkingViewModel", "Capture failed: ${e.message}")
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

                    val facePids = getFaceIds()
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
                CrashlyticsHelper.logError("AttendanceMarkingViewModel", "Gallery image processing failed: ${e.message}")
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
                CrashlyticsHelper.logError("AttendanceMarkingViewModel", "Failed to get person details: ${e.message}")
                null
            }
        }
    }

    private suspend fun getFaceIds(): List<String> {
        if(hasVolunteerAttendancePermissions || isSearching){
            return faceInfoDao.facePIDsList()
        }
        return faceInfoDao.studentFacePIDsList()
    }

    fun markAttendance(pid: String, isStudent: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isMarkingAttendance.update { true }

                // Format the selected date from UI state
                val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val formattedDate = dateFormatter.format(java.util.Date(_selectedDateMillis.value))

                val request = BulkAttendanceRequest(
                    date = formattedDate,
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
                            dismissBottomSheetAndRetakePhoto()
                        } else if (response?.skippedExisting == 1) {
                            emitError(ResponseError.BAD_REQUEST.apply {
                                actualResponse = "Attendance already marked for this date"
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
                CrashlyticsHelper.logError("AttendanceMarkingViewModel", "Failed to mark attendance: ${e.message}")
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = e.message ?: "Failed to mark attendance"
                })
            } finally {
                _isMarkingAttendance.update { false }
            }
        }
    }

    fun retakePhoto() {
        currentDetectionJob?.cancel()
        acceptingCaptureFromCamera = true
        _isCameraActive.update { true }
        _capturedImage.update { null }
        _recognizedFaces.update { emptyList() }
        _liveRecognizedFaces.update { emptyList() }
        _showBottomSheet.update { false }
    }

    override fun onCleared() {
        super.onCleared()
        stopFaceDetection()
    }

    fun stopFaceDetection() {
        currentDetectionJob?.cancel()
        acceptingCaptureFromCamera = false
        _isCameraActive.update { false }
        semaphore.tryAcquire() // Clear any pending operations
    }
    fun dismissBottomSheetAndRetakePhoto() {
        _showBottomSheet.update { false }
        retakePhoto()
    }

    fun updateCapturedImage(image: ProcessedImage) {
        _capturedImage.update { image }
    }

    fun updateSelectedDateMillis(millis: Long) {
        _selectedDateMillis.update { millis }
    }
}
