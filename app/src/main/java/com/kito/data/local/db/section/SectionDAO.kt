package com.kito.data.local.db.section

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

@Dao
interface SectionDAO {

    @Upsert
    suspend fun insertSection(sectionEntity: List<SectionEntity>)

    @Delete
    suspend fun deleteSection(sectionEntity: SectionEntity)
}