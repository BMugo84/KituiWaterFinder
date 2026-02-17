package com.example.kituiwaterfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kituiwaterfinder.data.WaterSource
import java.text.SimpleDateFormat
import java.util.*

/**
 * WaterSourceDetailScreen
 *
 * Shows detailed information about a selected water source.
 * Displays: name, type, location, status, and last updated time.
 * Has a button to navigate to the report screen.
 *
 * @param waterSource: The water source to display
 * @param onBackClick: Function called when back button is pressed
 * @param onReportClick: Function called when "Report Issue" button is pressed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterSourceDetailScreen(
    waterSource: WaterSource,
    onBackClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Source Details") },
                navigationIcon = {
                    // Back button
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
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
                .padding(24.dp)
        ) {
            // Water source name
            Text(
                text = waterSource.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Type
            DetailRow(
                label = "Type:",
                value = waterSource.type
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            DetailRow(
                label = "Location:",
                value = waterSource.location
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status with colored indicator
            Row {
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(120.dp)
                )

                Row {
                    // Colored circle indicator
                    Surface(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 4.dp),
                        shape = MaterialTheme.shapes.small,
                        color = if (waterSource.status == "Available")
                            Color(0xFF4CAF50)  // Green
                        else
                            Color(0xFFF44336)  // Red
                    ) {}

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = waterSource.status,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (waterSource.status == "Available")
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Last updated
            DetailRow(
                label = "Last Updated:",
                value = formatTimestamp(waterSource.lastUpdated)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Report Issue button
            Button(
                onClick = onReportClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Report an Issue",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * DetailRow
 *
 * A reusable component for displaying a label-value pair
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * Format timestamp to readable date/time
 * Converts milliseconds to "Feb 16, 2026 9:30 AM" format
 */
private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "Unknown"

    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
    return formatter.format(date)
}