package com.kito.feature.home

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.core.datastore.data.PrefsRepositoryImpl
import com.kito.core.designsystem.StartupSyncGuard
import com.kito.feature.home.presentation.HomeEvent
import com.kito.feature.home.presentation.HomeViewModel
import com.kito.testing.FakeAttendanceRepository
import com.kito.testing.FakeConnectivityRepository
import com.kito.testing.FakeCredentialsRepository
import com.kito.testing.FakeHomeRepository
import com.kito.testing.FakeScheduleRepository
import com.kito.testing.scheduleItem
import com.kito.testing.FakeSyncUseCase
import com.kito.testing.eventOrAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val tempPath = "home_prefs_test.preferences_pb".toPath()
    private lateinit var prefsRepository: PrefsRepository
    private lateinit var datastoreScope: CoroutineScope

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        datastoreScope = CoroutineScope(testDispatcher + SupervisorJob())
        prefsRepository = PrefsRepositoryImpl(
            PreferenceDataStoreFactory.createWithPath(
                scope = datastoreScope,
                produceFile = { tempPath }
            )
        )
    }

    @AfterTest
    fun teardown() {
        datastoreScope.cancel()
        Dispatchers.resetMain()
        try {
            FileSystem.SYSTEM.delete(tempPath)
        } catch (_: Exception) {
            // ignore
        }
    }

    private fun vm(
        homeRepo: FakeHomeRepository = FakeHomeRepository(),
        scheduleRepo: FakeScheduleRepository = FakeScheduleRepository(),
    ) = HomeViewModel(
        prefs = prefsRepository,
        credentialsRepository = FakeCredentialsRepository(),
        attendanceRepository = FakeAttendanceRepository(),
        scheduleRepository = scheduleRepo,
        homeRepository = homeRepo,
        appSyncUseCase = FakeSyncUseCase(),
        syncGuard = StartupSyncGuard(),
        connectivityRepository = FakeConnectivityRepository(),
        dispatcher = testDispatcher,
    )

    @Test
    fun ads_initiallyEmpty() = runTest(testDispatcher) {
        val v = vm()
        val job = launch { v.ads.collect {} }
        advanceUntilIdle()
        // FakeHomeRepository returns empty list → ads empty
        assertTrue(v.ads.value.isEmpty())
        job.cancel()
    }

    @Test
    fun ads_loadedFromRepo() = runTest(testDispatcher) {
        val v = vm(FakeHomeRepository(events = listOf(eventOrAd(1L), eventOrAd(2L))))
        val job = launch { v.ads.collect {} }
        advanceUntilIdle()
        assertEquals(2, v.ads.value.size)
        job.cancel()
    }

    @Test
    fun ads_requestedForCurrentPlatform() = runTest(testDispatcher) {
        val repo = FakeHomeRepository()
        vm(repo)
        advanceUntilIdle()
        // desktop test target is not Android
        assertEquals("ios", repo.requestedPlatform)
    }

    @Test
    fun khaooGullyUtilityFlag_noRows_hidden() = runTest(testDispatcher) {
        val v = vm(FakeHomeRepository())
        advanceUntilIdle()
        assertFalse(v.isKhaooGullyUtilityEnabled.value)
    }

    @Test
    fun khaooGullyUtilityFlag_enabled_shown() = runTest(testDispatcher) {
        val v = vm(FakeHomeRepository(featureFlags = mapOf("utilities" to true)))
        advanceUntilIdle()
        assertTrue(v.isKhaooGullyUtilityEnabled.value)
    }

    @Test
    fun onEvent_updateDay_updatesDayState() = runTest(testDispatcher) {
        val v = vm()
        val job = launch { v.day.collect {} }
        v.onEvent(HomeEvent.UpdateDay("MON"))
        advanceUntilIdle()
        assertEquals("MON", v.day.value)
        job.cancel()
    }

    @Test
    fun isScheduleEmpty_beforeFirstRead_isNullNotEmpty() = runTest(testDispatcher) {
        // Regression: the card showed "Set up your timetable" while the saved timetable was still loading.
        assertEquals(null, vm().isScheduleEmpty.value)
    }

    @Test
    fun isScheduleEmpty_savedTimetable_false() = runTest(testDispatcher) {
        val v = vm(scheduleRepo = FakeScheduleRepository(listOf(scheduleItem("OS"))))
        val job = launch { v.isScheduleEmpty.collect {} }
        advanceUntilIdle()
        assertEquals(false, v.isScheduleEmpty.value)
        job.cancel()
    }

    @Test
    fun isScheduleEmpty_nothingSaved_true() = runTest(testDispatcher) {
        val v = vm()
        val job = launch { v.isScheduleEmpty.collect {} }
        advanceUntilIdle()
        assertEquals(true, v.isScheduleEmpty.value)
        job.cancel()
    }

    @Test
    fun day_startsAsToday_soScheduleResolvesBeforeHomeScreenRuns() = runTest(testDispatcher) {
        val expected = com.kito.core.common.util.currentLocalDateTime().dayOfWeek.name.take(3)
        assertEquals(expected, vm().day.value)
    }
}
