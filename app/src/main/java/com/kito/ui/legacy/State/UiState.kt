package com.kito.ui.legacy.State

import com.kito.sap.AttendanceData

sealed class UiState {
    object Loading : UiState()
    object Login : UiState()
    data class YearSessionChange(val storedUsername: String, val storedPassword: String) : UiState()
    data class Attendance(val attendanceData: AttendanceData) : UiState()
    data class Error(val message: String) : UiState()
}