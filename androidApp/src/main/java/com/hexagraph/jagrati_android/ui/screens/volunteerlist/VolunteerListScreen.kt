package com.hexagraph.jagrati_android.ui.screens.volunteerlist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hexagraph.jagrati_android.R
import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.ui.components.PersonCard
import com.hexagraph.jagrati_android.ui.components.toPersonCardData
import com.hexagraph.jagrati_android.ui.theme.JagratiAndroidTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun VolunteerListScreen(
    onBackPress: () -> Unit,
    onVolunteerClick: (String) -> Unit,
    onSearchClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: VolunteerListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVolunteers()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    VolunteerListScreenLayout(
        volunteers = uiState.volunteers,
        allBatches = uiState.allBatches,
        selectedBatch = uiState.selectedBatch,
        isLoading = uiState.isLoading,
        onBatchSelected = viewModel::filterByBatch,
        onBackPress = onBackPress,
        onVolunteerClick = onVolunteerClick,
        onSearchClick = onSearchClick,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerListScreenLayout(
    volunteers: List<Volunteer>,
    allBatches: List<String>,
    selectedBatch: String?,
    isLoading: Boolean,
    onBatchSelected: (String?) -> Unit,
    onBackPress: () -> Unit,
    onVolunteerClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var showBatchFilter by remember { mutableStateOf(false) }
    val listState= rememberLazyListState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Volunteers",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    FilterChip(
                        selected = selectedBatch != null,
                        onClick = { showBatchFilter = !showBatchFilter },
                        label = {
                            Text(selectedBatch ?: "All Batches")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_filter_list_24),
                                contentDescription = "Filter by batch"
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = showBatchFilter,
                        onDismissRequest = { showBatchFilter = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Batches") },
                            onClick = {
                                onBatchSelected(null)
                                showBatchFilter = false
                            }
                        )
                        allBatches.forEach { batch ->
                            DropdownMenuItem(
                                text = { Text(batch) },
                                onClick = {
                                    onBatchSelected(batch)
                                    showBatchFilter = false
                                }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (volunteers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No volunteers found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(volunteers, key = { it.pid }) { volunteer ->
                        PersonCard(
                            data = volunteer.toPersonCardData(),
                            onClick = { onVolunteerClick(volunteer.pid) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VolunteerListScreenPreview() {
    JagratiAndroidTheme {
        VolunteerListScreenLayout(
            volunteers = listOf(
                Volunteer(
                    pid = "1",
                    firstName = "Amit",
                    lastName = "Kumar",
                    rollNumber = "2021BCS001",
                    gender = "Male",
                    batch = "2025",
                    dateOfBirth = "2003-01-01"
                ),
                Volunteer(
                    pid = "2",
                    firstName = "Sneha",
                    lastName = "Patel",
                    rollNumber = "2022BCS045",
                    gender = "Female",
                    batch = "2024",
                    dateOfBirth = "2004-05-15"
                ),
                Volunteer(
                    pid = "3",
                    firstName = "Rahul",
                    lastName = "Verma",
                    rollNumber = "2020BCS023",
                    gender = "Male",
                    batch = "2026",
                    dateOfBirth = "2002-11-20"
                )
            ),
            allBatches = listOf("2026", "2025", "2024"),
            selectedBatch = null,
            isLoading = false,
            onBatchSelected = {},
            onBackPress = {},
            onVolunteerClick = {},
            onSearchClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
