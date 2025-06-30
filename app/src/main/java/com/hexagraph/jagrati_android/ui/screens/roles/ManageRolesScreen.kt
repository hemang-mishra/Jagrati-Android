package com.hexagraph.jagrati_android.ui.screens.roles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hexagraph.jagrati_android.model.role.RoleResponse
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManageRolesScreen(
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    viewModel: ManageRolesViewModel = koinViewModel()
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

    ManageRolesScreenLayout(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onRefresh = { viewModel.loadRoles() },
        onAddRole = { viewModel.showAddRoleBottomSheet() },
        onEditRole = { role -> viewModel.showEditRoleBottomSheet(role) },
        onDeleteRole = { role -> viewModel.deactivateRole(role) },
        onHideBottomSheet = { viewModel.hideBottomSheet() },
        onRoleNameChange = { viewModel.updateRoleName(it) },
        onRoleDescriptionChange = { viewModel.updateRoleDescription(it) },
        onSaveRole = { viewModel.saveRole() },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRolesScreenLayout(
    uiState: ManageRolesUiState,
    onBackPressed: () -> Unit,
    onRefresh: () -> Unit,
    onAddRole: () -> Unit,
    onEditRole: (RoleResponse) -> Unit,
    onDeleteRole: (RoleResponse) -> Unit,
    onHideBottomSheet: () -> Unit,
    onRoleNameChange: (String) -> Unit,
    onRoleDescriptionChange: (String) -> Unit,
    onSaveRole: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val pullToRefreshState = rememberPullToRefreshState()

    // Role deletion confirmation dialog
    var roleToDelete by remember { mutableStateOf<RoleResponse?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Roles") },
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
            FloatingActionButton(
                onClick = onAddRole,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Role")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullToRefresh(
                    isRefreshing = uiState.isLoading,
                    state = pullToRefreshState,
                    onRefresh = onRefresh)
        ) {
            if (uiState.roles.isEmpty() && !uiState.isLoading) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No roles found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onAddRole) {
                        Text("Add Role")
                    }
                }
            } else {
                // Role list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.roles) { role ->
                        RoleItem(
                            role = role,
                            onEditClick = { onEditRole(role) },
                            onDeleteClick = { roleToDelete = role }
                        )
                    }
                }
            }


            // Centered loading indicator
            if (uiState.isLoading && uiState.roles.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Bottom sheet for adding/editing roles
    if (uiState.isBottomSheetVisible) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = onHideBottomSheet,
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (uiState.isEditMode) "Edit Role" else "Add Role",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.roleName,
                    onValueChange = onRoleNameChange,
                    label = { Text("Role Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.roleName.isEmpty()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.roleDescription,
                    onValueChange = onRoleDescriptionChange,
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                onHideBottomSheet()
                            }
                        }
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                    Button(
                        onClick = onSaveRole,
                        enabled = uiState.roleName.isNotEmpty()
                    ) {
                        Text(if (uiState.isEditMode) "Update" else "Save")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Delete confirmation dialog
    roleToDelete?.let { role ->
        AlertDialog(
            onDismissRequest = { roleToDelete = null },
            title = { Text("Deactivate Role") },
            text = {
                Text("Are you sure you want to deactivate the role '${role.name}'? " +
                    "This will remove the role from the active roles list.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRole(role)
                        roleToDelete = null
                    }
                ) {
                    Text("Deactivate", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { roleToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RoleItem(
    role: RoleResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = role.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (!role.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = role.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageRolesScreenPreview() {
    val previewRoles = listOf(
        RoleResponse(id = 1, name = "Administrator", description = "Has full access to all features", isActive = true),
        RoleResponse(id = 2, name = "Teacher", description = "Can manage classes and students", isActive = true),
        RoleResponse(id = 3, name = "Volunteer", description = null, isActive = true)
    )

    MaterialTheme {
        ManageRolesScreenLayout(
            uiState = ManageRolesUiState(roles = previewRoles, isLoading = false),
            onBackPressed = {},
            onRefresh = {},
            onAddRole = {},
            onEditRole = {},
            onDeleteRole = {},
            onHideBottomSheet = {},
            onRoleNameChange = {},
            onRoleDescriptionChange = {},
            onSaveRole = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyManageRolesScreenPreview() {
    MaterialTheme {
        ManageRolesScreenLayout(
            uiState = ManageRolesUiState(roles = emptyList(), isLoading = false),
            onBackPressed = {},
            onRefresh = {},
            onAddRole = {},
            onEditRole = {},
            onDeleteRole = {},
            onHideBottomSheet = {},
            onRoleNameChange = {},
            onRoleDescriptionChange = {},
            onSaveRole = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoleItemPreview() {
    val role = RoleResponse(
        id = 1,
        name = "Administrator",
        description = "Has full access to all features",
        isActive = true
    )

    MaterialTheme {
        RoleItem(
            role = role,
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
