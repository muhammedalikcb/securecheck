package com.muhammedalikocabey.securecheck.data.system

/** Compile-time facts about this build, injected so checks can be tested with any combination. */
data class BuildInfo(
    val packageName: String,
    val isDebug: Boolean,
    val versionName: String,
    val versionCode: Long,
    val builtTargetSdk: Int,
    val expectedCertSha256: String,
    val integrityProjectNumber: Long,
)

/** The `android.os.Build` fields the emulator heuristics read. */
data class BuildFields(
    val fingerprint: String,
    val model: String,
    val manufacturer: String,
    val brand: String,
    val device: String,
    val product: String,
    val hardware: String,
    val board: String,
    val tags: String,
    val securityPatch: String,
)
