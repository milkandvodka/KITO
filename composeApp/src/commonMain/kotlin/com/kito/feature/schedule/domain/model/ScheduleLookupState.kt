package com.kito.feature.schedule.domain.model

sealed interface ScheduleLookupState {
    data object Loading : ScheduleLookupState
    data object FoundInDatabase : ScheduleLookupState
    data class RollNotFound(val manualConfig: ManualScheduleConfig?) : ScheduleLookupState
    data class Error(val message: String) : ScheduleLookupState
}
