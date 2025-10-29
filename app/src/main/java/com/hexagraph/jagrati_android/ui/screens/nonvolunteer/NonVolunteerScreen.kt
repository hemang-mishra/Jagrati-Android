package com.hexagraph.jagrati_android.ui.screens.nonvolunteer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.AuthViewModel
import com.hexagraph.jagrati_android.util.AppPreferences
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Calendar

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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Improved Header
        NonVolunteerHeader(
            userName = userName,
            userEmail = userEmail,
            profileImageUrl = profileImageUrl,
            onSettingsClick = onSettingsClick,
            onSignOutClick = onSignOutClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Section Header
        Text(
            text = "Available Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        //TODO: Re-enable Events once the feature is ready
        // View Events Card
//        ImprovedActionCard(
//            title = "View Events",
//            description = "Browse and explore upcoming and past events",
//            icon = Icons.Default.DateRange,
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            iconColor = MaterialTheme.colorScheme.primary,
//            onClick = onViewEventsClick
//        )

        // Create Volunteer Request Card (if allowed)
        if (canCreateVolunteerRequest) {
            ImprovedActionCard(
                title = "Become a Volunteer",
                description = "Submit a request to register as a volunteer",
                icon = Icons.Default.Add,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconColor = MaterialTheme.colorScheme.tertiary,
                onClick = onCreateVolunteerRequestClick
            )
        }

        // View My Volunteer Requests Card
        ImprovedActionCard(
            title = "My Volunteer Requests",
            description = "Check the status of your volunteer applications",
            icon = Icons.AutoMirrored.Filled.List,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            iconColor = MaterialTheme.colorScheme.secondary,
            onClick = onViewMyRequestsClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Information Card
        ImprovedInfoCard(
            title = "Why Register as a Volunteer?",
            description = "To access all features including student management, attendance tracking, village and group data, and more, you need to register as a volunteer and get verified by our team. This ensures the security and privacy of sensitive information.",
            icon = Icons.Default.Info,
            containerColor = MaterialTheme.colorScheme.surface,
            iconColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * NonVolunteerHeader - Simple header following HomeScreen pattern
 */
@Composable
fun NonVolunteerHeader(
    userName: String,
    userEmail: String?,
    profileImageUrl: String?,
    onSettingsClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Empty space on left for symmetry (or could add menu icon later)
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Sign Out button on right
            IconButton(onClick = onSignOutClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Greeting and User Name
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

/**
 * Get greeting message based on time of day
 */
fun getGreeting(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

/**
 * ImprovedActionCard - A visually enhanced action card following the HomeScreen design pattern
 */
@Composable
fun ImprovedActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }

            // Text Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }

            // Chevron Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * ImprovedInfoCard - A visually enhanced info card following the StudentProfileScreen design pattern
 */
@Composable
fun ImprovedInfoCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.4f)
            )
        }
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
