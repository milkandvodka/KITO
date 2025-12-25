package com.kito.data.local.db.attendance

import androidx.room.Entity

@Entity(
    primaryKeys = ["subjectName", "year", "term"]
)
data class AttendanceEntity(
    val subjectCode: String,
    val subjectName: String,
    val attendedClasses: Int,
    val totalClasses: Int,
    val percentage: Double,
    val facultyName: String,
    val year: String,
    val term: String
)

