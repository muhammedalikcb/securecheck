package com.muhammedalikocabey.securecheck.data.check.device

import com.muhammedalikocabey.securecheck.data.system.BuildFields
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.Signal
import com.muhammedalikocabey.securecheck.data.system.SystemProps
import com.muhammedalikocabey.securecheck.data.system.confidence
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck
import javax.inject.Inject

class EmulatorCheck @Inject constructor(
    private val build: BuildFields,
    private val info: BuildInfo,
    private val props: SystemProps,
    private val files: ProcFiles,
) : SecurityCheck {
    override val id = CheckId.EMULATOR

    override suspend fun run(): CheckResult {
        val signals = signals(build, props.get("ro.kernel.qemu") == "1", QEMU_FILES.filter(files::exists))
        val confidence = signals.confidence() ?: return CheckResult.passed(id)
        val evidence = signals.map { it.description }
        return if (info.isDebug) CheckResult.expected(id, evidence) else CheckResult.finding(id, evidence, confidence)
    }

    companion object {
        private val QEMU_FILES = listOf("/dev/qemu_pipe", "/dev/socket/qemud", "/dev/goldfish_pipe")

        fun signals(fields: BuildFields, qemuProp: Boolean, qemuFiles: List<String>): List<Signal> = buildList {
            val fingerprint = fields.fingerprint.lowercase()
            if (listOf("generic", "emulator", "sdk_gphone", "vbox").any { it in fingerprint }) {
                add(Signal("fingerprint: ${fields.fingerprint}", 2))
            }
            val model = fields.model.lowercase()
            if (listOf("emulator", "android sdk built for", "sdk_gphone", "google_sdk").any { it in model }) {
                add(Signal("model: ${fields.model}", 2))
            }
            if (fields.hardware in setOf("goldfish", "ranchu", "vbox86")) add(Signal("hardware: ${fields.hardware}", 3))
            if (fields.manufacturer.contains("genymotion", ignoreCase = true)) {
                add(Signal("manufacturer: ${fields.manufacturer}", 3))
            }
            val product = fields.product.lowercase()
            if (listOf("sdk", "emulator", "simulator").any {
                    it in product
                }
            ) {
                add(Signal("product: ${fields.product}", 1))
            }
            if (qemuProp) add(Signal("ro.kernel.qemu=1", 3))
            qemuFiles.forEach { add(Signal("qemu device: $it", 3)) }
        }
    }
}
