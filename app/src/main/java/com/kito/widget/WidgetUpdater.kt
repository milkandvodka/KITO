package com.kito.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.kito.data.local.datastore.ProtoDataStoreProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

object WidgetUpdater {

    fun nudgeRedraw(context: Context) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Widget", "Widget Redraw Triggered")
            ProtoDataStoreProvider.get(appContext).updateData { state ->
                state.copy(
                    redrawToken = System.currentTimeMillis()
                )
            }
            yield()
            val manager = GlanceAppWidgetManager(appContext)
            val glanceIds = manager.getGlanceIds(TimetableWidget::class.java)
            glanceIds.forEach { glanceId ->
                TimetableWidget().update(appContext, glanceId)
            }
        }
    }
}
