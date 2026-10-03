package com.kito.feature.friendview.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.feature.friendview.domain.repository.FriendViewRepository
import com.kito.feature.friendview.presentation.components.sortedChronologically
import com.kito.feature.schedule.presentation.WeekDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Provided

import com.kito.feature.schedule.presentation.components.normalizeDay

class FriendScheduleViewModel(
    @Provided private val friendViewRepository: FriendViewRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendScheduleUiState())
    val uiState: StateFlow<FriendScheduleUiState> = _uiState.asStateFlow()

    fun loadSchedule(roll: String) {
        _uiState.update { it.copy(roll = roll, isLoading = true, error = null) }
        viewModelScope.launch(dispatcher) {
            try {
                val summary = runCatching { friendViewRepository.getFriendSummary(roll) }.getOrNull()
                val items = friendViewRepository.getFriendSchedule(roll)
                val grouped = WeekDay.entries.associateWith { day ->
                    items.filter { normalizeDay(it.day) == day.apiValue }.sortedChronologically()
                }
                _uiState.update {
                    it.copy(
                        roll = roll,
                        summary = summary,
                        schedule = grouped,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        roll = roll,
                        schedule = emptyMap(),
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}
