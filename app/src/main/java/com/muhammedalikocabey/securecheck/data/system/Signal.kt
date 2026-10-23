package com.muhammedalikocabey.securecheck.data.system

import com.muhammedalikocabey.securecheck.domain.model.Confidence

/** One observation a heuristic check made. The summed weight decides how much the check trusts its own verdict. */
data class Signal(val description: String, val weight: Int)

fun List<Signal>.confidence(): Confidence? {
    val total = sumOf { it.weight }
    return when {
        total >= STRONG -> Confidence.HIGH
        total >= MODERATE -> Confidence.MEDIUM
        total >= 1 -> Confidence.LOW
        else -> null
    }
}

private const val STRONG = 3
private const val MODERATE = 2
