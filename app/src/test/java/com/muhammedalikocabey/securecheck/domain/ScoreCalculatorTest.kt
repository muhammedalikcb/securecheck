package com.muhammedalikocabey.securecheck.domain

import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.RiskLevel
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.ScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreCalculatorTest {
    private val calculator = ScoreCalculator()

    @Test
    fun `all passed scores 100 with low risk`() {
        val score = calculator.calculate(CheckId.entries.map { CheckResult.passed(it) })
        assertEquals(100, score.value)
        assertEquals(RiskLevel.LOW, score.risk)
        assertEquals(100, score.coverage)
        assertEquals(0, score.findingCount)
    }

    @Test
    fun `finding cost is severity weight times confidence factor`() {
        val results = listOf(
            CheckResult.finding(CheckId.SCREEN_LOCK, listOf("x"), Confidence.MEDIUM, Severity.MEDIUM),
        )
        // 8 * 0.6 = 4.8, rounded
        assertEquals(95, calculator.calculate(results).value)
    }

    @Test
    fun `confirmed high finding caps the score even when everything else passes`() {
        val results = CheckId.entries.filter { it != CheckId.ROOT }.map { CheckResult.passed(it) } +
            CheckResult.finding(CheckId.ROOT, listOf("su"))
        val score = calculator.calculate(results)
        assertEquals(69, score.value)
        assertEquals(RiskLevel.ELEVATED, score.risk)
    }

    @Test
    fun `low confidence high finding does not trigger the cap`() {
        val results = listOf(CheckResult.finding(CheckId.ROOT, listOf("test-keys"), Confidence.LOW))
        // 15 * 0.3 = 4.5, so the raw score sits on the 95/96 rounding edge; only the cap matters here.
        assertTrue(calculator.calculate(results).value in 95..96)
    }

    @Test
    fun `unavailable results cost nothing but reduce coverage`() {
        val results = listOf(
            CheckResult.passed(CheckId.SCREEN_LOCK),
            CheckResult.unavailable(CheckId.PLAY_INTEGRITY, UnavailableReason.NOT_CONFIGURED),
        )
        val score = calculator.calculate(results)
        assertEquals(100, score.value)
        assertEquals(50, score.coverage)
    }

    @Test
    fun `info and expected never cost anything`() {
        val results = listOf(CheckResult.info(CheckId.VPN), CheckResult.expected(CheckId.DEBUGGABLE))
        assertEquals(100, calculator.calculate(results).value)
    }

    @Test
    fun `score never drops below zero`() {
        val results = CheckId.entries.map { CheckResult.finding(it, listOf("x"), severity = Severity.CRITICAL) }
        val score = calculator.calculate(results)
        assertEquals(0, score.value)
        assertEquals(RiskLevel.HIGH, score.risk)
    }

    @Test
    fun `empty input has zero coverage`() {
        assertEquals(0, calculator.calculate(emptyList()).coverage)
    }
}
