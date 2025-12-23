package com.kito.data.local.db.student

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface StudentDAO {

    @Upsert
    suspend fun insertStudent(studentEntity: List<StudentEntity>)

    @Delete
    suspend fun deleteStudent(studentEntity: StudentEntity)

}