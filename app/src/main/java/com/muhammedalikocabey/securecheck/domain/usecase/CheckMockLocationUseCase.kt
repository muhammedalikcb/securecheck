package com.muhammedalikocabey.securecheck.domain.usecase

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.muhammedalikocabey.securecheck.core.location.MockLocationDetector
import javax.inject.Inject

/**
 * Cihazda mock location (sahte konum) kullanımı olup olmadığını kontrol eder
 *
 * Checks if mock location is enabled on the device
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class CheckMockLocationUseCase @Inject constructor(
    private val mockLocationDetector: MockLocationDetector
) {
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    operator fun invoke(context: Context): Boolean =
        mockLocationDetector.isMockLocationEnabled(context)
}
