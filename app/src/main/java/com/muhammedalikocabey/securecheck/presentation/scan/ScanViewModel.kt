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

    private var job: Job? = null

    init {
        scan()
    }

    fun scan() {
        if (job?.isActive == true) return
        job = viewModelScope.launch {
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
        }
    }

    /** Runs one check again and recomputes the score without repeating the whole scan. */
    fun rerun(id: CheckId) {
        val current = (_state.value as? ScanUiState.Done)?.report ?: return
        viewModelScope.launch {
            val fresh = engine.runOne(id) ?: return@launch
            val results = current.results.map { if (it.id == id) fresh else it }
            val updated = current.copy(results = results, score = scoreCalculator.calculate(results))
            _state.value = ScanUiState.Done(updated, (_state.value as? ScanUiState.Done)?.diff)
        }
    }
}
