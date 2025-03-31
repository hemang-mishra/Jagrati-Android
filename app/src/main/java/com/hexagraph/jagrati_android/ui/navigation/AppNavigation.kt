package com.hexagraph.jagrati_android.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hexagraph.jagrati_android.ui.screens.home.HomeScreen
import com.hexagraph.jagrati_android.ui.screens.studentAttendance.StudentAttendanceScreen

@Composable
fun AppNavigation(
    navController: NavController = rememberNavController(),
    snackbarHostState: SnackbarHostState,
){
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.NavHomeRoute
    ){
        composable<Screens.NavHomeRoute> {
            HomeScreen(
                snackbarHostState = snackbarHostState,
                navigateToAttendancePage = {
                    navController.navigate(Screens.NavAttendanceRoute)
                },
            )
        }
        composable<Screens.NavAttendanceRoute> {
            StudentAttendanceScreen(
                snackbarHostState = snackbarHostState
            )
        }
    }
}