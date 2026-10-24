package com.muhammedalikocabey.securecheck.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammedalikocabey.securecheck.domain.history.ReportDiff
import com.muhammedalikocabey.securecheck.domain.history.ScanRecorder
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.domain.scan.ScanEngine
import com.muhammedalikocabey.securecheck.domain.scan.ScanEvent
import com.muhammedalikocabey.securecheck.domain.scan.ScoreCalculator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScanUiState {
    data object Idle : ScanUiState

    data class Scanning(
        val completed: Int,
        val total: Int,
        val results: List<CheckResult>,
        /** Checks that hold a slot right now, oldest first. */
        val running: List<CheckId> = emptyList(),
    ) : ScanUiState

    /** The scan stopped on an unexpected error; the user can start it again. */
    data object Failed : ScanUiState

    /** [diff] is how this scan differs from the previous saved one, or null when there is nothing to compare. */
    data class Done(val report: ScanReport, val diff: ReportDiff? = null) : ScanUiState
}

class ScanViewModel @Inject constructor(
    private val engine: ScanEngine,
    private val scoreCalculator: ScoreCalculator,
    private val recorder: ScanRecorder,
) : ViewModel() {
    private val _state = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    private val _rerunning = MutableStateFlow<Set<CheckId>>(emptySet())

    /** Checks that are being run again from the detail sheet. */
    val rerunning: StateFlow<Set<CheckId>> = _rerunning.asStateFlow()

    private var job: Job? = null

    init {
        scan()
    }

    fun scan() {
        if (job?.isActive == true) return
        job = viewModelScope.launch {
            try {
                engine.scan().collect { event ->
                    when (event) {
                        is ScanEvent.Started -> _state.value = ScanUiState.Scanning(0, event.total, emptyList())
                        is ScanEvent.CheckStarted -> _state.update { current ->
                            if (current is ScanUiState.Scanning) {
                                current.copy(
                                    running = current.running + event.id,
                                )
                            } else {
                                current
                            }
                        }
                        is ScanEvent.CheckCompleted -> _state.update { current ->
                            val scanning = current as? ScanUiState.Scanning
                            ScanUiState.Scanning(
                                event.completed,
                                event.total,
                                scanning?.results.orEmpty() + event.result,
                                scanning?.running.orEmpty() - event.result.id,
                            )
                        }
                        is ScanEvent.Finished ->
                            _state.value =
                                ScanUiState.Done(event.report, recorder.record(event.report))
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.value = ScanUiState.Failed
            }
        }
    }

    /** Runs one check again and recomputes the score without repeating the whole scan. */
    fun rerun(id: CheckId) {
        val before = (_state.value as? ScanUiState.Done) ?: return
        if (id in _rerunning.value) return
        _rerunning.update { it + id }
        viewModelScope.launch {
            try {
                val fresh = engine.runOne(id) ?: return@launch
                _state.update { current ->
                    // A new scan may have started while this check ran; its state wins.
                    if (current !is ScanUiState.Done || current.report.id != before.report.id) return@update current
                    val results = current.report.results.map { if (it.id == id) fresh else it }
                    val updated = current.report.copy(results = results, score = scoreCalculator.calculate(results))
                    ScanUiState.Done(updated, current.diff)
                }
            } finally {
                _rerunning.update { it - id }
            }
        }
    }
}
