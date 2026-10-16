package com.kito.feature.calendar.presentation

import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.calendar.presentation.components.monthIndex
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

data class CalendarUiState(
    val today: LocalDate,
    val displayedMonth: Int = today.monthIndex(),
    /** In-memory month cache: monthIndex -> entries by date. Months stay here once loaded. */
    val months: Map<Int, Map<LocalDate, List<CalendarEntry>>> = emptyMap(),
    val loadingMonths: Set<Int> = emptySet(),
    val isRefreshing: Boolean = false,
    /** A source failed with no saved copy for the displayed month. */
    val isOffline: Boolean = false,
    val selectedDate: LocalDate? = null,
    val selectedDayClasses: List<CalendarEntry> = emptyList(),
    val isLoadingClasses: Boolean = false,
) {
    val isLoading: Boolean get() = displayedMonth in loadingMonths && displayedMonth !in months

    val selectedDayEntries: List<CalendarEntry>
        get() = selectedDate?.let { months[it.monthIndex()]?.get(it) }.orEmpty()

    /** Next 30 days of holidays, exams and events (classes are only shown per day). */
    val upcoming: List<CalendarEntry>
        get() {
            val end = today.plus(UPCOMING_DAYS, DateTimeUnit.DAY)
            return months.values.asSequence()
                .flatMap { it.values.asSequence().flatten() }
                .filter { it.type != CalendarEntryType.CLASS && it.date >= today && it.date <= end }
                .distinctBy { if (it.type == CalendarEntryType.HOLIDAY) it.title else it.id }
                .sortedWith(compareBy({ it.date }, { it.type.ordinal }, { it.startTime ?: "" }))
                .take(UPCOMING_LIMIT)
                .toList()
        }

    companion object {
        const val UPCOMING_DAYS = 30
        const val UPCOMING_LIMIT = 10
    }
}
