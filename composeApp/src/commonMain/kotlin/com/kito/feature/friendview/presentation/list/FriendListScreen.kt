package com.kito.feature.friendview.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.kito.core.designsystem.SharedExpandContainer
import com.kito.core.presentation.navigation3.Routes
import org.koin.compose.koinInject

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.kito.core.platform.toast
import com.kito.core.ui.state.SyncUiState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FriendListScreen(
    onBack: () -> Unit,
    onNavigateToSchedule: (String) -> Unit,
    viewModel: FriendListViewModel = koinInject()
) {
    val friends by viewModel.friendsSummary.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val availableData by viewModel.availableSectionsData.collectAsState()
    val isAddingFriend by viewModel.isAddingFriend.collectAsState()
    val addFriendError by viewModel.addFriendError.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        delay(500.milliseconds)
        viewModel.onEvent(FriendListEvent.SyncOnOpen)
    }

    LaunchedEffect(Unit) {
        viewModel.syncEvents.collect { event ->
            when (event) {
                is SyncUiState.Success -> {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
                    toast("Sync completed")
                }
                is SyncUiState.Error -> {
                    haptic.performHapticFeedback(HapticFeedbackType.Reject)
                    toast(event.message)
                }
                else -> Unit
            }
        }
    }

    SharedExpandContainer(
        routeKey = Routes.FriendView,
        backgroundColor = Color(0xFF121116),
    ) {
        FriendListContent(
            friends = friends,
            showAddDialog = showAddDialog,
            onBack = onBack,
            onSelectFriend = onNavigateToSchedule,
            onRemoveFriend = { roll -> viewModel.onEvent(FriendListEvent.RemoveFriend(roll)) },
            onAddFriendByRoll = { name, roll -> viewModel.onEvent(FriendListEvent.AddFriendByRoll(name, roll)) },
            onAddFriendBySection = { name, batch, branch, section, el1, el2 ->
                viewModel.onEvent(
                    FriendListEvent.AddFriendBySection(
                        name = name,
                        batch = batch,
                        branch = branch,
                        section = section,
                        elective1 = el1,
                        elective2 = el2
                    )
                )
            },
            onShowAddDialog = { show -> viewModel.onEvent(FriendListEvent.ShowAddDialog(show)) },
            availableData = availableData,
            isAddingFriend = isAddingFriend,
            addFriendError = addFriendError
        )
    }
}
