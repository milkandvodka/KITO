package com.kito.data.local.db.attendance

import com.kito.sap.SubjectAttendance

fun SubjectAttendance.toAttendanceEntity(
    year: String,
    term: String
): AttendanceEntity {
    return AttendanceEntity(
        subjectCode = subjectCode,
        subjectName = subjectName,
        attendedClasses = attendedClasses,
        totalClasses = totalClasses,
        percentage = percentage,
        facultyName = facultyName,
        year = year,
        term = term
    )
}