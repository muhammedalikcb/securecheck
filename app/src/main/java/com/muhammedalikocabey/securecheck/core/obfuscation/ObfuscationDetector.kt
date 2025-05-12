package com.muhammedalikocabey.securecheck.core.obfuscation

import android.content.Context
import android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
import android.util.Log
import dalvik.system.DexFile
import javax.inject.Inject

/**
 * Uygulamanın kodunun ProGuard/R8 tarafından gizlenip gizlenmediğini (obfuscation) tespit eder.
 * Sınıf sayısının azlığına göre değerlendirme yapar. Debug build'lerde çalışmaz.
 *
 * Detects whether the app's code is obfuscated via ProGuard/R8 by analyzing the class count.
 * Skips analysis on debug builds to prevent false results.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class ObfuscationDetector @Inject constructor(
    private val context: Context
) {
    companion object {
        // Obfuscation aktifse sınıf sayısı çok az olur
        private const val DEFAULT_MIN_EXPECTED_CLASS_COUNT = 250
        private const val TAG = "ObfuscationDetector"
    }

    /**
     * Sınıf sayısı belirli bir eşikten azsa kod gizlenmiş (obfuscated) kabul edilir.
     *
     * If class count is below a threshold, considers the code obfuscated.
     *
     * @param minExpected Beklenen minimum sınıf sayısı
     * @return Kod gizlenmişse true, değilse false
     */
    fun isObfuscated(minExpected: Int = DEFAULT_MIN_EXPECTED_CLASS_COUNT): Boolean {
        return try {
            if (isDebugBuild()) return false // Debug build'te kontrol yapılmaz

            val classCount = getClassCount()
            val isObfuscated = classCount < minExpected

            Log.d(TAG, "Class count: $classCount, Obfuscated: $isObfuscated")

            isObfuscated
        } catch (e: Exception) {
            Log.w(TAG, "Dex parse failed: ${e.message}")
            true // erişilemezse güvenlik açısından obfuscated say
        }
    }

    /**
     * APK içindeki sınıf sayısını döner.
     *
     * Returns the number of classes loaded from the APK.
     */
    @Suppress("DEPRECATION")
    private fun getClassCount(): Int {
        val dexFile = DexFile(context.packageCodePath)
        val entries = dexFile.entries()
        return entries.toList().size
    }

    /**
     * Uygulama debug modda mı kontrol eder.
     *
     * Checks if the app is currently running in debug mode.
     */
    private fun isDebugBuild(): Boolean {
        return (context.applicationInfo.flags and FLAG_DEBUGGABLE) != 0
    }
}
