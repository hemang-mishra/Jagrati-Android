package com.hexagraph.jagrati_android.ui.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme

/**
 * Email input field component.
 *
 * @param email Current email value
 * @param onEmailChange Callback when email changes
 * @param modifier Modifier for styling
 * @param isError Whether the input has an error
 * @param errorMessage Error message to display
 * @param imeAction IME action for keyboard
 * @param onImeAction Callback when IME action is triggered
 */
@Composable
fun EmailInput(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Email") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon"
            )
        },
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() },
            onGo = { onImeAction() }
        ),
        singleLine = true
    )
}

/**
 * Password input field component.
 *
 * @param password Current password value
 * @param onPasswordChange Callback when password changes
 * @param label Label for the input field
 * @param isPasswordVisible Whether the password is visible
 * @param onTogglePasswordVisibility Callback to toggle password visibility
 * @param modifier Modifier for styling
 * @param isError Whether the input has an error
 * @param errorMessage Error message to display
 * @param imeAction IME action for keyboard
 * @param onImeAction Callback when IME action is triggered
 */
@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    label: String = "Password",
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Password Icon"
            )
        },
        trailingIcon = {
            TextButton(onClick = onTogglePasswordVisibility) {
                Text(
                    text = if (isPasswordVisible) "Hide" else "Show",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() },
            onGo = { onImeAction() }
        ),
        singleLine = true
    )
}

/**
 * Primary button component.
 *
 * @param text Button text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for styling
 * @param isLoading Whether the button is in loading state
 * @param enabled Whether the button is enabled
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(end = 8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        }
        Text(text = text)
    }
}

/**
 * Text button component.
 *
 * @param text Button text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for styling
 */
@Composable
fun TextLinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun EmailInputPreview() {
    JagratiAndroidTheme {
        EmailInput(
            email = "user@example.com",
            onEmailChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordInputPreview() {
    JagratiAndroidTheme {
        PasswordInput(
            password = "password123",
            onPasswordChange = {},
            isPasswordVisible = false,
            onTogglePasswordVisibility = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    JagratiAndroidTheme {
        PrimaryButton(
            text = "Sign In",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * Google Sign-In button component.
 *
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for styling
 * @param isLoading Whether the button is in loading state
 * @param enabled Whether the button is enabled
 */
@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = "Sign in with Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoogleSignInButtonPreview() {
    JagratiAndroidTheme {
        GoogleSignInButton(
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TextLinkButtonPreview() {
    JagratiAndroidTheme {
        TextLinkButton(
            text = "Forgot Password?",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
