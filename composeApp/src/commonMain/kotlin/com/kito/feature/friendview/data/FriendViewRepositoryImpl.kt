package com.kito.feature.friendview.data

import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.core.sync.data.SyncRemoteDataSource
import com.kito.feature.friendview.data.mapper.mergeDuplicateFriendClasses
import com.kito.feature.friendview.data.mapper.toDomain
import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.friendview.domain.repository.FriendViewRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import org.koin.core.annotation.Provided

class FriendViewRepositoryImpl(
    @Provided private val syncRemoteDataSource: SyncRemoteDataSource,
    @Provided private val prefs: PrefsRepository,
) : FriendViewRepository {

    companion object {
        const val CACHE_AGE_MS = 6 * 60 * 60 * 1000L
    }

    override suspend fun getFriendSummary(roll: String): FriendSummary {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = prefs.cachedFriendSummariesFlow.first()[roll]
        if (cached != null && !cached.isLoading && !cached.notFound && now - cached.lastSyncedAt in 0 until CACHE_AGE_MS) {
            return cached
        }
        if (roll.startsWith("SEC:")) {
            return cached ?: FriendSummary(roll = roll, notFound = true, lastSyncedAt = now)
        }
        val summary = fetchRemoteFriendSummary(roll)
        val finalSummary = if (cached?.name?.isNotBlank() == true) {
            summary.copy(name = cached.name, lastSyncedAt = now)
        } else {
            summary.copy(lastSyncedAt = now)
        }
        prefs.saveCachedFriendSummary(finalSummary)
        return finalSummary
    }

    override suspend fun getFriendSchedule(roll: String): List<FriendScheduleItem> {
        val now = Clock.System.now().toEpochMilliseconds()
        val cachedSummary = prefs.cachedFriendSummariesFlow.first()[roll]
        val cachedSchedule = prefs.cachedFriendSchedulesFlow.first()[roll]
        if (cachedSchedule != null && cachedSchedule.isNotEmpty() && cachedSummary != null && now - cachedSummary.lastSyncedAt in 0 until CACHE_AGE_MS) {
            return cachedSchedule
        }
        val remote = fetchRemoteFriendSchedule(roll)
        if (remote.isNotEmpty()) {
            prefs.saveCachedFriendSchedule(roll, remote)
        }
        return remote
    }

    override suspend fun syncFriend(roll: String): Result<Unit> = runCatching {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = prefs.cachedFriendSummariesFlow.first()[roll]
        val cachedSchedule = prefs.cachedFriendSchedulesFlow.first()[roll]

        if (cached != null && !cached.notFound && !cachedSchedule.isNullOrEmpty() && now - cached.lastSyncedAt in 0 until CACHE_AGE_MS) {
            return@runCatching
        }

        if (roll.startsWith("SEC:")) {
            val summary = getFriendSummary(roll)
            if (summary.notFound) return@runCatching
            val schedule = fetchRemoteFriendSchedule(roll)
            if (schedule.isNotEmpty()) {
                prefs.saveCachedFriendSchedule(roll, schedule)
            }
            return@runCatching
        }

        val remoteSummary = fetchRemoteFriendSummary(roll)
        if (remoteSummary.notFound) {
            prefs.saveCachedFriendSummary(remoteSummary.copy(lastSyncedAt = now))
            return@runCatching
        }
        val finalSummary = if (cached?.name?.isNotBlank() == true) {
            remoteSummary.copy(name = cached.name, lastSyncedAt = now)
        } else {
            remoteSummary.copy(lastSyncedAt = now)
        }
        prefs.saveCachedFriendSummary(finalSummary)

        val schedule = fetchRemoteFriendSchedule(roll)
        if (schedule.isNotEmpty()) {
            prefs.saveCachedFriendSchedule(roll, schedule)
        }
    }

    override suspend fun syncAllFriends(): Result<Unit> = runCatching {
        val rolls = prefs.friendRollsFlow.first()
        for (roll in rolls) {
            syncFriend(roll)
        }
    }

    override suspend fun fetchRemoteStudentSummary(roll: String): FriendSummary {
        val student = syncRemoteDataSource.getStudentByRoll(roll)
            ?: return FriendSummary(roll = roll, notFound = true)

        val elective = if (student.batch == "batch_3") {
            syncRemoteDataSource.getStudentElective(roll)
        } else null

        return FriendSummary(
            roll = roll,
            section = student.section,
            batch = student.batch,
            elective1 = elective?.elective_1.orEmpty(),
            elective2 = elective?.elective_2.orEmpty()
        )
    }

    private suspend fun fetchRemoteFriendSummary(roll: String): FriendSummary {
        return runCatching {
            fetchRemoteStudentSummary(roll)
        }.getOrElse {
            FriendSummary(roll = roll, notFound = true)
        }
    }

    private suspend fun fetchRemoteFriendSchedule(roll: String): List<FriendScheduleItem> {
        val cachedSummary = prefs.cachedFriendSummariesFlow.first()[roll]

        val student = if (!roll.startsWith("SEC:")) {
            syncRemoteDataSource.getStudentByRoll(roll)
        } else null

        val section = student?.section ?: cachedSummary?.section.orEmpty()
        val batch = student?.batch ?: cachedSummary?.batch.orEmpty()

        if (section.isBlank()) return emptyList()

        val coreTimetable = syncRemoteDataSource.getTimetableForStudent(
            section = section,
            batch = batch
        )

        val elective1 = if (student != null && student.batch == "batch_3") {
            syncRemoteDataSource.getStudentElective(roll)?.elective_1.orEmpty()
        } else cachedSummary?.elective1.orEmpty()

        val elective2 = if (student != null && student.batch == "batch_3") {
            syncRemoteDataSource.getStudentElective(roll)?.elective_2.orEmpty()
        } else cachedSummary?.elective2.orEmpty()

        val elective1Rows = if (elective1.isNotBlank()) {
            syncRemoteDataSource.getTimetableForStudent(
                section = elective1,
                batch = batch
            )
        } else emptyList()

        val elective2Rows = if (elective2.isNotBlank()) {
            syncRemoteDataSource.getTimetableForStudent(
                section = elective2,
                batch = batch
            )
        } else emptyList()

        return (coreTimetable + elective1Rows + elective2Rows)
            .map { it.toDomain() }
            .mergeDuplicateFriendClasses()
    }
}


