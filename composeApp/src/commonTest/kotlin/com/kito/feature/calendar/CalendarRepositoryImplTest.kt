package com.kito.feature.calendar

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.kito.feature.calendar.data.CalendarRepositoryImpl
import com.kito.testing.mockHttpClient
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CalendarRepositoryImplTest {
    private val testDispatcher = StandardTestDispatcher()
    private val tempPath = "calendar_repo_test.preferences_pb".toPath()
    private lateinit var scope: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private val requests = mutableListOf<Url>()

    private val body = """[{"id":7,"title":"Fest","date":"2026-02-14","start_time":"10:00:00","category":"event"}]"""

    private fun repo() = CalendarRepositoryImpl(
        mockHttpClient { request ->
            requests += request.url
            respond(body, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        },
        dataStore,
    )

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

    @Test
    fun getEventsByMonth_february_usesValidExclusiveEndDate() = runTest(testDispatcher) {
        val events = repo().getEventsByMonth(2026, 2)
        assertEquals(listOf("gte.2026-02-01", "lt.2026-03-01"), requests.single().parameters.getAll("date"))
        assertEquals("Fest", events.single().title)
    }

    @Test
    fun getEventsByMonth_december_rollsOverYear() = runTest(testDispatcher) {
        repo().getEventsByMonth(2026, 12)
        assertEquals(listOf("gte.2026-12-01", "lt.2027-01-01"), requests.single().parameters.getAll("date"))
    }

    @Test
    fun getEventsByMonth_secondCall_servedFromCache() = runTest(testDispatcher) {
        val repo = repo()
        repo.getEventsByMonth(2026, 2)
        val cached = repo.getEventsByMonth(2026, 2)
        assertEquals(1, requests.size)
        assertEquals(7L, cached.single().id)
    }

    @Test
    fun getEventsByMonth_forceRefresh_hitsNetwork() = runTest(testDispatcher) {
        val repo = repo()
        repo.getEventsByMonth(2026, 2)
        repo.getEventsByMonth(2026, 2, forceRefresh = true)
        assertEquals(2, requests.size)
    }

    @Test
    fun getEventsByMonth_differentMonths_cachedSeparately() = runTest(testDispatcher) {
        val repo = repo()
        repo.getEventsByMonth(2026, 2)
        repo.getEventsByMonth(2026, 3)
        assertEquals(2, requests.size)
    }
}
