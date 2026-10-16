package com.kito.feature.calendar.domain.usecase

import com.kito.feature.calendar.data.mapper.toCalendarEntries
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.calendar.domain.model.CalendarEvent
import com.kito.feature.calendar.domain.model.CalendarMonth
import com.kito.feature.calendar.domain.repository.CalendarRepository
import com.kito.feature.exam.domain.model.ExamSchedule
import com.kito.feature.exam.domain.repository.ExamRepository
import com.kito.feature.holiday.domain.model.Holiday
import com.kito.feature.holiday.domain.model.holidayList2026
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/** Merges academic events, the student's exams and holidays for one month. Classes are per-day, see [GetClassesForDateUseCase]. */
class GetCalendarMonthUseCase(
    private val calendarRepository: CalendarRepository,
    private val examRepository: ExamRepository,
    private val holidays: List<Holiday> = holidayList2026,
) {
    suspend operator fun invoke(year: Int, month: Int, roll: String, forceRefresh: Boolean = false): CalendarMonth =
        coroutineScope {
            val events = async { attempt { calendarRepository.getEventsByMonth(year, month, forceRefresh) } }
            val exams = async {
                if (roll.isBlank()) Result.success(emptyList())
                else attempt { examRepository.getExamSchedule(roll, forceRefresh) }
            }
            val eventsResult = events.await()
            val examsResult = exams.await()

            val entries = holidays.flatMap { it.toCalendarEntries() } +
                eventsResult.getOrDefault(emptyList()).mapNotNull { it.toEntry() } +
                examsResult.getOrDefault(emptyList()).mapNotNull { it.toEntry() }

            CalendarMonth(
                entriesByDate = entries
                    .filter { it.date.year == year && it.date.month.number == month }
                    .sortedWith(compareBy({ it.type.ordinal }, { it.startTime ?: "" }))
                    .groupBy { it.date },
                isPartial = eventsResult.isFailure || examsResult.isFailure,
            )
        }

    private suspend fun <T> attempt(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun CalendarEvent.toEntry(): CalendarEntry? {
    val day = runCatching { LocalDate.parse(date.take(10)) }.getOrNull() ?: return null
    return CalendarEntry(
        id = "event_$id",
        title = title,
        date = day,
        type = if (category.equals("exam", ignoreCase = true)) CalendarEntryType.EXAM else CalendarEntryType.EVENT,
        startTime = startTime.toHhMm(),
        endTime = endTime.toHhMm(),
        subtitle = description.ifBlank { category.ifBlank { null } },
        colorHex = color,
    )
}

private fun ExamSchedule.toEntry(): CalendarEntry? {
    val day = runCatching { LocalDate.parse(date.take(10)) }.getOrNull() ?: return null
    return CalendarEntry(
        id = "exam_${subjectCode ?: subject}_$date",
        title = subject,
        date = day,
        type = CalendarEntryType.EXAM,
        startTime = startTime.toHhMm(),
        endTime = endTime.toHhMm(),
        subtitle = subjectCode,
    )
}

/** "09:00:00" / "09:00" -> "09:00"; blank -> null (all-day). */
internal fun String.toHhMm(): String? = trim().take(5).ifBlank { null }
