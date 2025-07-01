package com.hexagraph.jagrati_android.ui.screens.volunteer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.volunteer.MyVolunteerRequestResponse
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MyVolunteerRequestsScreen(
    viewModel: VolunteerRequestViewModel,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    navigateToVolunteerRegistration: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadMyVolunteerRequests()
    }

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(message = error.toast)
            viewModel.clearErrorFlow()
        }
    }

    LaunchedEffect(key1 = uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message = message)
            viewModel.clearMsgFlow()
        }
    }

    MyVolunteerRequestsScreenLayout(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onRefresh = { viewModel.loadMyVolunteerRequests() },
        navigateToVolunteerRegistration = navigateToVolunteerRegistration,
        hasPendingRequests = uiState.hasPendingRequests
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVolunteerRequestsScreenLayout(
    uiState: VolunteerRequestUiState,
    onBackPressed: () -> Unit,
    onRefresh: () -> Unit,
    navigateToVolunteerRegistration: () -> Unit,
    hasPendingRequests: Boolean
) {
    val pullRefreshState = rememberPullToRefreshState()


    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Volunteer Requests",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullToRefresh(state = pullRefreshState, isRefreshing = uiState.isLoading, onRefresh = onRefresh)
        ) {
            if (uiState.myRequests.isEmpty() && !uiState.isLoading) {
                // Empty state
                EmptyRequestsState(
                    hasPendingRequests = hasPendingRequests,
                    onCreateRequestClicked = navigateToVolunteerRegistration
                )
            } else {
                // List of requests
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    items(uiState.myRequests) { request ->
                        RequestCard(request = request)
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // Add button to create new request if no pending requests
                    if (!hasPendingRequests) {
                        item {
                            Button(
                                onClick = navigateToVolunteerRegistration,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Create New Request",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun RequestCard(request: MyVolunteerRequestResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = when (request.status) {
            "APPROVED" -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            "PENDING" -> BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
            "REJECTED" -> BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
            else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status and timestamp row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Request ID
                Text(
                    text = "Request #${request.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                // Status badge
                RequestStatusBadge(status = request.status)
            }

            // Divider
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ) {}

            // Date information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateInfoItem(
                    title = "Submitted on",
                    date = request.createdAt,
                    icon = R.drawable.ic_assignment_ind
                )

                if (request.reviewedAt != null) {
                    DateInfoItem(
                        title = "Reviewed on",
                        date = request.reviewedAt,
                        icon = R.drawable.baseline_touch_app_24
                    )
                }
            }

            // Reason for rejection (if applicable)
            if (request.status == "REJECTED" && !request.message.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_description),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Reason: ${request.message}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestStatusBadge(status: String) {
    val (backgroundColor, contentColor, statusText) = when (status) {
        "APPROVED" -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.primary,
            "Approved"
        )
        "PENDING" -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.tertiary,
            "Pending"
        )
        "REJECTED" -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.error,
            "Rejected"
        )
        else -> Triple(
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.outline,
            status
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        contentColor = contentColor
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DateInfoItem(
    title: String,
    date: LocalDateTime,
    icon: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = formatDateTime(date),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmptyRequestsState(
    hasPendingRequests: Boolean,
    onCreateRequestClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon in a circle
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_assignment_ind),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (hasPendingRequests) "Pending Request" else "No Volunteer Requests",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasPendingRequests)
                "You have a pending volunteer request. Please wait for approval."
            else
                "You haven't submitted any volunteer requests yet.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = !hasPendingRequests,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = onCreateRequestClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Create New Request",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Helper function to format DateTime
private fun formatDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    return dateTime.format(formatter)
}

@Preview(showBackground = true)
@Composable
fun MyVolunteerRequestsPreview() {
    JagratiAndroidTheme {
        val requests = listOf(
            MyVolunteerRequestResponse(
                id = 1001,
                status = "PENDING",
                createdAt = LocalDateTime.now().minusDays(2),
                reviewedAt = null,
                message = null
            ),
            MyVolunteerRequestResponse(
                id = 1002,
                status = "APPROVED",
                createdAt = LocalDateTime.now().minusDays(10),
                reviewedAt = LocalDateTime.now().minusDays(8),
                message = null
            ),
            MyVolunteerRequestResponse(
                id = 1003,
                status = "REJECTED",
                createdAt = LocalDateTime.now().minusDays(15),
                reviewedAt = LocalDateTime.now().minusDays(13),
                message = "Your profile doesn't meet the current requirements."
            )
        )

        MyVolunteerRequestsScreenLayout(
            uiState = VolunteerRequestUiState(myRequests = requests),
            onBackPressed = {},
            onRefresh = {},
            navigateToVolunteerRegistration = {},
            hasPendingRequests = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyRequestsPreview() {
    JagratiAndroidTheme {
        MyVolunteerRequestsScreenLayout(
            uiState = VolunteerRequestUiState(myRequests = emptyList()),
            onBackPressed = {},
            onRefresh = {},
            navigateToVolunteerRegistration = {},
            hasPendingRequests = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PendingRequestEmptyPreview() {
    JagratiAndroidTheme {
        MyVolunteerRequestsScreenLayout(
            uiState = VolunteerRequestUiState(myRequests = emptyList()),
            onBackPressed = {},
            onRefresh = {},
            navigateToVolunteerRegistration = {},
            hasPendingRequests = true
        )
    }
}
