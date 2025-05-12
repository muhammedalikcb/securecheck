package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import javax.inject.Inject

/**
 * APK imzasının geçerli olup olmadığını kontrol eder
 *
 * Validates if the APK has a valid signature that matches the expected one
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckSignatureUseCase @Inject constructor(
    private val checker: SecurityChecker
) {
    operator fun invoke(context: Context): Boolean = checker.isSignatureValid(context)
}