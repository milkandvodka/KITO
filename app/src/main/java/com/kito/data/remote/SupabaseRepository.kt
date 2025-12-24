package com.kito.data.remote

import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.student.StudentEntity
import javax.inject.Inject

class SupabaseRepository @Inject constructor(
    private val api: SupabaseApi
) {

    suspend fun getStudents(): List<StudentEntity> {
        return api.getStudents()
    }

    suspend fun getSection(): List<SectionEntity> {
        return api.getTimetable()
    }

    suspend fun getStudentByRoll(rollNo: String): StudentEntity {
        val result = api.getStudentByRoll("eq.$rollNo")

        if (result.isEmpty()) {
            throw IllegalStateException("Student not found in Supabase")
        }

        return result.first()
    }

    suspend fun getTimetableForStudent(
        section: String,
        batch: String
    ): List<SectionEntity> {
        return api.getTimetableForStudent(
            sectionFilter = "eq.$section",
            batchFilter = "eq.$batch"
        )
    }

}
