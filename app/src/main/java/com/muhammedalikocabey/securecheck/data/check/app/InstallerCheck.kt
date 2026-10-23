package com.muhammedalikocabey.securecheck.data.check.app

import android.content.pm.PackageManager
import android.os.Build
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

fun interface InstallerSource {
    fun installer(): String?
}

class AndroidInstallerSource @Inject constructor(
    private val packageManager: PackageManager,
    private val info: BuildInfo,
) : InstallerSource {
    @Suppress("DEPRECATION")
    override fun installer(): String? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(info.packageName).installingPackageName
        } else {
            packageManager.getInstallerPackageName(info.packageName)
        }
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }
}

class InstallerCheck @Inject constructor(private val source: InstallerSource, private val info: BuildInfo) :
    SecurityCheck {
    override val id = CheckId.INSTALLER

    override suspend fun run(): CheckResult = evaluate(id, source.installer(), info.isDebug)

    companion object {
        val TRUSTED_STORES = setOf(
            "com.android.vending",
            "com.sec.android.app.samsungapps",
            "com.amazon.venezia",
            "com.huawei.appmarket",
            "com.xiaomi.mipicks",
        )

        fun evaluate(id: CheckId, installer: String?, isDebugBuild: Boolean): CheckResult {
            val evidence = listOf("installer: ${installer ?: "none"}")
            return when {
                installer in TRUSTED_STORES -> CheckResult.passed(id, evidence)
                isDebugBuild -> CheckResult.expected(id, evidence)
                // A package manager or file manager install is common and legitimate, so this stays a medium-confidence signal.
                else -> CheckResult.finding(id, evidence, Confidence.MEDIUM)
            }
        }
    }
}
