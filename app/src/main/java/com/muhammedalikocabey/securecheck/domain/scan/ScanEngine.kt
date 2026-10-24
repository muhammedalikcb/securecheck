package com.muhammedalikocabey.securecheck.domain.scan

import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import javax.inject.Inject

/**
 * Runs every registered [SecurityCheck] in parallel and streams progress.
 *
 * A check that throws or times out becomes UNAVAILABLE. It is never mapped to "passed", so a broken detector
 * cannot silently improve the score.
 */
class ScanEngine @Inject constructor(
    private val checks: Set<@JvmSuppressWildcards SecurityCheck>,
    private val scoreCalculator: ScoreCalculator,
    private val environment: ScanEnvironment,
    private val time: TimeSource,
    private val dispatcher: CoroutineDispatcher,
) {
    fun scan(): Flow<ScanEvent> = channelFlow {
        val startedAt = time.nowMs()
        val ordered = checks.sortedBy { it.id.ordinal }
        send(ScanEvent.Started(ordered.size))

        val results = mutableListOf<CheckResult>()
        val gate = Semaphore(MAX_PARALLEL)
        coroutineScope {
            ordered.map { check ->
                async {
                    val result = gate.withPermit {
                        send(ScanEvent.CheckStarted(check.id))
                        execute(check)
                    }
                    synchronized(results) {
                        results += result
                        ScanEvent.CheckCompleted(result, results.size, ordered.size)
                    }.also { send(it) }
                }
            }.forEach { it.await() }
        }

        val sorted = results.sortedBy { it.id.ordinal }
        send(
            ScanEvent.Finished(
                ScanReport(
                    id = UUID.randomUUID().toString(),
                    startedAtMs = startedAt,
                    durationMs = time.nowMs() - startedAt,
                    device = environment.device(),
                    app = environment.app(),
                    results = sorted,
                    score = scoreCalculator.calculate(sorted),
                ),
            ),
        )
    }

    /** Re-runs a single check, for the "check again" action in the detail sheet. */
    suspend fun runOne(id: CheckId): CheckResult? = checks.firstOrNull { it.id == id }?.let { execute(it) }

    private suspend fun execute(check: SecurityCheck): CheckResult {
        val started = time.nowMs()
        val result = try {
            withTimeoutOrNull(CHECK_TIMEOUT_MS) { withContext(dispatcher) { check.run() } }
                ?: CheckResult.unavailable(check.id, UnavailableReason.TIMEOUT)
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
            CheckResult.unavailable(check.id, UnavailableReason.ERROR, listOf(error.javaClass.simpleName))
        }
        return result.copy(durationMs = time.nowMs() - started)
    }

    companion object {
        const val CHECK_TIMEOUT_MS = 5_000L
        const val MAX_PARALLEL = 6
    }
}
