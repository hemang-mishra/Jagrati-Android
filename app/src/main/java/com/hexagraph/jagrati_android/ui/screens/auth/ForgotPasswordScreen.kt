package com.hexagraph.jagrati_android.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.ui.components.EmailInput
import com.hexagraph.jagrati_android.ui.components.PrimaryButton
import com.hexagraph.jagrati_android.ui.components.TextLinkButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.ForgotPasswordViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    snackbarHostState: SnackbarHostState,
    navigateToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val resetPasswordState by viewModel.resetPasswordState.collectAsState()

    // Animation states
    val (showIcon, setShowIcon) = remember { mutableStateOf(false) }
    val (showTitle, setShowTitle) = remember { mutableStateOf(false) }
    val (showForm, setShowForm) = remember { mutableStateOf(false) }
    val (showButtons, setShowButtons) = remember { mutableStateOf(false) }

    // Sequential animations
    LaunchedEffect(Unit) {
        setShowIcon(true)
        delay(300)
        setShowTitle(true)
        delay(200)
        setShowForm(true)
        delay(200)
        setShowButtons(true)
    }

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

    // Handle reset password state changes
    LaunchedEffect(resetPasswordState) {
        when (resetPasswordState) {
            is AuthResult.Success -> {
                snackbarHostState.showSnackbar("Password reset email sent. Check your inbox.")
                viewModel.resetPasswordResetState()
                navigateToLogin()
            }
            is AuthResult.Error -> {
                snackbarHostState.showSnackbar((resetPasswordState as AuthResult.Error).message)
                viewModel.resetPasswordResetState()
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

            // Animated Icon
            AnimatedVisibility(
                visible = showIcon,
                enter = fadeIn(animationSpec = tween(500)) +
                       slideInVertically(
                           initialOffsetY = { -200 },
                           animationSpec = tween(500)
                       )
            ) {
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
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Reset Password Icon",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
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
                        text = "Forgot Password",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter your email to receive a password reset link",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Animated Form
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmailInput(
                            email = email,
                            onEmailChange = viewModel::updateEmail,
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                if (email.isNotBlank()) {
                                    viewModel.sendPasswordResetEmail()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = "Reset Password",
                            onClick = { viewModel.sendPasswordResetEmail() },
                            isLoading = resetPasswordState is AuthResult.Loading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Back Button
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(700))
            ) {
                TextLinkButton(
                    text = "Back to Login",
                    onClick = navigateToLogin
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    JagratiAndroidTheme {
        ForgotPasswordScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navigateToLogin = {}
        )
    }
}
