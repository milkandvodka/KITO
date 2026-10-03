package com.kito.core.datastore.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Domain-level interface for accessing application settings and user preferences.
 * Adheres to Dependency Inversion by keeping datastore implementation details out of the domain/presentation layers.
 */
interface PrefsRepository {
    val notificationStateFlow: Flow<Boolean>
    val resetFixFlow: Flow<Boolean>
    val requiredAttendanceFlow: Flow<Int>
    val userNameFlow: Flow<String>
    val userRollFlow: Flow<String>
    val academicYearFlow: Flow<String>
    val termCodeFlow: Flow<String>
    val onBoardingFlow: Flow<Boolean>
    val userSetupDoneFlow: Flow<Boolean>
    val friendRollsFlow: Flow<List<String>>
    val selectedFriendRollFlow: Flow<String>
    /** False until the user has seen the "connect to KAYA" intro on the Schedule screen. */
    val kayaIntroSeenFlow: Flow<Boolean>
    /** True once the user has connected their KAYA account (credentials verified). */
    val kayaConnectedFlow: Flow<Boolean>

    val isManualScheduleFlow: Flow<Boolean>
    val manualSectionFlow: Flow<String>
    val manualBatchFlow: Flow<String>
    val manualElective1Flow: Flow<String>
    val manualElective2Flow: Flow<String>

    suspend fun setUserName(username: String)
    suspend fun setUserRollNumber(rollNumber: String)
    suspend fun setUserSetupDone()
    val cachedFriendSummariesFlow: Flow<Map<String, com.kito.feature.friendview.domain.model.FriendSummary>>
    val cachedFriendSchedulesFlow: Flow<Map<String, List<com.kito.feature.friendview.domain.model.FriendScheduleItem>>>

    suspend fun saveCachedFriendSummary(summary: com.kito.feature.friendview.domain.model.FriendSummary)
    suspend fun saveCachedFriendSchedule(roll: String, items: List<com.kito.feature.friendview.domain.model.FriendScheduleItem>)
    suspend fun setOnboardingDone()
    suspend fun setAcademicYear(year: String)
    suspend fun setTermCode(term: String)
    suspend fun setRequiredAttendance(attendance: Int)
    suspend fun setResetDone()
    suspend fun setNotificationState(state: Boolean)
    suspend fun addFriendRoll(roll: String)
    suspend fun removeFriendRoll(roll: String)
    suspend fun setSelectedFriendRoll(roll: String)
    suspend fun clearSelectedFriend()
    suspend fun setKayaIntroSeen()
    suspend fun setKayaConnected(connected: Boolean)
    suspend fun saveManualSchedule(
        section: String,
        batch: String,
        elective1: String = "",
        elective2: String = ""
    )
    suspend fun clearManualSchedule()
}
