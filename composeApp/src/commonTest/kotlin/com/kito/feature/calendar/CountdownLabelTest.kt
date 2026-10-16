package com.kito.feature.calendar

import com.kito.feature.calendar.presentation.components.countdownLabel
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CountdownLabelTest {
    private val today = LocalDate(2026, 10, 8)

    @Test
    fun countdownLabel_sameDay_today() = assertEquals("today", countdownLabel(today, today))

    @Test
    fun countdownLabel_pastDate_today() = assertEquals("today", countdownLabel(today, LocalDate(2026, 10, 1)))

    @Test
    fun countdownLabel_nextDay_tomorrow() = assertEquals("tomorrow", countdownLabel(today, LocalDate(2026, 10, 9)))

    @Test
    fun countdownLabel_acrossMonth_counts() = assertEquals("in 24 days", countdownLabel(today, LocalDate(2026, 11, 1)))
}
