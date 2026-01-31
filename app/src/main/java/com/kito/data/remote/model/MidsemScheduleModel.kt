package com.kito.data.remote.model

import androidx.annotation.Keep

@Keep
data class MidsemScheduleModel(
    val batch: String,
    val branch: String,
    val date: String,
    val day: String,
    val end_time: String,
    val semester: Int,
    val start_time: String,
    val subject: String,
    val subject_code: String
)