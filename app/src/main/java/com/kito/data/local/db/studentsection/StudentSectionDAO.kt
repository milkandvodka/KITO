package com.kito.data.local.db.studentsection

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSectionDAO {
    @Query("""
        SELECT 
            sec.id AS sectionId,
            stu.roll_no AS rollNo,
            sec.section AS section,
            sec.batch AS batch,
            sec.day AS day,
            sec.start_time AS startTime,
            sec.end_time AS endTime,
            sec.subject AS subject,
            sec.room AS room
        FROM SectionEntity sec
        JOIN StudentEntity stu
            ON sec.section = stu.section
           AND sec.batch = stu.batch
        WHERE stu.roll_no = :rollNo
          AND sec.day = :day
        ORDER BY sec.start_time
    """)
    fun getScheduleForStudent(
        rollNo: String,
        day: String
    ): Flow<List<StudentSectionEntity>>
}