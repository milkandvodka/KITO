package com.kito.feature.calendar.presentation.components

import androidx.compose.ui.graphics.Color
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType

/** Accent colour for an entry; events may carry their own colour from Supabase. */
fun entryTypeColor(entry: CalendarEntry): Color = when (entry.type) {
    CalendarEntryType.HOLIDAY -> Color(0xFF3FB68B)
    CalendarEntryType.EXAM -> Color(0xFFFF6A00)
    CalendarEntryType.EVENT -> parseHexColor(entry.colorHex) ?: Color(0xFF9D7CF2)
    CalendarEntryType.CLASS -> Color(0xFF5B9BD5)
}
