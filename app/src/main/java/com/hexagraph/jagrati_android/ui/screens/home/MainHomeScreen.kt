package com.hexagraph.jagrati_android.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.ui.components.DrawerDivider
import com.hexagraph.jagrati_android.ui.components.DrawerHeader
import com.hexagraph.jagrati_android.ui.components.DrawerItem
import com.hexagraph.jagrati_android.ui.components.DrawerSectionHeader
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
    updateFacialData: (String) -> Unit,
    appPreferences: AppPreferences = koinInject()
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
                    onTakeVolunteerAttendanceClick = {
                        scope.launch { drawerState.close() }
                        // TODO: Navigate to volunteer attendance
                    },
                    onTakeStudentAttendanceClick = {
                        scope.launch { drawerState.close() }
                        // TODO: Navigate to student attendance
                    },
                    onRegisterNewStudentClick = {
                        scope.launch { drawerState.close() }
                        navigateToStudentRegistrationScreen()
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
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                    1 -> AttendanceSummaryScreen()
                    2 -> SyllabusScreen()
                    3 -> NotificationsScreen()
                    4 -> ProfileScreen(
                        userData = userData,
                        updateFacialData = {
                            userData?.pid?.let { pid -> updateFacialData(pid) }
                        }
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
    onTakeVolunteerAttendanceClick: () -> Unit,
    onTakeStudentAttendanceClick: () -> Unit,
    onRegisterNewStudentClick: () -> Unit,
    onLogoutClick: () -> Unit
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

        DrawerSectionHeader(title = "ATTENDANCE & REGISTRATION")

        DrawerItem(
            label = "Take Volunteer Attendance",
            icon = R.drawable.ic_attendance,
            onClick = onTakeVolunteerAttendanceClick,
            colorIndex = 2
        )

        DrawerItem(
            label = "Take Student Attendance",
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
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home"
                )
            },
            label = null,
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_attendance),
                    contentDescription = "Attendance"
                )
            },
            label = null,
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_syllabus),
                    contentDescription = "Syllabus"
                )
            },
            label = null,
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = "Notifications"
                )
            },
            label = null,
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = { onItemSelected(4) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Profile"
                )
            },
            label = null,
            alwaysShowLabel = false
        )
    }
}

// Mock Screens for each bottom navigation item

@Composable
fun HomeContentScreen(onOpenDrawer: () -> Unit) {
    MockScreen(
        title = "Home",
        description = "Welcome to Jagrati! This is the home screen where you'll see quick actions and important updates.",
        onMenuClick = onOpenDrawer
    )
}

@Composable
fun AttendanceSummaryScreen() {
    MockScreen(
        title = "Attendance Summary",
        description = "View attendance records and statistics for students and volunteers."
    )
}

@Composable
fun SyllabusScreen() {
    MockScreen(
        title = "Syllabus",
        description = "Access course materials, lesson plans, and educational content."
    )
}

@Composable
fun NotificationsScreen() {
    MockScreen(
        title = "Notifications",
        description = "Stay updated with important announcements and reminders."
    )
}

@Composable
fun ProfileScreen(userData: User?,
                  updateFacialData: ()-> Unit
                  ) {
    MockScreen(
        title = "Profile",
        description = "Manage your profile information and preferences.\n\nUser: ${userData?.firstName ?: "Guest"}"
    )
    Box(modifier = Modifier.fillMaxSize()){
        Button(
            onClick = updateFacialData
        ) {
            Text("Upsert facial data")
        }
    }
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
                androidx.compose.material3.IconButton(
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

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

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
fun DrawerContentPreview() {
    JagratiAndroidTheme {
        DrawerContent(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            profileImageUrl = null,
            onManagementClick = {},
            onSettingsClick = {},
            onTakeVolunteerAttendanceClick = {},
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
