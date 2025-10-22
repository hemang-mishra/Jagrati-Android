package com.hexagraph.jagrati_android.ui.screens.studentprofile

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.ImageKitResponse
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
    onNavigateToAttendanceDetails: (String) -> Unit = {},
    onNavigateToFullScreenImage: (ImageKitResponse) -> Unit = {},
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
        onViewAttendanceDetails = { onNavigateToAttendanceDetails(pid) },
        onViewFullScreenImage = { imageData ->
            viewModel.hideEditOptionsSheet()
            onNavigateToFullScreenImage(imageData)
        },
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
    onViewAttendanceDetails: () -> Unit = {},
    onViewFullScreenImage: (ImageKitResponse) -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Student Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                    actionIconContentColor = MaterialTheme.colorScheme.primary
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.student != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Profile Photo Section with animation
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600)) +
                                    slideInVertically(animationSpec = tween(600))
                        ) {
                            ProfilePhotoSection(
                                student = uiState.student,
                                onPhotoClick = onPhotoClick
                            )
                        }
                    }

                    // Primary Details Section
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                                    slideInVertically(animationSpec = tween(600, delayMillis = 100))
                        ) {
                            PrimaryDetailsSection(
                                student = uiState.student,
                                onCallContact = onCallContact
                            )
                        }
                    }

                    // Attendance Summary Section (moved up for better UX)
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                                    slideInVertically(animationSpec = tween(600, delayMillis = 200))
                        ) {
                            AttendanceSummarySection(
                                lastPresentDate = uiState.lastPresentDate,
                                presentCountLastWeek = uiState.presentCountLastWeek,
                                presentCountLastMonth = uiState.presentCountLastMonth,
                                onViewDetails = onViewAttendanceDetails
                            )
                        }
                    }

                    // Secondary Details Section
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                                    slideInVertically(animationSpec = tween(600, delayMillis = 300))
                        ) {
                            SecondaryDetailsSection(student = uiState.student)
                        }
                    }

                    // Group History Section
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                                    slideInVertically(animationSpec = tween(600, delayMillis = 400))
                        ) {
                            GroupHistorySection(onViewGroupHistory = onViewGroupHistory)
                        }
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
            profilePicData = uiState.student?.profilePic,
            onDismiss = onDismissEditOptions,
            onEditFaceData = {
                onDismissEditOptions()
                onAddFaceData()
            },
            onViewFullScreen = { imageData ->
                onViewFullScreenImage(imageData)
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
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "profile_photo_scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .clickable(
                    onClick = {
                        isPressed = true
                        onPhotoClick()
                    },
                    onClickLabel = "Edit photo"
                ),
            contentAlignment = Alignment.Center
        ) {
            if (student.profilePic != null) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .shadow(12.dp, CircleShape)
                ) {
                    ProfileAvatar(
                        userName = "${student.firstName} ${student.lastName}",
                        profileImageUrl = student.profilePic.url,
                        size = 140.dp
                    )
                }
                // Edit icon badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(44.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                // No photo - show add face data prompt
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_face),
                        contentDescription = "No Photo",
                        modifier = Modifier.size(70.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Name below profile pic
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${student.firstName} ${student.lastName}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Status badge below name
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (student.isActive)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (student.isActive)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                    )
                    Text(
                        text = if (student.isActive) "Active" else "Inactive",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (student.isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Add face data button if no profile pic
        if (student.profilePic == null) {
            Button(
                onClick = onPhotoClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_face),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Facial Data", fontWeight = FontWeight.SemiBold)
            }
        }

        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        }
    }
}

@Composable
fun PrimaryDetailsSection(
    student: StudentResponse,
    onCallContact: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Group and Village chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoChip(
                icon = painterResource(id = R.drawable.ic_group),
                label = "Group",
                value = student.groupName,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            InfoChip(
                icon = painterResource(id = R.drawable.ic_location_on),
                label = "Village",
                value = student.villageName,
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        // Gender chip
        InfoChip(
            icon = when (student.gender.lowercase()) {
                "male" -> painterResource(id = R.drawable.ic_male)
                "female" -> painterResource(id = R.drawable.ic_female)
                else -> painterResource(id = R.drawable.ic_gender_neutral)
            },
            label = "Gender",
            value = student.gender,
            containerColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )

        // Contact Number
        student.primaryContactNo?.let { phone ->
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.97f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "call_button_scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),
                onClick = {
                    isPressed = true
                    onCallContact(phone)
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_phone),
                            contentDescription = "Call",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Primary Contact",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LaunchedEffect(isPressed) {
                if (isPressed) {
                    kotlinx.coroutines.delay(100)
                    isPressed = false
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: Painter,
    label: String,
    value: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = 0.15f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun SecondaryDetailsSection(student: StudentResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // School Class
            student.schoolClass?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_group),
                    label = "Class",
                    value = it
                )
            }

            // Year of Birth
            student.yearOfBirth?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_calendar_today),
                    label = "Year of Birth",
                    value = it.toString()
                )
            }

            // Father's Name
            student.fathersName?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_male),
                    label = "Father's Name",
                    value = it
                )
            }

            // Mother's Name
            student.mothersName?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_female),
                    label = "Mother's Name",
                    value = it
                )
            }

            // Secondary Contact
            student.secondaryContactNo?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_phone),
                    label = "Secondary Contact",
                    value = it
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: Painter,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AttendanceSummarySection(
    lastPresentDate: String?,
    presentCountLastWeek: Int,
    presentCountLastMonth: Int,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_event),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Attendance Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stats Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AttendanceStat(
                    icon = painterResource(id = R.drawable.ic_calendar_today),
                    label = "Last Present",
                    value = if (lastPresentDate != null)
                        AttendanceUtils.formatDate(lastPresentDate)
                    else
                        "Never",
                    isHighlighted = lastPresentDate != null
                )

                AttendanceStat(
                    icon = painterResource(id = R.drawable.ic_date_range),
                    label = "Last Week",
                    value = "$presentCountLastWeek days",
                    isHighlighted = presentCountLastWeek > 0
                )

                AttendanceStat(
                    icon = painterResource(id = R.drawable.ic_event),
                    label = "Last Month",
                    value = "$presentCountLastMonth days",
                    isHighlighted = presentCountLastMonth > 0
                )
            }

            // View Details Button
            Button(
                onClick = onViewDetails,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Detailed Attendance",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AttendanceStat(
    icon: Painter,
    label: String,
    value: String,
    isHighlighted: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isHighlighted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GroupHistorySection(onViewGroupHistory: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "group_history_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = {
            isPressed = true
            onViewGroupHistory()
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history),
                        contentDescription = "Group History",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "Group Transition History",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "View",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOptionsBottomSheet(
    hasProfilePic: Boolean,
    profilePicData: ImageKitResponse?,
    onDismiss: () -> Unit,
    onEditFaceData: () -> Unit,
    onViewFullScreen: (ImageKitResponse) -> Unit
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

            if (hasProfilePic && profilePicData != null) {
                BottomSheetOption(
                    icon = painterResource(id = R.drawable.ic_fullscreen),
                    text = "View Full Screen",
                    onClick = {
                        onViewFullScreen(profilePicData)
                    }
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
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(50)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInHorizontally(animationSpec = tween(400))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Timeline indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    if (history.oldGroupName != null) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = history.oldGroupName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_forward),
                                contentDescription = "changed to",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = history.newGroupName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "by",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = { onVolunteerClick(history.assignedByPid) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = history.assignedByPid,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
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
