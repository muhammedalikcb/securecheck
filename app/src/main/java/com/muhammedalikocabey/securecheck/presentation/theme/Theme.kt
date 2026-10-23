package com.muhammedalikocabey.securecheck.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

private val LocalSecureColors = staticCompositionLocalOf { LightSecureColors }

object SecureTheme {
    val colors: SecureColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSecureColors.current
}

private val LightScheme = lightColorScheme(
    primary = Palette.lightPrimary,
    onPrimary = Palette.lightOnPrimary,
    background = Palette.lightBackground,
    onBackground = Palette.lightOnSurface,
    surface = Palette.lightSurface,
    onSurface = Palette.lightOnSurface,
    surfaceVariant = Palette.lightSurfaceVariant,
    onSurfaceVariant = Palette.lightOnSurfaceVariant,
    outline = Palette.lightOutline,
    outlineVariant = Palette.lightOutline,
    error = LightSecureColors.critical.main,
)

private val DarkScheme = darkColorScheme(
    primary = Palette.darkPrimary,
    onPrimary = Palette.darkOnPrimary,
    background = Palette.darkBackground,
    onBackground = Palette.darkOnSurface,
    surface = Palette.darkSurface,
    onSurface = Palette.darkOnSurface,
    surfaceVariant = Palette.darkSurfaceVariant,
    onSurfaceVariant = Palette.darkOnSurfaceVariant,
    outline = Palette.darkOutline,
    outlineVariant = Palette.darkOutline,
    error = DarkSecureColors.critical.main,
)

private val SecureShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

/** The palette is fixed on purpose: status colors carry meaning and must not shift with the wallpaper. */
@Composable
fun SecureCheckTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSecureColors provides if (darkTheme) DarkSecureColors else LightSecureColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = SecureTypography,
            shapes = SecureShapes,
            content = content,
        )
    }
}
