package com.muhammedalikocabey.securecheck.presentation.scan

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.SecureCheckApp
import com.muhammedalikocabey.securecheck.di.AppComponent
import com.muhammedalikocabey.securecheck.domain.export.ExportFormat
import com.muhammedalikocabey.securecheck.presentation.AppActions
import com.muhammedalikocabey.securecheck.presentation.history.HistoryViewModel
import com.muhammedalikocabey.securecheck.presentation.settings.SettingsViewModel
import com.muhammedalikocabey.securecheck.presentation.theme.isDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import android.provider.Settings as AndroidSettings
import com.muhammedalikocabey.securecheck.presentation.SecureCheckApp as SecureCheckScreen

class MainActivity : ComponentActivity() {
    private val component: AppComponent get() = (application as SecureCheckApp).component
    private val scan: ScanViewModel by viewModels { component.viewModelFactory() }
    private val history: HistoryViewModel by viewModels { component.viewModelFactory() }
    private val settings: SettingsViewModel by viewModels { component.viewModelFactory() }

    // The daily scan still runs without the permission, it just cannot announce a change.
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by scan.state.collectAsStateWithLifecycle()
            val saved by history.reports.collectAsStateWithLifecycle()
            val prefs by settings.settings.collectAsStateWithLifecycle()
            val dark = prefs.theme.isDark(isSystemInDarkTheme())
            // System bar icons follow the in-app theme, which can differ from the system one.
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                    navigationBarStyle = SystemBarStyle.auto(LIGHT_SCRIM, DARK_SCRIM) { dark },
                )
                onDispose { }
            }
            val actions = remember { buildActions() }
            SecureCheckScreen(state, saved, prefs, actions)
        }
    }

    private fun buildActions() = AppActions(
        onScan = scan::scan,
        onRerun = scan::rerun,
        onShare = ::share,
        onOpenHistory = history::refresh,
        onTheme = settings::setTheme,
        onSaveHistory = settings::setSaveHistory,
        onDailyScan = { enabled ->
            settings.setDailyScan(enabled)
            if (enabled) askForNotifications()
        },
        onClearHistory = {
            settings.clearHistory()
            history.refresh()
        },
        onOpenLanguage = if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {
            ({ openLanguageSettings() })
        } else {
            null
        },
    )

    private fun askForNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!granted) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openLanguageSettings() {
        val intent = Intent(AndroidSettings.ACTION_APP_LOCALE_SETTINGS, Uri.fromParts("package", packageName, null))
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Some builds ship without the per-app language screen; the row simply does nothing there.
        }
    }

    private fun share(format: ExportFormat) {
        val report = (scan.state.value as? ScanUiState.Done)?.report ?: return
        lifecycleScope.launch {
            try {
                val uri = withContext(Dispatchers.IO) { component.reportExporter().export(report, format) }
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = format.mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    clipData = ClipData.newRawUri(null, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(send, getString(R.string.share_chooser)))
            } catch (_: IOException) {
                Toast.makeText(this@MainActivity, R.string.share_failed, Toast.LENGTH_SHORT).show()
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(this@MainActivity, R.string.share_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private companion object {
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}
