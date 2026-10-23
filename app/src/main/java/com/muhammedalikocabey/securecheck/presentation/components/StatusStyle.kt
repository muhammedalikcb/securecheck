package com.muhammedalikocabey.securecheck.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.Category
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.RiskLevel
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.presentation.theme.SecureTheme
import com.muhammedalikocabey.securecheck.presentation.theme.Tone

/**
 * How a status is drawn. Color is never the only signal: each status also has its own icon shape and a text label.
 */
@Immutable
class StatusStyle(val tone: Tone, val icon: ImageVector, @StringRes val label: Int)

@Composable
fun CheckResult.style(): StatusStyle {
    val colors = SecureTheme.colors
    return when (status) {
        CheckStatus.PASSED -> StatusStyle(colors.success, SecureIcons.Check, R.string.status_passed)
        CheckStatus.FINDING -> when (severity) {
            Severity.CRITICAL, Severity.HIGH -> StatusStyle(colors.critical, SecureIcons.Alert, R.string.status_finding)
            else -> StatusStyle(colors.warning, SecureIcons.Warning, R.string.status_finding)
        }
        CheckStatus.INFO -> StatusStyle(colors.info, SecureIcons.Info, R.string.status_info)
        CheckStatus.EXPECTED -> StatusStyle(colors.neutral, SecureIcons.Info, R.string.status_expected)
        CheckStatus.UNAVAILABLE -> StatusStyle(colors.neutral, SecureIcons.Dash, R.string.status_unavailable)
    }
}

@Composable
fun RiskLevel.tone(): Tone {
    val colors = SecureTheme.colors
    return when (this) {
        RiskLevel.LOW -> colors.success
        RiskLevel.MODERATE -> colors.warning
        RiskLevel.ELEVATED, RiskLevel.HIGH -> colors.critical
    }
}

/** The tone of a category is decided by its worst result, and stays neutral when nothing could run. */
@Composable
fun categoryTone(results: List<CheckResult>): Tone {
    val colors = SecureTheme.colors
    val worst = results.filter { it.status == CheckStatus.FINDING }.minByOrNull { it.severity.ordinal }
    return when {
        worst != null && worst.severity <= Severity.HIGH -> colors.critical
        worst != null -> colors.warning
        results.isNotEmpty() && results.all { it.status == CheckStatus.UNAVAILABLE } -> colors.neutral
        else -> colors.success
    }
}

fun Category.icon(): ImageVector = when (this) {
    Category.DEVICE -> SecureIcons.Device
    Category.APP -> SecureIcons.App
    Category.RUNTIME -> SecureIcons.Runtime
    Category.NETWORK -> SecureIcons.Network
    Category.DATA -> SecureIcons.Data
    Category.PLATFORM -> SecureIcons.Platform
}

/** A round tinted tile with the status icon. The icon is decorative; the row it sits in carries the status text. */
@Composable
fun StatusGlyph(tone: Tone, icon: ImageVector, modifier: Modifier = Modifier) {
    Box(modifier.size(40.dp).background(tone.container, CircleShape), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = tone.main, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun StatusBadge(tone: Tone, icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier.background(tone.container, CircleShape).padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tone.onContainer, modifier = Modifier.size(16.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = tone.onContainer)
    }
}

@Composable
fun StatusBadge(result: CheckResult, modifier: Modifier = Modifier) {
    val style = result.style()
    StatusBadge(style.tone, style.icon, stringResource(style.label), modifier)
}
