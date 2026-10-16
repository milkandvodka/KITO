package com.kito.feature.calendar.data.mapper

import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.holiday.domain.model.Holiday
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private val MONTHS = listOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")

/** Expands a holiday ("17 Oct, 2026" .. "25 Oct, 2026") into one entry per day. Unparseable dates are skipped. */
fun Holiday.toCalendarEntries(): List<CalendarEntry> {
    val start = parseHolidayDate(startDate) ?: return emptyList()
    val end = parseHolidayDate(endDate)?.takeIf { it >= start } ?: start
    return generateSequence(start) { it.plus(1, DateTimeUnit.DAY) }
        .takeWhile { it <= end }
        .map { day ->
            CalendarEntry(
                id = "holiday_${name}_$day",
                title = name,
                date = day,
                type = CalendarEntryType.HOLIDAY,
                subtitle = if (numberOfDays > 1) "$startDate – $endDate" else null,
            )
        }
        .toList()
}

/** "23 Jan, 2026" -> 2026-01-23 */
internal fun parseHolidayDate(text: String): LocalDate? = runCatching {
    val (day, month, year) = text.replace(",", "").trim().split(" ").filter { it.isNotBlank() }
    LocalDate(year.toInt(), MONTHS.indexOf(month.take(3).lowercase()) + 1, day.toInt())
}.getOrNull()
