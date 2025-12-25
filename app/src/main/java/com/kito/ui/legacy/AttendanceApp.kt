//package com.kito.ui.legacy
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import android.content.Context
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.kito.ui.legacy.viewmodel.AttendanceViewModelFactory
//import com.kito.data.local.preferences.legacypreferences.getAcademicYear
//import com.kito.data.local.preferences.legacypreferences.getPassword
//import com.kito.data.local.preferences.legacypreferences.getTermCode
//import com.kito.data.local.preferences.legacypreferences.getUsername
//import kotlinx.coroutines.launch
//import com.kito.ui.legacy.State.UiState
//import com.kito.ui.legacy.component.AboutDialog
//import com.kito.ui.legacy.screens.AttendanceScreen
//import com.kito.ui.legacy.screens.ErrorScreen
//import com.kito.ui.legacy.screens.LoginScreen
//import com.kito.ui.legacy.screens.YearSessionChangeScreen
//import com.kito.ui.legacy.viewmodel.AttendanceViewModel
//
//@Composable
//fun AttendanceApp(context: Context) {
//    val viewModel: AttendanceViewModel = viewModel(
//        factory = AttendanceViewModelFactory(context)
//    )
//
//    var showAboutDialog by remember { mutableStateOf(false) }
//
//    when (val uiState = viewModel.uiState) {
//        is UiState.Loading -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//        is UiState.Login -> {
//            LoginScreen(
//                onLogin = { username, password, academicYear, termCode ->
//                    viewModel.login(username, password, academicYear, termCode)
//                }
//            )
//        }
//        is UiState.YearSessionChange -> {
//            YearSessionChangeScreen(
//                onYearSessionChange = { academicYear, termCode ->
//                    // Use stored credentials but with new year/session
//                    viewModel.login(uiState.storedUsername, uiState.storedPassword, academicYear, termCode)
//                }
//            )
//        }
//        is UiState.Attendance -> {
//            AttendanceScreen(
//                attendanceData = uiState.attendanceData,
//                onLogout = { viewModel.logout() },
//                onChangeYearSession = { viewModel.changeYearOrSession() },
//                onShowAbout = { showAboutDialog = true },
//                onRefresh = {
//                    // Get stored credentials and re-fetch attendance
//                    viewModel.viewModelScope.launch {
//                        val username = context.getUsername()
//                        val password = context.getPassword()
//                        val year = context.getAcademicYear()
//                        val term = context.getTermCode()
//                        if (username.isNotEmpty() && password.isNotEmpty()) {
//                            viewModel.login(username, password, year, term, autoLogin = true)
//                        }
//                    }
//                }
//            )
//        }
//        is UiState.Error -> {
//            // For all errors, show the error screen and let viewmodel handle the logic
//            ErrorScreen(
//                errorMessage = uiState.message,
//                onRetry = {
//                    // Delegate to view model to handle retry logic properly
//                    viewModel.handleRetry(context)
//                },
//                onChangeYearSession = { viewModel.changeYearOrSession() }
//            )
//        }
//    }
//
//    // Show About dialog if requested
//    if (showAboutDialog) {
//        AboutDialog(
//            onDismiss = { showAboutDialog = false }
//        )
//    }
//}