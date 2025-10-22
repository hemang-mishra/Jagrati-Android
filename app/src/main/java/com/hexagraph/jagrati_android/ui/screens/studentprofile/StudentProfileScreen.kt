package com.hexagraph.jagrati_android.ui.screens.studentprofile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.student.StudentGroupHistoryResponse
import com.hexagraph.jagrati_android.model.student.StudentResponse
import com.hexagraph.jagrati_android.ui.components.ProfileAvatar
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.util.AttendanceUtils
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StudentProfileScreen(
    pid: String,
    onNavigateBack: () -> Unit,
    onNavigateToFaceDataRegister: (String) -> Unit,
    onNavigateToEditProfile: (String) -> Unit,
    onNavigateToVolunteerProfile: (String) -> Unit,
    viewModel: StudentProfileViewModel = koinViewModel { parametersOf(pid) },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it.toast,
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorFlow()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMsgFlow()
        }
    }

    StudentProfileLayout(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onRefresh = { viewModel.refresh() },
        onPhotoClick = { viewModel.showEditOptionsSheet() },
        onAddFaceData = { onNavigateToFaceDataRegister(pid) },
        onEditProfile = { onNavigateToEditProfile(pid) },
        onCallContact = { phoneNumber ->
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$phoneNumber".toUri()
            }
            context.startActivity(intent)
        },
        onViewGroupHistory = { viewModel.showGroupHistorySheet() },
        onDismissGroupHistory = { viewModel.hideGroupHistorySheet() },
        onDismissEditOptions = { viewModel.hideEditOptionsSheet() },
        onVolunteerClick = onNavigateToVolunteerProfile,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileLayout(
    uiState: StudentProfileUiState,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onPhotoClick: () -> Unit,
    onAddFaceData: () -> Unit,
    onEditProfile: () -> Unit,
    onCallContact: (String) -> Unit,
    onViewGroupHistory: () -> Unit,
    onDismissGroupHistory: () -> Unit,
    onDismissEditOptions: () -> Unit,
    onVolunteerClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.canEditProfile) {
                        IconButton(onClick = onEditProfile) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit Profile"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.student == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.student != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Photo Section
                    item {
                        ProfilePhotoSection(
                            student = uiState.student,
                            onPhotoClick = onPhotoClick
                        )
                    }

                    // Primary Details Section
                    item {
                        PrimaryDetailsSection(
                            student = uiState.student,
                            onCallContact = onCallContact
                        )
                    }

                    // Secondary Details Section
                    item {
                        SecondaryDetailsSection(student = uiState.student)
                    }

                    // Group History Section
                    item {
                        GroupHistorySection(onViewGroupHistory = onViewGroupHistory)
                    }

                    // Attendance Summary Section
                    item {
                        AttendanceSummarySection(
                            lastPresentDate = uiState.lastPresentDate,
                            presentCountLastWeek = uiState.presentCountLastWeek,
                            presentCountLastMonth = uiState.presentCountLastMonth
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Failed to load student profile")
                }
            }
        }
    }

    // Edit Options Bottom Sheet
    if (uiState.showEditOptionsSheet) {
        EditOptionsBottomSheet(
            hasProfilePic = uiState.student?.profilePic != null,
            onDismiss = onDismissEditOptions,
            onEditFaceData = {
                onDismissEditOptions()
                onAddFaceData()
            },
            onViewFullScreen = {
                onDismissEditOptions()
                // TODO: Implement full screen photo viewer
            }
        )
    }

    // Group History Bottom Sheet
    if (uiState.showGroupHistorySheet) {
        GroupHistoryBottomSheet(
            groupHistory = uiState.groupHistory,
            onDismiss = onDismissGroupHistory,
            onVolunteerClick = { volunteerPid ->
                onDismissGroupHistory()
                onVolunteerClick(volunteerPid)
            }
        )
    }
}

@Composable
fun ProfilePhotoSection(
    student: StudentResponse,
    onPhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable(onClick = onPhotoClick),
            contentAlignment = Alignment.Center
        ) {
            if (student.profilePic != null) {
                ProfileAvatar(
                    userName = "${student.firstName} ${student.lastName}",
                    profileImageUrl = student.profilePic.url,
                    size = 120.dp
                )
                // Edit icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                // No photo - show add face data prompt
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_face),
                            contentDescription = "No Photo",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onPhotoClick) {
                        Text("Add Facial Data")
                    }
                }
            }
        }
    }
}

@Composable
fun PrimaryDetailsSection(
    student: StudentResponse,
    onCallContact: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Name
            Text(
                text = "${student.firstName} ${student.lastName}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Group and Village
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailChip(
                    icon = painterResource(id = R.drawable.ic_group),
                    label = "Group",
                    value = student.groupName
                )
                DetailChip(
                    icon = painterResource(id = R.drawable.ic_location_on),
                    label = "Village",
                    value = student.villageName
                )
            }

            // Gender
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    when (student.gender.lowercase()) {
                        "male" -> painterResource(id = R.drawable.ic_male)
                        "female" -> painterResource(id = R.drawable.ic_female)
                        else -> painterResource(id = R.drawable.ic_gender_neutral)
                    },
                    contentDescription = "Gender",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = student.gender,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Contact Number
            student.primaryContactNo?.let { phone ->
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                OutlinedButton(
                    onClick = { onCallContact(phone) },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_phone),
                        contentDescription = "Call"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(phone)
                }
            }
        }
    }
}

