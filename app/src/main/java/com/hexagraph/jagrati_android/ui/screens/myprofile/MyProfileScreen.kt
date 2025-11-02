package com.hexagraph.jagrati_android.ui.screens.myprofile

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.ImageKitResponse
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.ui.components.ProfileAvatar
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.util.AttendanceUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyProfileScreen(
    onNavigateToEditProfile: (String) -> Unit = {},
    onNavigateToFaceDataRegister: (String) -> Unit = {},
    onNavigateToFullScreenImage: (ImageKitResponse) -> Unit = {},
    viewModel: MyProfileViewModel = koinViewModel(),
    onAttendanceDetailedView: (String, Boolean) -> Unit,
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

    MyProfileLayout(
        uiState = uiState,
        onRefresh = { viewModel.refresh() },
        onPhotoClick = { viewModel.showEditOptionsSheet() },
        onEditProfile = {
            uiState.currentUser?.pid?.let { onNavigateToEditProfile(it) }
        },
        onAddFaceData = {
            uiState.currentUser?.pid?.let { onNavigateToFaceDataRegister(it) }
        },
        onChatWhatsApp = { phoneNumber ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://wa.me/${phoneNumber.replace("+", "").replace(" ", "")}".toUri()
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://api.whatsapp.com/send?phone=${phoneNumber.replace("+", "").replace(" ", "")}".toUri()
                }
                context.startActivity(fallbackIntent)
            }
        },
        onDismissEditOptions = { viewModel.hideEditOptionsSheet() },
        onViewFullScreenImage = { imageData ->
            viewModel.hideEditOptionsSheet()
            onNavigateToFullScreenImage(imageData)
        },
        onViewDetails = {
            val pid = uiState.volunteer?.pid ?: uiState.currentUser?.pid ?: return@MyProfileLayout
            onAttendanceDetailedView(pid, false)
        },
        onDeleteFaceData = {
            viewModel.deleteFaceData()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileLayout(
    uiState: MyProfileUiState,
    onRefresh: () -> Unit,
    onPhotoClick: () -> Unit,
    onEditProfile: () -> Unit,
    onAddFaceData: () -> Unit,
    onChatWhatsApp: (String) -> Unit,
    onDismissEditOptions: () -> Unit,
    onViewDetails: () -> Unit,
    onDeleteFaceData: () -> Unit,
    onViewFullScreenImage: (ImageKitResponse) -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.isLoading && uiState.volunteer == null) {
            MyProfileShimmerLoading()
        } else if (uiState.volunteer != null || uiState.currentUser != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    MyProfileTopBar(
                        currentUser = uiState.currentUser,
                        volunteer = uiState.volunteer,
                        onEditProfile = onEditProfile,
                        onPhotoClick = onPhotoClick
                    )
                }

                item {
                    PrimaryDetailsSection(
                        volunteer = uiState.volunteer,
                        userRoles = uiState.userRoles,
                        onChatWhatsApp = onChatWhatsApp
                    )
                }

                item {
                    AttendanceSummarySection(
                        lastPresentDate = uiState.lastPresentDate,
                        presentCountLastMonth = uiState.presentCountLastMonth,
                        viewDetails = onViewDetails
                    )
                }

                item {
                    SecondaryDetailsSection(volunteer = uiState.volunteer)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Failed to load profile")
            }
        }
    }

    if (uiState.showEditOptionsSheet) {
        EditOptionsBottomSheet(
            hasProfilePic = uiState.volunteer?.profilePic != null || uiState.currentUser?.photoUrl != null,
            profilePicData = uiState.volunteer?.profilePic,
            onDismiss = onDismissEditOptions,
            onEditFaceData = {
                onDismissEditOptions()
                onAddFaceData()
            },
            onViewFullScreen = { imageData ->
                onViewFullScreenImage(imageData)
            },
            onDeleteFaceData = {
                onDismissEditOptions()
                onDeleteFaceData()
            }
        )
    }
}

