package com.muhammedalikocabey.securecheck.data.export

import com.muhammedalikocabey.securecheck.domain.model.Category
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.RiskLevel
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A single self-contained HTML page with no scripts and no external resources, so it opens anywhere and
 * can be attached to a ticket as is. The wording is English on purpose: it is a technical record.
 */
object HtmlReport {
    fun render(report: ScanReport): String = buildString {
        appendLine("<!doctype html>")
        appendLine("<html lang=\"en\"><head><meta charset=\"utf-8\">")
        appendLine("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
        appendLine("<title>SecureCheck report</title>")
        appendLine("<style>$STYLE</style></head><body><main>")
        appendLine("<h1>SecureCheck report</h1>")
        appendLine(
            "<p class=\"muted\">${escape(timestamp(report.startedAtMs))} · app ${escape(report.app.versionName)}</p>",
        )
        appendLine("<p class=\"muted\">${escape(deviceLine(report))}</p>")
        appendLine(
            "<p class=\"score\"><span>${report.score.value}</span> / 100 · ${riskLabel(report.score.risk)} · " +
                "${report.score.findingCount} finding(s) · ${report.score.coverage}% of checks could run</p>",
        )
        for (category in Category.entries) {
            val rows = report.results.filter { it.id.category == category }
            if (rows.isEmpty()) continue
            appendLine("<h2>${categoryLabel(category)}</h2>")
            appendLine(
                "<table><thead><tr><th>Check</th><th>Result</th><th>Severity</th><th>Evidence</th></tr></thead><tbody>",
            )
            rows.forEach { appendLine(row(it)) }
            appendLine("</tbody></table>")
        }
        appendLine("<p class=\"muted\">Generated on the device. Results are heuristic signals, not a guarantee.</p>")
        appendLine("</main></body></html>")
    }

    private fun row(result: CheckResult): String {
        val evidence = result.evidence.joinToString("<br>") { "<code>${escape(it)}</code>" }
        val status = statusLabel(result)
        return "<tr class=\"${result.status.name.lowercase(Locale.ROOT)}\"><td>${escape(result.id.key)}</td>" +
            "<td>$status</td><td>${result.severity.name.lowercase(Locale.ROOT)}</td><td>$evidence</td></tr>"
    }

    private fun statusLabel(result: CheckResult): String = when (result.status) {
        CheckStatus.PASSED -> "Passed"
        CheckStatus.FINDING -> "Finding (${result.confidence.name.lowercase(Locale.ROOT)} confidence)"
        CheckStatus.INFO -> "Info"
        CheckStatus.EXPECTED -> "Expected"
        CheckStatus.UNAVAILABLE -> "Not run (${result.reason?.name?.lowercase(
            Locale.ROOT,
        )?.replace('_', ' ') ?: "unknown"})"
    }

    private fun riskLabel(risk: RiskLevel): String = when (risk) {
        RiskLevel.LOW -> "Low risk"
        RiskLevel.MODERATE -> "Moderate risk"
        RiskLevel.ELEVATED -> "High risk"
        RiskLevel.HIGH -> "Critical risk"
    }

    private fun categoryLabel(category: Category): String = when (category) {
        Category.DEVICE -> "Device integrity"
        Category.APP -> "App integrity"
        Category.RUNTIME -> "Runtime"
        Category.NETWORK -> "Network"
        Category.DATA -> "Data"
        Category.PLATFORM -> "Platform"
    }

    private fun deviceLine(report: ScanReport): String = with(report.device) {
        "$manufacturer $model · Android $androidVersion (API $sdkInt) · security patch $securityPatch"
    }

    private fun timestamp(ms: Long): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm 'UTC'", Locale.ROOT).apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(ms))

    internal fun escape(text: String): String = buildString(text.length) {
        for (c in text) {
            when (c) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                '\'' -> append("&#39;")
                else -> append(c)
            }
        }
    }

    private const val STYLE =
        ":root{color-scheme:light dark;--fg:#0f1a24;--bg:#f7f9fb;--line:#c9d3dc;--muted:#55667a;--bad:#b3261e}" +
            "@media(prefers-color-scheme:dark){" +
            ":root{--fg:#e6edf3;--bg:#0b1117;--line:#2a3948;--muted:#a9b7c4;--bad:#ff8a80}}" +
            "body{margin:0;background:var(--bg);color:var(--fg);font:16px/1.5 system-ui,sans-serif}" +
            "main{max-width:860px;margin:0 auto;padding:24px 16px}" +
            ".muted{color:var(--muted);margin:.25rem 0}.score span{font-size:2.5rem;font-weight:700}" +
            "table{width:100%;border-collapse:collapse;margin:.5rem 0 1.5rem}" +
            "th,td{text-align:left;padding:8px;border-bottom:1px solid var(--line);vertical-align:top}" +
            "tr.finding td:nth-child(2){color:var(--bad);font-weight:600}code{font-size:.85em;word-break:break-all}"
}
