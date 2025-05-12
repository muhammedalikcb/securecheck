package com.muhammedalikocabey.securecheck.presentation.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

data class RiskColors(
    val low: Color,
    val medium: Color,
    val high: Color
)

val LocalRiskColors = compositionLocalOf {
    RiskColors(
        low = RiskLow,
        medium = RiskMedium,
        high = RiskHigh
    )
}

@Composable
fun SecureCheckTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
    val userPrefersDark = preferences.getBoolean("dark_mode", false)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (userPrefersDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        userPrefersDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val riskColors = RiskColors(
        low = RiskLow,
        medium = RiskMedium,
        high = RiskHigh
    )

    CompositionLocalProvider(LocalRiskColors provides riskColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}