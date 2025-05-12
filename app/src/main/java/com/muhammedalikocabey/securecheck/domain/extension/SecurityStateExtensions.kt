/**
 * SecurityState modeline ait çeşitli yardımcı fonksiyonları içeren Kotlin extension dosyası
 * Risk skoru hesaplama, geçilen kontrol sayısı ve toplam kontrol sayısını içerir
 *
 * Kotlin extension functions for SecurityState
 * Provides logic for calculating passed checks, total checks, and risk level
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
package com.muhammedalikocabey.securecheck.domain.extension

import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import com.muhammedalikocabey.securecheck.domain.usecase.WebViewSecurityResult

/**
 * Cihaz güvenlik seviyesini hesaplayarak `Safe`, `Medium`, `High` olarak döner
 *
 * Returns security risk level as one of: Safe, Medium, High
 */
val SecurityState.riskLevel: String
    get() {

        val scoreRatio = calculatePassedCheckCount().toDouble() / totalCheckCount()

        return when {
            scoreRatio > 0.85 -> "Safe"
            scoreRatio > 0.6 -> "Medium"
            else -> "High"
        }
    }

/**
 * Geçilen güvenlik kontrolü sayısını hesaplar
 *
 * Calculates number of passed security checks
 */
fun SecurityState.calculatePassedCheckCount(): Int {
    return listOf(
        !isRooted,
        !isDebuggerAttached,
        isFromPlayStore,
        isSignatureValid,
        ctsProfileMatch,
        basicIntegrity,
        isGenuine,
        !isTampered,
        isObfuscated,
        isStorageEncrypted,
        hasExportedComponents,
        isSslPinningPassed,
        isSafePendingIntent,
        !isIntentHijackable,
        !isDebuggable,
        !isEmulator,
        !isVpnActive,
        !isMockLocationEnabled
    ).count { it }
}

/**
 * Uygulamada toplam kaç güvenlik kontrolünün yapıldığını hesaplar
 *
 * Returns the total number of security checks considered in the system
 */
fun SecurityState.totalCheckCount(): Int {
    var count = listOf(
        isRooted,
        isDebuggerAttached,
        isFromPlayStore,
        isSignatureValid,
        ctsProfileMatch,
        basicIntegrity,
        isGenuine,
        isTampered,
        isObfuscated,
        isStorageEncrypted,
        hasExportedComponents,
        isSslPinningPassed,
        isSafePendingIntent,
        !isIntentHijackable,
        !isDebuggable,
        isDexLoaded,
        isReflectionUsed,
        !isFridaDetected,
        !isXposed,
        !isEmulator,
        !isVpnActive,
        !isMockLocationEnabled
    ).size

    if (webViewSecurity != null) count += 3

    return count
}

/**
 * WebView ayarlarının güvenli olup olmadığını kontrol eder
 *
 * Checks if WebView configuration is considered safe
 */
fun WebViewSecurityResult.isSafe(): Boolean =
    !isJsEnabled && !isFileAccessAllowed && !hasInsecureJsInterface