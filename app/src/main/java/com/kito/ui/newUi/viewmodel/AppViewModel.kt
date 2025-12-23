package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import com.kito.data.local.db.attendance.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
): ViewModel() {

}