package com.hexagraph.jagrati_android.ui.screens.permissions

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.permission.PermissionResponse
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.theme.JagratiThemeColors

@Composable
fun PermissionDetailScreen(
    permissionId: Long,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    viewModel: PermissionDetailViewModel
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

    PermissionDetailScreenLayout(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onRefresh = { viewModel.loadPermissionDetails() },
        onAssignRoleClick = { viewModel.showRoleSelectionDialog() },
        onRemoveRoleClick = { roleId -> viewModel.removeRoleFromPermission(roleId) },
        onRoleSelected = { roleId -> viewModel.assignRoleToPermission(roleId) },
        onDismissDialog = { viewModel.hideRoleSelectionDialog() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDetailScreenLayout(
    uiState: PermissionDetailUiState,
    onBackPressed: () -> Unit,
    onRefresh: () -> Unit,
    onAssignRoleClick: () -> Unit,
    onRemoveRoleClick: (Long) -> Unit,
    onRoleSelected: (Long) -> Unit,
    onDismissDialog: () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.permission?.name ?: "Permission Details",
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
        },
        floatingActionButton = {
            if (uiState.availableRoles.isNotEmpty() && !uiState.isLoading) {
                ExtendedFloatingActionButton(
                    onClick = onAssignRoleClick,
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Assign"
                        )
                    },
                    text = {
                        Text(
                            "Assign Role",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .pullToRefresh(
                    state = pullRefreshState,
                    isRefreshing = uiState.isLoading,
                    onRefresh = onRefresh
                )
        ) {
            when {
                uiState.isLoading && uiState.permission == null -> {
                    // Initial loading with better UX
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading permission details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                uiState.permission == null -> {
                    // Enhanced error state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_security),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Permission Not Found",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "The permission details could not be loaded. Please try again.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                else -> {
                    // Main content with responsive layout
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 800.dp)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        // Permission details card
                        item {
                            EnhancedPermissionDetailsCard(permission = uiState.permission!!)
                        }

                        // Roles section
                        item {
                            RolesSectionHeader(
                                assignedCount = uiState.assignedRoles.size,
                                availableCount = uiState.availableRoles.size
                            )
                        }

                        // Role items or empty state
                        if (uiState.assignedRoles.isEmpty()) {
                            item {
                                EmptyRolesState(
                                    hasAvailableRoles = uiState.availableRoles.isNotEmpty(),
                                    onAssignRoleClick = onAssignRoleClick
                                )
                            }
                        } else {
                            items(
                                items = uiState.assignedRoles,
                                key = { it.id }
                            ) { role ->
                                EnhancedRoleItem(
                                    role = role,
                                    onRemoveClick = { onRemoveRoleClick(role.id) },
                                    isLoading = uiState.roleAssignmentLoading
                                )
                            }
                        }

                        // Bottom padding for FAB
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // Enhanced role selection dialog
        if (uiState.showRoleSelectionDialog) {
            EnhancedRoleSelectionDialog(
                roles = uiState.availableRoles,
                onRoleSelected = onRoleSelected,
                onDismiss = onDismissDialog,
                isLoading = uiState.roleAssignmentLoading
            )
        }
    }
}

@Composable
fun EnhancedPermissionDetailsCard(permission: PermissionResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_security),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(6.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = permission.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Permission Details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Details in a more structured layout
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PermissionDetailRow(
                    icon = R.drawable.ic_category,
                    label = "Module",
                    value = permission.module
                )

                PermissionDetailRow(
                    icon = R.drawable.baseline_touch_app_24,
                    label = "Action",
                    value = permission.action
                )

                if (!permission.description.isNullOrBlank()) {
                    PermissionDetailRow(
                        icon = R.drawable.ic_description,
                        label = "Description",
                        value = permission.description,
                        isMultiline = true
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionDetailRow(
    icon: Int,
    label: String,
    value: String,
    isMultiline: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = if (isMultiline) Alignment.Top else Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RolesSectionHeader(assignedCount: Int, availableCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Assigned Roles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$assignedCount assigned • $availableCount available",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_assignment_ind),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun EmptyRolesState(
    hasAvailableRoles: Boolean,
    onAssignRoleClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_assignment_ind),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "No Roles Assigned",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (hasAvailableRoles) {
                    "This permission hasn't been assigned to any roles yet. Assign roles to grant users access."
                } else {
                    "No roles are available to assign to this permission."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            if (hasAvailableRoles) {
                OutlinedButton(
                    onClick = onAssignRoleClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assign First Role", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun EnhancedRoleItem(
    role: RoleSummaryResponse,
    onRemoveClick: () -> Unit,
    isLoading: Boolean
) {
    val batchColors = JagratiThemeColors.batchColors
    val roleColor = batchColors[role.id.toInt() % batchColors.size]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, roleColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Role color indicator
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = roleColor.copy(alpha = 0.1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = role.name.take(2).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = roleColor
                    )
                }
            }

            // Role details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = role.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!role.description.isNullOrBlank()) {
                    Text(
                        text = role.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Active status indicator
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (role.isActive) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = if (role.isActive) "Active" else "Inactive",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (role.isActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Remove button
            Surface(
                onClick = onRemoveClick,
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            ) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove role",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedRoleSelectionDialog(
    roles: List<RoleSummaryResponse>,
    onRoleSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    var selectedRoleId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_assignment_ind),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Select Role to Assign",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (roles.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_assignment_ind),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        "No Available Roles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "All roles have already been assigned to this permission.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Choose a role to assign to this permission:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    roles.forEach { role ->
                        val batchColors = JagratiThemeColors.batchColors
                        val roleColor = batchColors[role.id.toInt() % batchColors.size]

                        Surface(
                            onClick = { if (!isLoading) selectedRoleId = role.id },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(8.dp),
                            color = if (role.id == selectedRoleId) {
                                roleColor.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                if (role.id == selectedRoleId) roleColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = role.id == selectedRoleId,
                                    onClick = { if (!isLoading) selectedRoleId = role.id },
                                    enabled = !isLoading,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = roleColor
                                    )
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = role.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (!role.description.isNullOrBlank()) {
                                        Text(
                                            text = role.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        },
        confirmButton = {
            if (roles.isNotEmpty()) {
                Button(
                    onClick = {
                        selectedRoleId?.let { onRoleSelected(it) }
                    },
                    enabled = selectedRoleId != null && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Assigning...", fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Assign Role", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PermissionDetailScreenPreview() {
    val permission = PermissionResponse(
        id = 1,
        name = "USER_VIEW",
        description = "Permission to view user details and access user management screens. This includes viewing user profiles, contact information, and basic user statistics.",
        module = "User Management",
        action = "View Access"
    )

    val assignedRoles = listOf(
        RoleSummaryResponse(id = 1, name = "System Administrator", description = "Full system access with all permissions", isActive = true),
        RoleSummaryResponse(id = 2, name = "Teaching Staff", description = "Access to teaching materials and student data", isActive = true),
        RoleSummaryResponse(id = 3, name = "Coordinator", description = "Coordination and management access", isActive = false)
    )

    val availableRoles = listOf(
        RoleSummaryResponse(id = 4, name = "Student Volunteer", description = "Limited access for student helpers", isActive = true),
        RoleSummaryResponse(id = 5, name = "Parent Guardian", description = "Access to child information and progress", isActive = true)
    )

    JagratiAndroidTheme {
        PermissionDetailScreenLayout(
            uiState = PermissionDetailUiState(
                permissionId = 1,
                permission = permission,
                assignedRoles = assignedRoles,
                availableRoles = availableRoles,
                isLoading = false,
                showRoleSelectionDialog = false
            ),
            onBackPressed = {},
            onRefresh = {},
            onAssignRoleClick = {},
            onRemoveRoleClick = {},
            onRoleSelected = {},
            onDismissDialog = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PermissionDetailScreenDarkPreview() {
    val permission = PermissionResponse(
        id = 1,
        name = "STUDENT_MANAGE",
        description = "Comprehensive permission for student management including enrollment, profile updates, and academic tracking.",
        module = "Student Management",
        action = "Full Management"
    )

    val assignedRoles = listOf(
        RoleSummaryResponse(id = 1, name = "Program Director", description = "Overall program management and oversight", isActive = true),
        RoleSummaryResponse(id = 2, name = "Academic Coordinator", description = "Academic planning and student progress tracking", isActive = true)
    )

    JagratiAndroidTheme {
        PermissionDetailScreenLayout(
            uiState = PermissionDetailUiState(
                permissionId = 1,
                permission = permission,
                assignedRoles = assignedRoles,
                availableRoles = emptyList(),
                isLoading = false,
                showRoleSelectionDialog = false
            ),
            onBackPressed = {},
            onRefresh = {},
            onAssignRoleClick = {},
            onRemoveRoleClick = {},
            onRoleSelected = {},
            onDismissDialog = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun PermissionDetailScreenTabletPreview() {
    val permission = PermissionResponse(
        id = 1,
        name = "BATCH_COORDINATOR",
        description = "Permission for coordinating batches, managing schedules, and overseeing volunteer assignments.",
        module = "Batch Management",
        action = "Coordination"
    )

    val assignedRoles = listOf(
        RoleSummaryResponse(id = 1, name = "Senior Volunteer", description = "Experienced volunteers with leadership responsibilities", isActive = true),
        RoleSummaryResponse(id = 2, name = "Batch Leader", description = "Leaders assigned to specific batches", isActive = true),
        RoleSummaryResponse(id = 3, name = "Training Coordinator", description = "Manages training programs and schedules", isActive = false),
        RoleSummaryResponse(id = 4, name = "Resource Manager", description = "Manages educational resources and materials", isActive = true)
    )

    val availableRoles = listOf(
        RoleSummaryResponse(id = 5, name = "Assistant Coordinator", description = "Assists in coordination activities", isActive = true),
        RoleSummaryResponse(id = 6, name = "Field Supervisor", description = "Supervises field activities and operations", isActive = true)
    )

    JagratiAndroidTheme {
        PermissionDetailScreenLayout(
            uiState = PermissionDetailUiState(
                permissionId = 1,
                permission = permission,
                assignedRoles = assignedRoles,
                availableRoles = availableRoles,
                isLoading = false,
                showRoleSelectionDialog = false
            ),
            onBackPressed = {},
            onRefresh = {},
            onAssignRoleClick = {},
            onRemoveRoleClick = {},
            onRoleSelected = {},
            onDismissDialog = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionDetailScreenEmptyPreview() {
    val permission = PermissionResponse(
        id = 1,
        name = "CONTENT_CREATE",
        description = "Permission to create and upload educational content, including lesson plans and materials.",
        module = "Content Management",
        action = "Create"
    )

    val availableRoles = listOf(
        RoleSummaryResponse(id = 1, name = "Content Creator", description = "Creates educational content and materials", isActive = true),
        RoleSummaryResponse(id = 2, name = "Subject Expert", description = "Subject matter expert for content review", isActive = true)
    )

    JagratiAndroidTheme {
        PermissionDetailScreenLayout(
            uiState = PermissionDetailUiState(
                permissionId = 1,
                permission = permission,
                assignedRoles = emptyList(),
                availableRoles = availableRoles,
                isLoading = false,
                showRoleSelectionDialog = false
            ),
            onBackPressed = {},
            onRefresh = {},
            onAssignRoleClick = {},
            onRemoveRoleClick = {},
            onRoleSelected = {},
            onDismissDialog = {}
        )
    }
}