package com.hexagraph.jagrati_android.ui.screens.attendancereport

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.attendance.StudentVillageGenderCount
import com.hexagraph.jagrati_android.model.attendance.VolunteerBatchCount
import com.hexagraph.jagrati_android.ui.components.ProfileAvatar
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceReportScreen(
    onNavigateToStudentProfile: (String) -> Unit,
    onNavigateToVolunteerProfile: (String) -> Unit,
    onNavigateToTakeAttendance: () -> Unit = {},
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
        onPreviousDay = viewModel::goToPreviousDay,
        onNextDay = viewModel::goToNextDay,
        onToday = viewModel::goToToday,
        onTakeAttendance = onNavigateToTakeAttendance,
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
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onTakeAttendance: () -> Unit,
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
        initialSelectedDateMillis = uiState.selectedDateMillis,
    )

    Column(modifier = Modifier.fillMaxSize()) {
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar_month),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No attendance data available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.material3.Button(
                            onClick = onTakeAttendance,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Take Attendance")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        DateNavigationSection(
                            date = formatDate(uiState.selectedDateMillis),
                            isToday = isToday(uiState.selectedDateMillis),
                            onPreviousDay = onPreviousDay,
                            onNextDay = onNextDay,
                            onToday = onToday,
                            onDateClick = { showDatePicker = true },
                            onTakeAttendance = onTakeAttendance
                        )
                    }

                    // Summary Cards
                    item {
                        SummaryCards(
                            totalStudents = uiState.reportData.presentStudents.size,
                            totalVolunteers = uiState.reportData.presentVolunteers.size
                        )
                    }

                    // Stats Section
                    item {
                        StatsSection(
                            studentsByVillageGender = uiState.reportData.studentsByVillageGender,
                            volunteersByBatch = uiState.reportData.volunteersByBatch,
                            groupCounts = uiState.groupCounts
                        )
                    }

                    // Volunteers Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = "Volunteers",
                            count = uiState.filteredVolunteers.size
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
                        PersonCard(
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

                    // Students Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionHeader(
                            title = "Students",
                            count = uiState.filteredStudents.size
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
                        PersonCard(
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
fun DateNavigationSection(
    date: String,
    isToday: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onDateClick: () -> Unit,
    onTakeAttendance: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Attendance Report",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDateClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar_month),
                        contentDescription = "Select Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                androidx.compose.material3.FilledTonalButton(
                    onClick = onTakeAttendance,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Take",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousDay) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_left),
                        contentDescription = "Previous Day",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(
                    onClick = onToday,
                    enabled = !isToday
                ) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onNextDay) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = "Next Day",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TakeAttendanceButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_attendance_filled),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Take Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Mark attendance for today",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DateHeaderSection(
    date: String,
    onDateClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Attendance Report",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDateClick) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar_month),
                contentDescription = "Select Date",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SummaryCards(
    totalStudents: Int,
    totalVolunteers: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Students",
            count = totalStudents,
            icon = painterResource(R.drawable.ic_group),
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Volunteers",
            count = totalVolunteers,
            icon = painterResource(R.drawable.ic_person),
            containerColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.painter.Painter,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = 0.12f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = containerColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsSection(
    studentsByVillageGender: List<StudentVillageGenderCount>,
    volunteersByBatch: List<VolunteerBatchCount>,
    groupCounts: Map<String, Int>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Students by Village & Gender - Tabular View
        if (studentsByVillageGender.isNotEmpty()) {
            VillageGenderTableCard(studentsByVillageGender = studentsByVillageGender)
        }

        // Volunteers by Batch
        if (volunteersByBatch.isNotEmpty()) {
            StatsCard(
                title = "By Batch",
                items = volunteersByBatch.map {
                    (it.batch ?: "N/A") to it.count.toInt()
                }
            )
        }

        // Students by Group
        if (groupCounts.isNotEmpty()) {
            StatsCard(
                title = "By Group",
                items = groupCounts.map { it.key to it.value }
            )
        }
    }
}

@Composable
fun VillageGenderTableCard(studentsByVillageGender: List<StudentVillageGenderCount>) {
    val villageData = studentsByVillageGender.groupBy { it.villageId to it.villageName }
        .map { (villageInfo, counts) ->
            val maleCount = counts.find { it.gender == Gender.MALE }?.count?.toInt() ?: 0
            val femaleCount = counts.find { it.gender == Gender.FEMALE }?.count?.toInt() ?: 0
            val otherCount = counts.find { it.gender == Gender.OTHER }?.count?.toInt() ?: 0
            VillageGenderData(
                villageName = villageInfo.second,
                maleCount = maleCount,
                femaleCount = femaleCount,
                otherCount = otherCount,
                total = maleCount + femaleCount + otherCount
            )
        }
        .sortedByDescending { it.total }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "By Village & Gender",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Village",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Male",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(50.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Female",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(50.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Table Rows
                villageData.forEach { data ->
                    VillageGenderRow(data = data)
                }

                // Total Row
                val totalMale = villageData.sumOf { it.maleCount }
                val totalFemale = villageData.sumOf { it.femaleCount }
                val grandTotal = villageData.sumOf { it.total }

                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = totalMale.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(50.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = totalFemale.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(60.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = grandTotal.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(50.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun VillageGenderRow(data: VillageGenderData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.villageName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .width(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (data.maleCount > 0)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.maleCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (data.maleCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                color = if (data.maleCount > 0)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .width(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (data.femaleCount > 0)
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.femaleCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (data.femaleCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                color = if (data.femaleCount > 0)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .width(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data.total.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class VillageGenderData(
    val villageName: String,
    val maleCount: Int,
    val femaleCount: Int,
    val otherCount: Int,
    val total: Int
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsCard(
    title: String,
    items: List<Pair<String, Int>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { (label, count) ->
                    StatChip(label = label, count = count)
                }
            }
        }
    }
}

@Composable
fun StatChip(
    label: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
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

@Composable
fun SectionHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun PersonCard(
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
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileAvatar(
                userName = name,
                profileImageUrl = profileImageUrl,
                size = 48.dp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

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
                                        text = "Delete",
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
fun VolunteerFilters(
    availableBatches: List<String>,
    selectedBatch: String?,
    onBatchSelected: (String?) -> Unit
) {
    if (availableBatches.isEmpty()) return

    var showBatchMenu by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = selectedBatch != null,
            onClick = { showBatchMenu = !showBatchMenu },
            label = { Text(selectedBatch ?: "All Batches") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_filter_list_24),
                    contentDescription = "Filter",
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
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
                        contentDescription = "Filter",
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
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
                        contentDescription = "Filter",
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
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
                        contentDescription = "Filter",
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
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
    val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
    return dateFormat.format(Date(millis))
}

fun isToday(millis: Long): Boolean {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = millis
    }
    val today = Calendar.getInstance()
    return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}
