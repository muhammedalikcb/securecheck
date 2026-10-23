package com.muhammedalikocabey.securecheck.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
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
import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.presentation.components.rememberReduceMotion
import com.muhammedalikocabey.securecheck.presentation.dashboard.CategoryScreen
import com.muhammedalikocabey.securecheck.presentation.dashboard.DashboardScreen
import com.muhammedalikocabey.securecheck.presentation.dashboard.DetailSheet
import com.muhammedalikocabey.securecheck.presentation.dashboard.ScanScreen
import com.muhammedalikocabey.securecheck.presentation.history.HistoryScreen
import com.muhammedalikocabey.securecheck.presentation.scan.ScanUiState
import com.muhammedalikocabey.securecheck.presentation.settings.SettingsScreen
import com.muhammedalikocabey.securecheck.presentation.theme.SecureCheckTheme
import com.muhammedalikocabey.securecheck.presentation.theme.isDark

private sealed interface Screen {
    data object Scan : Screen

    data object Dashboard : Screen

    data class Detail(val category: Category) : Screen

    data object History : Screen

    data object Settings : Screen
}

/** Pages opened from the dashboard toolbar. Kept apart from [Category] so back always returns to the dashboard. */
private enum class Page { HISTORY, SETTINGS }

/** Holds the last finished report so a screen can finish its exit animation after a new scan has started. */
private class LastReport {
    var value: ScanReport? = null
}

@Composable
fun SecureCheckApp(
    state: ScanUiState,
    history: List<ScanReport> = emptyList(),
    settings: Settings = Settings(),
    actions: AppActions = AppActions(),
) {
    SecureCheckTheme(darkTheme = settings.theme.isDark(isSystemInDarkTheme())) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        ) {
            Content(state, history, settings, actions)
        }
    }
}

@Composable
private fun Content(state: ScanUiState, history: List<ScanReport>, settings: Settings, actions: AppActions) {
    var category by rememberSaveable { mutableStateOf<Category?>(null) }
    var page by rememberSaveable { mutableStateOf<Page?>(null) }
    var selected by rememberSaveable { mutableStateOf<CheckId?>(null) }
    val last = remember { LastReport() }
    if (state is ScanUiState.Done) last.value = state.report
    val report = (state as? ScanUiState.Done)?.report ?: last.value

    val screen: Screen = when {
        state !is ScanUiState.Done -> Screen.Scan
        page == Page.HISTORY -> Screen.History
        page == Page.SETTINGS -> Screen.Settings
        category == null -> Screen.Dashboard
        else -> Screen.Detail(category!!)
    }

    LaunchedEffect(page) { if (page == Page.HISTORY) actions.onOpenHistory() }

    BackHandler(enabled = (category != null || page != null) && selected == null) {
        if (page != null) page = null else category = null
    }

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
                        actions.onScan()
                    },
                    diff = (state as? ScanUiState.Done)?.diff,
                    onOpenHistory = { page = Page.HISTORY },
                    onOpenSettings = { page = Page.SETTINGS },
                    onShare = actions.onShare,
                )
            }
            Screen.History -> HistoryScreen(history, padding, onBack = { page = null })
            Screen.Settings -> SettingsScreen(
                settings = settings,
                versionName = report?.app?.versionName.orEmpty(),
                actions = actions,
                contentPadding = padding,
                onBack = { page = null },
            )
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
        DetailSheet(shown, onDismiss = { selected = null }, onRerun = { actions.onRerun(shown.id) })
    }
}
