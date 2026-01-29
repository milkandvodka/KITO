package com.kito.notification

import com.kito.data.local.datastore.StudentSectionDatastore
import com.kito.data.local.db.studentsection.StudentSectionEntity
import java.util.Calendar

fun computeNextClass(
    sections: List<StudentSectionEntity>
): Pair<StudentSectionDatastore?, Long?> {

    val now = System.currentTimeMillis()

    val next = sections
        .map { it to classStartMillis(it) }
        .filter { (_, startMillis) -> startMillis > now }
        .minByOrNull { it.second }

    return next?.let { (entity, millis) ->
        entity.toProto() to millis
    } ?: (null to null)
}

fun classStartMillis(section: StudentSectionEntity): Long {
    val now = Calendar.getInstance()

    val classCal = Calendar.getInstance().apply {
        // Set hour & minute
        val (hour, minute) = section.startTime
            .split(":")
            .take(2)
            .map { it.trim().toInt() }

        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // Map MON/TUE/... to Calendar.DAY_OF_WEEK
        val targetDay = when (section.day) {
            "MON" -> Calendar.MONDAY
            "TUE" -> Calendar.TUESDAY
            "WED" -> Calendar.WEDNESDAY
            "THU" -> Calendar.THURSDAY
            "FRI" -> Calendar.FRIDAY
            "SAT" -> Calendar.SATURDAY
            else -> Calendar.SUNDAY
        }

        set(Calendar.DAY_OF_WEEK, targetDay)

        // If this time already passed, jump to next week
        if (timeInMillis <= now.timeInMillis) {
            add(Calendar.WEEK_OF_YEAR, 1)
        }
    }

    return classCal.timeInMillis
}
fun StudentSectionEntity.toProto(): StudentSectionDatastore {
    return StudentSectionDatastore(
        sectionId = sectionId,
        rollNo = rollNo,
        section = section,
        batch = batch,
        day = day,
        startTime = startTime,
        endTime = endTime,
        subject = subject,
        room = room
    )
}