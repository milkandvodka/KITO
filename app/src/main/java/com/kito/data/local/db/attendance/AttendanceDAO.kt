package com.kito.data.local.db.attendance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDAO {
    @Upsert
    suspend fun insertAttendance(attendance: List<AttendanceEntity>)

    @Delete
    suspend fun deleteAttendance(attendanceEntity: AttendanceEntity)

    @Query("SELECT * FROM AttendanceEntity")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

}