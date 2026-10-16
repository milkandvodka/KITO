package com.kito.feature.calendar.presentation.components

import com.kito.feature.calendar.domain.model.CalendarEntryType

fun entryTypeLabel(type: CalendarEntryType): String = when (type) {
    CalendarEntryType.HOLIDAY -> "Holiday"
    CalendarEntryType.EXAM -> "Exam"
    CalendarEntryType.EVENT -> "Event"
    CalendarEntryType.CLASS -> "Class"
}
