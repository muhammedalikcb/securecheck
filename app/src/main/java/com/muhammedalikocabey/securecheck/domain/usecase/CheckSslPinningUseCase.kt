package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.core.network.SslPinningChecker
import javax.inject.Inject

/**
 * SSL Pinning kontrolünü gerçekleştirerek sunucu sertifikasının doğruluğunu kontrol eder
 *
 * Executes SSL pinning validation to check server certificate correctness
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckSslPinningUseCase @Inject constructor(
    private val checker: SslPinningChecker
) {
    operator fun invoke(): Boolean = checker.hasValidCertificate()
}