package com.kito.core.designsystem

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import kotlin.random.Random

private val LAVA_COLORS = listOf(
    Color(0xFF77280F).copy(alpha = 0.82f),
    Color(0xFF753107).copy(alpha = 0.82f),
    Color(0xFF62290A).copy(alpha = 0.82f),
    Color(0xFF46180C).copy(alpha = 0.82f),
    Color(0xFFA14B09).copy(alpha = 0.70f),
    Color(0xFF6B1414).copy(alpha = 0.75f),
)

/**
 * The app's drifting molten-orange mesh (same palette and motion as ScheduleCard's ongoing class),
 * packaged as one modifier. With [enableAnimations] = false it draws a still mesh.
 */
@Composable
fun Modifier.lavaMeshBackground(enableAnimations: Boolean = true): Modifier {
    val colors = remember { List(15) { Animatable(LAVA_COLORS[it % LAVA_COLORS.size]) } }
    val pointTop = remember { Animatable(0.3f) }
    val pointMid = remember { Animatable(0.7f) }

    LaunchedEffect(enableAnimations) {
        if (!enableAnimations) return@LaunchedEffect
        colors.forEachIndexed { i, anim ->
            launch {
                val random = Random(i * 97)
                while (true) {
                    anim.animateTo(
                        LAVA_COLORS[random.nextInt(LAVA_COLORS.size)],
                        tween(random.nextInt(1800, 4200), easing = LinearOutSlowInEasing)
                    )
                }
            }
        }
        launch {
            while (true) {
                pointTop.animateTo(0.8f, tween(4000))
                pointTop.animateTo(0.2f, tween(4000))
            }
        }
        launch {
            while (true) {
                pointMid.animateTo(0.3f, tween(4000))
                pointMid.animateTo(0.7f, tween(4000))
            }
        }
    }

    return meshGradient(
        points = listOf(
            listOf(
                Offset(0f, 0f) to colors[0].value,
                Offset(0.25f, 0f) to colors[1].value,
                Offset(0.5f, 0f) to colors[2].value,
                Offset(0.75f, 0f) to colors[3].value,
                Offset(1f, 0f) to colors[4].value,
            ),
            listOf(
                Offset(-0.05f, 0.55f) to colors[5].value,
                Offset(0.2f, pointTop.value) to colors[6].value,
                Offset(0.5f, 0.6f) to colors[7].value,
                Offset(0.8f, pointMid.value) to colors[8].value,
                Offset(1.05f, 0.55f) to colors[9].value,
            ),
            listOf(
                Offset(0f, 1f) to colors[10].value,
                Offset(0.25f, 1f) to colors[11].value,
                Offset(0.5f, 1f) to colors[12].value,
                Offset(0.75f, 1f) to colors[13].value,
                Offset(1f, 1f) to colors[14].value,
            ),
        ),
        resolutionX = 30
    )
}
