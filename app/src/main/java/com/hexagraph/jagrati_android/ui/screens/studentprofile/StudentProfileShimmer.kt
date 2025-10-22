package com.hexagraph.jagrati_android.ui.screens.studentprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun StudentProfileShimmerLoading() {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Profile photo section shimmer
        item {
            ProfilePhotoSectionShimmer()
        }

        // Primary details section shimmer
        item {
            PrimaryDetailsSectionShimmer()
        }

        // Attendance summary shimmer
        item {
            AttendanceSummarySectionShimmer()
        }

        // Secondary details shimmer
        item {
            SecondaryDetailsSectionShimmer()
        }

        // Group history shimmer
        item {
            GroupHistorySectionShimmer()
        }
    }
}

@Composable
fun ProfilePhotoSectionShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerCircle(size = 120.dp)

        ShimmerText(
            modifier = Modifier.width(200.dp),
            height = 28.dp
        )

        ShimmerText(
            modifier = Modifier.width(150.dp),
            height = 18.dp
        )
    }
}

@Composable
fun PrimaryDetailsSectionShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Village and Gender cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                InfoCardShimmer(modifier = Modifier.weight(1f))
            }
        }

        // Group and Father's Name
        repeat(2) {
            InfoCardShimmer(modifier = Modifier.fillMaxWidth())
        }

        // Contact card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerCircle(size = 40.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ShimmerText(
                        modifier = Modifier.width(100.dp),
                        height = 14.dp
                    )
                    ShimmerText(
                        modifier = Modifier.width(120.dp),
                        height = 18.dp
                    )
                }
                ShimmerCircle(size = 24.dp)
            }
        }
    }
}

@Composable
fun InfoCardShimmer(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerCircle(size = 36.dp)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ShimmerText(
                    modifier = Modifier.width(60.dp),
                    height = 12.dp
                )
                ShimmerText(
                    modifier = Modifier.width(90.dp),
                    height = 18.dp
                )
            }
        }
    }
}

@Composable
fun AttendanceSummarySectionShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerCircle(size = 24.dp)
                ShimmerText(
                    modifier = Modifier.width(150.dp),
                    height = 18.dp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShimmerCircle(size = 20.dp)
                            ShimmerText(
                                modifier = Modifier.width(140.dp),
                                height = 16.dp
                            )
                        }
                        ShimmerText(
                            modifier = Modifier.width(80.dp),
                            height = 16.dp
                        )
                    }
                }
            }

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun SecondaryDetailsSectionShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerText(
                modifier = Modifier.width(180.dp),
                height = 18.dp
            )

            repeat(6) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShimmerCircle(size = 20.dp)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ShimmerText(
                            modifier = Modifier.width(100.dp),
                            height = 14.dp
                        )
                        ShimmerText(
                            modifier = Modifier.fillMaxWidth(0.7f),
                            height = 18.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                shape = RoundedCornerShape(0.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            ShimmerText(
                modifier = Modifier.width(80.dp),
                height = 16.dp
            )

            repeat(4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShimmerCircle(size = 20.dp)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ShimmerText(
                            modifier = Modifier.width(120.dp),
                            height = 14.dp
                        )
                        ShimmerText(
                            modifier = Modifier.fillMaxWidth(0.6f),
                            height = 18.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupHistorySectionShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerCircle(size = 40.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ShimmerText(
                    modifier = Modifier.width(120.dp),
                    height = 18.dp
                )
                ShimmerText(
                    modifier = Modifier.width(180.dp),
                    height = 14.dp
                )
            }
            ShimmerCircle(size = 24.dp)
        }
    }
}

