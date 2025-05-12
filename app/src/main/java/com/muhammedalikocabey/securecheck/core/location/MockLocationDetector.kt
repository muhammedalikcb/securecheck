package com.muhammedalikocabey.securecheck.core.location

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.location.Location
import androidx.annotation.RequiresPermission
import javax.inject.Inject

/**
 * Cihazda sahte (mock) konum servisinin etkin olup olmadığını tespit eder.
 * Özellikle test cihazları ve konum sahteciliği yapan uygulamaların analizinde kullanılır.
 *
 * Detects whether mock (fake) location services are enabled on the device.
 * Useful for identifying test environments or location spoofing attempts.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class MockLocationDetector @Inject constructor() {

    /**
     * Sahte konum servisinin etkin olup olmadığını kontrol eder.
     *
     * Checks if mock location is enabled via any known location provider.
     *
     * @param context Uygulama bağlamı
     * @return Sahte konum etkinse true, değilse false
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun isMockLocationEnabled(context: Context): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val providers = locationManager?.allProviders ?: return false

            providers.any { provider ->
                val location: Location = locationManager.getLastKnownLocation(provider) ?: return@any false

                @Suppress("DEPRECATION")
                location.isFromMockProvider
            }
        } catch (e: Exception) {
            false
        }
    }
}