@Composable
fun MyProfileTopBar(
    currentUser: User?,
    volunteer: VolunteerDTO?,
    onEditProfile: () -> Unit,
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(scale)
                        .clickable(
                            onClick = {
                                isPressed = true
                                onPhotoClick()
                            },
                            onClickLabel = "View photo"
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val profileUrl = volunteer?.profilePic?.url ?: currentUser?.photoUrl
                    val userName = "${volunteer?.firstName ?: currentUser?.firstName ?: "User"} ${volunteer?.lastName ?: currentUser?.lastName ?: ""}"

                    if (profileUrl != null) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(4.dp, CircleShape)
                        ) {
                            ProfileAvatar(
                                userName = userName,
                                profileImageUrl = profileUrl,
                                size = 72.dp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "No Photo",
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Edit Photo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${volunteer?.firstName ?: currentUser?.firstName ?: "User"} ${volunteer?.lastName ?: currentUser?.lastName ?: ""}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    volunteer?.rollNumber?.let { rollNo ->
                        Text(
                            text = rollNo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    currentUser?.email?.let { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            IconButton(
                onClick = onEditProfile,
                modifier = Modifier
                    .size(44.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit Profile",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
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

@Composable
fun PrimaryDetailsSection(
    volunteer: VolunteerDTO?,
    userRoles: List<String>,
    onChatWhatsApp: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (userRoles.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Assigned Roles",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
                userRoles.chunked(2).forEach { rowRoles ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowRoles.forEach { role ->
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_shield),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = role,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                        if (rowRoles.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        volunteer?.gender?.let { gender ->
            InfoChip(
                icon = when (gender) {
                    Gender.MALE -> painterResource(id = R.drawable.ic_male)
                    Gender.FEMALE -> painterResource(id = R.drawable.ic_female)
                    else -> painterResource(id = R.drawable.ic_gender_neutral)
                },
                label = "Gender",
                value = gender.name,
                containerColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (volunteer?.programme != null && volunteer.branch != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChip(
                    icon = painterResource(id = R.drawable.ic_category),
                    label = "Programme",
                    value = volunteer.programme,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                InfoChip(
                    icon = painterResource(id = R.drawable.ic_category),
                    label = "Branch",
                    value = volunteer.branch,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            volunteer?.programme?.let { programme ->
                InfoChip(
                    icon = painterResource(id = R.drawable.ic_category),
                    label = "Programme",
                    value = programme,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            volunteer?.branch?.let { branch ->
                InfoChip(
                    icon = painterResource(id = R.drawable.ic_category),
                    label = "Branch",
                    value = branch,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        volunteer?.contactNumber?.let { phone ->
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.97f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "whatsapp_button_scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),
                onClick = {
                    isPressed = true
                    onChatWhatsApp(phone)
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF25D366).copy(alpha = 0.15f)
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
                            .background(Color(0xFF25D366)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Contact Number",
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
fun SecondaryDetailsSection(volunteer: VolunteerDTO?) {
    if (volunteer == null) return

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

            // Personal Information
            volunteer.yearOfStudy?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_calendar_today),
                    label = "Year of Study",
                    value = it.toString()
                )
            }

            if (!volunteer.dateOfBirth.isNullOrBlank()) {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_calendar_today),
                    label = "Date of Birth",
                    value = volunteer.dateOfBirth
                )
            }

            volunteer.batch?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_event),
                    label = "Batch",
                    value = it
                )
            }

            volunteer.college?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_school),
                    label = "College",
                    value = it
                )
            }

            volunteer.alternateEmail?.let {
                if (it.isNotBlank()) {
                    DetailRow(
                        icon = painterResource(id = R.drawable.ic_email),
                        label = "Alternate Email",
                        value = it
                    )
                }
            }

            // Address Information
            val hasAddress = !volunteer.streetAddress1.isNullOrBlank() ||
                    !volunteer.streetAddress2.isNullOrBlank() ||
                    volunteer.city != null ||
                    volunteer.state != null ||
                    volunteer.pincode != null

            if (hasAddress) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Text(
                    text = "Address",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            volunteer.streetAddress1?.let {
                if (it.isNotBlank()) {
                    DetailRow(
                        icon = painterResource(id = R.drawable.ic_location),
                        label = "Street Address 1",
                        value = it
                    )
                }
            }

            volunteer.streetAddress2?.let {
                if (it.isNotBlank()) {
                    DetailRow(
                        icon = painterResource(id = R.drawable.ic_location),
                        label = "Street Address 2",
                        value = it
                    )
                }
            }

            volunteer.city?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_location),
                    label = "City",
                    value = it
                )
            }

            volunteer.state?.let {
                DetailRow(
                    icon = painterResource(id = R.drawable.ic_location_on),
                    label = "State",
                    value = it
                )
            }

            volunteer.pincode?.let {
                if (it.isNotBlank()) {
                    DetailRow(
                        icon = painterResource(id = R.drawable.ic_location_on),
                        label = "Pincode",
                        value = it
                    )
                }
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
    presentCountLastMonth: Int,
    viewDetails: ()->Unit
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
                    text = "My Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

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
                    icon = painterResource(id = R.drawable.ic_event),
                    label = "Present in Last Month",
                    value = "$presentCountLastMonth days",
                    isHighlighted = presentCountLastMonth > 0
                )
            }

            Button(
                onClick = viewDetails,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOptionsBottomSheet(
    hasProfilePic: Boolean,
    profilePicData: ImageKitResponse?,
    onDismiss: () -> Unit,
    onEditFaceData: () -> Unit,
    onDeleteFaceData: () -> Unit,
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

                BottomSheetOption(
                    icon = painterResource(R.drawable.ic_delete),
                    text = "Delete Facial Data",
                    onClick = onDeleteFaceData
                )
            }

            BottomSheetOption(
                icon = painterResource(id = R.drawable.ic_face),
                text = if (hasProfilePic) "Update Facial Data" else "Add Facial Data",
                onClick = onEditFaceData
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BottomSheetOption(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyProfilePreview() {
    JagratiAndroidTheme {
        MyProfileLayout(
            uiState = MyProfileUiState(
                currentUser = User(
                    pid = "V001",
                    firstName = "Rajesh",
                    lastName = "Kumar",
                    email = "rajesh.kumar@iiitdmj.ac.in",
                    photoUrl = null,
                ),
                volunteer = VolunteerDTO(
                    pid = "V001",
                    rollNumber = "2021BCS001",
                    firstName = "Rajesh",
                    lastName = "Kumar",
                    gender = Gender.MALE,
                    alternateEmail = null,
                    batch = "2021",
                    programme = "B.Tech",
                    streetAddress2 = null,
                    pincode = null,
                    city = "Jabalpur",
                    state = "Madhya Pradesh",
                    dateOfBirth = "2003-05-15",
                    contactNumber = "+919876543210",
                    college = "IIITDMJ",
                    branch = "Computer Science",
                    yearOfStudy = 3,
                    profilePic = null,
                    isActive = true,
                    streetAddress1 = ""
                ),
                userRoles = listOf("Teacher", "Coordinator"),
                lastPresentDate = "2024-10-20",
                presentCountLastMonth = 15
            ),
            onRefresh = {},
            onPhotoClick = {},
            onEditProfile = {},
            onAddFaceData = {},
            onChatWhatsApp = {},
            onDismissEditOptions = {},
            onViewFullScreenImage = {},
            onViewDetails = {},
            onDeleteFaceData = {}
        )
    }
}
