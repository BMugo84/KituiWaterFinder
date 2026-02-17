package com.example.kituiwaterfinder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kituiwaterfinder.data.WaterSource

/**
 * WaterSourceCard
 *
 * A reusable card component that displays information about a water source.
 * Shows: name, type, location, and status with a color indicator.
 *
 * @param waterSource: The water source to display
 * @param onClick: Function to call when the card is tapped
 */
@Composable
fun WaterSourceCard(
    waterSource: WaterSource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },  // Make the card tappable
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status indicator (colored circle)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(top = 6.dp)
            ) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = MaterialTheme.shapes.small,
                    color = if (waterSource.status == "Available")
                        Color(0xFF4CAF50)  // Green
                    else
                        Color(0xFFF44336)  // Red
                ) {}
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Water source information
            Column(modifier = Modifier.weight(1f)) {
                // Name
                Text(
                    text = waterSource.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Type and Status
                Text(
                    text = "${waterSource.type} • ${waterSource.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Location
                Text(
                    text = waterSource.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}