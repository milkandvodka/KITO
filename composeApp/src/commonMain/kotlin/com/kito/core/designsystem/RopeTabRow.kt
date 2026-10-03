package com.kito.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun RopeTabRow(
    tabPosition: Float,
    onTabSelected: (Int) -> Unit,
    tabs: List<String> = listOf("SGPA", "CGPA"),
    modifier: Modifier = Modifier
) {
    val uiColors = UIColors()
    val tabWidths = remember { mutableStateMapOf<Int, Float>() }

//    val animatedTab by animateFloatAsState(
//        targetValue = selectedTab.toFloat(),
//        animationSpec = tween(
//            durationMillis = 350,
//            easing = FastOutSlowInEasing
//        ),
//        label = "tabAnimation"
//    )
    val animatedTab = tabPosition.coerceIn(0f, (tabs.size - 1).toFloat())

    val density = LocalDensity.current

    SecondaryTabRow(
        selectedTabIndex = animatedTab.roundToInt(),
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = uiColors.textSecondary,
        indicator = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {

                        if (tabWidths.isEmpty()) return@drawBehind

                        val sortedKeys = tabWidths.keys.sorted()

                        val path = Path()
                        var currentX = 0f

                        val ribbonSections = mutableMapOf<Int, Float>()
                        var totalLength = 0f

                        sortedKeys.forEach { index ->

                            val originalWidth = tabWidths[index] ?: return@forEach

                            val shrink = with(density) { 16.dp.toPx() }
                            val width = originalWidth - shrink
                            val startX = currentX + (shrink / 2f)

                            val height = size.height
                            val top = 10f
                            val bottom = height - 10f

                            if (index == 0) {
                                path.moveTo(startX, top)
                            }

                            path.quadraticTo(
                                startX + width,
                                top,
                                startX + width,
                                height / 2
                            )

                            path.quadraticTo(
                                startX + width,
                                bottom,
                                startX + width / 2,
                                bottom
                            )

                            path.quadraticTo(
                                startX,
                                bottom,
                                startX,
                                height / 2
                            )

                            path.quadraticTo(
                                startX,
                                top,
                                startX + width,
                                top
                            )

                            currentX += originalWidth

                            val measure = PathMeasure()
                            measure.setPath(path, false)
                            val length = measure.length
                            ribbonSections[index] = length - totalLength
                            totalLength = length
                        }

                        val baseIndex =
                            animatedTab.toInt().coerceIn(0, ribbonSections.size - 1)
                        val progress = animatedTab - baseIndex

                        val currentLength = ribbonSections[baseIndex] ?: 0f
                        val nextLength =
                            ribbonSections[baseIndex + 1] ?: currentLength

                        val ribbonLength =
                            currentLength + (nextLength - currentLength) * progress

                        val phaseOffset = sortedKeys
                            .filter { it < baseIndex }
                            .map { ribbonSections[it] ?: 0f }
                            .fold(0f) { acc, v -> acc - v } -
                                (currentLength * progress)

                        drawPath(
                            path = path,
                            color = uiColors.progressAccent,
                            style = Stroke(
                                width = 10f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round,
                                pathEffect = PathEffect.dashPathEffect(
                                    intervals = floatArrayOf(
                                        ribbonLength,
                                        totalLength
                                    ),
                                    phase = phaseOffset
                                )
                            )
                        )
                    }
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = animatedTab.roundToInt() == index,
                onClick = { onTabSelected(index) },
                selectedContentColor = uiColors.progressAccent,
                unselectedContentColor = Color(0xFF846F68),
                modifier = Modifier.onSizeChanged {
                    tabWidths[index] = it.width.toFloat()
                },
                interactionSource = NoRippleInteractionSource
            ) {
                Text(
                    text = title,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}