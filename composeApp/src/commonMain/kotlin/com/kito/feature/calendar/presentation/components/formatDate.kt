package com.kito.feature.calendar.presentation.components

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

private fun LocalDate.weekdayName() = dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }

/** "Fri, 17 Oct" */
fun formatShortDate(date: LocalDate): String =
    "${date.weekdayName().take(3)}, ${date.dayOfMonth} ${MONTH_NAMES[date.month.number - 1].take(3)}"

/** "Friday, 17 October" */
fun formatLongDate(date: LocalDate): String =
    "${date.weekdayName()}, ${date.dayOfMonth} ${MONTH_NAMES[date.month.number - 1]}"

/** "October 2026" */
fun formatMonthTitle(monthIndex: Int): String = "${MONTH_NAMES[monthIndex.indexMonth() - 1]} ${monthIndex.indexYear()}"

/** "09:00 – 11:00", "09:00", or "All day" */
fun formatTimeRange(start: String?, end: String?): String = when {
    start == null -> "All day"
    end == null || end == start -> start
    else -> "$start – $end"
}
