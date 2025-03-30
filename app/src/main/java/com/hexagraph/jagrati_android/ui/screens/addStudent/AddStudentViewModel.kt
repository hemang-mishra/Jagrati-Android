package com.hexagraph.jagrati_android.ui.screens.addStudent

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.repository.student.AddStudentRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStudentViewModel @Inject constructor(
    private val addStudentRepository: AddStudentRepository
) : BaseViewModel<AddStudentUIState>() {

    private val studentUiStateFlow = MutableStateFlow(AddStudentUIState())

    override val uiState: StateFlow<AddStudentUIState> = createUiStateFlow()

    fun changeDetailsOfStudents(studentDetails: StudentDetails){
        viewModelScope.launch {
            studentUiStateFlow.emit(
                studentUiStateFlow.value.copy(
                    studentData = studentDetails
                )
            )
        }
    }

    fun initialize(pid: String){
        viewModelScope.launch {
            val studentDetails = addStudentRepository.getStudentDetails(pid)
            if(studentDetails != null)
            studentUiStateFlow.emit(
                studentUiStateFlow.value.copy(
                    studentData = studentDetails,
                    isStudentNew = false
                )
            )
        }
    }

    fun saveStudent(studentDetails: StudentDetails): StudentDetails{
        var student = studentDetails
        //Assuming that this is the insertion operation if the pid is empty
        if(studentDetails.pid.isEmpty()){
            student = studentDetails.copy(pid = Utils.PIDGenerator(name = studentDetails.firstName))
        }
        viewModelScope.launch {
           addStudentRepository.upsertStudent(studentDetails)
        }
        return student
    }

    override fun createUiStateFlow(): StateFlow<AddStudentUIState> {
        return combine(studentUiStateFlow){
            studentUiState ->
            studentUiState[0]
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AddStudentUIState()
        )
    }
}