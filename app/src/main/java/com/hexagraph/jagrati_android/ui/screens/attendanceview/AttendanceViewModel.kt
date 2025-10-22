package com.hexagraph.jagrati_android.ui.screens.attendanceview

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordResponse
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AttendanceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AttendanceViewUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val personName: String = "",
    val isStudent: Boolean = true,
    val attendanceRecords: List<AttendanceRecordResponse> = emptyList(),
    val selectedYear: Int = LocalDate.now().year,
    val availableYears: List<Int> = emptyList(),
    val monthlyStats: Map<Int, Int> = emptyMap(),
    val totalPresent: Int = 0,
    val lastPresentDate: String? = null,
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class AttendanceViewModel(
    private val pid: String,
    private val isStudent: Boolean,
    private val attendanceRepository: AttendanceRepository,
    private val studentDao: StudentDao,
    private val volunteerDao: VolunteerDao
) : BaseViewModel<AttendanceViewUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _personName = MutableStateFlow("")
    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecordResponse>>(emptyList())
    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    private val _availableYears = MutableStateFlow<List<Int>>(emptyList())
    private val _monthlyStats = MutableStateFlow<Map<Int, Int>>(emptyMap())
    private val _totalPresent = MutableStateFlow(0)
    private val _lastPresentDate = MutableStateFlow<String?>(null)

    override val uiState: StateFlow<AttendanceViewUiState> = createUiStateFlow()

    init {
        loadPersonDetails()
        loadAttendanceRecords()
    }

    override fun createUiStateFlow(): StateFlow<AttendanceViewUiState> {
        return combine(
            _isLoading,
            _isRefreshing,
            _personName,
            _attendanceRecords,
            _selectedYear,
            _availableYears,
            _monthlyStats,
            _totalPresent,
            _lastPresentDate,
            errorFlow,
            successMsgFlow
        ) { flows ->
            AttendanceViewUiState(
                isLoading = flows[0] as Boolean,
                isRefreshing = flows[1] as Boolean,
                personName = flows[2] as String,
                isStudent = isStudent,
                attendanceRecords = flows[3] as List<AttendanceRecordResponse>,
                selectedYear = flows[4] as Int,
                availableYears = flows[5] as List<Int>,
                monthlyStats = flows[6] as Map<Int, Int>,
                totalPresent = flows[7] as Int,
                lastPresentDate = flows[8] as String?,
                error = flows[9] as ResponseError?,
                successMessage = flows[10] as String?
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceViewUiState()
        )
    }

    private fun loadPersonDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isStudent) {
                    val student = studentDao.getStudentDetailsByPid(pid)
                    _personName.update {
                        student?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Student"
                    }
                } else {
                    val volunteer = volunteerDao.getVolunteer(pid)
                    _personName.update {
                        volunteer?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Volunteer"
                    }
                }
            } catch (e: Exception) {
                emitError(ResponseError.UNKNOWN.apply {
                    actualResponse = "Failed to load person details"
                })
            }
        }
    }

    fun loadAttendanceRecords(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.update { true }
            } else {
                _isLoading.update { true }
            }

            val flow = if (isStudent) {
                attendanceRepository.getStudentAttendance(pid)
            } else {
                attendanceRepository.getVolunteerAttendance(pid)
            }

            flow.collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { response ->
                            _attendanceRecords.update { response.attendees }
                            calculateStats(response.attendees)
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

    private fun calculateStats(records: List<AttendanceRecordResponse>) {
        _totalPresent.update { AttendanceUtils.getTotalPresentCount(records) }
        _lastPresentDate.update { AttendanceUtils.getLastPresentDate(records) }

        val years = AttendanceUtils.getYearsWithRecords(records)
        _availableYears.update {
            if (years.isEmpty()) listOf(LocalDate.now().year) else years
        }

        if (_selectedYear.value !in _availableYears.value && _availableYears.value.isNotEmpty()) {
            _selectedYear.update { _availableYears.value.first() }
        }

        updateMonthlyStats(records, _selectedYear.value)
    }

    private fun updateMonthlyStats(records: List<AttendanceRecordResponse>, year: Int) {
        _monthlyStats.update { AttendanceUtils.getMonthlyStats(records, year) }
    }

    fun selectYear(year: Int) {
        _selectedYear.update { year }
        updateMonthlyStats(_attendanceRecords.value, year)
    }

    fun refresh() {
        loadAttendanceRecords(isRefresh = true)
    }

    fun getPresentDatesInMonth(year: Int, month: Int): Set<Int> {
        return AttendanceUtils.getPresentDatesInMonth(_attendanceRecords.value, year, month)
    }

    fun getRecordForDate(year: Int, month: Int, day: Int): AttendanceRecordResponse? {
        return AttendanceUtils.getRecordForDate(_attendanceRecords.value, year, month, day)
    }
}
