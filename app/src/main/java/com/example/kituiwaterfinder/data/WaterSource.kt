package com.example.kituiwaterfinder.data

/**
 * WaterSource data class
 *
 * This represents a single water source (borehole, kiosk, or dam) in our app.
 * Each water source has these properties:
 * - id: Unique identifier from Firebase
 * - name: Name of the water source (e.g., "Kilungu Borehole")
 * - type: Type of source ("Borehole", "Kiosk", or "Dam")
 * - location: Where it's located (e.g., "Near Kilungu Market")
 * - status: Current status ("Available" or "Not Available")
 * - lastUpdated: When the status was last updated (timestamp in milliseconds)
 */
data class WaterSource(
    val id: String = "",           // Default empty string if not provided
    val name: String = "",
    val type: String = "",
    val location: String = "",
    val status: String = "",
    val lastUpdated: Long = 0      // 0 if not provided
)