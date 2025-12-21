package com.hexagraph.jagrati_android.ui.screens.myprofile

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordResponse
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.model.toEntity
import com.hexagraph.jagrati_android.model.toUpdateVolunteerRequest
import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
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

data class MyProfileUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val currentUser: User? = null,
    val volunteer: VolunteerDTO? = null,
    val attendanceRecords: List<AttendanceRecordResponse> = emptyList(),
    val lastPresentDate: String? = null,
    val presentCountLastMonth: Int = 0,
    val userRoles: List<String> = emptyList(),
    val showEditOptionsSheet: Boolean = false,
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class MyProfileViewModel(
    private val volunteerRepository: VolunteerRepository,
    private val attendanceRepository: AttendanceRepository,
    private val appPreferences: AppPreferences,
    private val omniScanRepository: OmniScanRepository,
) : BaseViewModel<MyProfileUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _currentUser = MutableStateFlow<User?>(null)
    private val _volunteer = MutableStateFlow<VolunteerDTO?>(null)
    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecordResponse>>(emptyList())
    private val _lastPresentDate = MutableStateFlow<String?>(null)
    private val _presentCountLastMonth = MutableStateFlow(0)
    private val _userRoles = MutableStateFlow<List<String>>(emptyList())
    private val _showEditOptionsSheet = MutableStateFlow(false)

    override val uiState: StateFlow<MyProfileUiState> = createUiStateFlow()

    init {
        loadUserData()
    }

    override fun createUiStateFlow(): StateFlow<MyProfileUiState> {
        return combine(
            _isLoading,
            _isRefreshing,
            _currentUser,
            _volunteer,
            _attendanceRecords,
            _lastPresentDate,
            _presentCountLastMonth,
            _userRoles,
            _showEditOptionsSheet,
            errorFlow,
            successMsgFlow
        ) { flows ->
            MyProfileUiState(
                isLoading = flows[0] as Boolean,
                isRefreshing = flows[1] as Boolean,
                currentUser = flows[2] as User?,
                volunteer = flows[3] as VolunteerDTO?,
                attendanceRecords = flows[4] as List<AttendanceRecordResponse>,
                lastPresentDate = flows[5] as String?,
                presentCountLastMonth = flows[6] as Int,
                userRoles = flows[7] as List<String>,
                showEditOptionsSheet = flows[8] as Boolean,
                error = flows[9] as ResponseError?,
                successMessage = flows[10] as String?
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MyProfileUiState()
        )
    }

    private fun loadUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }

            val user = appPreferences.userDetails.getFlow().firstOrNull()
            val userRoles = appPreferences.userRoles.get()
            _currentUser.update { user }

            user?.let { userData ->
                _userRoles.update { userRoles.map { it.name } }

                user.pid.let { pid ->
                    loadVolunteerProfile(pid)
                    loadAttendanceData(pid)
                }
            }

            _isLoading.update { false }
        }
    }

    private suspend fun loadVolunteerProfile(pid: String) {
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
    }

    private suspend fun loadAttendanceData(pid: String) {
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
    }

    private fun calculateAttendanceStats(records: List<AttendanceRecordResponse>) {
        _lastPresentDate.update { AttendanceUtils.getLastPresentDate(records) }
        _presentCountLastMonth.update { AttendanceUtils.getPresentCountLastMonth(records) }
    }

    fun deleteFaceData() {
        viewModelScope.launch(Dispatchers.IO) {
            val volunteer = _volunteer.value
            val updateRequest = volunteer?.toEntity()?.toUpdateVolunteerRequest()?.copy(
                profilePic = null
            )
            if(updateRequest == null){
                emitError(ResponseError.UNKNOWN.apply { actualResponse = "Volunteer data is null." })
                return@launch
            }
            volunteerRepository.updateMyDetails(updateRequest).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { updatedVolunteer ->
                            _volunteer.update { updatedVolunteer }
                            omniScanRepository.deleteFaceIfExists(volunteer.pid)
                        }
                    }
                    resource.isFailed -> {
                        emitError(resource.error ?: ResponseError.UNKNOWN)
                        return@collect
                    }
                }
            }
            emitMsg("Face data deleted successfully.")
        }
    }

    fun showEditOptionsSheet() {
        _showEditOptionsSheet.update { true }
    }

    fun hideEditOptionsSheet() {
        _showEditOptionsSheet.update { false }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.update { true }

            val user = appPreferences.userDetails.getFlow().firstOrNull()
            val userRoles = appPreferences.userRoles.get()
            _currentUser.update { user }

            user?.let { userData ->
                _userRoles.update { userRoles.map { it.name } }

                user.pid.let { pid ->
                    loadVolunteerProfile(pid)
                    loadAttendanceData(pid)
                }
            }

            _isRefreshing.update { false }
        }
    }
}
