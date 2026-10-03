package com.kito.feature.schedule.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject

@Composable
fun ScheduleScreen(
    viewModel: ScheduleScreenViewModel = koinInject(),
    onBack: () -> Unit
) {
    val schedule by viewModel.weeklySchedule.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    ScheduleContent(
        schedule = schedule,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}
