package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.core.designsystem.lavaMeshBackground
import com.kito.feature.calendar.domain.model.CalendarEntry
import kotlinx.datetime.LocalDate

/** Molten "today" card above the grid: date, today's count and a countdown to the next big item. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodayHeroCard(
    today: LocalDate,
    todayCount: Int,
    nextHighlight: CalendarEntry?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableAnimations: Boolean = true,
) {
    val uiColors = UIColors()
    val shape = RoundedCornerShape(24.dp)
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .lavaMeshBackground(enableAnimations)
            .border(
                Dp.Hairline,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))),
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "TODAY",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFFFFC9A3),
        )
        Text(
            text = formatLongDate(today),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLargeEmphasized,
            color = uiColors.textPrimary,
        )
        Text(
            text = when (todayCount) {
                0 -> "Nothing on the calendar · tap for classes"
                1 -> "1 item today · tap to open"
                else -> "$todayCount items today · tap to open"
            },
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall,
            color = uiColors.textSecondary,
        )
        nextHighlight?.let { next ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    Icons.Rounded.HourglassTop,
                    contentDescription = null,
                    tint = entryTypeColor(next),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${next.title} ${countdownLabel(today, next.date)}",
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.labelMedium,
                    color = uiColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
