package com.muhammedalikocabey.securecheck.data.check.runtime

import android.annotation.SuppressLint
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.Signal
import com.muhammedalikocabey.securecheck.data.system.confidence
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

/** Flags native libraries mapped from places an installed app has no business loading code from. */
class InjectedCodeCheck @Inject constructor(private val files: ProcFiles, private val info: BuildInfo) :
    SecurityCheck {
    override val id = CheckId.INJECTED_CODE

    override suspend fun run(): CheckResult {
        val maps = files.readLines("/proc/self/maps") ?: return CheckResult.passed(id)
        val signals = signals(ProcessMaps.paths(maps), info.packageName)
        val confidence = signals.confidence() ?: return CheckResult.passed(id)
        return CheckResult.finding(id, signals.map { it.description }, confidence)
    }

    companion object {
        // These are match patterns for foreign mapped libraries, not paths this app opens.
        @SuppressLint("SdCardPath")
        private val UNTRUSTED_PREFIXES = listOf("/data/local/tmp/", "/sdcard/", "/storage/", "/mnt/")
        private val CODE_SUFFIXES = listOf(".so", ".dex", ".jar", ".apk", ".odex", ".vdex", ".oat")
        private const val DELETED_SUFFIX = " (deleted)"
        private val APP_DATA = Regex("^/data/(?:data|user/\\d+)/([^/]+)/")

        fun signals(paths: Set<String>, ownPackage: String): List<Signal> = buildList {
            // Fonts, databases and other data files are legitimately mapped from shared locations; only code counts.
            for (path in paths.filter(::isCode)) {
                val foreignData = APP_DATA.find(path)?.groupValues?.get(1)?.takeIf { it != ownPackage }
                when {
                    UNTRUSTED_PREFIXES.any(path::startsWith) -> add(Signal("mapped from: $path", 3))
                    foreignData != null -> add(Signal("mapped from another app: $path", 3))
                    path.endsWith(".so$DELETED_SUFFIX") -> add(Signal("deleted library still mapped: $path", 2))
                }
            }
        }

        private fun isCode(path: String) = path.removeSuffix(DELETED_SUFFIX).let { name ->
            CODE_SUFFIXES.any(name::endsWith)
        }
    }
}
