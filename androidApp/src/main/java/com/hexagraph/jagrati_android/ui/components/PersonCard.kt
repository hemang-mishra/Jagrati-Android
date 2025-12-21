package com.hexagraph.jagrati_android.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.model.Student
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme

data class PersonCardData(
    val title: String,
    val subtitle: String,
    val extra: String,
    val profileImageUrl: String?
)

fun Student.toPersonCardData(villageName: String, groupName: String): PersonCardData {
    return PersonCardData(
        title = "$firstName $lastName",
        subtitle = villageName,
        extra = groupName,
        profileImageUrl = profilePic?.url
    )
}

fun Volunteer.toPersonCardData(): PersonCardData {
    return PersonCardData(
        title = "$firstName $lastName",
        subtitle = rollNumber ?: "N/A",
        extra = batch ?: "N/A",
        profileImageUrl = profilePic?.url
    )
}

@Composable
fun PersonCard(
    data: PersonCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                userName = data.title,
                profileImageUrl = data.profileImageUrl,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = data.extra,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PersonCardPreview() {
    JagratiAndroidTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PersonCard(
                data = PersonCardData(
                    title = "Rahul Kumar Singh",
                    subtitle = "Bargi Village",
                    extra = "Group A",
                    profileImageUrl = null
                ),
                onClick = {}
            )

            PersonCard(
                data = PersonCardData(
                    title = "Priya Sharma",
                    subtitle = "2021BCS001",
                    extra = "Batch 2025",
                    profileImageUrl = null
                ),
                onClick = {}
            )
        }
    }
}

