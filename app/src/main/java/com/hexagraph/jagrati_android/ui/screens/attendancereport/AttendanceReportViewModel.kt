package com.hexagraph.jagrati_android.ui.screens.attendancereport

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceReportViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val appPreferences: AppPreferences,
    private val studentDao: StudentDao,
    private val volunteerDao: VolunteerDao
) : BaseViewModel<AttendanceReportUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    private val _reportData = MutableStateFlow<com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse?>(null)
    private val _selectedStudentVillage = MutableStateFlow<Long?>(null)
    private val _selectedStudentGender = MutableStateFlow<Gender?>(null)
    private val _selectedStudentGroup = MutableStateFlow<Long?>(null)
    private val _selectedVolunteerBatch = MutableStateFlow<String?>(null)
    private val _isDeletingAttendance = MutableStateFlow(false)
    private val _canDeleteStudentAttendance = MutableStateFlow(false)
    private val _canDeleteVolunteerAttendance = MutableStateFlow(false)
    private val _studentProfilePics = MutableStateFlow<Map<String, String?>>(emptyMap())
    private val _volunteerProfilePics = MutableStateFlow<Map<String, String?>>(emptyMap())

    override val uiState: StateFlow<AttendanceReportUiState> = createUiStateFlow()

    init {
        // Check permissions
        viewModelScope.launch {
            appPreferences.hasPermission(AllPermissions.ATTENDANCE_DELETE_STUDENT).collect {
                _canDeleteStudentAttendance.value = it
            }
        }
        viewModelScope.launch {
            appPreferences.hasPermission(AllPermissions.ATTENDANCE_DELETE_VOLUNTEER).collect {
                _canDeleteVolunteerAttendance.value = it
            }
        }
        loadAttendanceReport()
    }

    override fun createUiStateFlow(): StateFlow<AttendanceReportUiState> {
        return combine(
            _isLoading,
            _isRefreshing,
            _selectedDateMillis,
            _reportData,
            _selectedStudentVillage,
            _selectedStudentGender,
            _selectedStudentGroup,
            _selectedVolunteerBatch,
            _isDeletingAttendance,
            _canDeleteStudentAttendance,
            _canDeleteVolunteerAttendance,
            _studentProfilePics,
            _volunteerProfilePics
        ) { flows: Array<*> ->
            val isLoading = flows[0] as Boolean
            val isRefreshing = flows[1] as Boolean
            val dateMillis = flows[2] as Long
            val reportData = flows[3] as? com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse
            val studentVillage = flows[4] as? Long
            val studentGender = flows[5] as? Gender
            val studentGroup = flows[6] as? Long
            val volunteerBatch = flows[7] as? String
            val isDeletingAttendance = flows[8] as Boolean
            val canDeleteStudent = flows[9] as Boolean
            val canDeleteVolunteer = flows[10] as Boolean
            val studentPics = flows[11] as Map<String, String?>
            val volunteerPics = flows[12] as Map<String, String?>

            val filteredStudents = reportData?.presentStudents?.filter { student ->
                (studentVillage == null || student.villageId == studentVillage) &&
                (studentGender == null || student.gender == studentGender) &&
                (studentGroup == null || student.groupId == studentGroup)
            } ?: emptyList()

            val filteredVolunteers = reportData?.presentVolunteers?.filter { volunteer ->
                volunteerBatch == null || volunteer.batch == volunteerBatch
            } ?: emptyList()

            val groupCounts = reportData?.presentStudents?.groupBy { it.groupName }
                ?.mapValues { it.value.size } ?: emptyMap()

            AttendanceReportUiState(
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                selectedDateMillis = dateMillis,
                reportData = reportData,
                selectedStudentVillage = studentVillage,
                selectedStudentGender = studentGender,
                selectedStudentGroup = studentGroup,
                selectedVolunteerBatch = volunteerBatch,
                filteredStudents = filteredStudents,
                filteredVolunteers = filteredVolunteers,
                groupCounts = groupCounts,
                isDeletingAttendance = isDeletingAttendance,
                canDeleteStudentAttendance = canDeleteStudent,
                canDeleteVolunteerAttendance = canDeleteVolunteer,
                studentProfilePics = studentPics,
                volunteerProfilePics = volunteerPics
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceReportUiState()
        )
    }

    fun loadAttendanceReport(isRefreshing: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefreshing) {
                _isRefreshing.value = true
            } else {
                _isLoading.value = true
            }

            val dateString = formatDateForApi(_selectedDateMillis.value)

            attendanceRepository.getAttendanceReport(dateString).collect { resource ->
                when (resource.status) {
                    Resource.Status.LOADING -> {}
                    Resource.Status.SUCCESS -> {
                        _reportData.value = resource.data
                        _isLoading.value = false
                        _isRefreshing.value = false
                        clearFilters()

                        // Load profile pictures from DAO
                        resource.data?.let { report ->
                            loadProfilePictures(report)
                        }
                    }
                    Resource.Status.FAILED -> {
                        _isLoading.value = false
                        _isRefreshing.value = false
                        emitError(resource.error)
                    }
                }
            }
        }
    }

    private suspend fun loadProfilePictures(report: AttendanceReportResponse) {
        val studentPics = mutableMapOf<String, String?>()
        val volunteerPics = mutableMapOf<String, String?>()

        // Load student profile pictures
        report.presentStudents.forEach { student ->
            try {
                val studentEntity = studentDao.getStudentDetailsByPid(student.pid)
                studentPics[student.pid] = studentEntity?.profilePic?.url
            } catch (e: Exception) {
                studentPics[student.pid] = null
            }
        }

        // Load volunteer profile pictures
        report.presentVolunteers.forEach { volunteer ->
            try {
                val volunteerEntity = volunteerDao.getVolunteer(volunteer.pid)
                volunteerPics[volunteer.pid] = volunteerEntity?.profilePic?.url
            } catch (e: Exception) {
                volunteerPics[volunteer.pid] = null
            }
        }

        _studentProfilePics.value = studentPics
        _volunteerProfilePics.value = volunteerPics
    }

    fun setSelectedDate(dateMillis: Long) {
        _selectedDateMillis.value = dateMillis
        loadAttendanceReport()
    }

    fun setStudentVillageFilter(villageId: Long?) {
        _selectedStudentVillage.value = villageId
    }

    fun setStudentGenderFilter(gender: Gender?) {
        _selectedStudentGender.value = gender
    }

    fun setStudentGroupFilter(groupId: Long?) {
        _selectedStudentGroup.value = groupId
    }

    fun setVolunteerBatchFilter(batch: String?) {
        _selectedVolunteerBatch.value = batch
    }

    fun clearFilters() {
        _selectedStudentVillage.value = null
        _selectedStudentGender.value = null
        _selectedStudentGroup.value = null
        _selectedVolunteerBatch.value = null
    }

    fun deleteStudentAttendance(aid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDeletingAttendance.value = true

            try {
                val id = aid.toLongOrNull()
                if (id == null) {
                    emitError(ResponseError.BAD_REQUEST)
                    _isDeletingAttendance.value = false
                    return@launch
                }

                attendanceRepository.deleteStudentAttendance(id).collect { resource ->
                    when (resource.status) {
                        Resource.Status.LOADING -> {}
                        Resource.Status.SUCCESS -> {
                            _isDeletingAttendance.value = false
                            emitMsg("Attendance deleted successfully")
                            loadAttendanceReport()
                        }
                        Resource.Status.FAILED -> {
                            _isDeletingAttendance.value = false
                            emitError(resource.error)
                        }
                    }
                }
            } catch (e: Exception) {
                _isDeletingAttendance.value = false
                emitError(ResponseError.UNKNOWN)
            }
        }
    }

    fun deleteVolunteerAttendance(aid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDeletingAttendance.value = true

            try {
                val id = aid.toLongOrNull()
                if (id == null) {
                    emitError(ResponseError.BAD_REQUEST)
                    _isDeletingAttendance.value = false
                    return@launch
                }

                attendanceRepository.deleteVolunteerAttendance(id).collect { resource ->
                    when (resource.status) {
                        Resource.Status.LOADING -> {}
                        Resource.Status.SUCCESS -> {
                            _isDeletingAttendance.value = false
                            emitMsg("Attendance deleted successfully")
                            loadAttendanceReport()
                        }
                        Resource.Status.FAILED -> {
                            _isDeletingAttendance.value = false
                            emitError(resource.error)
                        }
                    }
                }
            } catch (e: Exception) {
                _isDeletingAttendance.value = false
                emitError(ResponseError.UNKNOWN)
            }
        }
    }

    private fun formatDateForApi(millis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(millis))
    }
}
