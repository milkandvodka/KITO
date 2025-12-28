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
import kotlinx.coroutines.delay
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
    private val appSyncUseCase: AppSyncUseCase,
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
    val requiredAttendance = prefs.requiredAttendanceFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )
    val isLoggedIn = securePrefs.isLoggedInFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )
    fun syncStateIdle(){
        _syncState.value = SyncUiState.Idle
    }
    fun changeName(name: String){
        viewModelScope.launch {
            try {
                _syncState.value = SyncUiState.Loading
                val formattedName = name
                    .trim()
                    .replace("\\s+".toRegex(), " ")
                    .lowercase()
                    .split(" ")
                    .joinToString(" ") { word ->
                        word.replaceFirstChar { it.uppercaseChar() }
                    }
                delay(1000)
                prefs.setUserName(formattedName)
                _syncState.value = SyncUiState.Success
            } catch (e: Exception) {
                _syncState.value = SyncUiState.Error(e.message ?: "Sync failed")
            }
        }
    }
    fun changeRoll(roll: String){
        viewModelScope.launch {
            try {
                _syncState.value = SyncUiState.Loading
                delay(1000)
                prefs.setUserRollNumber(roll)
                securePrefs.clearSapPassword()
                attendanceRepository.deleteAllAttendance()
                val result = appSyncUseCase.syncAll(
                    roll = prefs.userRollFlow.first(),
                    sapPassword = securePrefs.getSapPassword(),
                    year = prefs.academicYearFlow.first(),
                    term = prefs.termCodeFlow.first()
                )
                _syncState.value = result.fold(
                    onSuccess = {
                        SyncUiState.Success
                    },
                    onFailure = {
                        SyncUiState.Error(it.message ?: "Sync failed")
                    }
                )
            } catch (e: Exception) {
                _syncState.value = SyncUiState.Error(e.message ?: "Sync failed")
            }
        }
    }

    fun changeAttendance(attendance: Int){
        viewModelScope.launch {
            try {
                _syncState.value = SyncUiState.Loading
                delay(1000)
                prefs.setRequiredAttendance(attendance)
                _syncState.value = SyncUiState.Success
            }catch (e: Exception) {
                _syncState.value = SyncUiState.Error(e.message ?: "Sync failed")
            }
        }
    }
    fun changeYearTerm(year: String, term: String) {
        viewModelScope.launch {
            try {
                _syncState.value = SyncUiState.Loading
                delay(1000)
                prefs.setAcademicYear(year)
                prefs.setTermCode(term)
                attendanceRepository.deleteAllAttendance()
                val result = appSyncUseCase.syncAll(
                    roll = prefs.userRollFlow.first(),
                    sapPassword = securePrefs.getSapPassword(),
                    year = year,
                    term = term
                )
                _syncState.value = result.fold(
                    onSuccess = {
                        SyncUiState.Success
                    },
                    onFailure = {
                        SyncUiState.Error(it.message ?: "Sync failed")
                    }
                )
            } catch (e: Exception) {
                _syncState.value = SyncUiState.Error(e.message ?: "Sync failed")
            }
        }
    }
    fun logOut(){
        viewModelScope.launch {
            _syncState.value = SyncUiState.Loading
            delay(1000)
            try {
                securePrefs.clearSapPassword()
                attendanceRepository.deleteAllAttendance()
                _syncState.value = SyncUiState.Success
            } catch (e: Exception) {
                _syncState.value = SyncUiState.Error(e.message ?: "Sync failed")
            }
        }
    }
    fun logIn(password: String) {
        viewModelScope.launch {
            _syncState.value = SyncUiState.Loading
            delay(1000)
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
}