package com.kito.feature.schedule.domain.repository

import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.model.ManualScheduleConfig
import kotlinx.coroutines.flow.Flow

interface ManualScheduleRepository {
    fun observeIsManualSchedule(): Flow<Boolean>
    fun observeManualScheduleConfig(): Flow<ManualScheduleConfig?>
    suspend fun checkRollExists(rollNo: String): Boolean
    suspend fun getAvailableSections(): AvailableSectionsData
    suspend fun saveManualSchedule(rollNo: String, config: ManualScheduleConfig): Result<Unit>
    suspend fun clearManualSchedule(): Result<Unit>
}
