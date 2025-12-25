package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.preferences.PrefsRepository
import com.kito.data.local.preferences.SecurePrefs
import com.kito.ui.components.AppSyncUseCase
import com.kito.ui.components.state.SyncUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PrefsRepository,
    private val securePrefs: SecurePrefs,
    private val attendanceRepository: AttendanceRepository,
    private val appSyncUseCase: AppSyncUseCase
): ViewModel(){
    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState = _syncState.asStateFlow()
    val name = prefs.userNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )
    val rollNumber = prefs.userRollFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )
    val year = prefs.academicYearFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )
    val term: StateFlow<String> =
        prefs.termCodeFlow
            .map { code ->
                when (code) {
                    "010" -> "Autumn"
                    "020" -> "Spring"
                    else -> "Unknown"
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = "Unknown"
            )
    val isLoggedIn = securePrefs.isLoggedInFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = securePrefs.isLoggedIn()
            )
    suspend fun changeName(name: String){
        prefs.setUserName(name)
    }
    suspend fun changeRoll(roll: String){
        prefs.setUserRollNumber(roll)
        securePrefs.clearSapPassword()
        attendanceRepository.deleteAllAttendance()
    }
    suspend fun changeYearTerm(year: String, term: String) {
        prefs.setAcademicYear(year)
        prefs.setTermCode(term)
    }
    suspend fun logOut(){
        securePrefs.clearSapPassword()
        attendanceRepository.deleteAllAttendance()
    }
    suspend fun logIn(password: String) {
        _syncState.value = SyncUiState.Loading
        val roll = prefs.userRollFlow.first()
        val year = prefs.academicYearFlow.first()
        val term = prefs.termCodeFlow.first()

        val result = appSyncUseCase.syncAll(
            roll = roll,
            sapPassword = password,
            year = year,
            term = term
        )

        _syncState.value = result.fold(
            onSuccess = {
                securePrefs.saveSapPassword(password)
                SyncUiState.Success
            },
            onFailure = {
                SyncUiState.Error(it.message ?: "Sync failed")
            }
        )

    }
}