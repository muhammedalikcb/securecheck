package com.muhammedalikocabey.securecheck.data.detector.installer

import android.content.Context
import android.os.Build
import javax.inject.Inject

/**
 * Uygulamanın hangi mağaza veya kaynaktan yüklendiğini kontrol eden varsayılan uygulama sınıfı
 * Android R (API 30) sonrası `getInstallSourceInfo`, öncesinde `getInstallerPackageName` kullanır
 *
 * Default implementation for validating the installer source
 * Uses `getInstallSourceInfo` for API 30+ and falls back to `getInstallerPackageName` for older versions
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class InstallerValidatorImpl @Inject constructor() : IInstallerValidator {

    /**
     * Uygulama Google Play Store’dan mı yüklendiğini kontrol eder
     *
     * Validates whether the app was installed from the Google Play Store by checking the installer package.
     *
     * @param context Uygulama bağlamı
     * @return Eğer kaynak `com.android.vending` içeriyorsa true
     */
    override fun isFromPlayStore(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pm.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                pm.getInstallerPackageName(context.packageName)
            }
            installer?.contains("vending") == true
        } catch (e: Exception) {
            false
        }
    }
}