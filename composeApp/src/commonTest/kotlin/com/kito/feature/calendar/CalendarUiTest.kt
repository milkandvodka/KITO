package com.kito.feature.calendar

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.kito.feature.calendar.domain.model.CalendarEntry
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.calendar.presentation.CalendarContent
import com.kito.feature.calendar.presentation.CalendarUiState
import com.kito.feature.calendar.presentation.components.DayEventsDialogBody
import com.kito.feature.calendar.presentation.components.monthIndex
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.datetime.LocalDate
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CalendarUiTest {
    private val today = LocalDate(2026, 10, 8)
    private val exam = CalendarEntry("x1", "DBMS Midsem", LocalDate(2026, 10, 12), CalendarEntryType.EXAM, "09:00", "11:00")
    private val holiday = CalendarEntry("h1", "Durga Puja", LocalDate(2026, 10, 17), CalendarEntryType.HOLIDAY)

    private fun loadedState() = CalendarUiState(
        today = today,
        months = mapOf(today.monthIndex() to mapOf(exam.date to listOf(exam), holiday.date to listOf(holiday))),
    )

    @Test
    fun calendar_content_rendersGridAndAgenda() = runComposeUiTest {
        setContent { CalendarContent(state = loadedState(), onEvent = {}, enableAnimations = false) }
        onNodeWithTag("calendar_grid").assertIsDisplayed()
        onNodeWithText("October 2026").assertIsDisplayed()
        // Both agenda items exist (the second may need a scroll now that the hero card sits on top).
        onNodeWithTag("calendar_list").performScrollToNode(hasTestTag("calendar_agenda_item") and hasText("DBMS Midsem", substring = true))
        onNodeWithTag("calendar_list").performScrollToNode(hasTestTag("calendar_agenda_item") and hasText("Durga Puja", substring = true))
    }

    @Test
    fun calendar_todayHero_showsDateAndCountdown() = runComposeUiTest {
        setContent { CalendarContent(state = loadedState(), onEvent = {}, enableAnimations = false) }
        onNodeWithTag("calendar_today_hero").assertIsDisplayed()
        onNodeWithText("Thursday, 8 October").assertIsDisplayed()
        onNodeWithText("DBMS Midsem in 4 days").assertIsDisplayed()
    }

    @Test
    fun calendar_firstLoad_showsShimmer() = runComposeUiTest {
        setContent {
            CalendarContent(
                state = CalendarUiState(today = today, loadingMonths = setOf(today.monthIndex())),
                onEvent = {},
                enableAnimations = false,
            )
        }
        onNodeWithTag("calendar_loading").assertIsDisplayed()
    }

    @Test
    fun calendar_loadedButQuiet_showsAgendaEmpty() = runComposeUiTest {
        setContent {
            CalendarContent(
                state = CalendarUiState(today = today, months = mapOf(today.monthIndex() to emptyMap())),
                onEvent = {},
                enableAnimations = false,
            )
        }
        onNodeWithTag("calendar_agenda_empty").assertIsDisplayed()
    }

    @Test
    fun dayDialog_listsEntriesAndClasses() = runComposeUiTest {
        val cls = CalendarEntry("c1", "Operating Systems", exam.date, CalendarEntryType.CLASS, "11:00", "12:00", "Room B-201")
        setContent {
            DayEventsDialogBody(exam.date, listOf(exam), listOf(cls), false, rememberHazeState(), onDismiss = {}, enableAnimations = false)
        }
        onNodeWithTag("day_dialog_list").assertIsDisplayed()
        onNodeWithText("DBMS Midsem").assertIsDisplayed()
        onNodeWithText("Operating Systems").assertIsDisplayed()
        onNodeWithText("2 items").assertIsDisplayed()
    }

    @Test
    fun dayDialog_emptyDay_showsEmptyState() = runComposeUiTest {
        setContent {
            DayEventsDialogBody(today, emptyList(), emptyList(), false, rememberHazeState(), onDismiss = {}, enableAnimations = false)
        }
        onNodeWithTag("day_dialog_empty").assertIsDisplayed()
        onNodeWithText("Nothing scheduled").assertIsDisplayed()
    }
}
