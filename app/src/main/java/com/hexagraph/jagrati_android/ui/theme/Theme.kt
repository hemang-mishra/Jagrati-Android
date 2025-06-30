package com.hexagraph.jagrati_android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom colors composition local for accessing custom colors in components
val LocalCustomColors = staticCompositionLocalOf { CustomColors }

@Composable
fun JagratiAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to always use your brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

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

// Extension properties for easy access to custom colors
@Composable
fun customColors() = LocalCustomColors.current

// Utility composables for accessing themed colors
object JagratiThemeColors {
    val primary @Composable get() = MaterialTheme.colorScheme.primary
    val secondary @Composable get() = MaterialTheme.colorScheme.secondary
    val tertiary @Composable get() = MaterialTheme.colorScheme.tertiary
    val surface @Composable get() = MaterialTheme.colorScheme.surface
    val background @Composable get() = MaterialTheme.colorScheme.background
    val error @Composable get() = MaterialTheme.colorScheme.error

    // Custom brand colors
    val orange @Composable get() = JagratiColors.Orange
    val purple @Composable get() = JagratiColors.Purple
    val yellow @Composable get() = JagratiColors.Yellow
    val turquoise @Composable get() = JagratiColors.Turquoise
    val brown @Composable get() = JagratiColors.Brown

    // Batch colors based on current theme
    val batchColors @Composable get() = getBatchColors()
}