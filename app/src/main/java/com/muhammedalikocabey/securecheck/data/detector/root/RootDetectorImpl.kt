package com.muhammedalikocabey.securecheck.data.detector.root

import android.annotation.SuppressLint
import android.os.Build
import java.io.File
import javax.inject.Inject

/**
 * Root tespitini çeşitli dosya yolları, sistem özellikleri ve `which su` komutu ile gerçekleştiren sınıf
 *
 * Implementation of IRootDetector that performs root detection via file system checks,
 * build tags, system properties, and presence of `su` binary
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
class RootDetectorImpl @Inject constructor() : IRootDetector {
    @SuppressLint("SdCardPath")
    override fun isRooted(): Boolean {
        val rootPaths = listOf(
            "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
            "/system/app/Superuser.apk", "/system/app/SuperSU.apk", "/system/app/BusyBox.apk",
            "/data/data/com.noshufou.android.su", "/data/data/com.thirdparty.superuser",
            "/data/data/com.koushikdutta.superuser", "/data/data/com.zachspong.temprootremovejb",
            "/data/data/com.ramdroid.appquarantine", "/data/data/com.amphoras.hidemyroot",
            "/data/data/com.m0narx.suhide", "/data/data/com.devadvance.rootcloak",
            "/data/data/com.saurik.substrate", "/data/data/com.devadvance.rootcloak2"
        )

        val fileBased = rootPaths.any { File(it).exists() }
        val whichSu = try {
            ProcessBuilder("which", "su").start().inputStream.bufferedReader().readLine() != null
        } catch (e: Exception) { false }

        val buildTagsRooted = Build.TAGS?.contains("test-keys") == true

        val dangerousProps = listOf("ro.debuggable=1", "ro.secure=0")
        val propsRooted = try {
            val process = Runtime.getRuntime().exec("getprop")
            process.inputStream.bufferedReader().useLines { lines ->
                lines.any { line -> dangerousProps.any { prop -> line.contains(prop) } }
            }
        } catch (e: Exception) { false }

        return fileBased || whichSu || buildTagsRooted || propsRooted
    }
}