package com.muhammedalikocabey.securecheck.data.history

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.ProviderException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Turns a serialized report into the bytes that are written to disk, and back. Failures surface as [IOException]. */
interface ReportCipher {
    fun seal(plain: ByteArray): ByteArray

    fun open(stored: ByteArray): ByteArray

    /** Writes reports as they are. Used by JVM tests and as the fallback when no key can be created. */
    object None : ReportCipher {
        override fun seal(plain: ByteArray): ByteArray = plain

        override fun open(stored: ByteArray): ByteArray = stored
    }
}

/**
 * AES-256-GCM. A stored file is `VERSION | IV | ciphertext+tag`. A file that starts with `{` is a report written
 * before encryption existed and is read as is, so an update never makes old history unreadable. The next save
 * rewrites only the new report; old ones stay as they were until they age out of the 30-report window.
 *
 * Any tampering or a lost key fails the GCM tag check, which the store treats like a corrupt file and skips.
 */
class AesGcmReportCipher(private val key: () -> SecretKey) : ReportCipher {
    private val random = SecureRandom()

    override fun seal(plain: ByteArray): ByteArray = guarded {
        val iv = ByteArray(IV_BYTES).also(random::nextBytes)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, iv))
        }
        byteArrayOf(VERSION) + iv + cipher.doFinal(plain)
    }

    override fun open(stored: ByteArray): ByteArray = guarded {
        if (stored.firstOrNull() == PLAIN_JSON) return@guarded stored
        if (stored.size <= 1 + IV_BYTES || stored[0] != VERSION) throw IOException("Unknown report format")
        val iv = stored.copyOfRange(1, 1 + IV_BYTES)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, iv))
        }
        cipher.doFinal(stored, 1 + IV_BYTES, stored.size - 1 - IV_BYTES)
    }

    private inline fun <T> guarded(block: () -> T): T = try {
        block()
    } catch (error: GeneralSecurityException) {
        throw IOException("Report encryption failed", error)
    } catch (error: ProviderException) {
        throw IOException("Report encryption failed", error)
    }

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_BYTES = 12
        const val TAG_BITS = 128
        const val VERSION: Byte = 1
        const val PLAIN_JSON = '{'.code.toByte()
    }
}

/** The key never leaves the Android Keystore, and is not included in backups or device transfers. */
object KeystoreReportKey {
    private const val ALIAS = "securecheck.reports.v1"
    private const val PROVIDER = "AndroidKeyStore"
    private const val KEY_BITS = 256

    fun cipher(): ReportCipher = AesGcmReportCipher(::secretKey)

    @Synchronized
    private fun secretKey(): SecretKey {
        val store = KeyStore.getInstance(PROVIDER).apply { load(null) }
        (store.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val spec = KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_BITS)
            .build()
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER).apply { init(spec) }.generateKey()
    }
}
