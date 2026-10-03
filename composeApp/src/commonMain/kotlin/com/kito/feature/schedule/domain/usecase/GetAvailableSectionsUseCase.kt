package com.kito.feature.schedule.domain.usecase

import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository

class GetAvailableSectionsUseCase(
    private val manualScheduleRepository: ManualScheduleRepository
) {
    suspend operator fun invoke(): AvailableSectionsData {
        return manualScheduleRepository.getAvailableSections()
    }
}
