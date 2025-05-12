package com.muhammedalikocabey.securecheck.data.detector.debugger

import android.os.Debug
import javax.inject.Inject

/**
 * Debugger bağlı olup olmadığını `Debug` API'leri ile kontrol eden uygulama sınıfı
 *
 * Concrete implementation of `IDebuggerDetector` that checks debugger status using `Debug` APIs
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class DebuggerDetectorImpl @Inject constructor() : IDebuggerDetector {

    /**
     * Debugger bağlı mı veya `Debug.waitingForDebugger()` aktif mi kontrol eder
     *
     * Checks if the debugger is attached or if the process is waiting for it
     *
     * @return Eğer bağlıysa true
     */
    override fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }
}
