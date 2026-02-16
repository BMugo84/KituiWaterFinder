package com.example.kituiwaterfinder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kituiwaterfinder.data.FirebaseRepository
import com.example.kituiwaterfinder.ui.theme.KituiWaterFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create repository instance
        val repository = FirebaseRepository()

        setContent {
            KituiWaterFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var testResult by remember { mutableStateOf("Testing Firebase...") }

                    // Test Firebase connection when app starts
                    LaunchedEffect(Unit) {
                        repository.getWaterSources(
                            onSuccess = { waterSources ->
                                testResult = "Success! Found ${waterSources.size} water sources:\n\n" +
                                        waterSources.joinToString("\n") { it.name }
                            },
                            onFailure = { error ->
                                testResult = "Error: $error"
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Firebase Connection Test",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = testResult)
                    }
                }
            }
        }
    }
}