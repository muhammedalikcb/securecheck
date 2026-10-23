package com.muhammedalikocabey.securecheck.data.check.device

import android.content.Context
import android.provider.Settings
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class DeveloperOptionsCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.DEVELOPER_OPTIONS

    override suspend fun run(): CheckResult {
        val enabled =
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1
        return if (enabled) {
            CheckResult.finding(
                id,
                listOf("development_settings_enabled=1"),
            )
        } else {
            CheckResult.passed(id)
        }
    }
}

class UsbDebuggingCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.USB_DEBUGGING

    override suspend fun run(): CheckResult {
        val resolver = context.contentResolver
        val usb = Settings.Global.getInt(resolver, Settings.Global.ADB_ENABLED, 0) == 1
        val wireless = Settings.Global.getInt(resolver, "adb_wifi_enabled", 0) == 1
        val evidence = buildList {
            if (usb) add("adb_enabled=1")
            if (wireless) add("adb_wifi_enabled=1")
        }
        return if (evidence.isEmpty()) CheckResult.passed(id) else CheckResult.finding(id, evidence)
    }
}
