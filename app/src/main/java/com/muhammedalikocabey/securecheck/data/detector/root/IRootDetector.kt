package com.muhammedalikocabey.securecheck.data.detector.root

/**
 * Cihazın root'lu olup olmadığını kontrol eden interface
 * Root tespiti, uygulama güvenliğini etkileyen kritik bir kontroldür
 *
 * Interface for detecting whether the device is rooted
 * Root detection is crucial for ensuring app integrity and data safety
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface IRootDetector {

    /**
     * Root erişimi olup olmadığını kontrol eder
     *
     * Checks whether root access exists on the device
     *
     * @return Eğer cihaz root edilmişse true
     */
    fun isRooted(): Boolean
}