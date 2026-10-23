package com.muhammedalikocabey.securecheck.data.history

import com.muhammedalikocabey.securecheck.domain.history.ReportStore
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Locale

/**
 * One JSON file per report in the app's private storage, newest [limit] kept.
 *
 * Files are written to a temporary name and renamed, so a process kill mid-write leaves either the old state
 * or the new one, never a half-written report. The folder is kept out of backups by the backup rules.
 */
class JsonFileReportStore(
    private val dir: File,
    private val limit: Int = DEFAULT_LIMIT,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) : ReportStore {
    private val lock = Mutex()

    override suspend fun save(report: ScanReport) = lock.withLock {
        withContext(io) {
            if (!dir.isDirectory && !dir.mkdirs()) throw IOException("Cannot create ${dir.name}")
            val target = File(dir, fileName(report))
            val temp = File(dir, "${target.name}.tmp")
            temp.writeText(ReportJson.encode(report))
            if (!temp.renameTo(target)) {
                temp.delete()
                throw IOException("Cannot store report")
            }
            reportFiles().sortedByDescending { it.name }.drop(limit).forEach { it.delete() }
        }
    }

    override suspend fun all(): List<ScanReport> = lock.withLock {
        withContext(io) {
            reportFiles().sortedByDescending { it.name }.mapNotNull { file ->
                try {
                    ReportJson.decode(file.readText())
                } catch (_: IOException) {
                    null
                }
            }
        }
    }

    override suspend fun clear() = lock.withLock {
        withContext(io) { dir.listFiles().orEmpty().forEach { it.delete() } }
        Unit
    }

    private fun reportFiles(): List<File> = dir.listFiles { file ->
        file.isFile && file.name.endsWith(".json")
    }.orEmpty().toList()

    private fun fileName(report: ScanReport): String {
        val safeId = report.id.filter { it.isLetterOrDigit() || it == '-' }.ifEmpty { "report" }
        // Zero padded so a plain name sort is also a time sort.
        return String.format(Locale.ROOT, "%015d-%s.json", report.startedAtMs, safeId)
    }

    companion object {
        const val DEFAULT_LIMIT = 30
    }
}
