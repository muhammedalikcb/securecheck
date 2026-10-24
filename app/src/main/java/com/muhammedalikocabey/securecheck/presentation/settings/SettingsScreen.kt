package com.muhammedalikocabey.securecheck.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.domain.settings.ThemeMode
import com.muhammedalikocabey.securecheck.presentation.AppActions
import com.muhammedalikocabey.securecheck.presentation.components.ContentMaxWidth
import com.muhammedalikocabey.securecheck.presentation.components.ListRowMinHeight
import com.muhammedalikocabey.securecheck.presentation.components.OutlinedCard
import com.muhammedalikocabey.securecheck.presentation.components.ScreenHeader
import com.muhammedalikocabey.securecheck.presentation.components.SecureIcons
import com.muhammedalikocabey.securecheck.presentation.dashboard.SectionTitle

private const val DISABLED_ALPHA = 0.5f

@Composable
fun SettingsScreen(
    settings: Settings,
    versionName: String,
    actions: AppActions,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmClear by rememberSaveable { mutableStateOf(false) }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.widthIn(max = ContentMaxWidth).fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "header") { ScreenHeader(stringResource(R.string.settings_title), onBack) }

            item(key = "appearance") {
                Section(R.string.settings_appearance) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        if (index > 0) HorizontalDivider()
                        ThemeOption(mode, selected = settings.theme == mode, onSelect = { actions.onTheme(mode) })
                    }
                }
            }

            item(key = "background") {
                Section(R.string.settings_background) {
                    SwitchRow(
                        title = stringResource(R.string.settings_history),
                        description = stringResource(R.string.settings_history_desc),
                        checked = settings.saveHistory,
                        enabled = true,
                        onChange = actions.onSaveHistory,
                    )
                    HorizontalDivider()
                    SwitchRow(
                        title = stringResource(R.string.settings_daily),
                        description = stringResource(dailyDescription(settings.saveHistory)),
                        checked = settings.dailyScan,
                        enabled = settings.saveHistory,
                        onChange = actions.onDailyScan,
                    )
                }
            }

            item(key = "clear") {
                OutlinedButton(
                    onClick = { confirmClear = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                ) { Text(stringResource(R.string.settings_clear)) }
            }

            actions.onOpenLanguage?.let { openLanguage ->
                item(key = "language") {
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth()
                                .clickable(
                                    onClickLabel = stringResource(R.string.action_open_system_settings),
                                    role = Role.Button,
                                    onClick = openLanguage,
                                )
                                .heightIn(min = ListRowMinHeight).padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                RowTitle(stringResource(R.string.settings_language))
                                RowDescription(stringResource(R.string.settings_language_desc))
                            }
                            Icon(
                                SecureIcons.External,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            item(key = "about") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionTitle(stringResource(R.string.settings_about))
                    Text(
                        stringResource(R.string.settings_version, versionName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    RowDescription(stringResource(R.string.settings_privacy))
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text(stringResource(R.string.settings_clear_title)) },
            text = { Text(stringResource(R.string.settings_clear_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmClear = false
                        actions.onClearHistory()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text(stringResource(R.string.settings_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text(stringResource(R.string.settings_cancel)) }
            },
        )
    }
}

@Composable
private fun Section(titleRes: Int, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(titleRes))
        OutlinedCard(Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
private fun ThemeOption(mode: ThemeMode, selected: Boolean, onSelect: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(16.dp))
        RowTitle(stringResource(mode.labelRes))
    }
}

@Composable
private fun SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth()
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onChange)
            .heightIn(min = ListRowMinHeight)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).alpha(if (enabled) 1f else DISABLED_ALPHA)) {
            RowTitle(title)
            RowDescription(description)
        }
        Spacer(Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            // The default off state is a pale track on a pale card; give it a visible edge.
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun RowTitle(text: String) =
    Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)

@Composable
private fun RowDescription(text: String) =
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

private fun dailyDescription(historyOn: Boolean): Int =
    if (historyOn) R.string.settings_daily_desc else R.string.settings_daily_needs_history

private val ThemeMode.labelRes: Int
    get() = when (this) {
        ThemeMode.SYSTEM -> R.string.theme_system
        ThemeMode.LIGHT -> R.string.theme_light
        ThemeMode.DARK -> R.string.theme_dark
    }
