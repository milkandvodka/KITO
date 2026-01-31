package com.kito.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.kito.data.local.datastore.ProtoDataStoreProvider
import com.kito.di.dataStore
import kotlinx.coroutines.flow.first

class NotificationPipelineController private constructor(
    private val context: Context
) {

    private val scheduler = ClassNotificationScheduler(context)

    suspend fun sync() {
        val userEnabled = readUserPreference()
        val systemAllowed = context.areNotificationsAllowed()

        if (userEnabled && systemAllowed) {
            scheduleNext()
        } else {
            if (userEnabled && !systemAllowed) {
                context.dataStore.edit { preferences ->
                    preferences[KEY_NOTIFICATIONS_ENABLED] = false
                }
            }
            scheduler.cancelAll()
        }
    }

    private suspend fun readUserPreference(): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_NOTIFICATIONS_ENABLED] ?: false
    }

    private suspend fun scheduleNext() {
        val data = ProtoDataStoreProvider
            .get(context)
            .data
            .first()

        val (_, nextStart) = computeNextClass(data.list)
        nextStart?.let {
            scheduler.scheduleClass(it)
        }
    }

    companion object {
        private val KEY_NOTIFICATIONS_ENABLED =
            booleanPreferencesKey("notifications_enabled")

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: NotificationPipelineController? = null

        fun get(context: Context): NotificationPipelineController {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotificationPipelineController(
                    context.applicationContext
                ).also { INSTANCE = it }
            }
        }
    }
}
