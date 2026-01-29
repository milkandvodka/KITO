package com.kito.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kito.R


const val CLASS_NOTIFICATION_CHANNEL = "class_notification_channel"
const val CLASS_NOTIFICATION_ID = 1001
class ClassAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return // Permission not granted → silently ignore
        }

        val notification = NotificationCompat.Builder(
            context,
            CLASS_NOTIFICATION_CHANNEL
        )
            .setSmallIcon(R.drawable.custom_icon)
            .setContentTitle("Upcoming Class")
            .setContentText("Your next class is about to start")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(CLASS_NOTIFICATION_ID, notification)
    }
}