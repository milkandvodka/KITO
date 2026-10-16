package com.kito.feature.calendar.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val STAGGER_MS = 45L
private const val MAX_STAGGER_STEPS = 8

/** Fades/springs content up into place, delayed by [index] so lists cascade in. No-op when disabled. */
@Composable
fun Modifier.staggeredEntrance(
    index: Int = 0,
    enabled: Boolean = true,
    fromScale: Float = 1f,
    offset: Dp = 14.dp,
): Modifier {
    if (!enabled) return this
    val progress = remember { Animatable(0f) }
    val offsetPx = with(LocalDensity.current) { offset.toPx() }
    LaunchedEffect(Unit) {
        delay(STAGGER_MS * index.coerceAtMost(MAX_STAGGER_STEPS))
        progress.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
    }
    return graphicsLayer {
        val p = progress.value
        alpha = p.coerceIn(0f, 1f)
        translationY = (1f - p) * offsetPx
        val s = fromScale + (1f - fromScale) * p
        scaleX = s
        scaleY = s
    }
}
