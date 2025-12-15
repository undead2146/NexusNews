package com.example.nexusnews.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.nexusnews.data.local.datastore.ThemeMode

/**
 * NexusNews theme with support for user-controlled theme modes.
 *
 * @param themeMode The theme mode to use (LIGHT, DARK, or SYSTEM)
 * @param content The composable content to theme
 */
@Composable
fun NexusNewsTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val systemInDarkTheme = isSystemInDarkTheme()

    // Resolve actual dark theme based on mode
    val darkTheme =
        when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> systemInDarkTheme
        }

    val colors =
        if (darkTheme) {
            darkColorScheme(
                primary = Primary,
                secondary = Secondary,
                background = BackgroundDark,
                surface = SurfaceDark,
                onPrimary = TextOnDark,
                onSecondary = TextOnDark,
                onBackground = TextOnDark,
                onSurface = TextOnDark,
                error = ErrorRed,
            )
        } else {
            lightColorScheme(
                primary = Primary,
                secondary = Secondary,
                background = BackgroundLight,
                surface = SurfaceLight,
                onPrimary = TextOnDark,
                onSecondary = TextOnDark,
                onBackground = TextPrimary,
                onSurface = TextPrimary,
                error = ErrorRed,
            )
        }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content,
    )
}
