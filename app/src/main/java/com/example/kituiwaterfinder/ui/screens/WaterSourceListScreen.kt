package com.example.kituiwaterfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kituiwaterfinder.data.WaterSource
import com.example.kituiwaterfinder.ui.components.WaterSourceCard
import com.example.kituiwaterfinder.viewmodel.WaterSourceViewModel

/**
 * WaterSourceListScreen
 *
 * The main screen of the app.
 * Displays a scrollable list of all water sources.
 * Users can tap on any water source to see more details.
 *
 * @param viewModel: The ViewModel that provides water source data
 * @param onItemClick: Function called when a water source is tapped
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterSourceListScreen(
    viewModel: WaterSourceViewModel,
    onItemClick: (WaterSource) -> Unit
) {
    Scaffold(
        topBar = {
            // App bar at the top
            TopAppBar(
                title = {
                    Text(
                        text = "Kitui South Water Finder",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Show loading message
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading water sources...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Show error message if any
            if (viewModel.errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = viewModel.errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Show the list of water sources
            if (!viewModel.isLoading && viewModel.errorMessage.isEmpty()) {
                // Header showing count
                Text(
                    text = "${viewModel.waterSources.size} Water Sources Available",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Scrollable list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.waterSources) { waterSource ->
                        WaterSourceCard(
                            waterSource = waterSource,
                            onClick = { onItemClick(waterSource) }
                        )
                    }
                }
            }

            // Show empty state if no water sources
            if (!viewModel.isLoading &&
                viewModel.errorMessage.isEmpty() &&
                viewModel.waterSources.isEmpty()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No water sources found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}