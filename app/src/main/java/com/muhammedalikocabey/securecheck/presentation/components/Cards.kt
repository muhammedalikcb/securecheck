package com.muhammedalikocabey.securecheck.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.Category
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.CheckStatus
import com.muhammedalikocabey.securecheck.presentation.content.labelRes
import com.muhammedalikocabey.securecheck.presentation.content.summary
import com.muhammedalikocabey.securecheck.presentation.content.text
import com.muhammedalikocabey.securecheck.presentation.theme.NumericStyle

/** A flat, outlined container. Depth comes from the border, not from a shadow, so it reads the same in both themes. */
@Composable
fun OutlinedCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(content = content)
    }
}

@Composable
fun ResultRow(result: CheckResult, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val style = result.style()
    val statusLabel = stringResource(style.label)
    Row(
        modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = stringResource(R.string.action_show_details),
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { stateDescription = statusLabel }
            .heightIn(min = 64.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatusGlyph(style.tone, style.icon)
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(result.id.text().title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                result.summary(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            SecureIcons.Chevron,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** Rows separated by hairlines, inside one card. */
@Composable
fun ResultList(results: List<CheckResult>, onSelect: (CheckResult) -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(modifier.fillMaxWidth()) {
        results.forEachIndexed { index, result ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            ResultRow(result, onClick = { onSelect(result) })
        }
    }
}

@Composable
fun CategoryCard(category: Category, results: List<CheckResult>, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val tone = categoryTone(results)
    val clear = results.count { it.status != CheckStatus.FINDING && it.status != CheckStatus.UNAVAILABLE }
    val name = stringResource(category.labelRes)
    val summary = stringResource(R.string.category_clear, clear, results.size)
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .clickable(
                    onClickLabel = stringResource(R.string.action_show_details),
                    role = Role.Button,
                    onClick = onClick,
                )
                .heightIn(min = 112.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatusGlyph(tone, category.icon())
            Column {
                Text(name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    summary,
                    style = MaterialTheme.typography.bodyMedium.merge(NumericStyle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
