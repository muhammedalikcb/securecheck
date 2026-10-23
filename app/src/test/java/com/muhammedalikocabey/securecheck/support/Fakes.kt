package com.muhammedalikocabey.securecheck.support

import com.muhammedalikocabey.securecheck.data.system.BuildFields
import com.muhammedalikocabey.securecheck.data.system.BuildInfo
import com.muhammedalikocabey.securecheck.data.system.PortProbe
import com.muhammedalikocabey.securecheck.data.system.ProcFiles
import com.muhammedalikocabey.securecheck.data.system.SystemProps
import com.muhammedalikocabey.securecheck.domain.model.AppSummary
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.DeviceSummary
import com.muhammedalikocabey.securecheck.domain.scan.ScanEnvironment
import com.muhammedalikocabey.securecheck.domain.scan.SecurityCheck

class FakeFiles(
    private val lines: Map<String, List<String>> = emptyMap(),
    private val existing: Set<String> = emptySet(),
    private val dirs: Map<String, List<String>> = emptyMap(),
) : ProcFiles {
    override fun readLines(path: String) = lines[path]

    override fun exists(path: String) = path in existing || path in lines

    override fun listNames(path: String) = dirs[path]
}

class FakeProps(private val values: Map<String, String> = emptyMap()) : SystemProps {
    override fun get(key: String) = values[key].orEmpty()
}

class FakePorts(private val open: Boolean = false) : PortProbe {
    override fun isOpen(host: String, port: Int, timeoutMs: Int) = open
}

class FakeEnvironment(override val isDebugBuild: Boolean = false) : ScanEnvironment {
    override fun device() = DeviceSummary("Acme", "Phone 1", "15", 35, "2026-01-05")

    override fun app() = AppSummary("2.0.0", 3, isDebugBuild)
}

class StubCheck(override val id: CheckId, private val body: suspend () -> CheckResult) : SecurityCheck {
    override suspend fun run() = body()
}

fun buildInfo(isDebug: Boolean = false, expectedCert: String = "") = BuildInfo(
    packageName = "com.example.app",
    isDebug = isDebug,
    versionName = "2.0.0",
    versionCode = 3,
    builtTargetSdk = 36,
    expectedCertSha256 = expectedCert,
    integrityProjectNumber = 0,
)

fun buildFields(
    fingerprint: String = "google/raven/raven:15/AP1A/123:user/release-keys",
    model: String = "Pixel 6 Pro",
    manufacturer: String = "Google",
    hardware: String = "raven",
    product: String = "raven",
    tags: String = "release-keys",
    securityPatch: String = "2026-01-05",
) = BuildFields(fingerprint, model, manufacturer, "google", "raven", product, hardware, "raven", tags, securityPatch)
