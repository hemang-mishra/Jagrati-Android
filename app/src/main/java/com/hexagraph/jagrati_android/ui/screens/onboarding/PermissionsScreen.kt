package com.hexagraph.jagrati_android.ui.screens.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hexagraph.jagrati_android.permissions.PermissionsRequired
import com.hexagraph.jagrati_android.permissions.rememberPermissionLauncher
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onAllPermissionsGranted: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Animation states
    var showTitle by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(false) }
    var showDescription by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    // Trigger animations with delays
    LaunchedEffect(Unit) {
        showTitle = true
        delay(300)
        showImage = true
        delay(300)
        showDescription = true
        delay(300)
        showButtons = true
    }

    // Track permission states with mutable state to allow updates
    var permissionsToRequest by remember { 
        mutableStateOf(
            PermissionsRequired.values().filter { 
                PermissionsRequired.isPermissionAvailable(it) && 
                !PermissionsRequired.isPermissionGranted(context, it.permission)
            }
        )
    }

    // Track if any permission was permanently declined
    var permanentlyDeclinedPermission by remember { mutableStateOf<PermissionsRequired?>(null) }

    // Current permission being requested
    var currentPermissionIndex by remember { mutableStateOf(0) }

    // Function to refresh permission status
    val refreshPermissionStatus = {
        // Update the list of permissions that need to be requested
        permissionsToRequest = PermissionsRequired.values().filter { 
            PermissionsRequired.isPermissionAvailable(it) && 
            !PermissionsRequired.isPermissionGranted(context, it.permission)
        }

        // If all permissions are granted, call the callback
        if (permissionsToRequest.isEmpty()) {
            onAllPermissionsGranted()
        } else if (permanentlyDeclinedPermission != null) {
            // Check if the permanently declined permission is now granted
            val isNowGranted = PermissionsRequired.isPermissionGranted(
                context, 
                permanentlyDeclinedPermission!!.permission
            )
            if (isNowGranted) {
                // Permission was granted in settings, clear the permanently declined state
                permanentlyDeclinedPermission = null

                // Update current index if needed
                if (currentPermissionIndex < permissionsToRequest.size - 1) {
                    currentPermissionIndex++
                } else if (permissionsToRequest.isEmpty()) {
                    // All permissions granted
                    onAllPermissionsGranted()
                }
            }
        }
    }

    // Observe lifecycle events to refresh permission status when app resumes
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh permission status when app resumes
                refreshPermissionStatus()
            }
        }

        // Add the observer
        lifecycleOwner.lifecycle.addObserver(observer)

        // Remove the observer when the composable is disposed
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Permission launcher
    val permissionLauncher = rememberPermissionLauncher { isGranted ->
        if (isGranted) {
            // Update the permissions list immediately after granting
            permissionsToRequest = PermissionsRequired.values().filter { 
                PermissionsRequired.isPermissionAvailable(it) && 
                !PermissionsRequired.isPermissionGranted(context, it.permission)
            }

            // Check if all permissions are now granted
            if (permissionsToRequest.isEmpty()) {
                // All permissions granted
                onAllPermissionsGranted()
            } else if (currentPermissionIndex < permissionsToRequest.size) {
                // Continue with the current index if it's still valid
                // This handles the case where the list shrinks but there are still permissions to request
            } else {
                // Adjust the index if it's now out of bounds
                currentPermissionIndex = permissionsToRequest.size - 1
            }
        } else {
            // Permission denied, check if permanently declined
            val permission = permissionsToRequest[currentPermissionIndex]
            if (!shouldShowRequestPermissionRationale(context, permission.permission)) {
                permanentlyDeclinedPermission = permission
            }
        }
    }

    // Effect to check if all permissions are already granted
    LaunchedEffect(Unit) {
        if (permissionsToRequest.isEmpty()) {
            onAllPermissionsGranted()
        }
    }

    // UI with Scaffold
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = showButtons,
                        enter = fadeIn()
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (permissionsToRequest.isNotEmpty() && currentPermissionIndex < permissionsToRequest.size) {
                val currentPermission = permissionsToRequest[currentPermissionIndex]

                // Animated title
                AnimatedVisibility(
                    visible = showTitle,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
                ) {
                    Text(
                        text = currentPermission.title,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                }

                // Animated image
                AnimatedVisibility(
                    visible = showImage,
                    enter = fadeIn()
                ) {
                    Image(
                        painter = painterResource(id = currentPermission.image),
                        contentDescription = "Permission Image",
                        modifier = Modifier
                            .size(300.dp)
                            .padding(32.dp)
                    )
                }

                // Animated description
                AnimatedVisibility(
                    visible = showDescription,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
                ) {
                    Text(
                        text = if (permanentlyDeclinedPermission == currentPermission)
                            currentPermission.permanentlyDeclinedRationale
                        else
                            currentPermission.rationaleText,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated button
                AnimatedVisibility(
                    visible = showButtons,
                    enter = fadeIn()
                ) {
                    Button(
                        onClick = {
                            if (permanentlyDeclinedPermission == currentPermission) {
                                // Open app settings
                                PermissionsRequired.openAppSettings(context)
                            } else {
                                // Request permission
                                permissionLauncher.launch(currentPermission.permission)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = if (permanentlyDeclinedPermission == currentPermission)
                                "Open Settings"
                            else
                                "Grant Permission"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Helper function to check if permission rationale should be shown
private fun shouldShowRequestPermissionRationale(context: android.content.Context, permission: String): Boolean {
    return if (context is androidx.activity.ComponentActivity) {
        androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            permission
        )
    } else {
        false
    }
}
