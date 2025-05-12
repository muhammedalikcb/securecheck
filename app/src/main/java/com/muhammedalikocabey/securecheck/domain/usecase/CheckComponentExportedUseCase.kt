package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.core.icc.ComponentScanner
import javax.inject.Inject

/**
 * Manifest dosyasındaki exported bileşenlerin olup olmadığını kontrol eder
 * ComponentScanner sınıfı ile çalışır
 *
 * Checks if the app has exported components declared in the manifest
 * Uses ComponentScanner for detection
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckComponentExportedUseCase @Inject constructor(
    private val scanner: ComponentScanner
) {
    /**
     * Eğer exported component yoksa true döner
     */
    operator fun invoke(context: Context): Boolean =
        scanner.listExportedComponents(context).isEmpty()
}
