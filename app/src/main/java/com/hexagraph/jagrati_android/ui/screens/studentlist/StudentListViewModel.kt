package com.hexagraph.jagrati_android.ui.screens.studentlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VillageDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class StudentListUiState(
    val students: List<Student> = emptyList(),
    val villages: List<Village> = emptyList(),
    val groups: List<Groups> = emptyList(),
    val selectedVillage: Village? = null,
    val selectedGroup: Groups? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class StudentListViewModel(
    private val studentDao: StudentDao,
    private val villageDao: VillageDao,
    private val groupsDao: GroupsDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentListUiState())
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()

    private var allStudents: List<Student> = emptyList()

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                combine(
                    studentDao.getAllActiveStudentDetails(),
                    villageDao.getAllActiveVillages(),
                    groupsDao.getAllActiveGroups()
                ) { students, villages, groups ->
                    Triple(students, villages, groups)
                }
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to load students: ${e.message}"
                        )
                    }
                    .collect { (students, villages, groups) ->
                        allStudents = students
                        _uiState.value = _uiState.value.copy(
                            students = students,
                            villages = villages,
                            groups = groups,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load students: ${e.message}"
                )
            }
        }
    }

    fun filterByVillage(village: Village?) {
        _uiState.value = _uiState.value.copy(selectedVillage = village)
        applyFilters()
    }

    fun filterByGroup(group: Groups?) {
        _uiState.value = _uiState.value.copy(selectedGroup = group)
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val filtered = allStudents.filter { student ->
            val villageMatch = currentState.selectedVillage?.let {
                student.villageId == it.id
            } ?: true

            val groupMatch = currentState.selectedGroup?.let {
                student.groupId == it.id
            } ?: true

            villageMatch && groupMatch
        }

        _uiState.value = currentState.copy(students = filtered)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

