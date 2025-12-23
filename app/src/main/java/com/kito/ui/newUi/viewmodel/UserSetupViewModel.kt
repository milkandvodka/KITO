package com.kito.ui.newUi.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.attendance.toAttendanceEntity
import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.section.SectionRepository
import com.kito.data.local.db.student.StudentEntity
import com.kito.data.local.db.student.StudentRepository
import com.kito.data.local.preferences.newpreferences.PrefsRepository
import com.kito.sap.SubjectAttendance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSetupViewModel @Inject constructor(
    private val prefs: PrefsRepository,
    private val attendanceRepository: AttendanceRepository,
    private val sectionRepository: SectionRepository,
    private val studentRepository: StudentRepository
) : ViewModel(){
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
    suspend fun setUserSetupDone(){
        prefs.setUserSetupDone()
    }
    suspend fun fetchAttendance(
        year: String,
        term: String
    ): List<SubjectAttendance> {
        delay(1000)
        return sampleAttendance
    }

    suspend fun fetchStudents(): List<StudentEntity>{
        delay(1000)
        return sampleStudent
    }
    suspend fun fetchTimeTable(): List<SectionEntity>{
        delay(1000)
        return sampleSection
    }
    suspend fun fetchAndUpsertTimetable(){
        delay(1000)
        val timeTable = fetchTimeTable()
        val students = fetchStudents()
        sectionRepository.insertSection(timeTable)
        studentRepository.insertStudent(students)
    }
    suspend fun fetchAndUpsertAttendance(
        year: String,
        term: String
    ){
        delay(1000)
        val response = fetchAttendance(
            year = year,
            term = term
        )
        val attendance = response.map {
            it.toAttendanceEntity(
                year = year,
                term = term
            )
        }
        attendanceRepository.insertAttendance(attendance)
    }
    fun getAttendance() {
        viewModelScope.launch {
            attendanceRepository
                .getAllAttendance()
                .collect { list ->
                    Log.d("Attendance_DEMO", "Rows: ${list.size}")
                }
        }
    }
}

private val sampleStudent = listOf(
    StudentEntity(
        roll_no = "23053382",
        section = "CSE-24",
        batch = "batch_3"
    ),
    StudentEntity(
        roll_no = "23053287",
        section = "CSE-35",
        batch = "batch_3"
    )
)

