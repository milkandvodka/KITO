package com.kito.data.local.db.student

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface StudentDAO {

    @Upsert
    suspend fun insertStudent(studentEntity: List<StudentEntity>)

    @Delete
    suspend fun deleteStudent(studentEntity: StudentEntity)

    @Query("SELECT * FROM studententity WHERE roll_no = :rollNo")
    suspend fun getStudentByRoll(rollNo: String): StudentEntity

}