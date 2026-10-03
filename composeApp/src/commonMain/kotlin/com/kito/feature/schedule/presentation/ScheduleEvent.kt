package com.kito.feature.schedule.presentation

sealed interface ScheduleEvent {
    data class SelectBatch(val batch: String) : ScheduleEvent
    data class SelectBranch(val branch: String) : ScheduleEvent
    data class SelectCoreSection(val section: String) : ScheduleEvent
    data class SelectElective1(val section: String) : ScheduleEvent
    data class SelectElective2(val section: String) : ScheduleEvent
    data object SubmitManualSetup : ScheduleEvent
    data object OpenEditSheet : ScheduleEvent
    data object CloseEditSheet : ScheduleEvent
    data object ClearManualSetup : ScheduleEvent
    data object RetryLookup : ScheduleEvent
}
