package com.muhammedalikocabey.securecheck.domain

import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.ScanEngine
import com.muhammedalikocabey.securecheck.domain.scan.ScanEvent
import com.muhammedalikocabey.securecheck.domain.scan.ScoreCalculator
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import com.muhammedalikocabey.securecheck.domain.scan.TimeSource
import com.muhammedalikocabey.securecheck.support.FakeEnvironment
import com.muhammedalikocabey.securecheck.support.StubCheck
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanEngineTest {
    private fun TestScope.engine(vararg checks: SecurityCheck) = ScanEngine(
        checks = checks.toSet(),
        scoreCalculator = ScoreCalculator(),
        environment = FakeEnvironment(),
        time = TimeSource { currentTime },
        dispatcher = StandardTestDispatcher(testScheduler),
    )

    @Test
    fun `emits started, one event per check, then the report in check order`() = runTest {
        val engine = engine(
            StubCheck(CheckId.VPN) { CheckResult.passed(CheckId.VPN) },
            StubCheck(CheckId.ROOT) { CheckResult.finding(CheckId.ROOT, listOf("su")) },
        )
        val events = engine.scan().toList()

        assertEquals(ScanEvent.Started(2), events.first())
        assertEquals(2, events.filterIsInstance<ScanEvent.CheckCompleted>().size)
        assertEquals(
            setOf(CheckId.VPN, CheckId.ROOT),
            events.filterIsInstance<ScanEvent.CheckStarted>().map { it.id }.toSet(),
        )
        val report = (events.last() as ScanEvent.Finished).report
        assertEquals(listOf(CheckId.ROOT, CheckId.VPN), report.results.map { it.id })
        assertEquals(1, report.score.findingCount)
    }

    @Test
    fun `a check that throws becomes unavailable, not passed`() = runTest {
        val engine = engine(StubCheck(CheckId.ROOT) { error("boom") })
        val result = (engine.scan().toList().last() as ScanEvent.Finished).report.result(CheckId.ROOT)!!

        assertEquals(CheckStatus.UNAVAILABLE, result.status)
        assertEquals(UnavailableReason.ERROR, result.reason)
    }

    @Test
    fun `a check that hangs times out as unavailable`() = runTest {
        val engine = engine(
            StubCheck(CheckId.FRIDA) {
                delay(60_000)
                CheckResult.passed(CheckId.FRIDA)
            },
        )
        val result = (engine.scan().toList().last() as ScanEvent.Finished).report.result(CheckId.FRIDA)!!

        assertEquals(UnavailableReason.TIMEOUT, result.reason)
        assertTrue(currentTime < 60_000)
    }

    @Test
    fun `one failing check does not stop the others`() = runTest {
        val engine = engine(
            StubCheck(CheckId.ROOT) { error("boom") },
            StubCheck(CheckId.VPN) { CheckResult.passed(CheckId.VPN) },
        )
        val report = (engine.scan().toList().last() as ScanEvent.Finished).report
        assertEquals(CheckStatus.PASSED, report.result(CheckId.VPN)!!.status)
    }

    @Test
    fun `runOne re-executes a single check and ignores unknown ids`() = runTest {
        var calls = 0
        val engine = engine(
            StubCheck(CheckId.VPN) {
                calls++
                CheckResult.passed(CheckId.VPN)
            },
        )

        assertEquals(CheckStatus.PASSED, engine.runOne(CheckId.VPN)!!.status)
        assertEquals(1, calls)
        assertNull(engine.runOne(CheckId.ROOT))
    }
}
