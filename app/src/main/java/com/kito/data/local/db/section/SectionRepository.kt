package com.kito.data.local.db.section

import javax.inject.Inject

class SectionRepository @Inject constructor(
    private val sectionDAO: SectionDAO
) {

    suspend fun insertSection(sectionEntity: List<SectionEntity>) {
        sectionDAO.insertSection(sectionEntity)
    }

    suspend fun deleteSection(sectionEntity: SectionEntity) {
        sectionDAO.deleteSection(sectionEntity)
    }

}