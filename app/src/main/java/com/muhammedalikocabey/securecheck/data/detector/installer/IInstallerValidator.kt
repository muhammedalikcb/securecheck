package com.muhammedalikocabey.securecheck.data.detector.installer

import android.content.Context

/**
 * Uygulamanın hangi kaynak üzerinden (örneğin Play Store) yüklendiğini doğrulayan interface
 * Sahte yüklemeleri engellemek ve güvenilir dağıtım kontrolü yapmak için kullanılır
 *
 * Interface to validate the source from which the app was installed (e.g. Play Store).
 * Helps detect sideloaded APKs or unofficial installations.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface IInstallerValidator {

    /**
     * Uygulama Play Store'dan mı yüklendi kontrol eder.
     *
     * Checks whether the app was installed from the Google Play Store.
     *
     * @param context Uygulama bağlamı
     * @return Eğer kaynak Play Store ise true
     */
    fun isFromPlayStore(context: Context): Boolean
}