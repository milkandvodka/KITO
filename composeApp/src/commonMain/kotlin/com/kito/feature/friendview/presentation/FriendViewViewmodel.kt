package com.kito.feature.friendview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.domain.repository.FriendViewRepository
import com.kito.feature.schedule.presentation.WeekDay
import com.kito.feature.friendview.presentation.components.sortedChronologically
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FriendViewViewmodel(
    private val friendViewRepository: FriendViewRepository,
    private val prefs: PrefsRepository,
    private val dispatcher: kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default,
) : ViewModel() {

    val friendRolls = prefs.friendRollsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedFriendRoll = prefs.selectedFriendRollFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun onEvent(event: FriendViewEvent) {
        when (event) {
            is FriendViewEvent.SelectFriend -> selectFriend(event.roll)
            is FriendViewEvent.AddFriend -> addFriend(event.roll)
            is FriendViewEvent.RemoveFriend -> removeFromList(event.roll)
        }
    }

    private fun selectFriend(roll: String) {
        viewModelScope.launch(dispatcher) { prefs.setSelectedFriendRoll(roll) }
    }

    private fun addFriend(roll: String) {
        viewModelScope.launch(dispatcher) {
            prefs.addFriendRoll(roll)
            selectFriend(roll)
        }
    }

    private fun removeFromList(roll: String) {
        viewModelScope.launch(dispatcher) {
            prefs.removeFriendRoll(roll)
            if (roll == selectedFriendRoll.value) {
                selectFriend(friendRolls.value.firstOrNull() ?: "")
            }
        }
    }

    val weeklySchedule: StateFlow<Map<WeekDay, List<FriendScheduleItem>>> =
        selectedFriendRoll
            .flatMapLatest { roll ->
                if (roll.isBlank()) {
                    flow { emit(emptyMap()) }
                } else {
                    flow {
                        try {
                            val items = friendViewRepository.getFriendSchedule(roll)
                            val grouped = WeekDay.entries.associateWith { day ->
                                items.filter { com.kito.feature.schedule.presentation.components.normalizeDay(it.day) == day.apiValue }
                                    .sortedChronologically()
                            }
                            emit(grouped)
                        } catch (e: Exception) {
                            println("Error fetching friend schedule: ${e.message}")
                            emit(emptyMap<WeekDay, List<FriendScheduleItem>>())
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap(),
            )
}
