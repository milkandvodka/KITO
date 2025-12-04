package com.kito

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.kito.sap.*

@Composable
fun AttendanceApp(context: Context) {
    val viewModel: AttendanceViewModel = viewModel(
        factory = AttendanceViewModelFactory(context)
    )

    var showAboutDialog by remember { mutableStateOf(false) }

    when (val uiState = viewModel.uiState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Login -> {
            LoginScreen(
                onLogin = { username, password, academicYear, termCode ->
                    viewModel.login(username, password, academicYear, termCode)
                }
            )
        }
        is UiState.YearSessionChange -> {
            YearSessionChangeScreen(
                onYearSessionChange = { academicYear, termCode ->
                    // Use stored credentials but with new year/session
                    viewModel.login(uiState.storedUsername, uiState.storedPassword, academicYear, termCode)
                }
            )
        }
        is UiState.Attendance -> {
            AttendanceScreen(
                attendanceData = uiState.attendanceData,
                onLogout = { viewModel.logout() },
                onChangeYearSession = { viewModel.changeYearOrSession() },
                onShowAbout = { showAboutDialog = true },
                onRefresh = {
                    // Get stored credentials and re-fetch attendance
                    viewModel.viewModelScope.launch {
                        val username = context.getUsername()
                        val password = context.getPassword()
                        val year = context.getAcademicYear()
                        val term = context.getTermCode()
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            viewModel.login(username, password, year, term, autoLogin = true)
                        }
                    }
                }
            )
        }
        is UiState.Error -> {
            // For all errors, show the error screen and let viewmodel handle the logic
            ErrorScreen(
                errorMessage = uiState.message,
                onRetry = {
                    // Delegate to view model to handle retry logic properly
                    viewModel.handleRetry(context)
                },
                onChangeYearSession = { viewModel.changeYearOrSession() }
            )
        }
    }

    // Show About dialog if requested
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LoginScreen(
    onLogin: (String, String, String, String) -> Unit,
    initialUsername: String = "",
    initialPassword: String = "",
    errorText: String = ""
) {
    var username by remember { mutableStateOf(TextFieldValue(initialUsername)) }
    var password by remember { mutableStateOf(TextFieldValue(initialPassword)) }
    var selectedYear by remember { mutableStateOf("") }
    var selectedTerm by remember { mutableStateOf("") }

    // Auto-detect current year
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val years = (currentYear - 5..currentYear).map { it.toString() }.reversed()

    // Auto-select current year & Autumn on first load, unless we're in change mode
    LaunchedEffect(Unit) {
        if (selectedYear.isEmpty()) selectedYear = currentYear.toString()
        if (selectedTerm.isEmpty()) selectedTerm = "010"

        // If we have initial values from change mode, use them to pre-fill
        if (initialUsername.isNotEmpty()) {
            username = TextFieldValue(initialUsername)
        }
        if (initialPassword.isNotEmpty()) {
            password = TextFieldValue(initialPassword)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "KIIT Attendance Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Display error message if provided
        if (errorText.isNotEmpty()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // === YEAR: Horizontal Scrollable Chips ===
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Academic Year",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(years) { year ->
                    val isSelected = selectedYear == year
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedYear = year },
                        label = { Text(year) },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }

        // === TERM: Two clean toggle chips ===
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Term",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedTerm == "010",
                    onClick = { selectedTerm = "010" },
                    label = { Text("Autumn (010)") }
                )
                FilterChip(
                    selected = selectedTerm == "020",
                    onClick = { selectedTerm = "020" },
                    label = { Text("Spring (020)") }
                )
            }
        }

        Button(
            onClick = {
                onLogin(username.text, password.text, selectedYear, selectedTerm)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.text.isNotEmpty() &&
                    password.text.isNotEmpty() &&
                    selectedYear.isNotEmpty() &&
                    selectedTerm.isNotEmpty()
        ) {
            Text("Login & Sync Attendance")
        }
    }
}

