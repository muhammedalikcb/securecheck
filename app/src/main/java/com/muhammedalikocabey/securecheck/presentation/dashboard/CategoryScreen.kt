package com.muhammedalikocabey.securecheck.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.Category
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.presentation.components.ResultList
import com.muhammedalikocabey.securecheck.presentation.components.ScreenHeader
import com.muhammedalikocabey.securecheck.presentation.content.labelRes
import com.muhammedalikocabey.securecheck.presentation.theme.NumericStyle

@Composable
fun CategoryScreen(
    category: Category,
    results: List<CheckResult>,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onSelectCheck: (CheckResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Findings first, then what could not run, then everything that is fine.
    val ordered = results.sortedWith(
        compareBy<CheckResult> {
            when (it.status) {
                CheckStatus.FINDING -> 0
                CheckStatus.UNAVAILABLE -> 1
                else -> 2
            }
        }.thenBy { it.severity.ordinal }.thenBy { it.id.ordinal },
    )
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.widthIn(max = 640.dp).fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "header") { ScreenHeader(stringResource(category.labelRes), onBack) }
            item(key = "summary") {
                val clear = results.count { it.status != CheckStatus.FINDING && it.status != CheckStatus.UNAVAILABLE }
                Text(
                    stringResource(R.string.category_clear, clear, results.size),
                    style = MaterialTheme.typography.titleMedium.merge(NumericStyle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item(key = "list") { ResultList(ordered, onSelect = onSelectCheck) }
        }
    }
}
