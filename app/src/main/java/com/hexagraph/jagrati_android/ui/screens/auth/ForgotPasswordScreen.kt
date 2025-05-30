package com.hexagraph.jagrati_android.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.ui.components.auth.EmailInput
import com.hexagraph.jagrati_android.ui.components.auth.PrimaryButton
import com.hexagraph.jagrati_android.ui.components.auth.TextLinkButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.ForgotPasswordViewModel

/**
 * Forgot password screen component.
 *
 * @param snackbarHostState SnackbarHostState for displaying messages
 * @param navigateToLogin Callback to navigate to login screen
 * @param viewModel ForgotPasswordViewModel instance
 */
@Composable
fun ForgotPasswordScreen(
    snackbarHostState: SnackbarHostState,
    navigateToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val resetPasswordState by viewModel.resetPasswordState.collectAsState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                    imeAction = ImeAction.Done,
                    onImeAction = { 
                        if (email.isNotBlank()) {
                            viewModel.sendPasswordResetEmail()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = "Reset Password",
                    onClick = { viewModel.sendPasswordResetEmail() },
                    isLoading = resetPasswordState is AuthResult.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))

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
