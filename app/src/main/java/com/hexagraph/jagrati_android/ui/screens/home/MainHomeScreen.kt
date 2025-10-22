package com.hexagraph.jagrati_android.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/**
 * Main home screen with bottom navigation, side drawer, and nested navigation.
 */
@Composable
fun MainHomeScreen(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigateToLogin: () -> Unit = {},
    navigateToManagement: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    navigateToStudentRegistrationScreen: () -> Unit,
    navigateToStudentList: () -> Unit = {},
    navigateToVolunteerList: () -> Unit = {},
    navigateToStudentProfile: (String) -> Unit = {},
    navigateToVolunteerProfile: (String) -> Unit = {},
    updateFacialData: (String) -> Unit,
    onSearchClick: () -> Unit,
    navigateToAttendanceMarking: () -> Unit = {},
    navigateToFullScreenImage: (ImageKitResponse) -> Unit,
    navigateToCameraSearch: () -> Unit,
    appPreferences: AppPreferences = koinInject(),
) {
    var userData by remember { mutableStateOf<User?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedBottomNavItem by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        appPreferences.userDetails.getFlow().collect {
            userData = it
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
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
                    })
            }
        }) {
        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedBottomNavItem,
                onItemSelected = { selectedBottomNavItem = it })
        }) { paddingValues ->
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
                        onSearchByCameraClick = { navigateToCameraSearch() })

                    1 -> AttendanceReportScreen(
                        onNavigateToStudentProfile = navigateToStudentProfile,
                        onNavigateToVolunteerProfile = navigateToVolunteerProfile,
                        snackbarHostState = snackbarHostState,
                        onNavigateToTakeAttendance = navigateToAttendanceMarking
                    )

                    2 -> SyllabusScreen()
                    3 -> MyProfileScreen(
                        onNavigateToEditProfile = {
                            // TODO: Navigate to edit profile when implemented
                        }, onNavigateToFaceDataRegister = { pid ->
                            updateFacialData(pid)
                        }, onNavigateToFullScreenImage = { imageData ->
                            navigateToFullScreenImage(imageData)
                        }, snackbarHostState = snackbarHostState
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DrawerHeader(
            userName = userName, userEmail = userEmail, profileImageUrl = profileImageUrl
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
    selectedItem: Int, onItemSelected: (Int) -> Unit, modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 3.dp
    ) {
        NavigationBarItem(
            selected = selectedItem == 0, onClick = { onItemSelected(0) }, icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home_filled),
                    contentDescription = "Home"
                )
            }, label = { Text("Home") }, alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = selectedItem == 1, onClick = { onItemSelected(1) }, icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_attendance_filled),
                    contentDescription = "Attendance"
                )
            }, label = { Text("Attendance") }, alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = selectedItem == 2, onClick = { onItemSelected(2) }, icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_syllabus_filled),
                    contentDescription = "Syllabus"
                )
            }, label = { Text("Syllabus") }, alwaysShowLabel = true
        )

        NavigationBarItem(
            selected = selectedItem == 3, onClick = { onItemSelected(3) }, icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile_filled),
                    contentDescription = "Profile"
                )
            }, label = { Text("Profile") }, alwaysShowLabel = true
        )
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
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (onMenuClick != null) {
                IconButton(
                    onClick = onMenuClick, modifier = Modifier.align(Alignment.Start)
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
            onLogoutClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    JagratiAndroidTheme {
        BottomNavigationBar(
            selectedItem = 0, onItemSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
fun MockScreenPreview() {
    JagratiAndroidTheme {
        MockScreen(
            title = "Home", description = "Welcome to Jagrati!", onMenuClick = {})
    }
}
