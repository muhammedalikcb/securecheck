package com.muhammedalikocabey.securecheck

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.muhammedalikocabey.securecheck.data.history.KeystoreReportKey
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/** The JVM tests use a software key; only a device proves the real Android Keystore accepts the cipher setup. */
@RunWith(AndroidJUnit4::class)
class KeystoreReportCipherTest {
    private val cipher = KeystoreReportKey.cipher()

    @Test
    fun sealedBytesRoundTripAndDiffer() {
        val plain = """{"score":42}""".toByteArray()
        val first = cipher.seal(plain)
        val second = cipher.seal(plain)
        assertFalse(first.contentEquals(plain))
        assertFalse(first.contentEquals(second))
        assertArrayEquals(plain, cipher.open(first))
    }

    @Test
    fun tamperedBytesAreRejected() {
        val sealed = cipher.seal("secret".toByteArray())
        sealed[sealed.lastIndex] = (sealed.last().toInt() xor 1).toByte()
        val failed = try {
            cipher.open(sealed)
            false
        } catch (_: IOException) {
            true
        }
        assertTrue(failed)
    }
}
