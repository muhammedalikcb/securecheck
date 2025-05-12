package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import javax.inject.Inject

/**
 * Uygulamanın değiştirilip değiştirilmediğini kontrol eder
 *
 * Checks if the app has been tampered with (repackaging, fake builds, etc.)
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckTamperStatusUseCase @Inject constructor(
    private val checker: SecurityChecker
) {
    operator fun invoke(context: Context): Boolean = checker.isAppTampered(context)
}