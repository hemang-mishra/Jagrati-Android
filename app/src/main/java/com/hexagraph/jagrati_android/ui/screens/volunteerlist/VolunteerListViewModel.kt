package com.hexagraph.jagrati_android.ui.screens.volunteerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class VolunteerListUiState(
    val volunteers: List<Volunteer> = emptyList(),
    val allBatches: List<String> = emptyList(),
    val selectedBatch: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class VolunteerListViewModel(
    private val volunteerDao: VolunteerDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(VolunteerListUiState())
    val uiState: StateFlow<VolunteerListUiState> = _uiState.asStateFlow()

    private var allVolunteers: List<Volunteer> = emptyList()

    fun loadVolunteers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                volunteerDao.getAllActiveVolunteers()
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to load volunteers: ${e.message}"
                        )
                    }
                    .collect { volunteers ->
                        val sortedVolunteers = volunteers.sortedByDescending { volunteer ->
                            volunteer.batch?.toIntOrNull() ?: 0
                        }
                        allVolunteers = sortedVolunteers

                        val batches = volunteers
                            .mapNotNull { it.batch }
                            .distinct()
                            .sortedByDescending { it.toIntOrNull() ?: 0 }

                        _uiState.value = _uiState.value.copy(
                            volunteers = sortedVolunteers,
                            allBatches = batches,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load volunteers: ${e.message}"
                )
            }
        }
    }

    fun filterByBatch(batch: String?) {
        _uiState.value = _uiState.value.copy(selectedBatch = batch)

        val filtered = if (batch != null) {
            allVolunteers.filter { it.batch == batch }
        } else {
            allVolunteers
        }

        _uiState.value = _uiState.value.copy(volunteers = filtered)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
