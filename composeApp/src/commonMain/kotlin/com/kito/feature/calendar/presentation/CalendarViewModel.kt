package com.kito.feature.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kito.core.datastore.domain.repository.PrefsRepository
import com.kito.feature.calendar.domain.usecase.GetCalendarMonthUseCase
import com.kito.feature.calendar.domain.usecase.GetClassesForDateUseCase
import com.kito.feature.calendar.presentation.components.indexMonth
import com.kito.feature.calendar.presentation.components.indexYear
import com.kito.feature.calendar.presentation.components.monthIndex
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class CalendarViewModel(
    private val getCalendarMonth: GetCalendarMonthUseCase,
    private val getClassesForDate: GetClassesForDateUseCase,
    private val prefs: PrefsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState(today = today))
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadAround(today.monthIndex())
    }

    fun onEvent(event: CalendarScreenEvent) {
        when (event) {
            is CalendarScreenEvent.MonthChanged -> showMonth(event.monthIndex)
            CalendarScreenEvent.GoToToday -> showMonth(_uiState.value.today.monthIndex())
            is CalendarScreenEvent.SelectDate -> selectDate(event.date)
            CalendarScreenEvent.DismissDay -> _uiState.update {
                it.copy(selectedDate = null, selectedDayClasses = emptyList(), isLoadingClasses = false)
            }
            CalendarScreenEvent.Refresh -> refresh()
        }
    }

    private fun showMonth(index: Int) {
        val current = _uiState.value
        if (index != current.displayedMonth) {
            _uiState.update { it.copy(displayedMonth = index, isOffline = false) }
        }
        loadAround(index)
    }

    /** Displayed month plus both neighbours, so swiping is instant. */
    private fun loadAround(index: Int) {
        load(index)
        load(index + 1)
        load(index - 1)
    }

    private fun load(index: Int) {
        var claimed = false
        _uiState.update { s ->
            claimed = index !in s.months && index !in s.loadingMonths
            if (claimed) s.copy(loadingMonths = s.loadingMonths + index) else s
        }
        if (claimed) viewModelScope.launch(dispatcher) { fetch(index, forceRefresh = false) }
    }

    private suspend fun fetch(index: Int, forceRefresh: Boolean) {
        _uiState.update { it.copy(loadingMonths = it.loadingMonths + index) }
        val roll = prefs.userRollFlow.first()
        val month = getCalendarMonth(index.indexYear(), index.indexMonth(), roll, forceRefresh)
        _uiState.update {
            it.copy(
                months = it.months + (index to month.entriesByDate),
                loadingMonths = it.loadingMonths - index,
                isOffline = if (index == it.displayedMonth) month.isPartial else it.isOffline,
            )
        }
    }

    private fun refresh() {
        val state = _uiState.value
        if (state.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch(dispatcher) {
            // Displayed month + today's month and the next (they feed "Upcoming").
            val todayIndex = state.today.monthIndex()
            setOf(state.displayedMonth, todayIndex, todayIndex + 1)
                .map { launch { fetch(it, forceRefresh = true) } }
                .joinAll()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, selectedDayClasses = emptyList(), isLoadingClasses = true) }
        load(date.monthIndex())
        viewModelScope.launch(dispatcher) {
            val classes = getClassesForDate(date, prefs.userRollFlow.first())
            _uiState.update {
                // Ignore a late result if the user already opened another day.
                if (it.selectedDate != date) it else it.copy(selectedDayClasses = classes, isLoadingClasses = false)
            }
        }
    }
}
