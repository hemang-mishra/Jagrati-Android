package com.hexagraph.jagrati_android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.hexagraph.jagrati_android.preferences.OnboardingPreferences
import com.hexagraph.jagrati_android.ui.screens.auth.EmailVerificationScreen
import com.hexagraph.jagrati_android.ui.screens.auth.ForgotPasswordScreen
import com.hexagraph.jagrati_android.ui.screens.auth.LoginScreen
import com.hexagraph.jagrati_android.ui.screens.auth.SignUpDetailsScreen
import com.hexagraph.jagrati_android.ui.screens.auth.SignUpEmailScreen
import com.hexagraph.jagrati_android.ui.screens.home.HomeScreen
import com.hexagraph.jagrati_android.ui.screens.management.ManagementScreen
import com.hexagraph.jagrati_android.ui.screens.onboarding.OnboardingScreen
import com.hexagraph.jagrati_android.ui.screens.onboarding.PermissionsScreen
import com.hexagraph.jagrati_android.ui.screens.permissions.ManagePermissionsScreen
import com.hexagraph.jagrati_android.ui.screens.permissions.PermissionDetailScreen
import com.hexagraph.jagrati_android.ui.screens.permissions.PermissionDetailViewModel
import com.hexagraph.jagrati_android.ui.screens.roles.ManageRolesScreen
import com.hexagraph.jagrati_android.ui.screens.details_sync.DetailsSyncScreen
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
import com.hexagraph.jagrati_android.ui.screens.facedata.FaceDataRegisterScreen
import com.hexagraph.jagrati_android.ui.screens.facedata.FaceDataRegisterViewModel
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
) {
    // Get context for preferences
    val context = LocalContext.current

    // Check if onboarding has been completed
    val onboardingPreferences = remember { OnboardingPreferences.getInstance(context) }
    val isOnboardingCompleted = remember { onboardingPreferences.isOnboardingCompleted() }

    // Check if user is authenticated
    val isAuthenticated = authViewModel.isUserAuthenticated()

    var backstack = rememberNavBackStack(
        when {
            !isOnboardingCompleted -> Screens.NavOnboardingRoute
            isAuthenticated -> Screens.DetailsSyncRoute // Load user details first if authenticated
            else -> Screens.NavLoginRoute
        }
    )

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
                    updateFacialData = {
                        backstack.add(Screens.NavFaceDataRegisterRoute(it))
                    }
                )
//                HomeScreen(
//                    snackbarHostState = snackbarHostState,
//                    navigateToLogin = {
//                        backstack.clear()
//                        backstack.add(Screens.NavLoginRoute)
//                    },
//                    navigateToManagement = {
//                        backstack.add(Screens.NavManagementRoute)
//                    },
//                    navigateToVolunteerRegistration = {
//                        backstack.add(Screens.NavVolunteerRegistrationRoute)
//                    }
//                )
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
        }
    )
}
