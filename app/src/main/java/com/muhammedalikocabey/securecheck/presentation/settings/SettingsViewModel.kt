package com.muhammedalikocabey.securecheck.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammedalikocabey.securecheck.domain.history.ReportStore
import com.muhammedalikocabey.securecheck.domain.settings.ScanScheduler
import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.domain.settings.SettingsStore
import com.muhammedalikocabey.securecheck.domain.settings.ThemeMode
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val store: SettingsStore,
    private val scheduler: ScanScheduler,
    private val reports: ReportStore,
) : ViewModel() {
    val settings: StateFlow<Settings> = store.settings

    fun setTheme(mode: ThemeMode) = store.update { it.copy(theme = mode) }

    fun setDailyScan(enabled: Boolean) {
        // The daily scan has nothing to compare against without history, so it cannot run on its own.
        val allowed = enabled && store.settings.value.saveHistory
        store.update { it.copy(dailyScan = allowed) }
        scheduler.setDaily(allowed)
    }

    fun setSaveHistory(enabled: Boolean) {
        store.update { it.copy(saveHistory = enabled, dailyScan = it.dailyScan && enabled) }
        if (!enabled) scheduler.setDaily(false)
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                reports.clear()
            } catch (_: IOException) {
                // Nothing more to do: the next list read shows whatever is left.
            }
        }
    }
}
