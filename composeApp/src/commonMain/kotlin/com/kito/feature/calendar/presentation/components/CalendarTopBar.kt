package com.kito.feature.calendar.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.ExpressiveEasing
import com.kito.core.designsystem.UIColors
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

/** Frosted top bar, same recipe as the Attendance tab. */
@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarTopBar(
    monthIndex: Int,
    hazeState: HazeState,
    onToday: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiColors = UIColors()
    Column(
        modifier = modifier
            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                blurRadius = 15.dp
                noiseFactor = 0.05f
                inputScale = HazeInputScale.Auto
                alpha = 0.98f
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Later months roll up, earlier months roll down.
            AnimatedContent(
                targetState = monthIndex,
                transitionSpec = {
                    val dir = if (targetState > initialState) 1 else -1
                    val spec = tween<IntOffset>(350, easing = ExpressiveEasing.Emphasized)
                    (slideInVertically(spec) { it * dir } + fadeIn(tween(350))) togetherWith
                        (slideOutVertically(spec) { -it * dir } + fadeOut(tween(200)))
                },
                label = "monthTitle",
                modifier = Modifier.weight(1f)
            ) { index ->
                Text(
                    text = formatMonthTitle(index),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = uiColors.textPrimary,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                )
            }
            TopBarButton(Icons.Rounded.Today, "Today", onToday)
            TopBarButton(Icons.Rounded.ChevronLeft, "Previous month", onPrevious)
            TopBarButton(Icons.Rounded.ChevronRight, "Next month", onNext)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TopBarButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    IconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            onClick()
        },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White.copy(alpha = 0.08f),
            contentColor = UIColors().progressAccent
        ),
        modifier = Modifier.size(32.dp)
    ) {
        Icon(imageVector = icon, contentDescription = description, modifier = Modifier.size(22.dp))
    }
}
