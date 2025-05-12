package com.muhammedalikocabey.securecheck.domain.fake

/**
 * Obfuscation (kod gizleme) kontrolü sırasında bilerek projede bırakılan ancak kullanılmayan sınıf
 * ProGuard/R8 bu sınıfı silerse, obfuscation aktif kabul edilir
 *
 * Intentionally unused class to test obfuscation logic
 * If removed by ProGuard/R8, it's treated as a sign of code being obfuscated
 *
 * Bu sınıf üretim ortamında hiçbir yerde kullanılmamalıdır, yalnızca test amacıyla tanımlanmıştır
 *
 * This class should not be used in production, only serves as an obfuscation marker
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class IntentionallyMissingClass {

    /**
     * Sadece varlık doğrulaması amacıyla oluşturulmuş örnek bir metot
     *
     * Dummy method created for the sole purpose of confirming class existence
     */
    fun sampleMethod(): String = "obfuscated"
}