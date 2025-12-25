package com.kito.sap

import javax.inject.Inject

class SapRepository @Inject constructor() {
    private val sapClient = SapPortalClient()

    suspend fun login(
        username: String,
        password: String,
        academicYear: String,
        termCode: String,
    ): AttendanceResult {

        return try {
            val result = sapClient.fetchAttendance(
                username,
                password,
                academicYear,
                termCode
            )
            result
        } catch (e: Exception) {
            AttendanceResult.Error(e.message ?: "Unknown error")
        }
    }
}