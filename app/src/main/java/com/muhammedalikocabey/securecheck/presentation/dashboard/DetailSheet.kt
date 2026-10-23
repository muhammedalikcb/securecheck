package com.muhammedalikocabey.securecheck.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.presentation.components.StatusBadge
import com.muhammedalikocabey.securecheck.presentation.content.labelRes
import com.muhammedalikocabey.securecheck.presentation.content.summary
import com.muhammedalikocabey.securecheck.presentation.content.text

private const val MASVS_CONTROLS = "https://mas.owasp.org/MASVS/controls/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSheet(result: CheckResult, onDismiss: () -> Unit, onRerun: () -> Unit) {
    val text = result.id.text()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uriHandler = LocalUriHandler.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(text.title),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatusBadge(result)
                    if (result.status != CheckStatus.UNAVAILABLE) {
                        Text(
                            stringResource(result.confidence.labelRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Section(stringResource(R.string.sheet_found)) {
                Text(
                    result.summary(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (result.evidence.isEmpty()) {
                    Text(
                        stringResource(R.string.sheet_no_evidence),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    for (line in result.evidence) {
                        Text(
                            line,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Section(stringResource(R.string.sheet_why)) {
                Text(
                    stringResource(text.why),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (result.status == CheckStatus.FINDING) {
                Section(stringResource(R.string.sheet_fix)) {
                    Text(
                        stringResource(text.fix),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            result.id.masvs?.let { control ->
                Section(stringResource(R.string.sheet_standard)) {
                    Text(
                        control,
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    TextButton(
                        onClick = { runCatching { uriHandler.openUri("$MASVS_CONTROLS$control/") } },
                        modifier = Modifier.heightIn(min = 48.dp),
                    ) { Text(stringResource(R.string.action_open_standard)) }
                }
            }

            OutlinedButton(onClick = onRerun, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                Text(stringResource(R.string.action_rerun))
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        content()
    }
}
