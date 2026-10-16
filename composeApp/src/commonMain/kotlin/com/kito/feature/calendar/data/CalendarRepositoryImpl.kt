package com.kito.feature.calendar.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kito.core.cache.TimedJsonCache
import com.kito.core.network.supabase.model.CalendarEventModel
import com.kito.feature.calendar.data.mapper.toDomain
import com.kito.feature.calendar.domain.model.CalendarEvent
import com.kito.feature.calendar.domain.repository.CalendarRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.builtins.ListSerializer

import org.koin.core.annotation.Provided

class CalendarRepositoryImpl(
    @Provided private val client: HttpClient,
    @Provided dataStore: DataStore<Preferences>,
) : CalendarRepository {
    private val cache = TimedJsonCache(dataStore, ListSerializer(CalendarEventModel.serializer()))

    override suspend fun getEventsByMonth(year: Int, month: Int, forceRefresh: Boolean): List<CalendarEvent> {
        val (nextYear, nextMonth) = if (month == 12) year + 1 to 1 else year to month + 1
        val models = cache.getOrFetch("calendar_events_v1_${year}_$month", forceRefresh) {
            client.get("rest/v1/calendar_events") {
                parameter("date", "gte.${isoDate(year, month)}")
                parameter("date", "lt.${isoDate(nextYear, nextMonth)}")
                parameter("is_active", "eq.true")
                parameter("order", "date.asc,start_time.asc")
                parameter("select", "*")
            }.body<List<CalendarEventModel>>()
        }
        return models.map { it.toDomain() }
    }

    private fun isoDate(year: Int, month: Int) = "$year-${month.toString().padStart(2, '0')}-01"
}
