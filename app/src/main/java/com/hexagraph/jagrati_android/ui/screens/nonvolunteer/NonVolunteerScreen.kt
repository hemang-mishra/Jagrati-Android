package com.hexagraph.jagrati_android.ui.screens.nonvolunteer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.ui.components.ActionCard
import com.hexagraph.jagrati_android.ui.components.GreetingCard
import com.hexagraph.jagrati_android.ui.components.InfoCard
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * NonVolunteerScreen - Main screen for users who are not registered as volunteers.
 *
 * This screen provides limited functionality for non-volunteers including:
 * - Viewing events
 * - Creating volunteer requests
 * - Viewing their volunteer request status
 * - Information about volunteer registration
 */
@Composable
fun NonVolunteerScreen(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigateToEvents: () -> Unit = {},
    navigateToCreateVolunteerRequest: () -> Unit = {},
    navigateToMyVolunteerRequests: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    authViewModel: AuthViewModel = koinViewModel(),
    appPreferences: AppPreferences = koinInject()
) {
    val scrollState = rememberScrollState()
    var userData by remember { mutableStateOf<User?>(null) }
    var canCreateVolunteerRequest by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        userData = appPreferences.userDetails.get()
        // Check if user can create volunteer request (you can add more logic here)
        canCreateVolunteerRequest = true
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        NonVolunteerScreenLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            userName = userData?.firstName ?: "User",
            userEmail = userData?.email,
            profileImageUrl = userData?.photoUrl,
            canCreateVolunteerRequest = canCreateVolunteerRequest,
            onSettingsClick = navigateToSettings,
            onSignOutClick = {
                authViewModel.signOut()
                navigateToLogin()
            },
            onViewEventsClick = navigateToEvents,
            onCreateVolunteerRequestClick = navigateToCreateVolunteerRequest,
            onViewMyRequestsClick = navigateToMyVolunteerRequests
        )
    }
}

/**
 * NonVolunteerScreenLayout - Layout composable for the non-volunteer screen.
 * This is separated from the screen composable to allow for easy previewing without ViewModels.
 */
@Composable
fun NonVolunteerScreenLayout(
    userName: String,
    userEmail: String?,
    profileImageUrl: String?,
    canCreateVolunteerRequest: Boolean,
    onSettingsClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onViewEventsClick: () -> Unit,
    onCreateVolunteerRequestClick: () -> Unit,
    onViewMyRequestsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Greeting Card
        GreetingCard(
            userName = userName,
            userEmail = userEmail,
            profileImageUrl = profileImageUrl,
            greeting = "Welcome",
            showSettingsButton = true,
            showSignOutButton = true,
            onSettingsClick = onSettingsClick,
            onSignOutClick = onSignOutClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Section Header
        Text(
            text = "Available Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // View Events Action
        ActionCard(
            title = "View Events",
            description = "Browse and explore upcoming and past events",
            icon = Icons.Default.DateRange,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onViewEventsClick
        )

        // Create Volunteer Request Action (if allowed)
        if (canCreateVolunteerRequest) {
            ActionCard(
                title = "Become a Volunteer",
                description = "Submit a request to register as a volunteer",
                icon = Icons.Default.Add,
                iconTint = MaterialTheme.colorScheme.tertiary,
                onClick = onCreateVolunteerRequestClick
            )
        }

        // View My Volunteer Requests Action
        ActionCard(
            title = "My Volunteer Requests",
            description = "Check the status of your volunteer applications",
            icon = Icons.AutoMirrored.Filled.List,
            iconTint = MaterialTheme.colorScheme.secondary,
            onClick = onViewMyRequestsClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Information Card
        InfoCard(
            title = "Why Register as a Volunteer?",
            description = "To access all features including student management, attendance tracking, village and group data, and more, you need to register as a volunteer and get verified by our team. This ensures the security and privacy of sensitive information.",
            icon = Icons.Default.Info,
            iconTint = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun NonVolunteerScreenLayoutPreview() {
    JagratiAndroidTheme {
        NonVolunteerScreenLayout(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            profileImageUrl = null,
            canCreateVolunteerRequest = true,
            onSettingsClick = {},
            onSignOutClick = {},
            onViewEventsClick = {},
            onCreateVolunteerRequestClick = {},
            onViewMyRequestsClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun NonVolunteerScreenLayoutDarkPreview() {
    JagratiAndroidTheme(darkTheme = true) {
        NonVolunteerScreenLayout(
            userName = "Jane Smith",
            userEmail = "jane.smith@example.com",
            profileImageUrl = null,
            canCreateVolunteerRequest = false,
            onSettingsClick = {},
            onSignOutClick = {},
            onViewEventsClick = {},
            onCreateVolunteerRequestClick = {},
            onViewMyRequestsClick = {}
        )
    }
}
