package com.kito.data.remote

import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.student.StudentEntity
import com.kito.data.remote.model.TeacherFuzzySearchModel
import com.kito.data.remote.model.TeacherModel
import com.kito.data.remote.model.TeacherScheduleByIDModel
import com.kito.data.remote.request.TeacherScheduleByIDRequest
import com.kito.data.remote.request.TeacherSearchRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    @GET("rest/v1/timetable")
    suspend fun getTimetable(): List<SectionEntity>

    @GET("rest/v1/students")
    suspend fun getStudents(): List<StudentEntity>

    @GET("rest/v1/students")
    suspend fun getStudentByRoll(
        @Query("roll_no") rollFilter: String,
        @Query("select") select: String = "*"
    ): List<StudentEntity>

    @GET("rest/v1/timetable")
    suspend fun getTimetableForStudent(
        @Query("section") sectionFilter: String,
        @Query("batch") batchFilter: String,
        @Query("select") select: String = "*"
    ): List<SectionEntity>

    @GET("rest/v1/v_teachers_with_details")
    suspend fun getAllTeacherDetail(): List<TeacherModel>

    @POST("rest/v1/rpc/search_teachers_fuzzy")
    suspend fun getTeacherSearchResponse(
        @Body request : TeacherSearchRequest
    ):List<TeacherFuzzySearchModel>

    @POST("rest/v1/rpc/get_teacher_schedule_by_id")
    suspend fun getTeacherScheduleById(
        @Body request: TeacherScheduleByIDRequest
    ): List<TeacherScheduleByIDModel>

    @GET("rest/v1/v_teachers_with_details")
    suspend fun getTeacherDetailByID(
        @Query("teacher_id") teacherId: String
    ): List<TeacherModel>

}
