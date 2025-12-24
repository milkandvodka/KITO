package com.kito.ui.components

import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.attendance.toAttendanceEntity
import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.section.SectionRepository
import com.kito.data.local.db.student.StudentEntity
import com.kito.data.local.db.student.StudentRepository
import com.kito.data.local.preferences.PrefsRepository
import com.kito.data.remote.SupabaseRepository
import com.kito.sap.AttendanceResult
import com.kito.sap.SapRepository
import com.kito.ui.newUi.viewmodel.SyncResult
import javax.inject.Inject

class AppSyncUseCase @Inject constructor(
    private val prefs: PrefsRepository,
    private val supaBaseRepository: SupabaseRepository,
    private val studentRepository: StudentRepository,
    private val sectionRepository: SectionRepository,
    private val attendanceRepository: AttendanceRepository,
    private val sapRepository: SapRepository
) {
    private suspend fun fetchStudents( roll: String ): List<StudentEntity>{
        val students = supaBaseRepository.getStudentByRoll(roll)
        return listOf(students)
    }
    private suspend fun fetchTimeTable(section: String, batch: String): List<SectionEntity>{
        val timeTable = supaBaseRepository.getTimetableForStudent(section = section, batch = batch)
        return timeTable
    }
    private suspend fun fetchAndUpsertTimetable(
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
    private suspend fun fetchAttendance(
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
    private suspend fun fetchAndUpsertAttendance(
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

    suspend fun syncAll(
        roll: String,
        sapPassword: String,
        year: String,
        term: String
    ): Result<Unit> {
        return try {
            val student = supaBaseRepository.getStudentByRoll(roll)
            studentRepository.insertStudent(listOf(student))
            val timetable = supaBaseRepository.getTimetableForStudent(
                section = student.section,
                batch = student.batch
            )
            sectionRepository.insertSection(timetable)
            if (sapPassword.isNotEmpty()) {
                val response = sapRepository.login(
                    username = roll,
                    password = sapPassword,
                    academicYear = year,
                    termCode = term
                )
                if (response is AttendanceResult.Error) {
                    return Result.failure(
                        IllegalStateException(response.message)
                    )
                }
                if (response is AttendanceResult.Success) {
                    attendanceRepository.insertAttendance(
                        response.data.subjects.map {
                            it.toAttendanceEntity(year, term)
                        }
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}