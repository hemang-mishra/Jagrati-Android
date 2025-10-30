package com.hexagraph.jagrati_android.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onAllPermissionsGranted: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showSkipDialog by remember { mutableStateOf(false) }

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
                actions = {
                    TextButton(
                        onClick = { showSkipDialog = true }
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        // Animated gradient background
        AnimatedGradientBackground()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (permissionsToRequest.isNotEmpty() && currentPermissionIndex < permissionsToRequest.size) {
                val currentPermission = permissionsToRequest[currentPermissionIndex]

                // Staggered animations
                var animationsStarted by remember { mutableStateOf(false) }

                LaunchedEffect(currentPermissionIndex) {
                    animationsStarted = false
                    delay(50)
                    animationsStarted = true
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedElement(delay = 0, animationsStarted = animationsStarted) {
                        PermissionProgressIndicator(
                            currentStep = currentPermissionIndex + 1,
                            totalSteps = permissionsToRequest.size
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedElement(delay = 100, animationsStarted = animationsStarted) {
                        PermissionContentCard(
                            permission = currentPermission,
                            isPermanentlyDeclined = permanentlyDeclinedPermission == currentPermission,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedElement(delay = 200, animationsStarted = animationsStarted) {
                        PermissionActionButtons(
                            permission = currentPermission,
                            isPermanentlyDeclined = permanentlyDeclinedPermission == currentPermission,
                            onRequestPermission = {
                                if (permanentlyDeclinedPermission == currentPermission) {
                                    PermissionsRequired.openAppSettings(context)
                                } else {
                                    permissionLauncher.launch(currentPermission.permission)
                                }
                            },
                            onSkip = { showSkipDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Skip confirmation dialog
        if (showSkipDialog) {
            SkipPermissionsDialog(
                onConfirm = onAllPermissionsGranted,
                onDismiss = { showSkipDialog = false }
            )
        }
    }
}

@Composable
private fun AnimatedGradientBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.03f * offset),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f * (1 - offset)),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.02f * offset)
                    )
                )
            )
    )
}

@Composable
private fun AnimatedElement(
    delay: Long,
    animationsStarted: Boolean,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(30f) }

    LaunchedEffect(animationsStarted) {
        if (animationsStarted) {
            delay(delay)
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha.value
                translationY = offsetY.value
            }
    ) {
        content()
    }
}

@Composable
private fun SkipPermissionsDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Skip Permissions?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Some features may not work properly without these permissions.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "💡 You can enable them later from Settings",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Skip Anyway",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Go Back",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun PermissionContentCard(
    permission: PermissionsRequired,
    isPermanentlyDeclined: Boolean,
    modifier: Modifier = Modifier
) {
    // Subtle pulsing animation for the card
    val scale = remember { Animatable(0.95f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Card(
        modifier = modifier.scale(scale.value),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box {
            // Decorative gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated permission icon
                AnimatedPermissionIcon(permission = permission)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = permission.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Divider for visual separation
                Divider(
                    modifier = Modifier
                        .width(60.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isPermanentlyDeclined)
                        permission.permanentlyDeclinedRationale
                    else
                        permission.rationaleText,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedPermissionIcon(permission: PermissionsRequired) {
    // Floating animation for the image
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .size(220.dp)
            .graphicsLayer {
                translationY = floatOffset
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer glow effect
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main image container
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
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
    }
}

@Composable
private fun PermissionProgressIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(totalSteps) { index ->
            val isActive = index < currentStep
            val isPast = index < currentStep - 1

            // Animate the width of active indicator
            val width by animateDpAsState(
                targetValue = when {
                    isActive && !isPast -> 32.dp
                    isPast -> 12.dp
                    else -> 10.dp
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "Indicator Width"
            )

            val height by animateDpAsState(
                targetValue = if (isActive) 10.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "Indicator Height"
            )

            Surface(
                modifier = Modifier
                    .width(width)
                    .height(height)
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(8.dp),
                color = when {
                    isActive -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                }
            ) {}
        }
    }
}

@Composable
private fun PermissionActionButtons(
    permission: PermissionsRequired,
    isPermanentlyDeclined: Boolean,
    onRequestPermission: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                isPressed = true
                onRequestPermission()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .scale(buttonScale),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Text(
                text = if (isPermanentlyDeclined) "Open Settings" else "Grant Permission",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = if (isPermanentlyDeclined) "Open Settings" else "Grant Permission",
                modifier = Modifier.size(20.dp)
            )
        }

        TextButton(
            onClick = onSkip,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Skip for Now",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
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

