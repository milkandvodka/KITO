package com.kito.feature.friendview.data.mapper

import com.kito.core.database.entity.SectionEntity
import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.schedule.presentation.components.normalizeDay

fun SectionEntity.toDomain(): FriendScheduleItem = FriendScheduleItem(
    subject = subject,
    startTime = start_time,
    endTime = end_time,
    room = room,
    day = normalizeDay(day),
    section = section,
    batch = batch,
)

fun List<FriendScheduleItem>.mergeDuplicateFriendClasses(): List<FriendScheduleItem> {
    return this.groupBy {
        listOf(
            it.subject.trim().lowercase(),
            it.day.trim().lowercase(),
            it.startTime.trim(),
            it.endTime.trim()
        )
    }.values.map { duplicates ->
        if (duplicates.size == 1) {
            duplicates.first()
        } else {
            val first = duplicates.first()
            first.copy(
                room = com.kito.feature.schedule.data.mapper.mergeRooms(duplicates.map { it.room })
            )
        }
    }
}
