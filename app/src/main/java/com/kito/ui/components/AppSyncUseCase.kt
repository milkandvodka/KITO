package com.kito.ui.components

import android.util.Log
import com.kito.data.local.datastore.ProtoDatastoreRepository
import com.kito.data.local.datastore.StudentSectionDatastore
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.attendance.toAttendanceEntity
import com.kito.data.local.db.section.SectionRepository
import com.kito.data.local.db.student.StudentRepository
import com.kito.data.local.db.studentsection.StudentSectionRepository
import com.kito.data.local.preferences.PrefsRepository
import com.kito.data.remote.SupabaseRepository
import com.kito.sap.AttendanceResult
import com.kito.sap.SapRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppSyncUseCase @Inject constructor(
    private val prefs: PrefsRepository,
    private val supaBaseRepository: SupabaseRepository,
    private val studentRepository: StudentRepository,
    private val sectionRepository: SectionRepository,
    private val studentSectionRepository: StudentSectionRepository,
    private val attendanceRepository: AttendanceRepository,
    private val sapRepository: SapRepository,
    private val protoRepo: ProtoDatastoreRepository
){
    suspend fun syncAll(
        roll: String,
        sapPassword: String,
        year: String,
        term: String
    ): Result<Unit> = coroutineScope {
        try {
            val studentDeferred = async {
                supaBaseRepository.getStudentByRoll(roll)
            }
            val student = studentDeferred.await()
            val timetableDeferred = async {
                supaBaseRepository.getTimetableForStudent(
                    section = student.section,
                    batch = student.batch
                )
            }
            val insertStudentJob = launch {
                studentRepository.insertStudent(listOf(student))
            }
            val insertSectionJob = launch {
                sectionRepository.insertSection(timetableDeferred.await())
            }
            val attendanceJob = if (sapPassword.isNotEmpty()) {
                async {
                    val response = sapRepository.login(
                        username = roll,
                        password = sapPassword,
                        academicYear = year,
                        termCode = term
                    )

                    when (response) {
                        is AttendanceResult.Error -> {
                            throw IllegalStateException(response.message)
                        }
                        is AttendanceResult.Success -> {
                            attendanceRepository.insertAttendance(
                                response.data.subjects.map {
                                    it.toAttendanceEntity(year, term)
                                }
                            )
                        }
                    }
                }
            } else null
            insertStudentJob.join()
            insertSectionJob.join()
            val sections = studentSectionRepository.getAllScheduleForStudent(rollNo = roll).first()
            val protoList = sections.map {
                StudentSectionDatastore(
                    sectionId = it.sectionId,
                    rollNo = it.rollNo,
                    section = it.section,
                    batch = it.batch,
                    day = it.day,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    subject = it.subject,
                    room = it.room
                )
            }.toPersistentList()
            protoRepo.setSections(protoList)
            attendanceJob?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}