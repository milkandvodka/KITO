package com.kito.feature.friendview

import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.friendview.presentation.schedule.FriendScheduleViewModel
import com.kito.feature.schedule.presentation.WeekDay
import com.kito.testing.FakeFriendViewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FriendScheduleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initiallyEmpty() {
        val vm = FriendScheduleViewModel(FakeFriendViewRepository(), testDispatcher)
        assertTrue(vm.uiState.value.schedule.isEmpty())
        assertEquals("", vm.uiState.value.roll)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun loadSchedule_populatesScheduleAndSummary() = runTest(testDispatcher) {
        val items = listOf(
            FriendScheduleItem("Maths", "08:00:00", "09:00:00", "101", "MON", "CS-A", "B1"),
            FriendScheduleItem("Physics", "10:00:00", "11:00:00", "102", "MON", "CS-A", "B1")
        )
        val summaries = mapOf(
            "2205001" to FriendSummary(roll = "2205001", section = "CSE-12")
        )
        val repo = FakeFriendViewRepository(items = items, summaryMap = summaries)
        val vm = FriendScheduleViewModel(repo, testDispatcher)

        val job = launch { vm.uiState.collect {} }

        vm.loadSchedule("2205001")
        advanceUntilIdle()

        assertEquals("2205001", vm.uiState.value.roll)
        assertEquals("CSE-12", vm.uiState.value.summary?.section)
        assertFalse(vm.uiState.value.isLoading)
        val monSchedule = vm.uiState.value.schedule[WeekDay.MON].orEmpty()
        assertEquals(2, monSchedule.size)
        assertEquals("Maths", monSchedule[0].subject)
        assertEquals("Physics", monSchedule[1].subject)

        job.cancel()
    }
}
