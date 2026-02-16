package com.example.kituiwaterfinder.data

/**
 * Report data class
 *
 * This represents a user's report about a water source.
 * When someone reports an issue, we create one of these and save it to Firebase.
 *
 * Properties:
 * - id: Unique identifier from Firebase
 * - sourceName: Name of the water source being reported
 * - issue: Description of the problem (e.g., "No water available")
 * - timestamp: When the report was submitted
 */
data class Report(
    val id: String = "",
    val sourceName: String = "",
    val issue: String = "",
    val timestamp: Long = System.currentTimeMillis()  // Current time by default
)