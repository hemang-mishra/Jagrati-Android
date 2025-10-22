package com.hexagraph.jagrati_android.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.ImageKitResponse
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.ui.components.DrawerDivider
import com.hexagraph.jagrati_android.ui.components.DrawerHeader
import com.hexagraph.jagrati_android.ui.components.DrawerItem
import com.hexagraph.jagrati_android.ui.components.DrawerSectionHeader
import com.hexagraph.jagrati_android.ui.screens.attendancereport.AttendanceReportScreen
import com.hexagraph.jagrati_android.ui.screens.myprofile.MyProfileScreen
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Main home screen with bottom navigation, side drawer, and nested navigation.
 */
@Composable
fun MainHomeScreen(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigateToLogin: () -> Unit = {},
    navigateToManagement: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    navigateToStudentRegistrationScreen: ()-> Unit,
    navigateToStudentList: () -> Unit = {},
    navigateToVolunteerList: () -> Unit = {},
    navigateToStudentProfile: (String) -> Unit = {},
    navigateToVolunteerProfile: (String) -> Unit = {},
    updateFacialData: (String) -> Unit,
    onSearchClick: () -> Unit,
    navigateToAttendanceMarking: () -> Unit = {},
    navigateToFullScreenImage : (ImageKitResponse) -> Unit,
    appPreferences: AppPreferences = koinInject(),
) {
    var userData by remember { mutableStateOf<User?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedBottomNavItem by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        appPreferences.userDetails.getFlow().collect {
            userData = it
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
            ) {
                DrawerContent(
                    userName = userData?.firstName ?: "User",
                    userEmail = userData?.email,
                    profileImageUrl = userData?.photoUrl,
                    onManagementClick = {
                        scope.launch { drawerState.close() }
                        navigateToManagement()
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        // TODO: Navigate to settings
                    },
                    onTakeStudentAttendanceClick = {
                        scope.launch { drawerState.close() }
                        navigateToAttendanceMarking()
                    },
                    onRegisterNewStudentClick = {
                        scope.launch { drawerState.close() }
                        navigateToStudentRegistrationScreen()
                    },
                    onStudentListClick = {
                        scope.launch { drawerState.close() }
                        navigateToStudentList()
                    },
                    onVolunteerListClick = {
                        scope.launch { drawerState.close() }
                        navigateToVolunteerList()
                    },
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                        authViewModel.signOut()
                        navigateToLogin()
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                BottomNavigationBar(
                    selectedItem = selectedBottomNavItem,
                    onItemSelected = { selectedBottomNavItem = it }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedBottomNavItem) {
                    0 -> HomeContentScreen(
                        userName = userData?.firstName ?: "User",
                        onSearchClick = onSearchClick,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onTakeAttendanceClick = navigateToAttendanceMarking,
                        onRegisterStudentClick = navigateToStudentRegistrationScreen,
                        onSearchByCameraClick = { /* TODO: Navigate to camera search */ }
                    )
                    1 -> AttendanceReportScreen(
                        onNavigateToStudentProfile = navigateToStudentProfile,
                        onNavigateToVolunteerProfile = navigateToVolunteerProfile,
                        snackbarHostState = snackbarHostState
                    )
                    2 -> SyllabusScreen()
                    3 -> MyProfileScreen(
                        onNavigateToEditProfile = {
                            // TODO: Navigate to edit profile when implemented
                        },
                        onNavigateToFaceDataRegister = { pid ->
                            updateFacialData(pid)
                        },
                        onNavigateToFullScreenImage = { imageData ->
                            navigateToFullScreenImage(imageData)
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

/**
 * Drawer content layout component.
 */
@Composable
fun DrawerContent(
    userName: String,
    userEmail: String?,
    profileImageUrl: String?,
    onManagementClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTakeStudentAttendanceClick: () -> Unit,
    onRegisterNewStudentClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onStudentListClick: () -> Unit = {},
    onVolunteerListClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        DrawerHeader(
            userName = userName,
            userEmail = userEmail,
            profileImageUrl = profileImageUrl
        )

        DrawerDivider()

        DrawerSectionHeader(title = "ADMIN CONTROLS")

        DrawerItem(
            label = "Management",
            icon = R.drawable.ic_management,
            onClick = onManagementClick,
            colorIndex = 0
        )

        DrawerItem(
            label = "Settings",
            icon = R.drawable.ic_settings,
            onClick = onSettingsClick,
            colorIndex = 1
        )

        DrawerDivider()

        DrawerSectionHeader(title = "LISTS")

        DrawerItem(
            label = "Student List",
            icon = R.drawable.ic_person,
            onClick = onStudentListClick,
            colorIndex = 2
        )

        DrawerItem(
            label = "Volunteer List",
            icon = R.drawable.ic_person,
            onClick = onVolunteerListClick,
            colorIndex = 3
        )

        DrawerDivider()

        DrawerSectionHeader(title = "ATTENDANCE & REGISTRATION")

        DrawerItem(
            label = "Take Attendance",
            icon = R.drawable.ic_attendance,
            onClick = onTakeStudentAttendanceClick,
            colorIndex = 3
        )

        DrawerItem(
            label = "Register New Student",
            icon = R.drawable.ic_person_add,
            onClick = onRegisterNewStudentClick,
            colorIndex = 4
        )

        DrawerDivider()

        DrawerItem(
            label = "Log out",
            icon = R.drawable.ic_logout,
            onClick = onLogoutClick,
            iconTint = MaterialTheme.colorScheme.error,
            colorIndex = 0
        )
    }
}

/**
 * Bottom navigation bar component.
 */
@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 3.dp
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home_filled),
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_attendance_filled),
                    contentDescription = "Attendance"
                )
            },
            label = { Text("Attendance") },
            alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_syllabus_filled),
                    contentDescription = "Syllabus"
                )
            },
            label = { Text("Syllabus") },
            alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_filled),
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            alwaysShowLabel = true
        )
    }
}

