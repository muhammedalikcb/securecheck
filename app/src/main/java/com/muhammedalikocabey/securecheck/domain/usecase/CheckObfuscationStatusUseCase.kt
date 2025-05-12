package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.core.obfuscation.ObfuscationDetector
import javax.inject.Inject

/**
 * Uygulama kodunun obfuscation ile gizlenip gizlenmediğini kontrol eder
 *
 * Detects if code obfuscation (ProGuard/R8) is applied in the APK
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckObfuscationStatusUseCase @Inject constructor(
    private val detector: ObfuscationDetector
) {
    operator fun invoke(): Boolean = detector.isObfuscated()
}