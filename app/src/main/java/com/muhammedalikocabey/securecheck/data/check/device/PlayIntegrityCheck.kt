package com.muhammedalikocabey.securecheck.data.check.device

import android.content.Context
import android.util.Base64
import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.SecureRandom
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Requests a Play Integrity token and reports only whether Google Play issued one. The verdict inside the token is
 * encrypted for the app's server, so a client that decodes it itself proves nothing; see docs/play-integrity-backend.md.
 */
class PlayIntegrityCheck @Inject constructor(private val context: Context, private val info: BuildInfo) :
    SecurityCheck {
    override val id = CheckId.PLAY_INTEGRITY

    override suspend fun run(): CheckResult {
        if (info.integrityProjectNumber == 0L) {
            return CheckResult.unavailable(id, UnavailableReason.NOT_CONFIGURED)
        }
        val nonce = ByteArray(NONCE_BYTES).also { SecureRandom().nextBytes(it) }
            .let { Base64.encodeToString(it, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING) }
        val request = IntegrityTokenRequest.builder()
            .setNonce(nonce)
            .setCloudProjectNumber(info.integrityProjectNumber)
            .build()
        return try {
            IntegrityManagerFactory.create(context).requestIntegrityToken(request).await()
            CheckResult.info(id, listOf("token issued", "verdict must be verified on a server"))
        } catch (e: Exception) {
            CheckResult.unavailable(id, UnavailableReason.ERROR, listOf(e.javaClass.simpleName))
        }
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { continuation.resume(it) }
        addOnFailureListener { continuation.resumeWithException(it) }
    }

    private companion object {
        const val NONCE_BYTES = 24
    }
}
