package com.muhammedalikocabey.securecheck.data.check.device

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject

/**
 * Asks the hardware keystore for an attestation certificate and reads the boot state it reports.
 * The chain is not verified against Google's roots here: a client cannot vouch for itself, so the
 * result is a strong local signal and the README describes the server-side verification step.
 */
class KeyAttestationCheck @Inject constructor() : SecurityCheck {
    override val id = CheckId.KEY_ATTESTATION

    override suspend fun run(): CheckResult {
        val certificate = try {
            attestedLeafCertificate()
        } catch (_: java.security.GeneralSecurityException) {
            return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED)
        }
        val extension = certificate.getExtensionValue(ATTESTATION_OID)
            ?: return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED, listOf("no attestation extension"))
        val attestation = AttestationParser.parse(extension)
            ?: return CheckResult.unavailable(id, UnavailableReason.ERROR, listOf("attestation not parseable"))

        val evidence = listOfNotNull(
            "security level: ${attestation.securityLevel}",
            attestation.deviceLocked?.let { "device locked: $it" },
            attestation.bootState?.let { "verified boot state: $it" },
        )
        if (attestation.securityLevel == AttestationParser.SecurityLevel.SOFTWARE) {
            return CheckResult.unavailable(id, UnavailableReason.UNSUPPORTED, evidence)
        }
        val unlocked = attestation.deviceLocked == false
        val bootState = attestation.bootState
        return when {
            unlocked ||
                bootState == AttestationParser.BootState.UNVERIFIED ||
                bootState == AttestationParser.BootState.FAILED -> CheckResult.finding(id, evidence)
            bootState == AttestationParser.BootState.SELF_SIGNED ->
                CheckResult.finding(id, evidence, severity = Severity.MEDIUM)
            else -> CheckResult.passed(id, evidence)
        }
    }

    private fun attestedLeafCertificate(): X509Certificate {
        val challenge = ByteArray(CHALLENGE_BYTES).also { SecureRandom().nextBytes(it) }
        val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEYSTORE)
        generator.initialize(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_SIGN)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setAttestationChallenge(challenge)
                .build(),
        )
        generator.generateKeyPair()
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        try {
            return keyStore.getCertificateChain(ALIAS).first() as X509Certificate
        } finally {
            keyStore.deleteEntry(ALIAS)
        }
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val ALIAS = "securecheck_attestation_probe"
        const val ATTESTATION_OID = "1.3.6.1.4.1.11129.2.1.17"
        const val CHALLENGE_BYTES = 32
    }
}
