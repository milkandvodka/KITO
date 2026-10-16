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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.UIColors
import com.kito.feature.calendar.domain.model.CalendarEntry

/** One item inside the day dialog. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayEntryRow(entry: CalendarEntry, modifier: Modifier = Modifier) {
    val uiColors = UIColors()
    val accent = entryTypeColor(entry)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                color = uiColors.textPrimary,
            )
            Text(
                text = listOfNotNull(formatTimeRange(entry.startTime, entry.endTime), entry.subtitle)
                    .joinToString("  ·  "),
                style = MaterialTheme.typography.bodySmall,
                color = uiColors.textSecondary,
            )
        }
    }
}
