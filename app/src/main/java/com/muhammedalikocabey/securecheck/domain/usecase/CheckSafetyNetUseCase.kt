package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import javax.inject.Inject

/**
 * Google SafetyNet API kullanarak cihaz güvenlik profilini kontrol eder
 *
 * Executes Google SafetyNet API to verify device profile (CTS, Integrity)
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckSafetyNetUseCase @Inject constructor(
    private val checker: SecurityChecker
) {
    suspend operator fun invoke(context: Context): Pair<Boolean, Boolean> = checker.performSafetyNet(context)
}