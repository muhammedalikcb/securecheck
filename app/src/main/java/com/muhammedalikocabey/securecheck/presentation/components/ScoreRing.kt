package com.muhammedalikocabey.securecheck.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.muhammedalikocabey.securecheck.R
import com.muhammedalikocabey.securecheck.domain.model.RiskLevel
import com.muhammedalikocabey.securecheck.presentation.content.labelRes

@Composable
fun ScoreRing(score: Int, risk: RiskLevel, modifier: Modifier = Modifier, size: Dp = 208.dp) {
    val reduceMotion = rememberReduceMotion()
    val target = score.coerceIn(0, 100) / 100f
    val progress = remember { Animatable(if (reduceMotion) target else 0f) }
    LaunchedEffect(target, reduceMotion) {
        if (reduceMotion) {
            progress.snapTo(target)
        } else {
            progress.animateTo(target, tween(durationMillis = 800, easing = FastOutSlowInEasing))
        }
    }

    val arc = risk.tone().main
    val track = MaterialTheme.colorScheme.outline
    val description = stringResource(R.string.score_description, score, stringResource(risk.labelRes))

    Box(
        modifier
            .size(size)
            .clearAndSetSemantics {
                contentDescription = description
                liveRegion = LiveRegionMode.Polite
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(size)) {
            val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
            val inset = stroke.width / 2
            val arcSize = Size(this.size.width - stroke.width, this.size.height - stroke.width)
            drawArc(track, 0f, 360f, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = stroke)
            if (progress.value > 0f) {
                drawArc(
                    arc,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.value,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = stroke,
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                score.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                stringResource(R.string.score_out_of),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
