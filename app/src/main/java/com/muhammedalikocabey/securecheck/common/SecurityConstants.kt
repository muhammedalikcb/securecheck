package com.muhammedalikocabey.securecheck.common

import com.muhammedalikocabey.securecheck.BuildConfig

/**
 * Uygulama genelinde güvenli konfigürasyonlar ve doğrulamalar için kullanılan sabitler.
 * Şifrelenmiş imza hash'leri, shared preferences isimleri, dosya isimleri,
 * keystore alias'ları ve API anahtarlarını içerir.
 *
 * Constants used for secure configuration and validation throughout the app.
 * This includes encrypted signature hashes, shared preferences names, file names,
 * keystore aliases, and API keys.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
object SecurityConstants {

    const val EXPECTED_PACKAGE: String = BuildConfig.APPLICATION_ID

    // SHA-256 signature hash → AES şifreli değer çözülüp karşılaştırılır
    const val ENCRYPTED_SIGNATURE_HASH =
        "BlgdopNo4RvlhhfmAKcvuR1H/FP7KGwVTrX19NTVaBetjSBvPjPxDYfYfri4pTjWmMInI0Eg3OWIiO6P6+4BPfAVVc9HqLpAHH+ldYwniJaQhUYKJrBoAGRT2NlTsevg"


    // EncryptedSharedPreferences
    const val ENCRYPTED_PREF_NAME = "secure_prefs"

    // EncryptedFile
    const val ENCRYPTED_FILE_NAME = "secure_data.txt"

    // KeyStore Android
    const val ANDROID_KEYSTORE = "AndroidKeyStore"

    const val HOST_NAME = "https://securecheck.local"
}