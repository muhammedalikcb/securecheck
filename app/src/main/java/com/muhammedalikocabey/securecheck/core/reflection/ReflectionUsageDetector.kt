package com.muhammedalikocabey.securecheck.core.reflection

import java.lang.reflect.Method
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Verilen sınıf üzerinde reflection bazlı kullanım olup olmadığını analiz eder.
 * Özellikle `Class.forName`, `Method.invoke` gibi güvenlik riski oluşturan yapıları yakalamak için tasarlanmıştır.
 *
 * Analyzes a given class for reflection-based usage patterns like `Class.forName`, `Method.invoke`, etc.
 * Helpful for detecting reflection-related risks and hidden behavior.
 *
 * @author Muhammed Ali KOCABEY
 * @since 13.05.2025
 */
class ReflectionUsageDetector @Inject constructor() {

    /**
     * Verilen sınıf içinde reflection çağrısı yapan method olup olmadığını kontrol eder.
     *
     * Checks if any method inside the given class uses reflection patterns.
     *
     * @param kClass Analiz edilecek sınıf
     * @return Reflection tespit edildiyse true
     */
    fun analyzeClass(kClass: KClass<*>): Boolean {
        return kClass.java.declaredMethods.any {
            it.name.contains("invoke") || it.name.contains("reflect")
        }
    }

    /**
     * Bir metodun reflection ile erişime açık olup olmadığını belirler.
     *
     * Determines if a given method is likely to be accessed via reflection.
     *
     * @param method Java reflection Method nesnesi
     * @return Eğer reflection içeriyorsa true
     */
    private fun isReflectionProne(method: Method): Boolean {
        val name = method.name.lowercase()
        return name.contains("invoke") || name.contains("access$") || name.contains("reflect")
    }
}
