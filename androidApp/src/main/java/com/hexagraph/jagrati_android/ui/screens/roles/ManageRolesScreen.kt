package com.hexagraph.jagrati_android.ui.screens.roles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hexagraph.jagrati_android.model.role.RoleResponse
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.theme.JagratiThemeColors
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.hexagraph.jagrati_android.R

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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.screenWidthDp >= 600

    var roleToDelete by remember { mutableStateOf<RoleResponse?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Manage Roles",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${uiState.roles.size} active roles",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = onBackPressed,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddRole,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    Icon(Icons.Filled.Add, contentDescription = null)
                },
                text = { Text("Add Role") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullToRefresh(
                    isRefreshing = uiState.isLoading,
                    state = pullToRefreshState,
                    onRefresh = onRefresh
                )
        ) {
            when {
                uiState.roles.isEmpty() && !uiState.isLoading -> {
                    EmptyStateContent(onAddRole = onAddRole)
                }
                else -> {
                    RolesContent(
                        roles = uiState.roles,
                        isTablet = isTablet,
                        isLandscape = isLandscape,
                        onEditRole = onEditRole,
                        onDeleteRole = { roleToDelete = it }
                    )
                }
            }

            if (uiState.isLoading && uiState.roles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Text(
                            "Loading roles...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }

    // Enhanced Bottom Sheet
    if (uiState.isBottomSheetVisible) {
        RoleFormBottomSheet(
            isEditMode = uiState.isEditMode,
            roleName = uiState.roleName,
            roleDescription = uiState.roleDescription,
            onRoleNameChange = onRoleNameChange,
            onRoleDescriptionChange = onRoleDescriptionChange,
            onSaveRole = onSaveRole,
            onDismiss = onHideBottomSheet
        )
    }

    // Enhanced Delete Dialog
    roleToDelete?.let { role ->
        RoleDeleteDialog(
            role = role,
            onConfirm = {
                onDeleteRole(role)
                roleToDelete = null
            },
            onDismiss = { roleToDelete = null }
        )
    }
}

@Composable
private fun EmptyStateContent(onAddRole: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.size(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter=painterResource(R.drawable.ic_groups),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "No Roles Yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Create your first role to start organizing\nyour team and managing permissions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddRole,
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Role")
        }
    }
}

@Composable
private fun RolesContent(
    roles: List<RoleResponse>,
    isTablet: Boolean,
    isLandscape: Boolean,
    onEditRole: (RoleResponse) -> Unit,
    onDeleteRole: (RoleResponse) -> Unit
) {
    val columns = when {
        isTablet && isLandscape -> 3
        isTablet -> 2
        isLandscape -> 2
        else -> 1
    }

    if (columns == 1) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(roles) { role ->
                EnhancedRoleCard(
                    role = role,
                    roleIndex = roles.indexOf(role),
                    onEditClick = { onEditRole(role) },
                    onDeleteClick = { onDeleteRole(role) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // FAB space
            }
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            items(roles) { role ->
                EnhancedRoleCard(
                    role = role,
                    roleIndex = roles.indexOf(role),
                    onEditClick = { onEditRole(role) },
                    onDeleteClick = { onDeleteRole(role) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // FAB space
            }
        }
    }
}

@Composable
fun EnhancedRoleCard(
    role: RoleResponse,
    roleIndex: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val batchColors = JagratiThemeColors.batchColors
    val roleColor = batchColors[roleIndex % batchColors.size]

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            hoveredElevation = 3.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon and text content
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with colored background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(roleColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getRoleIcon(role.name)),
                        contentDescription = null,
                        tint = roleColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Text content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = role.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!role.description.isNullOrEmpty()) {
                        Text(
                            text = role.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "No description",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            // Right side: Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleFormBottomSheet(
    isEditMode: Boolean,
    roleName: String,
    roleDescription: String,
    onRoleNameChange: (String) -> Unit,
    onRoleDescriptionChange: (String) -> Unit,
    onSaveRole: () -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        dragHandle = {
            Column {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ) {}
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isEditMode) "Edit Role" else "Create New Role",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isEditMode) "Update role information" else "Add a new role to your team",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            onDismiss()
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = roleName,
                onValueChange = onRoleNameChange,
                label = { Text("Role Name") },
                placeholder = { Text("e.g. Teacher, Administrator, Volunteer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = roleName.isEmpty(),
                supportingText = if (roleName.isEmpty()) {
                    { Text("Role name is required", color = MaterialTheme.colorScheme.error) }
                } else null,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield),
                        contentDescription = null,
                        tint = if (roleName.isEmpty()) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = roleDescription,
                onValueChange = onRoleDescriptionChange,
                label = { Text("Description (Optional)") },
                placeholder = { Text("Describe the role's responsibilities") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_description),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onSaveRole,
                    enabled = roleName.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(if(isEditMode) R.drawable.ic_save else R.drawable.ic_add),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditMode) "Update Role" else "Create Role")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoleDeleteDialog(
    role: RoleResponse,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
        },
        title = {
            Text(
                "Deactivate Role?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Are you sure you want to deactivate the role:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "\"${role.name}\"",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This will remove the role from active roles and may affect users assigned to it.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Deactivate")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    )
}

private fun getRoleIcon(roleName: String): Int {
    return when (roleName.lowercase()) {
        else -> R.drawable.ic_groups
    }
}

// Preview functions with proper annotations
@Preview(showBackground = true)
@Composable
fun ManageRolesScreenPreview() {
    val previewRoles = listOf(
        RoleResponse(id = 1, name = "Administrator", description = "Has full access to all features and can manage system settings", isActive = true),
        RoleResponse(id = 2, name = "Teacher", description = "Can manage classes, students, and educational content", isActive = true),
        RoleResponse(id = 3, name = "Volunteer", description = "Supports teaching activities and community engagement", isActive = true),
        RoleResponse(id = 4, name = "Coordinator", description = "Organizes events and coordinates between different teams", isActive = true)
    )

    JagratiAndroidTheme {
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

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ManageRolesScreenDarkPreview() {
    val previewRoles = listOf(
        RoleResponse(id = 1, name = "Administrator", description = "Has full access to all features", isActive = true),
        RoleResponse(id = 2, name = "Teacher", description = "Can manage classes and students", isActive = true)
    )

    JagratiAndroidTheme {
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
    JagratiAndroidTheme {
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

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun TabletLandscapePreview() {
    val previewRoles = listOf(
        RoleResponse(id = 1, name = "Administrator", description = "Complete system access with all administrative privileges", isActive = true),
        RoleResponse(id = 2, name = "Teacher", description = "Educational content management and student interaction", isActive = true),
        RoleResponse(id = 3, name = "Volunteer", description = "Community support and engagement activities", isActive = true),
        RoleResponse(id = 4, name = "Coordinator", description = "Event organization and team coordination", isActive = true),
        RoleResponse(id = 5, name = "Student", description = "Learning access and participation in activities", isActive = true),
        RoleResponse(id = 6, name = "Manager", description = "Departmental oversight and resource management", isActive = true)
    )

    JagratiAndroidTheme {
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
fun RoleItemPreview() {
    val role = RoleResponse(
        id = 1,
        name = "Administrator",
        description = "Has full access to all features and system settings with complete administrative control",
        isActive = true
    )

    JagratiAndroidTheme {
        EnhancedRoleCard(
            role = role,
            roleIndex = 0,
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RoleItemDarkPreview() {
    val role = RoleResponse(
        id = 2,
        name = "Teacher",
        description = "Can manage classes, students, and educational content with teaching permissions",
        isActive = true
    )

    JagratiAndroidTheme {
        EnhancedRoleCard(
            role = role,
            roleIndex = 1,
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}