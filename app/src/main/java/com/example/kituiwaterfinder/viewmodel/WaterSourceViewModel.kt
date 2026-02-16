package com.example.kituiwaterfinder.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kituiwaterfinder.data.FirebaseRepository
import com.example.kituiwaterfinder.data.Report
import com.example.kituiwaterfinder.data.WaterSource

/**
 * WaterSourceViewModel
 *
 * This ViewModel manages all the data for our app.
 * It holds:
 * - The list of water sources
 * - Loading states (to show loading indicators)
 * - Error messages
 *
 * It also provides functions to:
 * - Load water sources from Firebase
 * - Submit reports
 * - Refresh data
 */
class WaterSourceViewModel : ViewModel() {

    // Create an instance of our repository to talk to Firebase
    private val repository = FirebaseRepository()

    private val TAG = "WaterSourceViewModel"

    // ===== STATE VARIABLES =====
    // These variables hold data that the UI will display
    // When these change, the UI automatically updates

    /**
     * List of all water sources
     * The UI will display this list
     */
    var waterSources by mutableStateOf<List<WaterSource>>(emptyList())
        private set  // Only this ViewModel can change it

    /**
     * Is the app currently loading data?
     * Used to show/hide loading indicators
     */
    var isLoading by mutableStateOf(false)
        private set

    /**
     * Is the app currently submitting a report?
     */
    var isSubmittingReport by mutableStateOf(false)
        private set

    /**
     * Error message to display to user
     * Empty string means no error
     */
    var errorMessage by mutableStateOf("")
        private set

    /**
     * Success message after submitting a report
     */
    var successMessage by mutableStateOf("")
        private set

    // ===== INITIALIZATION =====
    // This runs as soon as the ViewModel is created
    init {
        Log.d(TAG, "ViewModel initialized")
        loadWaterSources()  // Load data immediately
    }

    // ===== FUNCTIONS =====

    /**
     * Load all water sources from Firebase
     * This is called when the app starts and when user refreshes
     */
    fun loadWaterSources() {
        Log.d(TAG, "Loading water sources...")

        // Clear any previous error messages
        errorMessage = ""

        // Show loading indicator
        isLoading = true

        // Ask the repository to get data from Firebase
        repository.getWaterSources(
            onSuccess = { sources ->
                // This runs when data is successfully loaded
                Log.d(TAG, "Loaded ${sources.size} water sources")

                waterSources = sources  // Update the list
                isLoading = false       // Hide loading indicator
            },
            onFailure = { error ->
                // This runs if something went wrong
                Log.e(TAG, "Failed to load water sources: $error")

                errorMessage = "Failed to load water sources. Please check your internet connection."
                isLoading = false
            }
        )
    }

    /**
     * Submit a report about a water source
     *
     * @param report: The Report to submit
     * @param onComplete: Function to call when done (for navigation)
     */
    fun submitReport(report: Report, onComplete: () -> Unit) {
        Log.d(TAG, "Submitting report for ${report.sourceName}")

        // Clear previous messages
        errorMessage = ""
        successMessage = ""

        // Show submitting indicator
        isSubmittingReport = true

        // Ask repository to save the report
        repository.submitReport(
            report = report,
            onSuccess = {
                // Report saved successfully
                Log.d(TAG, "Report submitted successfully")

                isSubmittingReport = false
                successMessage = "Report submitted successfully!"

                // Call the completion function (usually navigates back)
                onComplete()
            },
            onFailure = { error ->
                // Something went wrong
                Log.e(TAG, "Failed to submit report: $error")

                isSubmittingReport = false
                errorMessage = "Failed to submit report. Please try again."
            }
        )
    }

    /**
     * Clear error message
     * Called when user dismisses an error dialog
     */
    fun clearErrorMessage() {
        errorMessage = ""
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        successMessage = ""
    }

    /**
     * Refresh the water sources list
     * Called when user pulls to refresh
     */
    fun refresh() {
        Log.d(TAG, "Refreshing water sources")
        loadWaterSources()
    }

    /**
     * Get a specific water source by ID
     * Used when navigating to detail screen
     *
     * @param id: The ID of the water source to find
     * @return: The WaterSource or null if not found
     */
    fun getWaterSourceById(id: String): WaterSource? {
        return waterSources.find { it.id == id }
    }
}