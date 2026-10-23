package com.muhammedalikocabey.securecheck.domain.scan

import com.muhammedalikocabey.securecheck.domain.model.AppSummary
import com.muhammedalikocabey.securecheck.domain.model.DeviceSummary

/** Wall clock, replaceable in tests. */
fun interface TimeSource {
    fun nowMs(): Long
}

/** Facts about the device and build the engine stamps onto a report. */
interface ScanEnvironment {
    val isDebugBuild: Boolean

    fun device(): DeviceSummary

    fun app(): AppSummary
}
