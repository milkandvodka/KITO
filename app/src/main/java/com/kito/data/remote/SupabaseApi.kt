package com.kito.data.remote

import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.student.StudentEntity
import retrofit2.http.GET
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
}
