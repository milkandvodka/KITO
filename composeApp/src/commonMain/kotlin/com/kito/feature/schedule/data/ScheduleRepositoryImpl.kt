package com.kito.feature.schedule.data

import com.kito.core.database.repository.SectionRepository
import com.kito.core.database.repository.StudentSectionRepository
import com.kito.feature.schedule.data.mapper.mergeDuplicateClasses
import com.kito.feature.schedule.data.mapper.toDomain
import com.kito.feature.schedule.domain.model.ScheduleItem
import com.kito.feature.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepositoryImpl(
    private val studentSectionRepository: StudentSectionRepository,
    private val sectionRepository: SectionRepository,
) : ScheduleRepository {
    override fun getScheduleForDay(rollNo: String, day: String): Flow<List<ScheduleItem>> =
        studentSectionRepository.getScheduleForStudent(rollNo, day).map { list ->
            list.map { it.toDomain() }.mergeDuplicateClasses()
        }

    override fun getAllSchedule(rollNo: String): Flow<List<ScheduleItem>> =
        studentSectionRepository.getAllScheduleForStudent(rollNo).map { list ->
            list.map { it.toDomain() }.mergeDuplicateClasses()
        }

    override suspend fun deleteAllSections() {
        sectionRepository.deleteAllSection()
    }
}
