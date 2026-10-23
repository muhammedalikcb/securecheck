package com.muhammedalikocabey.securecheck.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.presentation.components.OutlinedCard
import com.muhammedalikocabey.securecheck.presentation.components.ResultRow
import com.muhammedalikocabey.securecheck.presentation.theme.NumericStyle

private const val RECENT = 5

/** Determinate progress, so the wait has a visible end. Results appear as they arrive. */
@Composable
fun ScanScreen(
    completed: Int,
    total: Int,
    results: List<CheckResult>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.widthIn(max = 640.dp).fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "head") {
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        stringResource(R.string.scan_title),
                        modifier = Modifier.semantics { heading() }.padding(top = 24.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    LinearProgressIndicator(
                        progress = { if (total == 0) 0f else completed / total.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline,
                    )
                    Text(
                        stringResource(R.string.scan_progress, completed, total),
                        style = MaterialTheme.typography.titleMedium.merge(NumericStyle),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        stringResource(R.string.scan_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(results.takeLast(RECENT).asReversed(), key = { it.id.key }) { result ->
                OutlinedCard(Modifier.fillMaxWidth().animateItem()) { ResultRow(result, onClick = {}) }
            }
        }
    }
}
