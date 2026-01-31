package com.kito.ui.newUi.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.data.local.db.attendance.AttendanceEntity
import com.kito.data.local.db.attendance.AttendanceRepository
import com.kito.data.local.db.studentsection.StudentSectionEntity
import com.kito.data.local.db.studentsection.StudentSectionRepository
import com.kito.data.local.preferences.PrefsRepository
import com.kito.data.local.preferences.SecurePrefs
import com.kito.data.remote.SupabaseRepository
import com.kito.data.remote.model.MidsemScheduleModel
import com.kito.ui.components.AppSyncUseCase
import com.kito.ui.components.ConnectivityObserver
import com.kito.ui.components.StartupSyncGuard
import com.kito.ui.components.state.SyncUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val prefs: PrefsRepository,
    private val securePrefs: SecurePrefs,
    private val attendanceRepository: AttendanceRepository,
    private val studentSectionRepository: StudentSectionRepository,
    private val appSyncUseCase: AppSyncUseCase,
    private val syncGuard: StartupSyncGuard,
    private val connectivityObserver: ConnectivityObserver,
    private val supabaseRepository: SupabaseRepository,
): ViewModel() {
    val isOnline = connectivityObserver.isOnline
    val name = prefs.userNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    val sapLoggedIn = securePrefs.isLoggedInFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    private val _day = MutableStateFlow<String>("")
    val day: StateFlow<String> = _day

    fun updateDay(day: String) {
        _day.value = day
    }

    private val attendance: StateFlow<List<AttendanceEntity>> =
        attendanceRepository
            .getAllAttendance()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState = _syncState.asStateFlow()
    private val _syncEvents = MutableSharedFlow<SyncUiState>()
    val syncEvents: SharedFlow<SyncUiState> = _syncEvents

    private val _loginState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _examModel = MutableStateFlow<MidsemScheduleModel?>(null)
    val examModel = _examModel.asStateFlow()
    fun syncOnStartup() {
        if (syncGuard.hasSynced) return
        syncGuard.hasSynced = true
        viewModelScope.launch {
            _syncEvents.emit(SyncUiState.Loading)
            _syncState.value = SyncUiState.Loading
            val roll = prefs.userRollFlow.first()
            val sapPassword = securePrefs.getSapPassword()
            val year = prefs.academicYearFlow.first()
            val term = prefs.termCodeFlow.first()

            val result = appSyncUseCase.syncAll(
                roll = roll,
                sapPassword = sapPassword,
                year = year,
                term = term
            )

            _syncState.value = result.fold(
                onSuccess = {
                    _syncEvents.emit(SyncUiState.Success)
                    SyncUiState.Success
                },
                onFailure = {
                    SyncUiState.Error(it.message ?: "Sync failed")
                }
            )
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val schedule: StateFlow<List<StudentSectionEntity>> =
        prefs.userRollFlow
            .flatMapLatest { roll ->
                day.flatMapLatest { day ->
                    studentSectionRepository.getScheduleForStudent(
                        rollNo = roll,
                        day = day
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
    val averageAttendancePercentage: StateFlow<Double> =
        attendance
            .map { list ->
                if (list.isEmpty()) {
                    0.0
                } else {
                    list.map { it.percentage }.average()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )
    val highestAttendancePercentage: StateFlow<Double> =
        attendance
            .map { list ->
                list.maxOfOrNull { it.percentage } ?: 0.0
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )
    val lowestAttendancePercentage: StateFlow<Double> =
        attendance
            .map { list ->
                list.minOfOrNull { it.percentage } ?: 0.0
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )
    fun login(
        password: String
    ){
        viewModelScope.launch {
            _loginState.value = SyncUiState.Loading
            delay(1000)
            val roll = prefs.userRollFlow.first()
            val year = prefs.academicYearFlow.first()
            val term = prefs.termCodeFlow.first()

            val result = appSyncUseCase.syncAll(
                roll = roll,
                sapPassword = password,
                year = year,
                term = term
            )

            _loginState.value = result.fold(
                onSuccess = {
                    securePrefs.saveSapPassword(password)
                    SyncUiState.Success
                },
                onFailure = {
                    SyncUiState.Error(it.message ?: "Sync failed")
                }
            )
        }
    }
    fun setLoginStateIdle(){
        _loginState.value = SyncUiState.Idle
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getExamSchedule(){
        viewModelScope.launch {
            try {
                val roll = prefs.userRollFlow.first()
                val examSchedule = supabaseRepository.getMidSemSchedule(roll)
                _examModel.value = getNextOrOngoingExam(examSchedule)
            }catch (e: Exception){
                Log.d("exam model error",e.message?:"")
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextOrOngoingExam(
        exams: List<MidsemScheduleModel>
    ): MidsemScheduleModel? {

        val nowDate = LocalDate.now()
        val nowTime = LocalTime.now()

        return exams
            .mapNotNull { exam ->
                try {
                    val examDate = LocalDate.parse(exam.date)
                    val startTime = LocalTime.parse(exam.start_time)
                    val endTime = LocalTime.parse(exam.end_time)

                    when {
                        // 🟢 Exam is ONGOING
                        examDate == nowDate &&
                                !nowTime.isBefore(startTime) &&
                                nowTime.isBefore(endTime) -> {
                            exam to LocalDateTime.of(examDate, startTime)
                        }

                        // 🔵 Exam is in the FUTURE
                        examDate.isAfter(nowDate) ||
                                (examDate == nowDate && startTime.isAfter(nowTime)) -> {
                            exam to LocalDateTime.of(examDate, startTime)
                        }

                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            .minByOrNull { it.second }
            ?.first
    }
}