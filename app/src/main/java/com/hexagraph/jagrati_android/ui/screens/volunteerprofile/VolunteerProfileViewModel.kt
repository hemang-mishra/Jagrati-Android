package com.hexagraph.jagrati_android.ui.screens.volunteerprofile

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordResponse
import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.AttendanceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VolunteerProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val volunteer: VolunteerDTO? = null,
    val attendanceRecords: List<AttendanceRecordResponse> = emptyList(),
    val lastPresentDate: String? = null,
    val presentCountLastMonth: Int = 0,
    val userRoles: List<String> = emptyList(),
    val showEditOptionsSheet: Boolean = false,
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class VolunteerProfileViewModel(
    private val pid: String,
    private val volunteerRepository: VolunteerRepository,
    private val attendanceRepository: AttendanceRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel<VolunteerProfileUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _volunteer = MutableStateFlow<VolunteerDTO?>(null)
    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecordResponse>>(emptyList())
    private val _lastPresentDate = MutableStateFlow<String?>(null)
    private val _presentCountLastMonth = MutableStateFlow(0)
    private val _userRoles = MutableStateFlow<List<String>>(emptyList())
    private val _showEditOptionsSheet = MutableStateFlow(false)

    override val uiState: StateFlow<VolunteerProfileUiState> = createUiStateFlow()

    init {
        loadUserRoles()
        loadVolunteerProfile()
    }

    override fun createUiStateFlow(): StateFlow<VolunteerProfileUiState> {
        return combine(
            _isLoading,
            _isRefreshing,
            _volunteer,
            _attendanceRecords,
            _lastPresentDate,
            _presentCountLastMonth,
            _userRoles,
            _showEditOptionsSheet,
            errorFlow,
            successMsgFlow
        ) { flows ->
            VolunteerProfileUiState(
                isLoading = flows[0] as Boolean,
                isRefreshing = flows[1] as Boolean,
                volunteer = flows[2] as VolunteerDTO?,
                attendanceRecords = flows[3] as List<AttendanceRecordResponse>,
                lastPresentDate = flows[4] as String?,
                presentCountLastMonth = flows[5] as Int,
                userRoles = flows[6] as List<String>,
                showEditOptionsSheet = flows[7] as Boolean,
                error = flows[8] as ResponseError?,
                successMessage = flows[9] as String?
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VolunteerProfileUiState()
        )
    }

    private fun loadUserRoles() {
        viewModelScope.launch(Dispatchers.IO) {
            val userRoles = appPreferences.userRoles.getFlow().firstOrNull()
            userRoles?.let { roles ->
                _userRoles.update { roles.map { it.name } }
            }
        }
    }

    fun loadVolunteerProfile(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRefresh) {
                _isRefreshing.update { true }
            } else {
                _isLoading.update { true }
            }

            volunteerRepository.getVolunteerByPid(pid).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { volunteerData ->
                            _volunteer.update { volunteerData }
                        }
                    }
                    resource.isFailed -> {
                        emitError(resource.error ?: ResponseError.UNKNOWN)
                    }
                }
            }

            attendanceRepository.getVolunteerAttendance(pid).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { attendanceResponse ->
                            _attendanceRecords.update { attendanceResponse.attendees }
                            calculateAttendanceStats(attendanceResponse.attendees)
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
        _presentCountLastMonth.update { AttendanceUtils.getPresentCountLastMonth(records) }
    }

    fun showEditOptionsSheet() {
        _showEditOptionsSheet.update { true }
    }

    fun hideEditOptionsSheet() {
        _showEditOptionsSheet.update { false }
    }

    fun refresh() {
        loadVolunteerProfile(isRefresh = true)
    }
}

