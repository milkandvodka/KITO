package com.kito.ui.newUi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.attendance.toAttendanceEntity
import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.section.SectionRepository
import com.kito.data.local.db.student.StudentEntity
import com.kito.data.local.db.student.StudentRepository
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.data.remote.SupabaseRepository
import com.kito.sap.AttendanceResult
import com.kito.sap.SapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSetupViewModel @Inject constructor(
    private val prefs: PrefsRepository,
    private val attendanceRepository: AttendanceRepository,
    private val sectionRepository: SectionRepository,
    private val studentRepository: StudentRepository,
    private val sapRepository: SapRepository,
    private val supaBaseRepository: SupabaseRepository
) : ViewModel(){
    private val _setupState = MutableStateFlow<SetupState>(SetupState.Idle)
    val setupState = _setupState.asStateFlow()
    suspend fun setUserName(name: String) {
        val formattedName = name
            .trim()
            .replace("\\s+".toRegex(), " ")
            .lowercase()
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }

        prefs.setUserName(formattedName)
    }
    suspend fun setUserRoll(roll: String){
        prefs.setUserRollNumber(roll)
    }
    suspend fun setSapPassword(sapPassword: String){
        prefs.setSapPassword(sapPassword)
    }
    suspend fun setUserSetupDone() {
        prefs.setUserSetupDone()
    }

    suspend fun fetchStudents( roll: String ): List<StudentEntity>{
        val students = supaBaseRepository.getStudentByRoll(roll)
        return listOf(students)
    }
    suspend fun fetchTimeTable(section: String, batch: String): List<SectionEntity>{
        val timeTable = supaBaseRepository.getTimetableForStudent(section = section, batch = batch)
        return timeTable
    }
    suspend fun fetchAndUpsertTimetable(
        roll: String,
    ){
        val students = fetchStudents(
            roll
        )
        studentRepository.insertStudent(students)
        val student = studentRepository.getStudentByRoll(roll)
        val timeTable = fetchTimeTable(student.section,student.batch)
        sectionRepository.insertSection(timeTable)
    }

    suspend fun fetchAttendance(
        userRollNumber: String,
        userSapPassword: String,
        year: String,
        term: String
    ): AttendanceResult {
        return sapRepository.login(
            username = userRollNumber,
            password = userSapPassword,
            academicYear = year,
            termCode = term
        )
    }

    suspend fun fetchAndUpsertAttendance(
        username: String,
        password: String,
        year: String,
        term: String
    ): SyncResult {

        return when (
            val response = fetchAttendance(username, password, year, term)
        ) {

            is AttendanceResult.Error -> {
                SyncResult.Error(response.message)
            }

            is AttendanceResult.Success -> {
                val attendanceEntities = response.data.subjects.map {
                    it.toAttendanceEntity(year, term)
                }
                attendanceRepository.insertAttendance(attendanceEntities)
                SyncResult.Success
            }
        }
    }
    fun completeSetup(
        name: String,
        roll: String,
        sapPassword: String,
        year: String = "2025",
        term: String = "020"
    ) {
        viewModelScope.launch {
            _setupState.value = SetupState.Loading

            try {
                setUserName(name)
                setUserRoll(roll)

                fetchAndUpsertTimetable(
                    roll = roll
                )

                if (sapPassword.isNotEmpty()) {
                    when (
                        val result = fetchAndUpsertAttendance(
                            username = roll,
                            password = sapPassword,
                            year = year,
                            term = term
                        )
                    ) {
                        is SyncResult.Error -> {
                            _setupState.value = SetupState.Error(result.message)
                            return@launch
                        }
                        SyncResult.Success -> {
                            setSapPassword(sapPassword)
                        }
                    }
                }
                setUserSetupDone()
                _setupState.value = SetupState.Success

            } catch (e: Exception) {
                _setupState.value = SetupState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }

}

sealed class SyncResult {
    object Success : SyncResult()
    data class Error(val message: String) : SyncResult()
}
sealed class SetupState {
    object Idle : SetupState()
    object Loading : SetupState()
    object Success : SetupState()
    data class Error(val message: String) : SetupState()
}