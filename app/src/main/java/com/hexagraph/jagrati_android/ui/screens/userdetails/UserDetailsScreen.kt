package com.hexagraph.jagrati_android.ui.screens.userdetails

import android.content.res.Configuration
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/**
 * Main screen composable for fetching and handling user details.
 * This screen is intended to be shown at app startup after authentication
 * to fetch user permissions and other details.
 */
@Composable
fun UserDetailsScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: UserDetailsViewModel = koinViewModel(),
    onDetailsLoaded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle UI state effects
    LaunchedEffect(uiState) {
        // Show error message if available
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error.toast
            )
//            viewModel.clearErrorFlow()
        }


        // Navigate to next screen when details are loaded successfully
        if (uiState.isSuccess) {
            onDetailsLoaded()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        UserDetailsLayout(
            modifier = Modifier.padding(paddingValues),
            isLoading = uiState.isLoading,
            errorMessage = uiState.error?.toast,
            onRetry = { viewModel.fetchUserDetails() }
        )
    }
}

/**
 * Layout component for the UserDetails screen.
 * This composable is designed to be preview-friendly and not contain any business logic.
 */
@Composable
fun UserDetailsLayout(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background gradient for visual appeal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isLoading -> {
                    LoadingContent()
                }
                errorMessage != null -> {
                    ErrorContent(errorMessage, onRetry)
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Brand image with circular background
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.jagratiapplogo),
                    contentDescription = "Jagrati Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Brand-styled progress indicator
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                // Inner accent circle
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 3.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary message
            Text(
                text = "Setting up your experience...",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary message
            Text(
                text = "Fetching your details and preferences...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
private fun ErrorContent(errorMessage: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Error icon with background
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error title
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Error message
            Text(
                text = errorMessage,
                maxLines = 5,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Secondary action - could be "Skip" or "Help"
                OutlinedButton(
                    onClick = { /* Could implement help/support action */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                ) {
                    Text(
                        text = "Get Help",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Primary action - Retry
                Button(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Try Again",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Helpful tip
            Text(
                text = "💡 Tip: Check your internet connection and try again",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF7F2)
@Composable
fun UserDetailsLayoutLoadingPreview() {
    MaterialTheme {
        UserDetailsLayout(
            isLoading = true,
            errorMessage = null,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true,)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserDetailsLayoutErrorPreview() {
    JagratiAndroidTheme {
        UserDetailsLayout(
            isLoading = false,
            errorMessage = "Failed to connect to server. Please check your internet connection and try again.",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true,)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserDetailsLayoutDarkPreview() {
    JagratiAndroidTheme {
        UserDetailsLayout(
            isLoading = true,
            errorMessage = null,
            onRetry = {}
        )
    }
}