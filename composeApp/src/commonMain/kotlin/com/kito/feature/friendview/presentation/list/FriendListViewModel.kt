package com.kito.feature.friendview.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.core.ui.state.SyncUiState
import com.kito.feature.friendview.domain.FriendViewSyncGuard
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.friendview.domain.repository.FriendViewRepository
import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.usecase.GetAvailableSectionsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Provided
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class FriendListViewModel(
    @Provided private val friendViewRepository: FriendViewRepository,
    @Provided private val prefs: PrefsRepository,
    @Provided private val getAvailableSectionsUseCase: GetAvailableSectionsUseCase? = null,
    @Provided private val syncGuard: FriendViewSyncGuard = FriendViewSyncGuard(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _isAddingFriend = MutableStateFlow(false)
    val isAddingFriend: StateFlow<Boolean> = _isAddingFriend.asStateFlow()

    private val _addFriendError = MutableStateFlow<String?>(null)
    val addFriendError: StateFlow<String?> = _addFriendError.asStateFlow()

    private val _availableSectionsData = MutableStateFlow(AvailableSectionsData())
    val availableSectionsData: StateFlow<AvailableSectionsData> = _availableSectionsData.asStateFlow()

    private val _syncEvents = MutableSharedFlow<SyncUiState>()
    val syncEvents = _syncEvents.asSharedFlow()

    val friendRolls: StateFlow<List<String>> = prefs.friendRollsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val friendsSummary: StateFlow<List<FriendSummary>> =
        friendRolls
            .flatMapLatest { rolls ->
                prefs.cachedFriendSummariesFlow.flatMapLatest { cached ->
                    flow {
                        if (rolls.isEmpty()) {
                            emit(emptyList())
                        } else {
                            val items = rolls.map { roll ->
                                cached[roll] ?: FriendSummary(roll = roll, isLoading = true)
                            }
                            emit(items)
                            val missing = rolls.filter { roll -> cached[roll] == null }
                            if (missing.isNotEmpty()) {
                                val loaded = rolls.map { roll ->
                                    cached[roll] ?: runCatching {
                                        friendViewRepository.getFriendSummary(roll)
                                    }.getOrDefault(FriendSummary(roll = roll, notFound = true))
                                }
                                emit(loaded)
                            }
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        loadAvailableSections()
    }

    private fun loadAvailableSections() {
        if (getAvailableSectionsUseCase == null) return
        viewModelScope.launch(dispatcher) {
            runCatching {
                val data = getAvailableSectionsUseCase()
                _availableSectionsData.value = data
            }
        }
    }

    fun onEvent(event: FriendListEvent) {
        when (event) {
            is FriendListEvent.AddFriend -> addFriendByRoll("", event.roll)
            is FriendListEvent.AddFriendByRoll -> addFriendByRoll(event.name, event.roll)
            is FriendListEvent.AddFriendBySection -> addFriendBySection(
                name = event.name,
                batch = event.batch,
                section = event.section,
                elective1 = event.elective1,
                elective2 = event.elective2
            )
            is FriendListEvent.RemoveFriend -> removeFriend(event.roll)
            is FriendListEvent.ShowAddDialog -> {
                _addFriendError.value = null
                _showAddDialog.update { event.show }
            }
            is FriendListEvent.SyncOnOpen -> syncFriends()
        }
    }

    fun syncFriends() {
        if (syncGuard.hasSynced) return
        viewModelScope.launch(dispatcher) {
            val rolls = friendRolls.value
            if (rolls.isEmpty()) return@launch
            syncGuard.hasSynced = true
            val result = friendViewRepository.syncAllFriends()
            if (result.isSuccess) {
                _syncEvents.emit(SyncUiState.Success)
            } else {
                _syncEvents.emit(SyncUiState.Error(result.exceptionOrNull()?.message ?: "Sync failed"))
            }
        }
    }

    private fun addFriendByRoll(name: String, roll: String) {
        val trimmedRoll = roll.trim()
        val trimmedName = name.trim().ifBlank { trimmedRoll }
        if (trimmedRoll.isBlank()) {
            _addFriendError.value = "Please enter a valid roll number"
            return
        }

        viewModelScope.launch(dispatcher) {
            _isAddingFriend.value = true
            _addFriendError.value = null
            try {
                val summaryResult = runCatching {
                    friendViewRepository.fetchRemoteStudentSummary(trimmedRoll)
                }

                if (summaryResult.isFailure) {
                    val exception = summaryResult.exceptionOrNull()
                    if (isNetworkException(exception)) {
                        _addFriendError.value = "No internet connection. Please check your network and retry."
                    } else {
                        _addFriendError.value = "Roll no not available, recheck and retry"
                    }
                    return@launch
                }

                val summary = summaryResult.getOrNull()
                if (summary == null || summary.notFound) {
                    _addFriendError.value = "Roll no not available, recheck and retry"
                    return@launch
                }

                val updatedSummary = summary.copy(name = trimmedName)
                prefs.saveCachedFriendSummary(updatedSummary)
                prefs.addFriendRoll(trimmedRoll)

                runCatching {
                    val schedule = friendViewRepository.getFriendSchedule(trimmedRoll)
                    if (schedule.isNotEmpty()) {
                        prefs.saveCachedFriendSchedule(trimmedRoll, schedule)
                    }
                }

                _showAddDialog.value = false
            } catch (e: Throwable) {
                if (isNetworkException(e)) {
                    _addFriendError.value = "No internet connection. Please check your network and retry."
                } else {
                    _addFriendError.value = "Roll no not available, recheck and retry"
                }
            } finally {
                _isAddingFriend.value = false
            }
        }
    }

    private fun addFriendBySection(
        name: String,
        batch: String,
        section: String,
        elective1: String,
        elective2: String
    ) {
        val trimmedSection = section.trim()
        val trimmedName = name.trim().ifBlank { trimmedSection }
        if (batch.isBlank() || trimmedSection.isBlank()) {
            _addFriendError.value = "Please select year and section"
            return
        }

        viewModelScope.launch(dispatcher) {
            _isAddingFriend.value = true
            _addFriendError.value = null
            try {
                val randomSuffix = Random.nextInt(1000, 9999)
                val rollId = "SEC:${trimmedSection}:$randomSuffix"
                val summary = FriendSummary(
                    roll = rollId,
                    name = trimmedName,
                    section = trimmedSection,
                    batch = batch,
                    elective1 = elective1.trim(),
                    elective2 = elective2.trim()
                )

                prefs.saveCachedFriendSummary(summary)
                prefs.addFriendRoll(rollId)

                runCatching {
                    val schedule = friendViewRepository.getFriendSchedule(rollId)
                    if (schedule.isNotEmpty()) {
                        prefs.saveCachedFriendSchedule(rollId, schedule)
                    }
                }

                _showAddDialog.value = false
            } catch (e: Throwable) {
                if (isNetworkException(e)) {
                    _addFriendError.value = "No internet connection. Please check your network and retry."
                } else {
                    _addFriendError.value = e.message ?: "Failed to add friend"
                }
            } finally {
                _isAddingFriend.value = false
            }
        }
    }

    private fun removeFriend(roll: String) {
        viewModelScope.launch(dispatcher) {
            prefs.removeFriendRoll(roll)
        }
    }

    private fun isNetworkException(e: Throwable?): Boolean {
        if (e == null) return false
        val className = e::class.simpleName.orEmpty()
        val msg = (e.message ?: "").lowercase()
        return className.contains("Connect", ignoreCase = true) ||
                className.contains("Socket", ignoreCase = true) ||
                className.contains("Timeout", ignoreCase = true) ||
                className.contains("UnknownHost", ignoreCase = true) ||
                className.contains("UnresolvedAddress", ignoreCase = true) ||
                className.contains("IOException", ignoreCase = true) ||
                className.contains("Network", ignoreCase = true) ||
                msg.contains("unable to resolve host") ||
                msg.contains("failed to connect") ||
                msg.contains("network") ||
                msg.contains("timeout") ||
                msg.contains("connection refused")
    }
}
