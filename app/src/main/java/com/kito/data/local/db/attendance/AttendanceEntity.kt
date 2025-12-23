package com.kito.data.local.db.attendance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AttendanceEntity(
    @PrimaryKey
    val subjectCode: String = "",
    val subjectName: String = "",
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val percentage: Double = 0.0,
    val facultyName: String = "",
    val year: String = "",
    val term: String = ""
)
