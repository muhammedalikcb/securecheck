package com.muhammedalikocabey.securecheck.data.history

import com.muhammedalikocabey.securecheck.domain.model.AppSummary
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.DeviceSummary
import com.muhammedalikocabey.securecheck.domain.model.RiskLevel
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.domain.model.ScanScore
import com.muhammedalikocabey.securecheck.domain.model.Severity
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * The on-disk and export format of a report. [SCHEMA_VERSION] changes only when a field is removed or changes
 * meaning. Adding fields does not need a bump because readers ignore keys they do not know.
 */
object ReportJson {
    const val SCHEMA_VERSION = 1

    private val compact = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val pretty = Json(compact) { prettyPrint = true }

    fun encode(report: ScanReport, prettyPrint: Boolean = false): String =
        (if (prettyPrint) pretty else compact).encodeToString(ReportDto.serializer(), report.toDto())

    /** Returns null for corrupt input or a schema newer than this build understands. */
    fun decode(text: String): ScanReport? = try {
        compact.decodeFromString(ReportDto.serializer(), text).takeIf { it.schemaVersion <= SCHEMA_VERSION }
            ?.toDomain()
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}

@Serializable
internal data class ReportDto(
    val schemaVersion: Int,
    val id: String,
    val startedAtMs: Long,
    val durationMs: Long,
    val device: DeviceDto,
    val app: AppDto,
    val score: ScoreDto,
    val results: List<ResultDto>,
)

@Serializable
internal data class DeviceDto(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
)

@Serializable
internal data class AppDto(val versionName: String, val versionCode: Long, val debugBuild: Boolean)

@Serializable
internal data class ScoreDto(val value: Int, val risk: String, val coverage: Int, val findingCount: Int)

@Serializable
internal data class ResultDto(
    val id: String,
    val status: String,
    val severity: String,
    val confidence: String,
    val evidence: List<String> = emptyList(),
    val reason: String? = null,
    val durationMs: Long = 0,
)

private fun ScanReport.toDto() = ReportDto(
    schemaVersion = ReportJson.SCHEMA_VERSION,
    id = id,
    startedAtMs = startedAtMs,
    durationMs = durationMs,
    device = DeviceDto(device.manufacturer, device.model, device.androidVersion, device.sdkInt, device.securityPatch),
    app = AppDto(app.versionName, app.versionCode, app.debugBuild),
    score = ScoreDto(score.value, score.risk.name, score.coverage, score.findingCount),
    results = results.map {
        ResultDto(
            it.id.key,
            it.status.name,
            it.severity.name,
            it.confidence.name,
            it.evidence,
            it.reason?.name,
            it.durationMs,
        )
    },
)

private fun ReportDto.toDomain(): ScanReport? {
    val risk = score.risk.asEnum<RiskLevel>() ?: return null
    return ScanReport(
        id = id,
        startedAtMs = startedAtMs,
        durationMs = durationMs,
        device = DeviceSummary(
            device.manufacturer,
            device.model,
            device.androidVersion,
            device.sdkInt,
            device.securityPatch,
        ),
        app = AppSummary(app.versionName, app.versionCode, app.debugBuild),
        // A check removed in a later release simply drops out of old reports instead of invalidating them.
        results = results.mapNotNull { it.toDomain() },
        score = ScanScore(score.value, risk, score.coverage, score.findingCount),
    )
}

private fun ResultDto.toDomain(): CheckResult? {
    val checkId = CheckId.entries.firstOrNull { it.key == id } ?: return null
    return CheckResult(
        id = checkId,
        status = status.asEnum<CheckStatus>() ?: return null,
        severity = severity.asEnum<Severity>() ?: return null,
        confidence = confidence.asEnum<Confidence>() ?: return null,
        evidence = evidence,
        reason = reason?.asEnum<UnavailableReason>(),
        durationMs = durationMs,
    )
}

private inline fun <reified E : Enum<E>> String.asEnum(): E? = enumValues<E>().firstOrNull { it.name == this }
