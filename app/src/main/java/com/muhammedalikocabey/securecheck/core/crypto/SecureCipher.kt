package com.muhammedalikocabey.securecheck.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.muhammedalikocabey.securecheck.common.SecurityConstants.ANDROID_KEYSTORE
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Keystore kullanarak AES ile şifreleme/çözme işlemlerini gerçekleştiren sınıf.
 * Hassas verileri cihaz tabanlı anahtar ile şifreleyerek güvenli şekilde saklar.
 *
 * AES encryption/decryption implementation using Android Keystore.
 * Ensures sensitive data is encrypted with a device-bound key and securely stored.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@Singleton
class SecureCipher @Inject constructor() : ISecureCipher {

    private val keyAlias = "secure_check_key"
    private val transformation = "AES/CBC/PKCS7Padding"

    /**
     * Keystore içerisinde ilgili alias’a ait anahtar yoksa yeni bir AES anahtarı üretir.
     *
     * Generates a new AES key in the Android Keystore if it doesn't already exist.
     */
    private fun createKeyIfNotExists() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (keyStore.containsAlias(keyAlias)) return

        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            this.keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(false)
            .build()

        keyGen.init(spec)
        keyGen.generateKey()
    }

    /**
     * Android Keystore içerisinden kayıtlı gizli AES anahtarını getirir.
     *
     * Retrieves the stored AES secret key from Android Keystore.
     *
     * @return AES algoritmasında kullanılacak gizli anahtar
     */
    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        return keyStore.getKey(this.keyAlias, null) as SecretKey
    }

    /**
     * Düz metni AES algoritması ile şifreler, IV bilgisiyle birlikte Base64 olarak döner.
     *
     * Encrypts plain text using AES algorithm and returns Base64 encoded result including IV.
     *
     * @param plainText Şifrelenecek düz metin
     * @return IV + şifreli veri birleşimi, Base64 olarak kodlanmış
     */
    override fun encrypt(plainText: String): String {
        createKeyIfNotExists()
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(iv + encrypted, Base64.DEFAULT)
    }

    /**
     * Base64 formatındaki şifreli metni çözümler ve orijinal düz metni döner.
     * İlk 16 byte IV bilgisidir, geri kalanı şifreli veridir.
     *
     * Decrypts the Base64 encoded cipher text and returns the original plain text.
     * The first 16 bytes represent the IV used during encryption.
     *
     * @param cipherText Base64 formatında şifreli metin (IV + veri)
     * @return Orijinal düz metin
     */
    override fun decrypt(cipherText: String): String {
        createKeyIfNotExists()
        val bytes = Base64.decode(cipherText, Base64.DEFAULT)
        val iv = bytes.copyOfRange(0, 16)
        val encrypted = bytes.copyOfRange(16, bytes.size)
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
        return String(cipher.doFinal(encrypted))
    }
}