package com.example.kituiwaterfinder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue60,
    onPrimary = SurfaceLight,
    primaryContainer = Blue40,
    onPrimaryContainer = Blue80,
    secondary = Teal60,
    onSecondary = SurfaceLight,
    secondaryContainer = Teal40,
    onSecondaryContainer = Teal80,
    background = BackgroundLight,
    surface = SurfaceLight,
)

@Composable
fun KituiWaterFinderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}