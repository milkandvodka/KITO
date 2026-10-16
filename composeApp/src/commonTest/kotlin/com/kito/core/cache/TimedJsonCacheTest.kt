package com.kito.core.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.serializer
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimedJsonCacheTest {
    private val testDispatcher = StandardTestDispatcher()
    private val tempPath = "timed_cache_test.preferences_pb".toPath()
    private lateinit var scope: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private var clock = 1_000L
    private var fetches = 0

    @BeforeTest
    fun setup() {
        scope = CoroutineScope(testDispatcher + SupervisorJob())
        dataStore = PreferenceDataStoreFactory.createWithPath(scope = scope, produceFile = { tempPath })
    }

    @AfterTest
    fun teardown() {
        scope.cancel()
        try { FileSystem.SYSTEM.delete(tempPath) } catch (_: Exception) { }
    }

    private fun cache() = TimedJsonCache(dataStore, String.serializer(), maxAgeMs = 100L, now = { clock })

    private suspend fun TimedJsonCache<String>.get(value: String, force: Boolean = false, fail: Boolean = false) =
        getOrFetch("k", force) { fetches++; if (fail) error("offline") else value }

    @Test
    fun getOrFetch_freshCache_skipsFetch() = runTest(testDispatcher) {
        val c = cache()
        assertEquals("a", c.get("a"))
        clock += 50
        assertEquals("a", c.get("b"))
        assertEquals(1, fetches)
    }

    @Test
    fun getOrFetch_expiredCache_refetches() = runTest(testDispatcher) {
        val c = cache()
        c.get("a")
        clock += 150
        assertEquals("b", c.get("b"))
        assertEquals(2, fetches)
    }

    @Test
    fun getOrFetch_forceRefresh_bypassesFreshCache() = runTest(testDispatcher) {
        val c = cache()
        c.get("a")
        assertEquals("b", c.get("b", force = true))
    }

    @Test
    fun getOrFetch_fetchFails_returnsStaleCopy() = runTest(testDispatcher) {
        val c = cache()
        c.get("a")
        clock += 150
        assertEquals("a", c.get("b", fail = true))
        // The failure did not overwrite or refresh the saved copy.
        assertEquals("a", c.get("c", fail = true))
    }

    @Test
    fun getOrFetch_fetchFailsWithNoCache_throws() = runTest(testDispatcher) {
        assertFailsWith<IllegalStateException> { cache().get("a", fail = true) }
    }
}
