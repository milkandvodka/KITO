package com.kito.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class ClassNotificationScheduler(
    private val context: Context
) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleClass(startTimeMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) return
        }
        scheduleAlarm(
            triggerAtMillis = startTimeMillis - 10 * 60 * 1000,
            requestCode = UPCOMING_CLASS_NOTIFICATION_ID,
            type = ClassNotificationType.UPCOMING
        )
        scheduleAlarm(
            triggerAtMillis = startTimeMillis,
            requestCode = ONGOING_CLASS_NOTIFICATION_ID,
            type = ClassNotificationType.ONGOING
        )
    }

    private fun scheduleAlarm(
        triggerAtMillis: Long,
        requestCode: Int,
        type: ClassNotificationType
    ) {
        val notifyAt = triggerAtMillis
            .coerceAtLeast(System.currentTimeMillis() + 5_000)
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.kito.notification.ACTION_${type.name}"
            putExtra(EXTRA_NOTIFICATION_TYPE, type.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notifyAt,
            pendingIntent
        )
    }
    fun cancelAll() {
        cancel(UPCOMING_CLASS_NOTIFICATION_ID)
        cancel(ONGOING_CLASS_NOTIFICATION_ID)
    }
    private fun cancel(requestCode: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}