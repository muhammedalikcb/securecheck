package com.muhammedalikocabey.securecheck.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.domain.model.Category
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.presentation.components.rememberReduceMotion
import com.muhammedalikocabey.securecheck.presentation.dashboard.CategoryScreen
import com.muhammedalikocabey.securecheck.presentation.dashboard.DashboardScreen
import com.muhammedalikocabey.securecheck.presentation.dashboard.DetailSheet
import com.muhammedalikocabey.securecheck.presentation.dashboard.ScanScreen
import com.muhammedalikocabey.securecheck.presentation.scan.ScanUiState
import com.muhammedalikocabey.securecheck.presentation.theme.SecureCheckTheme

private sealed interface Screen {
    data object Scan : Screen

    data object Dashboard : Screen

    data class Detail(val category: Category) : Screen
}

/** Holds the last finished report so a screen can finish its exit animation after a new scan has started. */
private class LastReport {
    var value: ScanReport? = null
}

@Composable
fun SecureCheckApp(state: ScanUiState, onScan: () -> Unit, onRerun: (CheckId) -> Unit) {
    SecureCheckTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        ) {
            Content(state, onScan, onRerun)
        }
    }
}

@Composable
private fun Content(state: ScanUiState, onScan: () -> Unit, onRerun: (CheckId) -> Unit) {
    var category by rememberSaveable { mutableStateOf<Category?>(null) }
    var selected by rememberSaveable { mutableStateOf<CheckId?>(null) }
    val last = remember { LastReport() }
    if (state is ScanUiState.Done) last.value = state.report
    val report = (state as? ScanUiState.Done)?.report ?: last.value

    val screen: Screen = when {
        state !is ScanUiState.Done -> Screen.Scan
        category == null -> Screen.Dashboard
        else -> Screen.Detail(category!!)
    }

    BackHandler(enabled = category != null && selected == null) { category = null }

    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val padding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = insets.calculateTopPadding() + 16.dp,
        bottom = insets.calculateBottomPadding() + 24.dp,
    )
    val reduceMotion = rememberReduceMotion()

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            if (reduceMotion) {
                EnterTransition.None togetherWith ExitTransition.None
            } else {
                fadeIn(tween(220, delayMillis = 90)) togetherWith fadeOut(tween(90))
            }
        },
        label = "screen",
    ) { target ->
        when (target) {
            Screen.Scan -> {
                val scanning = state as? ScanUiState.Scanning
                ScanScreen(scanning?.completed ?: 0, scanning?.total ?: 0, scanning?.results.orEmpty(), padding)
            }
            Screen.Dashboard -> report?.let {
                DashboardScreen(
                    report = it,
                    contentPadding = padding,
                    onSelectCheck = { result -> selected = result.id },
                    onSelectCategory = { picked -> category = picked },
                    onScanAgain = {
                        category = null
                        selected = null
                        onScan()
                    },
                )
            }
            is Screen.Detail -> report?.let {
                CategoryScreen(
                    category = target.category,
                    results = it.results.filter { result -> result.id.category == target.category },
                    contentPadding = padding,
                    onBack = { category = null },
                    onSelectCheck = { result -> selected = result.id },
                )
            }
        }
    }

    val shown = selected?.let { id -> report?.result(id) }
    if (shown != null) {
        DetailSheet(shown, onDismiss = { selected = null }, onRerun = { onRerun(shown.id) })
    }
}
