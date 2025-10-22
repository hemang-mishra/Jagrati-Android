package com.hexagraph.jagrati_android.ui.screens.studentprofile

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordResponse
import com.hexagraph.jagrati_android.model.student.StudentGroupHistoryResponse
import com.hexagraph.jagrati_android.model.student.StudentResponse
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.repository.auth.StudentRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.AttendanceUtils
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val student: StudentResponse? = null,
    val attendanceRecords: List<AttendanceRecordResponse> = emptyList(),
    val lastPresentDate: String? = null,
    val presentCountLastWeek: Int = 0,
    val presentCountLastMonth: Int = 0,
    val groupHistory: List<StudentGroupHistoryResponse> = emptyList(),
    val showGroupHistorySheet: Boolean = false,
    val showEditOptionsSheet: Boolean = false,
    val canEditProfile: Boolean = false,
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class StudentProfileViewModel(
    private val pid: String,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel<StudentProfileUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _student = MutableStateFlow<StudentResponse?>(null)
    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecordResponse>>(emptyList())
    private val _lastPresentDate = MutableStateFlow<String?>(null)
    private val _presentCountLastWeek = MutableStateFlow(0)
    private val _presentCountLastMonth = MutableStateFlow(0)
    private val _groupHistory = MutableStateFlow<List<StudentGroupHistoryResponse>>(emptyList())
    private val _showGroupHistorySheet = MutableStateFlow(false)
    private val _showEditOptionsSheet = MutableStateFlow(false)
    private val _canEditProfile = MutableStateFlow(false)

    override val uiState: StateFlow<StudentProfileUiState> = createUiStateFlow()

    init {
        checkEditPermission()
        loadStudentProfile()
    }

    override fun createUiStateFlow(): StateFlow<StudentProfileUiState> {
        return combine(
            _isLoading,
            _isRefreshing,
            _student,
            _attendanceRecords,
            _lastPresentDate,
            _presentCountLastWeek,
            _presentCountLastMonth,
            _groupHistory,
            _showGroupHistorySheet,
            _showEditOptionsSheet,
            _canEditProfile,
            errorFlow,
            successMsgFlow
        ) { flows ->
            StudentProfileUiState(
                isLoading = flows[0] as Boolean,
                isRefreshing = flows[1] as Boolean,
                student = flows[2] as StudentResponse?,
                attendanceRecords = flows[3] as List<AttendanceRecordResponse>,
                lastPresentDate = flows[4] as String?,
                presentCountLastWeek = flows[5] as Int,
                presentCountLastMonth = flows[6] as Int,
                groupHistory = flows[7] as List<StudentGroupHistoryResponse>,
                showGroupHistorySheet = flows[8] as Boolean,
                showEditOptionsSheet = flows[9] as Boolean,
                canEditProfile = flows[10] as Boolean,
                error = flows[11] as ResponseError?,
                successMessage = flows[12] as String?
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudentProfileUiState()
        )
    }

    private fun checkEditPermission() {
        viewModelScope.launch(Dispatchers.IO) {
            val hasPermission = appPreferences.hasPermission(AllPermissions.STUDENT_UPDATE).firstOrNull() ?: false
            _canEditProfile.update { hasPermission }
        }
    }

    fun loadStudentProfile(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.update { true }
            } else {
                _isLoading.update { true }
            }

            // Load student details
            studentRepository.getStudentByPid(pid).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { studentResponse ->
                            _student.update { studentResponse }
                        }
                    }
                    resource.isFailed -> {
                        emitError(resource.error ?: ResponseError.UNKNOWN)
                    }
                }
            }

            // Load attendance records
            attendanceRepository.getStudentAttendance(pid).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { attendanceResponse ->
                            _attendanceRecords.update { attendanceResponse.records }
                            calculateAttendanceStats(attendanceResponse.records)
                        }
                    }
                    resource.isFailed -> {
                        emitError(resource.error ?: ResponseError.UNKNOWN)
                    }
                }
            }

            _isLoading.update { false }
            _isRefreshing.update { false }
        }
    }

    private fun calculateAttendanceStats(records: List<AttendanceRecordResponse>) {
        _lastPresentDate.update { AttendanceUtils.getLastPresentDate(records) }
        _presentCountLastWeek.update { AttendanceUtils.getPresentCountLastWeek(records) }
        _presentCountLastMonth.update { AttendanceUtils.getPresentCountLastMonth(records) }
    }

    fun loadGroupHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            studentRepository.getGroupHistory(pid).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { historyResponse ->
                            _groupHistory.update { historyResponse.history }
                        }
                    }
                    resource.isFailed -> {
                        emitError(resource.error ?: ResponseError.UNKNOWN)
                    }
                }
            }
        }
    }

    fun showGroupHistorySheet() {
        if (_groupHistory.value.isEmpty()) {
            loadGroupHistory()
        }
        _showGroupHistorySheet.update { true }
    }

    fun hideGroupHistorySheet() {
        _showGroupHistorySheet.update { false }
    }

    fun showEditOptionsSheet() {
        _showEditOptionsSheet.update { true }
    }

    fun hideEditOptionsSheet() {
        _showEditOptionsSheet.update { false }
    }

    fun refresh() {
        loadStudentProfile(isRefresh = true)
    }
}

