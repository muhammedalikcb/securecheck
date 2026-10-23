package com.muhammedalikocabey.securecheck.data.check.device

import android.app.KeyguardManager
import android.content.Context
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class ScreenLockCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.SCREEN_LOCK

    override suspend fun run(): CheckResult {
        val keyguard = context.getSystemService(KeyguardManager::class.java)
            ?: return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED)
        return if (keyguard.isDeviceSecure) {
            CheckResult.passed(id)
        } else {
            CheckResult.finding(id, listOf("isDeviceSecure=false"))
        }
    }
}
