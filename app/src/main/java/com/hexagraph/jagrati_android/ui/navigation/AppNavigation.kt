package com.hexagraph.jagrati_android.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.hexagraph.jagrati_android.ui.screens.onboarding.OnboardingScreen1
import com.hexagraph.jagrati_android.ui.screens.onboarding.OnboardingScreen2
import com.hexagraph.jagrati_android.ui.screens.onboarding.OnboardingScreen3
import com.hexagraph.jagrati_android.ui.screens.onboarding.PermissionsScreen
import com.hexagraph.jagrati_android.ui.screens.studentAttendance.StudentAttendanceScreen
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

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
            !isOnboardingCompleted -> Screens.NavOnboarding1Route
            isAuthenticated -> Screens.NavHomeRoute
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
            entry<Screens.NavOnboarding1Route> {
                OnboardingScreen1(
                    onNextClick = {
                        backstack.add(Screens.NavOnboarding2Route)
                    }
                )
            }
            entry<Screens.NavOnboarding2Route> {
                OnboardingScreen2(
                    onNextClick = {
                        backstack.add(Screens.NavOnboarding3Route)
                    },
                    onBackClick = {
                        backstack.popBackStack()
                    }
                )
            }
            entry<Screens.NavOnboarding3Route> {
                OnboardingScreen3(
                    onNextClick = {
                        backstack.add(Screens.NavPermissionsRoute)
                    },
                    onBackClick = {
                        backstack.popBackStack()
                    }
                )
            }
            entry<Screens.NavPermissionsRoute> {
                PermissionsScreen(
                    onAllPermissionsGranted = {
                        onboardingPreferences.setOnboardingCompleted()
                        if (isAuthenticated) {
                            backstack.clear()
                            backstack.add(Screens.NavHomeRoute)
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
            entry<Screens.NavLoginRoute> {
                LoginScreen(
                    snackbarHostState = snackbarHostState,
                    navigateToHome = {
                        backstack.clear()
                        backstack.add(Screens.NavHomeRoute)
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
                HomeScreen(
                    snackbarHostState = snackbarHostState,
                    navigateToAttendancePage = {
                        backstack.add(Screens.NavAttendanceRoute)
                    },
                    navigateToLogin = {
                        backstack.clear()
                        backstack.add(Screens.NavLoginRoute)
                    }
                )
            }
            entry<Screens.NavAttendanceRoute> {
                StudentAttendanceScreen(
                    snackbarHostState = snackbarHostState,
                    onBackPress = {
                        backstack.popBackStack()
                    }
                )
            }
        }

    )

}
