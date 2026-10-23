package com.muhammedalikocabey.securecheck.data.check.runtime

/** Helpers for `/proc/<pid>/maps` lines: `start-end perms offset dev inode path`. */
object ProcessMaps {
    fun pathOf(line: String): String? {
        val fields = line.trim().split(Regex("\\s+"), limit = 6)
        return fields.getOrNull(5)?.takeIf { it.isNotEmpty() }
    }

    fun paths(lines: List<String>): Set<String> = lines.mapNotNullTo(linkedSetOf(), ::pathOf)

    fun containsAny(lines: List<String>, needles: List<String>): List<String> =
        needles.filter { needle -> lines.any { it.contains(needle, ignoreCase = true) } }
}
