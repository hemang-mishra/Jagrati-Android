package com.hexagraph.jagrati_android.ui.screens.editvolunteerprofile

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.model.volunteer.UpdateVolunteerRequest
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditVolunteerProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val volunteer: VolunteerDTO? = null,

    // Form fields
    val firstName: String = "",
    val lastName: String = "",
    val rollNumber: String = "",
    val gender: Gender? = null,
    val dateOfBirth: String = "",
    val contactNumber: String = "",
    val alternateEmail: String = "",
    val college: String = "",
    val programme: String = "",
    val branch: String = "",
    val batch: String = "",
    val yearOfStudy: String = "",
    val streetAddress1: String = "",
    val streetAddress2: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",

    // Validation
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val dateOfBirthError: String? = null,
    val contactNumberError: String? = null,
    val pincodeError: String? = null,
    val yearOfStudyError: String? = null,

    val error: ResponseError? = null,
    val successMessage: String? = null
)

class EditVolunteerProfileViewModel(
    private val pid: String,
    private val volunteerRepository: VolunteerRepository
) : BaseViewModel<EditVolunteerProfileUiState>() {

    private val _isLoading = MutableStateFlow(false)
    private val _isSaving = MutableStateFlow(false)
    private val _volunteer = MutableStateFlow<VolunteerDTO?>(null)

    private val _firstName = MutableStateFlow("")
    private val _lastName = MutableStateFlow("")
    private val _rollNumber = MutableStateFlow("")
    private val _gender = MutableStateFlow<Gender?>(null)
    private val _dateOfBirth = MutableStateFlow("")
    private val _contactNumber = MutableStateFlow("")
    private val _alternateEmail = MutableStateFlow("")
    private val _college = MutableStateFlow("")
    private val _programme = MutableStateFlow("")
    private val _branch = MutableStateFlow("")
    private val _batch = MutableStateFlow("")
    private val _yearOfStudy = MutableStateFlow("")
    private val _streetAddress1 = MutableStateFlow("")
    private val _streetAddress2 = MutableStateFlow("")
    private val _city = MutableStateFlow("")
    private val _state = MutableStateFlow("")
    private val _pincode = MutableStateFlow("")

    private val _firstNameError = MutableStateFlow<String?>(null)
    private val _lastNameError = MutableStateFlow<String?>(null)
    private val _dateOfBirthError = MutableStateFlow<String?>(null)
    private val _contactNumberError = MutableStateFlow<String?>(null)
    private val _pincodeError = MutableStateFlow<String?>(null)
    private val _yearOfStudyError = MutableStateFlow<String?>(null)

    override val uiState: StateFlow<EditVolunteerProfileUiState> = createUiStateFlow()

    init {
        loadVolunteerData()
    }

    override fun createUiStateFlow(): StateFlow<EditVolunteerProfileUiState> {
        return combine(
            _isLoading,
            _isSaving,
            _volunteer,
            _firstName,
            _lastName,
            _rollNumber,
            _gender,
            _dateOfBirth,
            _contactNumber,
            _alternateEmail,
            _college,
            _programme,
            _branch,
            _batch,
            _yearOfStudy,
            _streetAddress1,
            _streetAddress2,
            _city,
            _state,
            _pincode,
            _firstNameError,
            _lastNameError,
            _dateOfBirthError,
            _contactNumberError,
            _pincodeError,
            _yearOfStudyError,
            errorFlow,
            successMsgFlow
        ) { flows ->
            EditVolunteerProfileUiState(
                isLoading = flows[0] as Boolean,
                isSaving = flows[1] as Boolean,
                volunteer = flows[2] as VolunteerDTO?,
                firstName = flows[3] as String,
                lastName = flows[4] as String,
                rollNumber = flows[5] as String,
                gender = flows[6] as Gender?,
                dateOfBirth = flows[7] as String,
                contactNumber = flows[8] as String,
                alternateEmail = flows[9] as String,
                college = flows[10] as String,
                programme = flows[11] as String,
                branch = flows[12] as String,
                batch = flows[13] as String,
                yearOfStudy = flows[14] as String,
                streetAddress1 = flows[15] as String,
                streetAddress2 = flows[16] as String,
                city = flows[17] as String,
                state = flows[18] as String,
                pincode = flows[19] as String,
                firstNameError = flows[20] as String?,
                lastNameError = flows[21] as String?,
                dateOfBirthError = flows[22] as String?,
                contactNumberError = flows[23] as String?,
                pincodeError = flows[24] as String?,
                yearOfStudyError = flows[25] as String?,
                error = flows[26] as ResponseError?,
                successMessage = flows[27] as String?
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EditVolunteerProfileUiState()
        )
    }

    private fun loadVolunteerData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }

            volunteerRepository.getVolunteerByPid(pid).collect { resource ->
                when {
                    resource.isSuccess -> {
                        resource.data?.let { volunteer ->
                            _volunteer.update { volunteer }
                            populateFormFields(volunteer)
                        }
                    }
                    resource.isFailed -> {
                        emitError(resource.error ?: ResponseError.UNKNOWN)
                    }
                }
            }

            _isLoading.update { false }
        }
    }

    private fun populateFormFields(volunteer: VolunteerDTO) {
        _firstName.update { volunteer.firstName }
        _lastName.update { volunteer.lastName }
        _rollNumber.update { volunteer.rollNumber ?: "" }
        _gender.update { volunteer.gender }
        _dateOfBirth.update { volunteer.dateOfBirth }
        _contactNumber.update { volunteer.contactNumber ?: "" }
        _alternateEmail.update { volunteer.alternateEmail ?: "" }
        _college.update { volunteer.college ?: "" }
        _programme.update { volunteer.programme ?: "" }
        _branch.update { volunteer.branch ?: "" }
        _batch.update { volunteer.batch ?: "" }
        _yearOfStudy.update { volunteer.yearOfStudy?.toString() ?: "" }
        _streetAddress1.update { volunteer.streetAddress1 ?: "" }
        _streetAddress2.update { volunteer.streetAddress2 ?: "" }
        _city.update { volunteer.city ?: "" }
        _state.update { volunteer.state ?: "" }
        _pincode.update { volunteer.pincode ?: "" }
    }

    fun updateFirstName(value: String) {
        _firstName.update { value }
        _firstNameError.update { null }
    }

    fun updateLastName(value: String) {
        _lastName.update { value }
        _lastNameError.update { null }
    }

    fun updateRollNumber(value: String) {
        _rollNumber.update { value }
    }

    fun updateGender(value: Gender) {
        _gender.update { value }
    }

    fun updateDateOfBirth(value: String) {
        _dateOfBirth.update { value }
        _dateOfBirthError.update { null }
    }

    fun updateContactNumber(value: String) {
        _contactNumber.update { value }
        _contactNumberError.update { null }
    }

    fun updateAlternateEmail(value: String) {
        _alternateEmail.update { value }
    }

    fun updateCollege(value: String) {
        _college.update { value }
    }

    fun updateProgramme(value: String) {
        _programme.update { value }
    }

    fun updateBranch(value: String) {
        _branch.update { value }
    }

    fun updateBatch(value: String) {
        _batch.update { value }
    }

    fun updateYearOfStudy(value: String) {
        _yearOfStudy.update { value }
        _yearOfStudyError.update { null }
    }

    fun updateStreetAddress1(value: String) {
        _streetAddress1.update { value }
    }

    fun updateStreetAddress2(value: String) {
        _streetAddress2.update { value }
    }

    fun updateCity(value: String) {
        _city.update { value }
    }

    fun updateState(value: String) {
        _state.update { value }
    }

    fun updatePincode(value: String) {
        _pincode.update { value }
        _pincodeError.update { null }
    }

    fun saveProfile() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.update { true }

            val updateRequest = UpdateVolunteerRequest(
                rollNumber = _rollNumber.value.ifBlank { null },
                firstName = _firstName.value,
                lastName = _lastName.value,
                gender = _gender.value,
                alternateEmail = _alternateEmail.value.ifBlank { null },
                batch = _batch.value.ifBlank { null },
                programme = _programme.value.ifBlank { null },
                streetAddress1 = _streetAddress1.value.ifBlank { null },
                streetAddress2 = _streetAddress2.value.ifBlank { null },
                pincode = _pincode.value.ifBlank { null },
                city = _city.value.ifBlank { null },
                state = _state.value.ifBlank { null },
                dateOfBirth = _dateOfBirth.value,
                contactNumber = _contactNumber.value.ifBlank { null },
                college = _college.value.ifBlank { null },
                branch = _branch.value.ifBlank { null },
                profilePic = _volunteer.value?.profilePic,
                yearOfStudy = _yearOfStudy.value.toIntOrNull()
            )

            volunteerRepository.updateMyDetails(updateRequest).collect { resource ->
                when {
                    resource.isSuccess -> {
                        emitMsg("Profile updated successfully")
                        _isSaving.update { false }
                    }
                    resource.isFailed -> {
                        emitError(resource.error ?: ResponseError.UNKNOWN)
                        _isSaving.update { false }
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (_firstName.value.isBlank()) {
            _firstNameError.update { "First name is required" }
            isValid = false
        }

        if (_lastName.value.isBlank()) {
            _lastNameError.update { "Last name is required" }
            isValid = false
        }

        if (_dateOfBirth.value.isBlank()) {
            _dateOfBirthError.update { "Date of birth is required" }
            isValid = false
        }

        if (_contactNumber.value.isNotBlank() && !_contactNumber.value.matches(Regex("^[0-9]{10}$"))) {
            _contactNumberError.update { "Contact number must be 10 digits" }
            isValid = false
        }

        if (_pincode.value.isNotBlank() && !_pincode.value.matches(Regex("^[0-9]{6}$"))) {
            _pincodeError.update { "Pincode must be 6 digits" }
            isValid = false
        }

        if (_yearOfStudy.value.isNotBlank()) {
            val year = _yearOfStudy.value.toIntOrNull()
            if (year == null || year < 1 || year > 7) {
                _yearOfStudyError.update { "Year of study must be between 1 and 7" }
                isValid = false
            }
        }

        return isValid
    }
}

