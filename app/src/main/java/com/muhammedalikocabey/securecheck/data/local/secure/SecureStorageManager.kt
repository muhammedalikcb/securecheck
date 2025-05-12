package com.muhammedalikocabey.securecheck.data.local.secure

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.muhammedalikocabey.securecheck.common.SecurityConstants.ENCRYPTED_FILE_NAME
import com.muhammedalikocabey.securecheck.common.SecurityConstants.ENCRYPTED_PREF_NAME
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * EncryptedSharedPreferences ve EncryptedFile kullanarak hassas verilerin güvenli şekilde saklanmasını sağlayan sınıf
 * AES256_GCM ve AES256_SIV algoritmaları kullanılarak Android Keystore destekli şifreleme yapılır
 *
 * Manages secure data storage using EncryptedSharedPreferences and EncryptedFile
 * Encrypts data using AES256 algorithms backed by Android Keystore
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@Singleton
@Suppress("DEPRECATION")
class SecureStorageManager @Inject constructor(
    private val context: Context,
    private val masterKey: MasterKey
) {

    /**
     * EncryptedSharedPreferences nesnesini oluşturur ve döner
     *
     * Returns an instance of EncryptedSharedPreferences
     */
    @Suppress("DEPRECATION")
    fun getEncryptedPrefs(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    /**
     * EncryptedFile üzerinden şifreli dosyaya veri yazar
     *
     * Writes the given content securely into an encrypted file
     */
    @Suppress("DEPRECATION")
    fun writeEncryptedFile(content: String): Boolean {
        return try {
            val file = File(context.filesDir, ENCRYPTED_FILE_NAME)
            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encryptedFile.openFileOutput().use { output ->
                output.write(content.toByteArray())
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Şifreli dosyadaki veriyi okur ve çözümler
     *
     * Reads and decrypts the content from the encrypted file
     */
    @Suppress("DEPRECATION")
    fun readEncryptedFile(): String? {
        return try {
            val file = File(context.filesDir, ENCRYPTED_FILE_NAME)
            val encryptedFile = EncryptedFile.Builder(
                context,
                file,
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            encryptedFile.openFileInput().bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Şifreli dosyayı kalıcı olarak siler
     *
     * Deletes the encrypted file from disk
     */
    fun clearEncryptedFile(): Boolean {
        return File(context.filesDir, ENCRYPTED_FILE_NAME).delete()
    }
}
