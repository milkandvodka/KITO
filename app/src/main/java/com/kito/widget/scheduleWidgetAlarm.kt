package com.kito.widget

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import com.kito.data.local.datastore.ProtoDataStoreProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java

fun scheduleWidgetAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WidgetUpdateReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
    alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        nextQuarterHourMillis(),
        pendingIntent
    )
}
class WidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val store = ProtoDataStoreProvider.get(context)
                store.updateData {
                    it.copy(lastUpdated = System.currentTimeMillis())
                }
                TimetableWidget().updateAll(context)
                scheduleWidgetAlarm(context)
            } finally {
                pending.finish()
            }
        }
    }
}
class SystemTimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        scheduleWidgetAlarm(context)
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Safe to call suspend function here
                TimetableWidget().updateAll(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
fun cancelWidgetAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WidgetUpdateReceiver::class.java)
    val pi = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pi)
}