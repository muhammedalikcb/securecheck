package com.muhammedalikocabey.securecheck.domain.usecase

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.muhammedalikocabey.securecheck.core.icc.PendingIntentLeak
import javax.inject.Inject

/**
 * Mutable PendingIntent kullanımı olup olmadığını kontrol eder
 *
 * Checks if any mutable PendingIntent is created, which can be tampered with
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckPendingIntentLeakUseCase @Inject constructor(
    private val detector: PendingIntentLeak
) {
    operator fun invoke(context: Context): Boolean {
        val intent = PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return !detector.isMutable(intent)
    }
}