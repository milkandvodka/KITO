package com.kito.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createClassNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CLASS_NOTIFICATION_CHANNEL,
            "Class Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for upcoming classes"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(channel)
    }
}