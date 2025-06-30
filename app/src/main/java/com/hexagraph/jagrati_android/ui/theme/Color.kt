package com.hexagraph.jagrati_android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors from your palette
object JagratiColors {
    val Orange = Color(0xFFF17E01)
    val Purple = Color(0xFFAA9FF8)
    val Yellow = Color(0xFFFD233)
    val Turquoise = Color(0xFF3FB8AF)
    val Brown = Color(0xFFA65700)
    val DarkBrown = Color(0xFF332118)
    val LightGray = Color(0xFFFAF7F2)
    val Black = Color(0xFF121212)
}

// Light Theme Colors
val LightColorScheme = lightColorScheme(
    // Primary - Orange (main brand color)
    primary = JagratiColors.Orange,
    onPrimary = Color.White,
    primaryContainer = JagratiColors.Orange.copy(alpha = 0.1f),
    onPrimaryContainer = JagratiColors.Orange,

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
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Outline
    outline = Color.Black.copy(alpha = 0.12f),
    outlineVariant = Color.Black.copy(alpha = 0.06f),

    // Inverse colors
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    inversePrimary = JagratiColors.Orange.copy(alpha = 0.8f),

    // Surface tint
    surfaceTint = JagratiColors.Orange
)

// Dark Theme Colors
val DarkColorScheme = darkColorScheme(
    // Primary - Orange (adjusted for dark theme)
    primary = JagratiColors.Orange.copy(alpha = 0.9f),
    onPrimary = Color.Black,
    primaryContainer = JagratiColors.Orange.copy(alpha = 0.2f),
    onPrimaryContainer = JagratiColors.Orange,

    // Secondary - Purple (adjusted for dark theme)
    secondary = JagratiColors.Purple.copy(alpha = 0.8f),
    onSecondary = Color.Black,
    secondaryContainer = JagratiColors.Purple.copy(alpha = 0.2f),
    onSecondaryContainer = JagratiColors.Purple,

    // Tertiary - Turquoise (using turquoise instead of yellow for better dark theme contrast)
    tertiary = JagratiColors.Turquoise,
    onTertiary = Color.Black,
    tertiaryContainer = JagratiColors.Turquoise.copy(alpha = 0.2f),
    onTertiaryContainer = JagratiColors.Turquoise,

    // Surface colors
    surface = JagratiColors.DarkBrown,
    onSurface = Color.White,
    surfaceVariant = JagratiColors.DarkBrown.copy(alpha = 0.8f),
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),

    // Background
    background = JagratiColors.Black,
    onBackground = Color.White,

    // Error (adjusted for dark theme)
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Outline
    outline = Color.White.copy(alpha = 0.12f),
    outlineVariant = Color.White.copy(alpha = 0.06f),

    // Inverse colors
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    inversePrimary = JagratiColors.Orange,

    // Surface tint
    surfaceTint = JagratiColors.Orange.copy(alpha = 0.8f)
)

// Additional custom colors for batch divisions, etc.
object CustomColors {
    val BatchOrange = JagratiColors.Orange
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
        BatchOrange,
        BatchPurple,
        BatchYellow,
        BatchTurquoise,
        BatchBrown
    )

    // Dark theme batch colors (slightly adjusted for better visibility)
    val darkBatchColors = listOf(
        BatchOrange.copy(alpha = 0.9f),
        BatchPurple.copy(alpha = 0.8f),
        BatchYellow.copy(alpha = 0.9f),
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