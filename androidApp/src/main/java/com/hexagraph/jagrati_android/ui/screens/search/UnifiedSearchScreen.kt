package com.hexagraph.jagrati_android.ui.screens.search

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.ui.components.PersonCard
import com.hexagraph.jagrati_android.ui.components.toPersonCardData
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UnifiedSearchScreen(
    onBackPress: () -> Unit,
    onSelect: (String, Boolean) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    hasVolunteerAttendancePerms: Boolean,
    isMarkingAttendance: Boolean,
    viewModel: UnifiedSearchViewModel = koinViewModel{ parametersOf(hasVolunteerAttendancePerms, isMarkingAttendance, System.currentTimeMillis()) }
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    BackHandler() {
        onBackPress()
    }



    UnifiedSearchScreenLayout(
        query = uiState.query,
        students = uiState.students,
        volunteers = uiState.volunteers,
        villages = uiState.villages,
        groups = uiState.groups,
        isSearching = uiState.isSearching,
        isMarkingAttendance = isMarkingAttendance,
        selectedDateMillis = uiState.selectedDateMillis,
        onQueryChange = viewModel::search,
        onBackPress = onBackPress,
        onStudentSelect = { pid -> onSelect(pid, true) },
        onVolunteerSelect = { pid -> onSelect(pid, false) },
        onDateSelected = viewModel::updateSelectedDate,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchScreenLayout(
    query: String,
    students: List<Student>,
    volunteers: List<Volunteer>,
    villages: List<Village>,
    groups: List<Groups>,
    isSearching: Boolean,
    isMarkingAttendance: Boolean,
    selectedDateMillis: Long,
    onQueryChange: (String) -> Unit,
    onBackPress: () -> Unit,
    onStudentSelect: (String) -> Unit,
    onVolunteerSelect: (String) -> Unit,
    onDateSelected: (Long) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var active by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }
    val selectedDateText = remember(selectedDateMillis) {
        dateFormatter.format(java.util.Date(selectedDateMillis))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val onActiveChange: (Boolean) -> Unit = { active = it }
            val colors1 = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            )

            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = { active = false },
                        expanded = active,
                        onExpandedChange = onActiveChange,
                        placeholder = {
                            Text("Search name or roll no...",
                                fontSize = 14.sp)
                        },
                        leadingIcon = {
                            if (active) {
                                IconButton(onClick = onBackPress) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        },
                        trailingIcon = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Date selector for marking attendance
                                if (isMarkingAttendance) {
                                    Surface(
                                        onClick = { showDatePicker = true },
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = androidx.compose.ui.res.painterResource(com.hexagraph.jagrati_android.R.drawable.ic_calendar),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = selectedDateText,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }

                                // Clear button
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { onQueryChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear"
                                        )
                                    }
                                }
                            }
                        },
                    )
                },
                expanded = active,
                onExpandedChange = {
                    onBackPress()
                },
                modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (active) 0.dp else 16.dp),
                shape = SearchBarDefaults.inputFieldShape,
                colors = colors1,
                tonalElevation = SearchBarDefaults.TonalElevation,
                shadowElevation = SearchBarDefaults.ShadowElevation,
                windowInsets = SearchBarDefaults.windowInsets,
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when {
                            query.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            modifier = Modifier.padding(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Search by name or roll number",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Start typing to see results",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.7f
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            isSearching -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            students.isEmpty() && volunteers.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "No results found",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Try a different search term",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                    }
                                }
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (students.isNotEmpty()) {
                                        item {
                                            Text(
                                                text = "Students (${students.size})",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                        items(students, key = { "student_${it.pid}" }) { student ->
                                            val villageName =
                                                villages.find { it.id == student.villageId }?.name
                                                    ?: "Unknown"
                                            val groupName =
                                                groups.find { it.id == student.groupId }?.name
                                                    ?: "Unknown"

                                            PersonCard(
                                                data = student.toPersonCardData(
                                                    villageName,
                                                    groupName
                                                ),
                                                onClick = { onStudentSelect(student.pid) }
                                            )
                                        }
                                    }

                                    if (volunteers.isNotEmpty()) {
                                        item {
                                            Text(
                                                text = "Volunteers (${volunteers.size})",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(
                                                    top = if (students.isNotEmpty()) 16.dp else 8.dp,
                                                    bottom = 8.dp
                                                )
                                            )
                                        }
                                        items(
                                            volunteers,
                                            key = { "volunteer_${it.pid}" }) { volunteer ->
                                            PersonCard(
                                                data = volunteer.toPersonCardData(),
                                                onClick = { onVolunteerSelect(volunteer.pid) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
            )

            // Date picker dialog
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDateMillis,
                    selectableDates = object : androidx.compose.material3.SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            // Prevent selecting future dates
                            return utcTimeMillis <= System.currentTimeMillis()
                        }

                        override fun isSelectableYear(year: Int): Boolean {
                            // Prevent selecting future years
                            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                            return year <= currentYear
                        }
                    }
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    onDateSelected(millis)
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors()
                    )
                }
            }
        }

        // SnackbarHost positioned at the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UnifiedSearchScreenPreview() {
    JagratiAndroidTheme {
        UnifiedSearchScreenLayout(
            query = "Rahul",
            students = listOf(
                Student(
                    pid = "1",
                    firstName = "Rahul",
                    lastName = "Kumar",
                    gender = "Male",
                    villageId = 1,
                    groupId = 1
                )
            ),
            volunteers = listOf(
                Volunteer(
                    pid = "2",
                    firstName = "Rahul",
                    lastName = "Verma",
                    rollNumber = "2020BCS023",
                    gender = "Male",
                    batch = "2026",
                    dateOfBirth = "2002-11-20"
                )
            ),
            villages = listOf(Village(id = 1, name = "Bargi")),
            groups = listOf(Groups(id = 1, name = "Group A")),
            isSearching = false,
            isMarkingAttendance = true,
            selectedDateMillis = System.currentTimeMillis(),
            onQueryChange = {},
            onBackPress = {},
            onStudentSelect = {},
            onVolunteerSelect = {},
            onDateSelected = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
