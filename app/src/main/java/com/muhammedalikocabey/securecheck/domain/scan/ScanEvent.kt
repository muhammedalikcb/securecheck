package com.muhammedalikocabey.securecheck.domain.scan

import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.ScanReport

sealed interface ScanEvent {
    data class Started(val total: Int) : ScanEvent

    data class CheckCompleted(val result: CheckResult, val completed: Int, val total: Int) : ScanEvent

    data class Finished(val report: ScanReport) : ScanEvent
}
