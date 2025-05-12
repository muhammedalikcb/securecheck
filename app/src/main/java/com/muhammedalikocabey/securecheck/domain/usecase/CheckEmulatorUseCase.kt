package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.core.reverseanalysis.EmulatorDetector
import javax.inject.Inject

/**
 * Uygulamanın bir emülatör üzerinde çalışıp çalışmadığını kontrol eder
 *
 * Checks whether the app is running on an emulator environment
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckEmulatorUseCase @Inject constructor(
    private val emulatorDetector: EmulatorDetector
) {
    operator fun invoke(): Boolean = emulatorDetector.isRunningOnEmulator()
}
