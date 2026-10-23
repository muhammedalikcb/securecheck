package com.muhammedalikocabey.securecheck.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.Category
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.presentation.components.CategoryCard
import com.muhammedalikocabey.securecheck.presentation.components.OutlinedCard
import com.muhammedalikocabey.securecheck.presentation.components.ResultList
import com.muhammedalikocabey.securecheck.presentation.components.ScoreRing
import com.muhammedalikocabey.securecheck.presentation.components.SecureIcons
import com.muhammedalikocabey.securecheck.presentation.components.StatusBadge
import com.muhammedalikocabey.securecheck.presentation.components.tone
import com.muhammedalikocabey.securecheck.presentation.content.labelRes
import com.muhammedalikocabey.securecheck.presentation.theme.NumericStyle
import com.muhammedalikocabey.securecheck.presentation.theme.SecureTheme

private const val TOP_FINDINGS = 3
private val ContentMaxWidth = 640.dp

@Composable
fun DashboardScreen(
    report: ScanReport,
    contentPadding: PaddingValues,
    onSelectCheck: (CheckResult) -> Unit,
    onSelectCategory: (Category) -> Unit,
    onScanAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.widthIn(max = ContentMaxWidth).fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "summary") { Summary(report) }
            if (report.app.debugBuild) item(key = "debug") { DebugBanner() }

            val top = report.prioritizedFindings().take(TOP_FINDINGS)
            if (top.isNotEmpty()) {
                item(key = "fix-title") { SectionTitle(stringResource(R.string.section_fix_first)) }
                item(key = "fix-list") { ResultList(top, onSelect = onSelectCheck) }
            }

            item(key = "categories-title") { SectionTitle(stringResource(R.string.section_categories)) }
            items(Category.entries.chunked(2), key = { row -> row.first().name }) { row ->
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    for (category in row) {
                        CategoryCard(
                            category,
                            report.results.filter { it.id.category == category },
                            onClick = { onSelectCategory(category) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }

            item(key = "scan-again") {
                Button(
                    onClick = onScanAgain,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                ) { Text(stringResource(R.string.action_scan_again)) }
            }
        }
    }
}

@Composable
private fun Summary(report: ScanReport) {
    val score = report.score
    val device = report.device
    val tone = score.risk.tone()
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScoreRing(score.value, score.risk)
        StatusBadge(
            tone = tone,
            icon = if (score.findingCount == 0) SecureIcons.Check else SecureIcons.Alert,
            text = stringResource(score.risk.labelRes),
        )
        Text(
            if (score.findingCount == 0) {
                stringResource(R.string.summary_clean)
            } else {
                pluralStringResource(R.plurals.findings_count, score.findingCount, score.findingCount)
            },
            style = MaterialTheme.typography.bodyLarge.merge(NumericStyle),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            stringResource(R.string.coverage_label, score.coverage),
            style = MaterialTheme.typography.bodyMedium.merge(NumericStyle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            stringResource(R.string.device_line, device.manufacturer, device.model, device.androidVersion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DebugBanner() {
    val tone = SecureTheme.colors.info
    OutlinedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            androidx.compose.material3.Icon(
                SecureIcons.Info,
                contentDescription = null,
                tint = tone.main,
            )
            Text(
                stringResource(R.string.debug_banner),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
internal fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier.semantics { heading() }.padding(top = 8.dp),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}
