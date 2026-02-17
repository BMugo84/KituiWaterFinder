package com.example.kituiwaterfinder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kituiwaterfinder.data.Report
import com.example.kituiwaterfinder.viewmodel.WaterSourceViewModel

/**
 * ReportScreen
 *
 * Allows users to submit a report about a water source.
 * Contains a text field for describing the issue and a submit button.
 *
 * @param waterSourceName: Name of the water source being reported
 * @param viewModel: The ViewModel to submit the report
 * @param onBackClick: Function called when back button is pressed
 * @param onReportSubmitted: Function called after report is successfully submitted
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    waterSourceName: String,
    viewModel: WaterSourceViewModel,
    onBackClick: () -> Unit,
    onReportSubmitted: () -> Unit
) {
    // Local state for the text field
    var issueText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report an Issue") },
                navigationIcon = {
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
            // Title
            Text(
                text = "Report Issue for:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Water source name (read-only)
            Text(
                text = waterSourceName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Issue description label
            Text(
                text = "Describe the issue:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text field for issue description
            OutlinedTextField(
                value = issueText,
                onValueChange = { issueText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = {
                    Text("Example: No water available, pump not working, etc.")
                },
                maxLines = 8
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = {
                    // Create a report object
                    val report = Report(
                        sourceName = waterSourceName,
                        issue = issueText,
                        timestamp = System.currentTimeMillis()
                    )

                    // Submit the report
                    viewModel.submitReport(report) {
                        // This runs after successful submission
                        onReportSubmitted()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = issueText.isNotBlank() && !viewModel.isSubmittingReport
            ) {
                if (viewModel.isSubmittingReport) {
                    Text("Submitting...")
                } else {
                    Text(
                        text = "Submit Report",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Show error if any
            if (viewModel.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
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
        }
    }
}