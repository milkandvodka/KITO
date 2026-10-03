package com.kito.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.kito.core.database.entity.StudentSectionEntity
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Provided

@Provided
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
        JOIN ActiveSessionEntity act
            ON sec.academic_year = act.academic_year
           AND sec.term_code = act.term_code
           AND sec.version = act.version
        WHERE stu.roll_no = :rollNo
          AND (UPPER(TRIM(sec.day)) = UPPER(TRIM(:day)) OR UPPER(SUBSTR(TRIM(sec.day), 1, 3)) = UPPER(SUBSTR(TRIM(:day), 1, 3)))
          AND sec.source = 'core'

        UNION

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
        JOIN StudentElectiveEntity selu ON (sec.section = selu.elective_1 OR sec.section = selu.elective_2)
        JOIN StudentEntity stu ON selu.roll_no = stu.roll_no
        JOIN ActiveSessionEntity act
            ON sec.academic_year = act.academic_year
           AND sec.term_code = act.term_code
           AND sec.version = act.version
        WHERE stu.roll_no = :rollNo
          AND (UPPER(TRIM(sec.day)) = UPPER(TRIM(:day)) OR UPPER(SUBSTR(TRIM(sec.day), 1, 3)) = UPPER(SUBSTR(TRIM(:day), 1, 3)))

        ORDER BY startTime
    """)
    fun getScheduleForStudent(
        rollNo: String,
        day: String
    ): Flow<List<StudentSectionEntity>>

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
        JOIN ActiveSessionEntity act
            ON sec.academic_year = act.academic_year
           AND sec.term_code = act.term_code
           AND sec.version = act.version
        WHERE stu.roll_no = :rollNo
          AND sec.source = 'core'

        UNION

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
        JOIN StudentElectiveEntity selu ON (sec.section = selu.elective_1 OR sec.section = selu.elective_2)
        JOIN StudentEntity stu ON selu.roll_no = stu.roll_no
        JOIN ActiveSessionEntity act
            ON sec.academic_year = act.academic_year
           AND sec.term_code = act.term_code
           AND sec.version = act.version
        WHERE stu.roll_no = :rollNo

        ORDER BY startTime
    """)
    fun getAllScheduleForStudent(
        rollNo: String
    ): Flow<List<StudentSectionEntity>>
}
