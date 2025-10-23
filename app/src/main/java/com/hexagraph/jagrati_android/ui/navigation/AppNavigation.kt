package com.hexagraph.jagrati_android.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.hexagraph.jagrati_android.model.ImageKitResponse
import com.hexagraph.jagrati_android.preferences.OnboardingPreferences
import com.hexagraph.jagrati_android.ui.viewmodels.AppViewModel
import com.hexagraph.jagrati_android.ui.screens.attendance.AttendanceMarkingScreen
import com.hexagraph.jagrati_android.ui.screens.attendance.AttendanceMarkingViewModel
import com.hexagraph.jagrati_android.ui.screens.attendancereport.AttendanceReportScreen
import com.hexagraph.jagrati_android.ui.screens.attendanceview.AttendanceViewScreen
import com.hexagraph.jagrati_android.ui.screens.auth.EmailVerificationScreen
import com.hexagraph.jagrati_android.ui.screens.auth.ForgotPasswordScreen
import com.hexagraph.jagrati_android.ui.screens.auth.LoginScreen
import com.hexagraph.jagrati_android.ui.screens.auth.SignUpDetailsScreen
import com.hexagraph.jagrati_android.ui.screens.auth.SignUpEmailScreen
import com.hexagraph.jagrati_android.ui.screens.management.ManagementScreen
import com.hexagraph.jagrati_android.ui.screens.onboarding.OnboardingScreen
import com.hexagraph.jagrati_android.ui.screens.onboarding.PermissionsScreen
import com.hexagraph.jagrati_android.ui.screens.permissions.ManagePermissionsScreen
import com.hexagraph.jagrati_android.ui.screens.permissions.PermissionDetailScreen
import com.hexagraph.jagrati_android.ui.screens.permissions.PermissionDetailViewModel
import com.hexagraph.jagrati_android.ui.screens.roles.ManageRolesScreen
import com.hexagraph.jagrati_android.ui.screens.details_sync.DetailsSyncScreen
import com.hexagraph.jagrati_android.ui.screens.editvolunteerprofile.EditVolunteerProfileScreen
import com.hexagraph.jagrati_android.ui.screens.home.MainHomeScreen
import com.hexagraph.jagrati_android.ui.screens.nonvolunteer.NonVolunteerScreen
import com.hexagraph.jagrati_android.ui.screens.userroles.UserDetailScreen
import com.hexagraph.jagrati_android.ui.screens.userroles.UserRolesScreen
import com.hexagraph.jagrati_android.ui.screens.village.VillageManagementScreen
import com.hexagraph.jagrati_android.ui.screens.group.GroupManagementScreen
import com.hexagraph.jagrati_android.ui.screens.volunteer.MyVolunteerRequestsScreen
import com.hexagraph.jagrati_android.ui.screens.volunteer.VolunteerRegistrationScreen
import com.hexagraph.jagrati_android.ui.screens.volunteer.manage.ManageVolunteerRequestsScreen
import com.hexagraph.jagrati_android.ui.screens.student.StudentRegistrationScreen
import com.hexagraph.jagrati_android.ui.screens.studentlist.StudentListScreen
import com.hexagraph.jagrati_android.ui.screens.studentprofile.StudentProfileScreen
import com.hexagraph.jagrati_android.ui.screens.volunteerlist.VolunteerListScreen
import com.hexagraph.jagrati_android.ui.screens.facedata.FaceDataRegisterScreen
import com.hexagraph.jagrati_android.ui.screens.facedata.FaceDataRegisterViewModel
import com.hexagraph.jagrati_android.ui.screens.imageviewer.FullScreenImageViewer
import com.hexagraph.jagrati_android.ui.screens.search.UnifiedSearchScreen
import com.hexagraph.jagrati_android.ui.screens.search.UnifiedSearchViewModel
import com.hexagraph.jagrati_android.ui.screens.volunteerprofile.VolunteerProfileScreen
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// Function to handle back navigation
private fun NavBackStack.popBackStack() {
    if (isNotEmpty()) {
        removeAt(size - 1)
    }
}

