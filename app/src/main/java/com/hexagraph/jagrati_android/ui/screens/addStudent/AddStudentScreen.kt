package com.hexagraph.jagrati_android.ui.screens.addStudent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.JagratiGroups
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.ui.components.ScreenHeader

@Composable
fun AddStudentScreen(
    pid: String? = null,
    onSuccessAddition: (StudentDetails) -> Unit,
    isFacialDataAvailable: Boolean,
    onPressBack: () -> Unit,
    addStudentViewModel: AddStudentViewModel = hiltViewModel()
) {
    val uiState by addStudentViewModel.uiState.collectAsState()
    BackHandler {
        onPressBack()
    }
    LaunchedEffect(Unit) {
        addStudentViewModel.initialize(pid, isFacialDataAvailable)
    }
    AddStudentScreenBase(
        uiState = uiState,
        onChangeOfStudentDetails = { addStudentViewModel.changeDetailsOfStudents(it) },
        onSave = {
            onSuccessAddition(addStudentViewModel.saveStudent(uiState.studentData))
        },
        onAddFacialData = {
            //To be implemented
        },
        onPressBack = onPressBack
    )
}

@Composable
fun AddStudentScreenBase(
    uiState: AddStudentUIState,
    onChangeOfStudentDetails: (StudentDetails) -> Unit,
    onSave: () -> Unit,
    onPressBack: () -> Unit,
    onAddFacialData: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val student = uiState.studentData
    var age by rememberSaveable { mutableStateOf(uiState.studentData.age.toString()) }

    LazyColumn(modifier = Modifier.padding(horizontal = 12.dp)) {
        item {
            ScreenHeader(
                onBackPress = onPressBack,
                title =  if (uiState.isStudentNew) "Add New Student" else "Edit Student Details",
                trailingContent = {}
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
                value = age,
                validator = {age->
                    age.toIntOrNull() != null && age.toInt() > 0
                },
                onValueChange = {
                    age = it
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
                    focusManager.moveFocus(FocusDirection.Down)
                    onChangeOfStudentDetails(student.copy(gender = it))
                })
        }

        item {
            DropdownField(
                label = "Village",
                options = Village.entries,
                selected = student.village,
                onSelectionChange = {
                    focusManager.moveFocus(FocusDirection.Down)
                    onChangeOfStudentDetails(student.copy(village = it))
                })
        }

        item {
            DropdownField(
                label = "Assigned Group",
                options = JagratiGroups.entries,
                selected = student.currentGroupId,
                onSelectionChange = {
                    focusManager.moveFocus(FocusDirection.Down)
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
fun StudentDetailsTextField(
    modifier: Modifier = Modifier,
    label: String,
    maxLines: Int = 1,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    imeAction: ImeAction = ImeAction.Next,
    onNext: () -> Unit,
    validator: (String) -> Boolean = { true }
) {
    var error by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            error = !validator(it)
            onValueChange(it)
        },
        maxLines = maxLines,
        label = { Text(label) },
        isError = error,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType,
            imeAction = imeAction),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = modifier.fillMaxWidth()
            .padding(vertical = 4.dp)
    )
    if (error) {
        Text(
            "Invalid $label",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun <T> DropdownField(
    label: String,
    options: List<T>,
    selected: T,
    onSelectionChange: (T) -> Unit
) {
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
                .onFocusChanged(){
                    if(it.isFocused){
                        expanded = true
                    }
                }
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
        onAddFacialData = {},
        onPressBack = {}
    )
}
