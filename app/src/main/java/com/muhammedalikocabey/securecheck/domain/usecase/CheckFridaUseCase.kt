package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.core.reverseanalysis.FridaDetector
import javax.inject.Inject

/**
 * Frida hook veya Frida sunucusunun izlerini kontrol eder
 *
 * Detects if Frida traces or active servers are present in the system
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckFridaUseCase @Inject constructor(
    private val fridaDetector: FridaDetector
) {
    operator fun invoke(): Boolean {
        return fridaDetector.isFridaLibraryPresent() ||
                fridaDetector.isFridaServerRunning() ||
                fridaDetector.isFridaInMaps()
    }
}