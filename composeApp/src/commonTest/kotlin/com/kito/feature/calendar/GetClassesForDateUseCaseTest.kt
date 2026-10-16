package com.kito.feature.calendar

import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.calendar.domain.usecase.GetClassesForDateUseCase
import com.kito.feature.holiday.domain.model.Holiday
import com.kito.feature.schedule.domain.model.ScheduleItem
import com.kito.feature.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetClassesForDateUseCaseTest {

    /** Records which weekday was asked for (the shared fake ignores it). */
    private class RecordingScheduleRepository(private val items: List<ScheduleItem>) : ScheduleRepository {
        val days = mutableListOf<String>()
        override fun getScheduleForDay(rollNo: String, day: String): Flow<List<ScheduleItem>> {
            days += day
            return flowOf(items)
        }
        override fun getAllSchedule(rollNo: String): Flow<List<ScheduleItem>> = flowOf(items)
        override suspend fun deleteAllSections() = Unit
    }

    private val items = listOf(
        ScheduleItem("OS", "11:00:00", "12:00:00", "B-201", "CS-A", "B1"),
        ScheduleItem("DBMS", "09:00:00", "10:00:00", null, "CS-A", "B1"),
        ScheduleItem("", "13:00:00", "14:00:00", null, "CS-A", "B1"),
    )
    private val holidays = listOf(Holiday("Holi", "04 Mar, 2026", startDay = "Wednesday", numberOfDays = 1, month = "March 2026"))

    @Test
    fun invoke_wednesday_queriesWedAndSortsByTime() = runTest {
        val repo = RecordingScheduleRepository(items)
        val classes = GetClassesForDateUseCase(repo, holidays)(LocalDate(2026, 10, 7), "22CS001")
        assertEquals(listOf("WED"), repo.days)
        assertEquals(listOf("DBMS", "OS"), classes.map { it.title })
        assertEquals("09:00", classes.first().startTime)
        assertEquals("Room B-201", classes.last().subtitle)
        assertTrue(classes.all { it.type == CalendarEntryType.CLASS })
    }

    @Test
    fun invoke_sunday_empty() = runTest {
        assertTrue(GetClassesForDateUseCase(RecordingScheduleRepository(items), holidays)(LocalDate(2026, 10, 11), "22CS001").isEmpty())
    }

    @Test
    fun invoke_holiday_empty() = runTest {
        assertTrue(GetClassesForDateUseCase(RecordingScheduleRepository(items), holidays)(LocalDate(2026, 3, 4), "22CS001").isEmpty())
    }

    @Test
    fun invoke_blankRoll_empty() = runTest {
        assertTrue(GetClassesForDateUseCase(RecordingScheduleRepository(items), holidays)(LocalDate(2026, 10, 7), "").isEmpty())
    }
}
