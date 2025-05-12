package com.muhammedalikocabey.securecheck.data.detector.safetynet

import android.content.Context

/**
 * SafetyNet doğrulaması gerçekleştiren interface
 * Cihazın güvenilirliğini (ctsProfileMatch, basicIntegrity) test etmek için kullanılır
 *
 * Interface for performing SafetyNet attestation
 * Used to verify device integrity through `ctsProfileMatch` and `basicIntegrity` verdicts
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface ISafetyNetValidator {
    suspend fun performSafetyNet(context: Context): Pair<Boolean, Boolean>
}