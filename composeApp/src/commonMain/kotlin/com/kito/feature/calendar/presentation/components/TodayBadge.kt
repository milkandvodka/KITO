package com.kito.feature.calendar.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import kotlinx.datetime.LocalDate

/** Orange circle with a breathing radial glow, like the selected bottom-bar tab. */
@Composable
fun TodayBadge(date: LocalDate, enableAnimations: Boolean) {
    val uiColors = UIColors()
    val glow = if (enableAnimations) {
        rememberInfiniteTransition(label = "today").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
            label = "todayGlow"
        ).value
    } else 0f
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(24.dp)
            .drawBehind {
                val radius = size.minDimension * (0.75f + 0.3f * glow)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(uiColors.accentOrangeEnd.copy(alpha = 0.2f + 0.3f * glow), Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    radius = radius
                )
            }
            .scale(1f + 0.05f * glow)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(uiColors.accentOrangeStart, uiColors.accentOrangeEnd)))
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Black,
        )
    }
}
