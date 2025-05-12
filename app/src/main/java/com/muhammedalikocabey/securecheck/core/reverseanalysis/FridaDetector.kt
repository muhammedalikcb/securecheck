package com.muhammedalikocabey.securecheck.core.reverseanalysis

import java.io.File
import javax.inject.Inject

/**
 * Frida framework’ünün sistemde çalışıp çalışmadığını tespit eder.
 * Frida, uygulamaları runtime’da değiştirmek için kullanılan güçlü bir reverse engineering aracıdır.
 *
 * Detects if the Frida dynamic instrumentation toolkit is present or active on the device.
 * Frida is commonly used for reverse engineering and runtime tampering of apps.
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class FridaDetector @Inject constructor() {

    /**
     * Frida’nın sistemde yüklü olduğuna dair bilinen dosya yollarını kontrol eder.
     *
     * Checks for known Frida server binaries or libraries in the file system.
     */
    fun isFridaLibraryPresent(): Boolean {
        return File("/data/local/tmp/frida-server").exists() ||
                File("/system/lib/libfrida-gadget.so").exists()
    }

    /**
     * Frida sunucusunun çalışıp çalışmadığını kontrol eder (netstat ile).
     *
     * Checks if the Frida server is currently running by analyzing active ports.
     */
    fun isFridaServerRunning(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("netstat")
            val output = process.inputStream.bufferedReader().readText()
            output.contains("27042") || output.contains("frida")
        } catch (e: Exception) { false }
    }

    /**
     * Frida’nın process memory map’ine enjekte olup olmadığını kontrol eder.
     *
     * Checks if Frida is mapped into the current process memory.
     */
    fun isFridaInMaps(): Boolean {
        return try {
            val pid = android.os.Process.myPid()
            val mapsFile = File("/proc/$pid/maps")
            mapsFile.readText().contains("frida")
        } catch (e: Exception) { false }
    }
}