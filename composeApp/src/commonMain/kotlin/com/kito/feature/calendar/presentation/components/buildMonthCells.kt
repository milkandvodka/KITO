package com.kito.feature.calendar.presentation.components

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** Always 42 days (6 weeks, Sunday-first) so every month has the same grid height. */
fun buildMonthCells(monthIndex: Int): List<LocalDate> {
    val first = LocalDate(monthIndex.indexYear(), monthIndex.indexMonth(), 1)
    val start = first.minus(first.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
    return List(42) { start.plus(it, DateTimeUnit.DAY) }
}
