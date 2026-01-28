package com.kito.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimeTableAppWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TimetableWidget()
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when(intent.action){
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_DATE_CHANGED->{
                CoroutineScope(Dispatchers.IO).launch {
                    glanceAppWidget.updateAll(context)
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // First widget added
//        scheduleDailyWidgetUpdate(context)
    }
    override fun onDeleted(context: Context, widgetIds: IntArray) {
        super.onDeleted(context, widgetIds)
        // Optional: cancel work when last widget removed
//        cancelDailyWidgetUpdate(context)
    }
}
