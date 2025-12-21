package com.hexagraph.jagrati_android.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
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
 * Revamped navigation drawer header with clean card design.
 */
@Composable
fun DrawerHeader(
    userName: String,
    userEmail: String?,
    profileImageUrl: String?,
    modifier: Modifier = Modifier
) {
    val batchColors = getBatchColors()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Clean profile card with subtle elevation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large centered avatar with gradient ring
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer gradient ring
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        batchColors[0],
                                        batchColors[1]
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner ring for spacing
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            ProfileAvatar(
                                userName = userName,
                                profileImageUrl = profileImageUrl,
                                size = 78.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User name - large and bold
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Email - subtle
                userEmail?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Active status badge - elegant
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = batchColors[0].copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(batchColors[0])
                        )
                        Text(
                            text = "Active Volunteer",
                            style = MaterialTheme.typography.labelMedium,
                            color = batchColors[0],
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * Modern section header with accent line.
 */
@Composable
fun DrawerSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Accent line
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Redesigned drawer item with modern card-based layout.
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
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container with gradient background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                iconColor.copy(alpha = 0.15f),
                                iconColor.copy(alpha = 0.08f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // Subtle chevron indicator
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Minimal, elegant divider.
 */
@Composable
fun DrawerDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
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
            userName = "Rajesh Kumar",
            userEmail = "rajesh.kumar@iiitdmj.ac.in",
            profileImageUrl = null
        )
    }
}

@Preview(showBackground = true, widthDp = 300)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 300)
@Composable
fun DrawerItemsPreview() {
    JagratiAndroidTheme {
        Column(
            modifier = Modifier
                .width(300.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            DrawerSectionHeader(title = "ADMIN CONTROLS")

            DrawerItem(
                label = "Management",
                icon = R.drawable.ic_management_rounded,
                onClick = {},
                colorIndex = 0
            )

            DrawerItem(
                label = "Settings",
                icon = R.drawable.ic_settings_rounded,
                onClick = {},
                colorIndex = 1
            )

            DrawerDivider()

            DrawerSectionHeader(title = "LISTS")

            DrawerItem(
                label = "Student List",
                icon = R.drawable.ic_person_rounded,
                onClick = {},
                colorIndex = 2
            )

            DrawerItem(
                label = "Volunteer List",
                icon = R.drawable.ic_person_rounded,
                onClick = {},
                colorIndex = 3
            )

            DrawerDivider()

            DrawerItem(
                label = "Log out",
                icon = R.drawable.ic_logout_rounded,
                onClick = {},
                iconTint = MaterialTheme.colorScheme.error,
                colorIndex = 0
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 800)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 300, heightDp = 800)
@Composable
fun FullDrawerPreview() {
    JagratiAndroidTheme {
        Column(
            modifier = Modifier
                .width(300.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            DrawerHeader(
                userName = "Rajesh Kumar",
                userEmail = "rajesh.kumar@iiitdmj.ac.in",
                profileImageUrl = null
            )

            DrawerDivider()

            DrawerSectionHeader(title = "ADMIN CONTROLS")

            DrawerItem(
                label = "Management",
                icon = R.drawable.ic_management_rounded,
                onClick = {},
                colorIndex = 0
            )

            DrawerItem(
                label = "Settings",
                icon = R.drawable.ic_settings_rounded,
                onClick = {},
                colorIndex = 1
            )

            DrawerDivider()

            DrawerSectionHeader(title = "LISTS")

            DrawerItem(
                label = "Student List",
                icon = R.drawable.ic_person_rounded,
                onClick = {},
                colorIndex = 2
            )

            DrawerItem(
                label = "Volunteer List",
                icon = R.drawable.ic_person_rounded,
                onClick = {},
                colorIndex = 3
            )

            DrawerDivider()

            DrawerSectionHeader(title = "ATTENDANCE")

            DrawerItem(
                label = "Take Attendance",
                icon = R.drawable.ic_attendance_rounded,
                onClick = {},
                colorIndex = 3
            )

            DrawerItem(
                label = "Register Student",
                icon = R.drawable.ic_person_add_rounded,
                onClick = {},
                colorIndex = 4
            )

            DrawerDivider()

            DrawerItem(
                label = "Log out",
                icon = R.drawable.ic_logout_rounded,
                onClick = {},
                iconTint = MaterialTheme.colorScheme.error,
                colorIndex = 0
            )
        }
    }
}
