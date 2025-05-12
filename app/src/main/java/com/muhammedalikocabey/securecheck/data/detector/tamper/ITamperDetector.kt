package com.muhammedalikocabey.securecheck.data.detector.tamper

import android.content.Context

/**
 * Uygulamanın değiştirilip değiştirilmediğini tespit eden interface
 * Package adı, imza geçerliliği ve sistem özellikleri kontrol edilerek analiz yapılır
 *
 * Interface for detecting whether the app has been tampered with
 * Combines package name, signature validation, and build property analysis
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface ITamperDetector {

    /**
     * Uygulamanın package adı ve imzasını kontrol eder
     * Ayrıca cihazın emülatör veya değişmiş bir ortamda çalışıp çalışmadığını sistem özellikleriyle analiz eder
     *
     * Implementation of ITamperDetector that compares expected package name and signature validity
     * Also checks suspicious build fingerprints to detect emulated or tampered environments
     *
     * @author Muhammed Ali KOCABEY
     * @since 11.05.2025
     */
    fun isAppTampered(context: Context): Boolean
}