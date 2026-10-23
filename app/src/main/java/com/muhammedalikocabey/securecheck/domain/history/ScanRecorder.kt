package com.muhammedalikocabey.securecheck.domain.history

import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.domain.settings.SettingsStore
import java.io.IOException
import javax.inject.Inject

/** Saves a finished report and compares it with the one before. History problems never break a scan. */
class ScanRecorder @Inject constructor(private val store: ReportStore, private val settings: SettingsStore) {
    /** Returns the diff against the previous saved scan, or null when history is off, empty or unavailable. */
    suspend fun record(report: ScanReport): ReportDiff? {
        if (!settings.settings.value.saveHistory) return null
        return try {
            val previous = store.all().firstOrNull { it.id != report.id }
            store.save(report)
            previous?.let { ReportDiff.between(it, report) }
        } catch (_: IOException) {
            null
        }
    }
}
