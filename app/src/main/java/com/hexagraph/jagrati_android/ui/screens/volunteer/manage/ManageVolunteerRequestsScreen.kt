package com.hexagraph.jagrati_android.ui.screens.volunteer.manage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.volunteer.AddressDTO
import com.hexagraph.jagrati_android.model.volunteer.DetailedVolunteerRequestResponse
import com.hexagraph.jagrati_android.model.volunteer.UserSummaryDTO
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ManageVolunteerRequestsScreen(
    viewModel: ManageVolunteerRequestsViewModel,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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

    ManageVolunteerRequestsLayout(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onRefresh = viewModel::loadVolunteerRequests,
        onApproveClick = viewModel::approveRequest,
        onRejectClick = viewModel::showRejectDialog,
        onShowDetailClick = viewModel::showDetailDialog,
        onDismissDetailDialog = viewModel::hideDetailDialog,
        onDismissRejectDialog = viewModel::hideRejectDialog,
        onRejectReasonChange = viewModel::updateRejectionReason,
        onConfirmReject = viewModel::rejectRequest
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageVolunteerRequestsLayout(
    uiState: ManageVolunteerRequestsUiState,
    onBackPressed: () -> Unit,
    onRefresh: () -> Unit,
    onApproveClick: (Long) -> Unit,
    onRejectClick: (Long) -> Unit,
    onShowDetailClick: (Long) -> Unit,
    onDismissDetailDialog: () -> Unit,
    onDismissRejectDialog: () -> Unit,
    onRejectReasonChange: (String) -> Unit,
    onConfirmReject: () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Volunteer Requests",
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
                ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
                state = pullRefreshState
            ) {
                if (uiState.volunteerRequests.isEmpty() && !uiState.isLoading) {
                    // Empty state
                    EmptyRequestsState()
                } else {
                    // List of requests
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        // Stats section
                        item {
                            RequestStatsCard(
                                totalRequests = uiState.volunteerRequests.size,
                                pendingRequests = uiState.volunteerRequests.count { it.status == "PENDING" },
                                approvedRequests = uiState.volunteerRequests.count { it.status == "APPROVED" },
                                rejectedRequests = uiState.volunteerRequests.count { it.status == "REJECTED" }
                            )
                        }

                        // Section header for pending requests
                        val pendingRequests = uiState.volunteerRequests.filter { it.status == "PENDING" }
                        if (pendingRequests.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Pending Requests",
                                    count = pendingRequests.size,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }

                            items(
                                items = pendingRequests,
                                key = { it.id }
                            ) { request ->
                                VolunteerRequestCard(
                                    request = request,
                                    onApproveClick = onApproveClick,
                                    onRejectClick = onRejectClick,
                                    onDetailClick = onShowDetailClick,
                                    isProcessing = uiState.processingRequestId == request.id
                                )
                            }
                        }

                        // Section header for approved requests
                        val approvedRequests = uiState.volunteerRequests.filter { it.status == "APPROVED" }
                        if (approvedRequests.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Approved Requests",
                                    count = approvedRequests.size,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            items(
                                items = approvedRequests,
                                key = { it.id }
                            ) { request ->
                                VolunteerRequestCard(
                                    request = request,
                                    onApproveClick = onApproveClick,
                                    onRejectClick = onRejectClick,
                                    onDetailClick = onShowDetailClick,
                                    isProcessing = uiState.processingRequestId == request.id
                                )
                            }
                        }

                        // Section header for rejected requests
                        val rejectedRequests = uiState.volunteerRequests.filter { it.status == "REJECTED" }
                        if (rejectedRequests.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Rejected Requests",
                                    count = rejectedRequests.size,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            items(
                                items = rejectedRequests,
                                key = { it.id }
                            ) { request ->
                                VolunteerRequestCard(
                                    request = request,
                                    onApproveClick = onApproveClick,
                                    onRejectClick = onRejectClick,
                                    onDetailClick = onShowDetailClick,
                                    isProcessing = uiState.processingRequestId == request.id
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    // Show rejection dialog if needed
    if (uiState.showRejectionDialog) {
        RejectionReasonDialog(
            onDismiss = onDismissRejectDialog,
            onConfirm = onConfirmReject,
            reason = uiState.rejectionReason,
            onReasonChange = onRejectReasonChange
        )
    }

    // Show detail dialog if needed
    if (uiState.showDetailDialog && uiState.selectedRequest != null) {
        VolunteerDetailDialog(
            request = uiState.selectedRequest,
            onDismiss = onDismissDetailDialog
        )
    }
}

@Composable
fun EmptyRequestsState() {
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
                    painter = painterResource(R.drawable.ic_person_add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Volunteer Requests",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "There are no volunteer requests to manage at the moment. New requests will appear here when submitted.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun RequestStatsCard(
    totalRequests: Int,
    pendingRequests: Int,
    approvedRequests: Int,
    rejectedRequests: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Request Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total",
                    value = totalRequests.toString(),
                    color = MaterialTheme.colorScheme.primary
                )

                StatItem(
                    label = "Pending",
                    value = pendingRequests.toString(),
                    color = MaterialTheme.colorScheme.tertiary
                )

                StatItem(
                    label = "Approved",
                    value = approvedRequests.toString(),
                    color = MaterialTheme.colorScheme.primary
                )

                StatItem(
                    label = "Rejected",
                    value = rejectedRequests.toString(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    count: Int,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp),
            color = color
        ) {}

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun VolunteerRequestCard(
    request: DetailedVolunteerRequestResponse,
    onApproveClick: (Long) -> Unit,
    onRejectClick: (Long) -> Unit,
    onDetailClick: (Long) -> Unit,
    isProcessing: Boolean
) {
    val (statusColor, statusText) = when (request.status) {
        "PENDING" -> Pair(MaterialTheme.colorScheme.tertiary, "Pending")
        "APPROVED" -> Pair(MaterialTheme.colorScheme.primary, "Approved")
        "REJECTED" -> Pair(MaterialTheme.colorScheme.error, "Rejected")
        else -> Pair(MaterialTheme.colorScheme.outline, request.status)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
        onClick = { onDetailClick(request.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile picture or placeholder
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = request.firstName.first().toString() + request.lastName.first().toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${request.firstName} ${request.lastName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = request.college ?: "No college specified",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = formatDate(request.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Request details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoItem(
                    icon = R.drawable.ic_assignment_ind,
                    label = "Branch",
                    value = request.branch ?: "N/A"
                )

                InfoItem(
                    icon = R.drawable.baseline_touch_app_24,
                    label = "Year",
                    value = request.yearOfStudy?.toString() ?: "N/A"
                )

                InfoItem(
                    icon = R.drawable.ic_category,
                    label = "Programme",
                    value = request.programme ?: "N/A"
                )
            }

            // Action buttons for pending requests
            if (request.status == "PENDING") {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reject button
                    OutlinedButton(
                        onClick = { onRejectClick(request.id) },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reject",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontWeight = FontWeight.SemiBold)
                    }

                    // Approve button
                    Button(
                        onClick = { onApproveClick(request.id) },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Approve",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: Int,
    label: String,
    value: String
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RejectionReasonDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    reason: String,
    onReasonChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "Reject Volunteer Request",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    "Please provide a reason for rejecting this volunteer request. This will be visible to the applicant.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rejection Reason") },
                    placeholder = { Text("e.g., Incomplete information, Ineligible") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Confirm Rejection")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun VolunteerDetailDialog(
    request: DetailedVolunteerRequestResponse,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Volunteer Request Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                // Request ID and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Request #${request.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    val (statusColor, statusText) = when (request.status) {
                        "PENDING" -> Pair(MaterialTheme.colorScheme.tertiary, "Pending")
                        "APPROVED" -> Pair(MaterialTheme.colorScheme.primary, "Approved")
                        "REJECTED" -> Pair(MaterialTheme.colorScheme.error, "Rejected")
                        else -> Pair(MaterialTheme.colorScheme.outline, request.status)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Personal Information
                DetailSection(title = "Personal Information") {
                    DetailRow(label = "Full Name", value = "${request.firstName} ${request.lastName}")
                    DetailRow(label = "Gender", value = request.gender)
                    DetailRow(label = "Date of Birth", value = request.dateOfBirth.toString())
                    DetailRow(label = "Contact Number", value = request.contactNumber ?: "Not provided")
                    DetailRow(label = "Email", value = request.alternateEmail ?: "Not provided")
                }

                // Academic Information
                DetailSection(title = "Academic Information") {
                    DetailRow(label = "College", value = request.college ?: "Not provided")
                    DetailRow(label = "Programme", value = request.programme ?: "Not provided")
                    DetailRow(label = "Branch", value = request.branch ?: "Not provided")
                    DetailRow(label = "Year of Study", value = request.yearOfStudy?.toString() ?: "Not provided")
                    DetailRow(label = "Batch", value = request.batch ?: "Not provided")
                    DetailRow(label = "Roll Number", value = request.rollNumber ?: "Not provided")
                }

                // Address Information
                if (request.address != null) {
                    DetailSection(title = "Address") {
                        DetailRow(label = "Street", value = request.address.streetAddress1 ?: "Not provided")
                        if (!request.address.streetAddress2.isNullOrBlank()) {
                            DetailRow(label = "Street 2", value = request.address.streetAddress2)
                        }
                        DetailRow(label = "City", value = request.address.city ?: "Not provided")
                        DetailRow(label = "State", value = request.address.state ?: "Not provided")
                        DetailRow(label = "Pincode", value = request.address.pincode ?: "Not provided")
                    }
                }

                // Request Information
                DetailSection(title = "Request Information") {
                    DetailRow(label = "Submitted By", value = request.requestedByUser.firstName + " " + request.requestedByUser.lastName)
                    DetailRow(label = "Submitted On", value = formatDateTime(request.createdAt))

                    if (request.reviewedAt != null && request.reviewedByUser != null) {
                        DetailRow(label = "Reviewed By", value = request.reviewedByUser.firstName + " " + request.reviewedByUser.lastName)
                        DetailRow(label = "Reviewed On", value = formatDateTime(request.reviewedAt))
                    }
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Close", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(0.4f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

// Helper functions for date formatting
private fun formatDate(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return date.format(formatter)
}

private fun formatDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
    return dateTime.format(formatter)
}

@Preview(showBackground = true)
@Composable
fun ManageVolunteerRequestsPreview() {
    val mockRequests = listOf(
        createMockRequest(
            id = 1001,
            firstName = "Rahul",
            lastName = "Sharma",
            status = "PENDING",
            branch = "CSE",
            programme = "B.Tech",
            yearOfStudy = 3
        ),
        createMockRequest(
            id = 1002,
            firstName = "Priya",
            lastName = "Singh",
            status = "APPROVED",
            branch = "ECE",
            programme = "B.Tech",
            yearOfStudy = 2
        ),
        createMockRequest(
            id = 1003,
            firstName = "Amit",
            lastName = "Verma",
            status = "REJECTED",
            branch = "ME",
            programme = "B.Tech",
            yearOfStudy = 4
        )
    )

    JagratiAndroidTheme {
        ManageVolunteerRequestsLayout(
            uiState = ManageVolunteerRequestsUiState(
                volunteerRequests = mockRequests,
                isLoading = false
            ),
            onBackPressed = {},
            onRefresh = {},
            onApproveClick = {},
            onRejectClick = {},
            onShowDetailClick = {},
            onDismissDetailDialog = {},
            onDismissRejectDialog = {},
            onRejectReasonChange = {},
            onConfirmReject = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatePreview() {
    JagratiAndroidTheme {
        ManageVolunteerRequestsLayout(
            uiState = ManageVolunteerRequestsUiState(
                volunteerRequests = emptyList(),
                isLoading = false
            ),
            onBackPressed = {},
            onRefresh = {},
            onApproveClick = {},
            onRejectClick = {},
            onShowDetailClick = {},
            onDismissDetailDialog = {},
            onDismissRejectDialog = {},
            onRejectReasonChange = {},
            onConfirmReject = {}
        )
    }
}

// Helper function to create mock data for previews
private fun createMockRequest(
    id: Long,
    firstName: String,
    lastName: String,
    status: String,
    branch: String,
    programme: String,
    yearOfStudy: Int
): DetailedVolunteerRequestResponse {
    return DetailedVolunteerRequestResponse(
        id = id,
        firstName = firstName,
        lastName = lastName,
        gender = "MALE",
        rollNumber = "2023${branch}00$id",
        alternateEmail = "$firstName.${lastName.lowercase()}@example.com",
        batch = "2023",
        programme = programme,
        dateOfBirth = LocalDate.of(2000, 1, 1),
        contactNumber = "9876543210",
        college = "IIITDM Jabalpur",
        branch = branch,
        yearOfStudy = yearOfStudy,
        address = AddressDTO(
            streetAddress1 = "123 Main St",
            streetAddress2 = "Apartment 4B",
            pincode = "482001",
            city = "Jabalpur",
            state = "Madhya Pradesh"
        ),
        status = status,
        createdAt = LocalDateTime.now().minusDays(5),
        requestedByUser = UserSummaryDTO(
            pid = "user123",
            firstName = firstName,
            lastName = lastName,
            email = "$firstName.${lastName.lowercase()}@example.com"
        ),
        reviewedByUser = if (status != "PENDING") UserSummaryDTO(
            pid = "admin123",
            firstName = "Admin",
            lastName = "User",
            email = "admin@example.com"
        ) else null,
        reviewedAt = if (status != "PENDING") LocalDateTime.now().minusDays(1) else null
    )
}
