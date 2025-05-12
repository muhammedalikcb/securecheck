package com.muhammedalikocabey.securecheck.domain.usecase

import android.webkit.WebView
import com.muhammedalikocabey.securecheck.core.webview.WebViewSecurityAnalyzer
import javax.inject.Inject

/**
 * WebView bileşeninin güvenli ayarlara sahip olup olmadığını kontrol eder
 *
 * Checks whether a WebView instance has safe configuration (JavaScript, file access, JS interface)
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckWebViewSecurityUseCase @Inject constructor(
    private val webViewSecurityAnalyzer: WebViewSecurityAnalyzer
) {
    operator fun invoke(webView: WebView, isDebug: Boolean): WebViewSecurityResult {
        return WebViewSecurityResult(
            isJsEnabled = webViewSecurityAnalyzer.isJavaScriptEnabled(webView),
            isFileAccessAllowed = webViewSecurityAnalyzer.isFileAccessAllowed(webView),
            hasInsecureJsInterface = webViewSecurityAnalyzer.hasInsecureJSInterface(webView, isDebug)
        )
    }
}

data class WebViewSecurityResult(
    val isJsEnabled: Boolean,
    val isFileAccessAllowed: Boolean,
    val hasInsecureJsInterface: Boolean
)
