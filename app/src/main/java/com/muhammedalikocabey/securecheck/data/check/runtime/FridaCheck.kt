package com.muhammedalikocabey.securecheck.data.check.runtime

import com.muhammedalikocabey.securecheck.data.system.PortProbe
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.Signal
import com.muhammedalikocabey.securecheck.data.system.confidence
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

/**
 * Looks for the usual traces of a Frida agent. A determined attacker can rename all of them, so a clean result lowers
 * risk but is not proof; the README says so.
 */
class FridaCheck @Inject constructor(private val files: ProcFiles, private val ports: PortProbe) : SecurityCheck {
    override val id = CheckId.FRIDA

    override suspend fun run(): CheckResult {
        val maps = files.readLines("/proc/self/maps").orEmpty()
        val threads = files.listNames("/proc/self/task").orEmpty().mapNotNull {
            files.readLines("/proc/self/task/$it/comm")?.firstOrNull()?.trim()
        }
        val signals = signals(
            maps = maps,
            threadNames = threads,
            presentFiles = SERVER_FILES.filter(files::exists),
            portOpen = ports.isOpen("127.0.0.1", FRIDA_PORT, PORT_TIMEOUT_MS),
        )
        val confidence = signals.confidence() ?: return CheckResult.passed(id)
        return CheckResult.finding(id, signals.map { it.description }, confidence)
    }

    companion object {
        private const val FRIDA_PORT = 27042
        private const val PORT_TIMEOUT_MS = 200
        private val SERVER_FILES = listOf("/data/local/tmp/frida-server", "/data/local/tmp/re.frida.server")
        private val MAP_NEEDLES = listOf("frida-agent", "frida-gadget", "frida_agent", "linjector")
        private val STRONG_THREADS = setOf("gum-js-loop", "pool-frida", "frida-main-loop")
        private val WEAK_THREADS = setOf("gmain", "gdbus")

        fun signals(
            maps: List<String>,
            threadNames: List<String>,
            presentFiles: List<String>,
            portOpen: Boolean,
        ): List<Signal> = buildList {
            ProcessMaps.containsAny(maps, MAP_NEEDLES).forEach { add(Signal("mapped library: $it", 3)) }
            threadNames.filter { it in STRONG_THREADS }.distinct().forEach { add(Signal("thread name: $it", 3)) }
            threadNames.filter { it in WEAK_THREADS }.distinct().forEach { add(Signal("thread name: $it", 1)) }
            presentFiles.forEach { add(Signal("file: $it", 3)) }
            if (portOpen) add(Signal("local port 27042 accepts connections", 2))
        }
    }
}
