package com.muhammedalikocabey.securecheck.domain.usecase

import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import javax.inject.Inject

/**
 * Cihaza debugger bağlı olup olmadığını tespit eder
 *
 * Detects if a debugger is currently attached to the app process
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckDebuggerStatusUseCase @Inject constructor(
    private val checker: SecurityChecker
) {
    operator fun invoke(): Boolean = checker.isDebuggerAttached()
}