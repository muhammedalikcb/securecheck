package com.muhammedalikocabey.securecheck.data.detector.safetynet

import android.content.Context
import android.util.Base64
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetClient
import com.muhammedalikocabey.securecheck.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Google SafetyNet API kullanarak cihazın güvenilirliğini kontrol eden uygulama sınıfı
 * JWT token'ı çözüp `ctsProfileMatch` ve `basicIntegrity` alanlarını okur
 *
 * Implementation of ISafetyNetValidator that performs device integrity checks via Google SafetyNet API
 * Parses the returned JWT token to extract integrity verdicts
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class SafetyNetValidatorImpl @Inject constructor() : ISafetyNetValidator {

    override suspend fun performSafetyNet(context: Context): Pair<Boolean, Boolean> =
        suspendCancellableCoroutine { cont ->
            val nonce = MessageDigest.getInstance("SHA-256")
                .digest(System.currentTimeMillis().toString().toByteArray())

            val client: SafetyNetClient = SafetyNet.getClient(context)

            client.attest(nonce, BuildConfig.SAFETYNET_API_KEY)
                .addOnSuccessListener { response ->
                    val payload = response.jwsResult
                        ?.split(".")
                        ?.getOrNull(1)

                    val json = try {
                        payload?.let {
                            JSONObject(Base64.decode(it, Base64.DEFAULT).decodeToString())
                        }
                    } catch (e: Exception) {
                        null
                    }

                    val cts = json?.optBoolean("ctsProfileMatch", false) ?: false
                    val integrity = json?.optBoolean("basicIntegrity", false) ?: false

                    cont.resume(Pair(cts, integrity))
                }
                .addOnFailureListener {
                    cont.resume(Pair(false, false))
                }
        }
}
