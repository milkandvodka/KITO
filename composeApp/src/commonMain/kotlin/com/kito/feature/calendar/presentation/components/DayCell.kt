package com.kito.feature.calendar.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kito.core.designsystem.UIColors
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import kotlinx.datetime.LocalDate

private const val MAX_CHIPS = 2

@Composable
fun DayCell(
    date: LocalDate,
    entries: List<CalendarEntry>,
    isToday: Boolean,
    inMonth: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableAnimations: Boolean = true,
) {
    val uiColors = UIColors()
    val isHoliday = entries.any { it.type == CalendarEntryType.HOLIDAY }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    // Same bouncy spring as the bottom bar tabs.
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "dayPress"
    )
    val background = when {
        pressed -> Color.White.copy(alpha = 0.08f)
        isHoliday && inMonth -> Color(0xFF3FB68B).copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .height(66.dp)
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .alpha(if (inMonth) 1f else 0.3f)
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        if (isToday) TodayBadge(date, enableAnimations) else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(24.dp)) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelMedium,
                    color = uiColors.textPrimary,
                )
            }
        }
        if (inMonth) {
            entries.take(MAX_CHIPS).forEach { EntryChip(it) }
            if (entries.size > MAX_CHIPS) {
                Text(
                    text = "+${entries.size - MAX_CHIPS}",
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    color = uiColors.textSecondary,
                    modifier = Modifier.fillMaxWidth().padding(start = 3.dp)
                )
            }
        }
    }
}