@Composable
fun DetailChip(
    icon: Painter,
    label: String,
    value: String
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SecondaryDetailsSection(student: StudentResponse) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Additional Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider()

            // School Class
            student.schoolClass?.let {
                SecondaryDetailRow(label = "Class", value = it)
            }

            // Year of Birth
            student.yearOfBirth?.let {
                SecondaryDetailRow(label = "Year of Birth", value = it.toString())
            }

            // Father's Name
            student.fathersName?.let {
                SecondaryDetailRow(label = "Father's Name", value = it)
            }

            // Mother's Name
            student.mothersName?.let {
                SecondaryDetailRow(label = "Mother's Name", value = it)
            }

            // Secondary Contact
            student.secondaryContactNo?.let {
                SecondaryDetailRow(label = "Secondary Contact", value = it)
            }

            // Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (student.isActive)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = if (student.isActive) "Active" else "Inactive",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (student.isActive)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
fun SecondaryDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun GroupHistorySection(onViewGroupHistory: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onViewGroupHistory
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = "Group History",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "View Group Transition History",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "View",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AttendanceSummarySection(
    lastPresentDate: String?,
    presentCountLastWeek: Int,
    presentCountLastMonth: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Attendance Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Divider()

            // Last Present Date
            if (lastPresentDate != null) {
                AttendanceStatRow(
                    icon = painterResource(id = R.drawable.ic_calendar_today),
                    label = "Last Present",
                    value = AttendanceUtils.formatDate(lastPresentDate)
                )
            } else {
                AttendanceStatRow(
                    icon = painterResource(id = R.drawable.ic_calendar_today),
                    label = "Last Present",
                    value = "Never"
                )
            }

            // Last Week Count
            AttendanceStatRow(
                icon = painterResource(id = R.drawable.ic_date_range),
                label = "Present in Last Week",
                value = "$presentCountLastWeek days"
            )

            // Last Month Count
            AttendanceStatRow(
                icon = painterResource(id = R.drawable.ic_event),
                label = "Present in Last Month",
                value = "$presentCountLastMonth days"
            )

            // View Detailed History Button
            OutlinedButton(
                onClick = { /* TODO: Navigate to detailed attendance history */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Detailed Attendance History")
            }
        }
    }
}

@Composable
fun AttendanceStatRow(
    icon: Painter,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOptionsBottomSheet(
    hasProfilePic: Boolean,
    onDismiss: () -> Unit,
    onEditFaceData: () -> Unit,
    onViewFullScreen: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Photo Options",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (hasProfilePic) {
                BottomSheetOption(
                    icon = painterResource(id = R.drawable.ic_fullscreen),
                    text = "View Full Screen",
                    onClick = onViewFullScreen
                )
            }

            BottomSheetOption(
                icon = painterResource(id = R.drawable.ic_face),
                text = if (hasProfilePic) "Edit Facial Data" else "Add Facial Data",
                onClick = onEditFaceData
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupHistoryBottomSheet(
    groupHistory: List<StudentGroupHistoryResponse>?,
    onDismiss: () -> Unit,
    onVolunteerClick: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Group Transition History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (groupHistory == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Loading history...")
                    }
                }
            } else {
                if(groupHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No group transitions found.")
                    }
                }
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groupHistory) { history ->
                        GroupHistoryItem(
                            history = history,
                            onVolunteerClick = onVolunteerClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GroupHistoryItem(
    history: StudentGroupHistoryResponse,
    onVolunteerClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Timeline indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = AttendanceUtils.formatDateTime(history.assignedAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (history.oldGroupName != null) {
                        Text(
                            text = history.oldGroupName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_forward),
                            contentDescription = "changed to",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = history.newGroupName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "by volunteer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = { onVolunteerClick(history.assignedByPid) },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = history.assignedByPid,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun BottomSheetOption(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentProfileLayoutPreview() {
    JagratiAndroidTheme {
        StudentProfileLayout(
            uiState = StudentProfileUiState(
                student = StudentResponse(
                    pid = "STU001",
                    firstName = "Rahul",
                    lastName = "Kumar",
                    yearOfBirth = 2010,
                    gender = "Male",
                    profilePic = null,
                    schoolClass = "5th",
                    villageId = 1,
                    villageName = "Village A",
                    groupId = 1,
                    groupName = "Group 1",
                    primaryContactNo = "9876543210",
                    secondaryContactNo = null,
                    fathersName = "Ram Kumar",
                    mothersName = "Sita Devi",
                    isActive = true
                ),
                lastPresentDate = "2025-10-20",
                presentCountLastWeek = 3,
                presentCountLastMonth = 12,
                canEditProfile = true
            ),
            onNavigateBack = {},
            onRefresh = {},
            onPhotoClick = {},
            onAddFaceData = {},
            onEditProfile = {},
            onCallContact = {},
            onViewGroupHistory = {},
            onDismissGroupHistory = {},
            onDismissEditOptions = {},
            onVolunteerClick = {}
        )
    }
}
