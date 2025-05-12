package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import android.content.Intent
import com.muhammedalikocabey.securecheck.core.icc.IntentHijackDetector
import javax.inject.Inject

/**
 * Verilen intent’in sistemde birden fazla uygulama tarafından yakalanabilir olup olmadığını kontrol eder
 *
 * Checks if the given intent can be resolved by more than one activity (potential hijack)
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckIntentHijackUseCase @Inject constructor(
    private val intentHijackDetector: IntentHijackDetector
) {
    operator fun invoke(context: Context, intent: Intent): Boolean {
        return intentHijackDetector.isIntentHijackable(context, intent)
    }
}