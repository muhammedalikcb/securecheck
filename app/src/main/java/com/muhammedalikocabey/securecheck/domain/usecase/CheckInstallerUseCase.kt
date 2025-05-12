package com.muhammedalikocabey.securecheck.domain.usecase

import android.content.Context
import com.muhammedalikocabey.securecheck.data.datasource.SecurityChecker
import javax.inject.Inject

/**
 * Uygulamanın Play Store’dan yüklenip yüklenmediğini kontrol eder
 *
 * Checks whether the app was installed from Google Play Store
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckInstallerUseCase @Inject constructor(
    private val checker: SecurityChecker
) {
    operator fun invoke(context: Context): Boolean = checker.isFromPlayStore(context)
}