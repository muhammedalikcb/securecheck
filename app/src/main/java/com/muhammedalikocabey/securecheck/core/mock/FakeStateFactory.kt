package com.muhammedalikocabey.securecheck.core.mock

import androidx.annotation.VisibleForTesting
import com.muhammedalikocabey.securecheck.BuildConfig
import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import com.muhammedalikocabey.securecheck.domain.usecase.WebViewSecurityResult

/**
 * Compose UI önizlemeleri (Preview) ve manuel testler için güvenli veya riskli durum senaryoları üretir.
 *
 * Generates fake `SecurityState` instances for Compose UI Previews and testing scenarios.
 * Provides both safe and risky security profiles for demonstration.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@VisibleForTesting
object FakeStateFactory {

    /**
     * Tüm kontrollerin başarılı olduğu güvenli örnek durum.
     *
     * A safe security state where all checks pass successfully.
     */
    fun createSafeState(): SecurityState {
        if (!BuildConfig.DEBUG) throw IllegalStateException("FakeStateFactory only for debug usage.")
        return SecurityState(
            isRooted = false,
            isDebuggerAttached = false,
            isFromPlayStore = true,
            isSignatureValid = true,
            ctsProfileMatch = true,
            basicIntegrity = true,
            isGenuine = true,
            isTampered = false,
            isObfuscated = true,
            isStorageEncrypted = true,
            isSafePendingIntent = true,
            hasExportedComponents = false,
            isSslPinningPassed = true,
            isIntentHijackable = false,
            isDebuggable = false,
            webViewSecurity = WebViewSecurityResult(
                isJsEnabled = false,
                isFileAccessAllowed = false,
                hasInsecureJsInterface = false
            ),
            isDexLoaded = false,
            isReflectionUsed = false,
            isFridaDetected = false,
            isXposed = false,
            isEmulator = false,
            isVpnActive = false,
            isMockLocationEnabled = false
        )
    }

    /**
     * Tüm güvenlik açıklarının tespit edildiği riskli örnek durum.
     *
     * A risky security state where all possible vulnerabilities are simulated.
     */
    fun createRiskyState(): SecurityState {
        if (!BuildConfig.DEBUG) throw IllegalStateException("FakeStateFactory only for debug usage.")
        return SecurityState(
            isRooted = true,
            isDebuggerAttached = true,
            isFromPlayStore = false,
            isSignatureValid = false,
            ctsProfileMatch = false,
            basicIntegrity = false,
            isGenuine = false,
            isTampered = true,
            isObfuscated = false,
            isStorageEncrypted = false,
            isSafePendingIntent = true,
            hasExportedComponents = true,
            isSslPinningPassed = false,
            isIntentHijackable = true,
            isDebuggable = true,
            webViewSecurity = WebViewSecurityResult(
                isJsEnabled = true,
                isFileAccessAllowed = true,
                hasInsecureJsInterface = true
            ),
            isDexLoaded = true,
            isReflectionUsed = true,
            isFridaDetected = true,
            isXposed = true,
            isEmulator = true,
            isVpnActive = true,
            isMockLocationEnabled = true
        )
    }
}
