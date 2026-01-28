package com.kito

import android.app.Application
//import com.kito.widget.scheduleDailyWidgetUpdate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KitoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
//        scheduleDailyWidgetUpdate(this)
    }
}