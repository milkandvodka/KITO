package com.kito.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.kito.MainActivity
import com.kito.R
import com.kito.data.local.datastore.ProtoDataStoreProvider
import com.kito.data.local.datastore.StudentSectionDatastore
import com.kito.ui.components.formatTo12Hour
import com.kito.widget.WidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar


const val CLASS_NOTIFICATION_CHANNEL = "class_notification_channel"
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val type = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE)
            ?.let { ClassNotificationType.valueOf(it) }
            ?: return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val state = ProtoDataStoreProvider.get(context).data.first()

                val now = System.currentTimeMillis()
                val today = todayKey()

                val todayClasses = state.list
                    .filter { it.day == today && it.rollNo == state.rollNo }
                    .sortedBy { it.startMillisToday() }

                val targetClass = when (type) {
                    ClassNotificationType.UPCOMING ->
                        todayClasses.firstOrNull { it.startMillisToday() > now }

                    ClassNotificationType.ONGOING ->
                        todayClasses.firstOrNull {
                            now in it.startMillisToday() until it.endMillisToday()
                        }
                } ?: return@launch

                showNotification(context, type, targetClass)

                when (type) {
                    ClassNotificationType.UPCOMING -> {
                    }

                    ClassNotificationType.ONGOING -> {
                        WidgetUpdater.nudgeRedraw(context)
                        rescheduleNext(context)
                    }
                }
            }finally {
                pendingResult.finish()
            }
        }
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(
        context: Context,
        type: ClassNotificationType,
        cls: StudentSectionDatastore
    ) {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "kito://schedule".toUri(),
            context,
            MainActivity::class.java // your launcher activity
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val title = when (type) {
            ClassNotificationType.UPCOMING -> "Upcoming Class"
            ClassNotificationType.ONGOING -> "Class Started"
        }

        val time = "${formatTo12Hour(cls.startTime)} - ${
            formatTo12Hour(
                cls.endTime
            )
        }"
        val room = cls.room?.let { " • Room $it" } ?: ""

        val text = "${cls.subject}$room\n$time"

        val id = "${cls.subject}_${type.name}".hashCode()

        val notification = NotificationCompat.Builder(
            context,
            CLASS_NOTIFICATION_CHANNEL
        )
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }


    private suspend fun rescheduleNext(context: Context) {
        NotificationPipelineController
            .get(context)
            .sync()
    }

    private fun StudentSectionDatastore.startMillisToday(): Long =
        timeToMillisToday(startTime)

    private fun StudentSectionDatastore.endMillisToday(): Long =
        timeToMillisToday(endTime)

    private fun timeToMillisToday(time: String): Long {
        val (h, m) = time.split(":").map { it.trim().toInt() }
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun todayKey(): String =
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            else -> "SUN"
        }
}

