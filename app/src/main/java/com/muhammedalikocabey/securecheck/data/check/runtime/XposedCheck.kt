package com.muhammedalikocabey.securecheck.data.check.runtime

import com.muhammedalikocabey.securecheck.data.check.device.PackageLookup
import com.muhammedalikocabey.securecheck.data.system.ClassLookup
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.Signal
import com.muhammedalikocabey.securecheck.data.system.confidence
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class XposedCheck @Inject constructor(
    private val files: ProcFiles,
    private val classes: ClassLookup,
    private val packages: PackageLookup,
) : SecurityCheck {
    override val id = CheckId.XPOSED

    override suspend fun run(): CheckResult {
        val maps = files.readLines("/proc/self/maps").orEmpty()
        val stack = Throwable().stackTrace.map { it.className }
        val signals = signals(
            maps = maps,
            classPath = System.getenv("CLASSPATH").orEmpty(),
            loadableClasses = BRIDGE_CLASSES.filter(classes::exists),
            stackClassNames = stack,
            managers = MANAGER_PACKAGES.filter(packages::isInstalled),
        )
        val confidence = signals.confidence() ?: return CheckResult.passed(id)
        return CheckResult.finding(id, signals.map { it.description }, confidence)
    }

    companion object {
        private val BRIDGE_CLASSES = listOf(
            "de.robv.android.xposed.XposedBridge",
            "de.robv.android.xposed.XC_MethodHook",
        )
        private val MAP_NEEDLES = listOf("XposedBridge", "lspd", "lsposed", "edxposed", "libriru")
        private val MANAGER_PACKAGES = listOf(
            "de.robv.android.xposed.installer",
            "org.lsposed.manager",
            "io.github.lsposed.manager",
            "org.meowcat.edxposed.manager",
            "com.solohsu.android.edxp.manager",
        )

        fun signals(
            maps: List<String>,
            classPath: String,
            loadableClasses: List<String>,
            stackClassNames: List<String>,
            managers: List<String>,
        ): List<Signal> = buildList {
            ProcessMaps.containsAny(maps, MAP_NEEDLES).forEach { add(Signal("mapped library: $it", 3)) }
            if (classPath.contains("XposedBridge", ignoreCase = true)) add(Signal("CLASSPATH names XposedBridge", 3))
            loadableClasses.forEach { add(Signal("class loadable: $it", 3)) }
            stackClassNames.filter { it.contains("xposed", ignoreCase = true) }.distinct()
                .forEach { add(Signal("stack frame: $it", 3)) }
            managers.forEach { add(Signal("manager package: $it", 2)) }
        }
    }
}
