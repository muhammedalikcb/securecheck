package com.muhammedalikocabey.securecheck.presentation.components

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/** True when the user turned animations off in system settings. Motion is then replaced by an instant change. */
@Composable
fun rememberReduceMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        runCatching { Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
            .getOrDefault(false)
    }
}
