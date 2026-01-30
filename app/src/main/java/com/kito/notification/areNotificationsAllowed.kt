package com.kito.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat

fun Context.areNotificationsAllowed(): Boolean {
    val manager = NotificationManagerCompat.from(this)
    return manager.areNotificationsEnabled()
}