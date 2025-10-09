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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.ui.components.PasswordInput
import com.hexagraph.jagrati_android.ui.components.PrimaryButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.SignUpViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpDetailsScreen(
    email: String,
    snackbarHostState: SnackbarHostState,
    navigateToEmailVerification: (String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val isConfirmPasswordVisible by viewModel.isConfirmPasswordVisible.collectAsState()
    val signUpState by viewModel.signUpState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Animation states
    val (showIcon, setShowIcon) = remember { mutableStateOf(false) }
    val (showTitle, setShowTitle) = remember { mutableStateOf(false) }
    val (showForm, setShowForm) = remember { mutableStateOf(false) }

    // Sequential animations
    LaunchedEffect(Unit) {
        setShowIcon(true)
        delay(300)
        setShowTitle(true)
        delay(300)
        setShowForm(true)
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

    // Set email from navigation argument
    LaunchedEffect(email) {
        viewModel.updateEmail(email)
    }

    // Handle sign up state changes
    LaunchedEffect(signUpState) {
        when (signUpState) {
            is AuthResult.VerificationNeeded -> {
                navigateToEmailVerification((signUpState as AuthResult.VerificationNeeded).email)
            }

            is AuthResult.Error -> {
                snackbarHostState.showSnackbar((signUpState as AuthResult.Error).message)
                viewModel.resetSignUpState()
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        .size(120.dp)
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
                        contentDescription = "Profile Details Icon",
                        modifier = Modifier.size(70.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Title
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Complete Your Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter your details to create your account",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                        // Display email (non-editable)
                        OutlinedTextField(
                            value = email,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Icon"
                                )
                            },
                            readOnly = true,
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // First Name input
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = viewModel::updateFirstName,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("First Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Person Icon"
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Last Name input
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = viewModel::updateLastName,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Last Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Person Icon"
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password input
                        PasswordInput(
                            password = password,
                            onPasswordChange = viewModel::updatePassword,
                            isPasswordVisible = isPasswordVisible,
                            onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                            imeAction = ImeAction.Next,
                            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm password input
                        PasswordInput(
                            password = confirmPassword,
                            onPasswordChange = viewModel::updateConfirmPassword,
                            label = "Confirm Password",
                            isPasswordVisible = isConfirmPasswordVisible,
                            onTogglePasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                if (firstName.isNotBlank() && lastName.isNotBlank() &&
                                    password.isNotBlank() && confirmPassword.isNotBlank()) {
                                    viewModel.createUserWithEmailAndPassword()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = "Sign Up",
                            onClick = { viewModel.createUserWithEmailAndPassword() },
                            isLoading = signUpState is AuthResult.Loading
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpDetailsScreenPreview() {
    JagratiAndroidTheme {
        SignUpDetailsScreen(
            email = "user@example.com",
            snackbarHostState = remember { SnackbarHostState() },
            navigateToEmailVerification = {},
            navigateBack = {}
        )
    }
}
