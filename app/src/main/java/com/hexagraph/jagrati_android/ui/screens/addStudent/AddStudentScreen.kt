package com.hexagraph.jagrati_android.ui.screens.addStudent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexagraph.jagrati_android.model.*

@Composable
fun AddStudentScreen(
    pid: String? = null,
    onSuccessAddition: (StudentDetails)->Unit,
    addStudentViewModel: AddStudentViewModel  = hiltViewModel()
){
    val uiState by addStudentViewModel.uiState.collectAsState()

    AddStudentScreenBase(
        uiState = uiState,
        onChangeOfStudentDetails = { addStudentViewModel.changeDetailsOfStudents(it) },
        onSave = {
            onSuccessAddition(addStudentViewModel.saveStudent(uiState.studentData))
                 },
        onAddFacialData = {
            //To be implemented
        }
    )
}

@Composable
fun AddStudentScreenBase(
    uiState: AddStudentUIState,
    onChangeOfStudentDetails: (StudentDetails) -> Unit,
    onSave: () -> Unit,
    onAddFacialData: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val student = uiState.studentData

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = if (uiState.isStudentNew) "Add New Student" else "Edit Student Details",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        item {
            if (!uiState.isFacialDataAdded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Facial data not added")
                    Button(onClick = onAddFacialData) {
                        Text("Add Facial Data")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            StudentDetailsTextField(
                label = "First Name",
                value = student.firstName,
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(firstName = it))
                },
                keyboardType = KeyboardType.Text,
                onNext = { focusManager.moveFocus(FocusDirection.Down) })
        }

        item {
            StudentDetailsTextField(
                label = "Last Name",
                value = student.lastName,
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(lastName = it))
                },
                keyboardType = KeyboardType.Text,
                onNext = { focusManager.moveFocus(FocusDirection.Down) })
        }

        item {
            StudentDetailsTextField(
                label = "Age",
                value = student.age.toString(),
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(age = it.toIntOrNull() ?: -1))
                },
                keyboardType = KeyboardType.Number,
                onNext = { focusManager.moveFocus(FocusDirection.Down) })
        }

        item {
            DropdownField(
                label = "Gender",
                options = Gender.entries,
                selected = student.gender,
                onSelectionChange = {
                    onChangeOfStudentDetails(student.copy(gender = it))
                })
        }

        item {
            DropdownField(
                label = "Village",
                options = Village.entries,
                selected = student.village,
                onSelectionChange = {
                    onChangeOfStudentDetails(student.copy(village = it))
                })
        }

        item {
            DropdownField(
                label = "Assigned Group",
                options = JagratiGroups.entries,
                selected = student.currentGroupId,
                onSelectionChange = {
                    onChangeOfStudentDetails(student.copy(currentGroupId = it))
                })
        }

        item {
            StudentDetailsTextField(
                label = "Class",
                value = student.schoolClass,
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(schoolClass = it))
                },
                keyboardType = KeyboardType.Text,
                onNext = { focusManager.moveFocus(FocusDirection.Down) })

        }

        item {
            StudentDetailsTextField(
                label = "Primary Contact",
                value = student.primaryContactNo,
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(primaryContactNo = it))
                },
                keyboardType = KeyboardType.Phone,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                validator = ::validatePhoneNumber
            )
        }

        item {
            StudentDetailsTextField(
                label = "Secondary Contact",
                value = student.secondaryContactNo,
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(secondaryContactNo = it))
                },
                keyboardType = KeyboardType.Phone,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                validator = ::validatePhoneNumber
            )
        }

        item {
            StudentDetailsTextField(
                label = "Guardian Name",
                value = student.guardianName,
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(guardianName = it))
                },
                keyboardType = KeyboardType.Text,
                onNext = { focusManager.moveFocus(FocusDirection.Down) })
        }

        item {
            StudentDetailsTextField(
                label = "Remarks",
                value = student.remarks ?: "",
                onValueChange = {
                    onChangeOfStudentDetails(student.copy(remarks = it))
                },
                keyboardType = KeyboardType.Text,
                onNext = { focusManager.moveFocus(FocusDirection.Down) })
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text("Save")
            }
        }
    }
}

@Composable
fun StudentDetailsTextField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType, onNext: () -> Unit, validator: (String) -> Boolean = { true }) {
    var error by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            error = !validator(it)
            onValueChange(it)
        },
        label = { Text(label) },
        isError = error,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = Modifier.fillMaxWidth()
    )
    if (error) {
        Text("Invalid $label", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun <T> DropdownField(label: String, options: List<T>, selected: T, onSelectionChange: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun validatePhoneNumber(number: String): Boolean {
    return number.matches(Regex("^\\d{10}\$"))
}

@Preview(showBackground = true)
@Composable
fun PreviewAddStudentScreen() {
    AddStudentScreenBase(
        uiState = AddStudentUIState(),
        onChangeOfStudentDetails = {},
        onSave = {},
        onAddFacialData = {}
    )
}
