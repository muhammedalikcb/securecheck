package com.muhammedalikocabey.securecheck.core.reflection

import android.annotation.SuppressLint
import javax.inject.Inject

/**
 * Dinamik DexClassLoader kullanımı ile dışarıdan .dex yüklenip yüklenmediğini tespit eder.
 * Genelde zararlı modül ekleme ve plugin enjeksiyonunu analiz etmek için kullanılır.
 *
 * Detects dynamic code loading via DexClassLoader, a common technique in malware and plugin-based apps.
 * Critical for analyzing plugin injection or runtime payloads.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class DexLoaderDetector @Inject constructor() {

    /**
     * Dex path üzerinden dışarıdan yüklenmiş bir .dex dosyası var mı kontrol eder.
     *
     * Checks if a custom .dex file is loaded from an external path.
     *
     * @return Eğer dış dex yüklendiyse true, aksi halde false
     */
    @SuppressLint("SdCardPath")
    fun isDexPathLoaded(): Boolean {
        return try {
            val pathListField = ClassLoader::class.java.getDeclaredField("pathList")
            pathListField.isAccessible = true

            val classLoader = DexLoaderDetector::class.java.classLoader
            val pathList = pathListField.get(classLoader)

            val dexElementsField = pathList.javaClass.getDeclaredField("dexElements")
            dexElementsField.isAccessible = true
            val dexElements = dexElementsField.get(pathList) as? Array<*>

            dexElements?.any {
                val elementStr = it.toString()
                elementStr.contains("/data/data/") && elementStr.contains(".dex")
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
