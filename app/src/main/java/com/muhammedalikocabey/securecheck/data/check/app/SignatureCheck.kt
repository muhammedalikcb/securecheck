package com.muhammedalikocabey.securecheck.data.check.app

import android.content.pm.PackageManager
import android.os.Build
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Inject

/** DER-encoded certificates that currently sign this app. */
fun interface SigningCertificates {
    fun current(): List<ByteArray>?
}

class AndroidSigningCertificates @Inject constructor(
    private val packageManager: PackageManager,
    private val info: BuildInfo,
) : SigningCertificates {
    @Suppress("DEPRECATION")
    override fun current(): List<ByteArray>? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val signing = packageManager
                .getPackageInfo(info.packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
            val signers = if (signing?.hasMultipleSigners() ==
                true
            ) {
                signing.apkContentsSigners
            } else {
                signing?.signingCertificateHistory
            }
            signers?.map { it.toByteArray() }
        } else {
            packageManager.getPackageInfo(info.packageName, PackageManager.GET_SIGNATURES).signatures?.map {
                it.toByteArray()
            }
        }
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }
}

class SignatureCheck @Inject constructor(private val certificates: SigningCertificates, private val info: BuildInfo) :
    SecurityCheck {
    override val id = CheckId.SIGNATURE

    override suspend fun run(): CheckResult {
        val der = certificates.current()?.takeIf { it.isNotEmpty() }
            ?: return CheckResult.unavailable(id, UnavailableReason.ERROR)
        val fingerprints = der.map(::sha256Hex)
        val debugCert = der.any(::isDebugCertificate)
        return verdict(id, fingerprints, debugCert, info.expectedCertSha256, info.isDebug)
    }

    companion object {
        fun verdict(
            id: CheckId,
            fingerprints: List<String>,
            debugCertificate: Boolean,
            expected: String,
            isDebugBuild: Boolean,
        ): CheckResult {
            val evidence = fingerprints.map { "sha256: $it" }
            val pinned = expected.replace(":", "").lowercase()
            return when {
                pinned.isNotEmpty() && pinned !in fingerprints.map { it.replace(":", "").lowercase() } ->
                    CheckResult.finding(
                        id,
                        evidence + "does not match the pinned certificate",
                        severity = Severity.CRITICAL,
                    )
                debugCertificate && isDebugBuild -> CheckResult.expected(id, evidence + "debug certificate")
                debugCertificate -> CheckResult.finding(id, evidence + "debug certificate on a release build")
                pinned.isNotEmpty() -> CheckResult.passed(id, evidence)
                else -> CheckResult.info(id, evidence + "no pinned certificate configured")
            }
        }

        fun sha256Hex(der: ByteArray): String =
            MessageDigest.getInstance("SHA-256").digest(der).joinToString("") { "%02x".format(it) }

        private fun isDebugCertificate(der: ByteArray): Boolean = runCatching {
            val cert = CertificateFactory.getInstance(
                "X.509",
            ).generateCertificate(ByteArrayInputStream(der)) as X509Certificate
            cert.subjectX500Principal.name.contains("CN=Android Debug")
        }.getOrDefault(false)
    }
}
