package com.hexagraph.jagrati_android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

// Custom colors composition local for accessing custom colors in components
val LocalCustomColors = staticCompositionLocalOf { CustomColors }

@Composable
fun JagratiAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalCustomColors provides CustomColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Utility composables for accessing themed colors
object JagratiThemeColors {
    val primary @Composable get() = MaterialTheme.colorScheme.primary
    val secondary @Composable get() = MaterialTheme.colorScheme.secondary
    val surface @Composable get() = MaterialTheme.colorScheme.surface
    val background @Composable get() = MaterialTheme.colorScheme.background
    val error @Composable get() = MaterialTheme.colorScheme.error

    // Custom brand colors
    val red @Composable get() = JagratiColors.Red
    val purple @Composable get() = JagratiColors.Purple

    // Batch colors based on current theme
    val batchColors @Composable get() = getBatchColors()
}