package com.muhammedalikocabey.securecheck.domain.settings

import kotlinx.coroutines.flow.StateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class Settings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    /** Runs a scan about once a day in the background and notifies only when new findings appear. */
    val dailyScan: Boolean = false,
    /** Keeps finished reports on this device so scans can be compared. */
    val saveHistory: Boolean = true,
)

interface SettingsStore {
    val settings: StateFlow<Settings>

    fun update(transform: (Settings) -> Settings)
}

/** Registers or cancels the recurring background scan. */
interface ScanScheduler {
    fun setDaily(enabled: Boolean)
}