// Home Dashboard Screen

@Composable
fun HomeContentScreen(
    onOpenDrawer: () -> Unit,
    onSearchClick: () -> Unit,
    userName: String = "User",
    notificationCount: Int = 3,
    onTakeAttendanceClick: () -> Unit,
    onRegisterStudentClick: () -> Unit,
    onSearchByCameraClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with greeting, notifications, and drawer
        item {
            HomeHeader(
                userName = userName,
                notificationCount = notificationCount,
                onOpenDrawer = onOpenDrawer,
                onNotificationClick = { /* TODO: Navigate to notifications */ }
            )
        }

        // Take Attendance Card
        item {
            TakeAttendanceCard(
                onClick = onTakeAttendanceClick
            )
        }

        // Register New Student Card
        item {
            RegisterStudentCard(
                onClick = onRegisterStudentClick
            )
        }

        // Search Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Find Students",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            SearchOptions(
                onSearchByCamera = onSearchByCameraClick,
                onSearchByName = onSearchClick
            )
        }

        // Quick Stats (Optional)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            QuickStatsSection()
        }
    }
}

@Composable
fun HomeHeader(
    userName: String,
    notificationCount: Int,
    onOpenDrawer: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: Menu and Notifications
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge {
                            Text(notificationCount.toString())
                        }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Greeting
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = getGreeting(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TakeAttendanceCard(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Take Attendance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Scan QR codes to mark attendance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            // QR Scanner Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qr_code),
                    contentDescription = "QR Scanner",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RegisterStudentCard(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Found someone new?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Register them as a student",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_person_add),
                    contentDescription = "Add Student",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SearchOptions(
    onSearchByCamera: () -> Unit,
    onSearchByName: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchOptionCardWithDrawable(
            title = "Search by Camera",
            iconRes = R.drawable.ic_photo_camera,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            iconColor = MaterialTheme.colorScheme.tertiary,
            onClick = onSearchByCamera,
            modifier = Modifier.weight(1f)
        )

        SearchOptionCardWithVector(
            title = "Search by Name",
            icon = Icons.Default.Search,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            iconColor = MaterialTheme.colorScheme.primary,
            onClick = onSearchByName,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SearchOptionCardWithDrawable(
    title: String,
    iconRes: Int,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SearchOptionCardWithVector(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Remove or keep the old SearchOptionCard for compatibility
@Composable
fun SearchOptionCard(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchOptionCardWithVector(
        title = title,
        icon = icon,
        containerColor = containerColor,
        iconColor = iconColor,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun QuickStatsSection() {
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
                text = "Today's Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatItem(
                    label = "Students",
                    value = "127",
                    modifier = Modifier.weight(1f)
                )
                QuickStatItem(
                    label = "Volunteers",
                    value = "24",
                    modifier = Modifier.weight(1f)
                )
                QuickStatItem(
                    label = "Groups",
                    value = "8",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun getGreeting(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

@Composable
fun SyllabusScreen() {
    MockScreen(
        title = "Syllabus",
        description = "Access course materials, lesson plans, and educational content."
    )
}

/**
 * Mock screen component for placeholder screens.
 */
@Composable
fun MockScreen(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onMenuClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (onMenuClick != null) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_touch_app_24),
                        contentDescription = "Open Menu",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentScreenPreview() {
    JagratiAndroidTheme {
        HomeContentScreen(
            onOpenDrawer = {},
            onSearchClick = {},
            userName = "Hemang",
            onTakeAttendanceClick = {},
            onRegisterStudentClick = {},
            onSearchByCameraClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerContentPreview() {
    JagratiAndroidTheme {
        DrawerContent(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            profileImageUrl = null,
            onManagementClick = {},
            onSettingsClick = {},
            onTakeStudentAttendanceClick = {},
            onRegisterNewStudentClick = {},
            onLogoutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    JagratiAndroidTheme {
        BottomNavigationBar(
            selectedItem = 0,
            onItemSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MockScreenPreview() {
    JagratiAndroidTheme {
        MockScreen(
            title = "Home",
            description = "Welcome to Jagrati!",
            onMenuClick = {}
        )
    }
}
