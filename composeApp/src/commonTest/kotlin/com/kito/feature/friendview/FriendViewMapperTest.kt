package com.kito.feature.friendview

import com.kito.core.database.entity.SectionEntity
import com.kito.feature.friendview.data.mapper.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FriendViewMapperTest {

    private fun entity(room: String? = "101") = SectionEntity(
        id = 1,
        section = "CS-A",
        day = "Monday",
        start_time = "09:00:00",
        end_time = "10:00:00",
        subject = "Maths",
        room = room,
        batch = "B1",
    )

    @Test
    fun toDomain_mapsAllFields() {
        val domain = entity().toDomain()
        assertEquals("Maths", domain.subject)
        assertEquals("09:00:00", domain.startTime)
        assertEquals("10:00:00", domain.endTime)
        assertEquals("101", domain.room)
        assertEquals("MON", domain.day)
        assertEquals("CS-A", domain.section)
        assertEquals("B1", domain.batch)
    }

    @Test
    fun toDomain_nullRoom_mapsToNull() {
        assertNull(entity(room = null).toDomain().room)
    }
}
