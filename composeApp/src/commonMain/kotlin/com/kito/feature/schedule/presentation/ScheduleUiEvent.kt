package com.kito.feature.schedule.presentation

sealed interface ScheduleUiEvent {
    data object SetupSuccess : ScheduleUiEvent
}
