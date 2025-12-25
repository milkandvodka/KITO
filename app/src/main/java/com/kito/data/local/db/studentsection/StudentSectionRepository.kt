package com.kito.data.local.db.studentsection

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentSectionRepository @Inject constructor(
    private val studentSectionDAO: StudentSectionDAO
) {
    fun getScheduleForStudent(rollNo: String, day: String): Flow<List<StudentSectionEntity>> {
        return studentSectionDAO.getScheduleForStudent(rollNo, day)
    }
}