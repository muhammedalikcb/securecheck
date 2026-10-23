package com.muhammedalikocabey.securecheck.domain.history

import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.ScanReport

/** Finished reports kept on the device. Implementations may throw [java.io.IOException]. */
interface ReportStore {
    suspend fun save(report: ScanReport)

    /** Newest first. Unreadable entries are skipped. */
    suspend fun all(): List<ScanReport>

    suspend fun clear()
}

/** What changed between two scans of the same device. */
data class ReportDiff(val scoreDelta: Int, val newFindings: List<CheckId>, val resolved: List<CheckId>) {
    val hasChanges: Boolean get() = scoreDelta != 0 || newFindings.isNotEmpty() || resolved.isNotEmpty()

    companion object {
        fun between(previous: ScanReport, current: ScanReport): ReportDiff {
            val before = previous.findingIds()
            val after = current.findingIds()
            return ReportDiff(
                scoreDelta = current.score.value - previous.score.value,
                newFindings = (after - before).sortedBy { it.ordinal },
                resolved = (before - after).sortedBy { it.ordinal },
            )
        }

        private fun ScanReport.findingIds(): Set<CheckId> =
            results.filter { it.status == CheckStatus.FINDING }.mapTo(mutableSetOf()) { it.id }
    }
}
