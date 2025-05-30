package com.hexagraph.jagrati_android.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

@Composable
fun AppNavigation(
    navController: NavController = rememberNavController(),
    snackbarHostState: SnackbarHostState,
    authViewModel: AuthViewModel = hiltViewModel(),
    onGoogleSignInClick: () -> Unit = {},
    googleIdToken: String? = null
){
    // Get context for preferences
    val context = LocalContext.current

    // Check if onboarding has been completed
    val onboardingPreferences = remember { OnboardingPreferences.getInstance(context) }
    val isOnboardingCompleted = remember { onboardingPreferences.isOnboardingCompleted() }

    // Check if user is authenticated
    val isAuthenticated = authViewModel.isUserAuthenticated()

    // Determine start destination
    val startDestination = when {
        !isOnboardingCompleted -> Screens.NavOnboarding1Route
        isAuthenticated -> Screens.NavHomeRoute
        else -> Screens.NavLoginRoute
    }

    NavHost(
        navController = navController as NavHostController,
        startDestination = startDestination
    ){
        // Onboarding routes
        composable<Screens.NavOnboarding1Route> {
            OnboardingScreen1(
                onNextClick = {
                    navController.navigate(Screens.NavOnboarding2Route)
                }
            )
        }

        composable<Screens.NavOnboarding2Route> {
            OnboardingScreen2(
                onNextClick = {
                    navController.navigate(Screens.NavOnboarding3Route)
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screens.NavOnboarding3Route> {
            OnboardingScreen3(
                onNextClick = {
                    navController.navigate(Screens.NavPermissionsRoute)
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screens.NavPermissionsRoute> {
            PermissionsScreen(
                onAllPermissionsGranted = {
                    // Mark onboarding as completed
                    onboardingPreferences.setOnboardingCompleted()

                    // Navigate to login or home based on authentication status
                    if (isAuthenticated) {
                        navController.navigate(Screens.NavHomeRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screens.NavLoginRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        // Authentication routes
        composable<Screens.NavLoginRoute> {
            LoginScreen(
                snackbarHostState = snackbarHostState,
                navigateToHome = {
                    navController.navigate(Screens.NavHomeRoute) {
                        popUpTo(Screens.NavLoginRoute) { inclusive = true }
                    }
                },
                navigateToSignUp = {
                    navController.navigate(Screens.NavSignUpEmailRoute)
                },
                navigateToForgotPassword = {
                    navController.navigate(Screens.NavForgotPasswordRoute)
                },
                navigateToEmailVerification = { email ->
                    navController.navigate("${Screens.NavEmailVerificationRoute}/$email")
                },
                onGoogleSignInClick = onGoogleSignInClick,
                googleIdToken = googleIdToken
            )
        }

        composable<Screens.NavSignUpEmailRoute> {
            SignUpEmailScreen(
                snackbarHostState = snackbarHostState,
                navigateToSignUpDetails = { email ->
                    navController.navigate("${Screens.NavSignUpDetailsRoute}/$email")
                },
                navigateToLogin = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = "${Screens.NavSignUpDetailsRoute}/{${Screens.NavSignUpDetailsRoute.EMAIL_ARG}}",
            arguments = listOf(
                navArgument(Screens.NavSignUpDetailsRoute.EMAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(Screens.NavSignUpDetailsRoute.EMAIL_ARG) ?: ""
            SignUpDetailsScreen(
                email = email,
                snackbarHostState = snackbarHostState,
                navigateToEmailVerification = { verificationEmail ->
                    navController.navigate("${Screens.NavEmailVerificationRoute}/$verificationEmail") {
                        popUpTo(Screens.NavLoginRoute)
                    }
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screens.NavForgotPasswordRoute> {
            ForgotPasswordScreen(
                snackbarHostState = snackbarHostState,
                navigateToLogin = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = "${Screens.NavEmailVerificationRoute}/{${Screens.NavEmailVerificationRoute.EMAIL_ARG}}",
            arguments = listOf(
                navArgument(Screens.NavEmailVerificationRoute.EMAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(Screens.NavEmailVerificationRoute.EMAIL_ARG) ?: ""
            EmailVerificationScreen(
                email = email,
                snackbarHostState = snackbarHostState,
                navigateToLogin = {
                    navController.navigate(Screens.NavLoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Main app routes
        composable<Screens.NavHomeRoute> {
            HomeScreen(
                snackbarHostState = snackbarHostState,
                navigateToAttendancePage = {
                    navController.navigate(Screens.NavAttendanceRoute)
                },
                navigateToLogin = {
                    navController.navigate(Screens.NavLoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Screens.NavAttendanceRoute> {
            StudentAttendanceScreen(
                snackbarHostState = snackbarHostState,
                onBackPress = {
                    navController.navigateUp()
                }
            )
        }
    }
}
