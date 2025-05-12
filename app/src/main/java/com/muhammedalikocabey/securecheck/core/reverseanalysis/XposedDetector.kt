package com.muhammedalikocabey.securecheck.core.reverseanalysis

import javax.inject.Inject

/**
 * Cihazda Xposed Framework’ünün yüklü olup olmadığını kontrol eder.
 * Xposed, sistem davranışlarını modifiye eden modül bazlı bir framework’tür ve ciddi güvenlik riskleri oluşturabilir.
 *
 * Detects whether the Xposed Framework is present on the device.
 * Xposed allows runtime code modification and may compromise app integrity.
 *
 * @author Muhammed Ali KOCABEY
 * @since 12.05.2025
 */
class XposedDetector @Inject constructor() {

    /**
     * Xposed kütüphanelerinin class loader aracılığıyla erişilebilir olup olmadığını kontrol eder.
     *
     * Tries to load known Xposed classes to determine its presence.
     *
     * @return Eğer Xposed framework yüklüyse true, aksi halde false
     */
    fun isXposedPresent(): Boolean {
        return try {
            Class.forName("de.robv.android.xposed.XposedHelpers")
            true
        } catch (e: Exception) {
            false
        }
    }
}