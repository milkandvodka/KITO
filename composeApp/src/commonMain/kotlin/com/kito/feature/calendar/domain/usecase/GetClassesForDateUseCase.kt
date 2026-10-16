package com.kito.feature.calendar.domain.usecase

import com.kito.feature.calendar.data.mapper.toCalendarEntries
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.holiday.domain.model.Holiday
import com.kito.feature.holiday.domain.model.holidayList2026
import com.kito.feature.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/** The student's weekly timetable resolved for one date (local Room data, works offline). No classes on Sundays or holidays. */
class GetClassesForDateUseCase(
    private val scheduleRepository: ScheduleRepository,
    holidays: List<Holiday> = holidayList2026,
) {
    private val holidayDates = holidays.flatMap { it.toCalendarEntries() }.map { it.date }.toSet()

    suspend operator fun invoke(date: LocalDate, roll: String): List<CalendarEntry> {
        if (roll.isBlank() || date.dayOfWeek == DayOfWeek.SUNDAY || date in holidayDates) return emptyList()
        return scheduleRepository.getScheduleForDay(roll, date.dayOfWeek.name.take(3)).first()
            .filter { it.subject.isNotBlank() }
            .sortedBy { it.startTime }
            .map {
                CalendarEntry(
                    id = "class_${it.subject}_${it.startTime}_$date",
                    title = it.subject,
                    date = date,
                    type = CalendarEntryType.CLASS,
                    startTime = it.startTime.toHhMm(),
                    endTime = it.endTime.toHhMm(),
                    subtitle = it.room?.let { room -> "Room $room" },
                )
            }
    }
}
