package com.hexagraph.jagrati_android.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceRequest
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VillageDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import com.hexagraph.jagrati_android.repository.auth.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UnifiedSearchUiState(
    val query: String = "",
    val students: List<Student> = emptyList(),
    val volunteers: List<Volunteer> = emptyList(),
    val villages: List<Village> = emptyList(),
    val groups: List<Groups> = emptyList(),
    val isSearching: Boolean = false,
    val errorMessage: String? = null
)

class UnifiedSearchViewModel(
    private val studentDao: StudentDao,
    private val volunteerDao: VolunteerDao,
    private val villageDao: VillageDao,
    private val groupsDao: GroupsDao,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnifiedSearchUiState())
    val uiState: StateFlow<UnifiedSearchUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")
    var selectedDateMillis: Long = 0L

    init {
        loadMetadata()
        setupSearch()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            try {
                villageDao.getAllActiveVillages().collect { villages ->
                    _uiState.value = _uiState.value.copy(villages = villages)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load metadata: ${e.message}"
                )
            }
        }

        viewModelScope.launch {
            try {
                groupsDao.getAllActiveGroups().collect { groups ->
                    _uiState.value = _uiState.value.copy(groups = groups)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load metadata: ${e.message}"
                )
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.value = _uiState.value.copy(
                            students = emptyList(),
                            volunteers = emptyList(),
                            isSearching = false
                        )
                    } else {
                        performSearch(query)
                    }
                }
        }
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(query = query)

        if (query.isNotBlank()) {
            _uiState.value = _uiState.value.copy(isSearching = true)
        }

        searchQueryFlow.value = query
    }

    private suspend fun performSearch(query: String) {
        try {
            val students = studentDao.getStudentDetailsByQuery(query).take(100)
            val volunteers = volunteerDao.getVolunteersByQuery(query).take(100)

            _uiState.value = _uiState.value.copy(
                students = students,
                volunteers = volunteers,
                isSearching = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isSearching = false,
                errorMessage = "Search failed: ${e.message}"
            )
        }
    }

    fun markAttendance(pid: String, isStudent: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update {
                    it.copy(isSearching = true)
                }

                // Format the selected date from UI state
                val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val formattedDate = dateFormatter.format(java.util.Date(selectedDateMillis))

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
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        } else if (response?.skippedExisting == 1) {
                            _uiState.update {
                                it.copy(errorMessage = "Attendance already marked for this date")
                            }

                        } else {
                            _uiState.update {
                                it.copy(errorMessage = "Failed to mark attendance")
                            }

                        }
                    } else if (resource.isFailed) {
                        _uiState.update {
                            it.copy(errorMessage = resource.error?.actualResponse)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AttendanceMarkingViewModel", "Failed to mark attendance: ${e.message}")
                _uiState.update {
                    it.copy(errorMessage = "Failed to mark attendance")
                }
            } finally {
                _uiState.update {
                    it.copy(isSearching = false)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

