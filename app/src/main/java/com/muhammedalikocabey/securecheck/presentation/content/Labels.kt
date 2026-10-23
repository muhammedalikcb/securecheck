package com.muhammedalikocabey.securecheck.presentation.content

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.Category
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.RiskLevel
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason

@get:StringRes
val RiskLevel.labelRes: Int
    get() = when (this) {
        RiskLevel.LOW -> R.string.risk_low
        RiskLevel.MODERATE -> R.string.risk_moderate
        RiskLevel.ELEVATED -> R.string.risk_elevated
        RiskLevel.HIGH -> R.string.risk_high
    }

@get:StringRes
val Severity.labelRes: Int
    get() = when (this) {
        Severity.CRITICAL -> R.string.severity_critical
        Severity.HIGH -> R.string.severity_high
        Severity.MEDIUM -> R.string.severity_medium
        Severity.LOW -> R.string.severity_low
        Severity.INFO -> R.string.severity_info
    }

@get:StringRes
val Confidence.labelRes: Int
    get() = when (this) {
        Confidence.HIGH -> R.string.confidence_high
        Confidence.MEDIUM -> R.string.confidence_medium
        Confidence.LOW -> R.string.confidence_low
    }

@get:StringRes
val CheckStatus.labelRes: Int
    get() = when (this) {
        CheckStatus.PASSED -> R.string.status_passed
        CheckStatus.FINDING -> R.string.status_finding
        CheckStatus.INFO -> R.string.status_info
        CheckStatus.UNAVAILABLE -> R.string.status_unavailable
        CheckStatus.EXPECTED -> R.string.status_expected
    }

@get:StringRes
val UnavailableReason.labelRes: Int
    get() = when (this) {
        UnavailableReason.NOT_CONFIGURED -> R.string.reason_not_configured
        UnavailableReason.UNSUPPORTED -> R.string.reason_unsupported
        UnavailableReason.PERMISSION -> R.string.reason_permission
        UnavailableReason.TIMEOUT -> R.string.reason_timeout
        UnavailableReason.ERROR -> R.string.reason_error
    }

@get:StringRes
val Category.labelRes: Int
    get() = when (this) {
        Category.DEVICE -> R.string.category_device
        Category.APP -> R.string.category_app
        Category.RUNTIME -> R.string.category_runtime
        Category.NETWORK -> R.string.category_network
        Category.DATA -> R.string.category_data
        Category.PLATFORM -> R.string.category_platform
    }

/** One sentence that states the outcome of this result in plain language. */
@Composable
fun CheckResult.summary(): String {
    val text = id.text()
    return stringResource(
        when (status) {
            CheckStatus.PASSED -> text.ok
            CheckStatus.FINDING -> text.bad
            CheckStatus.INFO -> text.info
            CheckStatus.EXPECTED -> R.string.expected_summary
            CheckStatus.UNAVAILABLE -> (reason ?: UnavailableReason.ERROR).labelRes
        },
    )
}
