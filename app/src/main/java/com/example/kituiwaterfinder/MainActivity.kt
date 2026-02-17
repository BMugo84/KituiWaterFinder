package com.example.kituiwaterfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kituiwaterfinder.navigation.NavGraph
import com.example.kituiwaterfinder.ui.theme.KituiWaterFinderTheme
import com.example.kituiwaterfinder.viewmodel.WaterSourceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Use our custom theme instead of default MaterialTheme
            KituiWaterFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: WaterSourceViewModel = viewModel()
                    NavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
