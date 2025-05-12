package com.muhammedalikocabey.securecheck.data.detector.debugger

/**
 * Debugger bağlı olup olmadığını kontrol eden dedektör için interface
 * Bağımlılığı soyutlayarak test edilebilirliği artırır
 *
 * Interface for checking if a debugger is attached.
 * Abstracts the implementation to enhance testability and DI flexibility.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
interface IDebuggerDetector {

    /**
     * Debugger bağlı mı kontrol eder.
     *
     * Checks if a debugger is attached to the process.
     *
     * @return Eğer bağlıysa true, değilse false
     */
    fun isDebuggerAttached(): Boolean
}
