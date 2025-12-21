package com.hexagraph.jagrati_android.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.ui.components.EmailInput
import com.hexagraph.jagrati_android.ui.components.PasswordInput
import com.hexagraph.jagrati_android.ui.components.PrimaryButton
import com.hexagraph.jagrati_android.ui.components.TextLinkButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.LoginViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    navigateToEmailVerification: (String) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Animation states
    val (showLogo, setShowLogo) = remember { mutableStateOf(false) }
    val (showTitle, setShowTitle) = remember { mutableStateOf(false) }
    val (showForm, setShowForm) = remember { mutableStateOf(false) }
    val (showButtons, setShowButtons) = remember { mutableStateOf(false) }

    // Sequential animations
    LaunchedEffect(Unit) {
        setShowLogo(true)
        delay(200)
        setShowTitle(true)
        delay(300)
        setShowForm(true)
        delay(300)
        setShowButtons(true)
    }

    // Content animation
    val contentAlpha by animateFloatAsState(
        targetValue = if (showForm) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "Content Alpha"
    )

    // Handle error and success messages
    LaunchedEffect(uiState.error, uiState.successMsg) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error.toast)
            viewModel.clearErrorFlow()
        }

        uiState.successMsg?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMsgFlow()
        }
    }

    // Handle login state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthResult.Success -> {
                navigateToHome()
            }
            is AuthResult.Error -> {
                snackbarHostState.showSnackbar((loginState as AuthResult.Error).message)
                viewModel.resetLoginState()
            }
            is AuthResult.VerificationNeeded -> {
                navigateToEmailVerification((loginState as AuthResult.VerificationNeeded).email)
            }
            else -> {}
        }
    }

    // Handle Google sign-in state changes
    val googleSignInState by viewModel.googleSignInState.collectAsState()
    LaunchedEffect(googleSignInState) {
        when (googleSignInState) {
            is AuthResult.Success -> {
                navigateToHome()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Animated Logo
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(animationSpec = tween(500)) +
                       slideInVertically(
                           initialOffsetY = { -200 },
                           animationSpec = tween(500)
                       )
            ) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(140.dp)
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
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Jagrati Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Title
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sign in to continue",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Login Form with animation
            AnimatedVisibility(
                visible = showForm,
                enter = fadeIn(animationSpec = tween(500))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        EmailInput(
                            email = email,
                            onEmailChange = viewModel::updateEmail,
                            imeAction = ImeAction.Next,
                            onImeAction = { /* Focus on password */ }
                        )

                        PasswordInput(
                            password = password,
                            onPasswordChange = viewModel::updatePassword,
                            isPasswordVisible = isPasswordVisible,
                            onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    viewModel.signInWithEmailAndPassword()
                                }
                            }
                        )

                        TextLinkButton(
                            text = "Forgot Password?",
                            onClick = navigateToForgotPassword,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PrimaryButton(
                            text = "Sign In",
                            onClick = { viewModel.signInWithEmailAndPassword() },
                            isLoading = loginState is AuthResult.Loading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom buttons with animation
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(700))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Sign-In Button
                    OutlinedButton(
                        onClick = {
                            viewModel.signInWithGoogle(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !(googleSignInState is AuthResult.Loading),
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (googleSignInState is AuthResult.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = "Sign in with Google",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Don't have an account?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextLinkButton(
                            text = "Sign Up",
                            onClick = navigateToSignUp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    JagratiAndroidTheme {
        LoginScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navigateToHome = {},
            navigateToSignUp = {},
            navigateToForgotPassword = {},
            navigateToEmailVerification = {},
        )
    }
}
