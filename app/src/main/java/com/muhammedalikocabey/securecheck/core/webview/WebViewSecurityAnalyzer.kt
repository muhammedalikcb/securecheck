package com.muhammedalikocabey.securecheck.core.webview

import android.webkit.WebView
import javax.inject.Inject

/**
 * WebView bileşeni için güvenlik analizleri sağlar.
 * JavaScript, dosya erişimi ve güvenli olmayan JS interface kullanımı gibi riskli özellikleri tespit eder.
 *
 * Provides security checks for WebView configurations.
 * Detects risky settings such as JavaScript enabled, file access, and insecure JS interfaces.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class WebViewSecurityAnalyzer @Inject constructor() {

    /**
     * WebView içinde JavaScript kullanımının aktif olup olmadığını kontrol eder.
     *
     * Checks whether JavaScript is enabled in the given WebView.
     *
     * @param webView İncelenecek WebView bileşeni
     * @return Eğer JavaScript açıksa true
     */
    fun isJavaScriptEnabled(webView: WebView): Boolean = webView.settings.javaScriptEnabled

    /**
     * WebView içinde dosya erişiminin açık olup olmadığını kontrol eder.
     *
     * Checks whether file access is allowed in the WebView settings.
     *
     * @param webView İncelenecek WebView bileşeni
     * @return Eğer dosya erişimi açıksa true
     */
    fun isFileAccessAllowed(webView: WebView): Boolean = webView.settings.allowFileAccess

    /**
     * WebView içinde güvensiz bir JavaScript interface olup olmadığını reflection ile analiz eder.
     * Sadece debug modda kontrol yapılır.
     *
     * Uses reflection to detect insecure JavaScript interfaces inside the WebView.
     * This check is only active in debug mode.
     *
     * @param webView İncelenecek WebView bileşeni
     * @param isDebug Uygulama debug modda mı
     * @return Eğer güvensiz interface varsa true, aksi halde false
     */
    @Suppress("DiscouragedPrivateApi")
    fun hasInsecureJSInterface(webView: WebView, isDebug: Boolean = false): Boolean {
        if (!isDebug) return false
        return try {
            val field = WebView::class.java.getDeclaredField("mProvider").apply { isAccessible = true }
            val provider = field.get(webView)
            val method = provider?.javaClass?.getDeclaredMethod("getJavascriptInterfaces")?.apply { isAccessible = true }
            val result = method?.invoke(provider)
            result != null
        } catch (e: Exception) {
            false
        }
    }
}