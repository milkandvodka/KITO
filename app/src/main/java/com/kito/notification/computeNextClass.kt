package com.kito.notification

import com.kito.data.local.datastore.StudentSectionDatastore
import java.util.Calendar

fun computeNextClass(
    sections: List<StudentSectionDatastore>
): Pair<StudentSectionDatastore?, Long?> {
    val now = System.currentTimeMillis()
    val next = sections
        .map { it to classStartMillis(it) }
        .filter { (_, startMillis) -> startMillis > now }
        .minByOrNull { it.second }
    return next?.let { (entity, millis) ->
        entity to millis
    } ?: (null to null)
}

fun classStartMillis(section: StudentSectionDatastore): Long {
    val now = Calendar.getInstance()
    val classCal = Calendar.getInstance().apply {
        val (hour, minute) = section.startTime
            .split(":")
            .take(2)
            .map { it.trim().toInt() }
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
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
        if (timeInMillis <= now.timeInMillis) {
            add(Calendar.WEEK_OF_YEAR, 1)
        }
    }

    return classCal.timeInMillis
}