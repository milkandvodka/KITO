package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.core.designsystem.lavaMeshBackground
import com.kito.feature.calendar.domain.model.CalendarEntry

/** Upcoming-list card: app gradient card (or lava mesh when [highlight]), grouped corners, coloured accent bar. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AgendaItemCard(
    entry: CalendarEntry,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    enableAnimations: Boolean = true,
) {
    val uiColors = UIColors()
    val accent = entryTypeColor(entry)
    val top = if (index == 0) 24.dp else 4.dp
    val bottom = if (index == count - 1) 24.dp else 4.dp
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom),
        modifier = modifier.fillMaxWidth()
    ) {
        // An exam coming up soon gets the molten mesh, like the Exam screen's next-exam card.
        Box(
            modifier = if (highlight) Modifier.lavaMeshBackground(enableAnimations) else Modifier.background(
                Brush.linearGradient(
                    listOf(uiColors.cardBackground, Color(0xFF2F222F), Color(0xFF2F222F), uiColors.cardBackgroundHigh)
                )
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.5f))))
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "${formatShortDate(entry.date)}  ·  ${entryTypeLabel(entry.type)}",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                    )
                    Text(
                        text = entry.title,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmallEmphasized,
                        color = uiColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = listOfNotNull(formatTimeRange(entry.startTime, entry.endTime), entry.subtitle)
                            .joinToString("  ·  "),
                        style = MaterialTheme.typography.bodySmall,
                        color = uiColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
