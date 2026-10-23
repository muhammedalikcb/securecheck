package com.muhammedalikocabey.securecheck.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Outline icons drawn on a 24 unit grid with one stroke weight, so the whole set reads as one family. */
object SecureIcons {
    val Check: ImageVector by lazy { icon("Check", CIRCLE, "M8 12.5l2.8 2.8L16.2 9.5") }
    val Alert: ImageVector by lazy { icon("Alert", "M12 3.5L21.5 20h-19z", "M12 10v4.5", "M12 17.2v.1") }
    val Warning: ImageVector by lazy { icon("Warning", CIRCLE, "M12 7.5v5.5", "M12 16.2v.1") }
    val Info: ImageVector by lazy { icon("Info", CIRCLE, "M12 11v5.5", "M12 7.8v.1") }
    val Dash: ImageVector by lazy { icon("Dash", CIRCLE, "M8 12h8") }
    val Shield: ImageVector by lazy {
        icon("Shield", "M12 3l7 3v5.5c0 4.2-2.8 7.4-7 9.5-4.2-2.1-7-5.3-7-9.5V6z")
    }
    val Chevron: ImageVector by lazy { icon("Chevron", "M9.5 6l6 6-6 6") }
    val Refresh: ImageVector by lazy { icon("Refresh", "M20 12a8 8 0 1 1-2.5-5.8", "M20 4v5h-5") }
    val Back: ImageVector by lazy { icon("Back", "M19 12H5", "M11 5l-7 7 7 7") }
    val History: ImageVector by lazy { icon("History", CIRCLE, "M12 7.5V12l3 2") }
    val Settings: ImageVector by lazy {
        icon(
            "Settings",
            "M4 7h8",
            "M18 7h2",
            "M12 7a2 2 0 1 0 4 0 2 2 0 1 0-4 0z",
            "M4 17h2",
            "M12 17h8",
            "M6 17a2 2 0 1 0 4 0 2 2 0 1 0-4 0z",
        )
    }
    val Share: ImageVector by lazy { icon("Share", "M12 15V4", "M8 8l4-4 4 4", "M5 12v7h14v-7") }
    val External: ImageVector by lazy {
        icon("External", "M14 4h6v6", "M20 4l-9 9", "M18 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5")
    }

    val Device: ImageVector by lazy {
        icon("Device", "M8 3h8a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z", "M11 18h2")
    }
    val App: ImageVector by lazy {
        icon("App", "M4 4h6.5v6.5H4z", "M13.5 4H20v6.5h-6.5z", "M4 13.5h6.5V20H4z", "M13.5 13.5H20V20h-6.5z")
    }
    val Runtime: ImageVector by lazy { icon("Runtime", "M3 12h4l3-8 4 16 3-8h4") }
    val Network: ImageVector by lazy {
        icon("Network", CIRCLE, "M3 12h18", "M12 3c3 2.6 3 15.4 0 18", "M12 3c-3 2.6-3 15.4 0 18")
    }
    val Data: ImageVector by lazy {
        icon(
            "Data",
            "M5 6c0-1.7 3.1-3 7-3s7 1.3 7 3-3.1 3-7 3-7-1.3-7-3z",
            "M5 6v12c0 1.7 3.1 3 7 3s7-1.3 7-3V6",
            "M5 12c0 1.7 3.1 3 7 3s7-1.3 7-3",
        )
    }
    val Platform: ImageVector by lazy {
        icon("Platform", "M12 3l9 5-9 5-9-5z", "M3 12.5l9 5 9-5", "M3 16.5l9 5 9-5")
    }

    private const val CIRCLE = "M12 3a9 9 0 1 0 0 18 9 9 0 1 0 0-18z"

    private fun icon(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            for (d in paths) {
                addPath(
                    pathData = addPathNodes(d),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()
}
