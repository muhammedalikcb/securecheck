package com.muhammedalikocabey.securecheck.data.check.runtime

import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.Signal
import com.muhammedalikocabey.securecheck.data.system.confidence
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class MagiskCheck @Inject constructor(private val files: ProcFiles) : SecurityCheck {
    override val id = CheckId.MAGISK

    override suspend fun run(): CheckResult {
        val signals = signals(
            maps = files.readLines("/proc/self/maps").orEmpty(),
            mountInfo = files.readLines("/proc/self/mountinfo").orEmpty(),
            presentPaths = PATHS.filter(files::exists),
        )
        val confidence = signals.confidence() ?: return CheckResult.passed(id)
        return CheckResult.finding(id, signals.map { it.description }, confidence)
    }

    companion object {
        private val PATHS = listOf("/sbin/.magisk", "/data/adb/magisk", "/data/adb/modules", "/dev/.magisk_unblock")

        fun signals(maps: List<String>, mountInfo: List<String>, presentPaths: List<String>): List<Signal> = buildList {
            if (ProcessMaps.containsAny(maps, listOf("magisk")).isNotEmpty()) add(Signal("magisk in process maps", 3))
            if (ProcessMaps.containsAny(
                    mountInfo,
                    listOf("magisk"),
                ).isNotEmpty()
            ) {
                add(Signal("magisk in mount table", 3))
            }
            presentPaths.forEach { add(Signal("path: $it", 3)) }
        }
    }
}
