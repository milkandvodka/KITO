package com.kito.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat

fun Context.areNotificationsAllowed(): Boolean {
    val manager = NotificationManagerCompat.from(this)
    val notificationsEnabled = manager.areNotificationsEnabled()

    val alarmsEnabled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }

    return notificationsEnabled && alarmsEnabled
}