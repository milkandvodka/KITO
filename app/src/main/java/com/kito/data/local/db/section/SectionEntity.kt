package com.kito.data.local.db.section

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SectionEntity(
    @PrimaryKey
    val id: Int = 0,
    val section: String = "",
    val day: String = "",
    val start_time: String = "",
    val end_time: String = "",
    val subject: String = "",
    val room: String = "",
    val batch: String = "",
)
