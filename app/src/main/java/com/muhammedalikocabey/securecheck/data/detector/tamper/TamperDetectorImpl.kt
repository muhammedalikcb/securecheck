package com.muhammedalikocabey.securecheck.data.detector.tamper

import android.content.Context
import android.os.Build
import com.muhammedalikocabey.securecheck.common.SecurityConstants
import com.muhammedalikocabey.securecheck.data.detector.signature.ISignatureValidator
import javax.inject.Inject

/**
 * Uygulamanın package adı ve imzasını kontrol eder
 * Ayrıca cihazın emülatör veya değişmiş bir ortamda çalışıp çalışmadığını sistem özellikleriyle analiz eder
 *
 * Implementation of ITamperDetector that compares expected package name and signature validity
 * Also checks suspicious build fingerprints to detect emulated or tampered environments
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class TamperDetectorImpl @Inject constructor(
    private val signatureValidator: ISignatureValidator
) : ITamperDetector {

    /**
     * Package adı, imza geçerliliği ve sistem bilgilerine göre analiz yapar
     *
     * Detects tampering via package name mismatch, invalid signature, or generic system builds
     */
    override fun isAppTampered(context: Context): Boolean {
        val packageValid = context.packageName == SecurityConstants.EXPECTED_PACKAGE
        val signatureValid = signatureValidator.isSignatureValid(context)

        val suspiciousBuild = listOf(
            Build.FINGERPRINT,
            Build.MODEL,
            Build.BRAND,
            Build.DEVICE,
            Build.PRODUCT,
            Build.HOST,
            Build.USER
        ).any { it.contains("generic", ignoreCase = true) || it.contains("sdk", ignoreCase = true) }

        return !(signatureValid && packageValid) || suspiciousBuild
    }
}