package com.hexagraph.jagrati_android.ui.screens.student

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.student.StudentRequest
import com.hexagraph.jagrati_android.model.student.StudentResponse
import com.hexagraph.jagrati_android.model.student.UpdateStudentRequest
import com.hexagraph.jagrati_android.model.student.toStudent
import com.hexagraph.jagrati_android.repository.auth.StudentRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentRegistrationUiState(
    val isLoading: Boolean = false,
    val isUpdateMode: Boolean = false,
    val existingPid: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val gender: String? = null,
    val yearOfBirth: Int? = null,
    val schoolClass: String = "",
    val primaryContactNo: String = "",
    val secondaryContactNo: String = "",
    val fathersName: String = "",
    val mothersName: String = "",
    val villages: Map<Long, String> = emptyMap(),
    val groups: Map<Long, String> = emptyMap(),
    val selectedVillageId: Long? = null,
    val selectedGroupId: Long? = null,
    val formErrors: Map<String, String> = emptyMap(),
    val submissionSuccessful: Boolean = false,
    val error: ResponseError? = null,
    val successMessage: String? = null
)

class StudentRegistrationViewModel(
    private val studentRepository: StudentRepository,
    private val studentDao: StudentDao,
    private val appPreferences: AppPreferences,
    private val pidToUpdate: String?
) : BaseViewModel<StudentRegistrationUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _isUpdateMode = MutableStateFlow(pidToUpdate != null)
    private val _existingPid = MutableStateFlow(pidToUpdate)

    private val _firstName = MutableStateFlow("")
    private val _lastName = MutableStateFlow("")
    private val _gender = MutableStateFlow<String?>(null)
    private val _yearOfBirth = MutableStateFlow<Int?>(null)
    private val _schoolClass = MutableStateFlow("")
    private val _primaryContactNo = MutableStateFlow("")
    private val _secondaryContactNo = MutableStateFlow("")
    private val _fathersName = MutableStateFlow("")
    private val _mothersName = MutableStateFlow("")

    private val _villages = MutableStateFlow<Map<Long, String>>(emptyMap())
    private val _groups = MutableStateFlow<Map<Long, String>>(emptyMap())
    private val _selectedVillageId = MutableStateFlow<Long?>(null)
    private val _selectedGroupId = MutableStateFlow<Long?>(null)

    private val _formErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    private val _submissionSuccessful = MutableStateFlow(false)

    override val uiState: StateFlow<StudentRegistrationUiState> = createUiStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                appPreferences.villages.getFlow().collect { villages ->
                    _villages.update { villages }
                }
            }
            launch {
                appPreferences.groups.getFlow().collect { groups ->
                    _groups.update { groups }
                }
            }
            if (pidToUpdate != null) {
                loadExistingStudentData(pidToUpdate)
            }
        }
    }

    override fun createUiStateFlow(): StateFlow<StudentRegistrationUiState> {
        return combine(
            _isLoading,
            _isUpdateMode,
            _existingPid,
            _firstName,
            _lastName,
            _gender,
            _yearOfBirth,
            _schoolClass,
            _primaryContactNo,
            _secondaryContactNo,
            _fathersName,
            _mothersName,
            _villages,
            _groups,
            _selectedVillageId,
            _selectedGroupId,
            _formErrors,
            _submissionSuccessful,
            errorFlow,
            successMsgFlow
        ) { flows ->
            StudentRegistrationUiState(
                isLoading = flows[0] as Boolean,
                isUpdateMode = flows[1] as Boolean,
                existingPid = flows[2] as String?,
                firstName = flows[3] as String,
                lastName = flows[4] as String,
                gender = flows[5] as String?,
                yearOfBirth = flows[6] as Int?,
                schoolClass = flows[7] as String,
                primaryContactNo = flows[8] as String,
                secondaryContactNo = flows[9] as String,
                fathersName = flows[10] as String,
                mothersName = flows[11] as String,
                villages = flows[12] as Map<Long, String>,
                groups = flows[13] as Map<Long, String>,
                selectedVillageId = flows[14] as Long?,
                selectedGroupId = flows[15] as Long?,
                formErrors = flows[16] as Map<String, String>,
                submissionSuccessful = flows[17] as Boolean,
                error = flows[18] as ResponseError?,
                successMessage = flows[19] as String?
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudentRegistrationUiState()
        )
    }

    private suspend fun loadExistingStudentData(pid: String) {
        _isLoading.update { true }
        try {
            studentRepository.getStudentByPid(pid).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { student ->
                            populateFormWithStudentData(student)
                        }
                        _isLoading.update { false }
                    }
                    resource.isFailed -> {
                        emitError(resource.error)
                        _isLoading.update { false }
                    }
                }
            }
        } catch (e: Exception) {
            emitError(ResponseError.UNKNOWN)
            _isLoading.update { false }
        }
    }

    private fun populateFormWithStudentData(student: StudentResponse) {
        _firstName.update { student.firstName }
        _lastName.update { student.lastName }
        _gender.update { student.gender }
        _yearOfBirth.update { student.yearOfBirth }
        _schoolClass.update { student.schoolClass ?: "" }
        _primaryContactNo.update { student.primaryContactNo ?: "" }
        _secondaryContactNo.update { student.secondaryContactNo ?: "" }
        _fathersName.update { student.fathersName ?: "" }
        _mothersName.update { student.mothersName ?: "" }
        _selectedVillageId.update { student.villageId }
        _selectedGroupId.update { student.groupId }
    }

    fun updateFirstName(value: String) {
        _firstName.update { value }
        clearFieldError("firstName")
    }

    fun updateLastName(value: String) {
        _lastName.update { value }
        clearFieldError("lastName")
    }

    fun updateGender(value: String) {
        _gender.update { value }
        clearFieldError("gender")
    }

    fun updateYearOfBirth(value: Int?) {
        _yearOfBirth.update { value }
        clearFieldError("yearOfBirth")
    }

    fun updateSchoolClass(value: String) {
        _schoolClass.update { value }
        clearFieldError("schoolClass")
    }

    fun updatePrimaryContactNo(value: String) {
        _primaryContactNo.update { value }
        clearFieldError("primaryContactNo")
    }

    fun updateSecondaryContactNo(value: String) {
        _secondaryContactNo.update { value }
        clearFieldError("secondaryContactNo")
    }

    fun updateFathersName(value: String) {
        _fathersName.update { value }
        clearFieldError("fathersName")
    }

    fun updateMothersName(value: String) {
        _mothersName.update { value }
        clearFieldError("mothersName")
    }

    fun updateSelectedVillageId(villageId: Long) {
        _selectedVillageId.update { villageId }
        clearFieldError("village")
    }

    fun updateSelectedGroupId(groupId: Long) {
        _selectedGroupId.update { groupId }
        clearFieldError("group")
    }

    private fun clearFieldError(field: String) {
        _formErrors.update { currentErrors ->
            currentErrors.toMutableMap().apply { remove(field) }
        }
    }

    fun submitStudent() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }

            try {
                if (_isUpdateMode.value && _existingPid.value != null) {
                    updateExistingStudent()
                } else {
                    registerNewStudent()
                }
            } catch (e: Exception) {
                emitError(ResponseError.UNKNOWN)
                _isLoading.update { false }
            }
        }
    }

    private suspend fun registerNewStudent() {
        val pid = Utils.PIDGenerator(name = _firstName.value + _lastName.value)

        val request = StudentRequest(
            pid = pid,
            firstName = _firstName.value.trim(),
            lastName = _lastName.value.trim(),
            yearOfBirth = _yearOfBirth.value,
            gender = _gender.value!!,
            schoolClass = _schoolClass.value.trim().takeIf { it.isNotEmpty() },
            villageId = _selectedVillageId.value!!,
            groupId = _selectedGroupId.value!!,
            primaryContactNo = _primaryContactNo.value.trim().takeIf { it.isNotEmpty() },
            secondaryContactNo = _secondaryContactNo.value.trim().takeIf { it.isNotEmpty() },
            fathersName = _fathersName.value.trim().takeIf { it.isNotEmpty() },
            mothersName = _mothersName.value.trim().takeIf { it.isNotEmpty() }
        )

        studentRepository.registerStudent(request).collect { resource ->
            when {
                resource.isSuccess -> {
                    emitMsg("Student registered successfully!")
                    // Addiong to DAO after registration
                    studentDao.upsertStudentDetails(
                        request.toStudent()
                    )
                    _submissionSuccessful.update { true }
                    _isLoading.update { false }
                }
                resource.isFailed -> {
                    emitError(resource.error)
                    _isLoading.update { false }
                }
            }
        }
    }

    private suspend fun updateExistingStudent() {
        val request = UpdateStudentRequest(
            pid = _existingPid.value!!,
            firstName = _firstName.value.trim(),
            lastName = _lastName.value.trim(),
            yearOfBirth = _yearOfBirth.value,
            gender = _gender.value,
            schoolClass = _schoolClass.value.trim().takeIf { it.isNotEmpty() },
            villageId = _selectedVillageId.value,
            groupId = _selectedGroupId.value,
            primaryContactNo = _primaryContactNo.value.trim().takeIf { it.isNotEmpty() },
            secondaryContactNo = _secondaryContactNo.value.trim().takeIf { it.isNotEmpty() },
            fathersName = _fathersName.value.trim().takeIf { it.isNotEmpty() },
            mothersName = _mothersName.value.trim().takeIf { it.isNotEmpty() }
        )

        studentRepository.updateStudent(request).collect { resource ->
            when {
                resource.isSuccess -> {
                    emitMsg("Student details updated successfully!")
                    _submissionSuccessful.update { true }
                    _isLoading.update { false }
                }
                resource.isFailed -> {
                    emitError(resource.error)
                    _isLoading.update { false }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (_firstName.value.trim().isEmpty()) {
            errors["firstName"] = "First name is required"
        }

        if (_lastName.value.trim().isEmpty()) {
            errors["lastName"] = "Last name is required"
        }

        if (_gender.value == null) {
            errors["gender"] = "Gender is required"
        }

        if (_selectedVillageId.value == null) {
            errors["village"] = "Village is required"
        }

        if (_selectedGroupId.value == null) {
            errors["group"] = "Group is required"
        }

        if (_primaryContactNo.value.trim().isNotEmpty() && _primaryContactNo.value.trim().length != 10) {
            errors["primaryContactNo"] = "Contact number must be 10 digits"
        }

        if (_secondaryContactNo.value.trim().isNotEmpty() && _secondaryContactNo.value.trim().length != 10) {
            errors["secondaryContactNo"] = "Contact number must be 10 digits"
        }

        _formErrors.update { errors }
        return errors.isEmpty()
    }

    fun resetSubmissionStatus() {
        _submissionSuccessful.update { false }
    }
}
