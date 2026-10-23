package com.muhammedalikocabey.securecheck.data.check.device

/**
 * Reads the Android Key Attestation extension (OID 1.3.6.1.4.1.11129.2.1.17) with a minimal DER walker.
 * Only the fields SecureCheck reports are extracted: the security level and the root of trust.
 */
object AttestationParser {
    enum class SecurityLevel { SOFTWARE, TRUSTED_ENVIRONMENT, STRONGBOX, UNKNOWN }

    enum class BootState { VERIFIED, SELF_SIGNED, UNVERIFIED, FAILED, UNKNOWN }

    data class Attestation(val securityLevel: SecurityLevel, val deviceLocked: Boolean?, val bootState: BootState?)

    private class Tlv(val tag: Int, val tagNumber: Int, val constructed: Boolean, val content: ByteArray)

    private const val ROOT_OF_TRUST_TAG = 704
    private const val TEE_ENFORCED_INDEX = 7
    private const val SOFTWARE_ENFORCED_INDEX = 6

    /** [extension] is the value returned by `X509Certificate.getExtensionValue`: a DER OCTET STRING. */
    fun parse(extension: ByteArray): Attestation? = runCatching {
        val octet = readAll(extension).singleOrNull() ?: return null
        val keyDescription = readAll(octet.content).singleOrNull() ?: return null
        val fields = readAll(keyDescription.content)
        if (fields.size <= TEE_ENFORCED_INDEX) return null

        val level = when (integerOf(fields[1])) {
            0 -> SecurityLevel.SOFTWARE
            1 -> SecurityLevel.TRUSTED_ENVIRONMENT
            2 -> SecurityLevel.STRONGBOX
            else -> SecurityLevel.UNKNOWN
        }
        val rootOfTrust = rootOfTrust(fields[TEE_ENFORCED_INDEX]) ?: rootOfTrust(fields[SOFTWARE_ENFORCED_INDEX])
        val rootFields = rootOfTrust?.let { readAll(it.content) }.orEmpty()

        Attestation(
            securityLevel = level,
            deviceLocked = rootFields.getOrNull(1)?.let { it.content.firstOrNull()?.toInt() != 0 },
            bootState = rootFields.getOrNull(2)?.let {
                when (integerOf(it)) {
                    0 -> BootState.VERIFIED
                    1 -> BootState.SELF_SIGNED
                    2 -> BootState.UNVERIFIED
                    3 -> BootState.FAILED
                    else -> BootState.UNKNOWN
                }
            },
        )
    }.getOrNull()

    /** `[704] EXPLICIT SEQUENCE` inside an AuthorizationList; the wrapper holds one SEQUENCE. */
    private fun rootOfTrust(authorizationList: Tlv): Tlv? =
        readAll(authorizationList.content).firstOrNull { it.tagNumber == ROOT_OF_TRUST_TAG }
            ?.let { readAll(it.content).firstOrNull() }

    private fun integerOf(tlv: Tlv): Int = tlv.content.fold(0) { acc, b -> (acc shl 8) or (b.toInt() and 0xFF) }

    private fun readAll(bytes: ByteArray): List<Tlv> {
        val result = mutableListOf<Tlv>()
        var pos = 0
        while (pos < bytes.size) {
            val first = bytes[pos++].toInt() and 0xFF
            var number = first and 0x1F
            if (number == 0x1F) {
                number = 0
                do {
                    val b = bytes[pos++].toInt() and 0xFF
                    number = (number shl 7) or (b and 0x7F)
                } while (b and 0x80 != 0)
            }
            var length = bytes[pos++].toInt() and 0xFF
            if (length and 0x80 != 0) {
                val count = length and 0x7F
                length = 0
                repeat(count) { length = (length shl 8) or (bytes[pos++].toInt() and 0xFF) }
            }
            require(length >= 0 && pos + length <= bytes.size) { "truncated DER" }
            result += Tlv(first, number, first and 0x20 != 0, bytes.copyOfRange(pos, pos + length))
            pos += length
        }
        return result
    }
}
