package com.muhammedalikocabey.securecheck.core.mock.network

import com.muhammedalikocabey.securecheck.common.SecurityConstants.HOST_NAME
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * Test amaçlı hostname doğrulayıcı
 * Mock HostnameVerifier for test usage only
 */
object HostVerifierMock : HostnameVerifier {
    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        // Gerçekte hostname == CN/SubjectAltName kontrol edilmeli
        return hostname == HOST_NAME
    }
}