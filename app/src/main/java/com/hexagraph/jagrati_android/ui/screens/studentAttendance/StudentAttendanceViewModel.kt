package com.hexagraph.jagrati_android.ui.screens.studentAttendance

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.AttendanceModel
import com.hexagraph.jagrati_android.model.PersonType
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.dao.AttendanceDao
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.StudentDetailsDao
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class StudentAttendanceViewModel(
    private val attendanceDao: AttendanceDao,
    private val faceInfoDao: FaceInfoDao,
    private val studentDetailsDao: StudentDetailsDao
) : BaseViewModel<StudentAttendanceUIState>() {
    val uiStateFlow = MutableStateFlow<StudentAttendanceUIState>(StudentAttendanceUIState())


    fun onEventDateSelected(timeMillis: Long, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val range = TimeUtils.getTimeMillisRangeForDate(timeMillis)
            val attendanceData = attendanceDao.getAttendanceInRange(range.first, range.second)
            uiStateFlow.emit(
                uiStateFlow.value.copy(
                    dateMillis = timeMillis,
                    currentListOfStudents = attendanceData
                )
            )
            getProcessedImagesListAndStudentInfo(context)
        }
    }

    private suspend fun getProcessedImagesListAndStudentInfo(context: Context) {
            val list = uiStateFlow.value.currentListOfStudents.map {
                it.toFaceInfo(faceInfoDao).processedImage(context)
            }
            val studentDetailsList = uiStateFlow.value.currentListOfStudents.map { it.toStudentDetails(studentDetailsDao) }
            uiStateFlow.emit(
                uiStateFlow.value.copy(
                    allProcessedImage = list,
                    allStudentDetails = studentDetailsList
                )
            )
    }

    fun takeAttendance(processedImages: List<ProcessedImage>, context: Context) {
        val attendanceList = uiStateFlow.value.currentListOfStudents
        viewModelScope.launch(Dispatchers.IO) {
            processedImages.forEach { image ->
                val search = attendanceList.find { it.pid == image.pid }
                if (search == null) {
                    attendanceDao.insertAttendance(
                        AttendanceModel(
                            pid = image.pid,
                            personType = PersonType.STUDENT,
                            attendanceDate = uiStateFlow.value.dateMillis,
                            updateTimeMillis = System.currentTimeMillis()
                        )
                    )
                }
            }
            attendanceList.forEach { attendance ->
                val search = processedImages.find { it.pid == attendance.pid }
                if (search == null) {
                    attendanceDao.deleteAttendance(attendance)
                }
            }
            onEventDateSelected(uiStateFlow.value.dateMillis, context)
        }
    }

    fun switchView() {
        viewModelScope.launch {
            uiStateFlow.emit(
                uiStateFlow.value.copy(
                    isScanModeActive = !uiState.value.isScanModeActive
                )
            )
        }
    }

    override val uiState: StateFlow<StudentAttendanceUIState> = createUiStateFlow()

    override fun createUiStateFlow(): StateFlow<StudentAttendanceUIState> {
        return uiStateFlow
    }

}