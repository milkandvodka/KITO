package com.kito.feature.calendar

import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.calendar.domain.usecase.GetCalendarMonthUseCase
import com.kito.feature.holiday.domain.model.Holiday
import com.kito.testing.FakeCalendarRepository
import com.kito.testing.FakeExamRepository
import com.kito.testing.calendarEvent
import com.kito.testing.examSchedule
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetCalendarMonthUseCaseTest {
    private val holidays = listOf(
        Holiday("Durga Puja", "30 Sep, 2026", "02 Oct, 2026", "Wednesday", "Friday", 3, "October 2026"),
    )

    private fun useCase(calendar: FakeCalendarRepository = FakeCalendarRepository(), exams: FakeExamRepository = FakeExamRepository()) =
        GetCalendarMonthUseCase(calendar, exams, holidays)

    @Test
    fun invoke_mergesAllSourcesForMonth() = runTest {
        val result = useCase(
            calendar = FakeCalendarRepository(listOf(calendarEvent(date = "2026-10-01"))),
            exams = FakeExamRepository(listOf(examSchedule(date = "2026-10-01"))),
        )(2026, 10, roll = "22CS001")

        val day = result.entriesByDate.getValue(LocalDate(2026, 10, 1))
        // Holiday first, then exam, then event.
        assertEquals(listOf(CalendarEntryType.HOLIDAY, CalendarEntryType.EXAM, CalendarEntryType.EVENT), day.map { it.type })
        assertFalse(result.isPartial)
    }

    @Test
    fun invoke_holidaySpanningMonths_onlyKeepsDaysInMonth() = runTest {
        val result = useCase()(2026, 10, roll = "")
        assertEquals(setOf(LocalDate(2026, 10, 1), LocalDate(2026, 10, 2)), result.entriesByDate.keys)
    }

    @Test
    fun invoke_examsOutsideMonth_excluded() = runTest {
        val result = useCase(exams = FakeExamRepository(listOf(examSchedule(date = "2026-11-05"))))(2026, 10, roll = "22CS001")
        assertTrue(result.entriesByDate.values.flatten().none { it.type == CalendarEntryType.EXAM })
    }

    @Test
    fun invoke_eventWithExamCategory_isExam() = runTest {
        val event = calendarEvent(date = "2026-10-20").copy(category = "Exam")
        val result = useCase(calendar = FakeCalendarRepository(listOf(event)))(2026, 10, roll = "")
        assertEquals(CalendarEntryType.EXAM, result.entriesByDate.getValue(LocalDate(2026, 10, 20)).single().type)
    }

    @Test
    fun invoke_blankRoll_skipsExams() = runTest {
        val result = useCase(exams = FakeExamRepository(fail = true))(2026, 10, roll = "")
        assertFalse(result.isPartial)
    }

    @Test
    fun invoke_sourceFails_keepsOthersAndFlagsPartial() = runTest {
        val result = useCase(
            calendar = FakeCalendarRepository(fail = true),
            exams = FakeExamRepository(listOf(examSchedule(date = "2026-10-10"))),
        )(2026, 10, roll = "22CS001")
        assertTrue(result.isPartial)
        assertEquals(CalendarEntryType.EXAM, result.entriesByDate.getValue(LocalDate(2026, 10, 10)).single().type)
    }

    @Test
    fun invoke_forceRefresh_passedToRepository() = runTest {
        val calendar = FakeCalendarRepository()
        useCase(calendar = calendar)(2026, 10, roll = "", forceRefresh = true)
        assertEquals(Triple(2026, 10, true), calendar.calls.single())
    }
}
