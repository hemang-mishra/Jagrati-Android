package com.hexagraph.jagrati_android.ui.screens.onboarding

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OnboardingPage {
    Welcome,
    Features,
    PrivacyPolicy
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onCompleteOnboarding: () -> Unit
) {
    var currentPage by remember { mutableStateOf(OnboardingPage.Welcome) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.screenWidthDp >= 600

    BackHandler(enabled = currentPage != OnboardingPage.Welcome) {
        when (currentPage) {
            OnboardingPage.Features -> currentPage = OnboardingPage.Welcome
            OnboardingPage.PrivacyPolicy -> currentPage = OnboardingPage.Features
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        // Animated gradient background
        AnimatedGradientBackground()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> direction * fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(400)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -direction * fullWidth },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeOut(animationSpec = tween(300))
                },
                label = "Onboarding Pages",
            ) { page ->
                when (page) {
                    OnboardingPage.Welcome -> {
                        if (isLandscape && !isTablet) {
                            LandscapeOnboardingContent(
                                title = "Welcome to",
                                highlight = "Jagrati",
                                description = "More than an initiative, a promise. To learn, to share, and to keep the light of education alive in every home we reach.",
                                buttonText = "Get Started",
                                currentStep = 1,
                                totalSteps = 3,
                                showLearnMore = true,
                                onNextClick = { currentPage = OnboardingPage.Features },
                                modifier = Modifier
                            )
                        } else {
                            PortraitOnboardingContent(
                                title = "Welcome to",
                                highlight = "Jagrati",
                                description = "More than an initiative, a promise. To learn, to share, and to keep the light of education alive in every home we reach.",
                                buttonText = "Get Started",
                                currentStep = 1,
                                totalSteps = 3,
                                showLearnMore = true,
                                onNextClick = { currentPage = OnboardingPage.Features },
                                isTablet = isTablet
                            )
                        }
                    }
                    OnboardingPage.Features -> {
                        if (isLandscape && !isTablet) {
                            LandscapeOnboardingContent(
                                title = "Powerful Features",
                                highlight = "All in One Place",
                                description = "Smart facial recognition attendance, seamless volunteer management, secure profile access, event participation, and real-time progress tracking.",
                                buttonText = "Continue",
                                currentStep = 2,
                                totalSteps = 3,
                                showLearnMore = false,
                                onNextClick = { currentPage = OnboardingPage.PrivacyPolicy },
                                showNextIcon = true,
                                modifier = Modifier
                            )
                        } else {
                            PortraitOnboardingContent(
                                title = "Powerful Features",
                                highlight = "All in One Place",
                                description = "Smart facial recognition attendance, seamless volunteer management, secure profile access, event participation, and real-time progress tracking.",
                                buttonText = "Continue",
                                currentStep = 2,
                                totalSteps = 3,
                                showLearnMore = false,
                                onNextClick = { currentPage = OnboardingPage.PrivacyPolicy },
                                showNextIcon = true,
                                isTablet = isTablet
                            )
                        }
                    }
                    OnboardingPage.PrivacyPolicy -> {
                        PrivacyPolicyPage(
                            onAccept = onCompleteOnboarding,
                            onDecline = { currentPage = OnboardingPage.Features }
                        )
                    }
                }
            }
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
private fun PortraitOnboardingContent(
    title: String,
    highlight: String,
    description: String,
    buttonText: String,
    currentStep: Int,
    totalSteps: Int,
    showLearnMore: Boolean,
    onNextClick: () -> Unit,
    showNextIcon: Boolean = false,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Staggered animations for each element
    var animationsStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationsStarted = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = if (isTablet) 48.dp else 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))

        // Progress indicator with delay
        AnimatedElement(delay = 0, animationsStarted = animationsStarted) {
            ProgressIndicator(currentStep = currentStep, totalSteps = totalSteps)
        }

        Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))

        // Hero content card with delay
        AnimatedElement(delay = 100, animationsStarted = animationsStarted) {
            HeroContentCard(
                title = title,
                highlight = highlight,
                description = description,
                isTablet = isTablet,
                currentStep = currentStep,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons with delay
        AnimatedElement(delay = 200, animationsStarted = animationsStarted) {
            ActionButtons(
                buttonText = buttonText,
                onNextClick = onNextClick,
                showLearnMore = showLearnMore,
                showNextIcon = showNextIcon,
                isTablet = isTablet,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))
    }
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
private fun LandscapeOnboardingContent(
    title: String,
    highlight: String,
    description: String,
    buttonText: String,
    currentStep: Int,
    totalSteps: Int,
    showLearnMore: Boolean,
    onNextClick: () -> Unit,
    showNextIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    var animationsStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationsStarted = true
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            AnimatedElement(delay = 0, animationsStarted = animationsStarted) {
                ProgressIndicator(currentStep = currentStep, totalSteps = totalSteps)
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedElement(delay = 100, animationsStarted = animationsStarted) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Text(
                        text = highlight,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedElement(delay = 200, animationsStarted = animationsStarted) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    lineHeight = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedElement(delay = 300, animationsStarted = animationsStarted) {
                ActionButtons(
                    buttonText = buttonText,
                    onNextClick = onNextClick,
                    showLearnMore = showLearnMore,
                    showNextIcon = showNextIcon,
                    isTablet = false,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }

        // Right side - Image
        AnimatedElement(delay = 150, animationsStarted = animationsStarted) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                HeroImage(isTablet = false)
            }
        }
    }
}

@Composable
private fun HeroContentCard(
    title: String,
    highlight: String,
    description: String,
    isTablet: Boolean,
    currentStep: Int,
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
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        )
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
                modifier = Modifier.padding(if (isTablet) 40.dp else 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Image
                HeroImage(isTablet = isTablet)

                Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 24.dp))

                // Title
                Text(
                    text = title,
                    style = if (isTablet) MaterialTheme.typography.headlineMedium
                    else MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                // Highlighted text with gradient-like effect
                Text(
                    text = highlight,
                    style = if (isTablet) MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    else MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Divider for visual separation
                Divider(
                    modifier = Modifier
                        .width(60.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text(
                    text = description,
                    style = if (isTablet) MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    else MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = if (isTablet) 28.sp else 26.sp,
                    modifier = Modifier.padding(horizontal = if (isTablet) 24.dp else 8.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroImage(isTablet: Boolean) {
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
            .size(if (isTablet) 280.dp else 220.dp)
            .graphicsLayer {
                translationY = floatOffset
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer glow effect
        Box(
            modifier = Modifier
                .size(if (isTablet) 300.dp else 240.dp)
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
                .size(if (isTablet) 280.dp else 220.dp)
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
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Jagrati Logo",
                modifier = Modifier
                    .size(if (isTablet) 240.dp else 180.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ProgressIndicator(
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
private fun ActionButtons(
    buttonText: String,
    onNextClick: () -> Unit,
    showLearnMore: Boolean,
    showNextIcon: Boolean,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button scale"
    )

    val openUrl = { url: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                isPressed = true
                onNextClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTablet) 64.dp else 56.dp)
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
                text = buttonText,
                style = if (isTablet) MaterialTheme.typography.titleLarge
                else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            if (showNextIcon) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (showLearnMore) {
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { openUrl("https://jagrati.iiitdmj.ac.in/") },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Learn More About Jagrati",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PrivacyPolicyPage(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val context = LocalContext.current
    var privacyAccepted by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == 2) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == 2)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                )
                if (index < 2) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Privacy Policy",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your Privacy Matters to Us",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy policy summary card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                PrivacyPolicySummaryItem(
                    iconRes = R.drawable.ic_photo_camera,
                    title = "Photos & Data Collection",
                    description = "We collect profile photos and attendance data to provide our services. You have full control over your data."
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrivacyPolicySummaryItem(
                    iconRes = R.drawable.ic_lock,
                    title = "Secure & Encrypted",
                    description = "All your data is encrypted and stored securely. We use industry-standard security practices."
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrivacyPolicySummaryItem(
                    iconRes = R.drawable.ic_shield,
                    title = "Your Control",
                    description = "You can update or delete your data anytime. Account deletion permanently removes all your information."
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrivacyPolicySummaryItem(
                    iconRes = R.drawable.ic_admin_panel_settings,
                    title = "No Selling or Sharing",
                    description = "We never sell or share your personal information with third parties for marketing purposes."
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://hemang-mishra.github.io/Jagrati-Android/")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Read Full Privacy Policy",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Checkbox for acceptance
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = privacyAccepted,
                onCheckedChange = { privacyAccepted = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "I have read and accept the Privacy Policy",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDecline,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onAccept,
                enabled = privacyAccepted,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Accept & Continue",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PrivacyPolicySummaryItem(
    iconRes: Int,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 0.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    JagratiAndroidTheme {
        OnboardingScreen(
            onCompleteOnboarding = { }
        )
    }
}
