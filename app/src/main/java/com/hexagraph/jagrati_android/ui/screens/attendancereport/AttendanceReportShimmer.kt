package com.hexagraph.jagrati_android.ui.screens.attendancereport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.ui.components.ShimmerBox
import com.hexagraph.jagrati_android.ui.components.ShimmerCircle
import com.hexagraph.jagrati_android.ui.components.ShimmerText

@Composable
fun AttendanceReportContentShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary cards shimmer
        SummaryCardsShimmer()

        // Stats section shimmer
        repeat(3) {
            StatsCardShimmer()
        }

        // Section header shimmer
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeaderShimmer()

        // Person cards shimmer (5 volunteers)
        repeat(5) {
            PersonCardShimmer()
        }

        // Another section header
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeaderShimmer()

        // More person cards (5 students)
        repeat(5) {
            PersonCardShimmer()
        }
    }
}

@Composable
fun AttendanceReportShimmerLoading() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header shimmer
        item {
            DateNavigationShimmer()
        }

        // Summary cards shimmer
        item {
            SummaryCardsShimmer()
        }

        // Stats section shimmer
        item {
            StatsShimmer()
        }

        // Section header shimmer
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeaderShimmer()
        }

        // Person cards shimmer
        items(5) {
            PersonCardShimmer()
        }

        // Another section header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeaderShimmer()
        }

        // More person cards
        items(5) {
            PersonCardShimmer()
        }
    }
}

@Composable
fun DateNavigationShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ShimmerText(
                    modifier = Modifier.width(180.dp),
                    height = 24.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerText(
                    modifier = Modifier.width(220.dp),
                    height = 18.dp
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ShimmerCircle(size = 40.dp)
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerCircle(size = 40.dp)
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                ShimmerCircle(size = 40.dp)
            }
        }
    }
}

@Composable
fun SummaryCardsShimmer() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(2) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerCircle(size = 48.dp)
                    ShimmerText(
                        modifier = Modifier.width(50.dp),
                        height = 28.dp
                    )
                    ShimmerText(
                        modifier = Modifier.width(70.dp),
                        height = 14.dp
                    )
                }
            }
        }
    }
}

@Composable
fun StatsShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            StatsCardShimmer()
        }
    }
}

@Composable
fun StatsCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerText(
                modifier = Modifier.width(120.dp),
                height = 16.dp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(28.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeaderShimmer() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerText(
            modifier = Modifier.width(120.dp),
            height = 24.dp
        )
        ShimmerCircle(size = 36.dp)
    }
}

@Composable
fun PersonCardShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerCircle(size = 48.dp)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    height = 18.dp
                )
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    height = 14.dp
                )
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.3f),
                    height = 12.dp
                )
            }

            ShimmerCircle(size = 24.dp)
        }
    }
}
