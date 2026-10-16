package com.kito.feature.calendar.domain.repository

import com.kito.feature.calendar.domain.model.CalendarEvent

/**
 * Domain boundary for calendar data. The implementation maps [CalendarEventModel] DTOs to
 * pure [CalendarEvent] domain objects. Presentation never sees the DTO.
 */
interface CalendarRepository {
    /** Cached per month; [forceRefresh] bypasses the cache. Falls back to stale data offline. */
    suspend fun getEventsByMonth(year: Int, month: Int, forceRefresh: Boolean = false): List<CalendarEvent>
}
