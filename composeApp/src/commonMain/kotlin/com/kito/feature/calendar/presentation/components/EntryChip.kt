package com.kito.feature.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kito.feature.calendar.domain.model.CalendarEntry

/** Tiny Google-style label inside a day cell. */
@Composable
fun EntryChip(entry: CalendarEntry, modifier: Modifier = Modifier) {
    val color = entryTypeColor(entry)
    Text(
        text = entry.title,
        color = color,
        fontSize = 9.sp,
        lineHeight = 11.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 3.dp, vertical = 1.dp)
    )
}
