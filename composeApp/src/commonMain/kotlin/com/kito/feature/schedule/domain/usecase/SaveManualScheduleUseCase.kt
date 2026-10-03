package com.kito.feature.schedule.domain.usecase

import com.kito.feature.schedule.domain.model.ManualScheduleConfig
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository

class SaveManualScheduleUseCase(
    private val manualScheduleRepository: ManualScheduleRepository
) {
    suspend operator fun invoke(rollNo: String, config: ManualScheduleConfig): Result<Unit> {
        return manualScheduleRepository.saveManualSchedule(rollNo, config)
    }
}
