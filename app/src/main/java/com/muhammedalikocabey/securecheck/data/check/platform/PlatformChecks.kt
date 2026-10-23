package com.muhammedalikocabey.securecheck.data.check.platform

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityManager
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

data class ComponentRecord(val kind: String, val name: String, val exported: Boolean, val permission: String?)

class ExportedComponentsCheck @Inject constructor(
    private val packageManager: PackageManager,
    private val info: BuildInfo,
) : SecurityCheck {
    override val id = CheckId.EXPORTED_COMPONENTS

    override suspend fun run(): CheckResult {
        val flags = PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES or
            PackageManager.GET_RECEIVERS or PackageManager.GET_PROVIDERS
        val pkg = try {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(info.packageName, flags)
        } catch (_: PackageManager.NameNotFoundException) {
            return CheckResult.unavailable(id, UnavailableReason.ERROR)
        }
        val records = buildList {
            pkg.activities?.forEach { add(ComponentRecord("activity", it.name, it.exported, null)) }
            pkg.services?.forEach { add(ComponentRecord("service", it.name, it.exported, it.permission)) }
            pkg.receivers?.forEach { add(ComponentRecord("receiver", it.name, it.exported, it.permission)) }
            pkg.providers?.forEach {
                add(
                    ComponentRecord(
                        "provider",
                        it.name,
                        it.exported,
                        it.readPermission ?: it.writePermission,
                    ),
                )
            }
        }
        val launcher = packageManager.getLaunchIntentForPackage(info.packageName)?.component?.className
        val exposed = exposed(records, launcher)
        return if (exposed.isEmpty()) {
            CheckResult.passed(id, listOf("exported components: ${records.count { it.exported }}"))
        } else {
            CheckResult.finding(id, exposed.map { "${it.kind}: ${it.name}" })
        }
    }

    companion object {
        /** Exported components other apps can reach without holding a permission, minus the launcher entry point. */
        fun exposed(records: List<ComponentRecord>, launcherClass: String?): List<ComponentRecord> =
            records.filter { it.exported && it.permission == null && it.name != launcherClass }
    }
}

class AccessibilityServicesCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.ACCESSIBILITY_SERVICES

    override suspend fun run(): CheckResult {
        val manager = context.getSystemService(AccessibilityManager::class.java)
            ?: return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED)
        val third = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .mapNotNull { it.resolveInfo?.serviceInfo?.applicationInfo }
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { it.packageName }
            .distinct()
        // Screen readers and switch-access tools are legitimate, so a hit is reported with reduced confidence.
        return if (third.isEmpty()) {
            CheckResult.passed(id)
        } else {
            CheckResult.finding(id, third.map { "enabled service from: $it" }, Confidence.MEDIUM)
        }
    }
}
