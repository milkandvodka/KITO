package com.kito.feature.calendar.presentation

import kotlinx.datetime.LocalDate

/** UI actions sent to [CalendarViewModel]. Named to avoid clashing with the domain CalendarEvent. */
sealed interface CalendarScreenEvent {
    /** [monthIndex] = year * 12 + (month - 1), see monthIndex(). */
    data class MonthChanged(val monthIndex: Int) : CalendarScreenEvent
    data object GoToToday : CalendarScreenEvent
    data class SelectDate(val date: LocalDate) : CalendarScreenEvent
    data object DismissDay : CalendarScreenEvent
    data object Refresh : CalendarScreenEvent
}
