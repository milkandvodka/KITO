package com.kito.feature.schedule.data.mapper

import com.kito.core.database.entity.StudentSectionEntity
import com.kito.feature.schedule.domain.model.ScheduleItem

fun StudentSectionEntity.toDomain(): ScheduleItem = ScheduleItem(
    subject = subject,
    startTime = startTime,
    endTime = endTime,
    room = room,
    section = section,
    batch = batch,
)

fun mergeRooms(rooms: List<String?>): String? {
    val nonBlankRooms = rooms.filterNotNull().map { it.trim() }.filter { it.isNotBlank() }.distinct()
    if (nonBlankRooms.isEmpty()) return null
    if (nonBlankRooms.size == 1) return nonBlankRooms.first()

    // Check if all share a common prefix like "Campus-3, C12"
    val hasCommonComma = nonBlankRooms.all { it.contains(",") }
    if (hasCommonComma) {
        val prefixes = nonBlankRooms.map { it.substringBefore(",").trim() }.distinct()
        if (prefixes.size == 1) {
            val prefix = prefixes.first()
            val roomCodes = nonBlankRooms.map { it.substringAfter(",").trim() }.distinct()
            return "$prefix, ${roomCodes.joinToString("/")}"
        }
    }

    return nonBlankRooms.joinToString("/")
}

fun List<ScheduleItem>.mergeDuplicateClasses(): List<ScheduleItem> {
    return this.groupBy {
        Triple(
            it.subject.trim().lowercase(),
            it.startTime.trim(),
            it.endTime.trim()
        )
    }.values.map { duplicates ->
        if (duplicates.size == 1) {
            duplicates.first()
        } else {
            val first = duplicates.first()
            first.copy(
                room = mergeRooms(duplicates.map { it.room })
            )
        }
    }
}
