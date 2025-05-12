package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.core.reverseanalysis.XposedDetector
import javax.inject.Inject

/**
 * Cihazda Xposed framework'ü yüklü olup olmadığını kontrol eder
 *
 * Checks if the Xposed framework is present on the device
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckXposedDetectorUseCase @Inject constructor(
    private val xposedDetector: XposedDetector
) {
    operator fun invoke(): Boolean = xposedDetector.isXposedPresent()
}