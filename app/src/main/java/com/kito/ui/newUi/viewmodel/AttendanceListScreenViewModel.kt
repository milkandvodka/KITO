package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceEntity
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceListScreenViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val prefs: PrefsRepository
): ViewModel(){
    private val _sapLoggedIn = MutableStateFlow(false)
    val sapLoggedIn = _sapLoggedIn.asStateFlow()
    val attendance: StateFlow<List<AttendanceEntity>> =
        attendanceRepository
            .getAllAttendance()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    init {
        viewModelScope.launch {
            _sapLoggedIn.value = prefs.getSapPassword().isNotEmpty()
        }
    }
}