package com.kito.widget

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kito.widget.WidgetUpdater.nudgeRedraw


const val workerName = "timetable_widget_tick"
class WidgetTickWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        Log.d("Widget", "WidgetWorker Tick")
        nudgeRedraw(applicationContext)
        return Result.success()
    }
}