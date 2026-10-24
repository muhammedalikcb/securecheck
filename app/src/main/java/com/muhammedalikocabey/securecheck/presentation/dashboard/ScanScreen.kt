package com.muhammedalikocabey.securecheck.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.presentation.components.ContentMaxWidth
import com.muhammedalikocabey.securecheck.presentation.components.ListRowMinHeight
import com.muhammedalikocabey.securecheck.presentation.components.OutlinedCard
import com.muhammedalikocabey.securecheck.presentation.components.ResultRow
import com.muhammedalikocabey.securecheck.presentation.components.rememberReduceMotion
import com.muhammedalikocabey.securecheck.presentation.content.text
import com.muhammedalikocabey.securecheck.presentation.theme.NumericStyle
import com.muhammedalikocabey.securecheck.presentation.theme.SecureTheme

private const val RECENT = 5
private const val RUNNING_SHOWN = 3

/** Determinate progress, so the wait has a visible end. Results appear as they arrive. */
@Composable
fun ScanScreen(
    completed: Int,
    total: Int,
    results: List<CheckResult>,
    running: List<CheckId>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val reduceMotion = rememberReduceMotion()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.widthIn(max = ContentMaxWidth).fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "head") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        stringResource(R.string.scan_title),
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    // Announced politely as it changes; the bar itself carries no extra label.
                    Column(
                        Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (total == 0) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = SecureTheme.colors.track,
                            )
                        } else {
                            LinearProgressIndicator(
                                progress = { completed / total.toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = SecureTheme.colors.track,
                            )
                            Text(
                                stringResource(R.string.scan_progress, completed, total),
                                style = MaterialTheme.typography.titleMedium.merge(NumericStyle),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    if (running.isNotEmpty()) {
                        val names = running.take(RUNNING_SHOWN).map {
                            stringResource(it.text().title)
                        }.joinToString(", ")
                        Text(
                            stringResource(R.string.scan_running, names),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Text(
                        stringResource(R.string.scan_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(results.takeLast(RECENT).asReversed(), key = { it.id.key }) { result ->
                OutlinedCard(Modifier.fillMaxWidth().then(if (reduceMotion) Modifier else Modifier.animateItem())) {
                    ResultRow(result, onClick = null)
                }
            }
        }
    }
}

/** Shown when the scan stopped on an error nobody planned for. The only way forward is to try again. */
@Composable
fun ScanFailed(onRetry: () -> Unit, contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
        Column(
            Modifier.widthIn(max = ContentMaxWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                stringResource(R.string.scan_failed_title),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.scan_failed_desc),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onRetry, modifier = Modifier.heightIn(min = ListRowMinHeight)) {
                Text(stringResource(R.string.action_scan_again))
            }
        }
    }
}