@Composable
fun AppNavigation(
    snackbarHostState: SnackbarHostState,
    authViewModel: AuthViewModel = koinViewModel(),
    appViewModel: AppViewModel,
    shouldLogout: Boolean
) {
    // Get context for preferences
    val context = LocalContext.current

    // Check if onboarding has been completed
    val onboardingPreferences = remember { OnboardingPreferences.getInstance(context) }
    val isOnboardingCompleted = remember { onboardingPreferences.isOnboardingCompleted() }

    // Check if user is authenticated
    val isAuthenticated = authViewModel.isUserAuthenticated()

    val backstack = rememberNavBackStack(
        when {
            !isOnboardingCompleted -> Screens.NavOnboardingRoute
            isAuthenticated -> Screens.DetailsSyncRoute // Load user details first if authenticated
            else -> Screens.NavLoginRoute
        }
    )

    // Handle automatic logout when refresh token becomes null/empty
    LaunchedEffect(shouldLogout) {
        if (shouldLogout) {
            backstack.clear()
            backstack.add(Screens.NavLoginRoute)

            appViewModel.onLogoutHandled()

            snackbarHostState.showSnackbar("Session expired. Please login again.")
        }
    }

    NavDisplay(
        backStack = backstack,
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            // Unified onboarding route
            entry<Screens.NavOnboardingRoute> {
                OnboardingScreen(
                    onCompleteOnboarding = {
                        backstack.add(Screens.NavPermissionsRoute)
                    }
                )
            }

            entry<Screens.NavPermissionsRoute> {
                PermissionsScreen(
                    onAllPermissionsGranted = {
                        onboardingPreferences.setOnboardingCompleted()
                        if (isAuthenticated) {
                            backstack.clear()
                            backstack.add(Screens.DetailsSyncRoute) // Go to UserDetails screen if authenticated
                        } else {
                            backstack.clear()
                            backstack.add(Screens.NavLoginRoute)
                        }
                    },
                    onBackClick = {
                        if (backstack.isNotEmpty()) {
                            backstack.removeAt(backstack.size - 1)
                        }
                    }
                )
            }

            // User details route - loads after authentication
            entry<Screens.DetailsSyncRoute> {
                DetailsSyncScreen(
                    snackbarHostState = snackbarHostState,
                    redirectToNonVolunteerDashboard = {
                        backstack.clear()
                        backstack.add(Screens.NavNonVolunteerHomeScreen)
                    },
                    redirectToVolunteerDashboard = {
                        backstack.clear()
                        backstack.add(Screens.NavHomeRoute)
                    },
                    onLogout = {
                        appViewModel.logout()
                    }
                )
            }

            // Authentication routes
            entry<Screens.NavLoginRoute> {
                LoginScreen(
                    snackbarHostState = snackbarHostState,
                    navigateToHome = {
                        backstack.clear()
                        backstack.add(Screens.DetailsSyncRoute) // Go to UserDetails first
                    },
                    navigateToSignUp = {
                        backstack.add(Screens.NavSignUpEmailRoute)
                    },
                    navigateToForgotPassword = {
                        backstack.add(Screens.NavForgotPasswordRoute)
                    },
                    navigateToEmailVerification = { email ->
                        backstack.add(Screens.NavEmailVerificationRoute(email))
                    },
                )
            }
            entry<Screens.NavSignUpEmailRoute> {
                SignUpEmailScreen(
                    snackbarHostState = snackbarHostState,
                    navigateToSignUpDetails = { email ->
                        backstack.add(Screens.NavSignUpDetailsRoute(email))
                    },
                    navigateToLogin = {
                        backstack.popBackStack()
                    }
                )
            }

            entry<Screens.NavSignUpDetailsRoute> { it ->
                val email = it.email
                SignUpDetailsScreen(
                    email = email,
                    snackbarHostState = snackbarHostState,
                    navigateToEmailVerification = { verificationEmail ->
                        backstack.clear()
                        backstack.add(Screens.NavEmailVerificationRoute(verificationEmail))
                    },
                    navigateBack = {
                        backstack.popBackStack()
                    }
                )
            }
            entry<Screens.NavForgotPasswordRoute> {
                ForgotPasswordScreen(
                    snackbarHostState = snackbarHostState,
                    navigateToLogin = {
                        backstack.popBackStack()
                    }
                )
            }
            entry<Screens.NavEmailVerificationRoute> { it ->
                val email = it.email
                EmailVerificationScreen(
                    email = email,
                    snackbarHostState = snackbarHostState,
                    navigateToLogin = {
                        backstack.clear()
                        backstack.add(Screens.NavLoginRoute)
                    }
                )
            }
            // Main app routes
            entry<Screens.NavHomeRoute> {
                MainHomeScreen(
                    snackbarHostState = snackbarHostState,
                    navigateToLogin = {
                        backstack.clear()
                        backstack.add(Screens.NavLoginRoute)
                    },
                    navigateToManagement = {
                        backstack.add(Screens.NavManagementRoute)
                    },
                    navigateToStudentRegistrationScreen = {
                        backstack.add(Screens.NavStudentRegistrationRoute())
                    },
                    navigateToStudentList = {
                        backstack.add(Screens.NavStudentListRoute)
                    },
                    navigateToVolunteerList = {
                        backstack.add(Screens.NavVolunteerListRoute)
                    },
                    navigateToStudentProfile = { pid ->
                        backstack.add(Screens.NavStudentProfileRoute(pid))
                    },
                    navigateToVolunteerProfile = { pid ->
                        backstack.add(Screens.NavVolunteerProfileRoute(pid))
                    },
                    onSearchClick = {
                        backstack.add(Screens.NavUnifiedSearchRoute)
                    },
                    updateFacialData = {
                        backstack.add(Screens.NavFaceDataRegisterRoute(it))
                    },
                    navigateToAttendanceMarking = {
                        backstack.add(Screens.NavCameraAttendanceMarkingRoute)
                    },
                    navigateToFullScreenImage = {
                        backstack.add(
                            Screens.NavFullScreenImageRoute(
                                imageUrl = it.url,
                                imageName = it.name,
                                fileId = it.fileId
                            )
                        )
                    },
                    navigateToCameraSearch = {
                        backstack.add(
                            Screens.NavCameraSearchRoute
                        )
                    },
                    navigateToEditProfile = {
                        backstack.add(
                            Screens.NavEditVolunteerProfileRoute(it)
                        )
                    }
                )
            }

            // Management screens
            entry<Screens.NavManagementRoute> {
                ManagementScreen(
                    snackbarHostState = snackbarHostState,
                    onNavigateToScreen = { screen ->
                        backstack.add(screen)
                    },
                    onBackPressed = {
                        backstack.popBackStack()
                    }
                )
            }

            // Role management screens
            entry<Screens.NavManageRolesRoute> {
                ManageRolesScreen(
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    }
                )
            }

            // Permission management screens
            entry<Screens.NavManagePermissionsRoute> {
                ManagePermissionsScreen(
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    },
                    onPermissionClicked = { permissionId ->
                        backstack.add(Screens.NavPermissionDetailRoute(permissionId))
                    }
                )
            }

            entry<Screens.NavPermissionDetailRoute> { it ->
                val permissionId = it.permissionId

                PermissionDetailScreen(
                    permissionId = permissionId,
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    },
                    viewModel = koinViewModel<PermissionDetailViewModel> { parametersOf(permissionId) }
                )
            }

            // User role management screens
            entry<Screens.NavUserRoleManagementRoute> {
                UserRolesScreen(
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    },
                    onUserClicked = { userPid ->
                        backstack.add(Screens.NavUserDetailRoute(userPid))
                    }
                )
            }

            entry<Screens.NavUserDetailRoute> { it ->
                val userPid = it.userPid

                UserDetailScreen(
                    userPid = userPid,
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    }
                )
            }

            // Volunteer screens
            entry<Screens.NavVolunteerRegistrationRoute> {
                VolunteerRegistrationScreen(
                    viewModel = koinViewModel(),
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    },
                    navigateToMyRequests = {
                        // Replace the current screen with My Requests screen
                        backstack.removeAt(backstack.size - 1)
                        backstack.add(Screens.NavMyVolunteerRequestsRoute)
                    }
                )
            }

            entry<Screens.NavMyVolunteerRequestsRoute> {
                MyVolunteerRequestsScreen(
                    viewModel = koinViewModel(),
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    },
                    navigateToVolunteerRegistration = {
                        backstack.add(Screens.NavVolunteerRegistrationRoute)
                    }
                )
            }

            entry<Screens.NavVillageManagementRoute> {
                VillageManagementScreen(
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    }
                )
            }

            entry<Screens.NavGroupManagementRoute> {
                GroupManagementScreen(
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    }
                )
            }

            entry<Screens.NavManageVolunteerRequestsRoute> {
                ManageVolunteerRequestsScreen(
                    viewModel = koinViewModel(),
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    }
                )
            }

            // Student management screens
            entry<Screens.NavStudentRegistrationRoute> { it ->
                val pid = it.pid

                StudentRegistrationScreen(
                    viewModel = koinViewModel { parametersOf(pid) },
                    snackbarHostState = snackbarHostState,
                    onBackPressed = {
                        backstack.popBackStack()
                    },
                    navigateToFacialData = {pid->
                        backstack.popBackStack()
                        backstack.add(Screens.NavFaceDataRegisterRoute(pid))
                    }
                )
            }

            entry<Screens.NavStudentListRoute> {
                StudentListScreen(
                    onBackPress = {
                        backstack.popBackStack()
                    },
                    onStudentClick = { pid: String ->
                        backstack.add(Screens.NavStudentProfileRoute(pid))
                    },
                    onSearchClick = {
                        backstack.add(Screens.NavUnifiedSearchRoute)
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            // Student Profile Screen
            entry<Screens.NavStudentProfileRoute> { it ->
                val pid = it.pid

                StudentProfileScreen(
                    pid = pid,
                    onNavigateBack = {
                        backstack.popBackStack()
                    },
                    onNavigateToFaceDataRegister = { studentPid ->
                        backstack.add(Screens.NavFaceDataRegisterRoute(studentPid))
                    },
                    onNavigateToEditProfile = { studentPid ->
                        backstack.add(Screens.NavStudentRegistrationRoute(studentPid))
                    },
                    onNavigateToVolunteerProfile = { volunteerPid ->
                        backstack.add(Screens.NavVolunteerProfileRoute(volunteerPid))
                    },
                    onNavigateToAttendanceDetails = { studentPid ->
                        backstack.add(
                            Screens.NavDetailedAttendanceViewRoute(
                                pid = studentPid,
                                isStudent = true
                            )
                        )
                    },
                    onNavigateToFullScreenImage = { imageData ->
                        backstack.add(
                            Screens.NavFullScreenImageRoute(
                                imageUrl = imageData.url,
                                imageName = imageData.name,
                                fileId = imageData.fileId
                            )
                        )
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            entry<Screens.NavVolunteerListRoute> {
                VolunteerListScreen(
                    onBackPress = {
                        backstack.popBackStack()
                    },
                    onVolunteerClick = { pid: String ->
                        backstack.add(Screens.NavVolunteerProfileRoute(pid))
                    },
                    onSearchClick = {
                        backstack.add(Screens.NavUnifiedSearchRoute)
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            entry<Screens.NavVolunteerProfileRoute> { it ->
                val pid = it.pid

                VolunteerProfileScreen(
                    pid = pid,
                    onNavigateBack = {
                        backstack.popBackStack()
                    },
                    onNavigateToFullScreenImage = { imageData ->
                        backstack.add(
                            Screens.NavFullScreenImageRoute(
                                imageUrl = imageData.url,
                                imageName = imageData.name,
                                fileId = imageData.fileId
                            )
                        )
                    },
                    snackbarHostState = snackbarHostState,
                    onViewAttendanceDetails = {
                        backstack.add(
                            Screens.NavDetailedAttendanceViewRoute(
                                pid = pid,
                                isStudent = false
                            )
                        )
                    },

                )
            }

            entry<Screens.NavEditVolunteerProfileRoute> { it ->
                val pid = it.pid

                EditVolunteerProfileScreen(
                    pid = pid,
                    onNavigateBack = {
                        backstack.popBackStack()
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            entry<Screens.NavUnifiedSearchRoute> {
                UnifiedSearchScreen(
                    onBackPress = {
                        backstack.popBackStack()
                    },
                    onSelect = { pid: String, isStudent: Boolean ->
                        if(isStudent)
                            backstack.add(Screens.NavStudentProfileRoute(pid))
                        else
                            backstack.add(Screens.NavVolunteerProfileRoute(pid))
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            entry<Screens.NavUnifiedSearchAttendanceRoute> {
                val vm = koinViewModel<UnifiedSearchViewModel>()
                vm.selectedDateMillis = it.dateMillis
                UnifiedSearchScreen(
                    viewModel = vm,
                    onBackPress = {
                        backstack.popBackStack()
                    },
                    onSelect = { pid: String, isStudent: Boolean ->
                        vm.markAttendance(pid, isStudent){
                            Toast.makeText(context, "Attendance marked!!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            // Attendance marking screen
            entry<Screens.NavCameraAttendanceMarkingRoute> {
                val vm = koinViewModel<AttendanceMarkingViewModel>()

                AttendanceMarkingScreen(
                    viewModel = vm,
                    onNavigateBack = {
                        backstack.popBackStack()
                    },
                    onPersonSelect = {
                        pid: String, isStudent: Boolean ->
                        vm.markAttendance(pid, isStudent){
                            Toast.makeText(context, "Attendance marked!!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onTextSearchClick = {
                        backstack.popBackStack()
                        backstack.add(Screens.NavUnifiedSearchAttendanceRoute(it))
                    },
                    isSearching = false
                )
            }

            entry<Screens.NavCameraSearchRoute>{
                val vm = koinViewModel<AttendanceMarkingViewModel>()
                AttendanceMarkingScreen(
                    viewModel = vm,
                    onNavigateBack = {
                        backstack.popBackStack()
                    },
                    onPersonSelect = {
                            pid: String, isStudent: Boolean ->
                        //Navigate to the respective screen
                        if(isStudent)
                            backstack.add(Screens.NavStudentProfileRoute(pid))
                        else
                            backstack.add(Screens.NavVolunteerProfileRoute(pid))
                    },
                    onTextSearchClick = {
                        backstack.add(Screens.NavUnifiedSearchRoute)
                    },
                    isSearching = true
                )
            }

            // Face data screens
            entry<Screens.NavFaceDataRegisterRoute> { it ->
                val pid = it.pid

                FaceDataRegisterScreen(
                    viewModel = koinViewModel<FaceDataRegisterViewModel> { parametersOf(pid) },
                    onNavigateBack = {
                        backstack.popBackStack()
                    }
                )
            }

            // Full screen image viewer
            entry<Screens.NavFullScreenImageRoute> { it ->
                val imageData = ImageKitResponse(
                    fileId = it.fileId,
                    name = it.imageName,
                    url = it.imageUrl
                )

                FullScreenImageViewer(
                    imageData = imageData,
                    onBackClick = {
                        backstack.popBackStack()
                    }
                )
            }

            entry<Screens.NavNonVolunteerHomeScreen>{
                //Add NonVolunteer HomeScreen
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.Cyan)
                ){
                    NonVolunteerScreen(
                        snackbarHostState = snackbarHostState,
                        navigateToEvents = { /* TODO: Implement navigation to events */ },
                        navigateToCreateVolunteerRequest = {
                            backstack.add(Screens.NavVolunteerRegistrationRoute)
                        },
                        navigateToMyVolunteerRequests = {
                            backstack.add(Screens.NavMyVolunteerRequestsRoute)
                        },
                        navigateToSettings = {
                            backstack.add(Screens.NavManagementRoute)
                        },
                        navigateToLogin = {
                            backstack.clear()
                            backstack.add(Screens.NavLoginRoute)
                        },
                        authViewModel = authViewModel,
                    )
                   }
            }

            entry<Screens.NavDetailedAttendanceViewRoute>{
                AttendanceViewScreen(
                    pid = it.pid,
                    isStudent = it.isStudent,
                    onNavigateBack = {
                        backstack.popBackStack()
                    }
                )
            }
        }
    )
}
