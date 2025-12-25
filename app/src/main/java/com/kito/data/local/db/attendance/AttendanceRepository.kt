package com.kito.data.local.db.attendance

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val attendanceDAO: AttendanceDAO
) {
    suspend fun insertAttendance(attendanceEntity: List<AttendanceEntity>) {
        attendanceDAO.insertAttendance(attendanceEntity)
    }
    suspend fun deleteAttendance(attendanceEntity: AttendanceEntity) {
        attendanceDAO.deleteAttendance(attendanceEntity)
    }
    fun getAllAttendance(): Flow<List<AttendanceEntity>> {
        return attendanceDAO.getAllAttendance()
    }

    suspend fun deleteAllAttendance() {
        attendanceDAO.deleteAllAttendance()
    }
}