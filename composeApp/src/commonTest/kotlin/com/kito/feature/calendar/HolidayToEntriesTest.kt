package com.kito.feature.calendar

import com.kito.feature.calendar.data.mapper.parseHolidayDate
import com.kito.feature.calendar.data.mapper.toCalendarEntries
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.holiday.domain.model.Holiday
import com.kito.feature.holiday.domain.model.holidayList2026
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HolidayToEntriesTest {

    @Test
    fun parseHolidayDate_validText_parses() {
        assertEquals(LocalDate(2026, 1, 23), parseHolidayDate("23 Jan, 2026"))
    }

    @Test
    fun parseHolidayDate_garbage_returnsNull() {
        assertNull(parseHolidayDate("someday"))
    }

    @Test
    fun toCalendarEntries_singleDay_oneEntry() {
        val entries = Holiday("Holi", "04 Mar, 2026", startDay = "Wednesday", numberOfDays = 1, month = "March 2026")
            .toCalendarEntries()
        assertEquals(1, entries.size)
        assertEquals(LocalDate(2026, 3, 4), entries.single().date)
        assertEquals(CalendarEntryType.HOLIDAY, entries.single().type)
    }

    @Test
    fun toCalendarEntries_range_expandsEveryDay() {
        val entries = Holiday(
            "Durga Puja", "17 Oct, 2026", "25 Oct, 2026", "Saturday", "Sunday", 9, "October 2026"
        ).toCalendarEntries()
        assertEquals(9, entries.size)
        assertEquals(LocalDate(2026, 10, 25), entries.last().date)
        assertEquals(entries.size, entries.map { it.id }.toSet().size)
    }

    @Test
    fun holidayList2026_everyEntryParses() {
        assertTrue(holidayList2026.all { it.toCalendarEntries().isNotEmpty() })
    }
}
