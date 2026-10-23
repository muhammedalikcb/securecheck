package com.muhammedalikocabey.securecheck.data.check.runtime

import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.data.system.DebuggerProbe
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class DebuggerCheck @Inject constructor(
    private val probe: DebuggerProbe,
    private val files: ProcFiles,
    private val info: BuildInfo,
) : SecurityCheck {
    override val id = CheckId.DEBUGGER

    override suspend fun run(): CheckResult {
        val evidence = buildList {
            if (probe.isAttached()) add("Debug.isDebuggerConnected=true")
            tracerPid(files.readLines("/proc/self/status").orEmpty())?.takeIf { it != 0 }?.let { add("TracerPid=$it") }
        }
        return when {
            evidence.isEmpty() -> CheckResult.passed(id)
            info.isDebug -> CheckResult.expected(id, evidence)
            else -> CheckResult.finding(id, evidence)
        }
    }

    companion object {
        fun tracerPid(statusLines: List<String>): Int? =
            statusLines.firstOrNull { it.startsWith("TracerPid:") }?.substringAfter(':')?.trim()?.toIntOrNull()
    }
}
