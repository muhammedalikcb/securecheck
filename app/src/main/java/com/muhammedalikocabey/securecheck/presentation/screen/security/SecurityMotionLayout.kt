package com.muhammedalikocabey.securecheck.presentation.screen.security

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.layoutId
import com.muhammedalikocabey.securecheck.domain.model.SecurityState
import com.muhammedalikocabey.securecheck.core.mock.FakeStateFactory
import com.muhammedalikocabey.securecheck.presentation.theme.SecureCheckTheme

/**
 * `SecurityStatusScreen` bileşenine giriş animasyonu ekleyen MotionLayout wrapper bileşeni
 *
 * Adds entry animation to SecurityStatusScreen using ConstraintLayout MotionLayout
 *
 * @author Muhammed Ali KOCABEY
 * @since 11.05.2025
 */
@OptIn(ExperimentalMotionApi::class)
@Composable
fun SecurityMotionLayout(
    securityState: SecurityState,
    onExportClick: () -> Unit
) {
    var animateToEnd by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (animateToEnd) 1f else 0f
    )

    LaunchedEffect(Unit) {
        animateToEnd = true
    }

    MotionLayout(
        start = ConstraintSet {
            val screen = createRefFor("screen")
            constrain(screen) {
                scaleX = 0.9f
                scaleY = 0.9f
                alpha = 0f
                centerTo(parent)
            }
        },
        end = ConstraintSet {
            val screen = createRefFor("screen")
            constrain(screen) {
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
                centerTo(parent)
            }
        },
        progress = progress,
        modifier = Modifier.fillMaxSize()
    ) {
        SecurityStatusScreen(
            modifier = Modifier.layoutId("screen"),
            securityState = securityState,
            onExportClick = onExportClick
        )
    }
}

@Preview(showBackground = true, name = "MotionLayout - Güvenli")
@Composable
fun PreviewMotionLayout() {
    val fakeState = FakeStateFactory.createSafeState()
    SecureCheckTheme {
        SecurityStatusScreen(
            securityState = fakeState,
            onExportClick = {}
        )
    }
}
