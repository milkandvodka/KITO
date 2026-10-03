package com.kito.feature.friendview.domain.repository

import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.domain.model.FriendSummary

interface FriendViewRepository {
    suspend fun getFriendSchedule(roll: String): List<FriendScheduleItem>
    suspend fun getFriendSummary(roll: String): FriendSummary
    suspend fun fetchRemoteStudentSummary(roll: String): FriendSummary
    suspend fun syncFriend(roll: String): Result<Unit>
    suspend fun syncAllFriends(): Result<Unit>
}

