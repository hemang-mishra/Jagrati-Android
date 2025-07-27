package com.hexagraph.jagrati_android.ui.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.hexagraph.jagrati_android.model.ManagementScreen
import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.ui.navigation.Screens
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagementScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateToScreen: (Screens) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: ManagementViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(message = error.actualResponse ?: "An error occurred")
            viewModel.clearErrorFlow()
        }
    }

    LaunchedEffect(key1 = uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message = message)
            viewModel.clearMsgFlow()
        }
    }

    ManagementScreenLayout(
        uiState = uiState,
        onRefresh = { viewModel.loadManagementScreens() },
        onNavigateToScreen = onNavigateToScreen,
        onBackPressed = onBackPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreenLayout(
    uiState: ManagementUiState,
    onRefresh: () -> Unit,
    onNavigateToScreen: (Screens) -> Unit,
    onBackPressed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Management",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.sections.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    ManagementContent(
                        sections = uiState.sections,
                        onNavigateToScreen = onNavigateToScreen,
                        isTablet = isTablet,
                        isLandscape = isLandscape
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading management options...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Management Access",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No management sections are available for your current permissions. Contact your administrator for access.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun ManagementContent(
    sections: Map<String, List<ManagementScreenPermissionState>>,
    onNavigateToScreen: (Screens) -> Unit,
    isTablet: Boolean,
    isLandscape: Boolean
) {
    val horizontalPadding = when {
        isTablet && isLandscape -> 48.dp
        isTablet -> 32.dp
        else -> 16.dp
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = horizontalPadding,
            vertical = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        sections.forEach { (sectionName, screens) ->
            val availableScreens = screens.filter { it.hasAllPermissions }
            if (availableScreens.isNotEmpty()) {
                item(key = sectionName) {
                    ManagementSection(
                        sectionName = sectionName,
                        screens = availableScreens,
                        onNavigateToScreen = onNavigateToScreen,
                        isTablet = isTablet,
                        isLandscape = isLandscape
                    )
                }
            }
        }
    }
}

@Composable
private fun ManagementSection(
    sectionName: String,
    screens: List<ManagementScreenPermissionState>,
    onNavigateToScreen: (Screens) -> Unit,
    isTablet: Boolean,
    isLandscape: Boolean
) {
    Column {
        SectionHeader(sectionName = sectionName)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isTablet && screens.size > 2 -> {
                // Grid layout for tablets with multiple items
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isLandscape) 3 else 2),
                    modifier = Modifier.height((screens.size / (if (isLandscape) 3 else 2) + 1) * 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(screens) { screenState ->
                        ManagementScreenItem(
                            screenName = screenState.screen.screenName,
                            screenIndex = screens.indexOf(screenState),
                            onClick = {
                                onNavigateToScreen(screenState.screen.screen)
                            },
                            isCompact = true
                        )
                    }
                }
            }
            else -> {
                // Column layout for phones or tablets with few items
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    screens.forEach { screenState ->
                        ManagementScreenItem(
                            screenName = screenState.screen.screenName,
                            screenIndex = screens.indexOf(screenState),
                            onClick = {
                                onNavigateToScreen(screenState.screen.screen)
                            },
                            isCompact = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(sectionName: String) {
    Column {
        Text(
            text = sectionName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.4f),
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ManagementScreenItem(
    screenName: String,
    screenIndex: Int,
    onClick: () -> Unit,
    isCompact: Boolean = false
) {
    // Use batch colors based on Jagrati design guidelines
    val accentColor = when (screenIndex % 4) {
        0 -> MaterialTheme.colorScheme.primary
        1 -> MaterialTheme.colorScheme.secondary
        2 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isCompact) 12.dp else 20.dp,
                    vertical = if (isCompact) 12.dp else 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Accent indicator
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 8.dp else 12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(if (isCompact) 8.dp else 12.dp))

                Text(
                    text = screenName,
                    style = if (isCompact) {
                        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    } else {
                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    },
                    maxLines = if (isCompact) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = if (isCompact) 18.sp else 20.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = accentColor,
                modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManagementScreenLayout() {
    val mockState = ManagementUiState(
        isLoading = false,
        sections = mapOf(
            "Roles and Permissions" to listOf(
                ManagementScreenPermissionState(
                    screen = ManagementScreen.ROLES_LIST_AND_EDIT,
                    hasAllPermissions = true
                ),
                ManagementScreenPermissionState(
                    screen = ManagementScreen.MANAGE_PERMISSIONS,
                    hasAllPermissions = true
                ),
                ManagementScreenPermissionState(
                    screen = ManagementScreen.USER_ROLE_MANAGEMENT,
                    hasAllPermissions = true
                )
            ),
            "User Management" to listOf(
                ManagementScreenPermissionState(
                    screen = ManagementScreen.ROLES_LIST_AND_EDIT,
                    hasAllPermissions = true
                ),
                ManagementScreenPermissionState(
                    screen = ManagementScreen.MANAGE_PERMISSIONS,
                    hasAllPermissions = true
                )
            )
        )
    )

    MaterialTheme {
        ManagementScreenLayout(
            uiState = mockState,
            onRefresh = {},
            onNavigateToScreen = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun PreviewManagementScreenTablet() {
    val mockState = ManagementUiState(
        isLoading = false,
        sections = mapOf(
            "Roles and Permissions" to listOf(
                ManagementScreenPermissionState(
                    screen = ManagementScreen.ROLES_LIST_AND_EDIT,
                    hasAllPermissions = true
                ),
                ManagementScreenPermissionState(
                    screen = ManagementScreen.MANAGE_PERMISSIONS,
                    hasAllPermissions = true
                ),
                ManagementScreenPermissionState(
                    screen = ManagementScreen.USER_ROLE_MANAGEMENT,
                    hasAllPermissions = true
                ),
                ManagementScreenPermissionState(
                    screen = ManagementScreen.ROLES_LIST_AND_EDIT,
                    hasAllPermissions = true
                )
            )
        )
    )

    MaterialTheme {
        ManagementScreenLayout(
            uiState = mockState,
            onRefresh = {},
            onNavigateToScreen = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManagementScreenLoading() {
    val mockState = ManagementUiState(
        isLoading = true
    )

    MaterialTheme {
        ManagementScreenLayout(
            uiState = mockState,
            onRefresh = {},
            onNavigateToScreen = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManagementScreenEmpty() {
    val mockState = ManagementUiState(
        isLoading = false,
        sections = emptyMap()
    )

    MaterialTheme {
        ManagementScreenLayout(
            uiState = mockState,
            onRefresh = {},
            onNavigateToScreen = {},
            onBackPressed = {}
        )
    }
}