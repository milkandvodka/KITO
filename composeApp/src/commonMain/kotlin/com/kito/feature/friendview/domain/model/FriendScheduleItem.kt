package com.kito.feature.friendview.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendScheduleItem(
    val subject: String,
    val startTime: String,
    val endTime: String,
    val room: String?,
    val day: String,
    val section: String,
    val batch: String,
)

