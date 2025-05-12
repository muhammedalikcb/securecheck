package com.muhammedalikocabey.securecheck.core.network

import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import javax.inject.Inject

/**
 * Sertifika pinleme için güvenilir sertifikaları yöneten trust manager oluşturur.
 * Bu yapı, istemci tarafında sadece tanımlı sertifikalara güvenilmesini sağlar.
 *
 * Generates a trust manager for certificate pinning by loading trusted X.509 certificates.
 * Ensures the client only accepts defined server certificates, enhancing connection security.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class PinningTrustManager @Inject constructor() {

    /**
     * Sertifika stream'inden bir X509TrustManager üretir.
     *
     * Builds an X509TrustManager from the given certificate input stream.
     *
     * @param certInputStream .cer veya .pem sertifika dosyası
     * @return X509TrustManager örneği
     */
    fun getTrustManager(certInputStream: InputStream): X509TrustManager {
        val cf = CertificateFactory.getInstance("X.509")
        val cert = cf.generateCertificate(certInputStream) as X509Certificate

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("securecheck", cert)
        }

        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore)
        }

        return tmf.trustManagers
            .filterIsInstance<X509TrustManager>()
            .first()
    }
}
