package com.kito.feature.calendar

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.kito.core.datastore.data.PrefsRepositoryImpl
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.feature.calendar.domain.model.CalendarEntryType
import com.kito.feature.calendar.domain.usecase.GetCalendarMonthUseCase
import com.kito.feature.calendar.domain.usecase.GetClassesForDateUseCase
import com.kito.feature.calendar.presentation.CalendarScreenEvent
import com.kito.feature.calendar.presentation.CalendarViewModel
import com.kito.feature.calendar.presentation.components.monthIndex
import com.kito.testing.FakeCalendarRepository
import com.kito.testing.FakeExamRepository
import com.kito.testing.FakeScheduleRepository
import com.kito.testing.calendarEvent
import com.kito.testing.scheduleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val tempPath = "calendar_vm_test.preferences_pb".toPath()
    private lateinit var prefs: PrefsRepository
    private lateinit var datastoreScope: CoroutineScope
    private val today = LocalDate(2026, 10, 8)
    private val october = today.monthIndex()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        datastoreScope = CoroutineScope(testDispatcher + SupervisorJob())
        prefs = PrefsRepositoryImpl(PreferenceDataStoreFactory.createWithPath(scope = datastoreScope, produceFile = { tempPath }))
    }

    @AfterTest
    fun teardown() {
        datastoreScope.cancel()
        Dispatchers.resetMain()
        try { FileSystem.SYSTEM.delete(tempPath) } catch (_: Exception) { }
    }

    private fun vm(
        calendar: FakeCalendarRepository = FakeCalendarRepository(),
        schedule: FakeScheduleRepository = FakeScheduleRepository(),
    ) = CalendarViewModel(
        getCalendarMonth = GetCalendarMonthUseCase(calendar, FakeExamRepository(), holidays = emptyList()),
        getClassesForDate = GetClassesForDateUseCase(schedule, holidays = emptyList()),
        prefs = prefs,
        dispatcher = testDispatcher,
        today = today,
    )

    @Test
    fun init_loadsDisplayedMonthAndNeighbours() = runTest(testDispatcher) {
        val calendar = FakeCalendarRepository()
        val v = vm(calendar)
        advanceUntilIdle()
        assertEquals(setOf(9, 10, 11), calendar.calls.map { it.second }.toSet())
        assertEquals(setOf(october - 1, october, october + 1), v.uiState.value.months.keys)
        assertFalse(v.uiState.value.isLoading)
    }

    @Test
    fun monthChanged_reusesMemoryCache() = runTest(testDispatcher) {
        val calendar = FakeCalendarRepository()
        val v = vm(calendar)
        advanceUntilIdle()
        v.onEvent(CalendarScreenEvent.MonthChanged(october + 1))
        advanceUntilIdle()
        v.onEvent(CalendarScreenEvent.MonthChanged(october))
        advanceUntilIdle()
        // Only December is new; Sep/Oct/Nov were fetched once.
        assertEquals(4, calendar.calls.size)
        assertEquals(october, v.uiState.value.displayedMonth)
    }

    @Test
    fun goToToday_returnsToCurrentMonth() = runTest(testDispatcher) {
        val v = vm()
        v.onEvent(CalendarScreenEvent.MonthChanged(october + 5))
        v.onEvent(CalendarScreenEvent.GoToToday)
        advanceUntilIdle()
        assertEquals(october, v.uiState.value.displayedMonth)
    }

    @Test
    fun selectDate_fillsDialogWithEntriesAndClasses() = runTest(testDispatcher) {
        prefs.setUserRollNumber("22CS001")
        val v = vm(
            calendar = FakeCalendarRepository(listOf(calendarEvent(date = "2026-10-14"))),
            schedule = FakeScheduleRepository(listOf(scheduleItem("OS"))),
        )
        advanceUntilIdle()
        v.onEvent(CalendarScreenEvent.SelectDate(LocalDate(2026, 10, 14)))
        advanceUntilIdle()
        val state = v.uiState.value
        assertEquals(LocalDate(2026, 10, 14), state.selectedDate)
        assertEquals("Fest", state.selectedDayEntries.single().title)
        assertEquals("OS", state.selectedDayClasses.single().title)
        assertFalse(state.isLoadingClasses)
    }

    @Test
    fun dismissDay_clearsSelection() = runTest(testDispatcher) {
        val v = vm()
        v.onEvent(CalendarScreenEvent.SelectDate(today))
        advanceUntilIdle()
        v.onEvent(CalendarScreenEvent.DismissDay)
        assertNull(v.uiState.value.selectedDate)
        assertTrue(v.uiState.value.selectedDayClasses.isEmpty())
    }

    @Test
    fun refresh_forcesNetworkAndEnds() = runTest(testDispatcher) {
        val calendar = FakeCalendarRepository()
        val v = vm(calendar)
        advanceUntilIdle()
        v.onEvent(CalendarScreenEvent.Refresh)
        advanceUntilIdle()
        assertTrue(calendar.calls.any { it.second == 10 && it.third })
        assertFalse(v.uiState.value.isRefreshing)
    }

    @Test
    fun upcoming_listsNext30DaysWithoutPast() = runTest(testDispatcher) {
        val v = vm(
            FakeCalendarRepository(
                listOf(calendarEvent(1, "Past", "2026-10-01"), calendarEvent(2, "Soon", "2026-10-20"))
            )
        )
        advanceUntilIdle()
        val upcoming = v.uiState.value.upcoming
        assertEquals(listOf("Soon"), upcoming.map { it.title }.distinct())
        assertTrue(upcoming.all { it.type == CalendarEntryType.EVENT })
    }

    @Test
    fun sourceFailure_marksOffline() = runTest(testDispatcher) {
        val v = vm(FakeCalendarRepository(fail = true))
        advanceUntilIdle()
        assertTrue(v.uiState.value.isOffline)
    }
}
