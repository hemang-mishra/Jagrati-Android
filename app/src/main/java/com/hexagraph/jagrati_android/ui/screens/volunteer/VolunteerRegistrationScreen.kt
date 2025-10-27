package com.hexagraph.jagrati_android.ui.screens.volunteer

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.util.AppPreferences
import org.koin.compose.koinInject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun VolunteerRegistrationScreen(
    viewModel: VolunteerRequestViewModel,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    navigateToMyRequests: () -> Unit,
    appPreferences: AppPreferences = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isVolunteer by appPreferences.isVolunteer.getFlow().collectAsState(initial = false)
    var showAlreadyVolunteerDialog by remember { mutableStateOf(false) }

    // Check if user is already a volunteer
    LaunchedEffect(key1 = isVolunteer) {
        if (isVolunteer) {
            showAlreadyVolunteerDialog = true
        }
    }

    // Show dialog if user is already a volunteer
    if (showAlreadyVolunteerDialog) {
        AlertDialog(
            onDismissRequest = {
                showAlreadyVolunteerDialog = false
                onBackPressed()
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Already Volunteer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Already a Volunteer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "You are already registered as a volunteer! You don't need to submit another registration request. You can access all volunteer features from the home screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlreadyVolunteerDialog = false
                        onBackPressed()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Got it")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(message = error.toast)
            viewModel.clearErrorFlow()
        }
    }

    LaunchedEffect(key1 = uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message = message)
            viewModel.clearMsgFlow()
        }
    }

    LaunchedEffect(key1 = uiState.submissionSuccessful) {
        if (uiState.submissionSuccessful) {
            // Reset submission status to avoid multiple navigations
            viewModel.resetSubmissionStatus()

            // Navigate to My Requests screen after successful submission
            navigateToMyRequests()
        }
    }

    LaunchedEffect(key1 = uiState.hasPendingRequests) {
        // Check if user has any pending requests when entering this screen
        if (uiState.hasPendingRequests) {
            Toast.makeText(
                context,
                "You have pending volunteer requests. Redirecting to My Requests.",
                Toast.LENGTH_LONG
            ).show()
            navigateToMyRequests()
        }
    }

    // Only show the form if user is not already a volunteer
    if (!isVolunteer) {
        VolunteerRegistrationScreenLayout(
            uiState = uiState,
            onBackPressed = onBackPressed,
            onFirstNameChanged = viewModel::updateFirstName,
            onLastNameChanged = viewModel::updateLastName,
            onGenderChanged = viewModel::updateGender,
            onRollNumberChanged = viewModel::updateRollNumber,
            onAlternateEmailChanged = viewModel::updateAlternateEmail,
            onBatchChanged = viewModel::updateBatch,
            onProgrammeChanged = viewModel::updateProgramme,
            onStreetAddress1Changed = viewModel::updateStreetAddress1,
            onStreetAddress2Changed = viewModel::updateStreetAddress2,
            onPincodeChanged = viewModel::updatePincode,
            onCityChanged = viewModel::updateCity,
            onStateChanged = viewModel::updateState,
            onDateOfBirthChanged = viewModel::updateDateOfBirth,
            onContactNumberChanged = viewModel::updateContactNumber,
            onCollegeChanged = viewModel::updateCollege,
            onBranchChanged = viewModel::updateBranch,
            onYearOfStudyChanged = viewModel::updateYearOfStudy,
            onSubmitClicked = viewModel::submitVolunteerRequest
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerRegistrationScreenLayout(
    uiState: VolunteerRequestUiState,
    onBackPressed: () -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onGenderChanged: (String) -> Unit,
    onRollNumberChanged: (String) -> Unit,
    onAlternateEmailChanged: (String) -> Unit,
    onBatchChanged: (String) -> Unit,
    onProgrammeChanged: (String) -> Unit,
    onStreetAddress1Changed: (String) -> Unit,
    onStreetAddress2Changed: (String) -> Unit,
    onPincodeChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onStateChanged: (String) -> Unit,
    onDateOfBirthChanged: (LocalDate?) -> Unit,
    onContactNumberChanged: (String) -> Unit,
    onCollegeChanged: (String) -> Unit,
    onBranchChanged: (String) -> Unit,
    onYearOfStudyChanged: (Int?) -> Unit,
    onSubmitClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Volunteer Registration",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Form header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Join as a Volunteer",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please fill out the form below to apply as a volunteer",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Personal information section
                FormSection(title = "Personal Information") {
                    // First Name
                    FormField(
                        label = "First Name",
                        value = uiState.firstName,
                        onValueChange = onFirstNameChanged,
                        error = uiState.formErrors["firstName"],
                        isRequired = true,
                        enabled = false
                    )

                    // Last Name
                    FormField(
                        label = "Last Name",
                        value = uiState.lastName,
                        onValueChange = onLastNameChanged,
                        error = uiState.formErrors["lastName"],
                        isRequired = true,
                        enabled = false
                    )

                    // Gender selection
                    GenderSelectionField(
                        selectedGender = uiState.gender,
                        onGenderSelected = onGenderChanged,
                        error = uiState.formErrors["gender"]
                    )

                    // Date of Birth
                    DatePickerField(
                        selectedDate = uiState.dateOfBirth,
                        onDateSelected = onDateOfBirthChanged,
                        error = uiState.formErrors["dateOfBirth"]
                    )

                    // Contact Number
                    FormField(
                        label = "WhatsApp Number",
                        value = uiState.contactNumber,
                        onValueChange = onContactNumberChanged,
                        error = uiState.formErrors["contactNumber"],
                        isRequired = true,
                        keyboardType = KeyboardType.Phone,
                        maxLength = 10
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Academic information section
                FormSection(title = "Academic Information") {
                    // College
                    FormField(
                        label = "College",
                        value = uiState.college,
                        onValueChange = onCollegeChanged,
                        error = uiState.formErrors["college"],
                        isRequired = true
                    )

                    // Roll Number
                    FormField(
                        label = "Roll Number",
                        value = uiState.rollNumber,
                        onValueChange = onRollNumberChanged,
                        error = uiState.formErrors["rollNumber"],
                        isRequired = true
                    )

                    // Alternate Email
                    FormField(
                        label = "Alternate Email (Optional)",
                        value = uiState.alternateEmail,
                        onValueChange = onAlternateEmailChanged,
                        error = uiState.formErrors["alternateEmail"],
                        isRequired = false,
                        keyboardType = KeyboardType.Email
                    )

                    // Batch Dropdown (last 10 years)
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val batchYears = (0..9).map { (currentYear - it).toString() }

                    DropdownField(
                        label = "Batch",
                        selectedItem = uiState.batch,
                        onItemSelected = onBatchChanged,
                        items = batchYears,
                        error = uiState.formErrors["batch"]
                    )

                    // Programme Dropdown
                    val programmes = listOf("B.Tech", "B.Des", "M.Tech", "M.Des", "PhD", "Other")

                    DropdownField(
                        label = "Programme",
                        selectedItem = uiState.programme,
                        onItemSelected = onProgrammeChanged,
                        items = programmes,
                        error = uiState.formErrors["programme"]
                    )

                    // Branch Dropdown
                    val branches = listOf("CSE", "ECE", "ME", "SM", "B.Des")

                    DropdownField(
                        label = "Branch",
                        selectedItem = uiState.branch,
                        onItemSelected = onBranchChanged,
                        items = branches,
                        error = uiState.formErrors["branch"]
                    )

                    // Year of Study Dropdown
                    val yearOptions = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
                    val yearValues = listOf(1, 2, 3, 4)

                    DropdownField(
                        label = "Year of Study",
                        selectedItem = uiState.yearOfStudy?.let { yearOptions[yearValues.indexOf(it)] } ?: "",
                        onItemSelected = { selected ->
                            val index = yearOptions.indexOf(selected)
                            if (index != -1) {
                                onYearOfStudyChanged(yearValues[index])
                            }
                        },
                        items = yearOptions,
                        error = uiState.formErrors["yearOfStudy"]
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Address section
                FormSection(title = "Address Information") {
                    // Street Address 1
                    FormField(
                        label = "Street Address 1",
                        value = uiState.streetAddress1,
                        onValueChange = onStreetAddress1Changed,
                        error = uiState.formErrors["streetAddress1"],
                        isRequired = true
                    )

                    // Street Address 2
                    FormField(
                        label = "Street Address 2 (Optional)",
                        value = uiState.streetAddress2,
                        onValueChange = onStreetAddress2Changed,
                        error = uiState.formErrors["streetAddress2"],
                        isRequired = false
                    )

                    // City
                    FormField(
                        label = "City",
                        value = uiState.city,
                        onValueChange = onCityChanged,
                        error = uiState.formErrors["city"],
                        isRequired = true
                    )

                    // State
                    FormField(
                        label = "State",
                        value = uiState.state,
                        onValueChange = onStateChanged,
                        error = uiState.formErrors["state"],
                        isRequired = true
                    )

                    // Pincode
                    FormField(
                        label = "Pincode",
                        value = uiState.pincode,
                        onValueChange = onPincodeChanged,
                        error = uiState.formErrors["pincode"],
                        isRequired = true,
                        keyboardType = KeyboardType.Number,
                        maxLength = 6,
                        imeAction = ImeAction.Done
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = onSubmitClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit Application",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Loading overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isRequired: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int? = null,
    imeAction: ImeAction = ImeAction.Next
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label + if (isRequired) " *" else "",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (maxLength == null || newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            isError = error != null,
            singleLine = true,
            enabled = enabled,
            shape = RoundedCornerShape(8.dp)
        )

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun GenderSelectionField(
    selectedGender: String?,
    onGenderSelected: (String) -> Unit,
    error: String?
) {
    val genderOptions = listOf("MALE", "FEMALE", "OTHER")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Gender *",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            genderOptions.forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onGenderSelected(gender) }
                ) {
                    RadioButton(
                        selected = selectedGender == gender,
                        onClick = { onGenderSelected(gender) }
                    )
                    Text(
                        text = gender.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    error: String?
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Date of Birth *",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
            ),
            onClick = { showDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedDate?.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    ) ?: "Select a date",
                    color = if (selectedDate == null) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    painter = painterResource(R.drawable.ic_calendar_month),
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(localDate)
                    }
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DropdownField(
    label: String,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    items: List<String>,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label *",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
            ),
            onClick = { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedItem.ifEmpty { "Select $label" },
                    color = if (selectedItem.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select $label",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(animationSpec = tween(150)),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VolunteerRegistrationPreview() {
    JagratiAndroidTheme {
        val previewState = VolunteerRequestUiState(
            firstName = "John",
            lastName = "Doe",
            gender = "MALE",
            rollNumber = "2021DCS001",
            alternateEmail = "john.doe@example.com",
            batch = "2021",
            programme = "B.Tech",
            streetAddress1 = "123 Main St",
            streetAddress2 = "Apartment 4B",
            pincode = "482001",
            city = "Jabalpur",
            state = "Madhya Pradesh",
            dateOfBirth = LocalDate.of(2000, 1, 15),
            contactNumber = "9876543210",
            college = "IIITDM Jabalpur",
            branch = "CSE",
            yearOfStudy = 3,
            formErrors = mapOf(
                "pincode" to "Please enter a valid 6-digit pincode"
            )
        )

        VolunteerRegistrationScreenLayout(
            uiState = previewState,
            onBackPressed = {},
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onGenderChanged = {},
            onRollNumberChanged = {},
            onAlternateEmailChanged = {},
            onBatchChanged = {},
            onProgrammeChanged = {},
            onStreetAddress1Changed = {},
            onStreetAddress2Changed = {},
            onPincodeChanged = {},
            onCityChanged = {},
            onStateChanged = {},
            onDateOfBirthChanged = {},
            onContactNumberChanged = {},
            onCollegeChanged = {},
            onBranchChanged = {},
            onYearOfStudyChanged = {},
            onSubmitClicked = {}
        )
    }
}
