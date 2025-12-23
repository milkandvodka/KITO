package com.kito.data.local.db.studentsection

data class StudentSectionEntity(
    val sectionId: Int,
    val rollNo: String,
    val section: String,
    val batch: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val room: String
)
