package com.kito.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeTableAppWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TimetableWidget()
}
