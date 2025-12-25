package com.kito.data.local.db.student

import javax.inject.Inject

class StudentRepository @Inject constructor(
    private val studentDAO: StudentDAO
) {

    suspend fun insertStudent(studentEntity: List<StudentEntity>) {
        studentDAO.insertStudent(studentEntity)
    }

    suspend fun deleteStudent(studentEntity: StudentEntity) {
        studentDAO.deleteStudent(studentEntity)
    }

    suspend fun getStudentByRoll(rollNo: String): StudentEntity {
        return studentDAO.getStudentByRoll(rollNo)
    }

}