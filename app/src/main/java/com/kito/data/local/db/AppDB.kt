package com.kito.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kito.data.local.db.attendance.AttendanceDAO
import com.kito.data.local.db.attendance.AttendanceEntity
import com.kito.data.local.db.section.SectionDAO
import com.kito.data.local.db.section.SectionEntity
import com.kito.data.local.db.student.StudentDAO
import com.kito.data.local.db.student.StudentEntity
import com.kito.data.local.db.studentsection.StudentSectionDAO

@Database(
    entities = [AttendanceEntity::class, StudentEntity::class, SectionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB: RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDAO
    abstract fun studentDao(): StudentDAO
    abstract fun sectionDao(): SectionDAO
    abstract fun studentSectionDao(): StudentSectionDAO
}