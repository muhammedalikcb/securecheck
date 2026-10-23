package com.muhammedalikocabey.securecheck.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.muhammedalikocabey.securecheck.data.history.ReportJson
import com.muhammedalikocabey.securecheck.domain.export.ExportFormat
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Writes a report to the app cache and returns a content URI for the share sheet.
 * Only the newest export is kept, and the cache folder is the only place a report ever leaves private storage.
 */
class ReportExporter @Inject constructor(private val context: Context) {
    /** Blocking file work, call it off the main thread. */
    @Throws(IOException::class)
    fun export(report: ScanReport, format: ExportFormat): Uri {
        val dir = File(context.cacheDir, DIRECTORY)
        dir.deleteRecursively()
        if (!dir.mkdirs()) throw IOException("Cannot create export folder")
        val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.ROOT).format(Date(report.startedAtMs))
        val file = File(dir, "securecheck-$stamp.${format.extension}")
        file.writeText(
            when (format) {
                ExportFormat.JSON -> ReportJson.encode(report, prettyPrint = true)
                ExportFormat.HTML -> HtmlReport.render(report)
            },
        )
        return FileProvider.getUriForFile(context, authority(context), file)
    }

    companion object {
        const val DIRECTORY = "exports"

        fun authority(context: Context) = "${context.packageName}.files"
    }
}
