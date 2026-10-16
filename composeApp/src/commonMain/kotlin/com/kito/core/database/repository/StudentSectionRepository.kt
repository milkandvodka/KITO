package com.kito.core.database.repository

import com.kito.core.database.dao.StudentSectionDAO
import com.kito.core.database.entity.StudentSectionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import com.kito.kaya.KayaTimetableStore
class StudentSectionRepository(
    private val studentSectionDao: StudentSectionDAO,
    private val kayaStore: KayaTimetableStore,
) {
    fun getScheduleForStudent(rollNo: String, day: String): Flow<List<StudentSectionEntity>> =
        getAllScheduleForStudent(rollNo).map { rows -> rows.filter { it.day == day } }

    // KAYA check and the Room read run in parallel, so the saved timetable isn't held behind a DataStore hop.
    fun getAllScheduleForStudent(rollNo: String): Flow<List<StudentSectionEntity>> =
        combine(
            kayaStore.observe(rollNo).distinctUntilChanged(),
            studentSectionDao.getAllScheduleForStudent(rollNo),
        ) { kaya, room ->
            kaya ?: room
        }.distinctUntilChanged()
}
