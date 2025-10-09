package com.hexagraph.jagrati_android.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.ui.components.EmailInput
import com.hexagraph.jagrati_android.ui.components.PrimaryButton
import com.hexagraph.jagrati_android.ui.components.TextLinkButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.SignUpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpEmailScreen(
    snackbarHostState: SnackbarHostState,
    navigateToSignUpDetails: (String) -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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
                        imageVector = Icons.Default.Person,
                        contentDescription = "Create Account Icon",
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
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter your email to get started",
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
                                    validateAndNavigate(
                                        email = email,
                                        isEmailValid = viewModel.isEmailValid(),
                                        snackbarHostState = snackbarHostState,
                                        navigateToSignUpDetails = navigateToSignUpDetails,
                                        coroutineScope = coroutineScope
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = "Next",
                            onClick = {
                                validateAndNavigate(
                                    email = email,
                                    isEmailValid = viewModel.isEmailValid(),
                                    snackbarHostState = snackbarHostState,
                                    navigateToSignUpDetails = navigateToSignUpDetails,
                                    coroutineScope = coroutineScope
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Bottom Text and Link
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(700))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextLinkButton(
                        text = "Sign In",
                        onClick = navigateToLogin
                    )
                }
            }
        }
    }
}

/**
 * Validates the email and navigates to the sign up details screen if valid.
 */
private fun validateAndNavigate(
    email: String,
    isEmailValid: Boolean,
    snackbarHostState: SnackbarHostState,
    navigateToSignUpDetails: (String) -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    if (email.isBlank()) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Email cannot be empty")
        }
        return
    }

    if (!isEmailValid) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Please enter a valid email address")
        }
        return
    }

    navigateToSignUpDetails(email)
}

@Preview(showBackground = true)
@Composable
fun SignUpEmailScreenPreview() {
    JagratiAndroidTheme {
        SignUpEmailScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navigateToSignUpDetails = {},
            navigateToLogin = {}
        )
    }
}
