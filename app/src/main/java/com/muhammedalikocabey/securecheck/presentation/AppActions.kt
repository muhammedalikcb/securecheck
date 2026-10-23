package com.muhammedalikocabey.securecheck.presentation

import com.muhammedalikocabey.securecheck.domain.export.ExportFormat
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.settings.ThemeMode

/** Everything the screens can ask the host to do. The defaults do nothing, which keeps previews and tests short. */
data class AppActions(
    val onScan: () -> Unit = {},
    val onRerun: (CheckId) -> Unit = {},
    val onShare: (ExportFormat) -> Unit = {},
    val onOpenHistory: () -> Unit = {},
    val onTheme: (ThemeMode) -> Unit = {},
    val onSaveHistory: (Boolean) -> Unit = {},
    val onDailyScan: (Boolean) -> Unit = {},
    val onClearHistory: () -> Unit = {},
    /** Null when the platform has no per-app language screen (before Android 13). */
    val onOpenLanguage: (() -> Unit)? = null,
)
