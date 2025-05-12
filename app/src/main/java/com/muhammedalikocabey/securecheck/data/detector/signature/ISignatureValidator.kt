package com.muhammedalikocabey.securecheck.data.detector.signature

import android.content.Context

/**
 * APK imza doğrulaması gerçekleştiren interface
 * Uygulamanın imzasının beklenen SHA-256 hash değeriyle eşleşip eşleşmediğini kontrol eder
 *
 * Interface for validating APK signature against a precomputed SHA-256 hash
 * Ensures that the app has not been repackaged or tampered
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface ISignatureValidator {

    /**
     * İmzanın doğruluğunu kontrol eder
     *
     * Checks whether the current APK signature matches the expected hash
     *
     * @param context Uygulama bağlamı
     * @return Eğer imza geçerliyse true
     */
    fun isSignatureValid(context: Context): Boolean
}