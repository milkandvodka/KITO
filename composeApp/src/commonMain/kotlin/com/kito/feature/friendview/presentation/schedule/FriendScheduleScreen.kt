package com.kito.feature.friendview.presentation.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.kito.core.designsystem.SharedExpandContainer
import com.kito.core.presentation.navigation3.Routes
import org.koin.compose.koinInject

@Composable
fun FriendScheduleScreen(
    roll: String,
    onBack: () -> Unit,
    viewModel: FriendScheduleViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(roll) {
        viewModel.loadSchedule(roll)
    }

    SharedExpandContainer(
        routeKey = Routes.FriendSchedule(roll),
        backgroundColor = Color(0xFF121116),
    ) {
        FriendScheduleContent(
            roll = roll,
            summary = state.summary,
            schedule = state.schedule,
            isLoading = state.isLoading,
            onBack = onBack
        )
    }
}
