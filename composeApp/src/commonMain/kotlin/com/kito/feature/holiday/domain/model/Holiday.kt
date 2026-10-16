package com.kito.feature.holiday.domain.model

data class Holiday(
    val name: String,
    val startDate: String,
    val endDate: String = startDate,
    val startDay: String,
    val endDay: String = startDay,
    val numberOfDays: Int,
    val month: String
)
