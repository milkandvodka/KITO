package com.kito.feature.friendview.presentation.schedule

import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.schedule.presentation.WeekDay

data class FriendScheduleUiState(
    val roll: String = "",
    val summary: FriendSummary? = null,
    val schedule: Map<WeekDay, List<FriendScheduleItem>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)
