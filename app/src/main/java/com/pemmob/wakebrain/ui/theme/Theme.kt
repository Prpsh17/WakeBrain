package com.pemmob.wakebrain.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA78BFA),
    onPrimaryContainer = Color(0xFF4C1D95),

    secondary = Color(0xFF6366F1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF818CF8),
    onSecondaryContainer = Color(0xFF312E81),

    tertiary = Color(0xFFA78BFA),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC4B5FD),
    onTertiaryContainer = Color(0xFF5B21B6),

    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF262626),
    onSurfaceVariant = Color(0xFFA1A1AA),

    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),

    outline = Color(0xFF52525B)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B5CF6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA78BFA),
    onPrimaryContainer = Color(0xFF5B21B6),

    secondary = Color(0xFF6366F1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF818CF8),
    onSecondaryContainer = Color(0xFF3730A3),

    tertiary = Color(0xFFA78BFA),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC4B5FD),
    onTertiaryContainer = Color(0xFF6D28D9),

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF0F0F0F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF0F0F0F),
    surfaceVariant = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFF52525B),

    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),

    outline = Color(0xFF71717A)
)

@Composable
fun WakeBrainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
