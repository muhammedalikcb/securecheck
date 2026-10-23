package com.muhammedalikocabey.securecheck.support

import com.muhammedalikocabey.securecheck.domain.history.ChangeNotifier
import com.muhammedalikocabey.securecheck.domain.history.ReportStore
import com.muhammedalikocabey.securecheck.domain.model.AppSummary
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.DeviceSummary
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.domain.scan.ScoreCalculator
import com.muhammedalikocabey.securecheck.domain.settings.ScanScheduler
import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.domain.settings.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

/** A full report where [findings] fail and every other check passes. */
fun reportOf(id: String, startedAtMs: Long = 0, vararg findings: CheckId): ScanReport {
    val results = CheckId.entries.map { check ->
        if (check in
            findings
        ) {
            CheckResult.finding(check, listOf("evidence for ${check.key}"))
        } else {
            CheckResult.passed(check)
        }
    }
    return ScanReport(
        id = id,
        startedAtMs = startedAtMs,
        durationMs = 900,
        device = DeviceSummary("Google", "Pixel 8", "15", 35, "2025-05-05"),
        app = AppSummary("2.0.0", 1, false),
        results = results,
        score = ScoreCalculator().calculate(results),
    )
}

class FakeReportStore(private val failing: Boolean = false) : ReportStore {
    val saved = mutableListOf<ScanReport>()

    override suspend fun save(report: ScanReport) {
        if (failing) throw IOException("disk full")
        saved += report
    }

    override suspend fun all(): List<ScanReport> {
        if (failing) throw IOException("unreadable")
        return saved.reversed()
    }

    override suspend fun clear() {
        saved.clear()
    }
}

class FakeSettingsStore(initial: Settings = Settings()) : SettingsStore {
    private val state = MutableStateFlow(initial)
    override val settings: StateFlow<Settings> = state

    override fun update(transform: (Settings) -> Settings) {
        state.value = transform(state.value)
    }
}

class FakeScheduler : ScanScheduler {
    val calls = mutableListOf<Boolean>()

    override fun setDaily(enabled: Boolean) {
        calls += enabled
    }
}

class FakeNotifier : ChangeNotifier {
    val calls = mutableListOf<Pair<Int, Int>>()

    override fun notifyNewFindings(count: Int, score: Int) {
        calls += count to score
    }
}
