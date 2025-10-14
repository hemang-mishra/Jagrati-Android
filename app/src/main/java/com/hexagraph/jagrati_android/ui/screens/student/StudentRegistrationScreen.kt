package com.hexagraph.jagrati_android.ui.screens.student

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import java.util.Calendar

@Composable
fun StudentRegistrationScreen(
    viewModel: StudentRegistrationViewModel,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    navigateToFacialData: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(message = error.genericToast)
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
            viewModel.resetSubmissionStatus()

            if (uiState.isUpdateMode) {
                Toast.makeText(context, "Student updated successfully", Toast.LENGTH_SHORT).show()
                onBackPressed()
            } else {
                Toast.makeText(context, "Feature yet to be implemented: Add facial data", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
    }

    StudentRegistrationScreenLayout(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onFirstNameChanged = viewModel::updateFirstName,
        onLastNameChanged = viewModel::updateLastName,
        onGenderChanged = viewModel::updateGender,
        onYearOfBirthChanged = viewModel::updateYearOfBirth,
        onSchoolClassChanged = viewModel::updateSchoolClass,
        onPrimaryContactNoChanged = viewModel::updatePrimaryContactNo,
        onSecondaryContactNoChanged = viewModel::updateSecondaryContactNo,
        onFathersNameChanged = viewModel::updateFathersName,
        onMothersNameChanged = viewModel::updateMothersName,
        onVillageSelected = viewModel::updateSelectedVillageId,
        onGroupSelected = viewModel::updateSelectedGroupId,
        onSubmitClicked = viewModel::submitStudent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreenLayout(
    uiState: StudentRegistrationUiState,
    onBackPressed: () -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onGenderChanged: (String) -> Unit,
    onYearOfBirthChanged: (Int?) -> Unit,
    onSchoolClassChanged: (String) -> Unit,
    onPrimaryContactNoChanged: (String) -> Unit,
    onSecondaryContactNoChanged: (String) -> Unit,
    onFathersNameChanged: (String) -> Unit,
    onMothersNameChanged: (String) -> Unit,
    onVillageSelected: (Long) -> Unit,
    onGroupSelected: (Long) -> Unit,
    onSubmitClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isUpdateMode) "Update Student" else "Student Registration",
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
                            text = if (uiState.isUpdateMode) "Update Student Details" else "Register New Student",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.isUpdateMode)
                                "Update the student information below"
                            else
                                "Please fill out the form to register a new student",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                StudentFormSection(title = "Personal Information") {
                    StudentFormField(
                        label = "First Name",
                        value = uiState.firstName,
                        onValueChange = onFirstNameChanged,
                        error = uiState.formErrors["firstName"],
                        isRequired = true
                    )

                    StudentFormField(
                        label = "Last Name",
                        value = uiState.lastName,
                        onValueChange = onLastNameChanged,
                        error = uiState.formErrors["lastName"],
                        isRequired = true
                    )

                    StudentGenderSelectionField(
                        selectedGender = uiState.gender,
                        onGenderSelected = onGenderChanged,
                        error = uiState.formErrors["gender"]
                    )

                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val birthYears = (currentYear downTo currentYear - 20).map { it.toString() }

                    StudentDropdownField(
                        label = "Year of Birth",
                        selectedItem = uiState.yearOfBirth?.toString() ?: "",
                        onItemSelected = { year ->
                            onYearOfBirthChanged(year.toIntOrNull())
                        },
                        items = birthYears,
                        error = uiState.formErrors["yearOfBirth"],
                        isRequired = false
                    )

                    val schoolClasses = listOf("Kindergarten","1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th","Not registered in school", "None of the above")

                    StudentDropdownField(
                        label = "School Class",
                        selectedItem = uiState.schoolClass,
                        onItemSelected = onSchoolClassChanged,
                        items = schoolClasses,
                        error = uiState.formErrors["schoolClass"],
                        isRequired = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                StudentFormSection(title = "Village & Group") {
                    VillageDropdownField(
                        label = "Village",
                        villages = uiState.villages,
                        selectedVillageId = uiState.selectedVillageId,
                        onVillageSelected = onVillageSelected,
                        error = uiState.formErrors["village"]
                    )

                    GroupDropdownField(
                        label = "Group",
                        groups = uiState.groups,
                        selectedGroupId = uiState.selectedGroupId,
                        onGroupSelected = onGroupSelected,
                        error = uiState.formErrors["group"]
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                StudentFormSection(title = "Contact & Family Information") {
                    StudentFormField(
                        label = "Primary Contact Number",
                        value = uiState.primaryContactNo,
                        onValueChange = onPrimaryContactNoChanged,
                        error = uiState.formErrors["primaryContactNo"],
                        isRequired = false,
                        keyboardType = KeyboardType.Phone,
                        maxLength = 10
                    )

                    StudentFormField(
                        label = "Secondary Contact Number",
                        value = uiState.secondaryContactNo,
                        onValueChange = onSecondaryContactNoChanged,
                        error = uiState.formErrors["secondaryContactNo"],
                        isRequired = false,
                        keyboardType = KeyboardType.Phone,
                        maxLength = 10
                    )

                    StudentFormField(
                        label = "Father's Name",
                        value = uiState.fathersName,
                        onValueChange = onFathersNameChanged,
                        error = uiState.formErrors["fathersName"],
                        isRequired = false
                    )

                    StudentFormField(
                        label = "Mother's Name",
                        value = uiState.mothersName,
                        onValueChange = onMothersNameChanged,
                        error = uiState.formErrors["mothersName"],
                        isRequired = false
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                            text = if (uiState.isUpdateMode) "Update Student" else "Register Student",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

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
fun StudentFormSection(
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
fun StudentFormField(
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
fun StudentGenderSelectionField(
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
                        text = gender.lowercase().replaceFirstChar { it.uppercase() },
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

@Composable
fun StudentDropdownField(
    label: String,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    items: List<String>,
    error: String?,
    isRequired: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label + if (isRequired) " *" else "",
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

@Composable
fun VillageDropdownField(
    label: String,
    villages: Map<Long, String>,
    selectedVillageId: Long?,
    onVillageSelected: (Long) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedVillageName = selectedVillageId?.let { villages[it] } ?: ""

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
                    text = selectedVillageName.ifEmpty { "Select $label" },
                    color = if (selectedVillageName.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                villages.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(text = name) },
                        onClick = {
                            onVillageSelected(id)
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

@Composable
fun GroupDropdownField(
    label: String,
    groups: Map<Long, String>,
    selectedGroupId: Long?,
    onGroupSelected: (Long) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedGroupName = selectedGroupId?.let { groups[it] } ?: ""

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
                    text = selectedGroupName.ifEmpty { "Select $label" },
                    color = if (selectedGroupName.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                groups.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(text = name) },
                        onClick = {
                            onGroupSelected(id)
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StudentRegistrationScreenPreview() {
    JagratiAndroidTheme {
        StudentRegistrationScreenLayout(
            uiState = StudentRegistrationUiState(
                firstName = "Rahul",
                lastName = "Sharma",
                villages = mapOf(1L to "Village A", 2L to "Village B"),
                groups = mapOf(1L to "Group 1", 2L to "Group 2")
            ),
            onBackPressed = {},
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onGenderChanged = {},
            onYearOfBirthChanged = {},
            onSchoolClassChanged = {},
            onPrimaryContactNoChanged = {},
            onSecondaryContactNoChanged = {},
            onFathersNameChanged = {},
            onMothersNameChanged = {},
            onVillageSelected = {},
            onGroupSelected = {},
            onSubmitClicked = {}
        )
    }
}

