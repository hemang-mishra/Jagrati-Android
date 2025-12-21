package com.hexagraph.jagrati_android.ui.screens.volunteer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.volunteer.MyVolunteerRequestResponse
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.theme.JagratiThemeColors
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
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Volunteer Requests",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .pullToRefresh(state = pullRefreshState, isRefreshing = uiState.isLoading, onRefresh = onRefresh)
        ) {
            if (uiState.myRequests.isEmpty() && !uiState.isLoading) {
                EmptyRequestsState(
                    hasPendingRequests = hasPendingRequests,
                    onCreateRequestClicked = navigateToVolunteerRegistration,
                    isTablet = isTablet
                )
            } else {
                RequestsContent(
                    requests = uiState.myRequests,
                    hasPendingRequests = hasPendingRequests,
                    navigateToVolunteerRegistration = navigateToVolunteerRegistration,
                    isTablet = isTablet,
                    isLandscape = isLandscape
                )
            }
        }
    }
}

@Composable
fun RequestsContent(
    requests: List<MyVolunteerRequestResponse>,
    hasPendingRequests: Boolean,
    navigateToVolunteerRegistration: () -> Unit,
    isTablet: Boolean,
    isLandscape: Boolean
) {
    val horizontalPadding = if (isTablet) 32.dp else 16.dp
    val maxWidth = if (isTablet) 800.dp else Int.MAX_VALUE.dp

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
        ) {
            if (isTablet && isLandscape) {
                // Grid layout for tablet landscape
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = horizontalPadding,
                        vertical = 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(requests) { request ->
                        RequestCard(
                            request = request,
                            isCompact = true
                        )
                    }

                    if (!hasPendingRequests) {
                        item {
                            CreateRequestCard(
                                onClick = navigateToVolunteerRegistration,
                                isCompact = true
                            )
                        }
                    }
                }
            } else {
                // List layout for phone or tablet portrait
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = horizontalPadding,
                        vertical = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(requests) { request ->
                        RequestCard(request = request)
                    }

                    if (!hasPendingRequests) {
                        item {
                            CreateRequestCard(onClick = navigateToVolunteerRegistration)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestCard(
    request: MyVolunteerRequestResponse,
    isCompact: Boolean = false
) {
    val batchColors = JagratiThemeColors.batchColors
    val cardColor = batchColors[request.id.hashCode() % batchColors.size]

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, cardColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                cardColor.copy(alpha = 0.15f),
                                cardColor.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Request #${request.id}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = cardColor
                        )
                    )

                    RequestStatusBadge(
                        status = request.status,
                        accentColor = cardColor
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
            ) {
                // Date information
                if (isCompact) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DateInfoItem(
                            title = "Submitted",
                            date = request.createdAt,
                            icon = R.drawable.ic_assignment_ind,
                            accentColor = cardColor,
                            isCompact = true
                        )

                        if (request.reviewedAt != null) {
                            DateInfoItem(
                                title = "Reviewed",
                                date = request.reviewedAt,
                                icon = R.drawable.baseline_touch_app_24,
                                accentColor = cardColor,
                                isCompact = true
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DateInfoItem(
                            title = "Submitted on",
                            date = request.createdAt,
                            icon = R.drawable.ic_assignment_ind,
                            accentColor = cardColor
                        )

                        if (request.reviewedAt != null) {
                            DateInfoItem(
                                title = "Reviewed on",
                                date = request.reviewedAt,
                                icon = R.drawable.baseline_touch_app_24,
                                accentColor = cardColor
                            )
                        }
                    }
                }

                // Rejection reason
                if (request.status == "REJECTED" && !request.message.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_description),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Rejection Reason",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = request.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestStatusBadge(
    status: String,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val (backgroundColor, contentColor, statusText, icon) = when (status) {
        "APPROVED" -> Tuple4(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.primary,
            "Approved",
            R.drawable.baseline_check_circle_24
        )
        "PENDING" -> Tuple4(
            accentColor.copy(alpha = 0.15f),
            accentColor,
            "Pending",
            R.drawable.baseline_schedule_24
        )
        "REJECTED" -> Tuple4(
            MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.error,
            "Rejected",
            R.drawable.baseline_cancel_24
        )
        else -> Tuple4(
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.outline,
            status,
            R.drawable.baseline_help_outline_24
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun DateInfoItem(
    title: String,
    date: LocalDateTime,
    icon: Int,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    isCompact: Boolean = false
) {
    if (isCompact) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                contentColor = accentColor,
                modifier = Modifier.size(32.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = formatDateTime(date),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                contentColor = accentColor,
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = formatDateTime(date),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun CreateRequestCard(
    onClick: () -> Unit,
    isCompact: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 16.dp else 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(if (isCompact) 36.dp else 48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(if (isCompact) 18.dp else 24.dp)
                        )
                    }
                }

                Text(
                    text = "Create New Request",
                    style = if (isCompact) {
                        MaterialTheme.typography.titleSmall
                    } else {
                        MaterialTheme.typography.titleMedium
                    }.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

@Composable
fun EmptyRequestsState(
    hasPendingRequests: Boolean,
    onCreateRequestClicked: () -> Unit,
    isTablet: Boolean = false
) {
    val iconSize = if (isTablet) 120.dp else 80.dp
    val maxWidth = if (isTablet) 600.dp else Int.MAX_VALUE.dp

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .padding(if (isTablet) 48.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated icon container
            Surface(
                modifier = Modifier.size(iconSize),
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
                        modifier = Modifier.size(iconSize * 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 24.dp))

            Text(
                text = if (hasPendingRequests) "Pending Request" else "No Volunteer Requests",
                style = if (isTablet) {
                    MaterialTheme.typography.headlineMedium
                } else {
                    MaterialTheme.typography.headlineSmall
                }.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (hasPendingRequests) {
                    "You have a pending volunteer request. Please wait for approval from the admin team."
                } else {
                    "Ready to make a difference? Submit your first volunteer request and join our mission to educate underprivileged children."
                },
                style = if (isTablet) {
                    MaterialTheme.typography.bodyLarge
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = if (isTablet) 24.sp else 20.sp
            )

            Spacer(modifier = Modifier.height(if (isTablet) 40.dp else 32.dp))

            AnimatedVisibility(
                visible = !hasPendingRequests,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FilledTonalButton(
                    onClick = onCreateRequestClicked,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(
                        horizontal = if (isTablet) 32.dp else 24.dp,
                        vertical = if (isTablet) 16.dp else 12.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Create New Request",
                        style = if (isTablet) {
                            MaterialTheme.typography.titleMedium
                        } else {
                            MaterialTheme.typography.titleSmall
                        }.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

// Helper data class for tuple
data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

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
                message = "Your profile doesn't meet the current requirements for this volunteer position."
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