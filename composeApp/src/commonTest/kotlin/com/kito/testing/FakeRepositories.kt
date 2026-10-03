package com.kito.testing

import com.kito.core.auth.domain.repository.CredentialsRepository
import com.kito.feature.attendance.domain.model.Attendance
import com.kito.feature.attendance.domain.repository.AttendanceRepository
import com.kito.feature.exam.domain.model.ExamSchedule
import com.kito.feature.exam.domain.repository.ExamRepository
import com.kito.feature.faculty.domain.model.Faculty
import com.kito.feature.faculty.domain.model.FacultyScheduleSlot
import com.kito.feature.faculty.domain.repository.FacultyRepository
import com.kito.feature.friendview.domain.model.FriendScheduleItem
import com.kito.feature.friendview.domain.repository.FriendViewRepository
import com.kito.feature.gpa.domain.model.StudentProfile
import com.kito.feature.gpa.domain.repository.GpaRepository
import com.kito.feature.home.domain.model.EventOrAd
import com.kito.feature.home.domain.repository.HomeRepository
import com.kito.feature.calendar.domain.model.CalendarEvent
import com.kito.feature.calendar.domain.repository.CalendarRepository
import com.kito.core.auth.AuthEvent
import com.kito.core.connectivity.domain.repository.ConnectivityRepository
import com.kito.core.auth.AuthRepository
import com.kito.core.auth.AuthState
import com.kito.core.auth.AuthUser
import com.kito.feature.schedule.domain.model.ScheduleItem
import com.kito.feature.schedule.domain.repository.ScheduleRepository
import com.kito.core.sync.domain.SyncUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAttendanceRepository(
    initial: List<Attendance> = emptyList(),
) : AttendanceRepository {
    private val flow = MutableStateFlow(initial)
    fun emit(items: List<Attendance>) { flow.value = items }
    override fun observeAttendance(year: Flow<String>, term: Flow<String>): Flow<List<Attendance>> = flow
    override suspend fun deleteAllAttendance() { flow.value = emptyList() }
    override suspend fun insertAttendance(items: List<Attendance>, year: String, term: String) {
        flow.value = items
    }
}

class FakeExamRepository(
    private val items: List<ExamSchedule> = emptyList(),
) : ExamRepository {
    override suspend fun getExamSchedule(roll: String): List<ExamSchedule> = items
}

class FakeFacultyRepository(
    private val all: List<Faculty> = emptyList(),
    private val schedule: List<FacultyScheduleSlot> = emptyList(),
) : FacultyRepository {
    override suspend fun getAllFaculty(): List<Faculty> = all
    override suspend fun searchFaculty(query: String): List<Faculty> =
        all.filter { it.name.contains(query, ignoreCase = true) }
    override suspend fun getFacultyById(id: Long): Faculty? = all.find { it.id == id }
    override suspend fun getFacultySchedule(id: Long): List<FacultyScheduleSlot> = schedule
}

class FakeScheduleRepository(
    private val items: List<ScheduleItem> = emptyList(),
) : ScheduleRepository {
    var deleteAllSectionsCalled = false
        private set

    override fun getAllSchedule(rollNo: String): Flow<List<ScheduleItem>> =
        MutableStateFlow(items)
    override fun getScheduleForDay(rollNo: String, day: String): Flow<List<ScheduleItem>> =
        MutableStateFlow(items.filter { it.subject.isNotEmpty() })
    override suspend fun deleteAllSections() {
        deleteAllSectionsCalled = true
    }
}

class FakeSyncUseCase : SyncUseCase {
    override suspend fun syncAttendance(roll: String, sapPassword: String, year: String, term: String): Result<Unit> =
        Result.success(Unit)
    override suspend fun syncAll(roll: String, sapPassword: String, year: String, term: String): Result<Unit> =
        Result.success(Unit)
}

class FakeGpaRepository(private val profile: StudentProfile? = null) : GpaRepository {
    override suspend fun getStudentProfile(roll: String): StudentProfile? = profile
}

class FakeFriendViewRepository(
    private val items: List<FriendScheduleItem> = emptyList(),
    private val summaryMap: Map<String, com.kito.feature.friendview.domain.model.FriendSummary> = emptyMap(),
    private val remoteError: Throwable? = null,
    private val defaultFound: Boolean = true
) : FriendViewRepository {
    override suspend fun getFriendSchedule(roll: String): List<FriendScheduleItem> = items
    override suspend fun getFriendSummary(roll: String): com.kito.feature.friendview.domain.model.FriendSummary =
        summaryMap[roll] ?: com.kito.feature.friendview.domain.model.FriendSummary(roll = roll, section = "CSE-1")
    override suspend fun fetchRemoteStudentSummary(roll: String): com.kito.feature.friendview.domain.model.FriendSummary {
        if (remoteError != null) throw remoteError
        if (summaryMap.containsKey(roll)) return summaryMap[roll]!!
        return if (defaultFound) {
            com.kito.feature.friendview.domain.model.FriendSummary(roll = roll, section = "CSE-1")
        } else {
            com.kito.feature.friendview.domain.model.FriendSummary(roll = roll, notFound = true)
        }
    }
    override suspend fun syncFriend(roll: String): Result<Unit> = Result.success(Unit)
    override suspend fun syncAllFriends(): Result<Unit> = Result.success(Unit)
}

class FakeHomeRepository(
    private val events: List<EventOrAd> = emptyList(),
    private val khaooGullyEnabled: Boolean = false,
) : HomeRepository {
    override suspend fun getEventsAndAds(): List<EventOrAd> = events
    override suspend fun isKhaooGullyEnabled(): Boolean = khaooGullyEnabled
}

class FakeCalendarRepository(private val events: List<CalendarEvent> = emptyList()) : CalendarRepository {
    override suspend fun getEventsByMonth(year: Int, month: Int): List<CalendarEvent> = events
}

class FakeConnectivityRepository(
    initialOnline: Boolean = true
) : ConnectivityRepository {
    private val flow = MutableStateFlow(initialOnline)
    override val isOnline = flow
    @Suppress("unused")
    fun setOnline(online: Boolean) { flow.value = online }
}

class FakeAuthRepository : AuthRepository {
    override val authState: kotlinx.coroutines.flow.StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
    override val events: kotlinx.coroutines.flow.SharedFlow<AuthEvent> = kotlinx.coroutines.flow.MutableSharedFlow()
    override suspend fun restoreSession() = Unit
    override suspend fun signInWithGoogle() = Unit
    override suspend fun signOut() = Unit
    override suspend fun updateDisplayName(name: String) = Unit
    override fun currentUser(): AuthUser? = null
}

class FakeCredentialsRepository(
    initialLoggedIn: Boolean = false,
    private var password: String = "",
) : CredentialsRepository {
    private val _isLoggedIn = MutableStateFlow(initialLoggedIn)
    override val isLoggedIn: Flow<Boolean> = _isLoggedIn
    override suspend fun getSapPassword(): String = password
    override suspend fun saveSapPassword(password: String): Boolean {
        this.password = password
        _isLoggedIn.value = password.isNotEmpty()
        return true
    }
    override suspend fun clearSapPassword(): Boolean {
        password = ""
        _isLoggedIn.value = false
        return true
    }
}
