package com.muhammedalikocabey.securecheck.core.icc

import android.app.PendingIntent
import javax.inject.Inject

/**
 * PendingIntent FLAG_MUTABLE hatası tespiti
 * Detects insecure PendingIntent FLAG_MUTABLE usage
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class PendingIntentLeak @Inject constructor() {
    fun isMutable(pendingIntent: PendingIntent): Boolean {
        return try {
            val field = PendingIntent::class.java.getDeclaredField("mFlags")
            field.isAccessible = true
            val flags = field.getInt(pendingIntent)
            (flags and PendingIntent.FLAG_MUTABLE) != 0
        } catch (e: Exception) {
            false
        }
    }
}
