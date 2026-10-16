package com.kito.feature.calendar.domain.model

import kotlinx.datetime.LocalDate

/** [isPartial] is true when a source failed and had no cached copy to fall back on. */
data class CalendarMonth(
    val entriesByDate: Map<LocalDate, List<CalendarEntry>>,
    val isPartial: Boolean,
)
