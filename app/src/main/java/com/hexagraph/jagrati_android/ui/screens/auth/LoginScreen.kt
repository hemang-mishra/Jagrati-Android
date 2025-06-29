package com.hexagraph.jagrati_android.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.ui.components.auth.EmailInput
import com.hexagraph.jagrati_android.ui.components.auth.PasswordInput
import com.hexagraph.jagrati_android.ui.components.auth.PrimaryButton
import com.hexagraph.jagrati_android.ui.components.auth.TextLinkButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.LoginViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Login screen component.
 *
 * @param snackbarHostState SnackbarHostState for displaying messages
 * @param navigateToHome Callback to navigate to home screen
 * @param navigateToSignUp Callback to navigate to sign up screen
 * @param navigateToForgotPassword Callback to navigate to forgot password screen
 * @param viewModel LoginViewModel instance
 */
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = "Sign In",
                    onClick = { viewModel.signInWithEmailAndPassword() },
                    isLoading = loginState is AuthResult.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign-In Button
                OutlinedButton(
                    onClick = {
                        viewModel.signInWithGoogle(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !(googleSignInState is AuthResult.Loading),
                    border = BorderStroke(1.dp, Color.LightGray)
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
                        Text(text = "Sign in with Google")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )

                TextLinkButton(
                    text = "Sign Up",
                    onClick = navigateToSignUp
                )
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
