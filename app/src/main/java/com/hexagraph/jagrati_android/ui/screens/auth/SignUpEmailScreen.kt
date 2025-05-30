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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexagraph.jagrati_android.ui.components.auth.EmailInput
import com.hexagraph.jagrati_android.ui.components.auth.PrimaryButton
import com.hexagraph.jagrati_android.ui.components.auth.TextLinkButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.SignUpViewModel
import kotlinx.coroutines.launch

/**
 * Sign up email screen component.
 *
 * @param snackbarHostState SnackbarHostState for displaying messages
 * @param navigateToSignUpDetails Callback to navigate to sign up details screen
 * @param navigateToLogin Callback to navigate to login screen
 * @param viewModel SignUpViewModel instance
 */
@Composable
fun SignUpEmailScreen(
    snackbarHostState: SnackbarHostState,
    navigateToSignUpDetails: (String) -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )

                TextLinkButton(
                    text = "Sign In",
                    onClick = navigateToLogin
                )
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
