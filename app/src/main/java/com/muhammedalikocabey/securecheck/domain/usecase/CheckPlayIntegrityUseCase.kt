package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import javax.inject.Inject

/**
 * Google Play Integrity API kullanarak cihazın güvenilirliğini kontrol eder
 *
 * Validates the device integrity using Google Play Integrity API
 *
 * @author Muhammed Ali KOCABEY
 * @since 10.05.2025
 */
class CheckPlayIntegrityUseCase @Inject constructor(
    private val checker: SecurityChecker
) {
    suspend operator fun invoke(context: Context): Boolean = checker.checkPlayIntegrity(context)
}