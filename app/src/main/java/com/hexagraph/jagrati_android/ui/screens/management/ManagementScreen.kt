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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Management") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.sections.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No management sections available for your permissions",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    uiState.sections.forEach { (sectionName, screens) ->
                        val availableScreens = screens.filter { it.hasAllPermissions }
                        if (availableScreens.isNotEmpty()) {
                            item(key = sectionName) {
                                SectionHeader(sectionName = sectionName)
                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    availableScreens.forEach { screenState ->
                                        ManagementScreenItem(
                                            screenName = screenState.screen.screenName,
                                            onClick = {
                                                onNavigateToScreen(screenState.screen.screen)
                                            }
                                        )
                                    }
                                }
                            }
                        }
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
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(bottom = 4.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ManagementScreenItem(
    screenName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = screenName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.primary
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
                    hasAllPermissions = false
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
