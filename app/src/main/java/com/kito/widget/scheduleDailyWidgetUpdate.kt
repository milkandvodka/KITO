//package com.kito.widget
//
//import android.content.Context
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.PeriodicWorkRequestBuilder
//import androidx.work.WorkManager
//import java.util.Calendar
//import java.util.concurrent.TimeUnit
//
//private const val WIDGET_WORK_NAME = "DailyTimetableWidgetUpdate"
//
//fun scheduleDailyWidgetUpdate(context: Context) {
//
//    val now = Calendar.getInstance()
//
//    // 🕒 Schedule next run at 12:15 AM
//    val next12_15AM = Calendar.getInstance().apply {
//        set(Calendar.HOUR_OF_DAY, 0)
//        set(Calendar.MINUTE, 15)
//        set(Calendar.SECOND, 0)
//        set(Calendar.MILLISECOND, 0)
//
//        // If it's already past 12:15 AM today → schedule tomorrow
//        if (before(now)) {
//            add(Calendar.DAY_OF_YEAR, 1)
//        }
//    }
//
//    val initialDelayMillis = next12_15AM.timeInMillis - now.timeInMillis
//
//    val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
//        1, TimeUnit.DAYS
//    )
//        .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
//        .build()
//
//    WorkManager.getInstance(context)
//        .enqueueUniquePeriodicWork(
//            WIDGET_WORK_NAME,
//            ExistingPeriodicWorkPolicy.UPDATE,
//            request
//        )
//}
//
//fun cancelDailyWidgetUpdate(context: Context) {
//    WorkManager.getInstance(context)
//        .cancelUniqueWork(WIDGET_WORK_NAME)
//}