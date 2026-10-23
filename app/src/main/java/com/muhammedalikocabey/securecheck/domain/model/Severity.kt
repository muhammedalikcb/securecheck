package com.muhammedalikocabey.securecheck.domain.model

/** How much a confirmed finding costs in the score. Weights are documented in the README. */
enum class Severity(val weight: Int) {
    CRITICAL(25),
    HIGH(15),
    MEDIUM(8),
    LOW(3),
    INFO(0),
}

/** How sure a check is about its own result. Client-side checks are heuristics, so this is part of the result. */
enum class Confidence(val factor: Float) {
    HIGH(1f),
    MEDIUM(0.6f),
    LOW(0.3f),
}
