package com.muhammedalikocabey.securecheck

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.muhammedalikocabey.securecheck.presentation.scan.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Launches the real activity on a device and waits for the automatic first scan to produce a score. */
@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun firstScanFinishesAndShowsTheScore() {
        val outOf = rule.activity.getString(R.string.score_out_of)
        rule.waitUntil(timeoutMillis = SCAN_TIMEOUT_MS) {
            rule.onAllNodesWithText(outOf).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onAllNodesWithText(outOf)[0].assertIsDisplayed()
    }

    private companion object {
        const val SCAN_TIMEOUT_MS = 60_000L
    }
}
