package com.muhammedalikocabey.securecheck.data.detector.signature

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.muhammedalikocabey.securecheck.common.SecurityConstants
import com.muhammedalikocabey.securecheck.core.crypto.ISecureCipher
import java.security.MessageDigest
import javax.inject.Inject

/**
 * SHA-256 ile şifrelenmiş beklenen imza hash'ini çözüp APK'nın gerçek imzası ile karşılaştıran sınıf
 * Android API seviyesine göre uygun imza bilgisini alır
 *
 * Implementation of ISignatureValidator that compares the decrypted signature hash with actual APK signature
 * Supports both modern and legacy signing methods depending on Android version
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class SignatureValidatorImpl @Inject constructor(
    private val cipher: ISecureCipher
) : ISignatureValidator {

    /**
     * APK'nın imza hash'ini çözerek sistemden alınan gerçek hash ile karşılaştırır
     *
     * Decrypts expected signature hash and compares with the actual APK hash
     */
    override fun isSignatureValid(context: Context): Boolean {
        return try {
            val expectedSha256 = cipher.decrypt(SecurityConstants.ENCRYPTED_SIGNATURE_HASH)
            val actualSha256 = getCurrentApkSha256(context)

            actualSha256?.equals(expectedSha256, ignoreCase = true) == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * API seviyesine göre APK'nın gerçek imzasının SHA-256 hash'ini döner
     *
     * Returns current APK's SHA-256 hash depending on Android API level
     */
    @Suppress("DEPRECATION")
    private fun getCurrentApkSha256(context: Context): String? {
        val packageManager = context.packageManager
        val packageName = context.packageName

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signer = info.signingInfo?.apkContentsSigners?.firstOrNull()
                signer?.toByteArray()?.toSha256()
            } else {
                val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                val signer = info.signatures?.firstOrNull()
                signer?.toByteArray()?.toSha256()
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Verilen byte dizisini SHA-256 hash string'e dönüştürür
     *
     * Converts given byte array to a SHA-256 hash string
     */
    private fun ByteArray.toSha256(): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(this)
            .joinToString("") { "%02x".format(it) }
    }
}
