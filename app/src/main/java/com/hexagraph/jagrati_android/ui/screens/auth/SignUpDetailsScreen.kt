package com.hexagraph.jagrati_android.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.model.AuthResult
import com.hexagraph.jagrati_android.ui.components.auth.PasswordInput
import com.hexagraph.jagrati_android.ui.components.auth.PrimaryButton
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.viewmodels.auth.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Sign up details screen component.
 *
 * @param email Email from the previous screen
 * @param snackbarHostState SnackbarHostState for displaying messages
 * @param navigateToEmailVerification Callback to navigate to email verification screen
 * @param navigateBack Callback to navigate back to the previous screen
 * @param viewModel SignUpViewModel instance
 */
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                        onNext = { /* Focus on last name */ }
                    ),
                    singleLine = true
                )

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
                        onNext = { /* Focus on password */ }
                    ),
                    singleLine = true
                )

                // Password input
                PasswordInput(
                    password = password,
                    onPasswordChange = viewModel::updatePassword,
                    isPasswordVisible = isPasswordVisible,
                    onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                    imeAction = ImeAction.Next,
                    onImeAction = { /* Focus on confirm password */ }
                )

                // Confirm password input
                PasswordInput(
                    password = confirmPassword,
                    onPasswordChange = viewModel::updateConfirmPassword,
                    label = "Confirm Password",
                    isPasswordVisible = isConfirmPasswordVisible,
                    onTogglePasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        if (firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                            viewModel.createUserWithEmailAndPassword()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = "Sign Up",
                    onClick = { viewModel.createUserWithEmailAndPassword() },
                    isLoading = signUpState is AuthResult.Loading
                )
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
