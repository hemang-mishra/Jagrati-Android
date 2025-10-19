package com.hexagraph.jagrati_android.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VillageDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

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
    private val groupsDao: GroupsDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnifiedSearchUiState())
    val uiState: StateFlow<UnifiedSearchUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

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

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

