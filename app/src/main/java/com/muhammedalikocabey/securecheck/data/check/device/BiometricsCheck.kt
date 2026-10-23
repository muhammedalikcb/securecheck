package com.muhammedalikocabey.securecheck.data.check.device

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.os.Build
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class BiometricsCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.BIOMETRICS

    override suspend fun run(): CheckResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED, listOf("requires API 30"))
        }
        val manager = context.getSystemService(BiometricManager::class.java)
            ?: return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED)
        return when (val code = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> CheckResult.passed(id, listOf("class 3 biometric enrolled"))
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> CheckResult.info(
                id,
                listOf("hardware present, none enrolled"),
            )
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> CheckResult.info(
                id,
                listOf("no class 3 biometric hardware"),
            )
            else -> CheckResult.unavailable(id, UnavailableReason.ERROR, listOf("canAuthenticate=$code"))
        }
    }
}
