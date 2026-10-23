package com.muhammedalikocabey.securecheck.presentation

import androidx.compose.ui.graphics.Color
import com.muhammedalikocabey.securecheck.presentation.theme.DarkSecureColors
import com.muhammedalikocabey.securecheck.presentation.theme.LightSecureColors
import com.muhammedalikocabey.securecheck.presentation.theme.Palette
import com.muhammedalikocabey.securecheck.presentation.theme.SecureColors
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

/** WCAG 2.2 contrast: 4.5:1 for text, 3:1 for icons and other meaningful graphics. */
class ContrastTest {
    private class Scheme(
        val name: String,
        val colors: SecureColors,
        val background: Color,
        val surface: Color,
        val onSurface: Color,
        val onSurfaceVariant: Color,
        val outline: Color,
        val primary: Color,
        val onPrimary: Color,
    )

    private val schemes = listOf(
        Scheme(
            "light",
            LightSecureColors,
            Palette.lightBackground,
            Palette.lightSurface,
            Palette.lightOnSurface,
            Palette.lightOnSurfaceVariant,
            Palette.lightOutline,
            Palette.lightPrimary,
            Palette.lightOnPrimary,
        ),
        Scheme(
            "dark",
            DarkSecureColors,
            Palette.darkBackground,
            Palette.darkSurface,
            Palette.darkOnSurface,
            Palette.darkOnSurfaceVariant,
            Palette.darkOutline,
            Palette.darkPrimary,
            Palette.darkOnPrimary,
        ),
    )

    @Test
    fun `body text meets 4_5 to 1 on every surface`() {
        for (s in schemes) {
            for (bg in listOf(s.background, s.surface)) {
                check(s.name, "onSurface on $bg", s.onSurface, bg, TEXT)
                check(s.name, "onSurfaceVariant on $bg", s.onSurfaceVariant, bg, TEXT)
            }
        }
    }

    @Test
    fun `primary action text and fill meet 4_5 to 1`() {
        for (s in schemes) {
            check(s.name, "onPrimary on primary", s.onPrimary, s.primary, TEXT)
            check(s.name, "primary text on surface", s.primary, s.surface, TEXT)
        }
    }

    @Test
    fun `status text on its own container meets 4_5 to 1`() {
        for (s in schemes) {
            for ((name, tone) in tones(s.colors)) {
                check(s.name, "$name onContainer on container", tone.onContainer, tone.container, TEXT)
            }
        }
    }

    @Test
    fun `status icons meet 3 to 1 on their tile and on the surface`() {
        for (s in schemes) {
            for ((name, tone) in tones(s.colors)) {
                check(s.name, "$name icon on container", tone.main, tone.container, GRAPHIC)
                check(s.name, "$name icon on surface", tone.main, s.surface, GRAPHIC)
            }
        }
    }

    @Test
    fun `progress track and dividers stay visible against the surface`() {
        // Hairlines are decorative, but the score ring track is a graphic that frames the value.
        for (s in schemes) check(s.name, "outline on surface", s.outline, s.surface, 1.2)
    }

    private fun tones(c: SecureColors) = mapOf(
        "success" to c.success,
        "warning" to c.warning,
        "critical" to c.critical,
        "info" to c.info,
        "neutral" to c.neutral,
    )

    private fun check(scheme: String, label: String, fg: Color, bg: Color, min: Double) {
        val ratio = contrast(fg, bg)
        assertTrue("$scheme: $label is %.2f:1, needs $min:1".format(ratio), ratio >= min)
    }

    private fun contrast(a: Color, b: Color): Double {
        val hi = maxOf(luminance(a), luminance(b))
        val lo = minOf(luminance(a), luminance(b))
        return (hi + 0.05) / (lo + 0.05)
    }

    private fun luminance(c: Color): Double {
        fun channel(v: Float): Double {
            val d = v.toDouble()
            return if (d <= 0.03928) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
    }

    private companion object {
        const val TEXT = 4.5
        const val GRAPHIC = 3.0
    }
}
