package com.kito.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kito.notification.NotificationPipelineController
import com.kito.widget.WidgetUpdater.nudgeRedraw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SystemTimeChangeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Widget", "System Time Changed")
        nudgeRedraw(context)
        val pendingResult = goAsync()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                NotificationPipelineController.get(context).sync()
            }
        }finally {
            pendingResult.finish()
        }
    }
}