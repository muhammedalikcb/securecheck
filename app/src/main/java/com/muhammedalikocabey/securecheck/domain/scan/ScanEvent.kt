package com.muhammedalikocabey.securecheck.domain.scan

import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.ScanReport

sealed interface ScanEvent {
    data class Started(val total: Int) : ScanEvent

    /** Sent when a check gets a slot to run, so the UI can say what is happening right now. */
    data class CheckStarted(val id: CheckId) : ScanEvent

    data class CheckCompleted(val result: CheckResult, val completed: Int, val total: Int) : ScanEvent

    data class Finished(val report: ScanReport) : ScanEvent
}