@Composable
fun AttendanceScreen(attendanceData: AttendanceData, onLogout: () -> Unit, onRefresh: () -> Unit, onChangeYearSession: () -> Unit = {}, onShowAbout: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Your Attendance",
                style = MaterialTheme.typography.headlineMedium
            )
            Row {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                // Overflow menu for logout, change year/session, and about
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Change Year/Session") },
                        onClick = {
                            expanded = false
                            onChangeYearSession()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("About") },
                        onClick = {
                            expanded = false
                            onShowAbout()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                            expanded = false
                            onLogout()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (attendanceData.subjects.isNotEmpty()) {
            LazyColumn {
                items(attendanceData.subjects.size) { index ->
                    val subject = attendanceData.subjects[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "${subject.subjectCode} - ${subject.subjectName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Attendance: ${subject.attendedClasses}/${subject.totalClasses}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            LinearProgressIndicator(
                                progress = (subject.percentage / 100).toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                            Text(
                                text = "Percentage: ${"%.2f".format(subject.percentage)}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (subject.facultyName.isNotEmpty()) {
                                Text(
                                    text = "Faculty: ${subject.facultyName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No attendance records found",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Try changing the year or session to view attendance for different periods",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit, onChangeYearSession: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Layout for two buttons below the error message with consistent styling
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.8f) // 80% width for consistency
            ) {
                Text("Try Again")
            }
            OutlinedButton(
                onClick = onChangeYearSession,
                modifier = Modifier.fillMaxWidth(0.8f) // Same width as Try Again button
            ) {
                Text("Change Year/Session")
            }
        }
    }
}

sealed class UiState {
    object Loading : UiState()
    object Login : UiState()
    data class YearSessionChange(val storedUsername: String, val storedPassword: String) : UiState()
    data class Attendance(val attendanceData: AttendanceData) : UiState()
    data class Error(val message: String) : UiState()
}

class AttendanceViewModel(private val context: Context) : ViewModel() {
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
        if (context.hasSavedCredentials()) {
            // Auto-login
            val username = context.getUsername()
            val password = context.getPassword()
            val year = context.getAcademicYear()
            val term = context.getTermCode()
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
                val yearToUse = if (academicYear.isNotEmpty()) academicYear else ""
                val termToUse = if (termCode.isNotEmpty()) termCode else ""

                val result = sapClient.fetchAttendance(username, password, yearToUse, termToUse)
                when (result) {
                    is AttendanceResult.Success -> {
                        // Save credentials only if user manually logged in (not auto)
                        if (!autoLogin) {
                            context.saveCredentials(username, password, academicYear, termCode)
                        }
                        _uiState.value = UiState.Attendance(result.data)
                    }
                    is AttendanceResult.Error -> {
                        _uiState.value = UiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            context.clearCredentials()
        }
        _uiState.value = UiState.Login
    }

    fun changeYearOrSession() {
        viewModelScope.launch {
            // Keep username and password, only clear year and session so new defaults can be selected
            val username = context.getUsername()
            val password = context.getPassword()
            context.clearYearAndSession()  // Clear only year and session
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
                    val username = context.getUsername()
                    val password = context.getPassword()
                    val year = context.getAcademicYear()
                    val term = context.getTermCode()
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

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val aboutText = """I built this app because the SAP portal experience was honestly frustrating — logging in every single time, selecting the year and session, and dealing with a cluttered UI. So I wanted to create something simple, fast, and genuinely helpful for students.

This app is a lightweight wrapper around the SAP portal. It logs in automatically, extracts the required tokens, fetches your attendance data, parses everything cleanly, and shows it in a way that actually feels usable.

Key goals:

Make attendance tracking effortless

Clearly show who marked your attendance and who didn't

Help you maintain your target attendance

Provide a clean, friendly UI instead of the default portal mess

Privacy first:
All your credentials are stored locally on your device only. Nothing is uploaded anywhere. The app simply automates what you normally do manually on the SAP website.

I'll keep improving it and adding features over time.

If you want to reach out, feel free to mail me at maiatrangihoon@gmail.com.

And if something suddenly stops working — blame the ICT Cell, not me; they keep changing things. 😄

LinkedIn: https://www.linkedin.com/in/subham-shah-51b29a343/"""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About This App") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = aboutText)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun YearSessionChangeScreen(
    onYearSessionChange: (String, String) -> Unit,
    errorText: String = ""
) {
    var selectedYear by remember { mutableStateOf("") }
    var selectedTerm by remember { mutableStateOf("") }

    // Auto-detect current year
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val years = (currentYear - 5..currentYear).map { it.toString() }.reversed()

    // Auto-select current year & Autumn on first load
    LaunchedEffect(Unit) {
        if (selectedYear.isEmpty()) selectedYear = currentYear.toString()
        if (selectedTerm.isEmpty()) selectedTerm = "010"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Change Year & Session",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Select new academic year and term",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        // Display error message if provided
        if (errorText.isNotEmpty()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // === YEAR: Horizontal Scrollable Chips ===
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Academic Year",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(years) { year ->
                    val isSelected = selectedYear == year
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedYear = year },
                        label = { Text(year) },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }

        // === TERM: Two clean toggle chips ===
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Term",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedTerm == "010",
                    onClick = { selectedTerm = "010" },
                    label = { Text("Autumn (010)") }
                )
                FilterChip(
                    selected = selectedTerm == "020",
                    onClick = { selectedTerm = "020" },
                    label = { Text("Spring (020)") }
                )
            }
        }

        Button(
            onClick = {
                onYearSessionChange(selectedYear, selectedTerm)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedYear.isNotEmpty() && selectedTerm.isNotEmpty()
        ) {
            Text("Update Year & Session")
        }
    }
}