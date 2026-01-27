package com.hexagraph.jagrati_android.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.ui.theme.CustomColors
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import java.util.Calendar
import androidx.core.net.toUri

// Home Dashboard Screen

@Composable
fun HomeContentScreen(
    onOpenDrawer: () -> Unit,
    onSearchClick: () -> Unit,
    userName: String = "User",
    notificationCount: Int = 3,
    onTakeAttendanceClick: () -> Unit,
    onRegisterStudentClick: () -> Unit,
    onSearchByCameraClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    hasStudentRegistrationPermission: Boolean = false,
    hasAttendancePermission: Boolean = false
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with greeting, notifications, and drawer
        item {
            HomeHeader(
                userName = userName,
                notificationCount = notificationCount,
                onOpenDrawer = onOpenDrawer,
                onNotificationClick = onNotificationClick
            )
        }

        // Take Attendance Card - Only show if user has permission
        if (hasAttendancePermission) {
            item {
                TakeAttendanceCard(
                    onClick = onTakeAttendanceClick
                )
            }
        }


//
//        // Register New Student Card - Only show if user has permission
//        if (hasStudentRegistrationPermission) {
//            item {
//                RegisterStudentCard(
//                    onClick = onRegisterStudentClick
//                )
//            }
//        }


        item {
            RegisterAndSyllabusRow(
                onSyllabusOpenClick = {
                    openLink(context, "https://drive.google.com/drive/folders/1_dREOlponixBajt9STv3lAilqmb5ab-h?usp=sharing")
                },
                onRegisterStudentClick = onRegisterStudentClick,
                registerPermissionExists = hasStudentRegistrationPermission
            )
        }

        item {
            SearchOptions(
                onSearchByCamera = onSearchByCameraClick,
                onSearchByName = onSearchClick
            )
        }


//        // Quick Stats (Optional)
//        item {
//            Spacer(modifier = Modifier.height(8.dp))
//            QuickStatsSection()
//        }
    }
}


fun openLink(context: Context, url: String){
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

@Composable
fun HomeHeader(
    userName: String,
    notificationCount: Int,
    onOpenDrawer: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: Menu and Notifications
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable{ onOpenDrawer()}
                    .dropShadow(
                        shape = RoundedCornerShape(12.dp),
                        shadow = Shadow(
                            radius = 8.dp,
                            spread = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(0.1f),
                            offset = DpOffset(x = 1.dp, 1.dp)
                        )
                    )
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            // Greeting
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getGreeting(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Box(
                modifier = Modifier
                    .clickable{ onNotificationClick()}
                    .dropShadow(
                        shape = RoundedCornerShape(12.dp),
                        shadow = Shadow(
                            radius = 6.dp,
                            spread = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(0.1f),
                            offset = DpOffset(x = 1.dp, 1.dp)
                        )
                    )
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(6.dp)
            )  {
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge {
                            Text(notificationCount.toString())
                        }
                    }
                }
            ) {
                    Icon(
                        modifier = Modifier.padding(4.dp),
                        painter = painterResource(R.drawable.ic_notification),
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }


    }
}

@Composable
fun TakeAttendanceCard(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Take Attendance",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Using your camera!!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            // QR Scanner Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qr_code),
                    contentDescription = "QR Scanner",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RegisterStudentCard(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Found someone new?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Register them as a student",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_person_add),
                    contentDescription = "Add Student",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SearchOptions(
    onSearchByCamera: () -> Unit,
    onSearchByName: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchOptionCardWithDrawable(
            title = "Search by Camera",
            iconRes = R.drawable.ic_camera,
            containerColor = CustomColors.BatchBrown.copy(0.2f),
            iconColor = CustomColors.BatchBrown,
            onClick = onSearchByCamera,
            modifier = Modifier.weight(1f)
        )

        SearchOptionCardWithVector(
            title = "Search by Name",
            icon = Icons.Default.Search,
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            iconColor = MaterialTheme.colorScheme.secondary,
            onClick = onSearchByName,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun RegisterAndSyllabusRow(
    onSyllabusOpenClick: () -> Unit,
    registerPermissionExists: Boolean,
    onRegisterStudentClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchOptionCardWithDrawable(
            title = "Open Course Material",
            iconRes = R.drawable.ic_book,
            containerColor = CustomColors.BatchTurquoise.copy(0.2f),
            iconColor = CustomColors.BatchTurquoise,
            onClick = onSyllabusOpenClick,
            modifier = Modifier.weight(1f)
        )
        if(registerPermissionExists) {
            SearchOptionCardWithDrawable(
                title = "Register New Student",
                iconRes = R.drawable.ic_person_add,
                containerColor = CustomColors.BatchYellow.copy(0.2f),
                iconColor = CustomColors.BatchYellow,
                onClick = onRegisterStudentClick,
                modifier = Modifier.weight(1f)
            )
        }

    }
}


@Composable
fun SearchOptionCardWithDrawable(
    title: String,
    iconRes: Int,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(2.dp, iconColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = iconColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SearchOptionCardWithVector(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(2.dp, iconColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = iconColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Remove or keep the old SearchOptionCard for compatibility
@Composable
fun SearchOptionCard(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchOptionCardWithVector(
        title = title,
        icon = icon,
        containerColor = containerColor,
        iconColor = iconColor,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun QuickStatsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatItem(
                    label = "Students",
                    value = "127",
                    modifier = Modifier.weight(1f)
                )
                QuickStatItem(
                    label = "Volunteers",
                    value = "24",
                    modifier = Modifier.weight(1f)
                )
                QuickStatItem(
                    label = "Groups",
                    value = "8",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun getGreeting(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}


@Preview(showBackground = true)
@Composable
fun HomeContentScreenPreview() {
    JagratiAndroidTheme {
        HomeContentScreen(
            onOpenDrawer = {},
            onSearchClick = {},
            userName = "Hemang",
            onTakeAttendanceClick = {},
            onRegisterStudentClick = {},
            onSearchByCameraClick = {})
    }
}