private val sampleSection = listOf(

    // ================== CSE-24 ==================

    // Monday
    SectionEntity(1, "CSE-24", "Monday", "09:00", "10:00", "Data Structures", "LH-101", "batch_3"),
    SectionEntity(2, "CSE-24", "Monday", "10:00", "11:00", "Discrete Mathematics", "LH-102", "batch_3"),
    SectionEntity(3, "CSE-24", "Monday", "14:00", "16:00", "DSA Lab", "LAB-3", "batch_3"),

    // Tuesday
    SectionEntity(4, "CSE-24", "Tuesday", "09:00", "10:00", "Computer Organization", "LH-101", "batch_3"),
    SectionEntity(5, "CSE-24", "Tuesday", "10:00", "11:00", "Probability & Statistics", "LH-102", "batch_3"),
    SectionEntity(6, "CSE-24", "Tuesday", "14:00", "16:00", "COA Lab", "LAB-2", "batch_3"),

    // Wednesday
    SectionEntity(7, "CSE-24", "Wednesday", "09:00", "10:00", "Operating Systems", "LH-101", "batch_3"),
    SectionEntity(8, "CSE-24", "Wednesday", "10:00", "11:00", "Theory of Computation", "LH-102", "batch_3"),
    SectionEntity(9, "CSE-24", "Wednesday", "14:00", "16:00", "OS Lab", "LAB-4", "batch_3"),

    // Thursday
    SectionEntity(10, "CSE-24", "Thursday", "09:00", "10:00", "Database Systems", "LH-101", "batch_3"),
    SectionEntity(11, "CSE-24", "Thursday", "10:00", "11:00", "Software Engineering", "LH-102", "batch_3"),
    SectionEntity(12, "CSE-24", "Thursday", "14:00", "16:00", "DBMS Lab", "LAB-1", "batch_3"),

    // Friday
    SectionEntity(13, "CSE-24", "Friday", "09:00", "10:00", "Computer Networks", "LH-101", "batch_3"),
    SectionEntity(14, "CSE-24", "Friday", "10:00", "11:00", "Artificial Intelligence", "LH-102", "batch_3"),
    SectionEntity(15, "CSE-24", "Friday", "14:00", "16:00", "Mini Project Lab", "LAB-5", "batch_3"),
    // ================== CSE-35 ==================

    // Monday
    SectionEntity(16, "CSE-35", "Monday", "09:00", "10:00", "Operating Systems", "LH-201", "batch_3"),
    SectionEntity(17, "CSE-35", "Monday", "10:00", "11:00", "Discrete Mathematics", "LH-202", "batch_3"),
    SectionEntity(18, "CSE-35", "Monday", "14:00", "16:00", "OS Lab", "LAB-6", "batch_3"),

    // Tuesday
    SectionEntity(19, "CSE-35", "Tuesday", "09:00", "10:00", "Computer Networks", "LH-201", "batch_3"),
    SectionEntity(20, "CSE-35", "Tuesday", "10:00", "11:00", "Database Systems", "LH-202", "batch_3"),
    SectionEntity(21, "CSE-35", "Tuesday", "14:00", "16:00", "CN Lab", "LAB-5", "batch_3"),

    // Wednesday
    SectionEntity(22, "CSE-35", "Wednesday", "09:00", "10:00", "Software Engineering", "LH-201", "batch_3"),
    SectionEntity(23, "CSE-35", "Wednesday", "10:00", "11:00", "Theory of Computation", "LH-202", "batch_3"),
    SectionEntity(24, "CSE-35", "Wednesday", "14:00", "16:00", "SE Lab", "LAB-4", "batch_3"),

    // Thursday
    SectionEntity(25, "CSE-35", "Thursday", "09:00", "10:00", "Artificial Intelligence", "LH-201", "batch_3"),
    SectionEntity(26, "CSE-35", "Thursday", "10:00", "11:00", "Compiler Design", "LH-202", "batch_3"),
    SectionEntity(27, "CSE-35", "Thursday", "14:00", "16:00", "CD Lab", "LAB-2", "batch_3"),

    // Friday
    SectionEntity(28, "CSE-35", "Friday", "09:00", "10:00", "Machine Learning", "LH-201", "batch_3"),
    SectionEntity(29, "CSE-35", "Friday", "10:00", "11:00", "Cloud Computing", "LH-202", "batch_3"),
    SectionEntity(30, "CSE-35", "Friday", "14:00", "16:00", "ML Lab", "LAB-1", "batch_3")
)

private val sampleAttendance = listOf(
    SubjectAttendance(
        subjectCode = "00F4",
        subjectName = "Data Mining and Data Warehousing",
        attendedClasses = 4,
        totalClasses = 41,
        percentage = (4.0 / 41) * 100,
        facultyName = "Amiya Ranjan Panda"
    ),
    SubjectAttendance(
        subjectCode = "00F5",
        subjectName = "Engineering Economics",
        attendedClasses = 4,
        totalClasses = 39,
        percentage = (4.0 / 39) * 100,
        facultyName = "Arvind Kumar Yadav"
    ),
    SubjectAttendance(
        subjectCode = "00F6",
        subjectName = "Design and Analysis of Algorithms",
        attendedClasses = 1,
        totalClasses = 41,
        percentage = (1.0 / 41) * 100,
        facultyName = "Partha Sarathi Paul"
    ),
    SubjectAttendance(
        subjectCode = "00F7",
        subjectName = "Software Engineering",
        attendedClasses = 24,
        totalClasses = 52,
        percentage = (24.0 / 52) * 100,
        facultyName = "Ipsita Paul"
    ),
    SubjectAttendance(
        subjectCode = "00F8",
        subjectName = "Computer Networks",
        attendedClasses = 10,
        totalClasses = 40,
        percentage = (10.0 / 40) * 100,
        facultyName = "Nitin Varyani"
    )
)