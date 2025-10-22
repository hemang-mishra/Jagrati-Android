package com.hexagraph.jagrati_android.ui.screens.attendancereport

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse
import com.hexagraph.jagrati_android.model.attendance.PresentStudent
import com.hexagraph.jagrati_android.model.attendance.PresentVolunteer
import com.hexagraph.jagrati_android.model.attendance.StudentVillageGenderCount
import com.hexagraph.jagrati_android.model.attendance.VolunteerBatchCount
import com.hexagraph.jagrati_android.ui.components.ProfileAvatar
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.theme.JagratiThemeColors
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceReportScreen(
    onNavigateToStudentProfile: (String) -> Unit,
    onNavigateToVolunteerProfile: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: AttendanceReportViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { error ->
            error?.let {
                snackbarHostState.showSnackbar(it.toast)
                viewModel.clearErrorFlow()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.successMsgFlow.collect { message ->
            message?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMsgFlow()
            }
        }
    }

    AttendanceReportScreenLayout(
        uiState = uiState,
        onDateSelected = viewModel::setSelectedDate,
        onRefresh = { viewModel.loadAttendanceReport(isRefreshing = true) },
        onStudentVillageFilter = viewModel::setStudentVillageFilter,
        onStudentGenderFilter = viewModel::setStudentGenderFilter,
        onStudentGroupFilter = viewModel::setStudentGroupFilter,
        onVolunteerBatchFilter = viewModel::setVolunteerBatchFilter,
        onDeleteStudentAttendance = viewModel::deleteStudentAttendance,
        onDeleteVolunteerAttendance = viewModel::deleteVolunteerAttendance,
        onNavigateToStudentProfile = onNavigateToStudentProfile,
        onNavigateToVolunteerProfile = onNavigateToVolunteerProfile,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AttendanceReportScreenLayout(
    uiState: AttendanceReportUiState,
    onDateSelected: (Long) -> Unit,
    onRefresh: () -> Unit,
    onStudentVillageFilter: (Long?) -> Unit,
    onStudentGenderFilter: (Gender?) -> Unit,
    onStudentGroupFilter: (Long?) -> Unit,
    onVolunteerBatchFilter: (String?) -> Unit,
    onDeleteStudentAttendance: (String) -> Unit,
    onDeleteVolunteerAttendance: (String) -> Unit,
    onNavigateToStudentProfile: (String) -> Unit,
    onNavigateToVolunteerProfile: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.selectedDateMillis
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with date and calendar button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Attendance Report",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatDate(uiState.selectedDateMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar_month),
                    contentDescription = "Select Date",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.reportData == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No attendance data available for this date",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        SummarySection(
                            studentsByVillageGender = uiState.reportData.studentsByVillageGender,
                            volunteersByBatch = uiState.reportData.volunteersByBatch,
                            totalStudents = uiState.reportData.presentStudents.size,
                            totalVolunteers = uiState.reportData.presentVolunteers.size
                        )
                    }

                    item {
                        GroupCountSection(groupCounts = uiState.groupCounts)
                    }

                    item {
                        Text(
                            text = "Volunteers (${uiState.filteredVolunteers.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    item {
                        VolunteerFilters(
                            availableBatches = uiState.reportData.volunteersByBatch.mapNotNull { it.batch },
                            selectedBatch = uiState.selectedVolunteerBatch,
                            onBatchSelected = onVolunteerBatchFilter
                        )
                    }

                    items(
                        items = uiState.filteredVolunteers,
                        key = { it.pid }
                    ) { volunteer ->
                        AttendancePersonCard(
                            name = "${volunteer.firstName} ${volunteer.lastName}",
                            subtitle = volunteer.rollNo,
                            extra = "",
                            profileImageUrl = uiState.volunteerProfilePics[volunteer.pid],
                            canDelete = uiState.canDeleteVolunteerAttendance,
                            onCardClick = { onNavigateToVolunteerProfile(volunteer.pid) },
                            onDelete = { onDeleteVolunteerAttendance(volunteer.aid) },
                            isDeletingAttendance = uiState.isDeletingAttendance
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Students (${uiState.filteredStudents.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    item {
                        StudentFilters(
                            availableVillages = uiState.reportData.studentsByVillageGender.map {
                                it.villageId to it.villageName
                            }.distinctBy { it.first },
                            availableGroups = uiState.reportData.presentStudents.map {
                                it.groupId to it.groupName
                            }.distinctBy { it.first },
                            selectedVillageId = uiState.selectedStudentVillage,
                            selectedGender = uiState.selectedStudentGender,
                            selectedGroupId = uiState.selectedStudentGroup,
                            onVillageSelected = onStudentVillageFilter,
                            onGenderSelected = onStudentGenderFilter,
                            onGroupSelected = onStudentGroupFilter
                        )
                    }

                    items(
                        items = uiState.filteredStudents,
                        key = { it.pid }
                    ) { student ->
                        AttendancePersonCard(
                            name = "${student.firstName} ${student.lastName}",
                            subtitle = student.villageName,
                            extra = student.groupName,
                            profileImageUrl = uiState.studentProfilePics[student.pid],
                            canDelete = uiState.canDeleteStudentAttendance,
                            onCardClick = { onNavigateToStudentProfile(student.pid) },
                            onDelete = { onDeleteStudentAttendance(student.aid) },
                            isDeletingAttendance = uiState.isDeletingAttendance
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateSelected(it) }
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
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun AttendancePersonCard(
    name: String,
    subtitle: String,
    extra: String,
    profileImageUrl: String?,
    canDelete: Boolean,
    onCardClick: () -> Unit,
    onDelete: () -> Unit,
    isDeletingAttendance: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            ProfileAvatar(userName = name, profileImageUrl = profileImageUrl)

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (extra.isNotBlank()) {
                    Text(
                        text = extra,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Three-dot menu (only if can delete)
            if (canDelete) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Delete Attendance",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            onClick = {
                                if (!isDeletingAttendance) {
                                    onDelete()
                                    showMenu = false
                                }
                            },
                            enabled = !isDeletingAttendance
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttendanceReportScreenPreview() {
    JagratiAndroidTheme {
        AttendanceReportScreenLayout(
            uiState = AttendanceReportUiState(
                isLoading = false,
                selectedDateMillis = System.currentTimeMillis(),
                reportData = AttendanceReportResponse(
                    date = "2024-01-15",
                    studentsByVillageGender = listOf(
                        StudentVillageGenderCount(1, "Bargi", Gender.MALE, 15),
                        StudentVillageGenderCount(1, "Bargi", Gender.FEMALE, 12),
                        StudentVillageGenderCount(2, "Tilhari", Gender.MALE, 10)
                    ),
                    volunteersByBatch = listOf(
                        VolunteerBatchCount("2021", 5),
                        VolunteerBatchCount("2022", 8)
                    ),
                    presentStudents = listOf(
                        PresentStudent("S1", "1", "Rahul", "Kumar", Gender.MALE, 1, "Bargi", 1, "Group A"),
                        PresentStudent("S2", "2", "Priya", "Sharma", Gender.FEMALE, 1, "Bargi", 1, "Group A")
                    ),
                    presentVolunteers = listOf(
                        PresentVolunteer("V1", "3", "Amit", "Singh", "2021", "23bcs103"),
                        PresentVolunteer("V2", "4", "Sneha", "Patel", "2022", "23bcs103")
                    )
                ),
                filteredStudents = listOf(
                    PresentStudent("S1", "1", "Rahul", "Kumar", Gender.MALE, 1, "Bargi", 1, "Group A")
                ),
                filteredVolunteers = listOf(
                    PresentVolunteer("V1", "3", "Amit", "Singh", "2021", "23bcs103")
                ),
                groupCounts = mapOf("Group A" to 15, "Group B" to 12)
            ),
            onDateSelected = {},
            onRefresh = {},
            onStudentVillageFilter = {},
            onStudentGenderFilter = {},
            onStudentGroupFilter = {},
            onVolunteerBatchFilter = {},
            onDeleteStudentAttendance = {},
            onDeleteVolunteerAttendance = {},
            onNavigateToStudentProfile = {},
            onNavigateToVolunteerProfile = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AttendanceReportScreenDarkPreview() {
    JagratiAndroidTheme {
        AttendanceReportScreenLayout(
            uiState = AttendanceReportUiState(
                isLoading = false,
                selectedDateMillis = System.currentTimeMillis(),
                reportData = AttendanceReportResponse(
                    date = "2024-01-15",
                    studentsByVillageGender = listOf(
                        StudentVillageGenderCount(1, "Bargi", Gender.MALE, 15),
                        StudentVillageGenderCount(1, "Bargi", Gender.FEMALE, 12),
                        StudentVillageGenderCount(2, "Tilhari", Gender.MALE, 10)
                    ),
                    volunteersByBatch = listOf(
                        VolunteerBatchCount("2021", 5),
                        VolunteerBatchCount("2022", 8)
                    ),
                    presentStudents = listOf(
                        PresentStudent("S1", "1", "Rahul", "Kumar", Gender.MALE, 1, "Bargi", 1, "Group A"),
                        PresentStudent("S2", "2", "Priya", "Sharma", Gender.FEMALE, 1, "Bargi", 1, "Group A")
                    ),
                    presentVolunteers = listOf(
                        PresentVolunteer("V1", "3", "Amit", "Singh", "2021", "23bcs103"),
                        PresentVolunteer("V2", "4", "Sneha", "Patel", "2022", "23bcs103")
                    )
                ),
                filteredStudents = listOf(
                    PresentStudent("S1", "1", "Rahul", "Kumar", Gender.MALE, 1, "Bargi", 1, "Group A", )
                ),
                filteredVolunteers = listOf(
                    PresentVolunteer("V1", "3", "Amit", "Singh", "2021", "23bcs103")
                ),
                groupCounts = mapOf("Group A" to 15, "Group B" to 12)
            ),
            onDateSelected = {},
            onRefresh = {},
            onStudentVillageFilter = {},
            onStudentGenderFilter = {},
            onStudentGroupFilter = {},
            onVolunteerBatchFilter = {},
            onDeleteStudentAttendance = {},
            onDeleteVolunteerAttendance = {},
            onNavigateToStudentProfile = {},
            onNavigateToVolunteerProfile = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SummarySectionPreview() {
    JagratiAndroidTheme {
        SummarySection(
            studentsByVillageGender = listOf(
                StudentVillageGenderCount(1, "Bargi", Gender.MALE, 15),
                StudentVillageGenderCount(1, "Bargi", Gender.FEMALE, 12)
            ),
            volunteersByBatch = listOf(
                VolunteerBatchCount("2021", 5),
                VolunteerBatchCount("2022", 8)
            ),
            totalStudents = 27,
            totalVolunteers = 13
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SummarySection(
    studentsByVillageGender: List<StudentVillageGenderCount>,
    volunteersByBatch: List<VolunteerBatchCount>,
    totalStudents: Int,
    totalVolunteers: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryCard(
                    title = "Students",
                    count = totalStudents,
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryCard(
                    title = "Volunteers",
                    count = totalVolunteers,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Students by Village & Gender",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                studentsByVillageGender.forEach { item ->
                    StatChip(
                        label = "${item.villageName} (${item.gender.name})",
                        count = item.count.toInt()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Volunteers by Batch",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                volunteersByBatch.forEach { item ->
                    StatChip(
                        label = item.batch ?: "N/A",
                        count = item.count.toInt()
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun StatChip(
    label: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupCountSection(groupCounts: Map<String, Int>) {
    if (groupCounts.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Students by Group",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupCounts.forEach { (groupName, count) ->
                    GroupCountChip(groupName = groupName, count = count)
                }
            }
        }
    }
}

@Composable
fun GroupCountChip(groupName: String, count: Int) {
    val batchColors = JagratiThemeColors.batchColors
    val color = batchColors[groupName.hashCode() % batchColors.size]

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = groupName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VolunteerFilters(
    availableBatches: List<String>,
    selectedBatch: String?,
    onBatchSelected: (String?) -> Unit
) {
    if (availableBatches.isEmpty()) return

    var showBatchMenu by remember { mutableStateOf(false) }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            FilterChip(
                selected = selectedBatch != null,
                onClick = { showBatchMenu = !showBatchMenu },
                label = { Text(selectedBatch ?: "All Batches") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_filter_list_24),
                        contentDescription = "Filter by batch",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            DropdownMenu(
                expanded = showBatchMenu,
                onDismissRequest = { showBatchMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Batches") },
                    onClick = {
                        onBatchSelected(null)
                        showBatchMenu = false
                    }
                )
                availableBatches.forEach { batch ->
                    DropdownMenuItem(
                        text = { Text(batch) },
                        onClick = {
                            onBatchSelected(batch)
                            showBatchMenu = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentFilters(
    availableVillages: List<Pair<Long, String>>,
    availableGroups: List<Pair<Long, String>>,
    selectedVillageId: Long?,
    selectedGender: Gender?,
    selectedGroupId: Long?,
    onVillageSelected: (Long?) -> Unit,
    onGenderSelected: (Gender?) -> Unit,
    onGroupSelected: (Long?) -> Unit
) {
    var showVillageMenu by remember { mutableStateOf(false) }
    var showGenderMenu by remember { mutableStateOf(false) }
    var showGroupMenu by remember { mutableStateOf(false) }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            FilterChip(
                selected = selectedVillageId != null,
                onClick = { showVillageMenu = !showVillageMenu },
                label = {
                    Text(availableVillages.find { it.first == selectedVillageId }?.second ?: "All Villages")
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_filter_list_24),
                        contentDescription = "Filter by village",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            DropdownMenu(
                expanded = showVillageMenu,
                onDismissRequest = { showVillageMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Villages") },
                    onClick = {
                        onVillageSelected(null)
                        showVillageMenu = false
                    }
                )
                availableVillages.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onVillageSelected(id)
                            showVillageMenu = false
                        }
                    )
                }
            }
        }

        Box {
            FilterChip(
                selected = selectedGender != null,
                onClick = { showGenderMenu = !showGenderMenu },
                label = { Text(selectedGender?.name ?: "All Genders") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_filter_list_24),
                        contentDescription = "Filter by gender",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            DropdownMenu(
                expanded = showGenderMenu,
                onDismissRequest = { showGenderMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Genders") },
                    onClick = {
                        onGenderSelected(null)
                        showGenderMenu = false
                    }
                )
                Gender.entries.forEach { gender ->
                    DropdownMenuItem(
                        text = { Text(gender.name) },
                        onClick = {
                            onGenderSelected(gender)
                            showGenderMenu = false
                        }
                    )
                }
            }
        }

        Box {
            FilterChip(
                selected = selectedGroupId != null,
                onClick = { showGroupMenu = !showGroupMenu },
                label = {
                    Text(availableGroups.find { it.first == selectedGroupId }?.second ?: "All Groups")
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_filter_list_24),
                        contentDescription = "Filter by group",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )

            DropdownMenu(
                expanded = showGroupMenu,
                onDismissRequest = { showGroupMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Groups") },
                    onClick = {
                        onGroupSelected(null)
                        showGroupMenu = false
                    }
                )
                availableGroups.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onGroupSelected(id)
                            showGroupMenu = false
                        }
                    )
                }
            }
        }
    }
}

fun formatDate(millis: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return dateFormat.format(Date(millis))
}
