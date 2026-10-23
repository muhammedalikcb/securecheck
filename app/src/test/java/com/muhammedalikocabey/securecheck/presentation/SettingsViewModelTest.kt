package com.muhammedalikocabey.securecheck.presentation

import com.muhammedalikocabey.securecheck.domain.settings.Settings
import com.muhammedalikocabey.securecheck.domain.settings.ThemeMode
import com.muhammedalikocabey.securecheck.presentation.settings.SettingsViewModel
import com.muhammedalikocabey.securecheck.support.FakeReportStore
import com.muhammedalikocabey.securecheck.support.FakeScheduler
import com.muhammedalikocabey.securecheck.support.FakeSettingsStore
import com.muhammedalikocabey.securecheck.support.reportOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val settings = FakeSettingsStore()
    private val scheduler = FakeScheduler()
    private val reports = FakeReportStore()
    private lateinit var model: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        model = SettingsViewModel(settings, scheduler, reports)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `theme choice is stored`() {
        model.setTheme(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, model.settings.value.theme)
    }

    @Test
    fun `daily scan turns on and schedules work while history is on`() {
        model.setDailyScan(true)

        assertTrue(model.settings.value.dailyScan)
        assertEquals(listOf(true), scheduler.calls)
    }

    @Test
    fun `daily scan cannot be enabled without history`() {
        val off = FakeSettingsStore(Settings(saveHistory = false))
        val vm = SettingsViewModel(off, scheduler, reports)
        vm.setDailyScan(true)

        assertFalse(off.settings.value.dailyScan)
        assertEquals(listOf(false), scheduler.calls)
    }

    @Test
    fun `turning history off also stops the daily scan`() {
        model.setDailyScan(true)
        model.setSaveHistory(false)

        assertFalse(model.settings.value.saveHistory)
        assertFalse(model.settings.value.dailyScan)
        assertEquals(listOf(true, false), scheduler.calls)
    }

    @Test
    fun `clear history empties the store`() {
        reports.saved += reportOf("a", 1)
        model.clearHistory()

        assertTrue(reports.saved.isEmpty())
    }
}
