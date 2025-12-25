package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceEntity
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.preferences.PrefsRepository
import com.kito.data.local.preferences.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AttendanceListScreenViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val prefs: PrefsRepository,
    private val securePrefs: SecurePrefs
): ViewModel(){
    val sapLoggedIn = securePrefs.isLoggedInFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
    val attendance: StateFlow<List<AttendanceEntity>> =
        attendanceRepository
            .getAllAttendance()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}