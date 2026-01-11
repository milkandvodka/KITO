package com.kito.data.remote

import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.student.StudentEntity
import com.kito.data.remote.model.TeacherFuzzySearchModel
import com.kito.data.remote.model.TeacherModel
import com.kito.data.remote.model.TeacherScheduleByIDModel
import com.kito.data.remote.request.TeacherScheduleByIDRequest
import com.kito.data.remote.request.TeacherSearchRequest
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

    suspend fun getAllTeacherDetail(): List<TeacherModel> {
        return api.getAllTeacherDetail()
    }

    suspend fun getTeacherSearchResponse(query: String): List<TeacherFuzzySearchModel> {
        return api.getTeacherSearchResponse(
            request = TeacherSearchRequest(
                p_query = query
            )
        )
    }

    suspend fun getTeacherScheduleById(teacherId: Long): List<TeacherScheduleByIDModel> {
        return api.getTeacherScheduleById(
            request = TeacherScheduleByIDRequest(
                p_teacher_id = teacherId
            )
        )
    }

    suspend fun getTeacherDetailByID(teacherId: Long): List<TeacherModel>{
        return api.getTeacherDetailByID(
            teacherId = "eq.$teacherId"
        )
    }
}
