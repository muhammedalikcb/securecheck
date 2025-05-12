package com.muhammedalikocabey.securecheck.core.tamper

import android.content.pm.ApplicationInfo
import android.content.Context
import javax.inject.Inject

/**
 * Uygulamanın debug olarak mı derlendiğini kontrol eder.
 * Debug modda çalışan uygulamalar daha savunmasız olabilir ve tespiti güvenlik açısından önemlidir.
 *
 * Checks whether the app is running in debug mode.
 * Apps built in debug mode are more vulnerable to tampering or inspection.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class DebugBuildDetector @Inject constructor() {

    /**
     * Uygulama debug modda mı kontrol eder.
     *
     * Determines if the application is running as a debuggable build.
     *
     * @param context Uygulama bağlamı
     * @return Eğer debuggable=true ise true döner, değilse false
     */
    fun isDebuggable(context: Context): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}
