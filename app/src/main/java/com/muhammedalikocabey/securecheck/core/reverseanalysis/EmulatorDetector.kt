package com.muhammedalikocabey.securecheck.core.reverseanalysis

import android.os.Build
import javax.inject.Inject

/**
 * Uygulamanın bir emülatör (AVD, Genymotion, vb.) ortamında çalışıp çalışmadığını tespit eder.
 * Bazı saldırı senaryoları emülatör ortamında gerçekleştirilir, bu nedenle tespiti kritiktir.
 *
 * Detects whether the app is running on an emulator (e.g. AVD, Genymotion).
 * Useful for blocking automated attacks or forensic inspection in emulated environments.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class EmulatorDetector @Inject constructor() {

    /**
     * Cihazın emülatör olup olmadığını donanımsal özelliklere bakarak kontrol eder.
     *
     * Checks if the device is an emulator by analyzing hardware and build properties.
     *
     * @return Eğer emülatör tespit edilirse true, aksi halde false
     */
    fun isRunningOnEmulator(): Boolean {
        return listOf(
            Build.FINGERPRINT, Build.MODEL, Build.BRAND,
            Build.DEVICE, Build.PRODUCT, Build.HARDWARE
        ).any {
            it.contains("generic", ignoreCase = true) ||
                    it.contains("emulator", ignoreCase = true) ||
                    it.contains("sdk", ignoreCase = true) ||
                    it.contains("goldfish", ignoreCase = true)
        }
    }
}
