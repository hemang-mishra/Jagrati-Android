package com.hexagraph.jagrati_android.ui.screens.volunteer

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.volunteer.CreateVolunteerRequest
import com.hexagraph.jagrati_android.model.volunteer.MyVolunteerRequestResponse
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRequestRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VolunteerRequestViewModel(
    private val volunteerRequestRepository: VolunteerRequestRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel<VolunteerRequestUiState>() {

    private val _myRequests = MutableStateFlow<List<MyVolunteerRequestResponse>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _hasVolunteerRole = MutableStateFlow(false)
    private val _hasPendingRequests = MutableStateFlow(false)

    // Form fields
    private val _firstName = MutableStateFlow("")
    private val _lastName = MutableStateFlow("")
    private val _gender = MutableStateFlow<String?>(null)
    private val _rollNumber = MutableStateFlow("")
    private val _alternateEmail = MutableStateFlow("")
    private val _batch = MutableStateFlow("")
    private val _programme = MutableStateFlow("B.Tech")
    private val _streetAddress1 = MutableStateFlow("")
    private val _streetAddress2 = MutableStateFlow("")
    private val _pincode = MutableStateFlow("")
    private val _city = MutableStateFlow("")
    private val _state = MutableStateFlow("")
    private val _dateOfBirth = MutableStateFlow<LocalDate?>(null)
    private val _contactNumber = MutableStateFlow("")
    private val _college = MutableStateFlow("IIITDM Jabalpur")
    private val _branch = MutableStateFlow("")
    private val _yearOfStudy = MutableStateFlow<Int?>(null)

    // Form validation
    private val _formErrors = MutableStateFlow<Map<String, String>>(emptyMap())

    // Success flag
    private val _submissionSuccessful = MutableStateFlow(false)

    override val uiState: StateFlow<VolunteerRequestUiState> = createUiStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            checkVolunteerRole()
            loadMyVolunteerRequests()
            _firstName.update {
                appPreferences.userDetails.get()?.firstName ?: ""
            }
            _lastName.update {
                appPreferences.userDetails.get()?.lastName ?: ""
            }
        }
    }

    override fun createUiStateFlow(): StateFlow<VolunteerRequestUiState> {
        return combine(
            _myRequests,
            _isLoading,
            _hasVolunteerRole,
            _hasPendingRequests,
            _firstName,
            _lastName,
            _gender,
            _rollNumber,
            _alternateEmail,
            _batch,
            _programme,
            _streetAddress1,
            _streetAddress2,
            _pincode,
            _city,
            _state,
            _dateOfBirth,
            _contactNumber,
            _college,
            _branch,
            _yearOfStudy,
            _formErrors,
            _submissionSuccessful,
            errorFlow,
            successMsgFlow
        ) { flows ->
            val myRequests = flows[0] as List<MyVolunteerRequestResponse>
            val isLoading = flows[1] as Boolean
            val hasVolunteerRole = flows[2] as Boolean
            val hasPendingRequests = flows[3] as Boolean
            val firstName = flows[4] as String
            val lastName = flows[5] as String
            val gender = flows[6] as String?
            val rollNumber = flows[7] as String
            val alternateEmail = flows[8] as String
            val batch = flows[9] as String
            val programme = flows[10] as String
            val streetAddress1 = flows[11] as String
            val streetAddress2 = flows[12] as String
            val pincode = flows[13] as String
            val city = flows[14] as String
            val state = flows[15] as String
            val dateOfBirth = flows[16] as LocalDate?
            val contactNumber = flows[17] as String
            val college = flows[18] as String
            val branch = flows[19] as String
            val yearOfStudy = flows[20] as Int?
            val formErrors = flows[21] as Map<String, String>
            val submissionSuccessful = flows[22] as Boolean
            val error = flows[23] as ResponseError?
            val successMsg = flows[24] as String?

            VolunteerRequestUiState(
                myRequests = myRequests,
                isLoading = isLoading,
                hasVolunteerRole = hasVolunteerRole,
                hasPendingRequests = hasPendingRequests,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                rollNumber = rollNumber,
                alternateEmail = alternateEmail,
                batch = batch,
                programme = programme,
                streetAddress1 = streetAddress1,
                streetAddress2 = streetAddress2,
                pincode = pincode,
                city = city,
                state = state,
                dateOfBirth = dateOfBirth,
                contactNumber = contactNumber,
                college = college,
                branch = branch,
                yearOfStudy = yearOfStudy,
                formErrors = formErrors,
                submissionSuccessful = submissionSuccessful,
                error = error,
                successMessage = successMsg
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VolunteerRequestUiState()
        )
    }

    fun loadMyVolunteerRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            volunteerRequestRepository.getMyVolunteerRequests().collect { resource ->
                when {
                    resource.isLoading -> {
                        // Already handled at start
                    }
                    resource.isSuccess -> {
                        resource.data?.let { response ->
                            _myRequests.value = response.requests
                            _hasPendingRequests.value = response.requests.any {
                                it.status == "PENDING"
                            }
                        }
                        _isLoading.value = false
                    }
                    resource.isFailed -> {
                        emitError(resource.error)
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    private suspend fun checkVolunteerRole() {
        val roles = appPreferences.userRoles.getFlow().firstOrNull() ?: emptyList()
        _hasVolunteerRole.value = roles.any { it.name == "VOLUNTEER" }
    }

    // Form update methods
    fun updateFirstName(value: String) {
        _firstName.value = value
        validateField("firstName", value)
    }

    fun updateLastName(value: String) {
        _lastName.value = value
        validateField("lastName", value)
    }

    fun updateGender(value: String) {
        _gender.value = value
        validateField("gender", value)
    }

    fun updateRollNumber(value: String) {
        _rollNumber.value = value
        validateField("rollNumber", value)
    }

    fun updateAlternateEmail(value: String) {
        _alternateEmail.value = value
        validateField("alternateEmail", value)
    }

    fun updateBatch(value: String) {
        _batch.value = value
        validateField("batch", value)
    }

    fun updateProgramme(value: String) {
        _programme.value = value
        validateField("programme", value)
    }

    fun updateStreetAddress1(value: String) {
        _streetAddress1.value = value
        validateField("streetAddress1", value)
    }

    fun updateStreetAddress2(value: String) {
        _streetAddress2.value = value
        // Optional field, no validation needed
    }

    fun updatePincode(value: String) {
        _pincode.value = value
        validateField("pincode", value)
    }

    fun updateCity(value: String) {
        _city.value = value
        validateField("city", value)
    }

    fun updateState(value: String) {
        _state.value = value
        validateField("state", value)
    }

    fun updateDateOfBirth(value: LocalDate?) {
        _dateOfBirth.value = value
        validateField("dateOfBirth", value?.toString() ?: "")
    }

    fun updateContactNumber(value: String) {
        _contactNumber.value = value
        validateField("contactNumber", value)
    }

    fun updateCollege(value: String) {
        _college.value = value
        validateField("college", value)
    }

    fun updateBranch(value: String) {
        _branch.value = value
        validateField("branch", value)
    }

    fun updateYearOfStudy(value: Int?) {
        _yearOfStudy.value = value
        validateField("yearOfStudy", value?.toString() ?: "")
    }

    private fun validateField(fieldName: String, value: String) {
        val errors = _formErrors.value.toMutableMap()

        when (fieldName) {
            "firstName", "lastName", "city", "state", "college", "branch" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "This field is required"
                } else {
                    errors.remove(fieldName)
                }
            }
            "gender" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Please select a gender"
                } else {
                    errors.remove(fieldName)
                }
            }
            "rollNumber" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Roll number is required"
                } else {
                    errors.remove(fieldName)
                }
            }
            "alternateEmail" -> {
                if (value.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                    errors[fieldName] = "Please enter a valid email address"
                } else {
                    errors.remove(fieldName)
                }
            }
            "batch" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Please select a batch"
                } else {
                    errors.remove(fieldName)
                }
            }
            "streetAddress1" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Address is required"
                } else {
                    errors.remove(fieldName)
                }
            }
            "pincode" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Pincode is required"
                } else if (value.length != 6 || !value.all { it.isDigit() }) {
                    errors[fieldName] = "Please enter a valid 6-digit pincode"
                } else {
                    errors.remove(fieldName)
                }
            }
            "dateOfBirth" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Date of birth is required"
                } else {
                    errors.remove(fieldName)
                }
            }
            "contactNumber" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Contact number is required"
                } else if (value.length != 10 || !value.all { it.isDigit() }) {
                    errors[fieldName] = "Please enter a valid 10-digit contact number"
                } else {
                    errors.remove(fieldName)
                }
            }
            "yearOfStudy" -> {
                if (value.isBlank()) {
                    errors[fieldName] = "Please select year of study"
                } else {
                    errors.remove(fieldName)
                }
            }
        }

        _formErrors.value = errors
    }

    fun validateForm(): Boolean {
        // Validate all fields
        validateField("firstName", _firstName.value)
        validateField("lastName", _lastName.value)
        validateField("gender", _gender.value ?: "")
        validateField("rollNumber", _rollNumber.value)
        if (_alternateEmail.value.isNotBlank()) {
            validateField("alternateEmail", _alternateEmail.value)
        }
        validateField("batch", _batch.value)
        validateField("programme", _programme.value)
        validateField("streetAddress1", _streetAddress1.value)
        validateField("pincode", _pincode.value)
        validateField("city", _city.value)
        validateField("state", _state.value)
        validateField("dateOfBirth", _dateOfBirth.value?.toString() ?: "")
        validateField("contactNumber", _contactNumber.value)
        validateField("college", _college.value)
        validateField("branch", _branch.value)
        validateField("yearOfStudy", _yearOfStudy.value?.toString() ?: "")

        return _formErrors.value.isEmpty()
    }

    fun submitVolunteerRequest() {
        if (!validateForm()) {
            emitError(ResponseError.BAD_REQUEST.apply {
                actualResponse = "Please fix the errors in the form before submitting"
            })
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedDate = _dateOfBirth.value?.format(dateFormatter) ?: ""

            val request = CreateVolunteerRequest(
                firstName = _firstName.value,
                lastName = _lastName.value,
                gender = _gender.value ?: "",
                rollNumber = _rollNumber.value.takeIf { it.isNotBlank() },
                alternateEmail = _alternateEmail.value.takeIf { it.isNotBlank() },
                batch = _batch.value.takeIf { it.isNotBlank() },
                programme = _programme.value.takeIf { it.isNotBlank() },
                streetAddress1 = _streetAddress1.value.takeIf { it.isNotBlank() },
                streetAddress2 = _streetAddress2.value.takeIf { it.isNotBlank() },
                pincode = _pincode.value.takeIf { it.isNotBlank() },
                city = _city.value.takeIf { it.isNotBlank() },
                state = _state.value.takeIf { it.isNotBlank() },
                dateOfBirth = formattedDate,
                contactNumber = _contactNumber.value.takeIf { it.isNotBlank() },
                college = _college.value.takeIf { it.isNotBlank() },
                branch = _branch.value.takeIf { it.isNotBlank() },
                yearOfStudy = _yearOfStudy.value
            )

            volunteerRequestRepository.createVolunteerRequest(request).collect { resource ->
                when {
                    resource.isLoading -> {
                        // Already handled at start
                    }
                    resource.isSuccess -> {
                        _isLoading.value = false
                        _submissionSuccessful.value = true
                        emitMsg("Volunteer request submitted successfully")
                        loadMyVolunteerRequests() // Refresh the list
                    }
                    resource.isFailed -> {
                        _isLoading.value = false
                        emitError(resource.error)
                    }
                }
            }
        }
    }

    fun resetSubmissionStatus() {
        _submissionSuccessful.value = false
    }
}

data class VolunteerRequestUiState(
    val myRequests: List<MyVolunteerRequestResponse> = emptyList(),
    val isLoading: Boolean = false,
    val hasVolunteerRole: Boolean = false,
    val hasPendingRequests: Boolean = false,

    // Form fields
    val firstName: String = "",
    val lastName: String = "",
    val gender: String? = null,
    val rollNumber: String = "",
    val alternateEmail: String = "",
    val batch: String = "",
    val programme: String = "B.Tech",
    val streetAddress1: String = "",
    val streetAddress2: String = "",
    val pincode: String = "",
    val city: String = "",
    val state: String = "",
    val dateOfBirth: LocalDate? = null,
    val contactNumber: String = "",
    val college: String = "IIITDM Jabalpur",
    val branch: String = "",
    val yearOfStudy: Int? = null,

    // Validation
    val formErrors: Map<String, String> = emptyMap(),
    val submissionSuccessful: Boolean = false,

    // Error handling
    val error: ResponseError? = null,
    val successMessage: String? = null
)
