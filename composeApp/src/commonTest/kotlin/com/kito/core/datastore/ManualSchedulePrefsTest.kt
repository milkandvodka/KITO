package com.kito.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.kito.core.datastore.data.PrefsRepositoryImpl
import com.kito.core.datastore.domain.repository.PrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
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
class ManualSchedulePrefsTest {

    private val testDispatcher = StandardTestDispatcher()
    private val tempPath = "manual_schedule_prefs_test.preferences_pb".toPath()
    private lateinit var datastoreScope: CoroutineScope
    private lateinit var prefsRepository: PrefsRepository

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

    @Test
    fun isManualSchedule_initiallyFalse() = runTest(testDispatcher) {
        assertFalse(prefsRepository.isManualScheduleFlow.first())
        assertEquals("", prefsRepository.manualSectionFlow.first())
        assertEquals("", prefsRepository.manualBatchFlow.first())
        assertEquals("", prefsRepository.manualElective1Flow.first())
        assertEquals("", prefsRepository.manualElective2Flow.first())
    }

    @Test
    fun saveManualSchedule_updatesAllManualFlows() = runTest(testDispatcher) {
        prefsRepository.saveManualSchedule(
            section = "CSE-12",
            batch = "batch_3",
            elective1 = "EL-CSE-01",
            elective2 = "EL-CSE-02"
        )

        assertTrue(prefsRepository.isManualScheduleFlow.first())
        assertEquals("CSE-12", prefsRepository.manualSectionFlow.first())
        assertEquals("batch_3", prefsRepository.manualBatchFlow.first())
        assertEquals("EL-CSE-01", prefsRepository.manualElective1Flow.first())
        assertEquals("EL-CSE-02", prefsRepository.manualElective2Flow.first())
    }

    @Test
    fun clearManualSchedule_resetsAllManualFlows() = runTest(testDispatcher) {
        prefsRepository.saveManualSchedule(
            section = "IT-02",
            batch = "batch_2",
            elective1 = "",
            elective2 = ""
        )
        assertTrue(prefsRepository.isManualScheduleFlow.first())

        prefsRepository.clearManualSchedule()

        assertFalse(prefsRepository.isManualScheduleFlow.first())
        assertEquals("", prefsRepository.manualSectionFlow.first())
        assertEquals("", prefsRepository.manualBatchFlow.first())
        assertEquals("", prefsRepository.manualElective1Flow.first())
        assertEquals("", prefsRepository.manualElective2Flow.first())
    }
}
