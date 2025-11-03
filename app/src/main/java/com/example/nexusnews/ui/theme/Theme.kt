package com.example.nexusnews.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun nexusNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
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
