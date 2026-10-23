package com.muhammedalikocabey.securecheck.data.check.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.inject.Inject

/** Creates a throwaway AES key and asks where the keystore actually put it. */
class HardwareKeystoreCheck @Inject constructor() : SecurityCheck {
    override val id = CheckId.HW_KEYSTORE

    override suspend fun run(): CheckResult = try {
        val key = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).run {
            init(
                KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build(),
            )
            generateKey()
        }
        try {
            val keyInfo = SecretKeyFactory.getInstance(
                key.algorithm,
                ANDROID_KEYSTORE,
            ).getKeySpec(key, KeyInfo::class.java) as KeyInfo
            describe(keyInfo)
        } finally {
            KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }.deleteEntry(ALIAS)
        }
    } catch (_: GeneralSecurityException) {
        CheckResult.unavailable(id, UnavailableReason.ERROR)
    }

    private fun describe(info: KeyInfo): CheckResult {
        val (hardware, label) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val level = info.securityLevel
            (
                level == KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT ||
                    level == KeyProperties.SECURITY_LEVEL_STRONGBOX
                ) to
                when (level) {
                    KeyProperties.SECURITY_LEVEL_STRONGBOX -> "StrongBox"
                    KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT -> "trusted environment"
                    else -> "software"
                }
        } else {
            @Suppress("DEPRECATION")
            info.isInsideSecureHardware.let { it to if (it) "secure hardware" else "software" }
        }
        val evidence = listOf("key security level: $label")
        return if (hardware) CheckResult.passed(id, evidence) else CheckResult.finding(id, evidence)
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val ALIAS = "securecheck_keystore_probe"
    }
}

class BackupCheck @Inject constructor(private val context: Context) : SecurityCheck {
    override val id = CheckId.BACKUP

    override suspend fun run(): CheckResult {
        val allowed = context.applicationInfo.flags and ApplicationInfo.FLAG_ALLOW_BACKUP != 0
        val evidence = listOf("FLAG_ALLOW_BACKUP=$allowed")
        return if (allowed) CheckResult.finding(id, evidence) else CheckResult.passed(id, evidence)
    }
}
