package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceEntity
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.studentsection.StudentSectionEntity
import com.kito.data.local.db.studentsection.StudentSectionRepository
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val prefs: PrefsRepository,
    private val attendanceRepository: AttendanceRepository,
    private val studentSectionRepository: StudentSectionRepository
): ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _sapLoggedIn = MutableStateFlow(false)
    val sapLoggedIn = _sapLoggedIn.asStateFlow()

    private val todayFlow = MutableStateFlow(
        LocalDate.now().dayOfWeek
            .name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
    )

    private val attendance: StateFlow<List<AttendanceEntity>> =
        attendanceRepository
            .getAllAttendance()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    @OptIn(ExperimentalCoroutinesApi::class)
    val schedule: StateFlow<List<StudentSectionEntity>> =
        prefs.userRollFlow
            .flatMapLatest { roll ->
                studentSectionRepository.getScheduleForStudent(
                    rollNo = roll,
                    day = todayFlow.value
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    val averageAttendancePercentage: StateFlow<Double> =
        attendance
            .map { list ->
                if (list.isEmpty()) {
                    0.0
                } else {
                    list.map { it.percentage }.average()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )

    init {
        viewModelScope.launch {
            _name.value = prefs.getUserName()
            _sapLoggedIn.value = prefs.getSapPassword().isNotEmpty()
        }
    }
}