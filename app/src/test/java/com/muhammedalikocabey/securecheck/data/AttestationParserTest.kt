package com.muhammedalikocabey.securecheck.data

import com.muhammedalikocabey.securecheck.data.check.device.AttestationParser
import com.muhammedalikocabey.securecheck.data.check.device.AttestationParser.BootState
import com.muhammedalikocabey.securecheck.data.check.device.AttestationParser.SecurityLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Builds the DER the keystore would produce, so the parser is tested without a device. */
class AttestationParserTest {
    private fun tlv(tag: Int, content: ByteArray): ByteArray {
        val length = when {
            content.size < 0x80 -> byteArrayOf(content.size.toByte())
            content.size < 0x100 -> byteArrayOf(0x81.toByte(), content.size.toByte())
            else -> byteArrayOf(0x82.toByte(), (content.size shr 8).toByte(), content.size.toByte())
        }
        return byteArrayOf(tag.toByte()) + length + content
    }

    private fun seq(vararg parts: ByteArray) = tlv(0x30, parts.fold(ByteArray(0)) { a, b -> a + b })

    private fun int(value: Int) = tlv(0x02, byteArrayOf(value.toByte()))

    private fun octets(content: ByteArray = ByteArray(32)) = tlv(0x04, content)

    private fun bool(value: Boolean) = tlv(0x01, byteArrayOf(if (value) 0xFF.toByte() else 0))

    /** `[704] EXPLICIT` wraps the RootOfTrust sequence; tag 704 is encoded as BF 85 40. */
    private fun rootOfTrust(locked: Boolean, state: Int) = byteArrayOf(0xBF.toByte(), 0x85.toByte(), 0x40) +
        tlv(0, ByteArray(0)).let { byteArrayOf() }.let { _ ->
            val inner = seq(octets(), bool(locked), tlv(0x0A, byteArrayOf(state.toByte())), octets())
            byteArrayOf(inner.size.toByte()) + inner
        }

    private fun keyDescription(level: Int, teeEnforced: ByteArray): ByteArray {
        val software = seq()
        val tee = tlv(0x30, teeEnforced)
        val body = seq(int(4), int(level), int(4), int(level), octets(), octets(), software, tee)
        return tlv(0x04, body)
    }

    @Test
    fun `reads security level and verified locked boot state`() {
        val parsed = AttestationParser.parse(keyDescription(1, rootOfTrust(locked = true, state = 0)))!!
        assertEquals(SecurityLevel.TRUSTED_ENVIRONMENT, parsed.securityLevel)
        assertEquals(true, parsed.deviceLocked)
        assertEquals(BootState.VERIFIED, parsed.bootState)
    }

    @Test
    fun `unlocked bootloader with unverified state`() {
        val parsed = AttestationParser.parse(keyDescription(2, rootOfTrust(locked = false, state = 2)))!!
        assertEquals(SecurityLevel.STRONGBOX, parsed.securityLevel)
        assertEquals(false, parsed.deviceLocked)
        assertEquals(BootState.UNVERIFIED, parsed.bootState)
    }

    @Test
    fun `software level without root of trust`() {
        val parsed = AttestationParser.parse(keyDescription(0, ByteArray(0)))!!
        assertEquals(SecurityLevel.SOFTWARE, parsed.securityLevel)
        assertNull(parsed.deviceLocked)
        assertNull(parsed.bootState)
    }

    @Test
    fun `garbage and truncated input return null instead of throwing`() {
        assertNull(AttestationParser.parse(byteArrayOf()))
        assertNull(AttestationParser.parse(byteArrayOf(0x04, 0x05, 0x30)))
        assertNull(AttestationParser.parse(keyDescription(1, ByteArray(0)).copyOf(10)))
    }
}
