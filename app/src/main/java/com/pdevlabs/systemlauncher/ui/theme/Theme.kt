package com.pdevlabs.systemlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme()
private val Dark = darkColorScheme(
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color(0xFF111111)
)

@Composable
fun LauncherTheme(dark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) Dark else Light,
        content = content
    )
}
