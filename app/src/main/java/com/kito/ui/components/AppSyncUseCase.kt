package com.kito.ui.components

import android.util.Log
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.attendance.toAttendanceEntity
import com.kito.data.local.db.section.SectionRepository
import com.kito.data.local.db.student.StudentRepository
import com.kito.data.local.preferences.PrefsRepository
import com.kito.data.remote.SupabaseRepository
import com.kito.sap.AttendanceResult
import com.kito.sap.SapRepository
import javax.inject.Inject

class AppSyncUseCase @Inject constructor(
    private val prefs: PrefsRepository,
    private val supaBaseRepository: SupabaseRepository,
    private val studentRepository: StudentRepository,
    private val sectionRepository: SectionRepository,
    private val attendanceRepository: AttendanceRepository,
    private val sapRepository: SapRepository
){
    suspend fun syncAll(
        roll: String,
        sapPassword: String,
        year: String,
        term: String
    ): Result<Unit> {
        try {
            val student = supaBaseRepository.getStudentByRoll(roll)
            studentRepository.insertStudent(listOf(student))

            val timetable = supaBaseRepository.getTimetableForStudent(
                section = student.section,
                batch = student.batch
            )
            sectionRepository.insertSection(timetable)
        } catch (e: Exception) {
            Log.e("Supabase", "Supabase sync failed for roll=$roll")
        }
        if (sapPassword.isNotEmpty()) {
            try {
                val response = sapRepository.login(
                    username = roll,
                    password = sapPassword,
                    academicYear = year,
                    termCode = term
                )

                when (response) {
                    is AttendanceResult.Error -> {
                        return Result.failure(
                            IllegalStateException(response.message)
                        )
                    }

                    is AttendanceResult.Success -> {
                        attendanceRepository.insertAttendance(
                            response.data.subjects.map {
                                it.toAttendanceEntity(year, term)
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
        return Result.success(Unit)
    }

//    suspend fun syncAll(
//        roll: String,
//        sapPassword: String,
//        year: String,
//        term: String
//    ): Result<Unit> {
//        return try {
//            val student = supaBaseRepository.getStudentByRoll(roll)
//            studentRepository.insertStudent(listOf(student))
//            val timetable = supaBaseRepository.getTimetableForStudent(
//                section = student.section,
//                batch = student.batch
//            )
//            sectionRepository.insertSection(timetable)
//            if (sapPassword.isNotEmpty()) {
//                val response = sapRepository.login(
//                    username = roll,
//                    password = sapPassword,
//                    academicYear = year,
//                    termCode = term
//                )
//                if (response is AttendanceResult.Error) {
//                    return Result.failure(
//                        IllegalStateException(response.message)
//                    )
//                }
//                if (response is AttendanceResult.Success) {
//                    attendanceRepository.insertAttendance(
//                        response.data.subjects.map {
//                            it.toAttendanceEntity(year, term)
//                        }
//                    )
//                }
//            }
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}