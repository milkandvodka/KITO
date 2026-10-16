package com.kito.core.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Clock

/**
 * Read-through JSON cache in DataStore: serves a fresh copy, otherwise fetches; if the
 * fetch fails, falls back to the stale copy. Failures never overwrite saved data.
 */
class TimedJsonCache<T>(
    private val dataStore: DataStore<Preferences>,
    private val serializer: KSerializer<T>,
    private val maxAgeMs: Long = DEFAULT_MAX_AGE_MS,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getOrFetch(key: String, forceRefresh: Boolean = false, fetch: suspend () -> T): T {
        val prefKey = stringPreferencesKey(key)
        val cached = dataStore.data.first()[prefKey]?.let { raw ->
            runCatching { json.decodeFromString(CacheEntry.serializer(serializer), raw) }.getOrNull()
        }
        val time = now()
        if (!forceRefresh && cached != null && time - cached.savedAt in 0 until maxAgeMs) return cached.value

        val fresh = try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return cached?.value ?: throw e
        }
        dataStore.edit {
            it[prefKey] = json.encodeToString(CacheEntry.serializer(serializer), CacheEntry(time, fresh))
        }
        return fresh
    }

    companion object {
        const val DEFAULT_MAX_AGE_MS = 6 * 60 * 60 * 1000L
    }
}
