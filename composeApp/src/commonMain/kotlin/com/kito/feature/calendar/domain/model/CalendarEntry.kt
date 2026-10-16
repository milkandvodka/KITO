package com.kito.feature.calendar.domain.model

import kotlinx.datetime.LocalDate

/** One item on the calendar, source-agnostic. Times are "HH:mm"; null means all-day. */
data class CalendarEntry(
    val id: String,
    val title: String,
    val date: LocalDate,
    val type: CalendarEntryType,
    val startTime: String? = null,
    val endTime: String? = null,
    val subtitle: String? = null,
    val colorHex: String? = null,
)
