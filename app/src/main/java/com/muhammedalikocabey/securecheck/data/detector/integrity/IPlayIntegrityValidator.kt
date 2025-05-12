package com.muhammedalikocabey.securecheck.data.detector.integrity

import android.content.Context

/**
 * Play Integrity API kullanılarak cihaz bütünlüğünü (integrity) kontrol eden interface
 * Güvenilir cihazlarda çalıştığını doğrulamak isteyen uygulamalarda kullanılır
 *
 * Interface for checking device integrity via the Google Play Integrity API
 * Helps ensure that the app is running on trusted and untampered environments
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface IPlayIntegrityValidator {
    suspend fun checkPlayIntegrity(context: Context): Boolean
}