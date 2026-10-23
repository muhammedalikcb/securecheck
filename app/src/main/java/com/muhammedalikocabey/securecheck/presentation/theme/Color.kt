package com.muhammedalikocabey.securecheck.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** One semantic tone: the [main] color for icons and strokes, with a tinted [container] and the text that sits on it. */
@Immutable
class Tone(val main: Color, val container: Color, val onContainer: Color)

@Immutable
class SecureColors(val success: Tone, val warning: Tone, val critical: Tone, val info: Tone, val neutral: Tone)

internal val LightSecureColors = SecureColors(
    success = Tone(Color(0xFF0F7A4B), Color(0xFFDDF3E7), Color(0xFF0A4F31)),
    warning = Tone(Color(0xFF8A5A00), Color(0xFFFFF0CC), Color(0xFF5C3B00)),
    critical = Tone(Color(0xFFB3261E), Color(0xFFFBE3E1), Color(0xFF7A1913)),
    info = Tone(Color(0xFF0B5CAD), Color(0xFFDCEBFA), Color(0xFF07396B)),
    neutral = Tone(Color(0xFF55667A), Color(0xFFEDF1F5), Color(0xFF3A4856)),
)

internal val DarkSecureColors = SecureColors(
    success = Tone(Color(0xFF4CD08F), Color(0xFF0F3324), Color(0xFFC7F2DC)),
    warning = Tone(Color(0xFFF2B94B), Color(0xFF3A2A05), Color(0xFFFFE2A3)),
    critical = Tone(Color(0xFFFF8A80), Color(0xFF4A1511), Color(0xFFFFD2CE)),
    info = Tone(Color(0xFF7DB7FF), Color(0xFF0F2740), Color(0xFFCFE5FF)),
    neutral = Tone(Color(0xFFA9B7C4), Color(0xFF1E2A35), Color(0xFFD3DCE4)),
)

internal object Palette {
    val lightBackground = Color(0xFFF6F8FA)
    val lightSurface = Color(0xFFFFFFFF)
    val lightSurfaceVariant = Color(0xFFEEF2F6)
    val lightOutline = Color(0xFFC9D3DC)
    val lightOnSurface = Color(0xFF0F1A24)
    val lightOnSurfaceVariant = Color(0xFF4A5B6B)
    val lightPrimary = Color(0xFF0B5CAD)
    val lightOnPrimary = Color(0xFFFFFFFF)

    val darkBackground = Color(0xFF0B1218)
    val darkSurface = Color(0xFF111B24)
    val darkSurfaceVariant = Color(0xFF18242F)
    val darkOutline = Color(0xFF2B3B4A)
    val darkOnSurface = Color(0xFFE6EDF3)
    val darkOnSurfaceVariant = Color(0xFF9FB1C1)
    val darkPrimary = Color(0xFF6CB2FF)
    val darkOnPrimary = Color(0xFF06223F)
}
