package com.kito.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

//class WidgetUpdateWorker(
//    context: Context,
//    params: WorkerParameters
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        Log.d(
//            "WidgetWorker",
//            "WorkManager fired}"
//        )
//        TimetableWidget().updateAll(applicationContext)
//        Log.d(
//            "WidgetWorker",
//            "Widget updateAll() called successfully"
//        )
//        return Result.success()
//    }
//}