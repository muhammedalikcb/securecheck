package com.muhammedalikocabey.securecheck.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.export.ExportFormat
import com.muhammedalikocabey.securecheck.domain.history.ReportDiff
import com.muhammedalikocabey.securecheck.domain.model.AppSummary
import com.muhammedalikocabey.securecheck.domain.model.CheckId
import com.muhammedalikocabey.securecheck.domain.model.CheckResult
import com.muhammedalikocabey.securecheck.domain.model.Confidence
import com.muhammedalikocabey.securecheck.domain.model.DeviceSummary
import com.muhammedalikocabey.securecheck.domain.model.ScanReport
import com.muhammedalikocabey.securecheck.domain.model.UnavailableReason
import com.muhammedalikocabey.securecheck.domain.scan.ScoreCalculator
import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.domain.settings.ThemeMode
import com.muhammedalikocabey.securecheck.presentation.content.labelRes
import com.muhammedalikocabey.securecheck.presentation.scan.ScanUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class SecureCheckAppTest {
    @get:Rule
    val rule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun sampleReport(debug: Boolean = false): ScanReport {
        val results = CheckId.entries.map { id ->
            when (id) {
                CheckId.ROOT -> CheckResult.finding(id, listOf("/system/xbin/su"))
                CheckId.SCREEN_LOCK -> CheckResult.finding(id, listOf("keyguard is not secure"), Confidence.MEDIUM)
                CheckId.USER_CA -> CheckResult.finding(id, listOf("2 user-installed CA certificates"))
                CheckId.PLAY_INTEGRITY -> CheckResult.unavailable(id, UnavailableReason.NOT_CONFIGURED)
                CheckId.VPN -> CheckResult.info(id, listOf("transport=VPN"))
                CheckId.DEBUGGABLE -> if (debug) CheckResult.expected(id) else CheckResult.passed(id)
                else -> CheckResult.passed(id)
            }
        }
        return ScanReport(
            id = "sample",
            startedAtMs = 0,
            durationMs = 1_200,
            device = DeviceSummary("Google", "Pixel 8", "15", 35, "2025-05-05"),
            app = AppSummary("2.0.0", 1, debug),
            results = results,
            score = ScoreCalculator().calculate(results),
        )
    }

    private fun show(state: ScanUiState, onScan: () -> Unit = {}, onRerun: (CheckId) -> Unit = {}) {
        rule.setContent {
            SecureCheckApp(state, actions = AppActions(onScan = onScan, onRerun = onRerun))
        }
    }

    private fun showWith(
        state: ScanUiState,
        history: List<ScanReport> = emptyList(),
        settings: Settings = Settings(),
        actions: AppActions = AppActions(),
    ) {
        rule.setContent { SecureCheckApp(state, history, settings, actions) }
    }

    private fun string(id: Int, vararg args: Any) = context.getString(id, *args)

    /**
     * Draws every window of the test (the sheet is its own window) into one PNG. Window capture through PixelCopy
     * does not run under Robolectric, so the views draw themselves instead.
     */
    private fun screenshot(name: String) {
        val views = rule.onAllNodes(isRoot()).fetchSemanticsNodes().mapNotNull { it.root as? View }
        val base = views.first()
        val bitmap = Bitmap.createBitmap(base.width, base.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        for (view in views) view.draw(canvas)
        val dir = File("build/screenshots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun `dashboard announces the score and lists what to fix first`() {
        val report = sampleReport()
        show(ScanUiState.Done(report))
        val description = string(R.string.score_description, report.score.value, string(report.score.risk.labelRes))
        rule.onNodeWithContentDescription(description).assertExists()
        rule.onNodeWithText(string(R.string.section_fix_first)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.check_root_title)).assertIsDisplayed()
        screenshot("dashboard-light-en")
    }

    @Test
    fun `selecting a finding opens the sheet with how to fix it`() {
        show(ScanUiState.Done(sampleReport()))
        rule.onAllNodesWithText(string(R.string.check_root_title))[0].performClick()
        rule.onNodeWithText(string(R.string.sheet_fix)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.action_rerun)).assertIsDisplayed()
        screenshot("sheet-finding-en")
    }

    @Test
    fun `rerun passes the check id to the caller`() {
        var rerun: CheckId? = null
        show(ScanUiState.Done(sampleReport()), onRerun = { rerun = it })
        rule.onAllNodesWithText(string(R.string.check_root_title))[0].performClick()
        rule.onNodeWithText(string(R.string.action_rerun)).performClick()
        assertEquals(CheckId.ROOT, rerun)
    }

    @Test
    fun `category card opens its list and back returns to the dashboard`() {
        show(ScanUiState.Done(sampleReport()))
        rule.onNode(hasScrollAction()).performScrollToNode(hasText(string(R.string.category_network)))
        rule.onNodeWithText(string(R.string.category_network)).performClick()
        rule.onNodeWithText(string(R.string.check_user_ca_title)).assertIsDisplayed()
        screenshot("category-network-en")
        rule.onNodeWithContentDescription(string(R.string.action_back)).performClick()
        rule.onNodeWithText(string(R.string.section_categories)).assertIsDisplayed()
    }

    @Test
    fun `scan again asks the caller to rescan`() {
        var scans = 0
        show(ScanUiState.Done(sampleReport()), onScan = { scans++ })
        rule.onNode(hasScrollAction()).performScrollToNode(hasText(string(R.string.action_scan_again)))
        rule.onNodeWithText(string(R.string.action_scan_again)).performClick()
        assertEquals(1, scans)
    }

    @Test
    fun `scanning shows determinate progress`() {
        val results = listOf(CheckResult.passed(CheckId.SCREEN_LOCK), CheckResult.passed(CheckId.ROOT))
        show(ScanUiState.Scanning(completed = 2, total = CheckId.entries.size, results = results))
        rule.onNodeWithText(string(R.string.scan_progress, 2, CheckId.entries.size)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.scan_hint)).assertIsDisplayed()
        screenshot("scanning-en")
    }

    @Test
    fun `debug builds explain why some results are expected`() {
        show(ScanUiState.Done(sampleReport(debug = true)))
        rule.onNodeWithText(string(R.string.debug_banner)).assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "tr-rTR-w411dp-h891dp-night-xxhdpi")
    fun `turkish dark dashboard`() {
        show(ScanUiState.Done(sampleReport()))
        rule.onNodeWithText(string(R.string.section_fix_first)).assertIsDisplayed()
        screenshot("dashboard-dark-tr")
    }

    @Test
    @Config(qualifiers = "w411dp-h891dp-night-xxhdpi")
    fun `dark sheet`() {
        show(ScanUiState.Done(sampleReport()))
        rule.onAllNodesWithText(string(R.string.check_screen_lock_title))[0].performClick()
        screenshot("sheet-dark-en")
    }

    @Test
    @Config(qualifiers = "w1024dp-h700dp-mdpi")
    fun `wide screens show the summary beside the findings`() {
        show(ScanUiState.Done(sampleReport()))
        rule.onNodeWithText(string(R.string.action_scan_again)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.section_fix_first)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.section_categories)).assertIsDisplayed()
        screenshot("dashboard-wide")
    }

    @Test
    @Config(qualifiers = "w411dp-h891dp-xxhdpi", fontScale = 1.6f)
    fun `large font keeps rows readable`() {
        show(ScanUiState.Done(sampleReport()))
        rule.onNodeWithText(string(R.string.section_fix_first)).assertIsDisplayed()
        screenshot("dashboard-large-font")
    }

    @Test
    fun `toolbar opens history and back returns to the dashboard`() {
        val newer = sampleReport()
        val older = sampleReport().copy(id = "older", startedAtMs = -86_400_000)
        var opened = 0
        showWith(ScanUiState.Done(newer), listOf(newer, older), actions = AppActions(onOpenHistory = { opened++ }))
        rule.onNodeWithContentDescription(string(R.string.action_history)).performClick()

        rule.onNodeWithText(string(R.string.history_title)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.history_first_scan)).assertExists()
        assertEquals(1, opened)
        screenshot("history-en")

        rule.onNodeWithContentDescription(string(R.string.action_back)).performClick()
        rule.onNodeWithContentDescription(string(R.string.action_history)).assertIsDisplayed()
    }

    @Test
    fun `empty history explains itself`() {
        showWith(ScanUiState.Done(sampleReport()))
        rule.onNodeWithContentDescription(string(R.string.action_history)).performClick()

        rule.onNodeWithText(string(R.string.history_empty_title)).assertIsDisplayed()
    }

    @Test
    fun `share menu offers both formats and reports the choice`() {
        val chosen = mutableListOf<ExportFormat>()
        showWith(ScanUiState.Done(sampleReport()), actions = AppActions(onShare = { chosen += it }))
        rule.onNodeWithContentDescription(string(R.string.action_share)).performClick()
        rule.onNodeWithText(string(R.string.share_json)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.share_html)).performClick()

        assertEquals(listOf(ExportFormat.HTML), chosen)
    }

    @Test
    fun `dashboard shows the change since the last scan`() {
        val diff = ReportDiff(scoreDelta = -10, newFindings = listOf(CheckId.ROOT), resolved = emptyList())
        showWith(ScanUiState.Done(sampleReport(), diff))

        rule.onNodeWithText(string(R.string.dashboard_since_last)).assertExists()
        rule.onNodeWithText(string(R.string.history_changes, 1, 0)).assertExists()
    }

    @Test
    fun `settings screen applies choices and confirms before deleting history`() {
        val themes = mutableListOf<ThemeMode>()
        var cleared = 0
        showWith(
            ScanUiState.Done(sampleReport()),
            actions = AppActions(onTheme = { themes += it }, onClearHistory = { cleared++ }),
        )
        rule.onNodeWithContentDescription(string(R.string.action_settings)).performClick()
        rule.onNodeWithText(string(R.string.settings_title)).assertIsDisplayed()
        screenshot("settings-en")

        rule.onNodeWithText(string(R.string.theme_dark)).performClick()
        assertEquals(listOf(ThemeMode.DARK), themes)

        rule.onNodeWithText(string(R.string.settings_clear)).performClick()
        rule.onNodeWithText(string(R.string.settings_clear_title)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.settings_cancel)).performClick()
        assertEquals(0, cleared)

        rule.onNodeWithText(string(R.string.settings_clear)).performClick()
        rule.onNodeWithText(string(R.string.settings_delete)).performClick()
        assertEquals(1, cleared)
    }

    @Test
    fun `daily scan is unavailable without history`() {
        showWith(ScanUiState.Done(sampleReport()), settings = Settings(saveHistory = false))
        rule.onNodeWithContentDescription(string(R.string.action_settings)).performClick()

        rule.onNodeWithText(string(R.string.settings_daily_needs_history)).assertExists()
        rule.onNodeWithText(string(R.string.settings_daily)).assertIsNotEnabled()
    }

    @Test
    @Config(qualifiers = "tr-rTR-w411dp-h891dp-night-xxhdpi")
    fun `turkish dark settings`() {
        showWith(ScanUiState.Done(sampleReport()), settings = Settings(theme = ThemeMode.DARK))
        rule.onNodeWithContentDescription(string(R.string.action_settings)).performClick()
        screenshot("settings-dark-tr")
    }
}
