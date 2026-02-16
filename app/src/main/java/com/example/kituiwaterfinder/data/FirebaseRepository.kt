package com.example.kituiwaterfinder.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FirebaseRepository"

    fun getWaterSources(
        onSuccess: (List<WaterSource>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("waterSources")
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val waterSources = mutableListOf<WaterSource>()

                for (document in documents) {
                    try {
                        // Safely get lastUpdated - handle both number and timestamp
                        val lastUpdated = when {
                            // Try to get as Long (number)
                            document.get("lastUpdated") is Long ->
                                document.getLong("lastUpdated") ?: 0L

                            // Try to get as Timestamp and convert
                            document.get("lastUpdated") is com.google.firebase.Timestamp -> {
                                val timestamp = document.getTimestamp("lastUpdated")
                                timestamp?.toDate()?.time ?: 0L
                            }

                            // If it's something else or null, use 0
                            else -> 0L
                        }

                        val waterSource = WaterSource(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            type = document.getString("type") ?: "",
                            location = document.getString("location") ?: "",
                            status = document.getString("status") ?: "",
                            lastUpdated = lastUpdated
                        )
                        waterSources.add(waterSource)

                    } catch (e: Exception) {
                        // If one document fails, log it but continue with others
                        Log.e(TAG, "Error parsing document ${document.id}: ${e.message}")
                    }
                }

                Log.d(TAG, "Successfully loaded ${waterSources.size} water sources")
                onSuccess(waterSources)
            }
            .addOnFailureListener { exception ->
                val errorMessage = "Error loading water sources: ${exception.message}"
                Log.e(TAG, errorMessage)
                onFailure(errorMessage)
            }
    }

    fun submitReport(
        report: Report,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val reportData = hashMapOf(
            "sourceName" to report.sourceName,
            "issue" to report.issue,
            "timestamp" to report.timestamp
        )

        db.collection("reports")
            .add(reportData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Report submitted with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                val errorMessage = "Error submitting report: ${exception.message}"
                Log.e(TAG, errorMessage)
                onFailure(errorMessage)
            }
    }
}
