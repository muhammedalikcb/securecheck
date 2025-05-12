package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.core.reflection.DexLoaderDetector
import javax.inject.Inject

/**
 * Uygulama tarafından runtime sırasında dış .dex dosyası yüklenip yüklenmediğini kontrol eder
 *
 * Detects dynamic DexClassLoader usage to load external .dex files
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckDexLoaderUseCase @Inject constructor(
    private val dexLoaderDetector: DexLoaderDetector
) {
    operator fun invoke(): Boolean = dexLoaderDetector.isDexPathLoaded()
}