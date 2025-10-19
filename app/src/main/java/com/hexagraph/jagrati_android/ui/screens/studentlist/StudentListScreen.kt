package com.hexagraph.jagrati_android.ui.screens.studentlist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.Groups
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Village
import com.hexagraph.jagrati_android.ui.components.PersonCard
import com.hexagraph.jagrati_android.ui.components.toPersonCardData
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun StudentListScreen(
    onBackPress: () -> Unit,
    onStudentClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: StudentListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    StudentListScreenLayout(
        students = uiState.students,
        villages = uiState.villages,
        groups = uiState.groups,
        isLoading = uiState.isLoading,
        selectedVillage = uiState.selectedVillage,
        selectedGroup = uiState.selectedGroup,
        onVillageSelected = viewModel::filterByVillage,
        onGroupSelected = viewModel::filterByGroup,
        onBackPress = onBackPress,
        onStudentClick = onStudentClick,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreenLayout(
    students: List<Student>,
    villages: List<Village>,
    groups: List<Groups>,
    isLoading: Boolean,
    selectedVillage: Village?,
    selectedGroup: Groups?,
    onVillageSelected: (Village?) -> Unit,
    onGroupSelected: (Groups?) -> Unit,
    onBackPress: () -> Unit,
    onStudentClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var showVillageFilter by remember { mutableStateOf(false) }
    var showGroupFilter by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Students",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    FilterChip(
                        selected = selectedVillage != null,
                        onClick = { showVillageFilter = !showVillageFilter },
                        label = {
                            Text(selectedVillage?.name ?: "All Villages")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_filter_list_24),
                                contentDescription = "Filter by village"
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = showVillageFilter,
                        onDismissRequest = { showVillageFilter = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Villages") },
                            onClick = {
                                onVillageSelected(null)
                                showVillageFilter = false
                            }
                        )
                        villages.forEach { village ->
                            DropdownMenuItem(
                                text = { Text(village.name) },
                                onClick = {
                                    onVillageSelected(village)
                                    showVillageFilter = false
                                }
                            )
                        }
                    }
                }

                Box {
                    FilterChip(
                        selected = selectedGroup != null,
                        onClick = { showGroupFilter = !showGroupFilter },
                        label = {
                            Text(selectedGroup?.name ?: "All Groups")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_filter_list_24),
                                contentDescription = "Filter by group"
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = showGroupFilter,
                        onDismissRequest = { showGroupFilter = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Groups") },
                            onClick = {
                                onGroupSelected(null)
                                showGroupFilter = false
                            }
                        )
                        groups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group.name) },
                                onClick = {
                                    onGroupSelected(group)
                                    showGroupFilter = false
                                }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (students.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No students found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students, key = { it.pid }) { student ->
                        val villageName = villages.find { it.id == student.villageId }?.name ?: "Unknown"
                        val groupName = groups.find { it.id == student.groupId }?.name ?: "Unknown"

                        PersonCard(
                            data = student.toPersonCardData(villageName, groupName),
                            onClick = { onStudentClick(student.pid) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StudentListScreenPreview() {
    JagratiAndroidTheme {
        StudentListScreenLayout(
            students = listOf(
                Student(
                    pid = "1",
                    firstName = "Rahul",
                    lastName = "Kumar",
                    gender = "Male",
                    villageId = 1,
                    groupId = 1
                ),
                Student(
                    pid = "2",
                    firstName = "Priya",
                    lastName = "Sharma",
                    gender = "Female",
                    villageId = 2,
                    groupId = 2
                )
            ),
            villages = listOf(
                Village(id = 1, name = "Bargi"),
                Village(id = 2, name = "Khandwa")
            ),
            groups = listOf(
                Groups(id = 1, name = "Group A"),
                Groups(id = 2, name = "Group B")
            ),
            isLoading = false,
            selectedVillage = null,
            selectedGroup = null,
            onVillageSelected = {},
            onGroupSelected = {},
            onBackPress = {},
            onStudentClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

