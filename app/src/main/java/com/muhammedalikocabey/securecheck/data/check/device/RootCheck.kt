package com.muhammedalikocabey.securecheck.data.check.device

import android.content.pm.PackageManager
import com.muhammedalikocabey.securecheck.data.system.BuildFields
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.Signal
import com.muhammedalikocabey.securecheck.data.system.SystemProps
import com.muhammedalikocabey.securecheck.data.system.confidence
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class RootCheck @Inject constructor(
    private val files: ProcFiles,
    private val props: SystemProps,
    private val packages: PackageLookup,
    private val build: BuildFields,
) : SecurityCheck {
    override val id = CheckId.ROOT

    override suspend fun run(): CheckResult {
        val signals = buildList {
            SU_PATHS.filter(files::exists).forEach { add(Signal("su binary: $it", 3)) }
            ROOT_PACKAGES.filter(packages::isInstalled).forEach { add(Signal("root manager package: $it", 2)) }
            writableSystemMounts(files.readLines("/proc/mounts").orEmpty())
                .forEach { add(Signal("writable mount: $it", 3)) }
            if (build.tags.contains("test-keys")) add(Signal("build tags: test-keys", 1))
            if (props.get("ro.debuggable") == "1") add(Signal("ro.debuggable=1", 1))
        }
        val confidence = signals.confidence() ?: return CheckResult.passed(id)
        return CheckResult.finding(id, signals.map { it.description }, confidence)
    }

    companion object {
        private val SU_PATHS = listOf(
            "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su", "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su", "/data/local/su", "/data/local/bin/su", "/data/local/xbin/su",
            "/su/bin/su", "/vendor/bin/su",
        )
        private val ROOT_PACKAGES = listOf(
            "com.topjohnwu.magisk", "io.github.vvb2060.magisk", "me.weishu.kernelsu", "me.bmax.apatch",
            "com.rifsxd.ksunext", "eu.chainfire.supersu", "com.koushikdutta.superuser", "com.noshufou.android.su",
            "com.thirdparty.superuser", "com.yellowes.su", "com.kingouser.com",
        )
        private val PROTECTED_MOUNTS = setOf("/system", "/vendor", "/product", "/system_ext")

        /** Partitions that should be read-only but are mounted `rw` in `/proc/mounts` format. */
        fun writableSystemMounts(mounts: List<String>): List<String> = mounts.mapNotNull { line ->
            val parts = line.split(' ')
            val mountPoint = parts.getOrNull(1)
            val options = parts.getOrNull(3)?.split(',').orEmpty()
            mountPoint?.takeIf { it in PROTECTED_MOUNTS && "rw" in options }
        }
    }
}

/** Package presence lookup, wrapped so detector logic does not depend on a Context. */
fun interface PackageLookup {
    fun isInstalled(packageName: String): Boolean
}

class AndroidPackageLookup @Inject constructor(private val packageManager: PackageManager) : PackageLookup {
    override fun isInstalled(packageName: String): Boolean = try {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}
