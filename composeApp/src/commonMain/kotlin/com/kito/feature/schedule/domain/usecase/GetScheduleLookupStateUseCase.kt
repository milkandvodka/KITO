package com.kito.feature.schedule.domain.usecase

import com.kito.feature.schedule.domain.model.ScheduleLookupState
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository

class GetScheduleLookupStateUseCase(
    private val manualScheduleRepository: ManualScheduleRepository
) {
    suspend operator fun invoke(rollNo: String): ScheduleLookupState {
        if (rollNo.isBlank()) return ScheduleLookupState.Loading
        return runCatching {
            val exists = manualScheduleRepository.checkRollExists(rollNo)
            if (exists) {
                ScheduleLookupState.FoundInDatabase
            } else {
                ScheduleLookupState.RollNotFound(null)
            }
        }.getOrElse { e ->
            ScheduleLookupState.Error(e.message ?: "Failed to verify roll number")
        }
    }
}
