package com.muhammedalikocabey.securecheck.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.history.ReportDiff
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.presentation.components.OutlinedCard
import com.muhammedalikocabey.securecheck.presentation.components.ScreenHeader
import com.muhammedalikocabey.securecheck.presentation.components.SecureIcons
import com.muhammedalikocabey.securecheck.presentation.components.StatusBadge
import com.muhammedalikocabey.securecheck.presentation.components.tone
import com.muhammedalikocabey.securecheck.presentation.content.labelRes
import com.muhammedalikocabey.securecheck.presentation.theme.NumericStyle
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    reports: List<ScanReport>,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.widthIn(max = 640.dp).fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "header") { ScreenHeader(stringResource(R.string.history_title), onBack) }
            if (reports.isEmpty()) {
                item(key = "empty") { EmptyState() }
            } else {
                itemsIndexed(reports, key = { _, report -> report.id }) { index, report ->
                    // The list is newest first, so the scan before this one is the next entry.
                    HistoryRow(report, previous = reports.getOrNull(index + 1))
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(report: ScanReport, previous: ScanReport?) {
    val score = report.score
    val date = remember(report.startedAtMs) {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(report.startedAtMs))
    }
    OutlinedCard(Modifier.fillMaxWidth().semantics(mergeDescendants = true) {}) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    date,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    stringResource(R.string.history_score, score.value),
                    style = MaterialTheme.typography.titleMedium.merge(NumericStyle),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (previous != null && score.value != previous.score.value) {
                    Text(
                        signed(score.value - previous.score.value),
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.merge(NumericStyle),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusBadge(
                    tone = score.risk.tone(),
                    icon = if (score.findingCount == 0) SecureIcons.Check else SecureIcons.Alert,
                    text = stringResource(score.risk.labelRes),
                )
                Text(
                    pluralStringResource(R.plurals.findings_count, score.findingCount, score.findingCount),
                    style = MaterialTheme.typography.bodyMedium.merge(NumericStyle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                changeLine(report, previous),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun changeLine(report: ScanReport, previous: ScanReport?): String {
    if (previous == null) return stringResource(R.string.history_first_scan)
    val diff = ReportDiff.between(previous, report)
    return if (diff.hasChanges) {
        stringResource(R.string.history_changes, diff.newFindings.size, diff.resolved.size)
    } else {
        stringResource(R.string.history_no_changes)
    }
}

private fun signed(value: Int): String = String.format(Locale.getDefault(), "%+d", value)

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            SecureIcons.History,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            stringResource(R.string.history_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.history_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
