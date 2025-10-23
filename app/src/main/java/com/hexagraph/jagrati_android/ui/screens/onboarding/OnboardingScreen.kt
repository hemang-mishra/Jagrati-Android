package com.hexagraph.jagrati_android.ui.screens.onboarding

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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

enum class OnboardingPage {
    Welcome,
    Management,
    Attendance
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
            OnboardingPage.Management -> currentPage = OnboardingPage.Welcome
            OnboardingPage.Attendance -> currentPage = OnboardingPage.Management
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
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
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -direction * fullWidth },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
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
                                description = "More than an initiative,a promise. To learn, to share, and to keep the light of education alive in every home we reach.",
                                buttonText = "Get Started",
                                currentStep = 1,
                                totalSteps = 3,
                                showLearnMore = false,
                                onNextClick = { currentPage = OnboardingPage.Management },
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
                                showLearnMore = false,
                                onNextClick = { currentPage = OnboardingPage.Management },
                                isTablet = isTablet
                            )
                        }
                    }
                    OnboardingPage.Management -> {
                        if (isLandscape && !isTablet) {
                            LandscapeOnboardingContent(
                                title = "Easy",
                                highlight = "Management",
                                description = "Seamless onboarding, event participation, and secure profile access for volunteers.",
                                buttonText = "Continue",
                                currentStep = 2,
                                totalSteps = 3,
                                showLearnMore = false,
                                onNextClick = { currentPage = OnboardingPage.Attendance },
                                showNextIcon = true,
                                modifier = Modifier
                            )
                        } else {
                            PortraitOnboardingContent(
                                title = "Easy",
                                highlight = "Management",
                                description = "Seamless onboarding, event participation, and secure profile access for volunteers.",
                                buttonText = "Continue",
                                currentStep = 2,
                                totalSteps = 3,
                                showLearnMore = false,
                                onNextClick = { currentPage = OnboardingPage.Attendance },
                                showNextIcon = true,
                                isTablet = isTablet
                            )
                        }
                    }
                    OnboardingPage.Attendance -> {
                        if (isLandscape && !isTablet) {
                            LandscapeOnboardingContent(
                                title = "Smart",
                                highlight = "Attendance",
                                description = "Facial recognition, progress tracking, and leaderboards boost volunteer motivation.",
                                buttonText = "Get Started",
                                currentStep = 3,
                                totalSteps = 3,
                                showLearnMore = false,
                                onNextClick = onCompleteOnboarding,
                                showNextIcon = true,
                                modifier = Modifier
                            )
                        } else {
                            PortraitOnboardingContent(
                                title = "Smart",
                                highlight = "Attendance",
                                description = "Facial recognition, progress tracking, and leaderboards boost volunteer motivation.",
                                buttonText = "Get Started",
                                currentStep = 3,
                                totalSteps = 3,
                                showLearnMore = false,
                                onNextClick = onCompleteOnboarding,
                                showNextIcon = true,
                                isTablet = isTablet
                            )
                        }
                    }
                }
            }
        }
    }
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

    // Animate content entry
    val contentAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500),
        label = "Content Alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = if (isTablet) 48.dp else 24.dp)
            .graphicsLayer(alpha = contentAlpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 24.dp))

        // Progress indicator
        ProgressIndicator(currentStep = currentStep, totalSteps = totalSteps)

        Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))

        // Hero content card
        HeroContentCard(
            title = title,
            highlight = highlight,
            description = description,
            isTablet = isTablet,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        ActionButtons(
            buttonText = buttonText,
            onNextClick = onNextClick,
            showLearnMore = showLearnMore,
            showNextIcon = showNextIcon,
            isTablet = isTablet,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))
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
    // Animate content entry
    val contentAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500),
        label = "Content Alpha"
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .graphicsLayer(alpha = contentAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ProgressIndicator(currentStep = currentStep, totalSteps = totalSteps)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Text(
                text = highlight,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            ActionButtons(
                buttonText = buttonText,
                onNextClick = onNextClick,
                showLearnMore = showLearnMore,
                showNextIcon = showNextIcon,
                isTablet = false,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }

        // Right side - Image
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

@Composable
private fun HeroContentCard(
    title: String,
    highlight: String,
    description: String,
    isTablet: Boolean,
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
            modifier = Modifier.padding(if (isTablet) 32.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Image
            HeroImage(isTablet = isTablet)

            Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 24.dp))

            // Title with gradient effect
            Text(
                text = title,
                style = if (isTablet) MaterialTheme.typography.headlineMedium
                else MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Text(
                text = highlight,
                style = if (isTablet) MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced description
            Text(
                text = description,
                style = if (isTablet) MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                else MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                lineHeight = if (isTablet) 26.sp else 24.sp,
                modifier = Modifier.padding(horizontal = if (isTablet) 16.dp else 8.dp)
            )

            Spacer(modifier = Modifier.height(if (isTablet) 24.dp else 16.dp))
        }
    }
}

@Composable
private fun HeroImage(isTablet: Boolean) {
    Box(
        modifier = Modifier
            .size(if (isTablet) 280.dp else 220.dp)
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
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual onboarding image
            contentDescription = "Onboarding Illustration",
            modifier = Modifier
                .size(if (isTablet) 240.dp else 180.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ProgressIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(totalSteps) { index ->
            val isActive = index < currentStep

            // Animate the size of the indicators
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
private fun ActionButtons(
    buttonText: String,
    onNextClick: () -> Unit,
    showLearnMore: Boolean,
    showNextIcon: Boolean,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Function to open a URL in the browser
    val openUrl = { url: String ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTablet) 56.dp else 48.dp),
            shape = RoundedCornerShape(if (isTablet) 16.dp else 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = buttonText,
                style = if (isTablet) MaterialTheme.typography.titleMedium
                else MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            if (showNextIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }

        if (showLearnMore) {
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { openUrl("https://jagrati.iiitdmj.ac.in/") }
            ) {
                Text(
                    text = "Learn More About Jagrati",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
