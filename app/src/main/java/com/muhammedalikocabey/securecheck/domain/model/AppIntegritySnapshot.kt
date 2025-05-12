package com.muhammedalikocabey.securecheck.domain.model

import com.muhammedalikocabey.securecheck.domain.extension.riskLevel
import org.json.JSONObject
import javax.inject.Inject

/**
 * SecurityState nesnesini JSON formatında serialize ederek cihazın bütünlük özetini üretir
 * Dışa aktarılan raporlarda, tüm güvenlik kontrollerinin çıktısı merkezi olarak bu sınıf üzerinden alınır
 *
 * Converts SecurityState to a JSON-formatted integrity snapshot
 * Used in exporting centralized reports of all security checks
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class AppIntegritySnapshot @Inject constructor() {

    /**
     * Verilen güvenlik durumunu okunabilir JSON formatına çevirir
     *
     * Converts the given security state into a formatted JSON string
     *
     * @param state Uygulama güvenlik durumu
     * @return Okunabilir JSON çıktısı
     */
    fun toJson(state: SecurityState): String {
        return JSONObject().apply {
            put("isRooted", state.isRooted)
            put("isDebuggerAttached", state.isDebuggerAttached)
            put("isFromPlayStore", state.isFromPlayStore)
            put("isSignatureValid", state.isSignatureValid)
            put("ctsProfileMatch", state.ctsProfileMatch)
            put("basicIntegrity", state.basicIntegrity)
            put("isGenuine", state.isGenuine)
            put("isTampered", state.isTampered)
            put("isObfuscationDetected", state.isObfuscated)
            put("isStorageEncrypted", state.isStorageEncrypted)
            put("isSafeExportedComponents", !state.hasExportedComponents)
            put("isSslPinningPassed", state.isSslPinningPassed)
            put("isSafePendingIntent", state.isSafePendingIntent)
            put("isIntentHijackable", state.isIntentHijackable)
            put("isDebuggable", state.isDebuggable)
            state.webViewSecurity?.let {
                put("webView", JSONObject().apply {
                    put("isJsEnabled", it.isJsEnabled)
                    put("isFileAccessAllowed", it.isFileAccessAllowed)
                    put("hasInsecureJsInterface", it.hasInsecureJsInterface)
                })
            }
            put("isDexLoaded", state.isDexLoaded)
            put("isReflectionUsed", state.isReflectionUsed)
            put("isFridaDetected", state.isFridaDetected)
            put("riskLevel", state.riskLevel)
            put("overallSafe", state.overallSafe)
            put("timestamp", System.currentTimeMillis())
        }.toString(4)
    }
}
