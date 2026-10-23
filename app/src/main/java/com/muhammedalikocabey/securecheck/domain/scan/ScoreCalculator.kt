package com.muhammedalikocabey.securecheck.domain.scan

import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.RiskLevel
import com.muhammedalikocabey.securecheck.domain.model.ScanScore
import com.muhammedalikocabey.securecheck.domain.model.Severity
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Score = 100 minus the weighted cost of findings. A finding costs `severity.weight * confidence.factor`.
 *
 * Informational, expected and unavailable results never cost anything, but unavailable ones lower [ScanScore.coverage]
 * so a scan that could not run half its checks cannot look perfect. A confirmed high-severity finding caps the
 * score at [CONFIRMED_HIGH_CAP]: a rooted device should not read as "good" because everything else passed.
 */
class ScoreCalculator @Inject constructor() {

    fun calculate(results: List<CheckResult>): ScanScore {
        val findings = results.filter { it.status == CheckStatus.FINDING }
        val penalty = findings.sumOf { it.severity.weight * it.confidence.factor.toDouble() }
        var value = (MAX - penalty).roundToInt().coerceIn(0, MAX)

        val confirmedHigh = findings.any {
            it.confidence == Confidence.HIGH && (it.severity == Severity.HIGH || it.severity == Severity.CRITICAL)
        }
        if (confirmedHigh) value = minOf(value, CONFIRMED_HIGH_CAP)

        val conclusive = results.count { it.status != CheckStatus.UNAVAILABLE }
        val coverage = if (results.isEmpty()) 0 else (conclusive * 100.0 / results.size).roundToInt()

        return ScanScore(value, riskFor(value), coverage, findings.size)
    }

    private fun riskFor(value: Int) = when {
        value >= LOW_RISK_MIN -> RiskLevel.LOW
        value >= MODERATE_RISK_MIN -> RiskLevel.MODERATE
        value >= ELEVATED_RISK_MIN -> RiskLevel.ELEVATED
        else -> RiskLevel.HIGH
    }

    companion object {
        const val MAX = 100
        const val CONFIRMED_HIGH_CAP = 69
        const val LOW_RISK_MIN = 90
        const val MODERATE_RISK_MIN = 70
        const val ELEVATED_RISK_MIN = 40
    }
}
