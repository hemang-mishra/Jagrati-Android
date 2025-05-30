package com.hexagraph.jagrati_android.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel

@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navigateToAttendancePage: () -> Unit,
    navigateToLogin: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
){
    Column(modifier = Modifier.fillMaxSize()) {
        DashboardScreenTitle(
            onClickSettings = {},
            onSignOut = {
                authViewModel.signOut()
                navigateToLogin()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navigateToAttendancePage() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Take Student Attendance")
        }
    }
}

@Composable
fun DashboardScreenTitle(
    modifier: Modifier = Modifier,
    farmerName: String = "Volunteer",
    onClickSettings: () -> Unit,
    onSignOut: () -> Unit
){
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Settings button
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onClickSettings
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }

        // Sign out button
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 48.dp),
            onClick = onSignOut
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
        }

        // User info
        Row {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Dashboard Icon",
                modifier = Modifier
                    .size(60.dp)
                    .padding(top = 8.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Hello",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
                Text(farmerName, fontSize = 20.sp)
            }
        }
    }
}
