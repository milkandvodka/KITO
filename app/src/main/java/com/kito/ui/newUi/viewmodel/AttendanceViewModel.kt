package com.kito.ui.newUi.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.sap.AttendanceResult
import com.kito.sap.SapPortalClient
import com.kito.ui.legacy.State.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    var prefs: PrefsRepository
) : ViewModel(){
    private val sapClient = SapPortalClient()

    private val _uiState = mutableStateOf<UiState>(UiState.Loading)
    val uiState: UiState
        get() = _uiState.value

    init {
        viewModelScope.launch {
            checkForSavedCredentials()
        }
    }

    private suspend fun checkForSavedCredentials() {
        if (prefs.hasSavedCredentials()) {
            // Auto-login
            val username = prefs.getUsername()
            val password = prefs.getPassword()
            val year = prefs.getAcademicYear()
            val term = prefs.getTermCode()
            login(username, password, year, term, autoLogin = true)
        } else {
            _uiState.value = UiState.Login
        }
    }

    fun login(
        username: String,
        password: String,
        academicYear: String,
        termCode: String,
        autoLogin: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val yearToUse = academicYear.ifEmpty { "" }
                val termToUse = termCode.ifEmpty { "" }

                val result = sapClient.fetchAttendance(username, password, yearToUse, termToUse)
                when (result) {
                    is AttendanceResult.Success -> {
                        // Save credentials only if user manually logged in (not auto)
                        if (!autoLogin) {
                            prefs.saveCredentials(username, password, academicYear, termCode)
                        }
//                        _uiState.value = UiState.Attendance(result.data)
                    }
                    is AttendanceResult.Error -> {
                        _uiState.value = UiState.Error(result.message)
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clearCredentials()
        }
        _uiState.value = UiState.Login
    }

    fun changeYearOrSession() {
        viewModelScope.launch {
            // Keep username and password, only clear year and session so new defaults can be selected
            val username = prefs.getUsername()
            val password = prefs.getPassword()
            prefs.clearYearAndSession()  // Clear only year and session
            _uiState.value = UiState.YearSessionChange(storedUsername = username, storedPassword = password)
        }
    }

    fun handleRetry(context: Context) {
        viewModelScope.launch {
            // Check if this is a login error vs attendance fetching error
            val currentState = _uiState.value
            if (currentState is UiState.Error) {
                val errorMessage = currentState.message
                val isLoginError = errorMessage.contains("Invalid credentials") ||
                        errorMessage.contains("authentication failed") ||
                        errorMessage.contains("Failed to load login page")

                if (isLoginError) {
                    // For login errors, just show the login screen again (no auto-retry with bad creds)
                    _uiState.value = UiState.Login
                } else {
                    // For attendance fetching errors, try to re-fetch with stored credentials
                    val username = prefs.getUsername()
                    val password = prefs.getPassword()
                    val year = prefs.getAcademicYear()
                    val term = prefs.getTermCode()
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        login(username, password, year, term, autoLogin = true)
                    } else {
                        // If no stored credentials, go back to login
                        _uiState.value = UiState.Login
                    }
                }
            }
        }
    }

    fun showAbout() {
        // This function can be used to trigger the about dialog
        // For now, we'll handle the about display in the UI directly
    }
}