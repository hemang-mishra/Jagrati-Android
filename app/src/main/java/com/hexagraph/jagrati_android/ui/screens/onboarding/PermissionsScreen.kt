package com.hexagraph.jagrati_android.ui.screens.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hexagraph.jagrati_android.permissions.PermissionsRequired
import com.hexagraph.jagrati_android.permissions.rememberPermissionLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onAllPermissionsGranted: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var permissionsToRequest by remember {
        mutableStateOf(
            PermissionsRequired.values().filter {
                PermissionsRequired.isPermissionAvailable(it) &&
                !PermissionsRequired.isPermissionGranted(context, it.permission)
            }
        )
    }

    var permanentlyDeclinedPermission by remember { mutableStateOf<PermissionsRequired?>(null) }
    var currentPermissionIndex by remember { mutableStateOf(0) }

    val refreshPermissionStatus = {
        permissionsToRequest = PermissionsRequired.values().filter {
            PermissionsRequired.isPermissionAvailable(it) &&
            !PermissionsRequired.isPermissionGranted(context, it.permission)
        }

        if (permissionsToRequest.isEmpty()) {
            onAllPermissionsGranted()
        } else if (permanentlyDeclinedPermission != null) {
            val isNowGranted = PermissionsRequired.isPermissionGranted(
                context,
                permanentlyDeclinedPermission!!.permission
            )
            if (isNowGranted) {
                permanentlyDeclinedPermission = null

                if (currentPermissionIndex < permissionsToRequest.size - 1) {
                    currentPermissionIndex++
                } else if (permissionsToRequest.isEmpty()) {
                    onAllPermissionsGranted()
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissionStatus()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissionLauncher = rememberPermissionLauncher { isGranted ->
        if (isGranted) {
            permissionsToRequest = PermissionsRequired.values().filter {
                PermissionsRequired.isPermissionAvailable(it) &&
                !PermissionsRequired.isPermissionGranted(context, it.permission)
            }

            if (permissionsToRequest.isEmpty()) {
                onAllPermissionsGranted()
            } else if (currentPermissionIndex < permissionsToRequest.size - 1) {
                currentPermissionIndex++
            }
        } else {
            val permission = permissionsToRequest[currentPermissionIndex]
            if (!shouldShowRequestPermissionRationale(context, permission.permission)) {
                permanentlyDeclinedPermission = permission
            }
        }
    }

    LaunchedEffect(Unit) {
        if (permissionsToRequest.isEmpty()) {
            onAllPermissionsGranted()
        }
    }

    BackHandler {
        onBackClick()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (permissionsToRequest.isNotEmpty() && currentPermissionIndex < permissionsToRequest.size) {
                val currentPermission = permissionsToRequest[currentPermissionIndex]

                val contentAlpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500),
                    label = "Content Alpha"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .graphicsLayer(alpha = contentAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    PermissionProgressIndicator(
                        currentStep = currentPermissionIndex + 1,
                        totalSteps = permissionsToRequest.size
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    PermissionContentCard(
                        permission = currentPermission,
                        isPermanentlyDeclined = permanentlyDeclinedPermission == currentPermission,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))

                    PermissionActionButton(
                        permission = currentPermission,
                        isPermanentlyDeclined = permanentlyDeclinedPermission == currentPermission,
                        onRequestPermission = {
                            if (permanentlyDeclinedPermission == currentPermission) {
                                PermissionsRequired.openAppSettings(context)
                            } else {
                                permissionLauncher.launch(currentPermission.permission)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun PermissionContentCard(
    permission: PermissionsRequired,
    isPermanentlyDeclined: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = permission.image),
                    contentDescription = permission.title,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = permission.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isPermanentlyDeclined)
                    permission.permanentlyDeclinedRationale
                else
                    permission.rationaleText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PermissionProgressIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(totalSteps) { index ->
            val isActive = index < currentStep

            val size by animateDpAsState(
                targetValue = if (isActive) 12.dp else 8.dp,
                animationSpec = tween(durationMillis = 300),
                label = "Indicator Size"
            )

            Surface(
                modifier = Modifier
                    .size(size)
                    .padding(horizontal = 4.dp),
                shape = CircleShape,
                color = if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            ) {}
        }
    }
}

@Composable
private fun PermissionActionButton(
    permission: PermissionsRequired,
    isPermanentlyDeclined: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onRequestPermission,
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = if (isPermanentlyDeclined) "Open Settings" else "Grant Permission",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = if (isPermanentlyDeclined) "Open Settings" else "Grant Permission"
        )
    }
}

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
