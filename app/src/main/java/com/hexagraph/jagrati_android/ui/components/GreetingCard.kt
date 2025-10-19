package com.hexagraph.jagrati_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.allowHardware
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme

/**
 * Greeting card component with user profile and actions.
 *
 * @param userName User's first name
 * @param userEmail User's email address
 * @param profileImageUrl Optional profile image URL
 * @param greeting Greeting message (default: "Hello")
 * @param showSettingsButton Whether to show settings button
 * @param showSignOutButton Whether to show sign out button
 * @param onSettingsClick Callback for settings button
 * @param onSignOutClick Callback for sign out button
 * @param modifier Modifier for styling
 */
@Composable
fun GreetingCard(
    modifier: Modifier = Modifier,
    userName: String,
    userEmail: String?,
    profileImageUrl: String? = null,
    greeting: String = "Hello",
    showSettingsButton: Boolean = true,
    showSignOutButton: Boolean = true,
    onSettingsClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // User profile image or letter avatar
                        ProfileAvatar(
                            userName = userName,
                            profileImageUrl = profileImageUrl,
                            size = 64.dp
                        )

                        Spacer(modifier = Modifier.padding(horizontal = 12.dp))

                        // User details
                        Column {
                            Text(
                                text = "$greeting,",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            userEmail?.let { email ->
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row {
                        if (showSettingsButton) {
                            IconButton(onClick = onSettingsClick) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        if (showSignOutButton) {
                            IconButton(onClick = onSignOutClick) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Sign Out",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Profile avatar component that shows image or first letter.
 *
 * @param userName User's name for fallback letter
 * @param profileImageUrl Optional profile image URL
 * @param size Size of the avatar
 * @param modifier Modifier for styling
 */
@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    userName: String,
    profileImageUrl: String? = null,
    size: Dp = 60.dp
) {
    val firstLetter = userName.firstOrNull()?.uppercase() ?: "U"
    val context = LocalContext.current
    if (profileImageUrl != null && profileImageUrl.isNotBlank()) {
        val loader = ImageLoader.Builder(context)
            .components { OkHttpNetworkFetcherFactory() }
            .build()
        // Display profile image if available
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(profileImageUrl)
                .allowHardware(false)
                .build(),
            imageLoader = loader,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )
    } else {
        // Display first letter in a circle
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = firstLetter,
                fontSize = (size.value / 2.5f).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingCardPreview() {
    JagratiAndroidTheme {
        GreetingCard(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileAvatarPreview() {
    JagratiAndroidTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileAvatar(userName = "John")
            ProfileAvatar(userName = "Alice", size = 80.dp)
        }
    }
}
