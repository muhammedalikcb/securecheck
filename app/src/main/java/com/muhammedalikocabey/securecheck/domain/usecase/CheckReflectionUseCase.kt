package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.core.reflection.ReflectionUsageDetector
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Bir sınıfın reflection içerip içermediğini analiz eder
 *
 * Analyzes if a given class contains reflection-related calls or behavior
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckReflectionUseCase @Inject constructor(
    private val reflectionUsageDetector: ReflectionUsageDetector
) {
    operator fun invoke(target: KClass<*>): Boolean {
        return reflectionUsageDetector.analyzeClass(target)
    }
}