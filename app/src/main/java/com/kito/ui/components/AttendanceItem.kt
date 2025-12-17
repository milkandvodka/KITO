package com.kito.ui.components

data class AttendanceItem(
    val title: String,
    val present: Int,
    val total: Int,
    val faculty: String
) {
    val percentage: Float
        get() = if (total == 0) 0f else (present.toFloat() / total.toFloat()) * 100f
}