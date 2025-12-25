package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceEntity
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.studentsection.StudentSectionEntity
import com.kito.data.local.db.studentsection.StudentSectionRepository
import com.kito.data.local.preferences.PrefsRepository
import com.kito.data.local.preferences.SecurePrefs
import com.kito.ui.components.AppSyncUseCase
import com.kito.ui.components.StartupSyncGuard
import com.kito.ui.components.state.SyncUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val prefs: PrefsRepository,
    private val securePrefs: SecurePrefs,
    private val attendanceRepository: AttendanceRepository,
    private val studentSectionRepository: StudentSectionRepository,
    private val appSyncUseCase: AppSyncUseCase,
    private val syncGuard: StartupSyncGuard
): ViewModel() {
    val name = prefs.userNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    val sapLoggedIn = securePrefs.isLoggedInFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    private val todayFlow = MutableStateFlow(
        when (LocalDate.now().dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "MON"
            java.time.DayOfWeek.TUESDAY -> "TUE"
            java.time.DayOfWeek.WEDNESDAY -> "WED"
            java.time.DayOfWeek.THURSDAY -> "THU"
            java.time.DayOfWeek.FRIDAY -> "FRI"
            else -> ""
        }
    )

    private val attendance: StateFlow<List<AttendanceEntity>> =
        attendanceRepository
            .getAllAttendance()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState = _syncState.asStateFlow()

    private val _syncEvents = MutableSharedFlow<SyncUiState>()
    val syncEvents: SharedFlow<SyncUiState> = _syncEvents

    fun syncOnStartup() {
        if (syncGuard.hasSynced) return
        syncGuard.hasSynced = true
        viewModelScope.launch {
            _syncState.value = SyncUiState.Loading

            val roll = prefs.userRollFlow.first()
            val sapPassword = securePrefs.getSapPassword()
            val year = prefs.academicYearFlow.first()
            val term = prefs.termCodeFlow.first()

            val result = appSyncUseCase.syncAll(
                roll = roll,
                sapPassword = sapPassword,
                year = year,
                term = term
            )

            _syncState.value = result.fold(
                onSuccess = {
                    _syncEvents.emit(SyncUiState.Success)
                    SyncUiState.Success
                },
                onFailure = {
                    SyncUiState.Error(it.message ?: "Sync failed")
                }
            )
        }
    }
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
}