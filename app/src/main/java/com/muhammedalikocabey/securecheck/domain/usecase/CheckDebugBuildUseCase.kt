package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.core.tamper.DebugBuildDetector
import javax.inject.Inject

/**
 * Uygulamanın debug build olarak çalışıp çalışmadığını kontrol eder
 *
 * Checks whether the app is running as a debug build
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckDebugBuildUseCase @Inject constructor(
    private val debugBuildDetector: DebugBuildDetector
) {
    operator fun invoke(context: Context): Boolean =
        debugBuildDetector.isDebuggable(context)
}