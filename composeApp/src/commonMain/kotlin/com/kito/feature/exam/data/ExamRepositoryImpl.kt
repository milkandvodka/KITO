package com.kito.feature.exam.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kito.core.cache.TimedJsonCache
import com.kito.core.network.supabase.model.MidsemScheduleModel
import com.kito.core.network.supabase.request.MidsemScheduleRequest
import com.kito.feature.exam.data.mapper.toDomain
import com.kito.feature.exam.domain.model.ExamSchedule
import com.kito.feature.exam.domain.repository.ExamRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.builtins.ListSerializer

import org.koin.core.annotation.Provided

class ExamRepositoryImpl(
    @Provided private val client: HttpClient,
    @Provided dataStore: DataStore<Preferences>,
) : ExamRepository {
    private val cache = TimedJsonCache(dataStore, ListSerializer(MidsemScheduleModel.serializer()))

    override suspend fun getExamSchedule(roll: String, forceRefresh: Boolean): List<ExamSchedule> {
        val models = cache.getOrFetch("exam_schedule_v1_$roll", forceRefresh) {
            client.post("rest/v1/rpc/get_midsem_schedule_by_roll") {
                contentType(ContentType.Application.Json)
                setBody(MidsemScheduleRequest(p_roll_no = roll))
            }.body<List<MidsemScheduleModel>>()
        }
        return models.map { it.toDomain() }
    }
}
