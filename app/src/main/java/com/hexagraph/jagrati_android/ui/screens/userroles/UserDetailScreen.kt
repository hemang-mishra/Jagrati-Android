package com.hexagraph.jagrati_android.ui.screens.userroles

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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.role.RoleResponse
import com.hexagraph.jagrati_android.model.user.UserWithRolesResponse
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.theme.JagratiThemeColors
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserDetailScreen(
    userPid: String,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    viewModel: UserDetailViewModel = koinViewModel { parametersOf(userPid) }
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

    UserDetailScreenLayout(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onRefresh = { viewModel.loadUserDetails() },
        onAssignRoleClick = { viewModel.showRoleSelectionDialog() },
        onRemoveRoleClick = { roleId -> viewModel.removeRoleFromUser(roleId) },
        onRoleSelected = { roleId -> viewModel.assignRoleToUser(roleId) },
        onDismissDialog = { viewModel.hideRoleSelectionDialog() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreenLayout(
    uiState: UserDetailUiState,
    onBackPressed: () -> Unit,
    onRefresh: () -> Unit,
    onAssignRoleClick: () -> Unit,
    onRemoveRoleClick: (Long) -> Unit,
    onRoleSelected: (Long) -> Unit,
    onDismissDialog: () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.user?.let { "${it.firstName} ${it.lastName}" } ?: "User Details",
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
            if (uiState.availableRoles.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAssignRoleClick,
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Assign Role",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    text = {
                        Text(
                            "Assign Role",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            state = pullRefreshState,
            onRefresh = onRefresh,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pullToRefresh(
                        isRefreshing = uiState.isLoading,
                        state = pullRefreshState,
                        onRefresh = onRefresh,
                    )
            ) {
                when {
                    uiState.isLoading && uiState.user == null -> {
                        // Enhanced loading state
                        LoadingState(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.user == null -> {
                        // Enhanced error state
                        ErrorState(
                            onRetry = onRefresh,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        // Enhanced content layout
                        if (isTablet && isLandscape) {
                            TabletLandscapeContent(
                                user = uiState.user,
                                isLoading = uiState.roleAssignmentLoading,
                                onRemoveRoleClick = onRemoveRoleClick
                            )
                        } else {
                            PhoneContent(
                                user = uiState.user,
                                isLoading = uiState.roleAssignmentLoading,
                                onRemoveRoleClick = onRemoveRoleClick
                            )
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
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading user details...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "User not found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "There was an error loading the user details. Please try again.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Try Again", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun PhoneContent(
    user: UserWithRolesResponse,
    isLoading: Boolean,
    onRemoveRoleClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            EnhancedUserProfileCard(user = user)
        }

        item {
            RolesSectionHeader(roleCount = user.roles.size)
        }

        if (user.roles.isEmpty()) {
            item {
                EmptyRolesCard()
            }
        } else {
            itemsIndexed(user.roles) { index, role ->
                EnhancedRoleCard(
                    role = role,
                    colorIndex = index,
                    onRemoveClick = { onRemoveRoleClick(role.id) },
                    isLoading = isLoading
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) } // FAB padding
    }
}

@Composable
fun TabletLandscapeContent(
    user: UserWithRolesResponse,
    isLoading: Boolean,
    onRemoveRoleClick: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Left side - User profile
        Column(
            modifier = Modifier.weight(1f)
        ) {
            EnhancedUserProfileCard(user = user)
        }

        // Right side - Roles
        Column(
            modifier = Modifier.weight(1f)
        ) {
            RolesSectionHeader(roleCount = user.roles.size)
            Spacer(modifier = Modifier.height(16.dp))

            if (user.roles.isEmpty()) {
                EmptyRolesCard()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(user.roles) { index, role ->
                        EnhancedRoleCard(
                            role = role,
                            colorIndex = index,
                            onRemoveClick = { onRemoveRoleClick(role.id) },
                            isLoading = isLoading
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedUserProfileCard(user: UserWithRolesResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Enhanced user avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    JagratiThemeColors.orange,
                                    JagratiThemeColors.purple
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${user.firstName.first().uppercase()}${user.lastName.first().uppercase()}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // User name
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Email with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Role count badge
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = "${user.roles.size} ${if (user.roles.size == 1) "Role" else "Roles"}",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_shield),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
fun RolesSectionHeader(roleCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "User Roles",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (roleCount == 0) "No roles assigned" else "$roleCount ${if (roleCount == 1) "role" else "roles"} assigned",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EmptyRolesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_shield),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No roles assigned yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Use the assign role button to add roles to this user",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedRoleCard(
    role: RoleResponse,
    colorIndex: Int,
    onRemoveClick: () -> Unit,
    isLoading: Boolean
) {
    val batchColors = JagratiThemeColors.batchColors
    val roleColor = batchColors[colorIndex % batchColors.size]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(2.dp, roleColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Role icon with color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(roleColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shield),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = roleColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Role details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = role.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = roleColor
                )
                if (!role.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = role.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Remove button
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                onClick = onRemoveClick,
                enabled = !isLoading
            ) {
                Box(contentAlignment = Alignment.Center) {
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
    roles: List<RoleResponse>,
    onRoleSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    var selectedRoleId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Assign New Role",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            if (roles.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No available roles to assign",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column {
                    Text(
                        text = "Select a role to assign to this user:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(roles) { index, role ->
                            val batchColors = JagratiThemeColors.batchColors
                            val roleColor = batchColors[index % batchColors.size]

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (role.id == selectedRoleId)
                                        roleColor.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                border = if (role.id == selectedRoleId)
                                    BorderStroke(2.dp, roleColor)
                                else null,
                                onClick = { selectedRoleId = role.id }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = role.id == selectedRoleId,
                                        onClick = { selectedRoleId = role.id },
                                        enabled = !isLoading,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = roleColor,
                                            unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(roleColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_shield),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = roleColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = role.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (role.id == selectedRoleId) roleColor
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (!role.description.isNullOrBlank()) {
                                            Text(
                                                text = role.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedRoleId?.let { onRoleSelected(it) } },
                enabled = selectedRoleId != null && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assigning...", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Assign Role", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    "Cancel",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserDetailScreenPreview() {
    val user = UserWithRolesResponse(
        pid = "user1",
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        roles = listOf(
            RoleResponse(id = 1, name = "Admin", description = "Full access to all features", isActive = true),
            RoleResponse(id = 2, name = "Teacher", description = "Access to teaching materials", isActive = true),
            RoleResponse(id = 3, name = "Volunteer", description = "Community volunteer access", isActive = true)
        )
    )

    val availableRoles = listOf(
        RoleResponse(id = 4, name = "Student", description = "Limited access for students", isActive = true),
        RoleResponse(id = 5, name = "Parent", description = "Access to child information", isActive = true)
    )

    JagratiAndroidTheme {
        UserDetailScreenLayout(
            uiState = UserDetailUiState(
                userPid = "user1",
                user = user,
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

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserDetailScreenTabletPreview() {
    val user = UserWithRolesResponse(
        pid = "user1",
        firstName = "Priya",
        lastName = "Sharma",
        email = "priya.sharma@iiitdmj.ac.in",
        roles = listOf(
            RoleResponse(id = 1, name = "Coordinator", description = "Manages teaching programs and volunteers", isActive = true),
            RoleResponse(id = 2, name = "Mentor", description = "Guides and supports students", isActive = true)
        )
    )

    val availableRoles = listOf(
        RoleResponse(id = 3, name = "Subject Expert", description = "Specialized in particular subjects", isActive = true),
        RoleResponse(id = 4, name = "Event Organizer", description = "Organizes community events", isActive = true)
    )

    JagratiAndroidTheme {
        UserDetailScreenLayout(
            uiState = UserDetailUiState(
                userPid = "user1",
                user = user,
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
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmptyRolesPreview() {
    val user = UserWithRolesResponse(
        pid = "user2",
        firstName = "Arjun",
        lastName = "Patel",
        email = "arjun.patel@iiitdmj.ac.in",
        roles = emptyList()
    )

    JagratiAndroidTheme {
        UserDetailScreenLayout(
            uiState = UserDetailUiState(
                userPid = "user2",
                user = user,
                availableRoles = listOf(
                    RoleResponse(id = 1, name = "Volunteer", description = "Community service volunteer", isActive = true),
                    RoleResponse(id = 2, name = "Tutor", description = "Subject tutoring specialist", isActive = true)
                ),
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
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RoleSelectionDialogPreview() {
    val roles = listOf(
        RoleResponse(id = 1, name = "Teaching Assistant", description = "Helps with classroom activities and student support", isActive = true),
        RoleResponse(id = 2, name = "Program Coordinator", description = "Oversees program implementation and volunteer coordination", isActive = true),
        RoleResponse(id = 3, name = "Community Liaison", description = "Connects with local community and stakeholders", isActive = true),
        RoleResponse(id = 4, name = "Content Creator", description = "Develops educational materials and resources", isActive = true)
    )

    JagratiAndroidTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            EnhancedRoleSelectionDialog(
                roles = roles,
                onRoleSelected = {},
                onDismiss = {},
                isLoading = false
            )
        }
    }
}