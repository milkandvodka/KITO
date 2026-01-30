package com.kito.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class TimeTableAppWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TimetableWidget()
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d("Widget", "onEnable Called")
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                workerName,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<WidgetTickWorker>(
                    15, TimeUnit.MINUTES
                ).build()
            )
    }
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d("Widget", "onDisable Called")
        WorkManager.getInstance(context).cancelUniqueWork(workerName)
    }
}
