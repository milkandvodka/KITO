package com.kito.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

private const val CLASS_ALARM_REQUEST_CODE = 1001

class ClassNotificationScheduler(
    private val context: Context
) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(triggerAtMillis: Long) {

        val notifyAt = (triggerAtMillis - 10 * 60 * 1000)
            .coerceAtLeast(System.currentTimeMillis() + 5_000)

        val intent = Intent(context, ClassAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            CLASS_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notifyAt,
            pendingIntent
        )
    }

    fun cancel() {
        val intent = Intent(context, ClassAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            CLASS_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}