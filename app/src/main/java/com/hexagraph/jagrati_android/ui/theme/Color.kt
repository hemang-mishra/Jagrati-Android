package com.hexagraph.jagrati_android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors from your palette
object JagratiColors {
    val Red = Color(0xFFE53935) // Vibrant red replacing orange
    val Purple = Color(0xFFAA9FF8)
    val Yellow = Color(0xFFEEC434)
    val Turquoise = Color(0xFF3FB8AF)
    val Brown = Color(0xFFA65700)
    val LightGray = Color(0xFFFAF7F2)
    val Black = Color(0xFF000000)
    val DarkGray = Color(0xFF1E1E1E) // Neutral dark gray for dark theme
}

// Light Theme Colors
val LightColorScheme = lightColorScheme(
    // Primary - Red (main brand color)
    primary = JagratiColors.Red,
    onPrimary = Color.White,
    primaryContainer = JagratiColors.Red.copy(alpha = 0.1f),
    onPrimaryContainer = JagratiColors.Red,

    // Secondary - Purple (contrast color)
    secondary = JagratiColors.Purple,
    onSecondary = Color.White,
    secondaryContainer = JagratiColors.Purple.copy(alpha = 0.1f),
    onSecondaryContainer = JagratiColors.Purple,

    // Tertiary - Yellow (accent)
    tertiary = JagratiColors.Yellow,
    onTertiary = Color.Black,
    tertiaryContainer = JagratiColors.Yellow.copy(alpha = 0.1f),
    onTertiaryContainer = JagratiColors.Yellow,

    // Surface colors
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = JagratiColors.LightGray,
    onSurfaceVariant = Color.Black.copy(alpha = 0.7f),

    // Background
    background = Color.White,
    onBackground = Color.Black,

    // Error (keep standard)
    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Outline
    outline = Color.Black.copy(alpha = 0.12f),
    outlineVariant = Color.Black.copy(alpha = 0.06f),

    // Inverse colors
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    inversePrimary = JagratiColors.Red.copy(alpha = 0.8f),

    // Surface tint
    surfaceTint = JagratiColors.Red
)

// Dark Theme Colors
val DarkColorScheme = darkColorScheme(
    // Primary - Red (adjusted for dark theme)
    primary = JagratiColors.Red,
    onPrimary = Color.White,
    primaryContainer = JagratiColors.Red.copy(alpha = 0.2f),
    onPrimaryContainer = JagratiColors.Red.copy(alpha = 0.9f),

    // Secondary - Purple (adjusted for dark theme)
    secondary = JagratiColors.Purple,
    onSecondary = Color.White,
    secondaryContainer = JagratiColors.Purple.copy(alpha = 0.2f),
    onSecondaryContainer = JagratiColors.Purple.copy(alpha = 0.9f),

    // Tertiary - Turquoise (using turquoise instead of yellow for better dark theme contrast)
    tertiary = JagratiColors.Turquoise,
    onTertiary = Color.White,
    tertiaryContainer = JagratiColors.Turquoise.copy(alpha = 0.2f),
    onTertiaryContainer = JagratiColors.Turquoise,

    // Surface colors - Using neutral dark gray instead of brown
    surface = JagratiColors.DarkGray,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),

    // Background
    background = JagratiColors.Black,
    onBackground = Color.White,

    // Error (adjusted for dark theme)
    error = Color(0xFFEF5350),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Outline
    outline = Color.White.copy(alpha = 0.12f),
    outlineVariant = Color.White.copy(alpha = 0.06f),

    // Inverse colors
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    inversePrimary = JagratiColors.Red,

    // Surface tint
    surfaceTint = JagratiColors.Red.copy(alpha = 0.8f)
)

// Additional custom colors for batch divisions, etc.
object CustomColors {
    val BatchRed = JagratiColors.Red
    val BatchPurple = JagratiColors.Purple
    val BatchYellow = JagratiColors.Yellow
    val BatchTurquoise = JagratiColors.Turquoise
    val BatchBrown = JagratiColors.Brown

    // Status colors
    val Success = Color(0xFF4CAF50)
    val Warning = JagratiColors.Yellow
    val Info = JagratiColors.Turquoise

    // Light theme batch colors
    val lightBatchColors = listOf(
        BatchRed,
        BatchPurple,
        BatchYellow,
        BatchTurquoise,
        BatchBrown
    )

    // Dark theme batch colors (slightly adjusted for better visibility)
    val darkBatchColors = listOf(
        BatchRed,
        BatchPurple,
        BatchYellow,
        BatchTurquoise,
        BatchBrown.copy(alpha = 0.8f)
    )
}

// Extension to access current batch colors based on theme
@Composable
fun getBatchColors(): List<Color> {
    return if (isSystemInDarkTheme()) {
        CustomColors.darkBatchColors
    } else {
        CustomColors.lightBatchColors
    }
}

val Dark_Background = Color.Black
val onDark_Background = Color.White