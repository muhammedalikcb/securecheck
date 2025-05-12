package com.muhammedalikocabey.securecheck.domain.model

import com.muhammedalikocabey.securecheck.domain.extension.isSafe
import com.muhammedalikocabey.securecheck.domain.usecase.WebViewSecurityResult

/**
 * Tüm güvenlik kontrollerinin sonucunu temsil eden data class
 * Her bir alan bağımsız bir güvenlik kontrolünün çıktısıdır ve UI + log + analiz katmanları tarafından kullanılır
 *
 * Data class representing the result of all security checks
 * Each field corresponds to the result of a specific check and is used across UI, logging, and export layers
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
data class SecurityState (
    val isRooted: Boolean,
    val isDebuggerAttached: Boolean,
    val isFromPlayStore: Boolean,
    val isSignatureValid: Boolean,
    val ctsProfileMatch: Boolean,
    val basicIntegrity: Boolean,
    val isGenuine: Boolean,
    val isTampered: Boolean,
    val isObfuscated: Boolean,
    val isStorageEncrypted: Boolean,
    val hasExportedComponents: Boolean,
    val isSslPinningPassed: Boolean,
    val isSafePendingIntent: Boolean,
    val isIntentHijackable: Boolean,
    val isDebuggable: Boolean,
    val webViewSecurity: WebViewSecurityResult? = null,
    val isDexLoaded: Boolean,
    val isReflectionUsed: Boolean,
    val isFridaDetected: Boolean,
    val isXposed: Boolean,
    val isEmulator: Boolean,
    val isVpnActive: Boolean,
    val isMockLocationEnabled: Boolean,
) {
    /**
     * Tüm kontrollerin başarıyla geçtiği durumlarda true döner
     *
     * Returns true if all security checks have passed successfully
     */
    val overallSafe: Boolean
        get() = listOf(
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
            !hasExportedComponents,
            isSslPinningPassed,
            isSafePendingIntent,
            !isIntentHijackable,
            !isDebuggable,
            (webViewSecurity?.isSafe() ?: true),
            !isDexLoaded,
            !isReflectionUsed,
            !isFridaDetected,
            !isXposed,
            !isEmulator,
            !isVpnActive,
            !isMockLocationEnabled
        ).all { it }
}


