package com.kito.feature.calendar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    CalendarContent(state = state, onEvent = viewModel::onEvent)
}
