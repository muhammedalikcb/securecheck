package com.muhammedalikocabey.securecheck.domain.model

enum class RiskLevel { LOW, MODERATE, ELEVATED, HIGH }

data class ScanScore(
    /** 0..100, higher is better. */
    val value: Int,
    val risk: RiskLevel,
    /** Share of checks that produced a conclusive answer, 0..100. */
    val coverage: Int,
    val findingCount: Int,
)

data class DeviceSummary(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
)

data class AppSummary(val versionName: String, val versionCode: Long, val debugBuild: Boolean)

data class ScanReport(
    val id: String,
    val startedAtMs: Long,
    val durationMs: Long,
    val device: DeviceSummary,
    val app: AppSummary,
    val results: List<CheckResult>,
    val score: ScanScore,
) {
    fun result(id: CheckId): CheckResult? = results.firstOrNull { it.id == id }

    /** Findings first, ordered by cost to the score, so the dashboard can show "fix these first". */
    fun prioritizedFindings(): List<CheckResult> = results
        .filter { it.status == CheckStatus.FINDING }
        .sortedWith(
            compareBy<CheckResult> { it.severity.ordinal }
                .thenBy { it.confidence.ordinal }
                .thenBy { it.id.ordinal },
        )
}
