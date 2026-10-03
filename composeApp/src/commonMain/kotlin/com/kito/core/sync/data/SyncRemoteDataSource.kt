package com.kito.core.sync.data

import com.kito.core.database.entity.SectionEntity
import com.kito.core.database.entity.StudentEntity
import com.kito.core.network.supabase.model.TimetableMetadataDto
import com.kito.core.network.supabase.request.MissingRollReportRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

import org.koin.core.annotation.Provided

import io.ktor.client.request.header

class SyncRemoteDataSource(
    @Provided private val client: HttpClient
) {
    suspend fun getStudentByRoll(rollNo: String): StudentEntity? {
        return client.get("rest/v1/students") {
            expectSuccess = true
            parameter("roll_no", "eq.$rollNo")
            parameter("select", "*")
        }.body<List<StudentEntity>>().firstOrNull()
    }

    suspend fun getActiveSessionConfig(): ActiveSessionConfig {
        val result: List<ActiveSessionConfig> = client.get("rest/v1/active_session") {
            expectSuccess = true
            parameter("select", "*")
        }.body()

        if (result.isEmpty()) {
            throw IllegalStateException("Active session config not found in Supabase")
        }

        return result.first()
    }

    suspend fun getTimetableForStudent(
        section: String,
        batch: String,
    ): List<SectionEntity> {
        return client.get("rest/v1/timetable") {
            expectSuccess = true
            parameter("section", "eq.$section")
            if (batch.isNotBlank()) {
                parameter("batch", "eq.$batch")
            }
            parameter("select", "*")
        }.body()
    }

    suspend fun getStudentElective(rollNo: String): StudentElectiveConfig? {
        val result: List<StudentElectiveConfig> = client.get("rest/v1/student_elective") {
            expectSuccess = true
            parameter("roll_no", "eq.$rollNo")
            parameter("select", "*")
        }.body()
        return result.firstOrNull()
    }

    suspend fun getAllStudentSections(): List<TimetableMetadataDto> {
        val result = mutableListOf<TimetableMetadataDto>()
        val pageSize = 1000
        var offset = 0
        val maxPages = 50

        while (offset < maxPages * pageSize) {
            val from = offset
            val to = offset + pageSize - 1
            val page = runCatching {
                val response = client.get("rest/v1/students") {
                    parameter("select", "batch,section")
                    parameter("limit", pageSize)
                    parameter("offset", offset)
                    header("Range", "$from-$to")
                }
                if (response.status.value in 200..299) {
                    response.body<List<TimetableMetadataDto>>()
                } else {
                    emptyList()
                }
            }.getOrDefault(emptyList())

            if (page.isEmpty()) break
            result.addAll(page)
            if (page.size < pageSize) break
            offset += pageSize
        }
        return result
    }

    suspend fun getTimetableMetadata(): List<TimetableMetadataDto> {
        val result = mutableListOf<TimetableMetadataDto>()
        val pageSize = 1000
        var offset = 0
        val maxPages = 50

        while (offset < maxPages * pageSize) {
            val from = offset
            val to = offset + pageSize - 1
            val page = runCatching {
                val response = client.get("rest/v1/timetable") {
                    parameter("select", "batch,section")
                    parameter("order", "id.asc")
                    parameter("limit", pageSize)
                    parameter("offset", offset)
                    header("Range", "$from-$to")
                }
                if (response.status.value in 200..299) {
                    response.body<List<TimetableMetadataDto>>()
                } else {
                    emptyList()
                }
            }.getOrDefault(emptyList())

            if (page.isEmpty()) break
            result.addAll(page)
            if (page.size < pageSize) break
            offset += pageSize
        }

        val studentRows = getAllStudentSections()
        return (result + studentRows).distinct()
    }

    suspend fun getAllStudentElectives(): List<StudentElectiveConfig> {
        val result = mutableListOf<StudentElectiveConfig>()
        val pageSize = 1000
        var offset = 0
        val maxPages = 50

        while (offset < maxPages * pageSize) {
            val from = offset
            val to = offset + pageSize - 1
            val page = runCatching {
                val response = client.get("rest/v1/student_elective") {
                    parameter("select", "*")
                    parameter("limit", pageSize)
                    parameter("offset", offset)
                    header("Range", "$from-$to")
                }
                if (response.status.value in 200..299) {
                    response.body<List<StudentElectiveConfig>>()
                } else {
                    emptyList()
                }
            }.getOrDefault(emptyList())

            if (page.isEmpty()) break
            result.addAll(page)
            if (page.size < pageSize) break
            offset += pageSize
        }
        return result
    }

    suspend fun reportMissingRoll(report: MissingRollReportRequest): Boolean {
        return runCatching {
            val response = client.post("rest/v1/missing_roll") {
                setBody(report)
            }
            response.status.value in 200..299
        }.getOrDefault(false)
    }
}
