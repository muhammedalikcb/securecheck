package com.muhammedalikocabey.securecheck.core.icc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import javax.inject.Inject

/**
 * Gönderilen bir intent’in sistemde birden fazla uygulama tarafından yakalanıp yakalanamayacağını tespit eder.
 * Bu durum, kötü amaçlı uygulamalar tarafından intent kaçırma (hijacking) saldırısına yol açabilir.
 *
 * Detects whether a given intent can be resolved by multiple apps in the system.
 * Such conditions may lead to intent hijacking attacks by malicious apps.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class IntentHijackDetector @Inject constructor() {

    @SuppressLint("QueryPermissionsNeeded")
    fun isIntentHijackable(context: Context, intent: Intent): Boolean {
        val pm = context.packageManager
        val matches: List<ResolveInfo> = pm.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return matches.size > 1
    }
}