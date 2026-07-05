package com.example.animewatch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.animewatch.util.AppSettings

/**
 * Тема приложения. Акцентный цвет теперь настраиваемый (экран "Настройки"),
 * остальная палитра (фон/поверхности/текст) фиксированная тёмная — по дизайну.
 */
@Composable
fun AnimeWatchTheme(content: @Composable () -> Unit) {
    val accentKey by AppSettings.accentColorKey.collectAsState()
    val accent = accentByKey(accentKey)

    val colorScheme = darkColorScheme(
        primary = accent.color,
        onPrimary = Color.White,
        primaryContainer = accent.dark,
        onPrimaryContainer = TextPrimary,
        secondary = accent.dark,
        background = BackgroundDark,
        onBackground = TextPrimary,
        surface = SurfaceDark,
        onSurface = TextPrimary,
        surfaceVariant = SurfaceDark,
        onSurfaceVariant = TextSecondary,
        error = ErrorRed,
        outline = DividerColor
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as? android.app.Activity)?.window
        window?.let {
            WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
            it.statusBarColor = BackgroundDark.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
