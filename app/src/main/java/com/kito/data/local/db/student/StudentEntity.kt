package com.kito.data.local.db.student

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StudentEntity (
    @PrimaryKey
    val roll_no: String = "",
    val section: String = "",
    val batch: String = ""
)