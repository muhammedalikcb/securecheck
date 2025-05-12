package com.muhammedalikocabey.securecheck.core.network

import android.content.Context
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.common.SecurityConstants.HOST_NAME
import java.net.URL
import javax.inject.Inject
import javax.inject.Named
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

/**
 * SSL Pinning doğrulamasını gerçekleştirir. Sertifika eşleşmesi başarılıysa bağlantı kurulabilir.
 * Sertifikalar sabit olarak uygulamaya gömülmelidir.
 *
 * Performs SSL pinning validation. Allows connections only if the server's certificate matches a pinned one.
 *
 * @param context Sertifika dosyasına erişim için gerekli olan app context
 * @param pinningTrustManager Sertifika doğrulama için kullanılan TrustManager sağlayıcısı
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class SslPinningChecker @Inject constructor(
    private val pinningTrustManager: PinningTrustManager,
    @Named("app_context") private val context: Context
) {
    /**
     * SSL pinning kontrolünü yapar ve bağlantının güvenli olup olmadığını döner.
     *
     * Validates SSL pinning and checks if a secure connection can be established.
     *
     * @return Eğer sertifika geçerliyse true, aksi halde false
     */
    fun hasValidCertificate(): Boolean {
        return try {
            val certStream = context.resources.openRawResource(R.raw.securecheck_cert)
            val trustManager = pinningTrustManager.getTrustManager(certStream)

            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, arrayOf<TrustManager>(trustManager), null)
            }

            val url = URL(HOST_NAME)
            val conn = (url.openConnection() as HttpsURLConnection).apply {
                sslSocketFactory = sslContext.socketFactory
                connect()
            }

            conn.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
}
