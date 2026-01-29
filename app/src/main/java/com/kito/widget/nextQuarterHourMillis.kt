package com.kito.widget

import java.util.Calendar

fun nextQuarterHourMillis(): Long {
    val now = System.currentTimeMillis()
    val calendar = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        val minute = get(Calendar.MINUTE)
        val nextMinute = ((minute / 15) + 1) * 15

        if (nextMinute >= 60) {
            add(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 0)
        } else {
            set(Calendar.MINUTE, nextMinute)
        }
    }
    return calendar.timeInMillis
}
