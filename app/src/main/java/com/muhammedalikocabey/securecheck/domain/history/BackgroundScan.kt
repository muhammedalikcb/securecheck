package com.muhammedalikocabey.securecheck.domain.history

import com.muhammedalikocabey.securecheck.domain.scan.ScanEngine
import com.muhammedalikocabey.securecheck.domain.scan.ScanEvent
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import javax.inject.Inject

fun interface ChangeNotifier {
    fun notifyNewFindings(count: Int, score: Int)
}

/** One unattended scan: run, record, and speak up only when something got worse. */
class BackgroundScan @Inject constructor(
    private val engine: ScanEngine,
    private val recorder: ScanRecorder,
    private val notifier: ChangeNotifier,
) {
    suspend fun run() {
        val report = engine.scan().filterIsInstance<ScanEvent.Finished>().first().report
        val diff = recorder.record(report) ?: return
        if (diff.newFindings.isNotEmpty()) notifier.notifyNewFindings(diff.newFindings.size, report.score.value)
    }
}
