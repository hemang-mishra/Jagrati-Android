package com.hexagraph.jagrati_android.ui.screens.volunteer.manage

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
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
        Icon(
            painter = painterResource(R.drawable.ic_person_add),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Volunteer Requests",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "New volunteer requests will appear here when submitted.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                    color = MaterialTheme.colorScheme.secondary
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.15f)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
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
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.firstName.first().toString() + request.lastName.first().toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = request.college ?: "No college specified",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = formatDate(request.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
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
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reject",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontWeight = FontWeight.Medium)
                    }

                    // Approve button
                    Button(
                        onClick = { onApproveClick(request.id) },
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Approve",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve", fontWeight = FontWeight.Medium)
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
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
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Reject Request",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Please provide a reason for rejecting this volunteer request.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rejection Reason") },
                    placeholder = { Text("e.g., Incomplete information") },
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
                Text("Reject", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Medium)
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
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                        text = "Request Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val (statusColor, statusText) = when (request.status) {
                        "PENDING" -> Pair(MaterialTheme.colorScheme.tertiary, "Pending")
                        "APPROVED" -> Pair(MaterialTheme.colorScheme.secondary, "Approved")
                        "REJECTED" -> Pair(MaterialTheme.colorScheme.error, "Rejected")
                        else -> Pair(MaterialTheme.colorScheme.outline, request.status)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
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
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", fontWeight = FontWeight.Medium)
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
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
