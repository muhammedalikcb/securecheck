package com.muhammedalikocabey.securecheck.data.settings

import android.content.Context
import androidx.core.content.edit
import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.domain.settings.SettingsStore
import com.muhammedalikocabey.securecheck.domain.settings.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Three small values, so plain SharedPreferences is enough and keeps the dependency list short. */
@Singleton
class PreferencesSettingsStore @Inject constructor(context: Context) : SettingsStore {
    private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    private val state = MutableStateFlow(read())

    override val settings: StateFlow<Settings> = state.asStateFlow()

    override fun update(transform: (Settings) -> Settings) {
        synchronized(this) {
            val next = transform(state.value)
            prefs.edit {
                putString(THEME, next.theme.name)
                putBoolean(DAILY_SCAN, next.dailyScan)
                putBoolean(SAVE_HISTORY, next.saveHistory)
            }
            state.value = next
        }
    }

    private fun read(): Settings {
        val theme = ThemeMode.entries.firstOrNull { it.name == prefs.getString(THEME, null) } ?: ThemeMode.SYSTEM
        return Settings(
            theme = theme,
            dailyScan = prefs.getBoolean(DAILY_SCAN, false),
            saveHistory = prefs.getBoolean(SAVE_HISTORY, true),
        )
    }

    private companion object {
        const val FILE = "settings"
        const val THEME = "theme"
        const val DAILY_SCAN = "daily_scan"
        const val SAVE_HISTORY = "save_history"
    }
}
