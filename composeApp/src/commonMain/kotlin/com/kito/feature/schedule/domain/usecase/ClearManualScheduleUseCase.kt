package com.kito.feature.schedule.domain.usecase

import com.kito.feature.schedule.domain.repository.ManualScheduleRepository

class ClearManualScheduleUseCase(
    private val manualScheduleRepository: ManualScheduleRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return manualScheduleRepository.clearManualSchedule()
    }
}
