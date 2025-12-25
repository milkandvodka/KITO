package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.studentsection.StudentSectionEntity
import com.kito.data.local.db.studentsection.StudentSectionRepository
import com.kito.data.local.preferences.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ScheduleScreenViewModel @Inject constructor(
    private val prefs: PrefsRepository,
    private val studentSectionRepository: StudentSectionRepository
): ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklySchedule: StateFlow<Map<WeekDay, List<StudentSectionEntity>>> =
        prefs.userRollFlow
            .flatMapLatest { roll ->
                kotlinx.coroutines.flow.combine(
                    WeekDay.entries.map { day ->
                        studentSectionRepository
                            .getScheduleForStudent(
                                rollNo = roll,
                                day = day.apiValue
                            )
                            .map { list -> day to list }
                    }
                ) { results ->
                    results.toMap()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )
}

enum class WeekDay(val apiValue: String) {
    MON("MON"),
    TUE("TUE"),
    WED("WED"),
    THU("THU"),
    FRI("FRI"),
    SAT("SAT"),
}