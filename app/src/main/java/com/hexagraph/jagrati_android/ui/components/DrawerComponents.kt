package com.hexagraph.jagrati_android.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import com.hexagraph.jagrati_android.ui.theme.getBatchColors

/**
 * Navigation drawer header with user profile - Polished modern design with improved hierarchy.
 *
 * @param userName User's name
 * @param userEmail User's email address
 * @param profileImageUrl Optional profile image URL
 * @param modifier Modifier for styling
 */
@Composable
fun DrawerHeader(
    userName: String,
    userEmail: String?,
    profileImageUrl: String?,
    modifier: Modifier = Modifier
) {
    val batchColors = getBatchColors()

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Profile card with improved contrast and spacing
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Larger avatar with soft background color
                        Box(
                            modifier = Modifier.size(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Soft background circle
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(batchColors[0].copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                ProfileAvatar(
                                    userName = userName,
                                    profileImageUrl = profileImageUrl,
                                    size = 64.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // User info with improved typography
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            userEmail?.let { email ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Status badge - smaller and right-aligned
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = batchColors[0].copy(alpha = 0.12f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Active",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = batchColors[0],
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * Section header for grouping drawer items.
 *
 * @param title Section title
 * @param modifier Modifier for styling
 */
@Composable
fun DrawerSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp
    )
}

/**
 * Navigation drawer item component with improved visual hierarchy and consistent spacing.
 *
 * @param label Item label text
 * @param icon Drawable resource ID for the icon
 * @param iconTint Icon tint color
 * @param onClick Click callback
 * @param modifier Modifier for styling
 * @param colorIndex Index for batch color selection (0-4)
 */
@Composable
fun DrawerItem(
    label: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    colorIndex: Int = 0
) {
    val batchColors = getBatchColors()
    val iconColor = if (iconTint == MaterialTheme.colorScheme.onSurface) {
        batchColors[colorIndex % batchColors.size]
    } else {
        iconTint
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with batch color background - filled variant style
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconColor.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Navigation drawer divider with improved spacing.
 */
@Composable
fun DrawerDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DrawerHeaderPreview() {
    JagratiAndroidTheme {
        DrawerHeader(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            profileImageUrl = null
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DrawerItemPreview() {
    JagratiAndroidTheme {
        Column(
            modifier = Modifier.background(Color(0xFFF7F8FA))
        ) {
            DrawerSectionHeader(title = "ADMIN CONTROLS")
            DrawerItem(
                label = "Management",
                icon = R.drawable.ic_management,
                onClick = {},
                colorIndex = 0
            )
            DrawerItem(
                label = "Settings",
                icon = R.drawable.ic_settings,
                onClick = {},
                colorIndex = 1
            )

            DrawerDivider()

            DrawerSectionHeader(title = "ATTENDANCE & REGISTRATION")
            DrawerItem(
                label = "Take Volunteer Attendance",
                icon = R.drawable.ic_person,
                onClick = {},
                colorIndex = 2
            )
            DrawerItem(
                label = "Take Student Attendance",
                icon = R.drawable.ic_person,
                onClick = {},
                colorIndex = 3
            )
            DrawerItem(
                label = "Register Student",
                icon = R.drawable.ic_notifications,
                onClick = {},
                colorIndex = 4
            )

            DrawerDivider()

            DrawerItem(
                label = "Log out",
                icon = R.drawable.ic_logout,
                onClick = {},
                iconTint = MaterialTheme.colorScheme.error,
                colorIndex = 0
            )
        }
    }
}
