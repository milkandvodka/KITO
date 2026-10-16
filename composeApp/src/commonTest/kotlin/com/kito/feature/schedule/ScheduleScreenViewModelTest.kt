package com.kito.feature.schedule

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.kito.core.datastore.data.PrefsRepositoryImpl
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.model.ManualScheduleConfig
import com.kito.feature.schedule.domain.model.ScheduleLookupState
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository
import com.kito.feature.schedule.domain.usecase.ClearManualScheduleUseCase
import com.kito.feature.schedule.domain.usecase.GetAvailableSectionsUseCase
import com.kito.feature.schedule.domain.usecase.GetScheduleLookupStateUseCase
import com.kito.feature.schedule.domain.usecase.SaveManualScheduleUseCase
import com.kito.feature.schedule.presentation.ScheduleEvent
import com.kito.feature.schedule.presentation.ScheduleScreenViewModel
import com.kito.feature.schedule.presentation.WeekDay
import com.kito.testing.FakeScheduleRepository
import com.kito.testing.scheduleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val tempPath = "schedule_prefs_test.preferences_pb".toPath()
    private lateinit var prefsRepository: PrefsRepository
    private lateinit var datastoreScope: CoroutineScope
    private lateinit var fakeManualScheduleRepo: FakeManualScheduleRepository

    class FakeManualScheduleRepository(
        var rollExists: Boolean = true,
        var availableData: AvailableSectionsData = AvailableSectionsData()
    ) : ManualScheduleRepository {
        val isManualFlow = MutableStateFlow(false)
        val configFlow = MutableStateFlow<ManualScheduleConfig?>(null)
        var lastSavedConfig: ManualScheduleConfig? = null

        override fun observeIsManualSchedule(): Flow<Boolean> = isManualFlow
        override fun observeManualScheduleConfig(): Flow<ManualScheduleConfig?> = configFlow
        override suspend fun checkRollExists(rollNo: String): Boolean = rollExists
        var sectionsCalls = 0
        override suspend fun getAvailableSections(): AvailableSectionsData {
            sectionsCalls++
            return availableData
        }
        override suspend fun saveManualSchedule(rollNo: String, config: ManualScheduleConfig): Result<Unit> {
            lastSavedConfig = config
            isManualFlow.value = true
            configFlow.value = config
            return Result.success(Unit)
        }
        override suspend fun clearManualSchedule(): Result<Unit> {
            isManualFlow.value = false
            configFlow.value = null
            return Result.success(Unit)
        }
    }

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
        fakeManualScheduleRepo = FakeManualScheduleRepository()
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

    private fun createViewModel(
        scheduleRepo: FakeScheduleRepository = FakeScheduleRepository()
    ): ScheduleScreenViewModel {
        return ScheduleScreenViewModel(
            prefs = prefsRepository,
            scheduleRepository = scheduleRepo,
            manualScheduleRepository = fakeManualScheduleRepo,
            getAvailableSectionsUseCase = GetAvailableSectionsUseCase(fakeManualScheduleRepo),
            saveManualScheduleUseCase = SaveManualScheduleUseCase(fakeManualScheduleRepo),
            clearManualScheduleUseCase = ClearManualScheduleUseCase(fakeManualScheduleRepo),
            getScheduleLookupStateUseCase = GetScheduleLookupStateUseCase(fakeManualScheduleRepo),
            dispatcher = testDispatcher
        )
    }

    @Test
    fun weeklySchedule_initiallyEmpty() = runTest(testDispatcher) {
        val vm = createViewModel()
        val job = launch { vm.weeklySchedule.collect {} }
        advanceUntilIdle()
        assertTrue(vm.weeklySchedule.value.isEmpty() || vm.weeklySchedule.value.values.all { it.isEmpty() })
        job.cancel()
    }

    @Test
    fun weeklySchedule_containsAllDays_whenSubscribed() = runTest(testDispatcher) {
        prefsRepository.setUserRollNumber("123456")
        val items = listOf(scheduleItem(subject = "Maths"), scheduleItem(subject = "Physics"))
        val vm = createViewModel(FakeScheduleRepository(items))
        val job = launch { vm.weeklySchedule.collect {} }
        advanceUntilIdle()

        val map = vm.weeklySchedule.value
        assertEquals(WeekDay.entries.size, map.size)
        assertTrue(map.containsKey(WeekDay.MON))
        assertEquals(2, map[WeekDay.MON]?.size)
        assertEquals("Maths", map[WeekDay.MON]?.get(0)?.subject)

        job.cancel()
    }

    @Test
    fun lookupState_whenRollNotFound_setsRollNotFound() = runTest(testDispatcher) {
        fakeManualScheduleRepo.rollExists = false
        prefsRepository.setUserRollNumber("999999")

        val vm = createViewModel()
        val job = launch { vm.uiState.collect {} }
        advanceUntilIdle()

        assertIs<ScheduleLookupState.RollNotFound>(vm.uiState.value.lookupState)
        assertFalse(vm.uiState.value.isManualSchedule)

        job.cancel()
    }

    @Test
    fun manualSetup_selectionAndSubmit_updatesState() = runTest(testDispatcher) {
        fakeManualScheduleRepo.rollExists = false
        prefsRepository.setUserRollNumber("999999")

        val vm = createViewModel()
        val job = launch { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onEvent(ScheduleEvent.SelectBatch("batch_3"))
        vm.onEvent(ScheduleEvent.SelectBranch("CSE"))
        vm.onEvent(ScheduleEvent.SelectCoreSection("CSE-12"))
        vm.onEvent(ScheduleEvent.SelectElective1("EL-01"))
        advanceUntilIdle()

        assertEquals("batch_3", vm.uiState.value.selectedBatch)
        assertEquals("CSE", vm.uiState.value.selectedBranch)
        assertEquals("CSE-12", vm.uiState.value.selectedCoreSection)
        assertEquals("EL-01", vm.uiState.value.selectedElective1)

        vm.onEvent(ScheduleEvent.SubmitManualSetup)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isManualSchedule)
        assertEquals("CSE-12", fakeManualScheduleRepo.lastSavedConfig?.section)
        assertEquals("batch_3", fakeManualScheduleRepo.lastSavedConfig?.batch)

        job.cancel()
    }

    @Test
    fun clearManualSetup_resetsManualState() = runTest(testDispatcher) {
        fakeManualScheduleRepo.rollExists = false
        prefsRepository.setUserRollNumber("999999")

        val vm = createViewModel()
        val job = launch { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onEvent(ScheduleEvent.SelectBatch("batch_2"))
        vm.onEvent(ScheduleEvent.SelectBranch("IT"))
        vm.onEvent(ScheduleEvent.SelectCoreSection("IT-02"))
        vm.onEvent(ScheduleEvent.SubmitManualSetup)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.isManualSchedule)

        vm.onEvent(ScheduleEvent.ClearManualSetup)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isManualSchedule)
        assertEquals("", vm.uiState.value.selectedCoreSection)

        job.cancel()
    }

    @Test
    fun init_doesNotDownloadSectionLists() = runTest(testDispatcher) {
        createViewModel()
        advanceUntilIdle()
        assertEquals(0, fakeManualScheduleRepo.sectionsCalls)
    }

    @Test
    fun loadSections_fetchesOnce() = runTest(testDispatcher) {
        val vm = createViewModel()
        vm.onEvent(ScheduleEvent.LoadSections)
        vm.onEvent(ScheduleEvent.OpenEditSheet)
        advanceUntilIdle()
        assertEquals(1, fakeManualScheduleRepo.sectionsCalls)
    }

    @Test
    fun retryLookup_refetchesSections() = runTest(testDispatcher) {
        val vm = createViewModel()
        vm.onEvent(ScheduleEvent.LoadSections)
        advanceUntilIdle()
        vm.onEvent(ScheduleEvent.RetryLookup)
        advanceUntilIdle()
        assertEquals(2, fakeManualScheduleRepo.sectionsCalls)
    }
}
