package com.muhammedalikocabey.securecheck.data.detector.integrity

import android.content.Context
import android.util.Base64
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Google Play Integrity API ile cihazın `MEETS_DEVICE_INTEGRITY` gibi verdict'lere sahip olup olmadığını kontrol eder
 * Sonuçlar `deviceRecognitionVerdict` alanından ayrıştırılır
 *
 * Checks whether the device meets Play Integrity requirements by parsing verdicts such as `MEETS_DEVICE_INTEGRITY`
 * Uses the official `IntegrityManager` to request integrity tokens
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class PlayIntegrityValidatorImpl @Inject constructor() : IPlayIntegrityValidator {

    override suspend fun checkPlayIntegrity(context: Context): Boolean =
        suspendCancellableCoroutine { cont ->
            val manager = IntegrityManagerFactory.create(context)
            val request = IntegrityTokenRequest.builder()
                .setNonce(UUID.randomUUID().toString())
                .build()

            manager.requestIntegrityToken(request)
                .addOnSuccessListener { response ->
                    val payload = response.token().split(".").getOrNull(1)
                    val verdicts = try {
                        val json = JSONObject(Base64.decode(payload, Base64.DEFAULT).decodeToString())
                        json.getJSONObject("deviceIntegrity")
                            .optJSONArray("deviceRecognitionVerdict")
                            ?.let { arr ->
                                List(arr.length()) { arr.optString(it) }
                            } ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }

                    cont.resume(verdicts.any { it.contains("MEETS") })
                }
                .addOnFailureListener {
                    cont.resume(false)
                }
        }
}
