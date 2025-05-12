package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import javax.inject.Inject

/**
 * Cihazın root edilmiş olup olmadığını kontrol eder
 *
 * Checks whether the device has root access
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckRootStatusUseCase @Inject constructor(
    private val checker: SecurityChecker
) {
    operator fun invoke(): Boolean = checker.isRooted()
}