package com.muhammedalikocabey.securecheck.domain

import com.muhammedalikocabey.securecheck.domain.history.BackgroundScan
import com.muhammedalikocabey.securecheck.domain.history.ReportDiff
import com.muhammedalikocabey.securecheck.domain.history.ScanRecorder
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.ScanEngine
import com.muhammedalikocabey.securecheck.domain.scan.ScoreCalculator
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import com.muhammedalikocabey.securecheck.domain.scan.TimeSource
import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.support.FakeEnvironment
import com.muhammedalikocabey.securecheck.support.FakeNotifier
import com.muhammedalikocabey.securecheck.support.FakeReportStore
import com.muhammedalikocabey.securecheck.support.FakeSettingsStore
import com.muhammedalikocabey.securecheck.support.StubCheck
import com.muhammedalikocabey.securecheck.support.reportOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryTest {
    @Test
    fun `diff lists new and resolved findings by check, not by count`() {
        val before = reportOf("a", 1, CheckId.ROOT, CheckId.VPN)
        val after = reportOf("b", 2, CheckId.VPN, CheckId.USER_CA)
        val diff = ReportDiff.between(before, after)

        assertEquals(listOf(CheckId.USER_CA), diff.newFindings)
        assertEquals(listOf(CheckId.ROOT), diff.resolved)
        assertTrue(diff.hasChanges)
    }

    @Test
    fun `identical scans produce an empty diff`() {
        val diff = ReportDiff.between(reportOf("a", 1, CheckId.ROOT), reportOf("b", 2, CheckId.ROOT))

        assertEquals(0, diff.scoreDelta)
        assertFalse(diff.hasChanges)
    }

    @Test
    fun `recorder saves, compares with the previous scan and skips the first`() = runTest {
        val store = FakeReportStore()
        val recorder = ScanRecorder(store, FakeSettingsStore())

        assertNull(recorder.record(reportOf("a", 1, CheckId.ROOT)))
        val diff = recorder.record(reportOf("b", 2))

        assertEquals(listOf(CheckId.ROOT), diff!!.resolved)
        assertEquals(2, store.saved.size)
    }

    @Test
    fun `recorder does nothing while history is off`() = runTest {
        val store = FakeReportStore()
        val recorder = ScanRecorder(store, FakeSettingsStore(Settings(saveHistory = false)))

        assertNull(recorder.record(reportOf("a", 1)))
        assertTrue(store.saved.isEmpty())
    }

    @Test
    fun `a broken store never breaks the scan`() = runTest {
        val recorder = ScanRecorder(FakeReportStore(failing = true), FakeSettingsStore())

        assertNull(recorder.record(reportOf("a", 1)))
    }

    private fun TestScope.background(notifier: FakeNotifier, store: FakeReportStore, vararg checks: SecurityCheck) =
        BackgroundScan(
            engine = ScanEngine(
                checks = checks.toSet(),
                scoreCalculator = ScoreCalculator(),
                environment = FakeEnvironment(),
                time = TimeSource { currentTime },
                dispatcher = StandardTestDispatcher(testScheduler),
            ),
            recorder = ScanRecorder(store, FakeSettingsStore()),
            notifier = notifier,
        )

    @Test
    fun `background scan notifies only when a finding is new`() = runTest {
        val store = FakeReportStore().apply { saved += reportOf("old", 1) }
        val notifier = FakeNotifier()
        val scan = background(
            notifier,
            store,
            StubCheck(CheckId.ROOT) { CheckResult.finding(CheckId.ROOT, listOf("su")) },
        )

        scan.run()
        assertEquals(1, notifier.calls.size)
        assertEquals(1, notifier.calls.single().first)

        // The same finding again is not news.
        scan.run()
        assertEquals(1, notifier.calls.size)
    }

    @Test
    fun `background scan stays quiet on the first run and when things improve`() = runTest {
        val notifier = FakeNotifier()
        val store = FakeReportStore()
        val scan = background(notifier, store, StubCheck(CheckId.ROOT) { CheckResult.passed(CheckId.ROOT) })

        scan.run()
        assertTrue(notifier.calls.isEmpty())

        store.saved += reportOf("worse", 0, CheckId.ROOT)
        scan.run()
        assertTrue(notifier.calls.isEmpty())
    }
}
