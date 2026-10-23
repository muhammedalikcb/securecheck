package com.muhammedalikocabey.securecheck.domain.model

/**
 * Outcome of one check.
 *
 * [evidence] holds raw technical facts (paths, versions, package names) and is intentionally not localized,
 * so a report reads the same on every device. User-facing sentences come from the presentation layer.
 */
data class CheckResult(
    val id: CheckId,
    val status: CheckStatus,
    val severity: Severity = id.severity,
    val confidence: Confidence = Confidence.HIGH,
    val evidence: List<String> = emptyList(),
    val reason: UnavailableReason? = null,
    val durationMs: Long = 0,
) {
    val isScored: Boolean get() = status == CheckStatus.FINDING

    companion object {
        fun passed(id: CheckId, evidence: List<String> = emptyList()) =
            CheckResult(id, CheckStatus.PASSED, evidence = evidence)

        fun info(id: CheckId, evidence: List<String> = emptyList()) =
            CheckResult(id, CheckStatus.INFO, evidence = evidence)

        fun finding(
            id: CheckId,
            evidence: List<String>,
            confidence: Confidence = Confidence.HIGH,
            severity: Severity = id.severity,
        ) = CheckResult(id, CheckStatus.FINDING, severity, confidence, evidence)

        fun expected(id: CheckId, evidence: List<String> = emptyList()) =
            CheckResult(id, CheckStatus.EXPECTED, evidence = evidence)

        fun unavailable(id: CheckId, reason: UnavailableReason, evidence: List<String> = emptyList()) =
            CheckResult(id, CheckStatus.UNAVAILABLE, severity = Severity.INFO, evidence = evidence, reason = reason)
    }
}
