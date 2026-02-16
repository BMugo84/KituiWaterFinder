package com.example.kituiwaterfinder.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * FirebaseRepository
 *
 * This class handles all communication with Firebase Firestore.
 * It provides functions to:
 * - Get all water sources from the database
 * - Submit reports about water sources
 *
 * Think of this as the "middleman" between our app and Firebase.
 */
class FirebaseRepository {

    // Get reference to Firebase Firestore database
    private val db = FirebaseFirestore.getInstance()

    // Tag for logging (helps us debug if something goes wrong)
    private val TAG = "FirebaseRepository"

    /**
     * Get all water sources from Firebase
     *
     * @param onSuccess: Function to call when data is successfully loaded
     *                   It receives a list of WaterSource objects
     * @param onFailure: Function to call if something goes wrong
     *                   It receives an error message
     */
    fun getWaterSources(
        onSuccess: (List<WaterSource>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Access the "waterSources" collection in Firestore
        db.collection("waterSources")
            .orderBy("name", Query.Direction.ASCENDING)  // Sort alphabetically by name
            .get()  // Fetch the data
            .addOnSuccessListener { documents ->
                // This runs when data is successfully fetched

                // Convert Firebase documents to WaterSource objects
                val waterSources = mutableListOf<WaterSource>()

                for (document in documents) {
                    // For each document, extract the data and create a WaterSource object
                    val waterSource = WaterSource(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        type = document.getString("type") ?: "",
                        location = document.getString("location") ?: "",
                        status = document.getString("status") ?: "",
                        lastUpdated = document.getLong("lastUpdated") ?: 0
                    )
                    waterSources.add(waterSource)
                }

                // Log success for debugging
                Log.d(TAG, "Successfully loaded ${waterSources.size} water sources")

                // Call the success function with our list
                onSuccess(waterSources)
            }
            .addOnFailureListener { exception ->
                // This runs if something goes wrong
                val errorMessage = "Error loading water sources: ${exception.message}"
                Log.e(TAG, errorMessage)

                // Call the failure function with error message
                onFailure(errorMessage)
            }
    }

    /**
     * Submit a report to Firebase
     *
     * @param report: The Report object to save
     * @param onSuccess: Function to call when report is successfully saved
     * @param onFailure: Function to call if something goes wrong
     */
    fun submitReport(
        report: Report,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Create a map of the report data
        // Firebase stores data as key-value pairs
        val reportData = hashMapOf(
            "sourceName" to report.sourceName,
            "issue" to report.issue,
            "timestamp" to report.timestamp
        )

        // Add the report to the "reports" collection
        db.collection("reports")
            .add(reportData)  // Firebase will auto-generate an ID
            .addOnSuccessListener { documentReference ->
                // This runs when report is successfully saved
                Log.d(TAG, "Report submitted with ID: ${documentReference.id}")

                // Call the success function
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // This runs if something goes wrong
                val errorMessage = "Error submitting report: ${exception.message}"
                Log.e(TAG, errorMessage)

                // Call the failure function
                onFailure(errorMessage)
            }
    }
}