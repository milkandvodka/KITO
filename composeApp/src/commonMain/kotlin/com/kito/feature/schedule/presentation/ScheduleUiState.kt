package com.kito.feature.schedule.presentation

import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.model.ManualScheduleConfig
import com.kito.feature.schedule.domain.model.ScheduleItem
import com.kito.feature.schedule.domain.model.ScheduleLookupState

data class ScheduleUiState(
    val weeklySchedule: Map<WeekDay, List<ScheduleItem>> = emptyMap(),
    val lookupState: ScheduleLookupState = ScheduleLookupState.Loading,
    val isManualSchedule: Boolean = false,
    val manualConfig: ManualScheduleConfig? = null,
    val availableData: AvailableSectionsData = AvailableSectionsData(),
    val selectedBatch: String = "",
    val selectedBranch: String = "",
    val selectedCoreSection: String = "",
    val selectedElective1: String = "",
    val selectedElective2: String = "",
    val isSubmittingSetup: Boolean = false,
    val isEditSheetOpen: Boolean = false,
    val errorMessage: String? = null
)
