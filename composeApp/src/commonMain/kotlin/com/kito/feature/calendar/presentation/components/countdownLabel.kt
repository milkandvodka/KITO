package com.kito.feature.calendar.presentation.components

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

/** "today" / "tomorrow" / "in 9 days" (past dates read as "today"). */
fun countdownLabel(from: LocalDate, to: LocalDate): String = when (val days = from.daysUntil(to)) {
    in Int.MIN_VALUE..0 -> "today"
    1 -> "tomorrow"
    else -> "in $days days"
}
