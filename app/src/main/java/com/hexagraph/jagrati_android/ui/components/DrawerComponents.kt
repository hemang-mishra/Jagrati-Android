package com.hexagraph.jagrati_android.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
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
 * Navigation drawer header with user profile - Modern asymmetric design.
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
        // Main content card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profile card with modern layout
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with colorful gradient border
                    Box(
                        modifier = Modifier.size(80.dp)
                    ) {
                        // Gradient border effect
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = Color.Transparent
                        ) {
                                ProfileAvatar(
                                    userName = userName,
                                    profileImageUrl = profileImageUrl,
                                    size = 74.dp
                                )
                            }

                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // User info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
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
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = batchColors[0].copy(alpha = 0.15f),
                            modifier = Modifier
                        ) {
                            Text(
                                text = "Active",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = batchColors[0],
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick stats or info chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Info chip 1
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = batchColors[1].copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = null,
                            tint = batchColors[1],
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.labelSmall,
                            color = batchColors[1],
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Info chip 2
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = batchColors[3].copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = null,
                            tint = batchColors[3],
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Updates",
                            style = MaterialTheme.typography.labelSmall,
                            color = batchColors[3],
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Navigation drawer item component.
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
            .padding(horizontal = 8.dp, vertical = 1.dp),
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with batch color background
            Surface(
                shape = MaterialTheme.shapes.small,
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Navigation drawer divider.
 */
@Composable
fun DrawerDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
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
@Composable
fun DrawerItemPreview() {
    JagratiAndroidTheme {
        Column {
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
            DrawerItem(
                label = "Log out",
                icon = R.drawable.ic_logout,
                onClick = {},
                iconTint = MaterialTheme.colorScheme.error,
                colorIndex = 2
            )
        }
    }
}
