package com.kito.feature.schedule

import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.model.ElectiveSlotOption
import com.kito.feature.schedule.domain.model.ManualScheduleConfig
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository
import com.kito.feature.schedule.domain.usecase.GetAvailableSectionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAvailableSectionsUseCaseTest {

    private class FakeManualScheduleRepo(
        private val data: AvailableSectionsData
    ) : ManualScheduleRepository {
        override fun observeIsManualSchedule(): Flow<Boolean> = MutableStateFlow(false)
        override fun observeManualScheduleConfig(): Flow<ManualScheduleConfig?> = MutableStateFlow(null)
        override suspend fun checkRollExists(rollNo: String): Boolean = false
        override suspend fun getAvailableSections(): AvailableSectionsData = data
        override suspend fun saveManualSchedule(rollNo: String, config: ManualScheduleConfig): Result<Unit> = Result.success(Unit)
        override suspend fun clearManualSchedule(): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun getAvailableSections_returnsGroupedData() = runTest {
        val testData = AvailableSectionsData(
            availableBatches = listOf("batch_1", "batch_2", "batch_3"),
            branchesByBatch = mapOf(
                "batch_3" to listOf("CSE", "IT")
            ),
            coreSectionsByBatchAndBranch = mapOf(
                "batch_3" to mapOf(
                    "CSE" to listOf("CSE-01", "CSE-02"),
                    "IT" to listOf("IT-01")
                )
            ),
            electiveSlotsByBatch = mapOf(
                "batch_3" to listOf(
                    ElectiveSlotOption(
                        slotKey = "elective_1",
                        displayName = "Elective 1",
                        availableSections = listOf("EL-01", "EL-02")
                    )
                )
            )
        )

        val useCase = GetAvailableSectionsUseCase(FakeManualScheduleRepo(testData))
        val result = useCase()

        assertEquals(3, result.availableBatches.size)
        assertTrue(result.availableBatches.contains("batch_3"))
        assertEquals(listOf("CSE", "IT"), result.branchesByBatch["batch_3"])
        assertEquals(listOf("CSE-01", "CSE-02"), result.coreSectionsByBatchAndBranch["batch_3"]?.get("CSE"))
        assertEquals(1, result.electiveSlotsByBatch["batch_3"]?.size)
        assertEquals("Elective 1", result.electiveSlotsByBatch["batch_3"]?.first()?.displayName)
    }
}
