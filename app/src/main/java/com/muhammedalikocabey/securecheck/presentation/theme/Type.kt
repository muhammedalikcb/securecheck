package com.muhammedalikocabey.securecheck.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Base = Typography()

/** Tabular figures keep digits the same width, so scores and counters do not jitter while they change. */
private const val TABULAR = "tnum"

internal val SecureTypography = Typography(
    displayLarge = Base.displayLarge.copy(
        fontSize = 64.sp,
        lineHeight = 68.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-1).sp,
        fontFeatureSettings = TABULAR,
    ),
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelSmall = Base.labelSmall.copy(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold),
)

/** For counters and fractions that sit in running text. */
val NumericStyle = TextStyle(fontFeatureSettings = TABULAR)
