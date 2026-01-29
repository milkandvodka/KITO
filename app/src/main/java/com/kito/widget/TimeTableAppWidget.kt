package com.kito.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import com.kito.data.local.datastore.ProtoDataStoreProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimeTableAppWidget : GlanceAppWidgetReceiver() {

    override val glanceAppWidget = TimetableWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleWidgetAlarm(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        scheduleWidgetAlarm(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelWidgetAlarm(context)
    }
}
