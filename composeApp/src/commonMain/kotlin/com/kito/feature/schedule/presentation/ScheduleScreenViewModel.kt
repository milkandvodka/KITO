package com.kito.feature.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.feature.schedule.domain.model.ManualScheduleConfig
import com.kito.feature.schedule.domain.model.ScheduleItem
import com.kito.feature.schedule.domain.repository.ManualScheduleRepository
import com.kito.feature.schedule.domain.repository.ScheduleRepository
import com.kito.feature.schedule.domain.usecase.ClearManualScheduleUseCase
import com.kito.feature.schedule.domain.usecase.GetAvailableSectionsUseCase
import com.kito.feature.schedule.domain.usecase.GetScheduleLookupStateUseCase
import com.kito.feature.schedule.domain.usecase.SaveManualScheduleUseCase
import com.kito.feature.schedule.presentation.components.extractBranchName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScheduleScreenViewModel(
    private val prefs: PrefsRepository,
    private val scheduleRepository: ScheduleRepository,
    private val manualScheduleRepository: ManualScheduleRepository,
    private val getAvailableSectionsUseCase: GetAvailableSectionsUseCase,
    private val saveManualScheduleUseCase: SaveManualScheduleUseCase,
    private val clearManualScheduleUseCase: ClearManualScheduleUseCase,
    private val getScheduleLookupStateUseCase: GetScheduleLookupStateUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ScheduleUiEvent>()
    val events: SharedFlow<ScheduleUiEvent> = _events.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklySchedule: StateFlow<Map<WeekDay, List<ScheduleItem>>> =
        prefs.userRollFlow
            .flatMapLatest { roll ->
                combine(
                    WeekDay.entries.map { day ->
                        scheduleRepository
                            .getScheduleForDay(
                                rollNo = roll,
                                day = day.apiValue
                            )
                            .map { list -> day to list }
                    }
                ) { results ->
                    results.toMap()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    // Section lists page through whole Supabase tables, so they load only when a picker opens
    // (LoadSections / OpenEditSheet), not on every app start.
    private var sectionsRequested = false

    init {
        observeManualConfig()
        checkStudentStatus()
    }

    private fun observeManualConfig() {
        viewModelScope.launch(dispatcher) {
            combine(
                manualScheduleRepository.observeIsManualSchedule(),
                manualScheduleRepository.observeManualScheduleConfig(),
                prefs.kayaConnectedFlow
            ) { isManual, config, kayaConnected ->
                _uiState.update {
                    it.copy(
                        isManualSchedule = isManual && !kayaConnected,
                        manualConfig = config,
                        selectedBatch = config?.batch ?: it.selectedBatch,
                        selectedBranch = config?.section?.let { sec -> extractBranchName(sec) } ?: it.selectedBranch,
                        selectedCoreSection = config?.section ?: it.selectedCoreSection,
                        selectedElective1 = config?.elective1 ?: it.selectedElective1,
                        selectedElective2 = config?.elective2 ?: it.selectedElective2
                    )
                }
            }.collect {}
        }
    }

    private fun checkStudentStatus() {
        viewModelScope.launch(dispatcher) {
            val roll = prefs.userRollFlow.first()
            val state = getScheduleLookupStateUseCase(roll)
            _uiState.update { it.copy(lookupState = state) }
        }
    }

    private fun loadAvailableSections(force: Boolean = false) {
        if (sectionsRequested && !force) return
        sectionsRequested = true
        viewModelScope.launch(dispatcher) {
            val available = getAvailableSectionsUseCase()
            _uiState.update { it.copy(availableData = available) }
        }
    }

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.SelectBatch -> {
                _uiState.update {
                    it.copy(
                        selectedBatch = event.batch,
                        selectedBranch = "",
                        selectedCoreSection = "",
                        selectedElective1 = "",
                        selectedElective2 = ""
                    )
                }
            }
            is ScheduleEvent.SelectBranch -> {
                _uiState.update {
                    it.copy(
                        selectedBranch = event.branch,
                        selectedCoreSection = "",
                        selectedElective1 = "",
                        selectedElective2 = ""
                    )
                }
            }
            is ScheduleEvent.SelectCoreSection -> {
                _uiState.update {
                    it.copy(
                        selectedCoreSection = event.section,
                        selectedElective1 = "",
                        selectedElective2 = ""
                    )
                }
            }
            is ScheduleEvent.SelectElective1 -> {
                _uiState.update { it.copy(selectedElective1 = event.section) }
            }
            is ScheduleEvent.SelectElective2 -> {
                _uiState.update { it.copy(selectedElective2 = event.section) }
            }
            ScheduleEvent.SubmitManualSetup -> {
                submitSetup()
            }
            ScheduleEvent.OpenEditSheet -> {
                _uiState.update { it.copy(isEditSheetOpen = true) }
                loadAvailableSections()
            }
            ScheduleEvent.LoadSections -> loadAvailableSections()
            ScheduleEvent.CloseEditSheet -> {
                _uiState.update { it.copy(isEditSheetOpen = false) }
            }
            ScheduleEvent.ClearManualSetup -> {
                clearSetup()
            }
            ScheduleEvent.RetryLookup -> {
                checkStudentStatus()
                loadAvailableSections(force = true)
            }
        }
    }

    private fun submitSetup() {
        val state = _uiState.value
        if (state.selectedCoreSection.isBlank() || state.selectedBatch.isBlank()) return

        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isSubmittingSetup = true, errorMessage = null) }
            val roll = prefs.userRollFlow.first()
            val config = ManualScheduleConfig(
                section = state.selectedCoreSection,
                batch = state.selectedBatch,
                elective1 = state.selectedElective1,
                elective2 = state.selectedElective2
            )
            val result = saveManualScheduleUseCase(roll, config)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isSubmittingSetup = false,
                        isEditSheetOpen = false,
                        isManualSchedule = true,
                        manualConfig = config
                    )
                }
                _events.emit(ScheduleUiEvent.SetupSuccess)
            } else {
                _uiState.update {
                    it.copy(
                        isSubmittingSetup = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to save timetable"
                    )
                }
            }
        }
    }

    private fun clearSetup() {
        viewModelScope.launch(dispatcher) {
            clearManualScheduleUseCase()
            _uiState.update {
                it.copy(
                    isManualSchedule = false,
                    manualConfig = null,
                    isEditSheetOpen = false,
                    selectedBatch = "",
                    selectedBranch = "",
                    selectedCoreSection = "",
                    selectedElective1 = "",
                    selectedElective2 = ""
                )
            }
            checkStudentStatus()
        }
    }
}
