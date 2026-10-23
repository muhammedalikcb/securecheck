package com.muhammedalikocabey.securecheck.data.check.app

import android.content.Context
import android.content.pm.ApplicationInfo
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class DebuggableCheck @Inject constructor(private val context: Context, private val info: BuildInfo) : SecurityCheck {
    override val id = CheckId.DEBUGGABLE

    override suspend fun run(): CheckResult {
        val debuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        val evidence = listOf("FLAG_DEBUGGABLE=$debuggable")
        return when {
            !debuggable -> CheckResult.passed(id, evidence)
            info.isDebug -> CheckResult.expected(id, evidence)
            else -> CheckResult.finding(id, evidence)
        }
    }
}

/** Never referenced by name in the keep rules, so a shrunk release build renames it. */
internal class ObfuscationProbe

class CodeShrinkingCheck @Inject constructor(private val info: BuildInfo) : SecurityCheck {
    override val id = CheckId.CODE_SHRINKING

    override suspend fun run(): CheckResult {
        val name = ObfuscationProbe::class.java.simpleName
        val renamed = name != EXPECTED_NAME
        val evidence = listOf("probe class name: $name")
        return when {
            renamed -> CheckResult.passed(id, evidence)
            info.isDebug -> CheckResult.expected(id, evidence)
            else -> CheckResult.finding(id, evidence)
        }
    }

    private companion object {
        const val EXPECTED_NAME = "ObfuscationProbe"
    }
}

/**
 * Compares the target SDK the build was compiled with against the one the installed package declares.
 * Re-signing a modified APK with a lower target SDK is a common way to switch off newer platform protections.
 */
class TargetSdkCheck @Inject constructor(private val context: Context, private val info: BuildInfo) : SecurityCheck {
    override val id = CheckId.TARGET_SDK

    override suspend fun run(): CheckResult {
        val installed = context.applicationInfo.targetSdkVersion
        val evidence = listOf("built for: ${info.builtTargetSdk}", "installed: $installed")
        return if (installed < info.builtTargetSdk) {
            CheckResult.finding(id, evidence, Confidence.MEDIUM, Severity.HIGH)
        } else {
            CheckResult.passed(id, evidence)
        }
    }
}
