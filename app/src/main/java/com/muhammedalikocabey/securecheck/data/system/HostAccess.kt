package com.muhammedalikocabey.securecheck.data.system

import java.io.File
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

/** Reads files and directories the checks look at. A fake in tests keeps detector logic off the real device. */
interface ProcFiles {
    /** Lines of a readable text file, or null when it is missing or not readable. */
    fun readLines(path: String): List<String>?

    fun exists(path: String): Boolean

    /** Entry names of a readable directory, or null. */
    fun listNames(path: String): List<String>?
}

interface SystemProps {
    /** Value of a system property, empty when unset or unreadable. */
    fun get(key: String): String
}

interface PortProbe {
    fun isOpen(host: String, port: Int, timeoutMs: Int): Boolean
}

@Singleton
class RealProcFiles @Inject constructor() : ProcFiles {
    override fun readLines(path: String): List<String>? = runCatching { File(path).readLines() }.getOrNull()

    override fun exists(path: String): Boolean = runCatching { File(path).exists() }.getOrDefault(false)

    override fun listNames(path: String): List<String>? = runCatching { File(path).list()?.toList() }.getOrNull()
}

/** Reads properties through `getprop`; `android.os.SystemProperties` is hidden API and not worth reflecting on. */
@Singleton
class GetpropSystemProps @Inject constructor() : SystemProps {
    override fun get(key: String): String = runCatching {
        val process = ProcessBuilder("getprop", key).redirectErrorStream(true).start()
        process.inputStream.bufferedReader().use { it.readLine().orEmpty().trim() }
    }.getOrDefault("")
}

@Singleton
class SocketPortProbe @Inject constructor() : PortProbe {
    override fun isOpen(host: String, port: Int, timeoutMs: Int): Boolean = runCatching {
        Socket().use { it.connect(InetSocketAddress(host, port), timeoutMs) }
        true
    }.getOrDefault(false)
}
