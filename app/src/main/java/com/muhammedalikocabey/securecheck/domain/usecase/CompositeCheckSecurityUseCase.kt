package com.muhammedalikocabey.securecheck.domain.usecase

import android.Manifest
import android.content.Context
import android.content.Intent
import android.webkit.WebView
import androidx.annotation.RequiresPermission
import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Tüm güvenlik UseCase'lerini çalıştırarak sonuçları SecurityState modeline dönüştürür
 *
 * Executes all individual security use cases and combines their results into a SecurityState
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CompositeCheckSecurityUseCase @Inject constructor(
    private val input: SecurityCheckInputs
) {
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend operator fun invoke(
        context: Context,
        intent: Intent? = null,
        webView: WebView? = null,
        target: KClass<*>? = null
    ): SecurityState {
        val isRooted = input.checkRootStatus()
        val isDebuggerAttached = input.checkDebuggerStatus()
        val isFromPlayStore = input.checkInstaller(context)
        val isSignatureValid = input.checkSignature(context)
        val (cts, integrity) = input.checkSafetyNet(context)
        val isGenuine = input.checkPlayIntegrity(context)
        val isTampered = input.checkTamperStatus(context)
        val isObfuscationDetected = input.checkObfuscationStatus()
        val isStorageEncrypted = input.checkStorageEncrypted()
        val hasPendingIntentLeak = input.checkPendingIntentLeak(context)
        val hasExportedComponents = input.checkComponentExported(context)
        val isSslPinningPassed = input.checkSslPinning()
        val isIntentHijackable = intent?.let { input.checkIntentHijack(context, it) } ?: false
        val isDebuggable = input.checkDebugBuild(context)
        val webViewSecurity = webView?.let {
            input.checkWebViewSecurity(it, isDebuggable)
        }
        val isDexLoaded = input.checkDexLoader()
        val isReflectionUsed = checkReflectionUsage(target)
        val isFridaDetected = input.checkFrida()
        val isXposed = input.checkXposedDetector()
        val isEmulator = input.checkEmulatorUseCase()
        val isVpnUsed = input.checkVpnStatusUseCase(context)
        val isMockLocation = input.checkMockLocationUseCase(context)

        return SecurityState(
            isRooted = isRooted,
            isDebuggerAttached = isDebuggerAttached,
            isFromPlayStore = isFromPlayStore,
            isSignatureValid = isSignatureValid,
            ctsProfileMatch = cts,
            basicIntegrity = integrity,
            isGenuine = isGenuine,
            isTampered = isTampered,
            isObfuscated = isObfuscationDetected,
            isStorageEncrypted = isStorageEncrypted,
            isSafePendingIntent = hasPendingIntentLeak,
            hasExportedComponents = hasExportedComponents,
            isSslPinningPassed = isSslPinningPassed,
            isIntentHijackable = isIntentHijackable,
            isDebuggable = isDebuggable,
            webViewSecurity = webViewSecurity,
            isDexLoaded = isDexLoaded,
            isReflectionUsed = isReflectionUsed,
            isFridaDetected = isFridaDetected,
            isXposed = isXposed,
            isEmulator = isEmulator,
            isVpnActive = isVpnUsed,
            isMockLocationEnabled = isMockLocation
        )
    }

    private fun checkReflectionUsage(target: KClass<*>?): Boolean {
        return input.checkReflection(target ?: SecurityChecker::class)
    }
}